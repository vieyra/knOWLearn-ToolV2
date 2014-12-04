package Utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URLSource {

    public static String getSourceName(String url) {
        Pattern regex = Pattern.compile("http://([^/]*).*[/#]([^/?#]+)[?]*.*");
        Matcher regexMatcher = regex.matcher(url);
        while (regexMatcher.find()) {
            return regexMatcher.group(2);
        }
        return "";
    }

    public static void main(String[] args) {
        System.out.println(getSourceName("http://www.berkeleybop.org/ontologies/obo-all/mesh/mesh.owl"));
    }
}