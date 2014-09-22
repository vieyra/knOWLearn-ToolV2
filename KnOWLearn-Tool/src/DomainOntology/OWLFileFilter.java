/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package DomainOntology;

import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


public class OWLFileFilter extends FileFilter {

    public static File saveOntology() {
        JFileChooser chooser = new JFileChooser(System.getProperty("user.dir"));
        OWLFileFilter filter = new OWLFileFilter();
        chooser.setFileFilter(filter);
        int returnVal = chooser.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile() != null) {
               System.out.println("\t****" + chooser.getSelectedFile().getAbsolutePath());
                return chooser.getSelectedFile();
            }
        }
        return null;
    }

    @Override
    public boolean accept(File f) {
        String name = f.getName().toLowerCase();
        return name.endsWith("owl");
    }

    @Override
    public String getDescription() {
        return "Ontology Web Language (.owl)";
    }
}
