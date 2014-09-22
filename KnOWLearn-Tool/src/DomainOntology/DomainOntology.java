package DomainOntology;

import InitialScheme.Sources.OntologySources.Axioms.Axiom;
import InitialScheme.Sources.OntologySources.Axioms.TwoElementsAxiom;
import java.io.File;

import java.util.List;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

//http://owlapi.svn.sourceforge.net/viewvc/owlapi/owl1_1/trunk/examples/src/main/java/org/coode/owlapi/examples/Example2.java?view=markup
public class DomainOntology {

    private List<Axiom> Axioms;
    private File LocalFile = new File("./owl/knOWLearn_Ontology.owl");
    private String LocalIRI = "http://knowlearn.cenidet.edu.mx/ontology_2.owl";
    private OWLOntologyManager manager;
    private OWLOntology ontology;
    private OWLDataFactory factory;

    public DomainOntology() throws OWLOntologyCreationException {
        //     this.Axioms = Axioms;
        this.CreateOntology();
    }

    public DomainOntology(List<Axiom> Axioms) throws OWLOntologyCreationException {
        this.Axioms = Axioms;
        this.CreateOntology();
        this.saveClassDefinitions();
        this.saveTaxonomicalRelations();
    }

    public DomainOntology(List<Axiom> Axioms, File LocalFile, String IRI) throws OWLOntologyCreationException {
        this.LocalFile = LocalFile;
        this.LocalIRI = IRI;
        this.Axioms = Axioms;
        this.CreateOntology();
        this.saveClassDefinitions();
        this.saveTaxonomicalRelations();
    }

    private void CreateOntology() throws OWLOntologyCreationException {
        this.manager = OWLManager.createOWLOntologyManager();

        IRI ontologyURI = IRI.create(this.LocalIRI);
        IRI physicalURI = IRI.create(this.LocalFile);

        System.out.println("/-\\ \t" + ontologyURI);
        System.out.println("/-\\ \t" + physicalURI);
        
        SimpleIRIMapper mapper = new SimpleIRIMapper(ontologyURI, physicalURI);
        manager.addIRIMapper(mapper);
        this.ontology = manager.createOntology(ontologyURI);
        this.factory = manager.getOWLDataFactory();
        try {
            this.manager.saveOntology(ontology);
        } catch (OWLOntologyStorageException ex) {
            System.err.println(ex.getMessage());
            System.err.println(ex.getCause());
        }
    }

    private void saveClassDefinitions() {
//        List<Axiom> toDelete = new ArrayList<Axiom>();
        for (Axiom axiom : this.Axioms) {
            if (axiom.getRelation().equals(InitialScheme.Sources.OntologySources.Axioms.AxiomType.Type)) {
                System.out.println("#" + axiom.getSubject().getID());
                OWLClass cls = factory.getOWLClass(IRI.create(this.LocalIRI + "#" + axiom.getSubject().getID()));
                OWLDeclarationAxiom declarationAxiom = factory.getOWLDeclarationAxiom(cls);
                this.manager.addAxiom(this.ontology, declarationAxiom);
            }
        }
        try {
            this.manager.saveOntology(this.ontology);
        } catch (OWLOntologyStorageException ex) {
            System.err.println(ex.getMessage());
            System.err.println(ex.getCause());
        }
    }

    private void saveTaxonomicalRelations() {
        for (Axiom axiom : this.Axioms) {
            if (axiom.isTwoElementsAxiom()) {
                if (axiom.getRelation().equals(InitialScheme.Sources.OntologySources.Axioms.AxiomType.SubClassOf)) {
                    TwoElementsAxiom Axiom2 = axiom.asTwoElementsAxiom();
                    System.out.println("#" + Axiom2.getSubject().getID() + " SubClassOf #" + Axiom2.getObject().getID());
                    OWLClass clsA = this.factory.getOWLClass(IRI.create(this.LocalIRI + "#" + Axiom2.getSubject().getID()));
                    OWLClass clsB = this.factory.getOWLClass(IRI.create(this.LocalIRI + "#" + Axiom2.getObject().getID()));

                    OWLAxiom Oaxiom = this.factory.getOWLSubClassOfAxiom(clsA, clsB);

                    AddAxiom addAxiom = new AddAxiom(ontology, Oaxiom);
                    this.manager.applyChange(addAxiom);
                }
                if (axiom.getRelation().equals(InitialScheme.Sources.OntologySources.Axioms.AxiomType.PartOf)) {
                    TwoElementsAxiom Axiom2 = axiom.asTwoElementsAxiom();
                    System.out.println("#" + Axiom2.getSubject().getID() + " PartOf #" + Axiom2.getObject().getID());
                    OWLClass clsA = this.factory.getOWLClass(IRI.create(this.LocalIRI + "#" + Axiom2.getSubject().getID()));
                    OWLClass clsB = this.factory.getOWLClass(IRI.create(this.LocalIRI + "#" + Axiom2.getObject().getID()));
                    OWLObjectProperty partOfProperty = this.factory.getOWLObjectProperty(IRI.create("PartOf"));

                    OWLObjectSomeValuesFrom partOfSomeHand = factory.getOWLObjectSomeValuesFrom(partOfProperty, clsB);
                    OWLSubClassOfAxiom ax = factory.getOWLSubClassOfAxiom(clsA, partOfSomeHand);
                    AddAxiom addAxiom = new AddAxiom(ontology, ax);
                    this.manager.applyChange(addAxiom);
                }
            }
        }
        try {
            this.manager.saveOntology(this.ontology);
        } catch (OWLOntologyStorageException ex) {
            System.err.println(ex.getMessage());
            System.err.println(ex.getCause());
        }
    }

    public static void main(String[] args) {
        try {
            new DomainOntology();
        } catch (OWLOntologyCreationException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getCause());
        }
    }
}
