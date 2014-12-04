package InitialScheme.Sources;

import InitialScheme.Sources.OntologySources.Axioms.Axiom;
import java.util.ArrayList;
import java.util.List;
import InitialScheme.Sources.WordNetSources.Synset;
import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;

public class Term {

    private List<Synset> synsets;
    private String Name;
    private String Sense;
    private boolean verified = false;
    private NounSynset selectedSynset;
    public List<Axiom> SynsetOntologyAxioms;
    public List<Axiom> OntologyAxioms;
    public List<Axiom> SynsetAxioms;

    public Term(String MDTerm) {
        this.synsets = new ArrayList<Synset>();
        parseMDTerm(MDTerm);
        SynsetOntologyAxioms = new ArrayList<Axiom>();
        OntologyAxioms = new ArrayList<Axiom>();
        SynsetAxioms = new ArrayList<Axiom>();
    }

    private void parseMDTerm(String MDTerm) {
        String[] termparse1 = MDTerm.split("/TermName/");
        this.Name = termparse1[0];
        String[] termparse2 = termparse1[1].split("/TermSense/");
        this.Sense = termparse2[0];
        try {
            for (String synset : termparse2[1].split("/Synset/")) {
                this.synsets.add(new Synset(synset));
            }
            this.setSelectedSynset();
        } catch (ArrayIndexOutOfBoundsException ex) { }
    }

    public String getName() {
        return Name;
    }

    public String getSense() {
        return Sense;
    }

    public void setSense(String Sense) {
        this.Sense = Sense;
        this.setSelectedSynset();
    }

    private void setSelectedSynset() {
        for (Synset synset : this.getSynsets()) {
            if (synset.getName().equals(this.Sense)) {
                this.selectedSynset = getCorrespondentSynset(synset.getDefinition());
            }
        }
    }

    public NounSynset getSelectedSynset() {
        return selectedSynset;
    }

    public List<Synset> getSynsets() {
        return synsets;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    private NounSynset getCorrespondentSynset(String definition) {
        WordNetDatabase wn = InitialScheme.Sources.WordNetSources.WordNet.getWordNetDatabase();
        edu.smu.tspell.wordnet.Synset[] synsets = wn.getSynsets(this.Name, SynsetType.NOUN);
        for (int i = 0; i < synsets.length; i++) {
            if ((Utils.string.JaroWinklerDistance(synsets[i].getDefinition(), definition)) > 0.9) {
                return (NounSynset) synsets[i];
            }
        }
        return null;
    }
}