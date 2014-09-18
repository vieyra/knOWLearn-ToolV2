package InitialScheme.Sources.OntologySources;

import InitialScheme.Mapping.OntologyMapp;
import InitialScheme.Mapping.RelationType;
import InitialScheme.Sources.OntologySources.Axioms.AxiomElement.AxiomElement;
import InitialScheme.Sources.WordNetSources.SynsetConcept;
import InitialScheme.Sources.WordNetSources.WordNet;
import Utils.string;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.xml.sax.SAXParseException;

public class OntologyManager {

    private OWLOntologyManager Manager;
    private OWLOntology Ontology;
    private OWLDataFactory DataFactory;
    private IRI Iri;
    private boolean namedbyLocalName = true;
    private boolean opened = true;

    public OntologyManager(String Iri){
        try{
        loadOntology(Iri, false);
        }catch(Exception ex){
            
        }
    }

    
    public OntologyManager(String Iri, boolean fromFile){
        try{
            loadOntology(Iri, fromFile);
        }catch(Exception ex){
            System.err.println((ex.getMessage().length() > 200) ? (ex.getMessage().split("\n")) :  ex.getMessage());//.substring(ex.getMessage().length() - 25));;
            this.opened = false;
        }
    }

    public boolean isOpened() {
        return opened;
    }
    
    

    private void loadOntology(String Iri, boolean fromFile) throws OWLOntologyCreationException, SAXParseException {
        Manager = OWLManager.createOWLOntologyManager();
        if (fromFile) {
            loadOntologyfromFile(Iri);
        } else {
            loadOntologyfromIRI(Iri);
        }
        this.DataFactory = this.Manager.getOWLDataFactory();
    }

    private void loadOntologyfromIRI(String Iri) throws OWLOntologyCreationException, SAXParseException {
        this.Iri = IRI.create(Iri);
        this.Ontology = this.Manager.loadOntologyFromOntologyDocument(this.Iri);
    }

    private void loadOntologyfromFile(String FileName) throws OWLOntologyCreationException, SAXParseException {
        File file = new File(FileName);
        this.Ontology = this.Manager.loadOntologyFromOntologyDocument(file);
        this.Iri = this.Manager.getOntologyDocumentIRI(this.Ontology);
    }

    public OWLDataFactory getDataFactory() {
        return DataFactory;
    }

    public IRI getIri() {
        return Iri;
    }

    public OWLOntologyManager getManager() {
        return Manager;
    }

    public OWLOntology getOntology() {
        return Ontology;
    }

    public boolean isNamedbyLocalName() {
        return namedbyLocalName;
    }

    public void setNamedbyLocalName(boolean namedbyLocalName) {
        this.namedbyLocalName = namedbyLocalName;
    }

    public List<OntologyMapp> searchSynsetInOntology(SynsetConcept synset) {
        List<OntologyMapp> MappingsWithOntologyClasses = new ArrayList<OntologyMapp>();
        Set<OWLClass> classes = this.getOntology().getClassesInSignature();
        int relatyontype = -1;
        for (OWLClass Class : classes) {
                if ((relatyontype = synset.isSimilarTo(Class.getIRI().getFragment())) != -1) {
                    MappingsWithOntologyClasses.add(new OntologyMapp(synset, Class, relatyontype));
                    this.namedbyLocalName = true;
                    continue;
                }
            for(OWLAnnotation annotation : this.getLabels(Class)){
                String label = getLabel(annotation.getValue().toString());
                if ((relatyontype = synset.isSimilarTo(label)) != -1) {
                    MappingsWithOntologyClasses.add(new OntologyMapp(synset, Class, relatyontype));
                    this.namedbyLocalName = false;
                    break;
                }
            }
        }
        return MappingsWithOntologyClasses;
    }
    
    public OntologyConcept searchTermInOntology(String term) {
        Set<OWLClass> classes = this.getOntology().getClassesInSignature();
        term = string.normalizeEntityName(term);
        double jwd;
        OntologyConcept Concept = new OntologyConcept(term);
        for (OWLClass Class : classes) {
            if ((jwd = string.JaroWinklerDistance(term,
                    string.normalizeEntityName(
                Class.getIRI().getFragment().toLowerCase())))
                    > 0.98) {
                if (Concept.setOntClass(Class, jwd)) {
                    this.setNamedbyLocalName(true);
                }
            } else {
                for (OWLAnnotation annotation : Class.getAnnotations(this.getOntology(), this.getDataFactory().getRDFSLabel())) {
                    if ((jwd = string.JaroWinklerDistance(
                            string.normalizeEntityName(getLabel(annotation.getValue().toString())),
                            Concept.getConceptName()))
                            > 0.98) {
                        if (Concept.setOntClass(Class, jwd)) {
                            this.setNamedbyLocalName(false);
                        }
                    }
                }
            }
        }
        return Concept;
    }

    public String getLabel(OWLClass Class) {
        for(OWLAnnotation annotation : this.getLabels(Class))
            return getLabel(annotation.getValue().toString());
        return "";
    }
    public Set<OWLAnnotation> getLabels(OWLClass Class) {
        return Class.getAnnotations(this.Ontology, this.getDataFactory().getRDFSLabel());
    }

    public List<OWLClass> getSubClasses(OWLClass Class, List<OWLClass> SubClasses) {
        for (OWLClassExpression ClassExpression : Class.getSubClasses(Ontology)) {
            if (!ClassExpression.isAnonymous()) {
                OWLClass SubClass = (OWLClass) ClassExpression;
                if (!SubClasses.contains(SubClass)) {
                    SubClasses.add(SubClass);
                }
                SubClasses = getSubClasses(SubClass, SubClasses);
            }
        }
        return SubClasses;
    }

    public int getLevelOfRelation(OWLClass subjectClass, OWLClass objectClass) {
        return getNivelOfRelation(subjectClass, objectClass, 6);
    }
    
    private List<OWLClass> getSuperClasses(OWLClass OntClass){
        List<OWLClass> SuperClasses = new ArrayList<OWLClass>();
        for (OWLClassExpression classExpression : OntClass.getSuperClasses(Ontology)) {
            if (!classExpression.isAnonymous()) {
                SuperClasses.add(classExpression.asOWLClass());
            }
        }
        return SuperClasses;
    }
    
    public int getNivelOfRelation(OWLClass subjectClass, OWLClass objectClass, int levelmax) {
//        System.out.println(subjectClass.getSuperClasses(Ontology).size());
        List<OWLClass> SuperClasses = getSuperClasses(subjectClass);
//        System.out.println(SuperClasses.size());
        for (int i = 1; i < levelmax; i++) {
            int superClassesSize = SuperClasses.size();
            if (superClassesSize == 0) {
                break;
            }
            for(OWLClass OntClass : SuperClasses){
//                System.out.println("**Comparying " + OntClass + " \t " + objectClass);
                if(objectClass.equals(OntClass)){
                    return i;
                }
            }
            List<OWLClass> aux = new ArrayList<OWLClass>();
            for (int j = 0; j < superClassesSize; j++) {
                aux.addAll(getSuperClasses(SuperClasses.get(j)));
            }
            SuperClasses.clear();
            SuperClasses.addAll(aux);
        }
        return -1;
    }

    
    public List<OWLClass> getSuperClasses(OWLClass Class, List<OWLClass> SuperClasses) {
        for (OWLClassExpression ClassExpression : Class.getSuperClasses(Ontology)) {
            if (!ClassExpression.isAnonymous()) {
                OWLClass SuperClass = /*(OWLClass) */ClassExpression.asOWLClass();
                if (!SuperClasses.contains(SuperClass)) {
                    SuperClasses.add(SuperClass);
                }
                SuperClasses = getSuperClasses(SuperClass, SuperClasses);
            }
        }
        return SuperClasses;
    }

    public static String getLabel(String value) {
        value = value.replace("\"", "").replace("^^xsd:string", "");
        if (value.matches(".*@[a-zA-Z]+")) {
            value = value.substring(0, value.indexOf("@"));
        }
        return value;
    }
    
    
    public static void printMapp(OntologyMapp mapp){
        int relationtype = mapp.getRelationType();
        String SynsetName = mapp.getSynsetConcept().getConceptName();
        String EntityName = mapp.getOntClass().getIRI().getFragment();
        switch(relationtype){
            case RelationType.EQUIVALENT:
                System.out.println(SynsetName + " is equivalent to " + EntityName);
                break;
            case RelationType.SYNONYM:
                System.out.println(SynsetName + " is synonym of " + EntityName);
                break;
            case RelationType.INSTANCE_HYPERNYM:
                System.out.println(SynsetName + " is instance hypernym of " + EntityName);
                break;
            case RelationType.INSTANCE_HYPONYM:
                System.out.println(SynsetName + " is instance hypernym of " + EntityName);
                break;
            case RelationType.MEMBER_HOLONYM:
                System.out.println(SynsetName + " is member holonym of " + EntityName);
                break;
            case RelationType.MEMBER_MERONYM:
                System.out.println(SynsetName + " is member meronym of  " + EntityName);
                break;
            case RelationType.PART_HOLONYM:
                System.out.println(SynsetName + " is part holonym of " + EntityName);
                break;
            case RelationType.PART_MERONYM:
                System.out.println(SynsetName + " is part meronym of  " + EntityName);
                break;
            case RelationType.SUBSTANCE_HOLONYM:
                System.out.println(SynsetName + " is substance holonym of " + EntityName);
                break;
            case RelationType.SUBSTANCE_MERONYM:
                System.out.println(SynsetName + " is substance meronym of  " + EntityName);
                break;
            case RelationType.PARTIAL:
                System.out.println(SynsetName + " matchs with " + EntityName);
                break;
            default:
                System.out.println(SynsetName + " and  " + EntityName + " don't have relation");
                break;
        }
    }
    
    public static void main(String[] args) throws OWLOntologyCreationException{
        OntologyManager Ontology = new OntologyManager("ontosem.owl",true);
        OWLClass SubjectClass = Ontology.searchTermInOntology("fibroglandular-mass-of-breast").getOntClass();
        OWLClass ObjectClass = Ontology.searchTermInOntology("anatomical-structure").getOntClass();
        System.out.println(SubjectClass + "\t" + ObjectClass + "\t" + 
                Ontology.getLevelOfRelation(SubjectClass, ObjectClass));
    }


}