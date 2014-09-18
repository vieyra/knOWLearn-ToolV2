package InitialScheme.Sources.OntologySources.Axioms;

import InitialScheme.Sources.OntologySources.Axioms.AxiomElement.AxiomElement;

public class Axiom {
    protected AxiomElement subject;
    protected Object object;
    protected String relation;
    protected boolean TwoElementsAxiom = false;
    
    public Axiom(AxiomElement subject, Object object, String relation){
        this.subject = subject;
        this.object = object;
        this.relation = relation;
    }

    public Object getObject() {
        return object;
    }

    public String getRelation() {
        return relation;
    }

    public AxiomElement getSubject() {
        return subject;
    }

    public TwoElementsAxiom asTwoElementsAxiom() {
        if(this.TwoElementsAxiom)
            return (TwoElementsAxiom) this;
        return null;
    }

    public boolean isTwoElementsAxiom() {
        return TwoElementsAxiom;
    }

    public void setSubject(AxiomElement subject) {
        this.subject = subject;
    }

    
}
