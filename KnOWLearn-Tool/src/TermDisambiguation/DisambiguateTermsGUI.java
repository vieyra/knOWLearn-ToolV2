package TermDisambiguation;

import InitialScheme.InitialScheme;
import Utils.PythonConnection.ConnectionManager;
import InitialScheme.Sources.Term;
import InitialScheme.Sources.WordNetSources.Synset;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

public class DisambiguateTermsGUI extends javax.swing.JFrame {

    private ConnectionManager con;
    private List<Term> Terms;
    private boolean Sensesactive = false;

    public DisambiguateTermsGUI(ConnectionManager con, String MetadataTerms) {
        this.con = con;
        this.Terms = new ArrayList<Term>();
        for (String term : MetadataTerms.split("/Term/")) {
            this.Terms.add(new Term(term));
        }

        initComponents();
        setModelTerms();
    }

    private void setModelTerms() {
        DefaultListModel modelterms = new DefaultListModel();
        for (Term term : this.Terms) {
            modelterms.addElement(term.getName());
        }
        this.TermsList.setModel(modelterms);
    }

    @SuppressWarnings("unchecked")
   // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
   private void initComponents() {

      jLabel1 = new javax.swing.JLabel();
      jLabel2 = new javax.swing.JLabel();
      jScrollPane2 = new javax.swing.JScrollPane();
      SensesList1 = new javax.swing.JList();
      jScrollPane1 = new javax.swing.JScrollPane();
      TermsList = new javax.swing.JList();
      jButton1 = new javax.swing.JButton();

      setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
      setTitle("::KnOWLearn:: - Terms Disambiguation");
      addWindowListener(new java.awt.event.WindowAdapter() {
         public void windowClosing(java.awt.event.WindowEvent evt) {
            formWindowClosing(evt);
         }
         public void windowClosed(java.awt.event.WindowEvent evt) {
            formWindowClosed(evt);
         }
      });

      jLabel1.setText("Terms:");

      jLabel2.setText("Senses:");

      SensesList1.addMouseListener(new java.awt.event.MouseAdapter() {
         public void mouseClicked(java.awt.event.MouseEvent evt) {
            SensesList1MouseClicked(evt);
         }
      });
      SensesList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
         public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            SensesList1ValueChanged(evt);
         }
      });
      jScrollPane2.setViewportView(SensesList1);

      TermsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
         public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
            TermsListValueChanged(evt);
         }
      });
      TermsList.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
         public void propertyChange(java.beans.PropertyChangeEvent evt) {
            TermsListPropertyChange(evt);
         }
      });
      jScrollPane1.setViewportView(TermsList);

      jButton1.setText("Get Initial Scheme");
      jButton1.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(java.awt.event.ActionEvent evt) {
            jButton1ActionPerformed(evt);
         }
      });

      javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
      getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
            .addGap(36, 36, 36)
            .addComponent(jLabel1)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel2)
            .addGap(175, 175, 175))
         .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addGap(79, 79, 79)
                  .addComponent(jButton1)
                  .addGap(0, 298, Short.MAX_VALUE))
               .addGroup(layout.createSequentialGroup()
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 455, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addGap(18, 18, 18))))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
         .addGroup(layout.createSequentialGroup()
            .addGap(32, 32, 32)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
               .addComponent(jLabel1)
               .addComponent(jLabel2))
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
               .addGroup(layout.createSequentialGroup()
                  .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE)
                  .addContainerGap())
               .addGroup(layout.createSequentialGroup()
                  .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                  .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                  .addComponent(jButton1)
                  .addGap(35, 35, 35))))
      );

      pack();
   }// </editor-fold>//GEN-END:initComponents

    private void TermsListPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_TermsListPropertyChange
    }//GEN-LAST:event_TermsListPropertyChange

    private void TermsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_TermsListValueChanged
        if (this.TermsList.getSelectedIndex() != -1) {
            showSynsetsForSelectedTerm();
        }
    }//GEN-LAST:event_TermsListValueChanged

    private void SensesList1ValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_SensesList1ValueChanged
        if (this.Sensesactive && this.TermsList.getSelectedIndex() != -1) {
            Term term = this.Terms.get(this.TermsList.getSelectedIndex());
            String selectedValue = this.SensesList1.getSelectedValue().toString();
            if (!selectedValue.equals("") && selectedValue.equals("None")) {
                String newtermsense = selectedValue.split(" : ")[0];
                term.setSense(newtermsense);
                term.setVerified(true);
            }
        }
    }//GEN-LAST:event_SensesList1ValueChanged

    private void SensesList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_SensesList1MouseClicked
        if (this.TermsList.getSelectedIndex() != -1) {
            Term term = this.Terms.get(this.TermsList.getSelectedIndex());
            String selectedValue = this.SensesList1.getSelectedValue().toString();
            if (!selectedValue.equals("") && !selectedValue.equals("None")) {
                String newtermsense = selectedValue.split(" : ")[0];
                term.setSense(newtermsense);
                term.setVerified(true);
            }
        }
    }//GEN-LAST:event_SensesList1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            for (Term term : this.Terms) {
                //breast/TermName/breast.n.02/TermSense/breast.n.01/SynsetName/the front of the trunk from the neck to the abdomen/SynsetDefinition//Synset/breast.n.02/SynsetName/either of two soft fleshy milk-secreting glandular organs on the chest of a woman/SynsetDefinition//Synset/breast.n.03/SynsetName/meat carved from the breast of a fowl/SynsetDefinition//Synset/breast.n.04/SynsetName/the part of an animal's body that corresponds to a person's chest/SynsetDefinition//Synset/
                System.out.print(term.getName() + "/TermName/"
                        + term.getSense() + "/TermSense/");
                for (Synset synset : term.getSynsets()) {
                    System.out.print(synset.getName()+"/SynsetName/" + synset.getDefinition() + "/SynsetDefinition//Synset/");
                }
                System.out.print("/Term/\n");
            }
            InitialScheme iScheme = new InitialScheme(this.Terms, con);
            this.dispose();
        } catch (RemoteException ex) {
            System.err.println("*" + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Ha ocurrido un error. " + ex.getMessage() + ". " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

   private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
      // TODO add your handling code here:
   }//GEN-LAST:event_formWindowClosing

   private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
      // TODO add your handling code here:
      con.close();
      
   }//GEN-LAST:event_formWindowClosed

    private void showSynsetsForSelectedTerm() {
        this.Sensesactive = false;
        Term term = this.Terms.get(this.TermsList.getSelectedIndex());
        DefaultListModel modelterms = new DefaultListModel();
        int selectedsynset = -1;
        for (Synset synset : term.getSynsets()) {
            modelterms.addElement(synset.getName() + " : " + synset.getDefinition());
            if (term.getSense().equals(synset.getName())) {
                selectedsynset = term.getSynsets().indexOf(synset);
            }
        }
        this.SensesList1.setModel(modelterms);
        if (selectedsynset != -1) {
            this.SensesList1.setSelectedIndex(selectedsynset);
        }
        this.Sensesactive = true;
    }
   // Variables declaration - do not modify//GEN-BEGIN:variables
   private javax.swing.JList SensesList1;
   private javax.swing.JList TermsList;
   private javax.swing.JButton jButton1;
   private javax.swing.JLabel jLabel1;
   private javax.swing.JLabel jLabel2;
   private javax.swing.JScrollPane jScrollPane1;
   private javax.swing.JScrollPane jScrollPane2;
   // End of variables declaration//GEN-END:variables
}
