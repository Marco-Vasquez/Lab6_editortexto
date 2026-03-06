
package lab_sem7_editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyledDocument;


public class VentanaEditor extends JFrame {

    private final JComboBox<String>  comboFuente;
    private final JComboBox<Integer> comboTamano;
    private final JToggleButton btnNegrita;
    private final JToggleButton btnCursiva;
    private final JToggleButton btnSubrayado;
    private final JButton btnColor;
    private final JLabel etiquetaEstado;
    private final JTextPane areaEditor;
    private final ServicioDocx  servicioDocx;

    private File archivoActual;
    private Color colorActual;
    private boolean actualizandoBarra;
    private final List<String[][]> listaTablasGuardar;
    
    
  public VentanaEditor() {
        super("Editor de Texto DOCX");
        this.servicioDocx=new ServicioDocx();
        this.colorActual=Color.BLACK;
        this.actualizandoBarra=false;
        this.listaTablasGuardar=new ArrayList<String[][]>();

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 680);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] fuentesDisponibles = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        comboFuente = new JComboBox<String>(fuentesDisponibles);
        comboFuente.setSelectedItem("Calibri");
        comboFuente.setMaximumSize(new Dimension(200, 28));
        comboFuente.setRenderer(new javax.swing.DefaultListCellRenderer() {
            @Override
            public java.awt.Component getListCellRendererComponent(
                    javax.swing.JList<?> lista, Object valor, int indice,
                    boolean seleccionado, boolean tieneFoco) {
                super.getListCellRendererComponent(lista, valor, indice, seleccionado, tieneFoco);
                if (valor != null) {
                    try { setFont(new Font(valor.toString(), Font.PLAIN, 14)); }
                    catch (Exception e) {  }
                }
                return this;
            }
        });
        

