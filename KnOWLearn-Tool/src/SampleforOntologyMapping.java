
import InitialScheme.Mapping.SynsetOntologyAxioms;
import InitialScheme.Sources.OntologySources.OntologyConcept;
import InitialScheme.Sources.OntologySources.OntologyManager;
import InitialScheme.Sources.Term;
import InitialScheme.Sources.WatsonDocument;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class SampleforOntologyMapping {

    static List<WatsonDocument> Documents;
    static List<Term> Terms;
    
    public static void main(String[] args) throws RemoteException, OWLOntologyCreationException{
        Documents = new ArrayList<WatsonDocument>();
        Terms = new ArrayList<Term>();
        init();
        getMappings();
    }
    
    public static void getMappings() throws OWLOntologyCreationException {
        System.out.println(Documents.size());
        for (WatsonDocument document : Documents) {
            OntologyManager Ontology = new OntologyManager(document.getFile(), true);
            System.out.println("**Ontology " + Documents.indexOf(document));
            int index = 0;
            for (Term term : Terms) {
                System.out.println("\t**Term " + (++index) + ": "+term.getName());
                if (document.getCoveredTerms().contains(term.getName())) {
                    if (term.getSelectedSynset() != null && !term.getSense().equals("None")) {
                        new SynsetOntologyAxioms(term.getSelectedSynset(), Ontology);
                    } else {
                        OntologyConcept concept = Ontology.searchTermInOntology(term.getName());
                        if (concept.getClass() != null) {
                            System.out.println("\t*Concept founded"
                                    + Ontology.getLabel(concept.getOntClass()).toString() + "@"
                                    + concept.getOntClass().getIRI().getFragment());
                        }
                    }
                }
            }
        }
    }
    
    
    
    public static void init(){
        String[] terms = {
            "breast/TermName/breast.n.02/TermSense/breast.n.01/SynsetName/the front of the trunk from the neck to the abdomen/SynsetDefinition//Synset/breast.n.02/SynsetName/either of two soft fleshy milk-secreting glandular organs on the chest of a woman/SynsetDefinition//Synset/breast.n.03/SynsetName/meat carved from the breast of a fowl/SynsetDefinition//Synset/breast.n.04/SynsetName/the part of an animal's body that corresponds to a person's chest/SynsetDefinition//Synset/",
            "cancer/TermName/cancer.n.01/TermSense/cancer.n.01/SynsetName/any malignant growth or tumor caused by abnormal and uncontrolled cell division; it may spread to other parts of the body through the lymphatic system or the blood stream/SynsetDefinition//Synset/cancer.n.02/SynsetName/(astrology) a person who is born while the sun is in Cancer/SynsetDefinition//Synset/cancer.n.03/SynsetName/a small zodiacal constellation in the northern hemisphere; between Leo and Gemini/SynsetDefinition//Synset/cancer.n.04/SynsetName/the fourth sign of the zodiac; the sun is in this sign from about June 21 to July 22/SynsetDefinition//Synset/cancer.n.05/SynsetName/type genus of the family Cancridae/SynsetDefinition//Synset/",
            "patient/TermName/patient.n.01/TermSense/patient.n.01/SynsetName/a person who requires medical care/SynsetDefinition//Synset/affected_role.n.01/SynsetName/the semantic role of an entity that is not the agent but is directly involved in or affected by the happening denoted by the verb in the clause/SynsetDefinition//Synset/",
            "women/TermName/woman.n.01/TermSense/woman.n.01/SynsetName/an adult female person (as opposed to a man)/SynsetDefinition//Synset/woman.n.02/SynsetName/a female person who plays a significant role (wife or mistress or girlfriend) in the life of a particular man/SynsetDefinition//Synset/charwoman.n.01/SynsetName/a human female employed to do housework/SynsetDefinition//Synset/womanhood.n.02/SynsetName/women as a class/SynsetDefinition//Synset/",
            "tumor/TermName/tumor.n.01/TermSense/tumor.n.01/SynsetName/an abnormal new mass of tissue that serves no purpose/SynsetDefinition//Synset/",
            "expression/TermName/expression.n.07/TermSense/expression.n.01/SynsetName/the feelings expressed on a person's face/SynsetDefinition//Synset/expression.n.02/SynsetName/expression without words/SynsetDefinition//Synset/expression.n.03/SynsetName/the communication (in speech or writing) of your beliefs or opinions/SynsetDefinition//Synset/saying.n.01/SynsetName/a word or phrase that particular people use in particular situations/SynsetDefinition//Synset/formulation.n.03/SynsetName/the style of expressing yourself/SynsetDefinition//Synset/formula.n.01/SynsetName/a group of symbols that make a mathematical statement/SynsetDefinition//Synset/expression.n.07/SynsetName/(genetics) the process of expressing a gene/SynsetDefinition//Synset/construction.n.02/SynsetName/a group of words that form a constituent of a sentence and are considered as a single unit/SynsetDefinition//Synset/expression.n.09/SynsetName/the act of forcing something out by squeezing or pressing/SynsetDefinition//Synset/",
            "protein/TermName/protein.n.01/TermSense/protein.n.01/SynsetName/any of a large group of nitrogenous organic compounds that are essential constituents of living cells; consist of polymers of amino acids; essential in the diet of animals for growth and for repair of tissues; can be obtained from meat and eggs and milk and legumes/SynsetDefinition//Synset/",
            "cell/TermName/cell.n.02/TermSense/cell.n.01/SynsetName/any small compartment/SynsetDefinition//Synset/cell.n.02/SynsetName/(biology) the basic structural and functional unit of all organisms; they may exist as independent units of life (as in monads) or may form colonies or tissues as in higher plants and animals/SynsetDefinition//Synset/cell.n.03/SynsetName/a device that delivers an electric current as the result of a chemical reaction/SynsetDefinition//Synset/cell.n.04/SynsetName/a small unit serving as part of or as the nucleus of a larger political movement/SynsetDefinition//Synset/cellular_telephone.n.01/SynsetName/a hand-held mobile radiotelephone for use in an area divided into small sections, each with its own short-range transmitter/receiver/SynsetDefinition//Synset/cell.n.06/SynsetName/small room in which a monk or nun lives/SynsetDefinition//Synset/cell.n.07/SynsetName/a room where a prisoner is kept/SynsetDefinition//Synset/",
            "treatment/TermName/treatment.n.01/TermSense/treatment.n.01/SynsetName/care provided to improve a situation (especially medical procedures or applications that are intended to relieve illness or injury)/SynsetDefinition//Synset/treatment.n.02/SynsetName/the management of someone or something/SynsetDefinition//Synset/treatment.n.03/SynsetName/a manner of dealing with something artistically/SynsetDefinition//Synset/discussion.n.01/SynsetName/an extended communication (often interactive) dealing with some particular topic/SynsetDefinition//Synset/",
            "human/TermName/homo.n.02/TermSense/homo.n.02/SynsetName/any living or extinct member of the family Hominidae characterized by superior intelligence, articulate speech, and erect carriage/SynsetDefinition//Synset/",
            "growth/TermName/growth.n.06/TermSense/growth.n.01/SynsetName/(biology) the process of an individual organism growing organically; a purely biological unfolding of events involved in an organism changing gradually from a simple to a more complex level/SynsetDefinition//Synset/growth.n.02/SynsetName/a progression from simpler to more complex forms/SynsetDefinition//Synset/increase.n.03/SynsetName/a process of becoming larger or longer or more numerous or more important/SynsetDefinition//Synset/growth.n.04/SynsetName/vegetation that has grown/SynsetDefinition//Synset/emergence.n.01/SynsetName/the gradual beginning or coming forth/SynsetDefinition//Synset/growth.n.06/SynsetName/(pathology) an abnormal proliferation of tissue (as in a tumor)/SynsetDefinition//Synset/growth.n.07/SynsetName/something grown or growing/SynsetDefinition//Synset/",
            "breast cancer/TermName/breast_cancer.n.01/TermSense/breast_cancer.n.01/SynsetName/cancer of the breast; one of the most common malignancies in women in the US/SynsetDefinition//Synset/",
            "cancer cell/TermName/cancer_cell.n.01/TermSense/cancer_cell.n.01/SynsetName/a cell that is part of a malignant tumor/SynsetDefinition//Synset/"
        };
        
        for(String term : terms){
            Terms.add(new Term(term));
        }
        
        WatsonDocument d1 = new WatsonDocument("http://morpheus.cs.umbc.edu/aks1/ontosem.owl");
        d1.setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/4/db7/2570/89ba1/4bae897f6f/de51920d47c64815b");
        d1.setCoveredTerms(Arrays.asList(new String[]{"breast","cancer","tumor","protein","cell","human"}));
        d1.setFile("ontosem.owl");
        
        WatsonDocument d2 = new WatsonDocument("http://www.berkeleybop.org/ontologies/obo-all/mesh/mesh.owl");
        d2.setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/a/db8/1942/acd39/9d8bcb607b/ec4d271305352e6cd");
        d2.setCoveredTerms(Arrays.asList(new String[]{"breast","protein","cell","growth"}));
        d2.setFile("mesh.owl");
        
        WatsonDocument d3 = new WatsonDocument("http://www.cyc.com/2003/04/01/cyc");
        d3.setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/f/48d/d2d6/47bf9/580b858398/2b74cd7b58b52e768");
        d3.setCoveredTerms(Arrays.asList(new String[]{"cancer","women","tumor","cell"}));
        d3.setFile("cyc.owl");
        
        WatsonDocument d4 = new WatsonDocument("http://ontologyportal.org/translations/SUMO.owl.txt");
        d4.setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/0/339/c2ff/21d76/1013cd189c/557c6d296bdc6957c");
        d4.setCoveredTerms(Arrays.asList(new String[]{"protein","cell","human","growth"}));
        d4.setFile("SUMO.owl.txt");
        
        WatsonDocument d5 = new WatsonDocument("http://www.berkeleybop.org/ontologies/obo-all/event/event.owl");
        d5.setCacheDocumentURL();
        d5.setCoveredTerms(Arrays.asList(new String[]{"treatment","growth","gene_expression"}));
        d5.setFile("event.owl");
        
        
        
        Documents.add(d1);
        Documents.add(d2);
        Documents.add(d3);
        Documents.add(d4);
        Documents.add(d5);
    }
    
    
    public static void main1(String[] args) throws RemoteException, OWLOntologyCreationException{
        System.out.println((int) (Math.random() * 239));
  
        
        //System.setProperty("wordnet.database.dir", "C:\\Program Files (x86)\\WordNet\\3.0\\dict");
        //WordNetDatabase wn = WordNetDatabase.getFileInstance();
//        WordNetDatabase wn = WordNet.getWordNetDatabase();
//        NounSynset nounSynset; 
//        NounSynset[] hyponyms; 
//        Synset[] synsets = wn.getSynsets("ontology", SynsetType.NOUN);
//        for (int i = 0; i < synsets.length; i++) {
//            nounSynset = (NounSynset) (synsets[i]);
//            
//            hyponyms = nounSynset.getHyponyms();
//            
//            System.out.println(nounSynset.getWordForms()[0]
//                    + ": " + nounSynset.getDefinition() + ") has " + hyponyms.length + " hyponyms");
//        }
        
        //        String[] Terms = {"breast","protein","cell","cancer"};
//        SearchTerms query = new SearchTerms(Terms);
//        for(WatsonDocument d : query.Documents)
//            new OwlApiModularization(d,"Module_"+query.Documents.indexOf(d) +".owl").getModule();
//        System.out.println("# "+ query.Documents.size());
//        OntologyMapper OMapper = new OntologyMapper(query.Documents);
        
    }
     
        
}
