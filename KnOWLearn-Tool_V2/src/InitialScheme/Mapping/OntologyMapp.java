package InitialScheme.Mapping;

import InitialScheme.Sources.WordNetSources.SynsetConcept;
import org.semanticweb.owlapi.model.OWLClass;

public class OntologyMapp {

    private SynsetConcept Synset;
    private OWLClass OntClass;
    private int relationtype;
    private double OverlapyingDegree;

    public OntologyMapp(SynsetConcept Synset, OWLClass OntClass) {
        this.Synset = Synset;
        this.OntClass = OntClass;
        this.relationtype = RelationType.SYNONYM;
        this.OverlapyingDegree = 1.0;
    }

    public OntologyMapp(SynsetConcept Synset, OWLClass OntClass, int relationtype) {
        this.Synset = Synset;
        this.OntClass = OntClass;
        this.relationtype = relationtype;
        if (this.relationtype < RelationType.PARTIAL) {
            this.OverlapyingDegree = 1.0;
        }
    }

    public OntologyMapp(SynsetConcept Synset, OWLClass OntClass, int relationtype, double OverlapyingDegree) {
        this.Synset = Synset;
        this.OntClass = OntClass;
        this.relationtype = relationtype;
        this.OverlapyingDegree = OverlapyingDegree;
    }

    public double getOverlapyingDegree() {
        return OverlapyingDegree;
    }

    public int getRelationType() {
        return relationtype;
    }

    public OWLClass getOntClass() {
        return OntClass;
    }

    public SynsetConcept getSynsetConcept() {
        return Synset;
    }

    public void setOverlapyingDegree(double OverlapyingDegree) {
        this.OverlapyingDegree = OverlapyingDegree;
    }

    public void setRelationType(int RelationType) {
        this.relationtype = RelationType;
    }
}
