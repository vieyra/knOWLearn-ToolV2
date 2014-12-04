package InitialScheme.WatsonSearch;

/**
 * @author Samuel Vieyra
 * samuel.vieyra@infotec.com.mx
 */
public class SearchConfiguration {

   /* Default configurations */
   
   /*    Entities */
   private boolean properties = false;
   private boolean classes = true;
   private boolean individuals = false;
   private boolean literals = false;
   
   /*    Match     */
   private boolean exactMatch = true;
   private boolean tokenMatch = false;
   
   /*    Scope    */
   private boolean localName = true;
   private boolean label = false;
   private boolean comment = false;
   
   public SearchConfiguration() {
      
   }

   public boolean isProperties() {
      return properties;
   }

   public void setProperties(boolean properties) {
      this.properties = properties;
   }

   public boolean isClasses() {
      return classes;
   }

   public void setClasses(boolean classes) {
      this.classes = classes;
   }

   public boolean isIndividuals() {
      return individuals;
   }

   public void setIndividuals(boolean individuals) {
      this.individuals = individuals;
   }

   public boolean isLiterals() {
      return literals;
   }

   public void setLiterals(boolean literals) {
      this.literals = literals;
   }

   public boolean isExactMatch() {
      return exactMatch;
   }

   public void setExactMatch(boolean exactMatch) {
      this.exactMatch = exactMatch;
   }

   public boolean isTokenMatch() {
      return tokenMatch;
   }

   public void setTokenMatch(boolean tokenMatch) {
      this.tokenMatch = tokenMatch;
   }

   public boolean isLocalName() {
      return localName;
   }

   public void setLocalName(boolean localName) {
      this.localName = localName;
   }

   public boolean isLabel() {
      return label;
   }

   public void setLabel(boolean label) {
      this.label = label;
   }

   public boolean isComment() {
      return comment;
   }

   public void setComment(boolean comment) {
      this.comment = comment;
   }
   
}
