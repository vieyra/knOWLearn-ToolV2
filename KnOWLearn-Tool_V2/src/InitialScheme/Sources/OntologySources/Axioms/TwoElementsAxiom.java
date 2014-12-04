package InitialScheme.Sources.OntologySources.Axioms;

import InitialScheme.Sources.OntologySources.Axioms.AxiomElement.AxiomElement;

public class TwoElementsAxiom extends Axiom{
    
    private AxiomElement object;
    
    public TwoElementsAxiom(AxiomElement subject, AxiomElement object, String relation){
        super(subject, object,  relation);
        this.object = object;
        this.TwoElementsAxiom = true;
    }

    @Override
    public AxiomElement getObject() {
        return object;
    }
    
}
