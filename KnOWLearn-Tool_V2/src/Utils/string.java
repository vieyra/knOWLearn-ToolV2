package Utils;

import InitialScheme.Sources.WordNetSources.WordNet;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class string {

    public static void main(String[] args) {
        System.out.println("'" + normalizeEntityName(" protein_") + "'");
    }

    public static void main1(String[] args) {
        System.out.println(JaroWinklerDistance("lipoprotein", "lipotropin"));
    }

    public static double JaroWinklerDistance(String string1, String string2) {
        return JaroWinklerDistance(string1, string2, 0.1);
    }

    public static double JaroWinklerDistance(String string1, String string2, double p) {
        double JD = JaroDistance(string1, string2);
        return JD + (getIndexofCommonPrefix(string1, string2) * p * (1 - JD));
    }

    private static int getIndexofCommonPrefix(String string1, String string2) {
        int n = ((string1.length() > string2.length()) ? (string2.length()) : string1.length()) > 4 ? 4
                : (string1.length() > string2.length()) ? (string2.length()) : string1.length();
        for (int i = 0; i < n; i++) {
            if (string1.charAt(i) != string2.charAt(i)) {
                return i;
            }
        }
        return n;
    }

    public static double JaroDistance(String string1, String string2) {
        int len1 = string1.length();
        int len2 = string2.length();
        if (len1 == 0) {
            return len2 == 0 ? 1.0 : 0.0;
        }

        int searchRange = Math.max(0, Math.max(len1, len2) / 2 - 1);

        boolean[] matched1 = new boolean[len1];
        Arrays.fill(matched1, false);
        boolean[] matched2 = new boolean[len2];
        Arrays.fill(matched2, false);

        int numCommon = 0;
        for (int i = 0; i < len1; ++i) {
            int start = Math.max(0, i - searchRange);
            int end = Math.min(i + searchRange + 1, len2);
            for (int j = start; j < end; ++j) {
                if (matched2[j]) {
                    continue;
                }
                if (string1.charAt(i) != string2.charAt(j)) {
                    continue;
                }
                matched1[i] = true;
                matched2[j] = true;
                ++numCommon;
                break;
            }
        }
        if (numCommon == 0) {
            return 0.0;
        }

        int numHalfTransposed = 0;
        int j = 0;
        for (int i = 0; i < len1; ++i) {
            if (!matched1[i]) {
                continue;
            }
            while (!matched2[j]) {
                ++j;
            }
            if (string1.charAt(i) != string2.charAt(j)) {
                ++numHalfTransposed;
            }
            ++j;
        }
        int numTransposed = numHalfTransposed / 2;

        double numCommonD = numCommon;
        return (numCommonD / len1
                + numCommonD / len2
                + (numCommon - numTransposed) / numCommonD) / 3.0;
    }

    public static float LevenshteinDistance(String string1, String string2) {
        int[][] matriz = new int[string1.length() + 1][string2.length() + 1];
        for (int i = 0; i < string1.length() + 1; i++) {
            matriz[i][0] = i;
        }
        for (int j = 0; j < string1.length() + 1; j++) {
            matriz[0][j] = j;
        }
        for (int i = 1; i < string1.length() + 1; i++) {
            for (int j = 1; j < string1.length() + 1; j++) {
                if (string1.charAt(i - 1) == string2.charAt(i - 1)) {
                    matriz[i][j] = matriz[i - 1][j - 1];
                } else {
                    matriz[i][j] = Math.min(matriz[i][j - 1] + 1, matriz[i - 1][j] + 1);
                    matriz[i][j] = Math.min(matriz[i][j], matriz[i - 1][j - 1] + 1);
                }
            }
        }
        return matriz[string1.length()][string2.length()];
    }

    public static String normalizeEntityName(String EntityName) {
        String normalizedEntityName = "";

        EntityName = EntityName.replace("-", " ");
        EntityName = EntityName.replace("_", " ");

        while (EntityName.contains("  ")) {
            EntityName = EntityName.replace("  ", " ");
        }
        
        if(EntityName.startsWith(" ")){
            EntityName = EntityName.substring(1);
        }
        if(EntityName.endsWith(" ")){
            EntityName = EntityName.substring(0,EntityName.length()-1);
        }

        if (EntityName.matches("[A-Z][a-z0-9 ]+([A-Z][a-z0-9 ]+)+")) {
            String[] splitedEntityName = EntityName.split("[A-Z ]");
            for (int i = 0; i < splitedEntityName.length; i++) {
                String splited = splitedEntityName[i];
                if (!splited.equals("")) {
                    String toAppend = "";
                    try {
                        toAppend = EntityName.substring(EntityName.indexOf(splited) - 1, (EntityName.indexOf(splited) + splited.length()));
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    normalizedEntityName += toAppend + ((i < splitedEntityName.length - 1) ? " " : "");
                }
            }
            EntityName = normalizedEntityName;
        }

        return EntityName.toLowerCase();
    }

    public static String[] getEntityNameSegments(String EntityName) {
        List<String> Segments = new ArrayList<String>();

        if (EntityName.matches("[A-Z][a-z0-9]+([A-Z][a-z0-9]+)+")) {
            String[] splitedEntityName = EntityName.split("[A-Z]");
            for (String splited : splitedEntityName) {
                if (!splited.equals("")) {
                    String toAppend;
                    try {
                        toAppend = EntityName.substring(EntityName.indexOf(splited) - 1, (EntityName.indexOf(splited) + splited.length())).toLowerCase();
                    } catch (Exception ex) {
                        break;
                    }
                    Segments.add(toAppend);
                    EntityName = EntityName.replace(toAppend, "");
                }
            }
        }
        if (EntityName.matches("[A-Za-z0-9]+([ _ -][A-Za-z0-9]+)+")) {
            return EntityName.split("[ _-]");
        }
        if (Segments.size() == 0) {
            Segments.add(EntityName);
        }
        return Segments.toArray(new String[]{});
    }

    public static boolean areSimilars(String WordNetWord, String EntityName) { //Name of a synset of WN and LocalName or Label of a OWLClass
        double jwd;
        WordNetWord = WordNetWord.toLowerCase();
        EntityName = EntityName.toLowerCase();
        if ((jwd = string.JaroWinklerDistance(WordNetWord, EntityName)) > 0.96) {
            return true;
        }
        if (hasWordNetRelation(WordNetWord, EntityName)) {
            return true;
        }
        if (jwd > 0.6) {
            String[] WordNetWordSegments = WordNetWord.split("[ _-]");
            String[] EntityNameSegments = getEntityNameSegments(EntityName);

            int dif = Math.abs(WordNetWordSegments.length - EntityNameSegments.length);
            int min = Math.min(WordNetWordSegments.length, EntityNameSegments.length);

            if (Math.max(WordNetWordSegments.length, EntityNameSegments.length) == 1) {
                return false;
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
                if (!(string.JaroWinklerDistance(WordNetWordSegment, EntityNameSegment) > 0.96)) {
                    if (!areSynonyms(WordNetWordSegment, EntityNameSegment)) {
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }

    private static boolean hasWordNetRelation(String string1, String string2) {
        if (areSynonyms(string1, string2)) {
            return true;
        }
        if (hasPartRelation(string1, string2)) {
            return true;
        }
        if (hasSubstanceRelation(string1, string2)) {
            return true;
        }
        if (hasMemberRelation(string1, string2)) {
            return true;
        }
        if (hasInstanceRelation(string1, string2)) {
            return true;
        }
        return false;
    }

    private static boolean areSynonyms(String string1, String string2) {
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        Synset[] synsets = wn.getSynsets(string2, SynsetType.NOUN);
        for (Synset synset : synsets) {
            for (String WordForm : synset.getWordForms()) {
                if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasPartRelation(String string1, String string2) {
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        Synset[] synsets = wn.getSynsets(string2, SynsetType.NOUN);
        for (Synset synset : synsets) {
            NounSynset NounSynset = (NounSynset) synset;
            for (NounSynset Holonym : NounSynset.getPartHolonyms()) {
                for (String WordForm : Holonym.getWordForms()) {
                    if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
                        return true;
                    }
                }
            }
            for (NounSynset Meronym : NounSynset.getPartMeronyms()) {
                for (String WordForm : Meronym.getWordForms()) {
                    if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasSubstanceRelation(String string1, String string2) {
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        Synset[] synsets = wn.getSynsets(string2, SynsetType.NOUN);
        for (Synset synset : synsets) {
            NounSynset NounSynset = (NounSynset) synset;
            for (NounSynset Holonym : NounSynset.getSubstanceHolonyms()) {
                for (String WordForm : Holonym.getWordForms()) {
                    if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
                        return true;
                    }
                }
            }
            for (NounSynset Meronym : NounSynset.getSubstanceMeronyms()) {
                for (String WordForm : Meronym.getWordForms()) {
                    if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasMemberRelation(String string1, String string2) {
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        Synset[] synsets = wn.getSynsets(string2, SynsetType.NOUN);
        for (Synset synset : synsets) {
            NounSynset NounSynset = (NounSynset) synset;
            for (NounSynset Holonym : NounSynset.getMemberHolonyms()) {
                for (String WordForm : Holonym.getWordForms()) {
                    if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
                        return true;
                    }
                }
            }
            for (NounSynset Meronym : NounSynset.getMemberMeronyms()) {
                for (String WordForm : Meronym.getWordForms()) {
                    if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasInstanceRelation(String string1, String string2) {
        WordNetDatabase wn = WordNet.getWordNetDatabase();
        Synset[] synsets = wn.getSynsets(string2, SynsetType.NOUN);
        for (Synset synset : synsets) {
            NounSynset NounSynset = (NounSynset) synset;
            for (NounSynset Hypernym : NounSynset.getInstanceHypernyms()) {
                for (String WordForm : Hypernym.getWordForms()) {
                    if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
                        return true;
                    }
                }
            }
            for (NounSynset Hyponym : NounSynset.getInstanceHyponyms()) {
                for (String WordForm : Hyponym.getWordForms()) {
                    if (string1.toLowerCase().equals(WordForm.toLowerCase())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}