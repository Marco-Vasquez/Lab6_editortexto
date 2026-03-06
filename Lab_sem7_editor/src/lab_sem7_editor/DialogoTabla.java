/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_sem7_editor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author andres
 */
public class DialogoTabla extends JDialog{
    private final JSpinner spinnerFilas;
    private final JSpinner spinnerColumnas;
    private DefaultTableModel modeloTabla;
    private JTable tablaVista;
    private boolean aceptado=false;
    public DialogoTabla(Frame ventanaPadre){
        super(ventanaPadre, "Crear/Editar Tabla", true);
        setLayout(new BorderLayout(8, 8));
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        spinnerFilas=new JSpinner(new SpinnerNumberModel(2, 1, 40, 1));
        spinnerColumnas=new JSpinner(new SpinnerNumberModel(2, 1, 20, 1));
        JPanel panelControles=new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelControles.setBorder(BorderFactory.createEmptyBorder(8, 8, 0, 8));
        panelControles.add(new JLabel("Filas: "));
        panelControles.add(spinnerFilas);
        panelControles.add(new JLabel("Columnas: "));
        panelControles.add(spinnerColumnas);
        JButton btnActualizar=new JButton("Actualizar grilla");
        btnActualizar.addActionListener(e->actualizarGrilla());
        panelControles.add(btnActualizar);
        add(panelControles, BorderLayout.NORTH);
        modeloTabla=new DefaultTableModel(2, 2);
        tablaVista=new JTable(modeloTabla);
        tablaVista.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        tablaVista.setRowHeight(24);
        JScrollPane scroll=new JScrollPane(tablaVista);
        scroll.setPreferredSize(new Dimension(500, 250));
        scroll.setBorder(BorderFactory.createTitledBorder("Datos de la tabla"));
        add(scroll, BorderLayout.CENTER);
        JPanel panelBotones=new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnAceptar=new JButton("Aceptar");
        JButton btnCancelar=new JButton("Cancelar");
        panelBotones.add(btnAceptar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
        btnAceptar.addActionListener(e->{ 
            aceptado=true;
            dispose(); 
        });
        btnCancelar.addActionListener(e->dispose());
        pack();
        setLocationRelativeTo(ventanaPadre);
        
    }
    private List<List<String>> leerDatosActuales() {
        List<List<String>> datos = new ArrayList<List<String>>();
        for (int fila=0;fila<modeloTabla.getRowCount();fila++) {
            List<String> filaDatos=new ArrayList<String>();
            for (int columna=0;columna<modeloTabla.getColumnCount();columna++) {
                Object valor=modeloTabla.getValueAt(fila,columna);
                if (valor==null) {
                    filaDatos.add("");
                } else {
                    filaDatos.add(valor.toString());
                }
            }
            datos.add(filaDatos);
        }
        return datos;
    }
    private void actualizarGrilla(){
        int filas, columnas;
        filas=(int) spinnerFilas.getValue();
        columnas=(int) spinnerColumnas.getValue();
        List<List<String>> datosAnteriores = leerDatosActuales();
        modeloTabla.setRowCount(filas);
        modeloTabla.setColumnCount(columnas);
        for(int fila=0;fila<filas;fila++){
            for(int columna=0;columna<columnas;columna++){
                String valor="";
                if(fila<datosAnteriores.size() && columna<datosAnteriores.get(fila).size()){
                    valor=datosAnteriores.get(fila).get(columna);
                }
                modeloTabla.setValueAt(valor,fila,columna);
            }
        }
    }
    private String[][] obtenerDatos(){
        int filas=modeloTabla.getRowCount();
        int columnas=modeloTabla.getColumnCount();
        String[][] datos=new String[filas][columnas];
        for(int fila=0;fila<filas;fila++){
            for(int columna=0;columna<columnas;columna++){
                Object valor=modeloTabla.getValueAt(fila, columna);
                datos[fila][columna]=valor==null ? "":valor.toString();
            }
        }
        return datos;
    }
    public static String[][] mostrar(Frame padre) {
        DialogoTabla dialogo=new DialogoTabla(padre);
        dialogo.setVisible(true);
        if (!dialogo.aceptado) return null;
        return dialogo.obtenerDatos();
    }
}
