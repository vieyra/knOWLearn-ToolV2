package InitialScheme.Sources;

import InitialScheme.Sources.OntologySources.OntologyManager;
import InitialScheme.WatsonSearch.OntologySearch;
import java.io.*;
import java.net.URL;
import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.List;

public class WatsonDocument {

    private String DocumentURL;
    private String CacheDocumentURL;
    private List<String> coveredTerms;
    private List<String> similarURLs;
    private String file;
    
    public WatsonDocument(String DocumentURL) {
        this.DocumentURL = DocumentURL;
        this.coveredTerms = new ArrayList<String>();
        this.similarURLs = new ArrayList<String>();
    }

    public void addTerm(String Class) {
        if (!this.coveredTerms.contains(Class)) {
            this.coveredTerms.add(Class);
        }
    }

    public void addSimilarURL(String Class) {
        if (!this.similarURLs.contains(Class)) {
            this.similarURLs.add(Class);
        }
    }

    public String getCacheDocumentURL() {
        return CacheDocumentURL;
    }

    public String getDocumentURL() {
        return DocumentURL;
    }

    public List<String> getCoveredTerms() {
        return coveredTerms;
    }

    public List<String> getSimilarURLs() {
        return similarURLs;
    }

    public void setCacheDocumentURL(String CacheDocumentURL) {
       this.CacheDocumentURL = CacheDocumentURL;
    }
    
    public void setCacheDocumentURL() {
        OntologySearch os = new OntologySearch();
        try {
            this.CacheDocumentURL = os.cacheLocation(this.DocumentURL);
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
            System.out.println(ex.getCause());
        }
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public void setCoveredTerms(List<String> coveredTerms) {
        this.coveredTerms = coveredTerms;
    }
    
    public boolean setNextSimilarURL(){
        if(this.similarURLs.size() > 0){
            this.DocumentURL = this.similarURLs.get(0);
//            System.out.println(this.DocumentURL);
            this.similarURLs.remove(0);
            this.getCacheDocumentURL();
            return true;
        }
        return false;
    }
    
    public boolean saveNextSimilarURL(){
        return this.setNextSimilarURL() && this.saveDocument();
    }
    
    
    public boolean saveDocument(){
                String filename = Utils.URLSource.getSourceName(this.getDocumentURL());
        if(!filename.contains(".")){
            filename += ".owl";
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            BufferedReader in;
            in = new BufferedReader(new InputStreamReader(new URL(this.getCacheDocumentURL()).openStream()));
            String line;
            while((line = in.readLine()) != null){
                out.write(line);
            }
            in.close();
            out.close();
//            System.out.println(this.CacheDocumentURL + " has been saved");
            this.setFile(filename);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    public boolean saveDocument(boolean fromOriginalURL){
//        System.out.println("Trying saving " + this.getCacheDocumentURL());
        String filename = Utils.URLSource.getSourceName(this.getDocumentURL());
        if(!filename.contains(".")){
            filename += ".owl";
        }
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filename));
            BufferedReader in;
            if (fromOriginalURL) {
                //System.out.println("***" + this.getDocumentURL());
                in = new BufferedReader(new InputStreamReader(new URL(this.getDocumentURL()).openStream()));
            } else {
                in = new BufferedReader(new InputStreamReader(new URL(this.getCacheDocumentURL()).openStream()));
            }
            String line;
            while((line = in.readLine()) != null){
                out.write(line);
            }
            in.close();
            out.close();
//            System.out.println(this.CacheDocumentURL + " has been saved");
            this.setFile(filename);
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    
    public OntologyManager openOntology(){
        OntologyManager Ontology = new OntologyManager(this.file, true);
        if(!Ontology.isOpened()){
            if (this.saveDocument(true)) {
                OntologyManager O = new OntologyManager(this.file, true);
                if(O.isOpened()){
                    return O;
                }
            }
            return null;
        }
        return Ontology;
    }
    
    
    
    public static void main(String[] args){
        WatsonDocument d = new WatsonDocument("http://reliant.teknowledge.com/DAML/SUMO.owl");
        d.setCacheDocumentURL();
        System.out.println(d.getCacheDocumentURL());
    }
    
}