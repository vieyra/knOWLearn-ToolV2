package InitialScheme;

import DomainOntology.DomainOntology;
import InitialScheme.Mapping.SynsetOntologyAxioms;
import InitialScheme.Sources.OntologySources.Axioms.Axiom;
import InitialScheme.Sources.OntologySources.Axioms.AxiomElement.AxiomElementSynset;
import InitialScheme.Sources.OntologySources.Axioms.AxiomType;
import InitialScheme.Sources.OntologySources.Axioms.TwoElementsAxiom;
import InitialScheme.Sources.OntologySources.OntologyConcept;
import InitialScheme.Sources.OntologySources.OntologyManager;
import InitialScheme.Sources.Term;
import InitialScheme.Sources.WatsonDocument;
import InitialScheme.Sources.WordNetSources.SynsetConcept;
import InitialScheme.Sources.WordNetSources.WordNet;
import InitialScheme.WatsonSearch.SearchTerms;
import DomainOntology.OWLFileFilter;
import Utils.PythonConnection.ConnectionManager;
import edu.smu.tspell.wordnet.NounSynset;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

public class InitialScheme {

    private List<Term> Terms;
    private ConnectionManager con;
    private List<WatsonDocument> Documents;
    private List<Axiom> Axioms;

    public InitialScheme(List<Term> Terms, List<WatsonDocument> Documents) {
        this.Terms = Terms;
        this.Documents = Documents;
        this.Axioms = new ArrayList<Axiom>();
        System.out.println("Getting Axioms");
        searchAxioms();
        System.out.println("Printing Axioms");
        printAxioms();
    }

    public InitialScheme(List<Term> Terms) throws RemoteException, IOException {
        init(Terms);
    }

    public InitialScheme(List<Term> Terms, ConnectionManager con) throws RemoteException {
        this.con = con;
        init(Terms);
//        printTerms();
    }

    private void init(List<Term> Terms) {
            this.Terms = Terms;
            this.Axioms = new ArrayList<Axiom>();
            deleteNonRelatedsTerms();
            System.out.println("Searching Ontologies" + "\t");
            searchOntologies();
            System.out.println("Saving Ontologies" + "\t");
            saveDocuments();
            System.out.println("Getting Axioms" + "\t");
            searchAxioms();
            System.out.println("TotalAxioms" + "\t" + this.Axioms.size());
            try {
                File LocalFile;
                String LocalIRI = "http://knowlearn.infotec.com.mx/";
                File OntFile = OWLFileFilter.saveOntology();
                System.out.println( "****** Result of OWLFileFilter " + OntFile);
                if(OntFile != null){
                    LocalFile = OntFile;
                    LocalIRI += OntFile.getName();
                }else{
                    LocalFile = new File("./owl/knOWLearn_Ontology.owl");
                    LocalIRI = "http://knowlearn.cenidet.edu.mx/ontology.owl";
                }
                System.out.println("---- Saving ontology ----");
            DomainOntology domainOntology = new DomainOntology(this.Axioms, LocalFile, LocalIRI);
            } catch (OWLOntologyCreationException ex) {
                System.err.println("Ontology can´t be created");
            } 
    }

    private void printAxioms() {
        Object[] Elements = getElements(this.Axioms);
        List<NounSynset> Synsets = (List<NounSynset>) Elements[0];
        List<OWLClass> Classes = (List<OWLClass>) Elements[1];
        for (NounSynset synset : Synsets) {
            System.out.println(" ");
            for (Axiom axiom : this.Axioms) {
                if (axiom.isTwoElementsAxiom() && (axiom.getRelation().equals(AxiomType.SubClassOf) || axiom.getRelation().equals(AxiomType.PartOf))) {
                    if (axiom.getSubject().isSynsetType()) {
                        TwoElementsAxiom axiom2 = axiom.asTwoElementsAxiom();
                        if (axiom2.getSubject().asAxiomElementSynset().getFirstElementMapped().equals(synset)) {
                            System.out.println("\t" + axiom2.getSubject().getID() + "\t" + axiom2.getRelation() + "\t" + axiom2.getObject().getID());
                        }
                    }
                }
            }
        }
        for (OWLClass Class : Classes) {
            System.out.println(" ");
            for (Axiom axiom : this.Axioms) {
                if (axiom.isTwoElementsAxiom() && (axiom.getRelation().equals(AxiomType.SubClassOf) || axiom.getRelation().equals(AxiomType.SubClassOf))) {
                    if (axiom.getSubject().isOWLClassType()) {
                        TwoElementsAxiom axiom2 = axiom.asTwoElementsAxiom();
                        if (axiom2.getSubject().asAxiomElementClass().getFirstElementMapped().equals(Class)) {
                            System.out.println("\t" + axiom2.getSubject().getID() + "\t" + axiom2.getRelation() + "\t" + axiom2.getObject().getID());
                        }
                    }
                }
            }
        }
    }

    private void getSynsetAxioms(NounSynset synset) {
        SynsetConcept SC =  new SynsetConcept(synset);
        AxiomElementSynset subject =  addClassSynsetAxiom(synset);
        for(NounSynset Hyponym : SC.getHyponyms(1)){
            AxiomElementSynset object =  addClassSynsetAxiom(Hyponym);
            addAxiom(object,subject,AxiomType.SubClassOf);
        }
        addHypernyms(subject);
    }
    
    
    private void addHypernyms(AxiomElementSynset subject){
        SynsetConcept SC =  new SynsetConcept(subject.getFirstElementMapped());
        for(NounSynset hypernym : SC.getHypernyms(1)){
            if (!WordNet.TopLevelEntities.contains(hypernym)) {
                AxiomElementSynset object = addClassSynsetAxiom(hypernym);
                addAxiom(subject, object, AxiomType.SubClassOf);
                addHypernyms(object);
            }
        }
    }
    
    private void addAxiom(AxiomElementSynset subject, AxiomElementSynset object, String axiom_type){
        if(!containsAxiom(subject,object,axiom_type)){
            this.Axioms.add(new TwoElementsAxiom(subject,object,axiom_type));
        }
    }
    
    private boolean containsAxiom(AxiomElementSynset subject, AxiomElementSynset object, String axiom_type){
        for(Axiom axiom : this.Axioms){
            if(axiom.isTwoElementsAxiom() && axiom.getRelation().equals(axiom_type)){
                if(axiom.getSubject().isSynsetType() && axiom.asTwoElementsAxiom().getObject().isSynsetType()){
                    if(axiom.getSubject().asAxiomElementSynset().getFirstElementMapped().equals(subject.getFirstElementMapped()) 
                            && axiom.asTwoElementsAxiom().getObject().getID().equals(object.getID())){
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    private AxiomElementSynset addClassSynsetAxiom(NounSynset synset){
        AxiomElementSynset subject = new AxiomElementSynset(synset,null);
        Axiom axiomclass = new Axiom(subject, null , AxiomType.Type);
        Axiom axiomlabel = new Axiom(subject, synset.getWordForms()[0], AxiomType.HasLabel);
        if(!this.Axioms.contains(axiomlabel)){
            this.Axioms.add(axiomlabel);
        }
        if(this.Axioms.contains(axiomclass)){
            for(Axiom axiom : this.Axioms){
                if(axiom.equals(axiomclass)){
                    return axiom.getSubject().asAxiomElementSynset();
                }
            }
        }
        this.Axioms.add(axiomclass);
        
        return subject;
    }
    
    private void searchAxioms() {
        for (WatsonDocument document : this.Documents) {
            try {
                OntologyManager Ontology = document.openOntology();
                if (Ontology == null) {
                    System.err.println("The ontology can't be opened");
                    continue;
                }
                for (Term term : this.Terms) {
                    if (document.getCoveredTerms().contains(term.getName())) {
                        //System.out.println(" Searching SOAxioms @" + document.getFile() + " with term " + term.getName());
                        if (term.getSelectedSynset() != null && !term.getSense().equals("None")) {
                            List<Axiom> SOAxioms = new SynsetOntologyAxioms(term.getSelectedSynset(), Ontology).getAxioms();
                            if (SOAxioms.size() > 0) {
                                //System.out.println(SOAxioms.size() + " SOAxioms founded for " + document.getFile() + " and term " + term.getName());
                                term.SynsetOntologyAxioms.addAll(SOAxioms);
                            }
                        } else {
                            OntologyConcept concept = Ontology.searchTermInOntology(term.getName());
                            if(concept.getOntClass() != null){
                                System.out.println(concept.getOntClass().getIRI().getFragment() + " founded for " + term.getName());
                            }
                        }
                    }
                }
            } catch (Exception ex) {
                System.err.println((ex.getMessage().length() > 100) ? ex.getMessage().substring(0, 99) : ex.getMessage() + "\n"
                        + ((ex.getCause().toString().length() > 100) ? ex.getCause().toString().substring(0, 99) : ex.getCause()));
                continue;
            }
        }
        deleteRepeatedAxioms();
        prugneRelevantAxioms();
    }

    private void prugneRelevantAxioms() {
        
        for (Term term : Terms) {
            //System.out.println("Prugning relevant axioms");
            if(this.Axioms == null){
                System.err.println("Prugning relevant axioms");
                this.Axioms = new ArrayList<Axiom>();
            }
            this.Axioms.addAll(term.SynsetOntologyAxioms);
            
            if (term.SynsetOntologyAxioms.size() < 1) {
                if(term.getSelectedSynset() != null){
                    getSynsetAxioms(term.getSelectedSynset());
                }
//                if (term.getSense() != null && term.getSense() != "None") {
//                    System.out.println(term.getName() + " no tiene mapps");
//                }
            }
        }
        printAxioms();
        System.out.println("\t" + this.Axioms.size() + " Axioms are founded");
        Object[] elements = getElements(this.Axioms);
        List<NounSynset> Synsets = (List<NounSynset>) elements[0];
        for (NounSynset synset : Synsets) {
//                System.out.println("Synset " + synset.getWordForms()[0]);
            List<Axiom> PartOfAxioms = new ArrayList<Axiom>();
            List<Axiom> SubClassOfAxioms = new ArrayList<Axiom>();
            for (Axiom axiom : this.Axioms) {
                if (axiom.isTwoElementsAxiom()) {
                    if (axiom.getSubject().isSynsetType()) {
                        if (axiom.getSubject().asAxiomElementSynset().getFirstElementMapped().equals(synset)) {
                            if (axiom.getRelation().equals(AxiomType.PartOf)) {
                                PartOfAxioms.add(axiom);
                            }
                            if (axiom.getRelation().equals(AxiomType.SubClassOf)) {
                                SubClassOfAxioms.add(axiom);
                            }
                        }
                    }
                }
            }
//            if (SubClassOfAxioms.size() > 1) {
                SubClassOfAxioms = removePartOfAxiomsofSubClassOfAxioms(SubClassOfAxioms, PartOfAxioms);
                System.out.println("\t" + this.Axioms.size() + " Axioms are founded after of remove partofsubclasof");
                if (SubClassOfAxioms.size() > 1) {
                    List<Axiom> toDelete = new ArrayList<Axiom>();
                    for (int i = 0; i < SubClassOfAxioms.size(); i++) {
                        List<Axiom> SuperClasses = new ArrayList<Axiom>();
                        getSuperClasses(SubClassOfAxioms.get(i), this.Axioms, SuperClasses);
                        for (Axiom SuperClass : SuperClasses) {
                            for (Axiom axiom : SubClassOfAxioms) {
                                if (!axiom.equals(SubClassOfAxioms.get(i))) {
//                                        System.out.println("Comparying " + SuperClass.asTwoElementsAxiom().getObject().getID() + " with " + axiom.asTwoElementsAxiom().getObject().getID());
                                    if (SuperClass.asTwoElementsAxiom().getObject().getID().equals(axiom.asTwoElementsAxiom().getObject().getID())) {
//                                            System.out.println("*");
                                        toDelete.add(SubClassOfAxioms.get(i));
                                    }
                                }
                            }
                        }
                    }
                    
                    this.Axioms.removeAll(toDelete);
                    System.out.println("\t" + this.Axioms.size() + " Axioms are founded later to remove inconsistent axioms");
                    SubClassOfAxioms.removeAll(toDelete);

                }
//            }
            for (Axiom SubClassAxiom : this.Axioms) {
                if (SubClassAxiom.isTwoElementsAxiom()) {
//                        System.out.println(SubClassAxiom.getSubject().getID() + "\t" + SubClassAxiom.getRelation() + "\t" + SubClassAxiom.asTwoElementsAxiom().getObject().getID());
                }
            }
        }
//            this.Axioms.addAll(term.SynsetOntologyAxioms);
//        }
    }

    private static Object[] getElements(List<Axiom> axioms) { //Object[0] = List<Synset> ; Object[1] = List<OWLClass>
        List<NounSynset> Synsets = new ArrayList<NounSynset>();
        List<OWLClass> Classes = new ArrayList<OWLClass>();
        for (Axiom axiom : axioms) {
            if (axiom.getRelation().equals(AxiomType.Type)) {
                if (axiom.getSubject().isSynsetType()) {
                    NounSynset synset = axiom.getSubject().asAxiomElementSynset().getFirstElementMapped();
                    if (!Synsets.contains(synset)) {
                        Synsets.add(synset);
                    }
                } else {
                    OWLClass Class = axiom.getSubject().asAxiomElementClass().getFirstElementMapped();
                    if (!Classes.contains(Class)) {
                        Classes.add(Class);
                    }
                }
            }
        }
        return new Object[]{Synsets, Classes};
    }

    private List<Axiom> removePartOfAxiomsofSubClassOfAxioms(List<Axiom> SubClassOfAxioms, List<Axiom> PartOfAxioms) {
        List<Axiom> toDelete = new ArrayList<Axiom>();
        for (Axiom Axiom : SubClassOfAxioms) {
            TwoElementsAxiom SubClassOfAxiom = Axiom.asTwoElementsAxiom();
            for (Axiom pAxiom : PartOfAxioms) {
                TwoElementsAxiom PartOfAxiom = pAxiom.asTwoElementsAxiom();
                if (PartOfAxiom.getSubject().getID().equals(SubClassOfAxiom.getSubject().getID())
                        && PartOfAxiom.getObject().getID().equals(SubClassOfAxiom.getObject().getID())) {
                    toDelete.add(Axiom);
                }
            }
        }
        SubClassOfAxioms.removeAll(toDelete);
        this.Axioms.removeAll(toDelete);
        return SubClassOfAxioms;
    }

    private void getSuperClasses(Axiom axiom, List<Axiom> Axioms, List<Axiom> SuperClasses) {
        TwoElementsAxiom E2Axiom = axiom.asTwoElementsAxiom();
        for (Axiom ax : Axioms) {
            if (ax.isTwoElementsAxiom() && ax.getRelation().equals(AxiomType.SubClassOf)) {
                TwoElementsAxiom ax2 = ax.asTwoElementsAxiom();
                if (E2Axiom.getObject().getID().equals(ax2.getSubject().getID())) {
                    SuperClasses.add(ax);
                    getSuperClasses(ax, Axioms, SuperClasses);
                }
            }
        }
    }

    private void deleteRepeatedAxioms() {
        for (Term term : Terms) {
            List<Axiom> toDelete = new ArrayList<Axiom>();
            for (int i = 0; i < term.SynsetOntologyAxioms.size(); i++) {
                if (term.SynsetOntologyAxioms.get(i).isTwoElementsAxiom() && !toDelete.contains(term.SynsetOntologyAxioms.get(i))) {
                    TwoElementsAxiom Axiomi = term.SynsetOntologyAxioms.get(i).asTwoElementsAxiom();
                    for (int j = i + 1; j < term.SynsetOntologyAxioms.size(); j++) {
                        if (term.SynsetOntologyAxioms.get(j).isTwoElementsAxiom()) {
                            TwoElementsAxiom Axiomj = term.SynsetOntologyAxioms.get(j).asTwoElementsAxiom();
                            if (Axiomi.getRelation().equals(Axiomj.getRelation())
                                    && Axiomi.getSubject().getID().equals(Axiomj.getSubject().getID())
                                    && Axiomi.getObject().getID().equals(Axiomj.getObject().getID())) {
                                toDelete.add(term.SynsetOntologyAxioms.get(j));
                            }
                        }
                    }
                }
            }
            term.SynsetOntologyAxioms.removeAll(toDelete);
        }
    }

    private void deleteNonRelatedsTerms() {
        List<Term> toDelete = new ArrayList<Term>();
        for (Term term : this.Terms) {
            if (term.getSense().equals("None") && term.getSynsets().size() > 0) {
                toDelete.add(term);
            }
        }
        this.Terms.removeAll(toDelete);
    }

    private void searchOntologies(){
        try {
            String[] terms = new String[this.Terms.size()];
            for (int i = 0; i < this.Terms.size(); i++) {
                terms[i] = this.Terms.get(i).getName();
            }
            SearchTerms query = new SearchTerms(terms);
            this.Documents = query.Documents;
            System.out.println("\t" + this.Documents.size() + " Ontologies are founded.");
        } catch (RemoteException ex) {
            System.err.println("Error @InitialScheme.SearchingOntologies" + ex.getMessage());
        }
    }

    private void saveDocuments() {
        List<WatsonDocument> toDelete = new ArrayList<WatsonDocument>();
        for (WatsonDocument document : this.Documents) {
            System.out.println("Saving (" + (this.Documents.indexOf(document) + 1) + "/" + this.Documents.size() + "):" + document.getDocumentURL() + "\t");
            if (!document.saveDocument()) {
                while (!document.saveNextSimilarURL() && document.getSimilarURLs().size() > 0);
                if (document.getFile() == null || "".equals(document.getFile())) {
                    toDelete.add(document);
                }
            }
        }
        this.Documents.removeAll(toDelete);
        System.out.println("\t" + this.Documents.size() + " ontologies are saved.");
        
        System.out.println("\tint NumberOfDocuments = " + this.Documents.size());
        System.out.println("\tWatsonDocument[] Documents = new WatsonDocument[NumberOfDocuments];\n");
        for(int i = 0 ; i < this.Documents.size() ; i++) {
            System.out.println("\tDocuments["+i+"] = new WatsonDocument(\""+this.Documents.get(i).getDocumentURL() +"\");");
            System.out.println("\tDocuments["+i+"].setCacheDocumentURL(\""+this.Documents.get(i).getCacheDocumentURL() +"\");");
            System.out.print("\tDocuments["+i+"].setCoveredTerms(Arrays.asList(new String[]{");
            for(int j = 0 ; j < this.Documents.get(i).getCoveredTerms().size() ; j++) {
                System.out.print("\"" + this.Documents.get(i).getCoveredTerms().get(j) + ((j < this.Documents.get(i).getCoveredTerms().size() - 1)? "\",": "\"}));"));
            }
            System.out.println("\tDocuments["+i+"].setFile(\""+this.Documents.get(i).getFile() +"\"");    
        }

        

    }

    public List<Axiom> getAxioms() {
        return Axioms;
    }
    
}
