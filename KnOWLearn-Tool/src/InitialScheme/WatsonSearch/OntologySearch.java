package InitialScheme.WatsonSearch;

import java.rmi.RemoteException;
import javax.xml.rpc.ServiceException;

import uk.ac.open.kmi.watson.clientapi.OntologySearchServiceLocator;
import uk.ac.open.kmi.watson.clientapi.WatsonService;

public class OntologySearch {

    private uk.ac.open.kmi.watson.clientapi.OntologySearch os;

    public OntologySearch() {
        OntologySearchServiceLocator locator = new OntologySearchServiceLocator();
        try {
            os = locator.getUrnOntologySearch();
        } catch (ServiceException e) {
            System.err.print(e.getMessage());
            System.err.println(e.getCause());
        }
    }

    public String[] search(String[] params) throws RemoteException {
        return os.getSemanticContentByKeywords(params);
    }

    public String[] searchClasses(String[] params) throws RemoteException {
        return os.getSemanticContentByKeywordsWithRestrictions(params,
                WatsonService.LOCAL_NAME + WatsonService.LABEL, WatsonService.CLASS, WatsonService.EXACT_MATCH);
    }

    public String[] searchIndividuals(String[] params) throws RemoteException {
        return os.getSemanticContentByKeywordsWithRestrictions(params,
                WatsonService.LOCAL_NAME + WatsonService.LABEL, WatsonService.INDIVIDUAL, WatsonService.EXACT_MATCH);
    }

    public String[] rsearch(String[] params) throws RemoteException {
        return os.getSemanticContentByKeywordsWithRestrictions(params,
                WatsonService.LOCAL_NAME + WatsonService.LABEL, WatsonService.CLASS, WatsonService.EXACT_MATCH);

    }

    public String[] rsearchp(String[] params) throws RemoteException {
        return os.getSemanticContentByKeywordsWithRestrictionsPaginated(params,
                WatsonService.LOCAL_NAME + WatsonService.LABEL + WatsonService.COMMENT, WatsonService.PROPERTY + WatsonService.INDIVIDUAL, WatsonService.TOKEN_MATCH, 0, 11);
    }

    public String cacheLocation(String url) throws RemoteException {
        return os.getCacheLocation(url);
    }

    public String[] bestCoverage(String[] params) throws RemoteException {
        return os.getBestCoverageWithRestrictions(params, WatsonService.LOCAL_NAME + WatsonService.LABEL, WatsonService.CLASS, WatsonService.TOKEN_MATCH);
    }
}