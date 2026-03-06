/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lab_sem7_editor;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

public class ServicioDocx {
    public void guardarDocx(JTextPane editor, List<String[][]> listaTablasExtra, File archivo) throws IOException {
        XWPFDocument documentoPoi = new XWPFDocument();
        StyledDocument docSwing = editor.getStyledDocument();
        Element raiz = docSwing.getDefaultRootElement();
        for (int control = 0; control < raiz.getElementCount(); control++) {
            Element parrafo = raiz.getElement(control);
            int inicio = parrafo.getStartOffset();
            int fin = parrafo.getEndOffset();
            String textoParrafo = "";
            try {
                textoParrafo = docSwing.getText(inicio, fin - inicio);
            } catch (BadLocationException e) {
                textoParrafo = "";
            }
            if (textoParrafo.endsWith("\n")) {
                textoParrafo = textoParrafo.substring(0, textoParrafo.length() - 1);
            }
            XWPFParagraph parrafoPoi = documentoPoi.createParagraph();
            int cursor = inicio;
            while (cursor < fin) {
                Element elementoChar = docSwing.getCharacterElement(cursor);
                int runInicio = Math.max(cursor, elementoChar.getStartOffset());
                int runFin = Math.min(fin, elementoChar.getEndOffset());
                if (runFin <= runInicio) break;
                String textoRun = "";
                try {
                    textoRun = docSwing.getText(runInicio, runFin - runInicio);
                } catch (BadLocationException e) {
                    textoRun = "";
                }
                if (textoRun.endsWith("\n")) {
                    textoRun = textoRun.substring(0, textoRun.length() - 1);
                }
                if (!textoRun.isEmpty()) {
                    EstiloTexto estilo = leerEstiloSwing(elementoChar.getAttributes());
                    XWPFRun runPoi = parrafoPoi.createRun();
                    aplicarEstiloARun(runPoi, estilo, textoRun);
                }
                cursor = runFin;
            }
        }
        for (String[][] datosTabla : listaTablasExtra) {
            guardarTablaEnDocumento(documentoPoi, datosTabla);
        }
        try (FileOutputStream salida = new FileOutputStream(archivo)) {
            documentoPoi.write(salida);
        }
        documentoPoi.close();
    }
    private void aplicarEstiloARun(XWPFRun runPoi, EstiloTexto estilo, String texto) {
        runPoi.setText(texto);
        runPoi.setFontFamily(estilo.getFuente());
        runPoi.setFontSize(estilo.getTamano());
        runPoi.setBold(estilo.isNegrita());
        runPoi.setItalic(estilo.isCursiva());
        runPoi.setUnderline(estilo.isSubrayado()
                ? org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE
                : org.apache.poi.xwpf.usermodel.UnderlinePatterns.NONE);
        runPoi.setColor(estilo.colorComoTexto());
    }
    public void abrirDocx(File archivo, JTextPane editor) throws IOException {
        try (FileInputStream entrada = new FileInputStream(archivo);
             XWPFDocument documentoPoi = new XWPFDocument(entrada)) {
            StyledDocument docSwing = editor.getStyledDocument();
            try {
                docSwing.remove(0, docSwing.getLength());
            } catch (BadLocationException e) {
                throw new IOException("No se pudo limpiar el editor.", e);
            }
            boolean esPrimero = true;
            for (Object elemento : documentoPoi.getBodyElements()) {
                if (elemento instanceof XWPFParagraph) {
                    XWPFParagraph parrafoPoi = (XWPFParagraph) elemento;
                    if (!esPrimero) {
                        insertarTexto(docSwing, "\n", EstiloTexto.porDefecto());
                    }
                    esPrimero = false;
                    for (XWPFRun runPoi : parrafoPoi.getRuns()) {
                        String texto = runPoi.getText(0);
                        if (texto == null) texto = "";
                        EstiloTexto estilo = leerEstiloPoi(runPoi);
                        insertarTexto(docSwing, texto, estilo);
                    }
                } else if (elemento instanceof XWPFTable) {
                    if (!esPrimero) {
                        insertarTexto(docSwing, "\n", EstiloTexto.porDefecto());
                    }
                    esPrimero = false;
                    mostrarTablaComoTexto((XWPFTable) elemento, docSwing);
                }
            }
            editor.setCaretPosition(0);
        }
    }
    private void insertarTexto(StyledDocument docSwing, String texto, EstiloTexto estilo) throws IOException {
        try {
            docSwing.insertString(docSwing.getLength(), texto, estiloAAtributos(estilo));
        } catch (BadLocationException e) {
            throw new IOException("Error al insertar texto.", e);
        }
    }
    private void mostrarTablaComoTexto(XWPFTable tabla, StyledDocument docSwing) throws IOException {
        EstiloTexto estiloTabla = new EstiloTexto("Calibri", 10, new Color(30, 90, 170), true, false, false);
        for (XWPFTableRow fila : tabla.getRows()) {
            StringBuilder lineaFila = new StringBuilder("| ");
            for (XWPFTableCell celda : fila.getTableCells()) {
                lineaFila.append(celda.getText()).append(" | ");
            }
            insertarTexto(docSwing, lineaFila.toString() + "\n", estiloTabla);
        }
    }
    private void guardarTablaEnDocumento(XWPFDocument documentoPoi, String[][] datosTabla) {
        if (datosTabla == null || datosTabla.length == 0) return;
        int cantFilas = datosTabla.length;
        int cantColumnas = datosTabla[0].length;
        XWPFTable tablaPoi = documentoPoi.createTable(cantFilas, cantColumnas);
        for (int fila = 0; fila < cantFilas; fila++) {
            for (int columna = 0; columna < cantColumnas; columna++) {
                String valor = datosTabla[fila][columna];
                tablaPoi.getRow(fila).getCell(columna).setText(valor == null ? "" : valor);
            }
        }
    }
    public MutableAttributeSet estiloAAtributos(EstiloTexto estilo) {
        MutableAttributeSet atributos = new SimpleAttributeSet();
        StyleConstants.setFontFamily(atributos, estilo.getFuente());
        StyleConstants.setFontSize(atributos, estilo.getTamano());
        StyleConstants.setBold(atributos, estilo.isNegrita());
        StyleConstants.setItalic(atributos, estilo.isCursiva());
        StyleConstants.setUnderline(atributos, estilo.isSubrayado());
        StyleConstants.setForeground(atributos, estilo.getColor());
        return atributos;
    }
    public EstiloTexto leerEstiloSwing(AttributeSet atributos) {
        if (atributos == null) return EstiloTexto.porDefecto();
        String fuente = StyleConstants.getFontFamily(atributos);
        int tamano = StyleConstants.getFontSize(atributos);
        boolean negrita = StyleConstants.isBold(atributos);
        boolean cursiva = StyleConstants.isItalic(atributos);
        boolean subrayado = StyleConstants.isUnderline(atributos);
        Color color = StyleConstants.getForeground(atributos);
        return new EstiloTexto(fuente, tamano, color, negrita, cursiva, subrayado);
    }
    private EstiloTexto leerEstiloPoi(XWPFRun runPoi) {
        String fuente = runPoi.getFontFamily() != null ? runPoi.getFontFamily() : "Calibri";
        int tamano = runPoi.getFontSize() > 0 ? runPoi.getFontSize() : 11;
        boolean negrita = runPoi.isBold();
        boolean cursiva = runPoi.isItalic();
        boolean subrayado = runPoi.getUnderline() != org.apache.poi.xwpf.usermodel.UnderlinePatterns.NONE;
        Color color = Color.BLACK;
        String textoColor = runPoi.getColor();
        if (textoColor != null && !textoColor.equalsIgnoreCase("auto") && textoColor.length() == 6) {
            color = EstiloTexto.textoAColor(textoColor);
        }
        return new EstiloTexto(fuente, tamano, color, negrita, cursiva, subrayado);
    }
}
