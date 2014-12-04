package InitialScheme.Sources.WordNetSources;

import java.util.ArrayList;
import java.util.List;

public class Synset {

    private String Name;
    private String Definition;
    private List<Synset> Hypernyms;
    private List<Synset> Hyponyms;

    public Synset(String MDSynset) {
        if (MDSynset.contains("/SynsetName/")) {
            parseMDSynset(MDSynset);
        } else {
            this.Name = MDSynset;
        }
        this.Hypernyms = new ArrayList<Synset>();
        this.Hyponyms = new ArrayList<Synset>();
    }

    private void parseMDSynset(String MDSynset) {
        String[] Synset = MDSynset.split("/SynsetName/");
        this.Name = Synset[0];
        this.Definition = Synset[1].replace("/SynsetDefinition/", "");
    }

    public String getDefinition() {
        return Definition;
    }

    public String getName() {
        return Name;
    }

    public List<Synset> getHypernyms() {
        return Hypernyms;
    }

    public List<Synset> getHyponyms() {
        return Hyponyms;
    }

    public void addHypernym(Synset synset) {
        this.Hypernyms.add(synset);
    }

    public void addHyponym(Synset synset) {
        this.Hyponyms.add(synset);
    }
}