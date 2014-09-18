package InitialScheme.Mapping;

import InitialScheme.Sources.OntologySources.OntologyManager;
import InitialScheme.Sources.Term;
import InitialScheme.Sources.WordNetSources.SynsetConcept;
import InitialScheme.Sources.WordNetSources.WordNet;
import edu.smu.tspell.wordnet.NounSynset;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class SynsetOntologyMapps {

    private SynsetConcept Synset;
    private OntologyManager Ontology;
    private OWLClass Class;
    private List<OntologyMapp> Mappings;

    public SynsetOntologyMapps(SynsetConcept Synset, OntologyManager Ontology) {
        this.Synset = Synset;
        this.Ontology = Ontology;
        this.Mappings = Ontology.searchSynsetInOntology(this.Synset);
        int l = this.Mappings.size();
        for(int i = 0 ; i < l; i++){
            OntologyMapp mapp = this.Mappings.get(i);
            if(mapp.getRelationType() == RelationType.EQUIVALENT){
                this.Class = mapp.getOntClass();
                SearchMapps();                
            }
        }
    }

    private void SearchMapps() {
        SearchMappsInHypernyms();
        SearchMappsInHyponyms();
        SearchMappsHypernymsWithHyponyms();
    }

    private void findMapping(NounSynset synset, OWLClass Class) {
        int relationtype = -1;
        SynsetConcept Synset = new SynsetConcept(synset);
        if (Ontology.isNamedbyLocalName()) {
//            if(Synset.getConceptName().matches("exocrine")){
//                System.out.println("Searching mapp between "+ Synset.getConceptName() +" and " + Class.getIRI().getFragment());
//                System.out.println(relationtype = Synset.isSimilarTo(Class.getIRI().getFragment()));
//            }
            if ((relationtype = Synset.isSimilarTo(Class.getIRI().getFragment())) != -1) {
                this.Mappings.add(new OntologyMapp(Synset, Class, relationtype));
            }
        } else {
            for (OWLAnnotation annotation : Ontology.getLabels(Class)) {
                String label = OntologyManager.getLabel(annotation.getValue().toString());
//                if (Synset.getConceptName().matches("exocrine")) {
//                    System.out.println("Searching mapp between " + Synset.getConceptName() + " and " + label);
//                    System.out.println(relationtype = Synset.isSimilarTo(label));
//                }
                if ((relationtype = Synset.isSimilarTo(label)) != -1) {
                    this.Mappings.add(new OntologyMapp(Synset, Class, relationtype));
                }
            }
        }
    }
    
    private void SearchMappsInHypernyms() {
        List<NounSynset> Hypernyms = this.Synset.getHypernyms();
        Hypernyms.removeAll(WordNet.TopLevelEntities);
        List<OWLClass> SuperClasses = Ontology.getSuperClasses(this.Class, new ArrayList<OWLClass>());
        for (OWLClass Class : SuperClasses) {
            for (NounSynset Hypernym : Hypernyms) {
                //System.out.println("Searching mapps between " + Hypernym.getWordForms()[0] +" and " + Class.getIRI().getFragment() +":" + Ontology.getLabels(Class).toString());
                this.findMapping(Hypernym, Class);
                    
//                if (Ontology.isNamedbyLocalName()) {
//                    if (string.areSimilars(Hypernym.getWordForms()[0], Class.getIRI().getFragment())) {
//                        this.Mappings.add(new OntologyMapp(new SynsetConcept(Hypernym), Class));
//                    }
//                } else {
//                    for (OWLAnnotation annotation : Ontology.getLabels(Class)) {
//                        String label = OntologyManager.getLabel(annotation.getValue().toString());
//                        if (string.areSimilars(Hypernym.getWordForms()[0], label)) {
//                            this.Mappings.add(new OntologyMapp(new SynsetConcept(Hypernym), Class));
//                        }
//                    }
//                }
            }
        }
    }

    private void SearchMappsInHyponyms() {
        List<NounSynset> Hyponyms = this.Synset.getHyponyms();
        List<OWLClass> SubClasses = Ontology.getSubClasses(this.Class, new ArrayList<OWLClass>());
        for (OWLClass Class : SubClasses) {
            for (NounSynset Hyponym : Hyponyms) {
                this.findMapping(Hyponym, Class);
//                if (Ontology.isNamedbyLocalName()) {
//                    if (string.areSimilars(Hyponym.getWordForms()[0], Class.getIRI().getFragment())) {
//                        this.Mappings.add(new OntologyMapp(new SynsetConcept(Hyponym), Class));
//                    }
//                } else {
//                    for (OWLAnnotation annotation : Ontology.getLabels(Class)) {
//                        String label = OntologyManager.getLabel(annotation.getValue().toString());
//                        if (string.areSimilars(Hyponym.getWordForms()[0], label)) {
//                            this.Mappings.add(new OntologyMapp(new SynsetConcept(Hyponym), Class));
//                        }
//                    }
//                }
            }
        }
    }

    private void SearchMappsHypernymsWithHyponyms() {
        List<NounSynset> Hypernyms = this.Synset.getHypernyms();
        List<OWLClass> SuperClasses = Ontology.getSuperClasses(this.Class, new ArrayList<OWLClass>());
        List<NounSynset> Hyponyms = this.Synset.getHyponyms();
        List<OWLClass> SubClasses = Ontology.getSubClasses(this.Class, new ArrayList<OWLClass>());
        for (OWLClass Class : SubClasses) {
            for (NounSynset Hypernym : Hypernyms) {
                this.findMapping(Hypernym, Class);
//                if (Ontology.isNamedbyLocalName()) {
//                    if (string.areSimilars(Hypernym.getWordForms()[0], Class.getIRI().getFragment())) {
//                        this.Mappings.add(new OntologyMapp(new SynsetConcept(Hypernym), Class, RelationType.PARTIAL));
//                    }
//                } else {
//                    for (OWLAnnotation annotation : Ontology.getLabels(Class)) {
//                        String label = OntologyManager.getLabel(annotation.getValue().toString());
//                        if (string.areSimilars(Hypernym.getWordForms()[0], label)) {
//                            this.Mappings.add(new OntologyMapp(new SynsetConcept(Hypernym), Class, RelationType.PARTIAL));
//                        }
//                    }
//                }
            }
        }

        for (OWLClass Class : SuperClasses) {
            for (NounSynset Hyponym : Hyponyms) {
                this.findMapping(Hyponym, Class);
//                if (Ontology.isNamedbyLocalName()) {
//                    if (string.areSimilars(Hyponym.getWordForms()[0], Class.getIRI().getFragment())) {
//                        this.Mappings.add(new OntologyMapp(new SynsetConcept(Hyponym), Class, RelationType.PARTIAL));
//                    }
//                } else {
//                    for (OWLAnnotation annotation : Ontology.getLabels(Class)) {
//                        String label = OntologyManager.getLabel(annotation.getValue().toString());
//                        if (string.areSimilars(Hyponym.getWordForms()[0], label)) {
//                            this.Mappings.add(new OntologyMapp(new SynsetConcept(Hyponym), Class, RelationType.PARTIAL));
//                        }
//                    }
//                }
            }
        }
    }

    public List<OntologyMapp> getMappings() {
        return Mappings;
    }

    public SynsetConcept getSynset() {
        return Synset;
    }

    
    public static void main(String[] args) throws OWLOntologyCreationException {
        String MDTerms = "breast/TermName/breast.n.02/TermSense/breast.n.01/SynsetName/the front of the trunk from the neck to the abdomen/SynsetDefinition//Synset/breast.n.02/SynsetName/either of two soft fleshy milk-secreting glandular organs on the chest of a woman/SynsetDefinition//Synset/breast.n.03/SynsetName/meat carved from the breast of a fowl/SynsetDefinition//Synset/breast.n.04/SynsetName/the part of an animal's body that corresponds to a person's chest/SynsetDefinition//Synset/"
                + "/Term/cancer/TermName/cancer.n.01/TermSense/cancer.n.01/SynsetName/any malignant growth or tumor caused by abnormal and uncontrolled cell division; it may spread to other parts of the body through the lymphatic system or the blood stream/SynsetDefinition//Synset/cancer.n.02/SynsetName/(astrology) a person who is born while the sun is in Cancer/SynsetDefinition//Synset/cancer.n.03/SynsetName/a small zodiacal constellation in the northern hemisphere; between Leo and Gemini/SynsetDefinition//Synset/cancer.n.04/SynsetName/the fourth sign of the zodiac; the sun is in this sign from about June 21 to July 22/SynsetDefinition//Synset/cancer.n.05/SynsetName/type genus of the family Cancridae/SynsetDefinition//Synset/"
                + "/Term/cell/TermName/cell.n.02/TermSense/cell.n.01/SynsetName/any small compartment/SynsetDefinition//Synset/cell.n.02/SynsetName/(biology) the basic structural and functional unit of all organisms; they may exist as independent units of life (as in monads) or may form colonies or tissues as in higher plants and animals/SynsetDefinition//Synset/cell.n.03/SynsetName/a device that delivers an electric current as the result of a chemical reaction/SynsetDefinition//Synset/cell.n.04/SynsetName/a small unit serving as part of or as the nucleus of a larger political movement/SynsetDefinition//Synset/cellular_telephone.n.01/SynsetName/a hand-held mobile radiotelephone for use in an area divided into small sections, each with its own short-range transmitter/receiver/SynsetDefinition//Synset/cell.n.06/SynsetName/small room in which a monk or nun lives/SynsetDefinition//Synset/cell.n.07/SynsetName/a room where a prisoner is kept/SynsetDefinition//Synset/"
                + "/Term/patient/TermName/patient.n.01/TermSense/patient.n.01/SynsetName/a person who requires medical care/SynsetDefinition//Synset/affected_role.n.01/SynsetName/the semantic role of an entity that is not the agent but is directly involved in or affected by the happening denoted by the verb in the clause/SynsetDefinition//Synset/"
                + "/Term/study/TermName/study.n.09/TermSense/survey.n.01/SynsetName/a detailed critical inspection/SynsetDefinition//Synset/study.n.02/SynsetName/applying the mind to learning and understanding a subject (especially by reading)/SynsetDefinition//Synset/report.n.01/SynsetName/a written document describing the findings of some individual or group/SynsetDefinition//Synset/study.n.04/SynsetName/a state of deep mental absorption/SynsetDefinition//Synset/study.n.05/SynsetName/a room used for reading and writing and studying/SynsetDefinition//Synset/discipline.n.01/SynsetName/a branch of knowledge/SynsetDefinition//Synset/sketch.n.01/SynsetName/preliminary drawing for later elaboration/SynsetDefinition//Synset/cogitation.n.02/SynsetName/attentive consideration and meditation/SynsetDefinition//Synset/study.n.09/SynsetName/someone who memorizes quickly and easily (as the lines for a part in a play)/SynsetDefinition//Synset/study.n.10/SynsetName/a composition intended to develop one aspect of the performer's technique/SynsetDefinition//Synset/"
                + "/Term/breast cancer/TermName/breast_cancer.n.01/TermSense/breast_cancer.n.01/SynsetName/cancer of the breast; one of the most common malignancies in women in the US/SynsetDefinition//Synset/"
                + "/Term/";
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
                return " matchs with ";
            case RelationType.HYPERNYM:
                return " is hypernym of  ";
            case RelationType.HYPONYM:
                return " is hyponym of  ";
            default:
                return "don't have relation with ";

        }
    }

}

