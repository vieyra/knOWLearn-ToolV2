package InitialScheme.Sources.OntologySources.Axioms.AxiomElement;

import org.semanticweb.owlapi.model.OWLClass;

public class AxiomElementClass extends AxiomElement {
   
    
    private OWLClass FirstElementMapped;
    
    
    public AxiomElementClass(OWLClass FirstElementMapped, OWLClass SecondElementMapped){
        this.FirstElementMapped = FirstElementMapped;
        this.SecondElementMapped = SecondElementMapped;
        this.SynsetType = false;
    }

    @Override
    public OWLClass getFirstElementMapped() {
        return FirstElementMapped;
    }


    
    
   
}
