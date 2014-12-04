package InitialScheme.Mapping;

import InitialScheme.Sources.OntologySources.Axioms.Axiom;
import InitialScheme.Sources.OntologySources.Axioms.AxiomElement.AxiomElement;
import InitialScheme.Sources.OntologySources.Axioms.AxiomElement.AxiomElementClass;
import InitialScheme.Sources.OntologySources.Axioms.AxiomElement.AxiomElementSynset;
import InitialScheme.Sources.OntologySources.Axioms.AxiomType;
import InitialScheme.Sources.OntologySources.Axioms.TwoElementsAxiom;
import InitialScheme.Sources.OntologySources.OntologyManager;
import InitialScheme.Sources.Term;
import InitialScheme.Sources.WordNetSources.SynsetConcept;
import Utils.string;
import edu.smu.tspell.wordnet.NounSynset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class SynsetOntologyAxioms {

    private OntologyManager Ontology;
    private NounSynset Synset;
    private List<OntologyMapp> Mapps;
    private List<Axiom> Axioms;

    public SynsetOntologyAxioms(NounSynset Synset, OntologyManager Ontology) {
        this.Ontology = Ontology;
        this.Synset = Synset;
        Mapps = new SynsetOntologyMapps(new SynsetConcept(this.Synset), Ontology).getMappings();
        Axioms = new ArrayList<Axiom>();

        searchAxioms();
        this.getHerarchyAxioms();
        //processHerarchyMapps();
        //getAxioms();
        //deleteHerarchyMapps();
//        for (OntologyMapp map : this.Mapps) {
//            System.out.println("\t" + map.getSynsetConcept().getConceptName() +"\t" +  getRelationType(map) + "\t" + ((Ontology.isNamedbyLocalName())? map.getOntClass().getIRI().getFragment() : Ontology.getLabel(map.getOntClass()) ));
//        }
//        for (Axiom axiom : this.Axioms) {
//            if(axiom.getRelation().equals(AxiomType.Type)){
//            System.out.println("\"" + axiom.getSubject().getID() + "\"\t"  + axiom.getSubject().getFirstElementMapped() + "\t" + axiom.getSubject().getSecondElementMapped()
//                    + ((axiom.getRelation() == AxiomType.Type)
//                    ? " \t" + axiom.getSubject() + " " + axiom.getSubject().getFirstElementMapped() + " " + axiom.getSubject().getSecondElementMapped()
//                    : "")
//                    );
//            }
//            else{
//                if(axiom.isTwoElementsAxiom()){
//                    TwoElementsAxiom axiom2 = axiom.asTwoElementsAxiom();
//                    System.out.println("\"" + axiom2.getSubject().getID() + "\"\t"  + axiom.getRelation() + " \t" + "\"" + axiom2.getObject().getID() +"\"");
//                }
//            }
//        }
    }

    private void searchAxioms() {
        List<OntologyMapp> toDelete = new ArrayList<OntologyMapp>();

        for (int i = 0; i < this.Mapps.size(); i++) {
            OntologyMapp Mapp = this.Mapps.get(i);
            int relationtype = Mapp.getRelationType();
            switch (relationtype) {
                case RelationType.EQUIVALENT:
                    addClassAxiom(new AxiomElementSynset(Mapp.getSynsetConcept().getSynset(), Mapp.getOntClass()));
                    toDelete.add(Mapp);
                    break;
                case RelationType.SYNONYM:
                    AxiomElementSynset axiom_element = new AxiomElementSynset(Mapp.getSynsetConcept().getSynset(), Mapp.getOntClass());
                    addClassAxiom(axiom_element);
                    Axiom label_axiom = new Axiom(axiom_element,
                            (this.Ontology.isNamedbyLocalName()
                            ? Mapp.getOntClass().getIRI().getFragment() : this.Ontology.getLabel(Mapp.getOntClass())),
                            AxiomType.HasLabel);
                    this.Axioms.add(label_axiom);
                    toDelete.add(Mapp);
                    break;
                case RelationType.PARTIAL:
                    String entityName = getEntityName(Mapp.getOntClass());
                    NounSynset nounSynset = Mapp.getSynsetConcept().getSynsetforEntity(entityName);
                    AxiomElement Axiom = null;
                    if (nounSynset != null) {
                        Axiom = new AxiomElementSynset(nounSynset, Mapp.getOntClass());
                        addClassAxiom((AxiomElementSynset) Axiom);
                    } else {
                        Axiom = new AxiomElementClass(Mapp.getOntClass(), null);
                        String ID = string.normalizeEntityName(getEntityName(Mapp.getOntClass())).replace(" ", "_");
                        Axiom.setID(ID);

                        addClassAxiom(Axiom.asAxiomElementClass());
                    }

//                    String entityName = getEntityName(Mapp.getOntClass());
//                    NounSynset nounSynset = Mapp.getSynsetConcept().getPartial(entityName);
//                    if(nounSynset != null) {
//                        System.err.println(entityName + " matches with " + nounSynset.getWordForms()[0]);
//                        AxiomElementSynset AxiomSubject = new AxiomElementSynset(nounSynset,Mapp.getOntClass());
//                        addClassAxiom(AxiomSubject);
//                    }else{
//                        AxiomElementClass AxiomSubject = new AxiomElementClass(Mapp.getOntClass(), null);
//                        AxiomSubject.setID(entityName);
//                        addClassAxiom(AxiomSubject);
//                        System.err.println(entityName + " was added as axiom ");
//                    }
                    break;
                default:
                    NounSynset nounsynset = getRelatedEntity(Mapp);
                    if (nounsynset != null) {
                        AxiomElementSynset AxiomObject = new AxiomElementSynset(nounsynset, Mapp.getOntClass());
                        addClassAxiom(AxiomObject);
                        AxiomElementSynset AxiomSubject = new AxiomElementSynset(Mapp.getSynsetConcept().getSynset(), null);
                        addClassAxiom(AxiomSubject);
                        this.addAxiom(AxiomSubject, AxiomObject, Mapp.getRelationType());

                        toDelete.add(Mapp);
                    }
//                        addAxiom(AxiomSubject, AxiomObject, Mapp.getRelationType());
//                        toDelete.add(Mapp);

//                        SynsetConcept synsetconcept = new SynsetConcept(nounsynset);
//                        addMapping(new OntologyMapp(synsetconcept, Mapp.getOntClass(), RelationType.EQUIVALENT));

                    break;
            }
        }
        this.Mapps.removeAll(toDelete);
    }

    private void addAxiom(AxiomElementSynset AxiomSubject, AxiomElementSynset AxiomObject, int relationType) {
        String axiomtype = this.getAxiomType(relationType);
        Axiom axiom;
        if (axiomtype.equals(AxiomType.Contains)) {
            axiom = new TwoElementsAxiom(AxiomObject, AxiomSubject, AxiomType.InstanceOf);
        } else if (axiomtype.equals(AxiomType.HasPart)) {
            axiom = new TwoElementsAxiom(AxiomObject, AxiomSubject, AxiomType.PartOf);
        } else if (axiomtype.equals(AxiomType.SuperClassOf)) {
            axiom = new TwoElementsAxiom(AxiomObject, AxiomSubject, AxiomType.SubClassOf);
        } else {
            axiom = new TwoElementsAxiom(AxiomSubject, AxiomObject, axiomtype);
        }
        if (!this.containsAxiom(axiom.asTwoElementsAxiom())) {
            if (AxiomSubject.getID().equals("medullary_sheath")) {
                System.out.println("**medullary_sheath in " + this.Synset.getWordForms()[0]);
            }
            this.Axioms.add(axiom);
        }
    }

    private boolean containsAxiom(TwoElementsAxiom axiom) {
        for (Axiom axiom_ : this.Axioms) {
            if (axiom_.isTwoElementsAxiom()) {
                if (axiom_.getSubject().equals(axiom.getSubject()) && axiom_.getObject().equals(axiom.getObject()) && axiom_.getRelation().equals(axiom.getRelation())) {
                    return true;
                }
            }
        }
        return false;
    }

    public List<Axiom> getAxioms() {
        return Axioms;
    }

//    private boolean containsAxiom(Axiom axiom){
//        for(Axiom axiom_ : this.Axioms){
//            if(!axiom_.getRelation().equals(axiom.getRelation())){
//                continue;
//            }
//            if (axiom_.getSubject().equals(axiom.getSubject()) && axiom_.getSubject().equals(axiom.getSubject())) {
//                return true;
//            } 
//            if(axiom_.getSubject().isSynsetType() && axiom_.getObject().getClass().equals(axiom_.getSubject())){
//                if(axiom.getSubject().asAxiomElementSynset().getFirstElementMapped().equals(
//                        ((AxiomElementSynset)axiom.getObject()).getFirstElementMapped())
//                        && )
//            }
//        }
//        return false;
//    }
    private String getAxiomType(int relationType) {
        switch (relationType) {
            case RelationType.HYPERNYM:
                return AxiomType.SuperClassOf;
            case RelationType.HYPONYM:
                return AxiomType.SubClassOf;
            case RelationType.INSTANCE_HYPERNYM:
                return AxiomType.Contains;
            case RelationType.INSTANCE_HYPONYM:
                return AxiomType.InstanceOf;
            case RelationType.PART_HOLONYM:
                return AxiomType.HasPart;
            case RelationType.PART_MERONYM:
                return AxiomType.PartOf;
            case RelationType.MEMBER_HOLONYM:
                return AxiomType.HasPart;
            case RelationType.MEMBER_MERONYM:
                return AxiomType.PartOf;
            case RelationType.SUBSTANCE_HOLONYM:
                return AxiomType.HasPart;
            case RelationType.SUBSTANCE_MERONYM:
                return AxiomType.PartOf;
            default:
                return "";
        }

    }

    private String getEntityName(OWLClass Class) {
        if (Ontology.isNamedbyLocalName()) {
            return Class.getIRI().getFragment();
        } else {
            return Ontology.getLabel(Class);
        }
    }

    private void addClassAxiom(AxiomElementClass axiom_element) {
        if (!containsClassAxiom(axiom_element)) {
            Axiom axiom = new Axiom(axiom_element, "Class", AxiomType.Type);
            String ID_Axiom = string.normalizeEntityName(getEntityName(axiom_element.getFirstElementMapped()));
            Axiom label_axiom = new Axiom(axiom_element, ID_Axiom, AxiomType.HasLabel);
            this.Axioms.add(axiom);
            this.Axioms.add(label_axiom);
        }
    }

    private void addClassAxiom(AxiomElementSynset axiom_element) {
        boolean containsClass = false;
        if (axiom_element.getSecondElementMapped() != null) {
            containsClass = containsClassAxiom(axiom_element.getSecondElementMapped());
        }

        boolean containsSynset = containsClassAxiom(axiom_element.getFirstElementMapped());

        boolean containsAxiomElement = containsClassAxiom(axiom_element);

        if (!containsSynset && !containsClass) {
            Axiom axiom = new Axiom(axiom_element, "Class", AxiomType.Type);
            Axiom label_axiom = new Axiom(axiom_element, axiom_element.getFirstElementMapped().getWordForms()[0], AxiomType.HasLabel);
            this.Axioms.add(axiom);
            this.Axioms.add(label_axiom);
        } else {

            if ((!containsAxiomElement) && axiom_element.getSecondElementMapped() != null) {
                if (containsClass && containsSynset) {
                    changeAxiomsWithClassAndSynset(axiom_element);
                } else {
                    if (containsClass) {
                        changeAxiomsWithClass(axiom_element);
                    }
                    if (containsSynset) {
                        changeAxiomsWithSynset(axiom_element);
                    }
                }
            }
        }
//        if (!containsClassAxiom(axiom_element)) {
//            if (!containsClassAxiom(axiom_element.getFirstElementMapped())) {
//                Axiom axiom = new Axiom(axiom_element, "Class", AxiomType.Type);
//                Axiom label_axiom = new Axiom(axiom_element, axiom_element.getFirstElementMapped().getWordForms()[0], AxiomType.HasLabel);
//                this.Axioms.add(axiom);
//                this.Axioms.add(label_axiom);
//            } else {
//                if (axiom_element.getSecondElementMapped() != null) {
//                    for (Axiom axiom : this.Axioms) {
//                        if (axiom.getSubject().getFirstElementMapped().equals(axiom_element.getFirstElementMapped())) {
//                            axiom.getSubject().setSecondElementMapped(axiom_element.getSecondElementMapped());
//                        }
//                    }
//                }
//
//            }
//        }
    }

    private void getHerarchyAxioms() {
        List<AxiomElement> ElementwithSuperClass = new ArrayList<AxiomElement>();
        for (Axiom axiom : this.Axioms) {
            if (axiom.getRelation().equals(AxiomType.SubClassOf)) {
                ElementwithSuperClass.add(axiom.getSubject());
            }
        }
        for (int i = 0; i < (this.Axioms.size()); i++) {
            int[][] relations = new int[this.Axioms.size()][2];
            for (int j = 0; j < (this.Axioms.size()); j++) {
                if (i != j) {
                    if (this.Axioms.get(i).getRelation().equals(AxiomType.Type) && this.Axioms.get(j).getRelation().equals(AxiomType.Type)) {
                        if (!ElementwithSuperClass.contains(this.Axioms.get(i).getSubject()) && !containsPartOfRelation(this.Axioms.get(i).getSubject(), this.Axioms.get(j).getSubject())) {
                            int WNrelation = getWNRelationBetween(this.Axioms.get(i).getSubject(), this.Axioms.get(j).getSubject());
//                            if(WNrelation != -1 )
//                                System.out.println("*****"+this.Axioms.get(i).getSubject().getID() + " &  " +this.Axioms.get(j).getSubject().getID() + " WN:  "+WNrelation);
                            int ONTrelation = getONTRelationBetween(this.Axioms.get(i).getSubject(), this.Axioms.get(j).getSubject());
//                            if(ONTrelation != -1 )
//                                System.out.println("*****"+this.Axioms.get(i).getSubject().getID() + " &  " +this.Axioms.get(j).getSubject().getID() + " ONT:  "+ONTrelation);
                            if (WNrelation != -1 || ONTrelation != -1) {
                                if (WNrelation >= ONTrelation) {
                                    relations[j][0] = WNrelation;
                                    relations[j][1] = 0;
                                } else {
                                    relations[j][0] = ONTrelation;
                                    relations[j][1] = 1;
                                }
                            }
                            /*
                             * if (relation != -1) { if (relation == 1) {
                             * this.Axioms.add(new
                             * TwoElementsAxiom(this.Axioms.get(i).getSubject(),
                             * this.Axioms.get(j).getSubject(),
                             * AxiomType.SubClassOf)); } else {
                             * this.Axioms.add(new
                             * TwoElementsAxiom(this.Axioms.get(i).getSubject(),
                             * this.Axioms.get(j).getSubject(),
                             * AxiomType.IndirectSubClassOf + relation)); }
                            }
                             */
                        }
                    }
                }
            }
            int min = 10;
            boolean wn = false;
            boolean ont = false;
            for (int c = 0; c < this.Axioms.size(); c++) {
                if (relations[c][0] > 0) {
                    if (relations[c][0] < min) {
                        min = relations[c][0];
                        if (relations[c][1] == 0) {
                            wn = true;
                            ont = false;
                        } else {
                            wn = false;
                            ont = true;
                        }
                    } else if (relations[c][0] == min) {
                        if (relations[c][1] == 0) {
                            wn = true;
                        } else {
                            ont = true;
                        }
                    }
                }
            }
            if (min != 10) {
                for (int d = 0; d < relations.length; d++) {
                    if (relations[d][0] == min) {
                        if (relations[d][1] == 0) {
                            this.Axioms.add(new TwoElementsAxiom(this.Axioms.get(i).getSubject(), this.Axioms.get(d).getSubject(), AxiomType.SubClassOf));
                        } else if (!wn) {
                            this.Axioms.add(new TwoElementsAxiom(this.Axioms.get(i).getSubject(), this.Axioms.get(d).getSubject(), AxiomType.SubClassOf));
                        }
                    }
                }
            }
        }
    }

    private boolean containsPartOfRelation(AxiomElement subject, AxiomElement object) {
        for (Axiom axiom : this.Axioms) {
            if (axiom.getRelation().equals(AxiomType.PartOf)) {
                if (axiom.getObject().equals(object) && axiom.getSubject().equals(subject)) {
                    return true;
                }
            }
        }
        return false;
    }

    private int getWNRelationBetween(AxiomElement subject, AxiomElement object) {
        if (subject.isSynsetType()) {
            if (object.isOWLClassType()) {
                return -1;
            }
            return new SynsetConcept(subject.asAxiomElementSynset().getFirstElementMapped()).getNivelOfRelation(object.asAxiomElementSynset().getFirstElementMapped());
        }
        return -1;
    }

    private int getONTRelationBetween(AxiomElement subject, AxiomElement object) {
        int ONTrelation = -1;

        OWLClass subjectClass = null;
        OWLClass objectClass = null;

        if (subject.isOWLClassType()) {
            subjectClass = subject.asAxiomElementClass().getFirstElementMapped();
        } else {
            subjectClass = subject.getSecondElementMapped();
        }

        if (object.isOWLClassType()) {
            objectClass = object.asAxiomElementClass().getFirstElementMapped();
        } else {
            objectClass = object.getSecondElementMapped();
        }

        if (objectClass != null && subjectClass != null) {
            ONTrelation = Ontology.getLevelOfRelation(subjectClass, objectClass);
        }
        return ONTrelation;
    }

    private int getRelationBetween(AxiomElement subject, AxiomElement object) {
        int WNrelation = -1;
        int ONTrelation = -1;
        if (subject.isSynsetType()) {
            if (object.isOWLClassType()) {
                return -1;
            }
            WNrelation = new SynsetConcept(subject.asAxiomElementSynset().getFirstElementMapped()).getNivelOfRelation(object.asAxiomElementSynset().getFirstElementMapped());
        }

        OWLClass subjectClass = null;
        OWLClass objectClass = null;

        if (subject.isOWLClassType()) {
            subjectClass = subject.asAxiomElementClass().getFirstElementMapped();
        } else {
            subjectClass = subject.getSecondElementMapped();
        }

        if (object.isOWLClassType()) {
            objectClass = object.asAxiomElementClass().getFirstElementMapped();
        } else {
            objectClass = object.getSecondElementMapped();
        }

        if (objectClass != null && subjectClass != null) {
            ONTrelation = Ontology.getLevelOfRelation(subjectClass, objectClass);
//            System.out.println("******" + objectClass.getIRI().getFragment() +  " and " + subjectClass.getIRI().getFragment() + " : " + ONTrelation);
        }

        if (WNrelation == -1 && ONTrelation == -1) {
            return -1;
        } else {
            if (WNrelation == -1) {
                return ONTrelation;
            }
            if (ONTrelation == -1) {
                return WNrelation;
            }
            return Math.min(WNrelation, ONTrelation);
        }
    }

    private void changeAxiomsWithClassAndSynset(AxiomElementSynset axiom_element) {
        for (Axiom axiom : this.Axioms) {
            if (axiom.getSubject().isSynsetType()) {
                if (axiom.getSubject().asAxiomElementSynset().getFirstElementMapped().equals((NounSynset) axiom_element.getFirstElementMapped())) {
                    axiom.setSubject(axiom_element);
                }
            } else {
                if (axiom.getSubject().asAxiomElementClass().getFirstElementMapped().equals((OWLClass) axiom_element.getSecondElementMapped())) {
                    axiom.setSubject(axiom_element);
                }
            }
        }
    }

    private void changeAxiomsWithClass(AxiomElementSynset axiom_element) {
        for (Axiom axiom : this.Axioms) {
            if (axiom.getSubject().isOWLClassType()) {
                if (axiom.getSubject().asAxiomElementClass().getFirstElementMapped().equals((OWLClass) axiom_element.getSecondElementMapped())) {
                    axiom.setSubject(axiom_element);
                }
            }
        }
    }

    private void changeAxiomsWithSynset(AxiomElementSynset axiom_element) {
        for (Axiom axiom : this.Axioms) {
            if (axiom.getSubject().isSynsetType()) {
                if (axiom.getSubject().asAxiomElementSynset().getFirstElementMapped().equals((NounSynset) axiom_element.getFirstElementMapped())) {
                    axiom.setSubject(axiom_element);
                }
            }
        }
    }

    private boolean containsClassAxiom(AxiomElementClass axiom_element) {
        return containsClassAxiom(axiom_element.getFirstElementMapped());
    }

    private boolean containsClassAxiom(OWLClass axiom_element) {
        for (Axiom axiom : this.Axioms) {
            if (axiom.getRelation().equals(AxiomType.Type)) {
                if (axiom.getSubject().isSynsetType()) {
                    if (axiom.getSubject().getSecondElementMapped() != null && axiom.getSubject().getSecondElementMapped().equals(axiom_element)) {
                        return true;
                    }
                } else {
                    if (axiom.getSubject().getFirstElementMapped() != null && axiom.getSubject().getFirstElementMapped().equals(axiom_element)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean containsClassAxiom(AxiomElementSynset axiom_element) {
        for (Axiom axiom : this.Axioms) {
            if (axiom.getSubject().equals(axiom_element)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsClassAxiom(NounSynset axiom_element) {
        for (Axiom axiom : this.Axioms) {
            if (axiom.getSubject().getFirstElementMapped().equals(axiom_element)) {
                return true;
            }
        }
        return false;
    }

    private NounSynset getRelatedEntity(OntologyMapp Mapp) {
        String entityName = getEntityName(Mapp.getOntClass());
        NounSynset nounsynset = getRelatedEntity(Mapp.getSynsetConcept(), entityName, Mapp.getRelationType());
        if (nounsynset == null) {
            if (!Ontology.isNamedbyLocalName() && (Ontology.getLabels(Mapp.getOntClass()).size() > 1)) {
                for (int i = 1; i < Ontology.getLabels(Mapp.getOntClass()).size(); i++) {
                    if ((nounsynset = getRelatedEntity(Mapp.getSynsetConcept(), entityName, Mapp.getRelationType())) != null) {
                        return nounsynset;
                    }
                }
            }
        }
        return nounsynset;
    }

    private NounSynset getRelatedEntity(SynsetConcept synsetconcept, String entity, int relationtype) {
        switch (relationtype) {
            case RelationType.INSTANCE_HYPERNYM:
                return synsetconcept.getInstanceHyponym(entity);
            case RelationType.INSTANCE_HYPONYM:
                return synsetconcept.getInstanceHypernym(entity);
            case RelationType.MEMBER_HOLONYM:
                return synsetconcept.getMemberMeronym(entity);
            case RelationType.MEMBER_MERONYM:
                return synsetconcept.getMemberHolonym(entity);
            case RelationType.PART_HOLONYM:
                return synsetconcept.getPartMeronym(entity);
            case RelationType.PART_MERONYM:
                return synsetconcept.getPartHolonym(entity);
            case RelationType.SUBSTANCE_HOLONYM:
                return synsetconcept.getSubstanceMeronym(entity);
            case RelationType.SUBSTANCE_MERONYM:
                return synsetconcept.getSubstanceHolonym(entity);
            case RelationType.HYPERNYM:
                return synsetconcept.getHyponym(entity);
            case RelationType.HYPONYM:
                return synsetconcept.getHypernym(entity);
            default:
                return null;
        }
    }

//    private boolean containsAxiom(Axiom axiom) {
//        for (Axiom ax : this.Axioms) {
//            if( ax.getObject().equals(axiom.getObject()) && ax.getSubject().equals(axiom.getSubject()) && ax.getRelation().equals(axiom.getRelation()))
//                return true;
//        }
//        return false;
//    }
//    
//    private boolean compareMapps(OntologyMapp aMapp, OntologyMapp Mapp) {
//        if (aMapp.getSynsetConcept().getConceptName().equals(Mapp.getSynsetConcept().getConceptName()) && aMapp.getOntClass().equals(Mapp.getOntClass())) {
//            return true;
//        }
//        return false;
//    }
//
//    private void getAxioms() {
//        List<OntologyMapp> toDelete = new ArrayList<OntologyMapp>();
//        for (OntologyMapp Mapp : this.Mapps) {
//            if (Mapp.getRelationType() == RelationType.EQUIVALENT) {
//                AxiomElementSynset axiom_element = new AxiomElementSynset(Mapp.getSynsetConcept().getSynset(), Mapp.getOntClass());
//                addClassAxiom(axiom_element);
//                toDelete.add(Mapp);
//            }
//            if (Mapp.getRelationType() == RelationType.SYNONYM) {
//                AxiomElementSynset axiom_element = new AxiomElementSynset(Mapp.getSynsetConcept().getSynset(), Mapp.getOntClass());
//                addClassAxiom(axiom_element);
////                Axiom axiom = new Axiom(axiom_element,"Class",AxiomType.Type);
////                this.Axioms.add(axiom);
//                Axiom label_axiom = new Axiom(axiom_element,
//                        (this.Ontology.isNamedbyLocalName()
//                        ? Mapp.getOntClass().getIRI().getFragment() : this.Ontology.getLabel(Mapp.getOntClass())),
//                        AxiomType.HasLabel);
//                this.Axioms.add(label_axiom);
//                toDelete.add(Mapp);
//            }
//        }
//        this.Mapps.removeAll(toDelete);
//    }
//
//    private void addAxiom(AxiomElementSynset subject, AxiomElementSynset object, int relationtype) {
//        Axiom axiom = new Axiom(subject,object, AxiomType.getAxiomType(relationtype));
//        if(!containsAxiom(axiom))
//            this.Axioms.add(axiom);
//    }
//    
//    private Axiom getClassAxiom(NounSynset FirstElement){
//        for (Axiom axiom : this.Axioms) {
//            if (axiom.getSubject().getFirstElementMapped().equals(FirstElement)) {
//                return axiom;
//            }
//        }
//        return null;
//    }
//
//
//    private boolean containsClassAxiom(OWLClass axiom_element) {
//        for (Axiom axiom : this.Axioms) {
//            if (axiom.getSubject().getFirstElementMapped().equals(axiom_element)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private void processHerarchyMapps() {
//        processHypernymMapps();
//        processHyponymMapps();
////        processPartOfMapps();
////        int n = this.Mapps.size();
////        for (int i = 0; i < n; i++) {
////            OntologyMapp Mapp = this.Mapps.get(i);
////            switch(Mapp.getRelationType()){
////                case RelationType.HYPERNYM:
////                    if (Ontology.isNamedbyLocalName()) {
////                        addMapping(new OntologyMapp(
////                                new SynsetConcept(Mapp.getSynsetConcept().getHyponym(Mapp.getOntClass().getIRI().getFragment())),
////                                Mapp.getOntClass(), RelationType.EQUIVALENT));
////                    } else {
////                        addMapping(new OntologyMapp(
////                                new SynsetConcept(Mapp.getSynsetConcept().getHyponym(Ontology.getLabel(Mapp.getOntClass()))),
////                                Mapp.getOntClass(), RelationType.EQUIVALENT));
////                    }
////                    break;
////                case RelationType.HYPONYM:
////                    if (Ontology.isNamedbyLocalName()) {
////                        addMapping(new OntologyMapp(
////                                new SynsetConcept(Mapp.getSynsetConcept().getHypernym(Mapp.getOntClass().getIRI().getFragment())),
////                                Mapp.getOntClass(), RelationType.EQUIVALENT));
////                    } else {
////                        addMapping(new OntologyMapp(
////                                new SynsetConcept(Mapp.getSynsetConcept().getHypernym(Ontology.getLabel(Mapp.getOntClass()))),
////                                Mapp.getOntClass(), RelationType.EQUIVALENT));
////                    }
////                    break;
////                case RelationType.MEMBER_HOLONYM:
////                    break;
////                case RelationType.MEMBER_MERONYM:
////                    break;
////                case RelationType.PART_HOLONYM:
////                    break;
////                case RelationType.PART_MERONYM:
////                    break;
////                case RelationType.SUBSTANCE_HOLONYM:
////                    break;
////                case RelationType.SUBSTANCE_MERONYM:
////                    break;
////                case RelationType.INSTANCE_HYPERNYM:
////                    break;
////                case RelationType.INSTANCE_HYPONYM:
////                    break;
////                default:
////                    break;
////           }
////        }
//
//    }
//    private void processPartOfMapps() {
//        int n = this.Mapps.size();
//        for (int i = 0; i < n; i++) {
//            OntologyMapp Mapp = this.Mapps.get(i);
//        }
//    }
// private boolean containsMapp(OntologyMapp Mapp) {
//        for (OntologyMapp aMapp : this.Mapps) {
//            if (compareMapps(aMapp, Mapp)) {
//                return true;
//            }
//        }
//        return false;
//    }
//    private boolean addMapping(OntologyMapp Mapp) {
//        boolean containsMapp;
//        if (!(containsMapp = containsMapp(Mapp))) {
//            this.Mapps.add(Mapp);
//        }
//        return !containsMapp;
//    }
//    private void processHypernymMapps() {
//        int n = this.Mapps.size();
//        for (int i = 0; i < n; i++) {
//            OntologyMapp Mapp = this.Mapps.get(i);
//            if (Mapp.getRelationType() == RelationType.HYPERNYM) {
//                if (Ontology.isNamedbyLocalName()) {
//                    addMapping(new OntologyMapp(
//                            new SynsetConcept(Mapp.getSynsetConcept().getHyponym(Mapp.getOntClass().getIRI().getFragment())),
//                            Mapp.getOntClass(), RelationType.EQUIVALENT));
//                } else {
//                    addMapping(new OntologyMapp(
//                            new SynsetConcept(Mapp.getSynsetConcept().getHyponym(Ontology.getLabel(Mapp.getOntClass()))),
//                            Mapp.getOntClass(), RelationType.EQUIVALENT));
//                }
//            }
//        }
//        //this.mappings.removeAll(toDelete);
//    }
//    private void processHyponymMapps() {
//        int n = this.Mapps.size();
//        for (int i = 0; i < n; i++) {
//            OntologyMapp Mapp = this.Mapps.get(i);
//            if (Mapp.getRelationType() == RelationType.HYPONYM) {
//                if (Ontology.isNamedbyLocalName()) {
//                    addMapping(new OntologyMapp(
//                            new SynsetConcept(Mapp.getSynsetConcept().getHypernym(Mapp.getOntClass().getIRI().getFragment())),
//                            Mapp.getOntClass(), RelationType.EQUIVALENT));
//                } else {
//                    addMapping(new OntologyMapp(
//                            new SynsetConcept(Mapp.getSynsetConcept().getHypernym(Ontology.getLabel(Mapp.getOntClass()))),
//                            Mapp.getOntClass(), RelationType.EQUIVALENT));
//                }
//            }
//        }
//    }
    public static void main(String[] args) throws OWLOntologyCreationException {
//        String MDTerms = "breast/TermName/breast.n.02/TermSense/breast.n.01/SynsetName/the front of the trunk from the neck to the abdomen/SynsetDefinition//Synset/breast.n.02/SynsetName/either of two soft fleshy milk-secreting glandular organs on the chest of a woman/SynsetDefinition//Synset/breast.n.03/SynsetName/meat carved from the breast of a fowl/SynsetDefinition//Synset/breast.n.04/SynsetName/the part of an animal's body that corresponds to a person's chest/SynsetDefinition//Synset/";
//        String[] Ontologies = {"c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_0.owl"
//        };
        String MDTerms = "breast/TermName/breast.n.02/TermSense/breast.n.01/SynsetName/the front of the trunk from the neck to the abdomen/SynsetDefinition//Synset/breast.n.02/SynsetName/either of two soft fleshy milk-secreting glandular organs on the chest of a woman/SynsetDefinition//Synset/breast.n.03/SynsetName/meat carved from the breast of a fowl/SynsetDefinition//Synset/breast.n.04/SynsetName/the part of an animal's body that corresponds to a person's chest/SynsetDefinition//Synset/"
                + "/Term/cancer/TermName/cancer.n.01/TermSense/cancer.n.01/SynsetName/any malignant growth or tumor caused by abnormal and uncontrolled cell division; it may spread to other parts of the body through the lymphatic system or the blood stream/SynsetDefinition//Synset/cancer.n.02/SynsetName/(astrology) a person who is born while the sun is in Cancer/SynsetDefinition//Synset/cancer.n.03/SynsetName/a small zodiacal constellation in the northern hemisphere; between Leo and Gemini/SynsetDefinition//Synset/cancer.n.04/SynsetName/the fourth sign of the zodiac; the sun is in this sign from about June 21 to July 22/SynsetDefinition//Synset/cancer.n.05/SynsetName/type genus of the family Cancridae/SynsetDefinition//Synset/"
                + "/Term/cell/TermName/cell.n.02/TermSense/cell.n.01/SynsetName/any small compartment/SynsetDefinition//Synset/cell.n.02/SynsetName/(biology) the basic structural and functional unit of all organisms; they may exist as independent units of life (as in monads) or may form colonies or tissues as in higher plants and animals/SynsetDefinition//Synset/cell.n.03/SynsetName/a device that delivers an electric current as the result of a chemical reaction/SynsetDefinition//Synset/cell.n.04/SynsetName/a small unit serving as part of or as the nucleus of a larger political movement/SynsetDefinition//Synset/cellular_telephone.n.01/SynsetName/a hand-held mobile radiotelephone for use in an area divided into small sections, each with its own short-range transmitter/receiver/SynsetDefinition//Synset/cell.n.06/SynsetName/small room in which a monk or nun lives/SynsetDefinition//Synset/cell.n.07/SynsetName/a room where a prisoner is kept/SynsetDefinition//Synset/"
                + "/Term/patient/TermName/patient.n.01/TermSense/patient.n.01/SynsetName/a person who requires medical care/SynsetDefinition//Synset/affected_role.n.01/SynsetName/the semantic role of an entity that is not the agent but is directly involved in or affected by the happening denoted by the verb in the clause/SynsetDefinition//Synset/"
                + "/Term/study/TermName/study.n.09/TermSense/survey.n.01/SynsetName/a detailed critical inspection/SynsetDefinition//Synset/study.n.02/SynsetName/applying the mind to learning and understanding a subject (especially by reading)/SynsetDefinition//Synset/report.n.01/SynsetName/a written document describing the findings of some individual or group/SynsetDefinition//Synset/study.n.04/SynsetName/a state of deep mental absorption/SynsetDefinition//Synset/study.n.05/SynsetName/a room used for reading and writing and studying/SynsetDefinition//Synset/discipline.n.01/SynsetName/a branch of knowledge/SynsetDefinition//Synset/sketch.n.01/SynsetName/preliminary drawing for later elaboration/SynsetDefinition//Synset/cogitation.n.02/SynsetName/attentive consideration and meditation/SynsetDefinition//Synset/study.n.09/SynsetName/someone who memorizes quickly and easily (as the lines for a part in a play)/SynsetDefinition//Synset/study.n.10/SynsetName/a composition intended to develop one aspect of the performer's technique/SynsetDefinition//Synset/"
                + "/Term/breast cancer/TermName/breast_cancer.n.01/TermSense/breast_cancer.n.01/SynsetName/cancer of the breast; one of the most common malignancies in women in the US/SynsetDefinition//Synset/"
                + "/Term/cancer patient/TermName/None/TermSense/"
                + "/Term/cancer cell/TermName/cancer_cell.n.01/TermSense/cancer_cell.n.01/SynsetName/a cell that is part of a malignant tumor/SynsetDefinition//Synset/"
                + "/Term/breast cancer patient/TermName/None/TermSense/"
                + "/Term/breast cancer cell/TermName/None/TermSense/"
                + "/Term/cell line/TermName/None/TermSense/";
        String[] Ontologies = {"c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_0.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_1.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_2.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_3.owl",
            //            //            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_4.owl",
            //            //            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_0.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_5.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_6.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_7.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_8.owl"
        };


        for (String ontology : Ontologies) {
            OntologyManager Ontology = new OntologyManager(ontology, true);
            System.out.println("**Ontology " + Arrays.asList(Ontologies).indexOf(ontology));
            int index = 0;
            for (String MDTerm : MDTerms.split("/Term/")) {
                System.out.println("\t**Term " + (++index));
                Term term = new Term(MDTerm);
                if (term.getSelectedSynset() != null) {
                    new SynsetOntologyAxioms(term.getSelectedSynset(), Ontology);
                }
            }
        }

    }

    public static void main2(String[] args) throws OWLOntologyCreationException {
        String MDTerms = "breast/TermName/breast.n.02/TermSense/breast.n.01/SynsetName/the front of the trunk from the neck to the abdomen/SynsetDefinition//Synset/breast.n.02/SynsetName/either of two soft fleshy milk-secreting glandular organs on the chest of a woman/SynsetDefinition//Synset/breast.n.03/SynsetName/meat carved from the breast of a fowl/SynsetDefinition//Synset/breast.n.04/SynsetName/the part of an animal's body that corresponds to a person's chest/SynsetDefinition//Synset/";
        String[] Ontologies = {"c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_0.owl"
        };

        int i = 0;
        for (String ontology : Ontologies) {
            OntologyManager Ontology = new OntologyManager(ontology, true);
            for (String MDTerm : MDTerms.split("/Term/")) {
                Term term = new Term(MDTerm);
                if (term.getSelectedSynset() != null) {
                    SynsetOntologyMapps mappings = new SynsetOntologyMapps(new SynsetConcept(term.getSelectedSynset()), Ontology);

                    for (OntologyMapp map : mappings.getMappings()) {
                        System.out.println("\t" + (++i) + ".- " + map.getSynsetConcept().getConceptName() + getRelationType(map) + Ontology.getLabels(map.getOntClass()).toString() + "@" + map.getOntClass().getIRI());
                    }
                }

            }
        }
    }

    public static void main1(String[] args) throws OWLOntologyCreationException {
        String MDTerms = "breast/TermName/breast.n.02/TermSense/breast.n.01/SynsetName/the front of the trunk from the neck to the abdomen/SynsetDefinition//Synset/breast.n.02/SynsetName/either of two soft fleshy milk-secreting glandular organs on the chest of a woman/SynsetDefinition//Synset/breast.n.03/SynsetName/meat carved from the breast of a fowl/SynsetDefinition//Synset/breast.n.04/SynsetName/the part of an animal's body that corresponds to a person's chest/SynsetDefinition//Synset/"
                + "/Term/cancer/TermName/cancer.n.01/TermSense/cancer.n.01/SynsetName/any malignant growth or tumor caused by abnormal and uncontrolled cell division; it may spread to other parts of the body through the lymphatic system or the blood stream/SynsetDefinition//Synset/cancer.n.02/SynsetName/(astrology) a person who is born while the sun is in Cancer/SynsetDefinition//Synset/cancer.n.03/SynsetName/a small zodiacal constellation in the northern hemisphere; between Leo and Gemini/SynsetDefinition//Synset/cancer.n.04/SynsetName/the fourth sign of the zodiac; the sun is in this sign from about June 21 to July 22/SynsetDefinition//Synset/cancer.n.05/SynsetName/type genus of the family Cancridae/SynsetDefinition//Synset/"
                + "/Term/cell/TermName/cell.n.02/TermSense/cell.n.01/SynsetName/any small compartment/SynsetDefinition//Synset/cell.n.02/SynsetName/(biology) the basic structural and functional unit of all organisms; they may exist as independent units of life (as in monads) or may form colonies or tissues as in higher plants and animals/SynsetDefinition//Synset/cell.n.03/SynsetName/a device that delivers an electric current as the result of a chemical reaction/SynsetDefinition//Synset/cell.n.04/SynsetName/a small unit serving as part of or as the nucleus of a larger political movement/SynsetDefinition//Synset/cellular_telephone.n.01/SynsetName/a hand-held mobile radiotelephone for use in an area divided into small sections, each with its own short-range transmitter/receiver/SynsetDefinition//Synset/cell.n.06/SynsetName/small room in which a monk or nun lives/SynsetDefinition//Synset/cell.n.07/SynsetName/a room where a prisoner is kept/SynsetDefinition//Synset/"
                + "/Term/patient/TermName/patient.n.01/TermSense/patient.n.01/SynsetName/a person who requires medical care/SynsetDefinition//Synset/affected_role.n.01/SynsetName/the semantic role of an entity that is not the agent but is directly involved in or affected by the happening denoted by the verb in the clause/SynsetDefinition//Synset/"
                + "/Term/study/TermName/study.n.09/TermSense/survey.n.01/SynsetName/a detailed critical inspection/SynsetDefinition//Synset/study.n.02/SynsetName/applying the mind to learning and understanding a subject (especially by reading)/SynsetDefinition//Synset/report.n.01/SynsetName/a written document describing the findings of some individual or group/SynsetDefinition//Synset/study.n.04/SynsetName/a state of deep mental absorption/SynsetDefinition//Synset/study.n.05/SynsetName/a room used for reading and writing and studying/SynsetDefinition//Synset/discipline.n.01/SynsetName/a branch of knowledge/SynsetDefinition//Synset/sketch.n.01/SynsetName/preliminary drawing for later elaboration/SynsetDefinition//Synset/cogitation.n.02/SynsetName/attentive consideration and meditation/SynsetDefinition//Synset/study.n.09/SynsetName/someone who memorizes quickly and easily (as the lines for a part in a play)/SynsetDefinition//Synset/study.n.10/SynsetName/a composition intended to develop one aspect of the performer's technique/SynsetDefinition//Synset/"
                + "/Term/breast cancer/TermName/breast_cancer.n.01/TermSense/breast_cancer.n.01/SynsetName/cancer of the breast; one of the most common malignancies in women in the US/SynsetDefinition//Synset/"
                + "/Term/cancer patient/TermName/None/TermSense/"
                + "/Term/cancer cell/TermName/cancer_cell.n.01/TermSense/cancer_cell.n.01/SynsetName/a cell that is part of a malignant tumor/SynsetDefinition//Synset/"
                + "/Term/breast cancer patient/TermName/None/TermSense/"
                + "/Term/breast cancer cell/TermName/None/TermSense/"
                + "/Term/cell line/TermName/None/TermSense/";
        String[] Ontologies = {"c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_0.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_1.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_2.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_3.owl",
            //            //            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_4.owl",
            //            //            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_0.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_5.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_6.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_7.owl",
            "c:/Users/Vieyra/Desktop/KnOWLearn/Breast_Cancer-Modules_v7/Module_8.owl"
        };
        int i = 0;
        for (String ontology : Ontologies) {
            OntologyManager Ontology = new OntologyManager(ontology, true);
            for (String MDTerm : MDTerms.split("/Term/")) {
                Term term = new Term(MDTerm);
                if (term.getSelectedSynset() != null) {
                    SynsetOntologyMapps mappings = new SynsetOntologyMapps(new SynsetConcept(term.getSelectedSynset()), Ontology);

                    for (OntologyMapp map : mappings.getMappings()) {
                        System.out.println("\t" + (++i) + ".- " + map.getSynsetConcept().getConceptName() + getRelationType(map) + Ontology.getLabels(map.getOntClass()).toString() + "@" + map.getOntClass().getIRI());
                    }
                }

            }

        }
    }

    private static String getRelationType(OntologyMapp mapp) {
        int relationtype = mapp.getRelationType();
        switch (relationtype) {
            case RelationType.EQUIVALENT:
                return " is equivalent to ";
            case RelationType.SYNONYM:
                return " is synonym of ";
            case RelationType.INSTANCE_HYPERNYM:
                return " is instance hypernym of ";
            case RelationType.INSTANCE_HYPONYM:
                return " is instance hypernym of ";
            case RelationType.MEMBER_HOLONYM:
                return " is member holonym of ";
            case RelationType.MEMBER_MERONYM:
                return " is member meronym of  ";
            case RelationType.PART_HOLONYM:
                return " is part holonym of ";
            case RelationType.PART_MERONYM:
                return " is part meronym of  ";
            case RelationType.SUBSTANCE_HOLONYM:
                return " is substance holonym of ";
            case RelationType.SUBSTANCE_MERONYM:
                return " is substance meronym of  ";
            case RelationType.PARTIAL:
                return " matches with ";
            case RelationType.HYPERNYM:
                return " is hypernym of  ";
            case RelationType.HYPONYM:
                return " is hyponym of  ";
            default:
                return "don't have relation with ";

        }
    }
}
