package Evaluation;

import InitialScheme.Sources.OntologySources.OntologyManager;
import InitialScheme.WatsonSearch.OntologySearch;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.semanticweb.owlapi.model.OWLClass;

public class RelevantTerminology {
    
    private OntologyManager man;
    private OntologySearch os = new OntologySearch();
    private List<String> OntologyVocabulary = new ArrayList<String>();
    
    public RelevantTerminology(String LocationPath) {
        this.os = new OntologySearch();
        System.out.println("::: Opening ontology :::");
        openOntology(LocationPath);
        System.out.println("::: Getting vocabulary :::");
//        this.individuallySearch();
        getVocabulary();
        System.out.println("::: Searching Terminology :::");
        searchTerminology();
    }
    
    private void openOntology(String Location) {
        this.man = new OntologyManager(Location, true);
    }
    
    private void getVocabulary() {
        System.out.println(this.man.getOntology().getClassesInSignature().size() + " classes");
        for (Iterator<OWLClass> it = this.man.getOntology().getClassesInSignature().iterator(); it.hasNext();) {
            OWLClass Class = it.next();
            String ClassName = this.man.getLabel(Class).replace(" ", "_");
            //String ClassName = Class.getIRI().getFragment();
            if (ClassName.split("_").length < 3) {
                Random r = new Random();
                if ((r.nextInt(50)) == 16) {
                    this.OntologyVocabulary.add(ClassName.toLowerCase());
                    //System.out.println("\t" + ClassName + " added");
                }
            }
            if (this.OntologyVocabulary.size() > 499) {
                break;
            }
        }
    }
    
    
    private void individuallySearch(){
//         List<Object[]> Wterms = new ArrayList<Object[]>();
         for (Iterator<OWLClass> it = this.man.getOntology().getClassesInSignature().iterator(); it.hasNext();) {
            OWLClass Class = it.next();
            String ClassName = this.man.getLabel(Class).replace(" ", "_");
            try {
                int Lresponse = os.searchClasses(new String[]{ClassName}).length;
                if(Lresponse > 1){
                    System.out.println(ClassName + ";" + Lresponse);
                }
            } catch (RemoteException ex) {
                System.err.println(ex.getMessage());
            }
         }
    }
    
    private void searchTerminology() {
        System.out.println(this.OntologyVocabulary.size() + " terms to search");
        List<Object[]> Wterms = new ArrayList<Object[]>();
        
        for (String term : OntologyVocabulary) {
            Wterms.add(new Object[]{term, 0});
        }
        for (int i = 0; i < OntologyVocabulary.size() - 1; i++) {
            for (int j = i + 1; j < OntologyVocabulary.size(); j++) {
                try {
                    int Lresponse = os.searchClasses(new String[]{OntologyVocabulary.get(i), OntologyVocabulary.get(j)}).length;
                    if (Lresponse > 0) {
                        Wterms.get(i)[1] = Integer.parseInt(Wterms.get(i)[1] + "") + Lresponse;
                        Wterms.get(j)[1] = Integer.parseInt(Wterms.get(j)[1] + "") + Lresponse;
                        System.out.println("\t" + OntologyVocabulary.get(i) + ":" + Wterms.get(i)[1] + " \t" + OntologyVocabulary.get(j) + ":" + Wterms.get(j)[1] + "\t" + Lresponse);
                    }
                } catch (Exception ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
        for (Object[] term : Wterms) {
            System.out.println(term[0] + ";" + term[1]);
        }
    }
    
    public static void main2(String[] args) throws IOException {
        String[] terms = new String[]{"ascites","endocrine_gland","fibroblast","forearm","gastrointestinal_system",
        "glipizide","idoxuridine","mesothelial_cell","milrinone_lactate","myoepithelial_cell",
        "sodium_lactate","tourniquet","urinary_system","urothelium"};
        for(int i = 0 ; i < terms.length - 2  ; i++)
            for(int j = i + 1 ; j < terms.length - 1; j++)
                for(int k = j + 1 ; k < terms.length ; k++){
                    System.out.println(terms[i] + "\t" +terms[j] + "\t" +terms[k]);
                }

    }
    public static void main(String[] args) throws IOException {
        new RelevantTerminology("Thesaurus.owl");
        //new RelevantTerminology("biotop.owl");
        //new RelevantTerminology("go_daily-termdb.owl");
        
//        OntologyManager m = new OntologyManager("Thesaurus.owl", true);
//        OntologySearch os = new OntologySearch();
//        List<String> terms = new ArrayList<String>();
////        FileWriter fw = new FileWriter(new BufferedWriter(new File("")));
//        for (Iterator<OWLClass> it = m.getOntology().getClassesInSignature().iterator(); it.hasNext();) {
//            OWLClass Class = it.next();
//            //System.out.println((Class.getIRI().getFragment().split("_").length < 3)? Class.getIRI().getFragment().toLowerCase().replace("_", " "): "");
//            if (Class.getIRI().getFragment().split("_").length < 3) {
//                terms.add(Class.getIRI().getFragment().toLowerCase());
//            }
//            
//            
//        }
//        System.out.println(":::" + terms.size() + " terms are taked");
//        List<Object[]> Wterms = new ArrayList<Object[]>();
//        for (String term : terms) {
//            Wterms.add(new Object[]{term, 0});
//        }
//        for (int i = 0; i < terms.size() - 1; i++) {
//            for (int j = i + 1; j < terms.size(); j++) {
//                try {
//                    int Lresponse = os.searchClasses(new String[]{terms.get(i), terms.get(j)}).length;
//                    if (Lresponse > 0) {
//                        Wterms.get(i)[1] = (int) Wterms.get(i)[1] + Lresponse;
//                        Wterms.get(j)[1] = (int) Wterms.get(j)[1] + Lresponse;
//                        System.out.println("\t" + terms.get(i) + ":" + Wterms.get(i)[1] + " \t" + terms.get(j) + ":" + Wterms.get(j)[1] + "\t" + Lresponse);
//                    }
//                } catch (Exception ex) {
//                    System.err.println(ex.getMessage());
//                }
//            }
//        }
//        
//        for (Object[] term : Wterms) {
//            System.out.println(term[0] + ";" + term[1]);
//        }
//        
    }
}
