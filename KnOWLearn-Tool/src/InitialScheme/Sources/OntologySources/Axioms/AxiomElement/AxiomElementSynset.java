
package InitialScheme.Sources.OntologySources.Axioms.AxiomElement;

import edu.smu.tspell.wordnet.NounSynset;
import org.semanticweb.owlapi.model.OWLClass;

public class AxiomElementSynset extends AxiomElement{
    
    private NounSynset FirstElementMapped;
    
    
    public AxiomElementSynset(NounSynset FirstElementMapped, OWLClass SecondElementMapped){
        this.FirstElementMapped = FirstElementMapped;
        this.SecondElementMapped = SecondElementMapped;
        this.setID(this.FirstElementMapped.getWordForms()[0].replace(" ", "_"));
    }

    @Override
    public NounSynset getFirstElementMapped() {
        return FirstElementMapped;
    }

}
