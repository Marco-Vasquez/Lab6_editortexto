/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_sem7_editor;
import javax.swing.SwingUtilities;
/**
 *
 * @author andres
 */
public class Lab_sem7_editor {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new VentanaEditor().setVisible(true);
            }
        });
    }
    
}
