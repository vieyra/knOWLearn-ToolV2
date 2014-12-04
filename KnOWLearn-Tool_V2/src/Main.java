
import DomainOntology.DomainOntology;
import InitialScheme.InitialScheme;
import InitialScheme.Mapping.SynsetOntologyAxioms;
import InitialScheme.Sources.OntologySources.Axioms.Axiom;
import InitialScheme.Sources.OntologySources.Axioms.AxiomType;
import InitialScheme.Sources.OntologySources.Axioms.TwoElementsAxiom;
import InitialScheme.Sources.OntologySources.OntologyConcept;
import InitialScheme.Sources.OntologySources.OntologyManager;
import InitialScheme.Sources.Term;
import InitialScheme.Sources.WatsonDocument;
import edu.smu.tspell.wordnet.NounSynset;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyMerger;

public class Main {

   public static void main(String[] args) throws IOException, InterruptedException {
      int port = 10914;
      while (true) {
         Process process = Runtime.getRuntime().exec("python ConnectionManager.py " + port);

         BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
//         BufferedReader brError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

//         System.out.println("Here is the standard output of the command:\n");
         String s = null;
         System.out.println("Trying connection in port " + port);
         s = br.readLine();
         Thread.sleep(10000);
         if (s != null) {
            System.out.println("Connection started in port " + port);
            break;
         }
         port += 5;
         // read any errors from the attempted command
//         System.out.println("Here is the standard error of the command (if any):\n");
//         while ((s = brError.readLine()) != null) {
//            System.out.println(s);
//         }
      }
      
//      process.destroy();
      //      InputStream inputstream = process.getInputStream();
      //      BufferedInputStream bis = new BufferedInputStream(inputstream);

   }
   private static int MIN_PORT_NUMBER = 9050;
   private static int MAX_PORT_NUMBER = 19050;
   
   public static boolean available(int port) {
      if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
         throw new IllegalArgumentException("Invalid start port: " + port);
      }

      ServerSocket ss = null;
      DatagramSocket ds = null;
      try {
         ss = new ServerSocket(port);
         ss.setReuseAddress(true);
         ds = new DatagramSocket(port);
         ds.setReuseAddress(true);
         return true;
      } catch (IOException e) {
      } finally {
         if (ds != null) {
            ds.close();
         }

         if (ss != null) {
            try {
               ss.close();
            } catch (IOException e) {
               /* should not be thrown */
            }
         }
      }

      return false;
   }
   
   
    public static void main0(String[] args) throws RemoteException, OWLOntologyCreationException, IOException {
        String[] terms = new String[]{
            "patient/TermName/patient.n.01/TermSense/patient.n.01/SynsetName/a person who requires medical care/SynsetDefinition//Synset/affected_role.n.01/SynsetName/the semantic role of an entity that is not the agent but is directly involved in or affected by the happening denoted by the verb in the clause/SynsetDefinition//Synset/",
            "breast/TermName/breast.n.02/TermSense/breast.n.01/SynsetName/the front of the trunk from the neck to the abdomen/SynsetDefinition//Synset/breast.n.02/SynsetName/either of two soft fleshy milk-secreting glandular organs on the chest of a woman/SynsetDefinition//Synset/breast.n.03/SynsetName/meat carved from the breast of a fowl/SynsetDefinition//Synset/breast.n.04/SynsetName/the part of an animal's body that corresponds to a person's chest/SynsetDefinition//Synset/",
            "cancer/TermName/cancer.n.01/TermSense/cancer.n.01/SynsetName/any malignant growth or tumor caused by abnormal and uncontrolled cell division; it may spread to other parts of the body through the lymphatic system or the blood stream/SynsetDefinition//Synset/cancer.n.02/SynsetName/(astrology) a person who is born while the sun is in Cancer/SynsetDefinition//Synset/cancer.n.03/SynsetName/a small zodiacal constellation in the northern hemisphere; between Leo and Gemini/SynsetDefinition//Synset/cancer.n.04/SynsetName/the fourth sign of the zodiac; the sun is in this sign from about June 21 to July 22/SynsetDefinition//Synset/cancer.n.05/SynsetName/type genus of the family Cancridae/SynsetDefinition//Synset/",
            "women/TermName/woman.n.01/TermSense/woman.n.01/SynsetName/an adult female person (as opposed to a man)/SynsetDefinition//Synset/woman.n.02/SynsetName/a female person who plays a significant role (wife or mistress or girlfriend) in the life of a particular man/SynsetDefinition//Synset/charwoman.n.01/SynsetName/a human female employed to do housework/SynsetDefinition//Synset/womanhood.n.02/SynsetName/women as a class/SynsetDefinition//Synset/",
            "tumor/TermName/tumor.n.01/TermSense/tumor.n.01/SynsetName/an abnormal new mass of tissue that serves no purpose/SynsetDefinition//Synset/",
            "expression/TermName/expression.n.07/TermSense/expression.n.01/SynsetName/the feelings expressed on a person's face/SynsetDefinition//Synset/expression.n.02/SynsetName/expression without words/SynsetDefinition//Synset/expression.n.03/SynsetName/the communication (in speech or writing) of your beliefs or opinions/SynsetDefinition//Synset/saying.n.01/SynsetName/a word or phrase that particular people use in particular situations/SynsetDefinition//Synset/formulation.n.03/SynsetName/the style of expressing yourself/SynsetDefinition//Synset/formula.n.01/SynsetName/a group of symbols that make a mathematical statement/SynsetDefinition//Synset/expression.n.07/SynsetName/(genetics) the process of expressing a gene/SynsetDefinition//Synset/construction.n.02/SynsetName/a group of words that form a constituent of a sentence and are considered as a single unit/SynsetDefinition//Synset/expression.n.09/SynsetName/the act of forcing something out by squeezing or pressing/SynsetDefinition//Synset/",
            "protein/TermName/protein.n.01/TermSense/protein.n.01/SynsetName/any of a large group of nitrogenous organic compounds that are essential constituents of living cells; consist of polymers of amino acids; essential in the diet of animals for growth and for repair of tissues; can be obtained from meat and eggs and milk and legumes/SynsetDefinition//Synset/",
            "cell/TermName/cell.n.02/TermSense/cell.n.01/SynsetName/any small compartment/SynsetDefinition//Synset/cell.n.02/SynsetName/(biology) the basic structural and functional unit of all organisms; they may exist as independent units of life (as in monads) or may form colonies or tissues as in higher plants and animals/SynsetDefinition//Synset/cell.n.03/SynsetName/a device that delivers an electric current as the result of a chemical reaction/SynsetDefinition//Synset/cell.n.04/SynsetName/a small unit serving as part of or as the nucleus of a larger political movement/SynsetDefinition//Synset/cellular_telephone.n.01/SynsetName/a hand-held mobile radiotelephone for use in an area divided into small sections, each with its own short-range transmitter/receiver/SynsetDefinition//Synset/cell.n.06/SynsetName/small room in which a monk or nun lives/SynsetDefinition//Synset/cell.n.07/SynsetName/a room where a prisoner is kept/SynsetDefinition//Synset/",
            "study/TermName/survey.n.01/TermSense/survey.n.01/SynsetName/a detailed critical inspection/SynsetDefinition//Synset/study.n.02/SynsetName/applying the mind to learning and understanding a subject (especially by reading)/SynsetDefinition//Synset/report.n.01/SynsetName/a written document describing the findings of some individual or group/SynsetDefinition//Synset/study.n.04/SynsetName/a state of deep mental absorption/SynsetDefinition//Synset/study.n.05/SynsetName/a room used for reading and writing and studying/SynsetDefinition//Synset/discipline.n.01/SynsetName/a branch of knowledge/SynsetDefinition//Synset/sketch.n.01/SynsetName/preliminary drawing for later elaboration/SynsetDefinition//Synset/cogitation.n.02/SynsetName/attentive consideration and meditation/SynsetDefinition//Synset/study.n.09/SynsetName/someone who memorizes quickly and easily (as the lines for a part in a play)/SynsetDefinition//Synset/study.n.10/SynsetName/a composition intended to develop one aspect of the performer's technique/SynsetDefinition//Synset/",
            "treatment/TermName/treatment.n.01/TermSense/treatment.n.01/SynsetName/care provided to improve a situation (especially medical procedures or applications that are intended to relieve illness or injury)/SynsetDefinition//Synset/treatment.n.02/SynsetName/the management of someone or something/SynsetDefinition//Synset/treatment.n.03/SynsetName/a manner of dealing with something artistically/SynsetDefinition//Synset/discussion.n.01/SynsetName/an extended communication (often interactive) dealing with some particular topic/SynsetDefinition//Synset/",
            "human/TermName/homo.n.02/TermSense/homo.n.02/SynsetName/any living or extinct member of the family Hominidae characterized by superior intelligence, articulate speech, and erect carriage/SynsetDefinition//Synset/",
            "growth/TermName/growth.n.06/TermSense/growth.n.01/SynsetName/(biology) the process of an individual organism growing organically; a purely biological unfolding of events involved in an organism changing gradually from a simple to a more complex level/SynsetDefinition//Synset/growth.n.02/SynsetName/a progression from simpler to more complex forms/SynsetDefinition//Synset/increase.n.03/SynsetName/a process of becoming larger or longer or more numerous or more important/SynsetDefinition//Synset/growth.n.04/SynsetName/vegetation that has grown/SynsetDefinition//Synset/emergence.n.01/SynsetName/the gradual beginning or coming forth/SynsetDefinition//Synset/growth.n.06/SynsetName/(pathology) an abnormal proliferation of tissue (as in a tumor)/SynsetDefinition//Synset/growth.n.07/SynsetName/something grown or growing/SynsetDefinition//Synset/",
            "breast cancer/TermName/breast_cancer.n.01/TermSense/breast_cancer.n.01/SynsetName/cancer of the breast; one of the most common malignancies in women in the US/SynsetDefinition//Synset/",
            "breast cancer cell/TermName/None/TermSense/",
            "cell line/TermName/None/TermSense/",
            "cell growth/TermName/None/TermSense/",
            "growth factor/TermName/growth_factor.n.01/TermSense/growth_factor.n.01/SynsetName/a protein that is involved in cell differentiation and growth/SynsetDefinition//Synset/",
            "cell proliferation/TermName/None/TermSense/",
            "cancer cell line/TermName/None/TermSense/",
            "prostate cancer/TermName/prostate_cancer.n.01/TermSense/prostate_cancer.n.01/SynsetName/cancer of the prostate gland/SynsetDefinition//Synset/",
            "gene expression/TermName/gene_expression.n.01/TermSense/gene_expression.n.01/SynsetName/conversion of the information encoded in a gene first into messenger RNA and then to a protein/SynsetDefinition//Synset/"
        };
        List<Term> Terms = new ArrayList<Term>();
        for (String term : terms) {
            Terms.add(new Term(term));
        }
                //new InitialScheme(Terms);
        int NumberOfDocuments = 4;
        WatsonDocument[] Documents = new WatsonDocument[NumberOfDocuments];

        Documents[0] = new WatsonDocument("http://morpheus.cs.umbc.edu/aks1/ontosem.owl");
        Documents[0].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/4/db7/2570/89ba1/4bae897f6f/de51920d47c64815b");
        Documents[0].setCoveredTerms(Arrays.asList(new String[]{"breast", "cancer", "tumor", "protein", "cell", "study", "human"}));
        Documents[0].setFile("ontosem.owl");

        Documents[1] = new WatsonDocument("http://www.berkeleybop.org/ontologies/obo-all/mesh/mesh.owl");
        Documents[1].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/a/db8/1942/acd39/9d8bcb607b/ec4d271305352e6cd");
        Documents[1].setCoveredTerms(Arrays.asList(new String[]{"breast", "protein", "cell", "growth", "gene_expression"}));
      Documents[1].setFile("mesh.owl");

      Documents[2] = new WatsonDocument("http://purl.org/obo/owl/evoc");
      Documents[2].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/9/69d/e693/7b130/ff987a7ac2/5353e0485200e1680");
      Documents[2].setCoveredTerms(Arrays.asList(new String[]{"breast", "treatment", "growth_factor"}));
      Documents[2].setFile("evoc.owl");

      Documents[3] = new WatsonDocument("http://www.cyc.com/2003/04/01/cyc");
      Documents[3].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/f/48d/d2d6/47bf9/580b858398/2b74cd7b58b52e768");
      Documents[3].setCoveredTerms(Arrays.asList(new String[]{"cancer", "women", "tumor", "cell"}));
      Documents[3].setFile("cyc.owl");

//        Documents[4] = new WatsonDocument("http://139.91.183.30:9090/RDF/VRP/Examples/tap.rdf");
//        Documents[4].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/e/aa9/02c9/cd6d9/318c843642/9e583ebfa89145d54");
//        Documents[4].setCoveredTerms(Arrays.asList(new String[]{"cancer", "breast_cancer", "prostate_cancer"}));
//        Documents[4].setFile("tap.rdf");
//
//        Documents[5] = new WatsonDocument("http://ontologyportal.org/translations/SUMO.owl.txt");
//        Documents[5].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/0/339/c2ff/21d76/1013cd189c/557c6d296bdc6957c");
//        Documents[5].setCoveredTerms(Arrays.asList(new String[]{"protein", "cell", "human", "growth"}));
//        Documents[5].setFile("SUMO.owl.txt");
//
//        Documents[6] = new WatsonDocument("http://www.berkeleybop.org/ontologies/obo-all/event/event.owl");
//        Documents[6].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/3/704/1bb0/599fb/8dd0f1a12c/e50ad0662b6d49567");
//        Documents[6].setCoveredTerms(Arrays.asList(new String[]{"treatment", "growth", "gene_expression"}));
//        Documents[6].setFile("event.owl");

      InitialScheme IS = new InitialScheme(Terms, Arrays.asList(Documents));
      new DomainOntology(IS.getAxioms());
//        System.out.println("Getting axioms");
//        getAxioms(Terms.toArray(new Term[]{}), Arrays.asList(Documents));
//        System.out.println("\nDeleting repeated axioms");
//        deleteRepeatedAxioms(Terms);
//        System.out.println("Printing relevant Axioms");
//        printRelevantAxioms(Terms);
//        System.out.println("Printed relevant Axioms");
   }

   public static void main5(String[] args) throws RemoteException, OWLOntologyCreationException, IOException {
      String[] terms = new String[]{
         "breast/TermName/breast.n.01/TermSense/breast.n.01/SynsetName/the front of the trunk from the neck to the abdomen/SynsetDefinition//Synset/breast.n.02/SynsetName/either of two soft fleshy milk-secreting glandular organs on the chest of a woman/SynsetDefinition//Synset/breast.n.03/SynsetName/meat carved from the breast of a fowl/SynsetDefinition//Synset/breast.n.04/SynsetName/the part of an animal's body that corresponds to a person's chest/SynsetDefinition//Synset//Term/"
         + "cancer/TermName/cancer.n.01/TermSense/cancer.n.01/SynsetName/any malignant growth or tumor caused by abnormal and uncontrolled cell division; it may spread to other parts of the body through the lymphatic system or the blood stream/SynsetDefinition//Synset/cancer.n.02/SynsetName/(astrology) a person who is born while the sun is in Cancer/SynsetDefinition//Synset/cancer.n.03/SynsetName/a small zodiacal constellation in the northern hemisphere; between Leo and Gemini/SynsetDefinition//Synset/cancer.n.04/SynsetName/the fourth sign of the zodiac; the sun is in this sign from about June 21 to July 22/SynsetDefinition//Synset/cancer.n.05/SynsetName/type genus of the family Cancridae/SynsetDefinition//Synset//Term/"
         + "diagnosis/TermName/diagnosis.n.01/TermSense/diagnosis.n.01/SynsetName/identifying the nature or cause of some phenomenon/SynsetDefinition//Synset//Term/"
         + "patient/TermName/patient.n.01/TermSense/patient.n.01/SynsetName/a person who requires medical care/SynsetDefinition//Synset/affected_role.n.01/SynsetName/the semantic role of an entity that is not the agent but is directly involved in or affected by the happening denoted by the verb in the clause/SynsetDefinition//Synset//Term/"
         + "study/TermName/study.n.09/TermSense/survey.n.01/SynsetName/a detailed critical inspection/SynsetDefinition//Synset/study.n.02/SynsetName/applying the mind to learning and understanding a subject (especially by reading)/SynsetDefinition//Synset/report.n.01/SynsetName/a written document describing the findings of some individual or group/SynsetDefinition//Synset/study.n.04/SynsetName/a state of deep mental absorption/SynsetDefinition//Synset/study.n.05/SynsetName/a room used for reading and writing and studying/SynsetDefinition//Synset/discipline.n.01/SynsetName/a branch of knowledge/SynsetDefinition//Synset/sketch.n.01/SynsetName/preliminary drawing for later elaboration/SynsetDefinition//Synset/cogitation.n.02/SynsetName/attentive consideration and meditation/SynsetDefinition//Synset/study.n.09/SynsetName/someone who memorizes quickly and easily (as the lines for a part in a play)/SynsetDefinition//Synset/study.n.10/SynsetName/a composition intended to develop one aspect of the performer's technique/SynsetDefinition//Synset//Term/"
         + "cell/TermName/cell.n.02/TermSense/cell.n.01/SynsetName/any small compartment/SynsetDefinition//Synset/cell.n.02/SynsetName/(biology) the basic structural and functional unit of all organisms; they may exist as independent units of life (as in monads) or may form colonies or tissues as in higher plants and animals/SynsetDefinition//Synset/cell.n.03/SynsetName/a device that delivers an electric current as the result of a chemical reaction/SynsetDefinition//Synset/cell.n.04/SynsetName/a small unit serving as part of or as the nucleus of a larger political movement/SynsetDefinition//Synset/cellular_telephone.n.01/SynsetName/a hand-held mobile radiotelephone for use in an area divided into small sections, each with its own short-range transmitter/receiver/SynsetDefinition//Synset/cell.n.06/SynsetName/small room in which a monk or nun lives/SynsetDefinition//Synset/cell.n.07/SynsetName/a room where a prisoner is kept/SynsetDefinition//Synset//Term/"
         + "expression/TermName/expression.n.07/TermSense/expression.n.01/SynsetName/the feelings expressed on a person's face/SynsetDefinition//Synset/expression.n.02/SynsetName/expression without words/SynsetDefinition//Synset/expression.n.03/SynsetName/the communication (in speech or writing) of your beliefs or opinions/SynsetDefinition//Synset/saying.n.01/SynsetName/a word or phrase that particular people use in particular situations/SynsetDefinition//Synset/formulation.n.03/SynsetName/the style of expressing yourself/SynsetDefinition//Synset/formula.n.01/SynsetName/a group of symbols that make a mathematical statement/SynsetDefinition//Synset/expression.n.07/SynsetName/(genetics) the process of expressing a gene/SynsetDefinition//Synset/construction.n.02/SynsetName/a group of words that form a constituent of a sentence and are considered as a single unit/SynsetDefinition//Synset/expression.n.09/SynsetName/the act of forcing something out by squeezing or pressing/SynsetDefinition//Synset//Term/"
         + "tumor/TermName/tumor.n.01/TermSense/tumor.n.01/SynsetName/an abnormal new mass of tissue that serves no purpose/SynsetDefinition//Synset//Term/"
         + "role/TermName/character.n.04/TermSense/function.n.03/SynsetName/the actions and activities assigned to or required or expected of a person or group/SynsetDefinition//Synset/character.n.04/SynsetName/an actor's portrayal of someone in a play/SynsetDefinition//Synset/function.n.02/SynsetName/what something is used for/SynsetDefinition//Synset/role.n.04/SynsetName/normal or customary activity of a person in a particular social setting/SynsetDefinition//Synset//Term/"
         + "protein/TermName/protein.n.01/TermSense/protein.n.01/SynsetName/any of a large group of nitrogenous organic compounds that are essential constituents of living cells; consist of polymers of amino acids; essential in the diet of animals for growth and for repair of tissues; can be obtained from meat and eggs and milk and legumes/SynsetDefinition//Synset//Term/"
         + "survival/TermName/None/TermSense/survival.n.01/SynsetName/a state of surviving; remaining alive/SynsetDefinition//Synset/survival.n.02/SynsetName/a natural process resulting in the evolution of organisms best adapted to the environment/SynsetDefinition//Synset/survival.n.03/SynsetName/something that survives/SynsetDefinition//Synset//Term/"
         + "carcinoma/TermName/carcinoma.n.01/TermSense/carcinoma.n.01/SynsetName/any malignant tumor derived from epithelial tissue; one of the four major types of cancer/SynsetDefinition//Synset//Term/"
         + "risk/TermName/risk.n.03/TermSense/hazard.n.01/SynsetName/a source of danger; a possibility of incurring loss or misfortune/SynsetDefinition//Synset/risk.n.02/SynsetName/a venture undertaken without regard to possible loss or injury/SynsetDefinition//Synset/risk.n.03/SynsetName/the probability of becoming infected given that exposure to an infectious agent has occurred/SynsetDefinition//Synset/risk.n.04/SynsetName/the probability of being exposed to an infectious agent/SynsetDefinition//Synset//Term/"
         + "treatment/TermName/treatment.n.02/TermSense/treatment.n.01/SynsetName/care provided to improve a situation (especially medical procedures or applications that are intended to relieve illness or injury)/SynsetDefinition//Synset/treatment.n.02/SynsetName/the management of someone or something/SynsetDefinition//Synset/treatment.n.03/SynsetName/a manner of dealing with something artistically/SynsetDefinition//Synset/discussion.n.01/SynsetName/an extended communication (often interactive) dealing with some particular topic/SynsetDefinition//Synset//Term/"
         + "case/TermName/font.n.01/TermSense/case.n.01/SynsetName/an occurrence of something/SynsetDefinition//Synset/event.n.02/SynsetName/a special set of circumstances/SynsetDefinition//Synset/lawsuit.n.01/SynsetName/a comprehensive term for any proceeding in a court of law whereby an individual seeks a legal remedy/SynsetDefinition//Synset/case.n.04/SynsetName/the actual state of things/SynsetDefinition//Synset/case.n.05/SynsetName/a portable container for carrying several objects/SynsetDefinition//Synset/case.n.06/SynsetName/a person requiring professional services/SynsetDefinition//Synset/subject.n.06/SynsetName/a person who is subjected to experimental or other observational procedures; someone who is an object of investigation/SynsetDefinition//Synset/case.n.08/SynsetName/a problem requiring investigation/SynsetDefinition//Synset/case.n.09/SynsetName/a statement of facts and reasons used to support an argument/SynsetDefinition//Synset/case.n.10/SynsetName/the quantity contained in a case/SynsetDefinition//Synset/case.n.11/SynsetName/nouns or pronouns or adjectives (often marked by inflection) related in some way to other words in a sentence/SynsetDefinition//Synset/case.n.12/SynsetName/a specific state of mind that is temporary/SynsetDefinition//Synset/character.n.05/SynsetName/a person of a specified kind (usually with many eccentricities)/SynsetDefinition//Synset/font.n.01/SynsetName/a specific size and style of type within a type family/SynsetDefinition//Synset/sheath.n.02/SynsetName/an enveloping structure or covering enclosing an animal or plant organ or part/SynsetDefinition//Synset/shell.n.08/SynsetName/the housing or outer covering of something/SynsetDefinition//Synset/casing.n.03/SynsetName/the enclosing frame around a door or window opening/SynsetDefinition//Synset/case.n.18/SynsetName/(printing) the receptacle in which a compositor has his type, which is divided into compartments for the different letters, spaces, or numbers/SynsetDefinition//Synset/case.n.19/SynsetName/bed linen consisting of a cover for a pillow/SynsetDefinition//Synset/case.n.20/SynsetName/a glass container used to store and display items in a shop or museum or home/SynsetDefinition//Synset//Term/"
         + "metastasis/TermName/metastasis.n.01/TermSense/metastasis.n.01/SynsetName/the spreading of a disease (especially cancer) to another part of the body/SynsetDefinition//Synset//Term/"
         + "positive/TermName/positive.n.01/TermSense/positive.n.01/SynsetName/the primary form of an adjective or adverb; denotes a quality without qualification, comparison, or relation to increase or diminution/SynsetDefinition//Synset/positive.n.02/SynsetName/a film showing a photographic image whose tones correspond to those of the original subject/SynsetDefinition//Synset//Term/"
         + "surgery/TermName/None/TermSense/surgery.n.01/SynsetName/the branch of medical science that treats disease or injury by operative procedures/SynsetDefinition//Synset/surgery.n.02/SynsetName/a room where a doctor or dentist can be consulted/SynsetDefinition//Synset/operating_room.n.01/SynsetName/a room in a hospital equipped for the performance of surgical operations/SynsetDefinition//Synset/operation.n.06/SynsetName/a medical procedure involving an incision with instruments; performed to repair damage or arrest disease in a living body/SynsetDefinition//Synset//Term/"
         + "receptor/TermName/receptor.n.01/TermSense/receptor.n.01/SynsetName/a cellular structure that is postulated to exist in order to mediate between a chemical agent that acts on nervous tissue and the physiological response/SynsetDefinition//Synset/sense_organ.n.01/SynsetName/an organ having nerve endings (in the skin or viscera or eye or ear or nose or mouth) that respond to stimulation/SynsetDefinition//Synset//Term/"
         + "growth/TermName/growth.n.06/TermSense/growth.n.01/SynsetName/(biology) the process of an individual organism growing organically; a purely biological unfolding of events involved in an organism changing gradually from a simple to a more complex level/SynsetDefinition//Synset/growth.n.02/SynsetName/a progression from simpler to more complex forms/SynsetDefinition//Synset/increase.n.03/SynsetName/a process of becoming larger or longer or more numerous or more important/SynsetDefinition//Synset/growth.n.04/SynsetName/vegetation that has grown/SynsetDefinition//Synset/emergence.n.01/SynsetName/the gradual beginning or coming forth/SynsetDefinition//Synset/growth.n.06/SynsetName/(pathology) an abnormal proliferation of tissue (as in a tumor)/SynsetDefinition//Synset/growth.n.07/SynsetName/something grown or growing/SynsetDefinition//Synset//Term/"
         + "gene/TermName/gene.n.01/TermSense/gene.n.01/SynsetName/(genetics) a segment of DNA that is involved in producing a polypeptide chain; it can include regions preceding and following the coding DNA as well as introns between the exons; it is considered a unit of heredity/SynsetDefinition//Synset//Term/"
         + "proliferation/TermName/proliferation.n.02/TermSense/proliferation.n.01/SynsetName/growth by the rapid multiplication of parts/SynsetDefinition//Synset/proliferation.n.02/SynsetName/a rapid increase in number (especially a rapid increase in the number of deadly weapons)/SynsetDefinition//Synset//Term/"
         + "analysis/TermName/analysis.n.04/TermSense/analysis.n.01/SynsetName/an investigation of the component parts of a whole and their relations in making up the whole/SynsetDefinition//Synset/analysis.n.02/SynsetName/the abstract separation of a whole into its constituent parts in order to study the parts and their relations/SynsetDefinition//Synset/analysis.n.03/SynsetName/a form of literary criticism in which the structure of a piece of writing is analyzed/SynsetDefinition//Synset/analysis.n.04/SynsetName/the use of closed-class words instead of inflections: e.g., `the father of the bride' instead of `the bride's father'/SynsetDefinition//Synset/analysis.n.05/SynsetName/a branch of mathematics involving calculus and the theory of limits; sequences and series and integration and differentiation/SynsetDefinition//Synset/psychoanalysis.n.01/SynsetName/a set of techniques for exploring underlying motives and a method of treating various mental disorders; based on the theories of Sigmund Freud/SynsetDefinition//Synset//Term/"
         + "therapy/TermName/therapy.n.01/TermSense/therapy.n.01/SynsetName/(medicine) the act of caring for someone (as by medication or remedial training etc.)/SynsetDefinition//Synset//Term/"
         + "breast cancer/TermName/breast_cancer.n.01/TermSense/breast_cancer.n.01/SynsetName/cancer of the breast; one of the most common malignancies in women in the US/SynsetDefinition//Synset//Term/"
         + "cancer patient/TermName/None/TermSense//Term/"
         + "cancer cell/TermName/cancer_cell.n.01/TermSense/cancer_cell.n.01/SynsetName/a cell that is part of a malignant tumor/SynsetDefinition//Synset//Term/"
         + "cell line/TermName/None/TermSense//Term/"
         + "breast cancer patient/TermName/None/TermSense//Term/"
         + "breast cancer cell/TermName/None/TermSense//Term/"
         + "breast carcinoma/TermName/None/TermSense//Term/"
         + "human breast/TermName/None/TermSense//Term/"
         + "breast tumor/TermName/None/TermSense//Term/"
         + "cell proliferation/TermName/None/TermSense//Term/"
         + "tumor cell/TermName/None/TermSense//Term/"
         + "cell growth/TermName/None/TermSense//Term/"
         + "growth factor/TermName/growth_factor.n.01/TermSense/growth_factor.n.01/SynsetName/a protein that is involved in cell differentiation and growth/SynsetDefinition//Synset//Term/"
      };
        List<Term> Terms = new ArrayList<Term>();
        for (String term : terms) {
            Terms.add(new Term(term));
        }
        //new InitialScheme(Terms);
        
	int NumberOfDocuments = 20;
	WatsonDocument[] Documents = new WatsonDocument[NumberOfDocuments];

	Documents[0] = new WatsonDocument("http://morpheus.cs.umbc.edu/aks1/ontosem.owl");
	Documents[0].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/4/db7/2570/89ba1/4bae897f6f/de51920d47c64815b");
	Documents[0].setCoveredTerms(Arrays.asList(new String[]{"breast","cancer","tumor","cell","protein","human","metastasis","receptor","gene"}));	
        Documents[0].setFile("ontosem.owl");
        
	Documents[1] = new WatsonDocument("http://www.berkeleybop.org/ontologies/obo-all/mesh/mesh.owl");
	Documents[1].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/a/db8/1942/acd39/9d8bcb607b/ec4d271305352e6cd");
	Documents[1].setCoveredTerms(Arrays.asList(new String[]{"breast","diagnosis","cell","protein","growth","gene"}));
        Documents[1].setFile("mesh.owl");
        
	Documents[2] = new WatsonDocument("http://secse.atosorigin.es:10000/ontologies/cyc.owl");
	Documents[2].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/5/4b6/466d/b79a5/f1f2298c82/d30800bfaeeff211a");
	Documents[2].setCoveredTerms(Arrays.asList(new String[]{"breast","cell","estrogen"}));
        Documents[2].setFile("cyc.owl");
        
	Documents[3] = new WatsonDocument("http://purl.org/obo/owl/evoc");
	Documents[3].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/9/69d/e693/7b130/ff987a7ac2/5353e0485200e1680");
	Documents[3].setCoveredTerms(Arrays.asList(new String[]{"breast","carcinoma","treatment","cell_line"}));
        Documents[3].setFile("evoc.owl");
        
	Documents[4] = new WatsonDocument("http://purl.org/obo/owl/brenda");
	Documents[4].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/5/fd9/416d/0130a/f70011fd15/f38a7d9614e0fbb88");
	Documents[4].setCoveredTerms(Arrays.asList(new String[]{"breast","breast_cancer_cell","mcf-7_cell"}));
        Documents[4].setFile("brenda.owl");
        
	Documents[5] = new WatsonDocument("http://www.cyc.com/2003/04/01/cyc");
	Documents[5].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/f/48d/d2d6/47bf9/580b858398/2b74cd7b58b52e768");
	Documents[5].setCoveredTerms(Arrays.asList(new String[]{"cancer","women","tumor","cell","surgery"}));
        Documents[5].setFile("cyc.owl");
        
	Documents[6] = new WatsonDocument("http://139.91.183.30:9090/RDF/VRP/Examples/tap.rdf");
	Documents[6].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/e/aa9/02c9/cd6d9/318c843642/9e583ebfa89145d54");
	Documents[6].setCoveredTerms(Arrays.asList(new String[]{"cancer","surgery","breast_cancer"}));
        Documents[6].setFile("tap.rdf");
        
	Documents[7] = new WatsonDocument("http://watson.kmi.open.ac.uk/ontologies/LT4eL/CSnCSv0.01Lex.owl");
	Documents[7].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/3/e26/40cd/82b39/009154df36/c1ad5bcefe9c8107b");
	Documents[7].setCoveredTerms(Arrays.asList(new String[]{"diagnosis","cell","human"}));
        Documents[7].setFile("CSnCSv0.01Lex.owl");
        
	Documents[8] = new WatsonDocument("http://purl.org/obo/owl/OBO_REL");
	Documents[8].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/8/e5c/b0b7/09355/b3f8fdfa49/ac15b33d84399fedc");
	Documents[8].setCoveredTerms(Arrays.asList(new String[]{"diagnosis","cell","protein","cell_line"}));
        Documents[8].setFile("OBO_REL.owl");
        
	Documents[9] = new WatsonDocument("http://www.co-ode.org/ontologies/galen");
	Documents[9].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/e/e52/ec24/dccee/048db02bf1/654d0642dd2630ae4");
	Documents[9].setCoveredTerms(Arrays.asList(new String[]{"diagnosis","cell","chemotherapy","surgery"}));	
        Documents[9].setFile("galen.owl");
        
	Documents[10] = new WatsonDocument("http://swpatho.ag-nbi.de/owldata/umlssn.owl");
	Documents[10].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/f/594/6dcc/c1477/537477a9dc/02a8eabf3addaca3c");
	Documents[10].setCoveredTerms(Arrays.asList(new String[]{"cell","protein","receptor","gene"}));	
        Documents[10].setFile("umlssn.owl");
        
	Documents[11] = new WatsonDocument("http://nlp.postech.ac.kr/Research/POSBIOTM/content/POSBIOTM_data/POSBIOTMontology.daml");
	Documents[11].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/4/07c/234d/e645f/331d0c24b4/deb9ceeb9d7792804");
	Documents[11].setCoveredTerms(Arrays.asList(new String[]{"cell","protein","gene"}));	
        Documents[11].setFile("POSBIOTMontology.daml");
        
	Documents[12] = new WatsonDocument("http://www.w3.org/2000/10/swap/test/pathway/pathway.rdf");
	Documents[12].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/7/b60/a487/18dcf/b3f943bf9d/17c27f37326803959");
	Documents[12].setCoveredTerms(Arrays.asList(new String[]{"cell","protein","receptor","gene"}));	
        Documents[12].setFile("pathway.rdf");
        
	Documents[13] = new WatsonDocument("http://www.w3.org/2000/10/swap/test/tambis/tambis");
	Documents[13].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/c/efa/37c0/ab39a/3b3a28bdc9/aa6b8751517e8ece2");
	Documents[13].setCoveredTerms(Arrays.asList(new String[]{"cell","protein","receptor","gene"}));	
        Documents[13].setFile("tambis.owl");
        
	Documents[14] = new WatsonDocument("http://ontologyportal.org/translations/SUMO.owl.txt");
	Documents[14].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/0/339/c2ff/21d76/1013cd189c/557c6d296bdc6957c");
	Documents[14].setCoveredTerms(Arrays.asList(new String[]{"cell","protein","human","surgery","growth"}));	
        Documents[14].setFile("SUMO.owl.txt");
        
	Documents[15] = new WatsonDocument("http://onto.cs.yale.edu:8080/umls/UMLSinDAML/NET/SRSTR.daml");
	Documents[15].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/6/106/c76c/85aec/4dc85591e5/1bbab684687972fc5");
	Documents[15].setCoveredTerms(Arrays.asList(new String[]{"cell","human","receptor"}));	
        Documents[15].setFile("SRSTR.daml");
        
	Documents[16] = new WatsonDocument("http://www.phinformatics.org/Assets/Ontology/umls.owl");
	Documents[16].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/b/034/3207/d9169/8fd58e6f17/ad2a3619df6db6387");
	Documents[16].setCoveredTerms(Arrays.asList(new String[]{"cell","human","receptor"}));	
        Documents[16].setFile("umls.owl");
        
	Documents[17] = new WatsonDocument("http://mensa.sl.iupui.edu/ontology/BiologicalOntology.owl");
	Documents[17].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/b/0dc/e342/56767/46ed689b01/ae4ce7a402cc70ab7");
	Documents[17].setCoveredTerms(Arrays.asList(new String[]{"expression","protein","gene"}));	
        Documents[17].setFile("BiologicalOntology.owl");
        
	Documents[18] = new WatsonDocument("http://www.ebi.ac.uk/sbo/docs/exports/SBO_OWL.owl");
	Documents[18].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/f/bc0/6b93/4b2b1/5a7906320b/d3f7ab882a2c94c83");
	Documents[18].setCoveredTerms(Arrays.asList(new String[]{"protein","receptor","gene"}));	
        Documents[18].setFile("SBO_OWL.owl");
        
	Documents[19] = new WatsonDocument("http://www.berkeleybop.org/ontologies/obo-all/event/event.owl");
	Documents[19].setCacheDocumentURL("http://kmi-web05.open.ac.uk:81/cache/3/704/1bb0/599fb/8dd0f1a12c/e50ad0662b6d49567");
	Documents[19].setCoveredTerms(Arrays.asList(new String[]{"treatment","growth","cell_proliferation"}));	
        Documents[19].setFile("event.owl");
        
        InitialScheme IS = new InitialScheme(Terms, Arrays.asList(Documents));
        new DomainOntology(IS.getAxioms());
//        System.out.println("Getting axioms");
//        getAxioms(Terms.toArray(new Term[]{}), Arrays.asList(Documents));
//        System.out.println("\nDeleting repeated axioms");
//        deleteRepeatedAxioms(Terms);
//        System.out.println("Printing relevant Axioms");
//        printRelevantAxioms(Terms);
//        System.out.println("Printed relevant Axioms");
    }

    private static void deleteRepeatedAxioms(List<Term> Terms) {
        for (Term term : Terms) {
            List<Axiom> toDelete = new ArrayList<Axiom>();
            for (int i = 0; i < term.SynsetOntologyAxioms.size(); i++) {
                if (term.SynsetOntologyAxioms.get(i).isTwoElementsAxiom() && !toDelete.contains(term.SynsetOntologyAxioms.get(i))) {
                    TwoElementsAxiom Axiomi = term.SynsetOntologyAxioms.get(i).asTwoElementsAxiom();
                    for (int j = i + 1; j < term.SynsetOntologyAxioms.size(); j++) {
                        if (term.SynsetOntologyAxioms.get(j).isTwoElementsAxiom()) {
                            TwoElementsAxiom Axiomj = term.SynsetOntologyAxioms.get(j).asTwoElementsAxiom();
                            if (Axiomi.getRelation().equals(Axiomj.getRelation())
                                    && Axiomi.getSubject().getID().equals(Axiomj.getSubject().getID())
                                    && Axiomi.getObject().getID().equals(Axiomj.getObject().getID())) {
                                toDelete.add(term.SynsetOntologyAxioms.get(j));
                            }
                        }
                    }
                }
            }
            term.SynsetOntologyAxioms.removeAll(toDelete);
        }
    }

    private static Object[] getElements(Term term) { //Object[0] = List<Synset> ; Object[1] = List<OWLClass>
        List<NounSynset> Synsets = new ArrayList<NounSynset>();
        List<OWLClass> Classes = new ArrayList<OWLClass>();
        for (Axiom axiom : term.SynsetOntologyAxioms) {
            if (axiom.getRelation().equals(AxiomType.Type)) {
                if (axiom.getSubject().isSynsetType()) {
                    NounSynset synset = axiom.getSubject().asAxiomElementSynset().getFirstElementMapped();
                    if (!Synsets.contains(synset)) {
                        Synsets.add(synset);
                    }
                } else {
                    OWLClass Class = axiom.getSubject().asAxiomElementClass().getFirstElementMapped();
                    if (!Classes.contains(Class)) {
                        Classes.add(Class);
                    }
                }
            }
        }
        return new Object[]{Synsets, Classes};
    }

    private static void printRelevantAxioms(List<Term> Terms) {
        for (Term term : Terms) {
            Object[] elements = getElements(term);
            List<NounSynset> Synsets = (List<NounSynset>) elements[0];
            List<OWLClass> Classes = (List<OWLClass>) elements[1];
//            List<NounSynset> Synsets = new ArrayList<NounSynset>();
//            List<OWLClass> Classes = new ArrayList<OWLClass>();
//            for (Axiom axiom : term.SynsetOntologyAxioms) {
//                if (axiom.getRelation().equals(AxiomType.Type)) {
//                    if (axiom.getSubject().isSynsetType()) {
//                        NounSynset synset = axiom.getSubject().asAxiomElementSynset().getFirstElementMapped();
//                        if (!Synsets.contains(synset)) {
//                            Synsets.add(synset);
//                        }
//                    } else {
//                        OWLClass Class = axiom.getSubject().asAxiomElementClass().getFirstElementMapped();
//                        if (!Classes.contains(Class)) {
//                            Classes.add(Class);
//                        }
//                    }
//                }
//            }
            System.out.println("Axioms for term: " + term.getName());
            for (NounSynset synset : Synsets) {
                System.out.println("\tAxioms for synset: " + synset.getWordForms()[0]);
                List<Axiom> PartOfAxioms = new ArrayList<Axiom>();
                List<Axiom> SubClassOfAxioms = new ArrayList<Axiom>();
                for (Axiom axiom : term.SynsetOntologyAxioms) {
                    if (axiom.isTwoElementsAxiom()) {
                        if (axiom.getSubject().isSynsetType()) {
                            if (axiom.getSubject().asAxiomElementSynset().getFirstElementMapped().equals(synset)) {
                                if (axiom.getRelation().equals(AxiomType.HasPart)) {
                                    PartOfAxioms.add(axiom);
                                }
                                if (axiom.getRelation().equals(AxiomType.SubClassOf)) {
                                    SubClassOfAxioms.add(axiom);
                                }
//                                TwoElementsAxiom axiom2 = axiom.asTwoElementsAxiom();
//                                System.out.println("\t\t" + axiom2.getSubject().getID() + "\t" + axiom2.getRelation() + "\t" + axiom2.getObject().getID());
                            }
                        }
                    }
                }
                for (Axiom axiom : PartOfAxioms) {
                    TwoElementsAxiom axiom2 = axiom.asTwoElementsAxiom();
                    System.out.println("\t\t" + axiom2.getSubject().getID() + "\t" + axiom2.getRelation() + "\t" + axiom2.getObject().getID());
                }
                if (SubClassOfAxioms.size() > 1) {
                    SubClassOfAxioms = removePartOfAxiomsofSubClassOfAxioms(SubClassOfAxioms, PartOfAxioms);
                    if (SubClassOfAxioms.size() > 1) {
                        for (Axiom axiom : SubClassOfAxioms) {
                            TwoElementsAxiom axiom2 = axiom.asTwoElementsAxiom();
                            System.out.println("\t\t" + axiom2.getSubject().getID() + "\t" + axiom2.getRelation() + "\t" + axiom2.getObject().getID());
                            List<Axiom> SuperClasses = new ArrayList<Axiom>();
                            System.out.print("*");
                            getSuperClasses(axiom, term.SynsetOntologyAxioms, SuperClasses);
                            for (Axiom SuperClass : SuperClasses) {
                                TwoElementsAxiom axiomSC = SuperClass.asTwoElementsAxiom();
                                System.out.println("\t\t" + axiomSC.getSubject().getID() + "\t" + axiomSC.getRelation() + "\t" + axiomSC.getObject().getID());
                            }
                        }
                    } else {
                        for (Axiom axiom : SubClassOfAxioms) {
                            TwoElementsAxiom axiom2 = axiom.asTwoElementsAxiom();
                            System.out.println("\t\t" + axiom2.getSubject().getID() + "\t" + axiom2.getRelation() + "\t" + axiom2.getObject().getID());
                        }
                    }

                } else {
                    for (Axiom axiom : SubClassOfAxioms) {
                        TwoElementsAxiom axiom2 = axiom.asTwoElementsAxiom();
                        System.out.println("\t\t" + axiom2.getSubject().getID() + "\t" + axiom2.getRelation() + "\t" + axiom2.getObject().getID());
                    }
                }
            }
            for (OWLClass Class : Classes) {
                System.out.println("\tAxioms for class: " + Class.getIRI());
                for (Axiom axiom : term.SynsetOntologyAxioms) {
                    if (axiom.isTwoElementsAxiom()) {
                        if (axiom.getSubject().isOWLClassType()) {
                            if (axiom.getSubject().asAxiomElementClass().getFirstElementMapped().equals(Class)) {
                                TwoElementsAxiom axiom2 = axiom.asTwoElementsAxiom();
                                System.out.println("\t\t" + axiom2.getSubject().getID() + "\t" + axiom2.getRelation() + "\t" + axiom2.getObject().getID());
                            }
                        }
                    }
                }
            }
        }
    }

    private static void getSuperClasses(Axiom axiom, List<Axiom> Axioms, List<Axiom> SuperClasses) {
        TwoElementsAxiom E2Axiom = axiom.asTwoElementsAxiom();
        for (Axiom ax : Axioms) {
            if (ax.isTwoElementsAxiom() && ax.getRelation().equals(AxiomType.SubClassOf)) {
                TwoElementsAxiom ax2 = ax.asTwoElementsAxiom();
                if (E2Axiom.getObject().getID().equals(ax2.getSubject().getID())) {
                    SuperClasses.add(ax);
                    System.out.println("\t\t\t"+ax.getSubject().getID());
                    getSuperClasses(ax, Axioms, SuperClasses);
                }
            }
        }
    }

    private static List<Axiom> removePartOfAxiomsofSubClassOfAxioms(List<Axiom> SubClassOfAxioms, List<Axiom> PartOfAxioms) {
        List<Axiom> toDelete = new ArrayList<Axiom>();
        for (Axiom Axiom : SubClassOfAxioms) {
            TwoElementsAxiom SubClassOfAxiom = Axiom.asTwoElementsAxiom();
            for (Axiom pAxiom : PartOfAxioms) {
                TwoElementsAxiom PartOfAxiom = pAxiom.asTwoElementsAxiom();
                if (PartOfAxiom.getSubject().getID().equals(SubClassOfAxiom.getSubject().getID())
                        && PartOfAxiom.getObject().getID().equals(SubClassOfAxiom.getObject().getID())) {
                    toDelete.add(Axiom);
                }
            }
        }
        SubClassOfAxioms.removeAll(toDelete);
        return SubClassOfAxioms;
    }

    private static void getAxioms(Term[] Terms, List<WatsonDocument> documents) {
        for (WatsonDocument document : documents) {
            try {
                OntologyManager Ontology = document.openOntology();
                System.out.print("\n("+(documents.indexOf(document)+1)+"/"+(documents.size())+")");
                if (Ontology == null) {
                    System.err.println("The ontology can't be opened");
                    continue;
                }
                int c = 0;
                for (Term term : Terms) {
                    System.out.print("\t"+(++c)+"  ");
                    if (document.getCoveredTerms().contains(term.getName())) {
                        if (term.getSelectedSynset() != null && !term.getSense().equals("None")) {
                            List<Axiom> SOAxioms = new SynsetOntologyAxioms(term.getSelectedSynset(), Ontology).getAxioms();
                            if (SOAxioms.size() > 0) {
                                for (Axiom axiom : SOAxioms) {
                                }
                                term.SynsetOntologyAxioms.addAll(SOAxioms);
                            }

                        } else {
                            System.out.println("Searching concept for " + term.getName());
                            OntologyConcept concept = Ontology.searchTermInOntology(term.getName());

                            if (concept.getClass() != null) {
                                System.out.println("\t" + term.getName() + " has founded in"
                                        + Ontology.getLabel(concept.getOntClass()).toString() + "@"
                                        + concept.getOntClass().getIRI().getFragment());
                            }

                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println((ex.getMessage().length() > 100) ? ex.getMessage().substring(0, 99) : ex.getMessage() + ""
                        + ((ex.getCause().toString().length() > 100) ? ex.getCause().toString().substring(0, 99) : ex.getCause()));
                continue;
            }
        }
    }

    public static void main3(String[] args) throws RemoteException, OWLOntologyCreationException {
        String url = "http://www.w3.org/2005/04/swls/Simple Life Science Ontology";
        WatsonDocument d = new WatsonDocument(url);

        d.setCacheDocumentURL();
        System.out.println(d.getCacheDocumentURL());
        d.saveDocument();
//         d.saveDocument();
//         OntologyManager m = d.openOntology();
//         OntologyManager m = new OntologyManager("http://ontologyportal.org/translations/SUMO.owl.txt");
//         System.out.println(m);
//         
//         System.out.println(m.searchTermInOntology("Language"));
    }

    public static void main1(String[] args) throws RemoteException, OWLOntologyCreationException {
        WatsonDocument document = new WatsonDocument("http://ontologyportal.org/translations/SUMO.owl.txt");
        document.addSimilarURL("http://reliant.teknowledge.com/DAML/SUMO.daml");
        document.addSimilarURL("http://reliant.teknowledge.com/DAML/SUMO.owl");
        document.addSimilarURL("http://lists.w3.org/Archives/Public/www-rdf-logic/2003Apr/att-0009/SUMO.daml");
        document.setCacheDocumentURL();
        if (!document.saveDocument()) {
            while (!document.saveNextSimilarURL());
            if (document.getFile() == null || "".equals(document.getFile())) {
//                toDelete.add(document);
            }
        }
        System.out.println(document.getFile());
        System.out.println(document.openOntology());
    }

    public static void main2(String[] args) throws RemoteException, OWLOntologyCreationException {

        try {
            // Just load two arbitrary ontologies for the purposes of this example
            OWLOntologyManager man = OWLManager.createOWLOntologyManager();
            man.loadOntologyFromOntologyDocument(IRI.create("file:/e:/owl/sample2.owl"));
            man.loadOntologyFromOntologyDocument(IRI.create("file:/e:/owl/sample1.owl"));
            // Create our ontology merger
            OWLOntologyMerger merger = new OWLOntologyMerger(man);
            // We merge all of the loaded ontologies.  Since an OWLOntologyManager is an OWLOntologySetProvider we
            // just pass this in.  We also need to specify the URI of the new ontology that will be created.
            IRI mergedOntologyIRI = IRI.create("http://www.semanticweb.com/mymergedont");
            OWLOntology merged = merger.createMergedOntology(man, mergedOntologyIRI);
            // Print out the axioms in the merged ontology.
            for (OWLAxiom ax : merged.getAxioms()) {
                System.out.println(ax);
            }
            // Save to RDF/XML
            man.saveOntology(merged, new RDFXMLOntologyFormat(), IRI.create("file:/e:/owl/merged.owl"));
        } catch (OWLOntologyCreationException e) {
            System.out.println("Could not load ontology: " + e.getMessage());
        } catch (OWLOntologyStorageException e) {
            System.out.println("Problem saving ontology: " + e.getMessage());
        }

        //String[] Terms = {"language", "processing", "learning", "process", "using", "concept", "text", "paper", "Web", "construction", "natural language", "natural language processing", "knowledge acquisition",  "Web service", "domain expert", "Semantic Web", "knowledge base"};
//        String[] Terms = {"breast","cancer","patient","women","expression","protein","study","cell","human","treatment","growth","cancer patient","human breast","cancer cell","breast cancer patient","cell growth","growth factor","breast cancer","human breast cancer","cancer screening","protein expression","breast carcinoma","cell line","cell proliferation","cancer cell line","prostate cancer","breast tumor","MCF-7 cell","tumor cell","breast cancer cell"};
//        
//            SearchTerms query = new SearchTerms(Terms);
//            //query.removeRepeatedOntologies();
//            List<WatsonDocument> documents = query.Documents;
//            
//   
    }
}
