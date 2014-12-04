package InitialScheme.Sources.OntologySources;

import Utils.string;
import org.semanticweb.owlapi.model.OWLClass;

public class OntologyConcept {

    private String ConceptName;
    private double JWD = 0.0;     //JaroWinklerDistance between ConceptName and class.localname or class.label
    private OWLClass Class;


    public OntologyConcept(String Concept){
        this.ConceptName = Concept;
    }
    
    public OWLClass getOntClass() {
        return Class;
    }

    public String getConceptName() {
        return ConceptName;
    }

    public boolean setOntClass(OWLClass Class, double jwd) {
        if(jwd >= this.JWD && this.Class != Class)
            if(this.JWD == 1.0){
                double JWDistancetoNewClass = string.JaroWinklerDistance(
                        ConceptName.toLowerCase(), Class.getIRI().getFragment().toLowerCase());
                double JWDDistancetoCurrentClass = string.JaroWinklerDistance(
                        ConceptName.toLowerCase(), this.Class.getIRI().getFragment().toLowerCase());;
                if(JWDistancetoNewClass > JWDDistancetoCurrentClass){
                    this.Class = Class;
                    return true;
                }
                return false;
            } else {
                this.Class = Class;
                this.JWD = jwd;
                return true;
            }
        return false;
    } 
}
