package InitialScheme.Sources.WordNetSources;

import InitialScheme.Mapping.RelationType;
import InitialScheme.Sources.OntologySources.OntologyConcept;
import Utils.string;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SynsetConcept extends OntologyConcept {

    private NounSynset synset;

    public SynsetConcept(String name) {
        super(name);
    }

    public SynsetConcept(NounSynset Synset) {
        super(Synset.getWordForms()[0]);
        this.synset = Synset;
    }

    public NounSynset getSynsetforEntity(String EntityName) {
        EntityName = string.normalizeEntityName(EntityName).replace(" ", "_");
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        for(edu.smu.tspell.wordnet.Synset synset_ : wn.getSynsets(EntityName, SynsetType.NOUN)){
            NounSynset nounsynset = (NounSynset) synset_;
            for(NounSynset hypernym : this.getHypernyms()){
                 if(hypernym.equals(nounsynset)){
                    return nounsynset;
                }
            }
            for(NounSynset hyponym : this.getHyponyms()){
                if(hyponym.equals(nounsynset)){
                    return nounsynset;
                }
            }    
        }
        return null;
    }
    
    public NounSynset getSynset() {
        return synset;
    }

    public void setSynset(NounSynset Synset) {
        this.synset = Synset;
    }

    public List<NounSynset> getHypernyms() {
        return this.getHypernyms(5);
    }

    public List<NounSynset> getHypernyms(int level) {
        List<NounSynset> Hypernyms = new ArrayList<NounSynset>();
        List<NounSynset> antHypernyms = new ArrayList<NounSynset>();
        antHypernyms.addAll(Arrays.asList(this.synset.getHypernyms()));
        for (int i = 0; i < level; i++) {
            int antHypernymsSize = antHypernyms.size();
            if (antHypernymsSize == 0) {
                break;
            }
            Hypernyms.removeAll(WordNet.TopLevelEntities);
            Hypernyms.addAll(antHypernyms);
            for (int j = 0; j < antHypernymsSize; j++) {
                antHypernyms.addAll(Arrays.asList(antHypernyms.get(j).getHypernyms()));
            }
            antHypernyms.removeAll(Hypernyms);
        }
        return Hypernyms;
    }

    public List<NounSynset> getHyponyms() {
        return this.getHyponyms(5);
    }

    public int getNivelOfRelation(NounSynset objectElement) {
//        int relation = getNivelOfRelation(objectElement, 6);
//        System.out.println("********" + this.synset.getWordForms()[0] + " with " + objectElement.getWordForms()[0] + " : " + relation);
//        return relation;
        return getNivelOfRelation(objectElement, 6);
    }

    private int getNivelOfRelation(NounSynset objectElement, int levelmax) {
        List<NounSynset> antHypernyms = new ArrayList<NounSynset>();
        antHypernyms.addAll(Arrays.asList(this.synset.getHypernyms()));
        for (int i = 1; i < levelmax; i++) {
            int antHypernymsSize = antHypernyms.size();
            if (antHypernymsSize == 0) {
                break;
            }
            for(NounSynset synset : antHypernyms){
                if(synset.equals(objectElement)){
                    return i;
                }
            }
            List<NounSynset> aux = new ArrayList<NounSynset>();
            for (int j = 0; j < antHypernymsSize; j++) {
                aux.addAll(Arrays.asList(antHypernyms.get(j).getHypernyms()));
            }
            antHypernyms.clear();
            antHypernyms.addAll(aux);
        }
        return -1;
    }
    
    public List<NounSynset> getHyponyms(int level) {
        List<NounSynset> Hyponyms = new ArrayList<NounSynset>();
        List<NounSynset> antHyponyms = new ArrayList<NounSynset>();
        antHyponyms.addAll(Arrays.asList(this.synset.getHyponyms()));
        for (int i = 0; i < level; i++) {
            int antHyponymsSize = antHyponyms.size();
            if (antHyponymsSize == 0) {
                break;
            }
            Hyponyms.addAll(antHyponyms);
            for (int j = 0; j < antHyponymsSize; j++) {
                antHyponyms.addAll(Arrays.asList(antHyponyms.get(j).getHyponyms()));
            }
            antHyponyms.removeAll(Hyponyms);
        }
        return Hyponyms;
    }
    
    
    public int isSimilarTo(String EntityName) { //Name of a synset of WN and LocalName or Label of a OWLClass
        int relationtype = -1;
        double jwd;
        EntityName = string.normalizeEntityName(EntityName);
        String conceptName = string.normalizeEntityName(this.getConceptName());
        if (string.JaroWinklerDistance(conceptName, EntityName) > 0.98) {
            return RelationType.EQUIVALENT;
        }
        if ((relationtype = this.isRelatedWith(EntityName)) != -1) {
            return relationtype;
        }
        for (String WordNetWord : this.synset.getWordForms()) {
            WordNetWord = WordNetWord.toLowerCase();
            if ((jwd = string.JaroWinklerDistance(WordNetWord, EntityName)) > 0.6) {
                if((relationtype = compareSegments(WordNetWord.split("[ _-]"),string.getEntityNameSegments(EntityName))) != -1){
//System.out.println(EntityName + " and " + WordNetWord);
                    return relationtype;
                }
            }
        }
        return relationtype;
    }
    
    public int isRelatedWith(String string) {
        int relationtype = -1;
        if ((relationtype = isSynonymOf(string)) != -1) {
            return relationtype;
        }
        if ((relationtype = isHypernymof(string)) != -1) {
            return relationtype;
        }
        if ((relationtype = isHyponymof(string)) != -1) {
            return relationtype;
        }
        if ((relationtype = hasPartRelation(string)) != -1) {
            return relationtype;
        }
        if ((relationtype = hasSubstanceRelation(string)) != -1) {
            return relationtype;
        }
        if ((relationtype = hasMemberRelation(string)) != -1) {
            return relationtype;
        }
        if ((relationtype = hasInstanceRelation(string)) != -1) {
            return relationtype;
        }
        return relationtype;
    }
    
    public int isSynonymOf(String entity) {
        for (String WordForm : this.synset.getWordForms()) {
            if (string.JaroWinklerDistance(
                string.normalizeEntityName(entity)
                , WordForm.toLowerCase()) > 0.98) {
                return RelationType.SYNONYM;
            }
        }
        return -1;
    }
    
    public NounSynset getMemberHolonym(String entity) {
        entity =  entity.replace("_", " ").replace("-", " ").toLowerCase();
        for (NounSynset Holonym : this.synset.getMemberHolonyms()) {
            for (String WordForm : Holonym.getWordForms()) {
                if (string.JaroWinklerDistance(
                        entity.toLowerCase(), WordForm.toLowerCase()) > 0.975) {
                    return Holonym;
                }
            }
        }
        return null;

    }

    public NounSynset getPartial(String entity){
        entity =  entity.replace("_", " ").replace("-", " ").toLowerCase();
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        edu.smu.tspell.wordnet.Synset[] synsets = wn.getSynsets(entity, SynsetType.NOUN);
        if (synsets.length == 1) {
            return (NounSynset) synsets[0];
        }
        if (synsets.length > 1) {
            for (edu.smu.tspell.wordnet.Synset synset : synsets) {
                if (this.getHypernyms().contains((NounSynset) synset)) {
                    return (NounSynset) synset;
                }
                if (this.getHyponyms().contains((NounSynset) synset)) {
                    return (NounSynset) synset;
                }
            }
        }
        return null;
    }
    
    public NounSynset getPartHolonym(String entity) {
        entity =  entity.replace("_", " ").replace("-", " ").toLowerCase();
        for (NounSynset Holonym : this.synset.getPartHolonyms()) {
            for (String WordForm : Holonym.getWordForms()) {
                if (string.JaroWinklerDistance(
                        entity.toLowerCase(), WordForm.toLowerCase()) > 0.975) {
                    return Holonym;
                }
            }
        }
        return null;
    }

    public NounSynset getSubstanceHolonym(String entity) {
        entity =  entity.replace("_", " ").replace("-", " ").toLowerCase();
        for (NounSynset Holonym : this.synset.getSubstanceHolonyms()) {
            for (String WordForm : Holonym.getWordForms()) {
                if (string.JaroWinklerDistance(
                        entity.toLowerCase(), WordForm.toLowerCase()) > 0.975) {
                    return Holonym;
                }
            }
        }
        return null;        
    }
    
    public NounSynset getMemberMeronym(String entity) {
        entity =  entity.replace("_", " ").replace("-", " ").toLowerCase();
        for (NounSynset Meronym : this.synset.getMemberMeronyms()) {
            for (String WordForm : Meronym.getWordForms()) {
                if (string.JaroWinklerDistance(
                        entity.toLowerCase(), WordForm.toLowerCase()) > 0.975) {
                    return Meronym;
                }
            }
        }
        return null;
    }

    public NounSynset getPartMeronym(String entity) {
        entity =  entity.replace("_", " ").replace("-", " ").toLowerCase();
        for (NounSynset Meronym : this.synset.getPartMeronyms()) {
            for (String WordForm : Meronym.getWordForms()) {
                if (string.JaroWinklerDistance(
                        entity.toLowerCase(), WordForm.toLowerCase()) > 0.975) {
                    return Meronym;
                }
            }
        }
        return null;
    }

    public NounSynset getSubstanceMeronym(String entity) {
        entity =  entity.replace("_", " ").replace("-", " ").toLowerCase();
        for (NounSynset Meronym : this.synset.getSubstanceMeronyms()) {
            for (String WordForm : Meronym.getWordForms()) {
                if (string.JaroWinklerDistance(
                        entity.toLowerCase(), WordForm.toLowerCase()) > 0.975) {
                    return Meronym;
                }
            }
        }
        return null;        
    }
    
    public NounSynset getInstanceHypernym(String entity) {
        entity =  entity.replace("_", " ").replace("-", " ").toLowerCase();
        for (NounSynset Hypernym : this.synset.getInstanceHypernyms()) {
            for (String WordForm : Hypernym.getWordForms()) {
                if (string.JaroWinklerDistance(
                        entity.toLowerCase(), WordForm.toLowerCase()) > 0.975) {
                    return Hypernym;
                }
            }
        }
        return null;        
    }

    public NounSynset getInstanceHyponym(String entity) {
       WordNetDatabase wn = WordNet.getWordNetDatabase();
        for (edu.smu.tspell.wordnet.Synset entitySynset : wn.getSynsets(entity, SynsetType.NOUN)) {
            for (NounSynset Hypernym : ((NounSynset) entitySynset).getInstanceHypernyms()) {
                if (Hypernym.equals(this.synset)) {
                    return (NounSynset)entitySynset;
                }
            }
        }
        return null; 
    }
    
    /*

     */

    public NounSynset getHyponym(String entity) {
      //System.out.println("Searching synset for " + entity + " in hyponyms of " + this.getConceptName());
       WordNetDatabase wn = WordNet.getWordNetDatabase();
       entity = string.normalizeEntityName(entity).replace(" ", "_");
        for (edu.smu.tspell.wordnet.Synset entitySynset : wn.getSynsets(entity, SynsetType.NOUN)) {
            for (NounSynset Hypernym : ((NounSynset) entitySynset).getHypernyms()) {
                if (Hypernym.equals(this.synset)) {
                    return (NounSynset)entitySynset;
                }
            }
        }
        return null; 
    }
    
    private int isHypernymof(String entity) {
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        for (edu.smu.tspell.wordnet.Synset entitySynset : wn.getSynsets(entity, SynsetType.NOUN)) {
            for (NounSynset Hypernym : ((NounSynset) entitySynset).getHypernyms()) {
                if (Hypernym.equals(this.synset)) {
                    return RelationType.HYPERNYM;
                }
            }
        }
        return -1;
    }

    public NounSynset getHypernym(String entity) {
        //System.out.println("Searching synset for " + entity + " in hypernyms of " + this.getConceptName());
        entity =  entity.replace("_", " ").replace("-", " ").toLowerCase();
        for (NounSynset Hypernym : this.synset.getHypernyms()) {
            for (String WordForm : Hypernym.getWordForms()) {
                if (string.JaroWinklerDistance(
                        entity.toLowerCase(), WordForm.toLowerCase()) > 0.975) {
                    return Hypernym;
                }
            }
        }
        return null;        
    }
    
    private int isHyponymof(String entity) {
        for (NounSynset Hypernym : this.synset.getHypernyms()) {
            for (String WordForm : Hypernym.getWordForms()) {
                if (string.JaroWinklerDistance(
                        entity.toLowerCase(), WordForm.toLowerCase()) > 0.975) {
                    return RelationType.HYPONYM;
                }
            }
        }
        return -1;
    }

    public int hasPartRelation(String string) {
        for (NounSynset Holonym : this.synset.getPartHolonyms()) {
            for (String WordForm : Holonym.getWordForms()) {
                if (string.toLowerCase().equals(WordForm.toLowerCase())) {
                    return RelationType.PART_MERONYM;
                }
            }
        }
        for (NounSynset Meronym : this.synset.getPartMeronyms()) {
            for (String WordForm : Meronym.getWordForms()) {
                if (string.toLowerCase().equals(WordForm.toLowerCase())) {
                    return RelationType.PART_HOLONYM;
                }
            }
        }
        return -1;
    }

    public int hasSubstanceRelation(String string) {
        for (NounSynset Holonym : this.synset.getSubstanceHolonyms()) {
            for (String WordForm : Holonym.getWordForms()) {
                if (string.toLowerCase().equals(WordForm.toLowerCase())) {
                    return RelationType.SUBSTANCE_MERONYM;
                }
            }
        }
        for (NounSynset Meronym : this.synset.getSubstanceMeronyms()) {
            for (String WordForm : Meronym.getWordForms()) {
                if (string.toLowerCase().equals(WordForm.toLowerCase())) {
                    return RelationType.SUBSTANCE_MERONYM;
                }
            }
        }
        return -1;
    }

    public int hasMemberRelation(String string) {
        for (NounSynset Holonym : this.synset.getMemberHolonyms()) {
            for (String WordForm : Holonym.getWordForms()) {
                if (string.toLowerCase().equals(WordForm.toLowerCase())) {
                    return RelationType.MEMBER_MERONYM;
                }
            }
        }
        for (NounSynset Meronym : this.synset.getMemberMeronyms()) {
            for (String WordForm : Meronym.getWordForms()) {
                if (string.toLowerCase().equals(WordForm.toLowerCase())) {
                    return RelationType.MEMBER_HOLONYM;
                }
            }
        }
        return -1;
    }

    public int hasInstanceRelation(String string) {
        for (NounSynset Hypernym : this.synset.getInstanceHypernyms()) {
            for (String WordForm : Hypernym.getWordForms()) {
                if (string.toLowerCase().equals(WordForm.toLowerCase())) {
                    return RelationType.INSTANCE_HYPONYM;
                }
            }
        }
        for (NounSynset Hyponym : this.synset.getInstanceHyponyms()) {
            for (String WordForm : Hyponym.getWordForms()) {
                if (string.toLowerCase().equals(WordForm.toLowerCase())) {
                    return RelationType.INSTANCE_HYPERNYM;
                }
            }
        }
        return -1;
    }
    
    private static int compareSegments(String[] WordNetWordSegments, String[] EntityNameSegments) {
//        String[] WordNetWordSegments = WordNetWord.split("[ _-]");
//                String[] EntityNameSegments = string.getEntityNameSegments(EntityName);

        int dif = Math.abs(WordNetWordSegments.length - EntityNameSegments.length);
        int min = Math.min(WordNetWordSegments.length, EntityNameSegments.length);

        
        if (Math.max(WordNetWordSegments.length, EntityNameSegments.length) == 1) {
            return -1;
        }
        
        for (int i = min - 1; i >= 0; i--) {
            String WordNetWordSegment = WordNetWordSegments[i];
            String EntityNameSegment = EntityNameSegments[i];
            if (dif != 0) {
                if (WordNetWordSegments.length > EntityNameSegments.length) {
                    WordNetWordSegment = WordNetWordSegments[i + dif];
                } else {
                    EntityNameSegment = EntityNameSegments[i + dif];
                }
            }
            if (!(string.JaroWinklerDistance(WordNetWordSegment, EntityNameSegment) > 0.98)) {
                if (!areSynonyms(WordNetWordSegment, EntityNameSegment)) {
                    return -1;
                }
            }
        }
        if(dif == 0)
            return RelationType.SYNONYM;
        return RelationType.PARTIAL;
    }

    private static boolean areSynonyms(String string1, String string2) {
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        edu.smu.tspell.wordnet.Synset[] synsets = wn.getSynsets(string2, SynsetType.NOUN);
        for (edu.smu.tspell.wordnet.Synset synset : synsets) {
            for (String WordForm : synset.getWordForms()) {
                if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
//----------Revisar esto----------Revisar esto----------Revisar esto----------Revisar esto----------//              
                    if (!string1.equals("ace") && !(string2.equals("one") || string2.equals("1") || string2.equals("i") || string2.equals("I") || string2.equals("ace"))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main1(String[] args){
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        String EntityName = "B_CELL";
        String SynsetName = "cell";
        System.out.println();
    }
    
    public static void main(String[] args){
        WordNetDatabase wn = WordNet.getWordNetDatabase();
//        String EntityName = "tissue";
//        String SynsetName = "body_part";
        String EntityName = "structure";
        String SynsetName = "breast";
        SynsetConcept synsetconcept = new SynsetConcept((NounSynset)wn.getSynsets(SynsetName)[1]);
//        NounSynset Synset = synsetconcept.getPartHolonym(EntityName);
        System.out.println(synsetconcept.getNivelOfRelation((NounSynset)wn.getSynsets(EntityName)[3]));
        
        System.out.println(((NounSynset)wn.getSynsets(EntityName)[3]).getDefinition());
//        System.out.println(Synset.getWordForms()[0]);
    }
    
    public static void main2(String[] args){
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        String EntityName = "colon-cancer";
        String SynsetName = "cancer";
        int relationtype = new SynsetConcept((NounSynset)wn.getSynsets(SynsetName)[0]).isSimilarTo(EntityName);
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
            case RelationType.HYPERNYM:
                System.out.println(SynsetName + " is hypernym of  " + EntityName);
                break;
            case RelationType.HYPONYM:
                System.out.println(SynsetName + " is hyponym of " + EntityName);
                break;
            default:
                System.out.println(SynsetName + " and  " + EntityName + " don't have relation");
                break;
        }
    }


    
    

}