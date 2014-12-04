package InitialScheme.Sources.OntologySources.Axioms.AxiomElement;

import org.semanticweb.owlapi.model.OWLClass;

public class AxiomElement {
    protected String ID;
    protected Object FirstElementMapped;
    protected OWLClass SecondElementMapped;
    protected boolean SynsetType = true;
    
    public AxiomElement() {
        
    }
    
    public AxiomElement(Object FirstElementMapped, OWLClass SecondElementMapped) {
        this.FirstElementMapped = FirstElementMapped;
        this.SecondElementMapped = SecondElementMapped;
    }

    public Object getFirstElementMapped() {
        return FirstElementMapped;
    }

    public OWLClass getSecondElementMapped() {
        return SecondElementMapped;
    }

    public void setSynsetType(boolean SynsetType) {
        this.SynsetType = SynsetType;
    }
    
    
    
    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public void setSecondElementMapped(OWLClass SecondElementMapped) {
        this.SecondElementMapped = SecondElementMapped;
    }

    public boolean hasSecondElement() {
        return this.SecondElementMapped != null;
    }

    public boolean isSynsetType() {
        return SynsetType;
    }
    
    public boolean isOWLClassType() {
        return !SynsetType;
    }
    
    public AxiomElementSynset asAxiomElementSynset(){
        if(this.isSynsetType()){
            return (AxiomElementSynset) this;
        }
        return null;
    }
    
    public AxiomElementClass asAxiomElementClass(){
        if(this.isOWLClassType()){
            return (AxiomElementClass) this;
        }
        return null;
    }
    
}
