
import InitialScheme.Sources.OntologySources.OntologyManager;
import InitialScheme.WatsonSearch.OntologySearch;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClass;

public class OpenOntology {
    
    public OpenOntology(String LocationPath){
        
        
        listClasses();
    }
    

    private void listClasses(){
        
    }
    
    public static void main(String[] args) throws IOException{
        OntologyManager m = new OntologyManager("Thesaurus.owl", true);
        OntologySearch os = new OntologySearch();
        List<String> terms = new ArrayList<String>();
//        FileWriter fw = new FileWriter(new BufferedWriter(new File("")));
        for (Iterator<OWLClass> it = m.getOntology().getClassesInSignature().iterator(); it.hasNext();) {
            OWLClass Class = it.next();
            //System.out.println((Class.getIRI().getFragment().split("_").length < 3)? Class.getIRI().getFragment().toLowerCase().replace("_", " "): "");
            if (Class.getIRI().getFragment().split("_").length < 3) {
                terms.add(Class.getIRI().getFragment().toLowerCase());
            }


        }
        System.out.println(":::" + terms.size() + " terms are taked");
        List<Object[]> Wterms = new ArrayList<Object[]>();
        for (String term : terms) {
            Wterms.add(new Object[]{term,0});
        }
        for (int i = 0; i < terms.size() - 1; i++) {
            for (int j = i + 1; j < terms.size(); j++) {
                try{
                int Lresponse = os.searchByClasses(new String[]{terms.get(i),terms.get(j)}).length;
                if(Lresponse > 0){
                    Wterms.get(i)[1] = Integer.parseInt(Wterms.get(i)[1] + "") + Lresponse;
                    Wterms.get(j)[1] = Integer.parseInt(Wterms.get(j)[1] + "") + Lresponse;
                    System.out.println("\t" + terms.get(i)+ ":"+Wterms.get(i)[1]+ " \t"+terms.get(j)+":"+Wterms.get(j)[1]+"\t"+Lresponse);
                }
                }catch(Exception ex){
                    System.err.println(ex.getMessage());
                }
            }
        }
        
        for(Object[] term : Wterms){
            System.out.println(term[0] + ";" + term[1]);
        }

    }
    
}
