package InitialScheme.Sources.WordNetSources;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.util.ArrayList;
import java.util.List;

public class WordNet {

    private static WordNetDatabase WordnetDatabase = null;
    public static List<NounSynset> TopLevelEntities;

    public static WordNetDatabase getWordNetDatabase() {
        if (WordnetDatabase == null) {
            System.setProperty("wordnet.database.dir", "./resources/WordNet-3.0/dict");
            WordnetDatabase = WordNetDatabase.getFileInstance();
            getTopLevelEntities();
        }
        return WordnetDatabase;
    }

    private static void getTopLevelEntities() {
        TopLevelEntities = new ArrayList<NounSynset>();
        
        //Adding Entity
        TopLevelEntities.add((NounSynset)WordnetDatabase.getSynsets("entity")[0]);
        
        //Adding Thing
        TopLevelEntities.add((NounSynset)WordnetDatabase.getSynsets("thing")[7]);
        
        //Adding Physical Entity
        TopLevelEntities.add((NounSynset)WordnetDatabase.getSynsets("physical_entity")[0]);
        
        //Adding Abstraction
        TopLevelEntities.add((NounSynset)WordnetDatabase.getSynsets("abstraction")[5]);
        
        //Adding Thing.n.12
        TopLevelEntities.add((NounSynset)WordnetDatabase.getSynsets("thing")[11]);
        
        //Adding Object
        TopLevelEntities.add((NounSynset)WordnetDatabase.getSynsets("object")[0]);
        
        //Adding Whole
        TopLevelEntities.add((NounSynset)WordnetDatabase.getSynsets("whole")[1]);
        
    }
    
//    public static void main(String[] args){
//        WordNetDatabase wn = WordNet.getWordNetDatabase();
//        if(WordNet.TopLevelEntities.contains((NounSynset) wn.getSynsets("whole")[1])){
//            System.out.println("It contains whole[1] ");
//        }
//        if(WordNet.TopLevelEntities.contains((NounSynset)wn.getSynsets("physical_entity")[0])){
//            System.out.println("It contains physical_entity[0] ");
//        }
//    }
}