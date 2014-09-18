package InitialScheme.WatsonSearch;

import InitialScheme.Sources.WatsonDocument;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import Utils.URLSource;
import Utils.string;

public class SearchTerms {

    private OntologySearch os;
    private List<String> URLOntologies;
    public List<String> Classes;
    public List<WatsonDocument> Documents;
    private String[] Terms;

    public SearchTerms(String[] Terms) throws RemoteException {
        this.os = new OntologySearch();
        this.URLOntologies = new ArrayList<String>();
        this.Documents = new ArrayList<WatsonDocument>();
        this.Classes = new ArrayList<String>();
        this.Terms = Terms;
        getClasses();
        removeOntologieswith2Terms();
        removeRepeatedOntologies();
        getWatsonCacheURLs();
    }

    private void getClasses() throws RemoteException {
        List<String> TermsSets = getSubsets(this.Terms);
        for (String TermsSet : TermsSets) {
            String[] params = TermsSet.split(" ");
            String[] results = os.searchClasses(params);
            for (String result : results) {
                if (!this.URLOntologies.contains(result)) {
                    this.URLOntologies.add(result);
                    WatsonDocument doc = new WatsonDocument(result);
                    for (String param : params) {
                        doc.addTerm(param);
                    }
                    this.Documents.add(doc);
                } else {
                    int index = this.URLOntologies.indexOf(result);
                    WatsonDocument doc = this.Documents.get(index);
                    for (String param : params) {
                        doc.addTerm(param);
                    }
                }
            }
        }
    }

    private List<String> getSubsets(String[] terms) {
        List<String> subsets = new ArrayList();
        int n = terms.length;
        for (int i = 0; i < (n - 1); i++) {
            for (int j = i + 1; j < n; j++) {
                if (terms[i] != terms[j]) {
                    subsets.add((terms[i] + ";" + terms[j]).replace(' ', '_').replace(';', ' '));
                }
            }
        }
        return subsets;
    }

    private void removeOntologieswith2Terms() {
        List<WatsonDocument> toRemove = new ArrayList();
        for (WatsonDocument d : this.Documents) {
            if (d.getCoveredTerms().size() < 3) {
                toRemove.add(d);
            }
        }
        this.Documents.removeAll(toRemove);
    }

    private void removeRepeatedOntologies() {
        List<WatsonDocument> toRemove = new ArrayList<WatsonDocument>();
        for (int i = 0; i < this.Documents.size() - 1; i++) {
            for (int j = i + 1; j < this.Documents.size(); j++) {
                if (this.Documents.get(i).getCoveredTerms().containsAll(this.Documents.get(j).getCoveredTerms())) {
                    if (string.JaroWinklerDistance(
                            URLSource.getSourceName(this.Documents.get(i).getDocumentURL().toLowerCase()),
                            URLSource.getSourceName(this.Documents.get(j).getDocumentURL().toLowerCase()))
                            > 0.8) {
                        if (!toRemove.contains(j)) {
                            if (toRemove.contains(i)) {
                                int similar = Integer.parseInt(this.Documents.get(i).getSimilarURLs().get(0));
                                this.Documents.get(j).getSimilarURLs().add(similar + "");
                                this.Documents.get(similar).getSimilarURLs().add(
                                        this.Documents.get(j).getDocumentURL());
                            } else {
                                this.Documents.get(j).getSimilarURLs().add(i + "");
                                this.Documents.get(i).getSimilarURLs().add(
                                        this.Documents.get(j).getDocumentURL());
                            }

                            toRemove.add(this.Documents.get(j));
                        }
                    }
                }
            }
        }
        this.Documents.removeAll(toRemove);
    }

    private void getWatsonCacheURLs() {
        for (WatsonDocument d : this.Documents) {
            d.setCacheDocumentURL();
        }
    }
}