
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
        
  Integer[] tamanosDisponibles = {8, 9, 10, 11, 12, 14, 16, 18, 20, 24, 28, 32, 36, 42, 48, 64, 92, 144, 190, 240, 300};
        comboTamano = new JComboBox<Integer>(tamanosDisponibles);
        comboTamano.setSelectedItem(11);
        comboTamano.setMaximumSize(new Dimension(80, 28));

        btnNegrita=new JToggleButton("B");
        btnCursiva=new JToggleButton("I");
        btnSubrayado=new JToggleButton("U");
        btnNegrita.setFont(new Font("Serif", Font.BOLD, 14));
        btnCursiva.setFont(new Font("Serif", Font.ITALIC, 14));
        btnSubrayado.setFont(new Font("Serif", Font.PLAIN, 14));

        btnColor = new JButton("Color");
        btnColor.setBackground(colorActual);
        btnColor.setForeground(Color.WHITE);
        btnColor.setOpaque(true);

        areaEditor=new JTextPane();
        areaEditor.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        etiquetaEstado=new JLabel("Listo  |  Crea o abre un archivo .docx");
        etiquetaEstado.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

        setJMenuBar(construirMenuBar());
        add(construirBarraHerramientas(), BorderLayout.NORTH);
        add(new JScrollPane(areaEditor), BorderLayout.CENTER);
        add(etiquetaEstado, BorderLayout.SOUTH);

        registrarEventos();
        aplicarFormato();
        actualizarTitulo();
    }
  private JMenuBar construirMenuBar(){
        JMenuBar barraMenu=new JMenuBar();
        JMenu menuArchivo=new JMenu("Archivo");

        JMenuItem itemNuevo=new JMenuItem("Nuevo");
        JMenuItem itemAbrir= new JMenuItem("Abrir .docx");
        JMenuItem itemGuardar = new JMenuItem("Guardar");
        JMenuItem itemGuardarAs = new JMenuItem("Guardar como...");
        JMenuItem itemSalir = new JMenuItem("Salir");

        itemNuevo.addActionListener(e-> nuevoDocumento());
        itemAbrir.addActionListener(e-> abrirArchivo());
        itemGuardar.addActionListener(e-> guardarArchivo());
        itemGuardarAs.addActionListener(e-> guardarComo());
        itemSalir.addActionListener(e-> dispose());

        menuArchivo.add(itemNuevo);
        menuArchivo.add(itemAbrir);
        menuArchivo.add(itemGuardar);
        menuArchivo.add(itemGuardarAs);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);

        barraMenu.add(menuArchivo);
        return barraMenu;
    }
private JToolBar construirBarraHerramientas(){
        JToolBar barra = new JToolBar();
        barra.setFloatable(false);

        barra.add(new JLabel(" Fuente: "));
        barra.add(comboFuente);
        barra.addSeparator(new Dimension(6,0));
        barra.add(new JLabel("Tamaño: "));
        barra.add(comboTamano);
        barra.addSeparator(new Dimension(6,0));
        barra.add(btnNegrita);
        barra.add(btnCursiva);
        barra.add(btnSubrayado);
        barra.addSeparator(new Dimension(6,0));
        barra.add(btnColor);
        barra.addSeparator(new Dimension(10,0));

        JButton btnTabla = new JButton("+ Tabla");
        btnTabla.addActionListener(e -> insertarTabla());
        barra.add(btnTabla);

        return barra;
    }
 private void registrarEventos(){
        comboFuente.addActionListener(e-> aplicarFormato());
        comboTamano.addActionListener(e-> aplicarFormato());
        btnNegrita.addActionListener(e-> aplicarFormato());
        btnCursiva.addActionListener(e-> aplicarFormato());
        btnSubrayado.addActionListener(e-> aplicarFormato());

        btnColor.addActionListener(e -> {
            Color colorElegido = JColorChooser.showDialog(this, "Selecciona un color", colorActual);
            if (colorElegido != null) {
                colorActual = colorElegido;
                btnColor.setBackground(colorElegido);
                btnColor.setForeground(colorTextoContraste(colorElegido));
                aplicarFormato();
            }
        });

        areaEditor.addCaretListener(e -> refrescarBarra());
    }
private void aplicarFormato(){
        if (actualizandoBarra) return;

        EstiloTexto estilo=leerEstiloDeBarra();
        MutableAttributeSet attrs = servicioDocx.estiloAAtributos(estilo);
        StyledDocument docSwing = areaEditor.getStyledDocument();

        int inicio = areaEditor.getSelectionStart();
        int fin    = areaEditor.getSelectionEnd();

        if (fin > inicio) {
            docSwing.setCharacterAttributes(inicio, fin - inicio, attrs, false);
            etiquetaEstado.setText("Formato aplicado a la selección.");
        } else {
            etiquetaEstado.setText("Formato listo para escribir.");
        }

        areaEditor.setCharacterAttributes(attrs, false);
        areaEditor.requestFocusInWindow();
    }

    private void refrescarBarra(){
        StyledDocument docSwing = areaEditor.getStyledDocument();
        if (docSwing.getLength() == 0) return;

        int posicion = areaEditor.getSelectionStart();
        if (posicion >= docSwing.getLength()) posicion = docSwing.getLength() - 1;
        if (posicion < 0) posicion = 0;

        EstiloTexto estilo = servicioDocx.leerEstiloSwing(
                docSwing.getCharacterElement(posicion).getAttributes());

        actualizandoBarra = true;
        try{
            comboFuente.setSelectedItem(estilo.getFuente());
            comboTamano.setSelectedItem(estilo.getTamanoPt());
            btnNegrita.setSelected(estilo.isNegrita());
            btnCursiva.setSelected(estilo.isCursiva());
            btnSubrayado.setSelected(estilo.isSubrayado());
            colorActual = estilo.getColor();
            btnColor.setBackground(colorActual);
            btnColor.setForeground(colorTextoContraste(colorActual));
        } finally{
            actualizandoBarra = false;
        }
    }

    private EstiloTexto leerEstiloDeBarra(){
        String fuente = comboFuente.getSelectedItem() != null
                ? comboFuente.getSelectedItem().toString() : "Calibri";
        int tamano    = comboTamano.getSelectedItem() instanceof Number
                ? ((Number) comboTamano.getSelectedItem()).intValue() : 11;
        return new EstiloTexto(fuente, tamano, colorActual,
                btnNegrita.isSelected(), btnCursiva.isSelected(), btnSubrayado.isSelected());
    }

 private void insertarTabla(){
        String[][] datosTabla = DialogoTabla.mostrar(this);
        if (datosTabla == null) return;

        listaTablasGuardar.add(datosTabla);

        StringBuilder representacion = new StringBuilder("\n");
        for (String[] fila : datosTabla) {
            representacion.append("| ");
            for (String celda : fila) {
                representacion.append(celda).append(" | ");
            }
            representacion.append("\n");
        }
        representacion.append("\n");

        try {
            EstiloTexto estiloTabla = new EstiloTexto("Courier New", 11,
                    new Color(30, 90, 170), true, false, false);
            StyledDocument docSwing = areaEditor.getStyledDocument();
            docSwing.insertString(docSwing.getLength(),
                    representacion.toString(),
                    servicioDocx.estiloAAtributos(estiloTabla));
            etiquetaEstado.setText("Tabla insertada.");
        } catch (javax.swing.text.BadLocationException ex) {
            mostrarError("No se pudo insertar la tabla en el editor.");
        }
    }
 private void nuevoDocumento(){
        int respuesta = JOptionPane.showConfirmDialog(this,
                "¿Descartar el contenido actual y comenzar uno nuevo?",
                "Nuevo documento", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION) return;

        areaEditor.setText("");
        listaTablasGuardar.clear();
        archivoActual = null;
        actualizarTitulo();
        etiquetaEstado.setText("Documento nuevo.");
    }

    private void abrirArchivo(){
        JFileChooser selector = crearSelectorDocx();
        if (selector.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File archivo = selector.getSelectedFile();
        if (archivo == null) return;

        try {
            servicioDocx.abrirDocx(archivo, areaEditor);
            archivoActual = archivo;
            listaTablasGuardar.clear();
            actualizarTitulo();
            etiquetaEstado.setText("Archivo abierto: " + archivo.getName());
        }catch (IOException ex){
            mostrarError("No se pudo abrir el archivo.\n" + ex.getMessage());
        }
    }

    private void guardarArchivo(){
        if (archivoActual == null){ guardarComo(); return; }
        guardarEnRuta(archivoActual);
    }

    private void guardarComo(){
        JFileChooser selector = crearSelectorDocx();
        if (selector.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File archivo = selector.getSelectedFile();
        if (archivo == null) return;

        if (!archivo.getName().toLowerCase().endsWith(".docx")) {
            archivo = new File(archivo.getAbsolutePath() + ".docx");
        }
        guardarEnRuta(archivo);
    }

    private void guardarEnRuta(File archivo){
        try{
            servicioDocx.guardarDocx(areaEditor, listaTablasGuardar, archivo);
            archivoActual = archivo;
            actualizarTitulo();
            etiquetaEstado.setText("Guardado: " + archivo.getName());
        }catch (IOException ex){
            mostrarError("No se pudo guardar el archivo.\n" + ex.getMessage());
        }
    }
    
    private JFileChooser crearSelectorDocx() {
        JFileChooser selector = new JFileChooser();
        selector.setFileFilter(new FileNameExtensionFilter("Documento Word (*.docx)", "docx"));
        selector.setAcceptAllFileFilterUsed(false);
        return selector;
    }

    private Color colorTextoContraste(Color fondo) {
        int luminancia = (int) (0.299 * fondo.getRed()
                               + 0.587 * fondo.getGreen()
                               + 0.114 * fondo.getBlue());
        return luminancia > 150 ? Color.BLACK : Color.WHITE;
    }

    private void actualizarTitulo() {
        setTitle(archivoActual == null
                ? "Editor DOCX  —  Sin título"
                : "Editor DOCX  —  " + archivoActual.getName());
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        etiquetaEstado.setText("Error: " + mensaje.replace('\n', ' '));
    }
}



