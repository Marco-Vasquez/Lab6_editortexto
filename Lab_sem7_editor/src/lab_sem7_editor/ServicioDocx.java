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
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;


/**
 *
 * @author user
 */
public class ServicioDocx {
  public void guardarDocx(JTextPane editor, List<String[][]> listaTablasExtra, File archivo) throws IOException  {
   XWPFDocument documentoPoi = new XWPFDocument();
   StyledDocument docSwing = editor.getStyledDocument();
   Element raiz = docSwing.getDefaultRootElement();
   
   for (int control = 0; control < raiz.getElementCount(); control++){
       Element parrafo = raiz.getElement(control);
       int inicio = parrafo.getStartOffset();
       int fin = parrafo.getEndOffset();
       
       String textoParrafo = "";
       try {
           textoParrafo = docSwing.getText(inicio, fin-inicio);
           
       }catch (BadLocationException e){
           textoParrafo = "";
       }
       
       if(textoParrafo.endsWith("/n")){
           textoParrafo = textoParrafo.substring(0, textoParrafo.length() - 1);
       }
       
       XWPFParagraph parrafoPoi = documentoPoi.createParagraph();
       int cursor = inicio;
       
       while(cursor < fin){
           Element elementoChar = docSwing.getCharacterElement(cursor);
           int runInicio = Math.max(cursor, elementoChar.getStartOffset());
           int runFin = Math.min(fin, elementoChar.getEndOffset());
           if (runFin <= runInicio) break;
           
           String textoRun = "";
           try { 
               textoRun = docSwing.getText(runInicio, runFin - runInicio);
           }catch (BadLocationException e){
               textoRun = "";
               
           }
           
           if(textoRun.endsWith("/n"))
               textoRun = textoRun.substring(0,textoRun.length() - 1);
           
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
        runPoi.setFontSize(estilo.getTamanoPt());
        runPoi.setBold(estilo.isNegrita());
        runPoi.setItalic(estilo.isCursiva());
        runPoi.setUnderline(
            estilo.isSubrayado()
                ? org.apache.poi.xwpf.usermodel.UnderlinePatterns.SINGLE
                : org.apache.poi.xwpf.usermodel.UnderlinePatterns.NONE
        );

        CTRPr propiedadesRun = runPoi.getCTR().isSetRPr()
                ? runPoi.getCTR().getRPr()
                : runPoi.getCTR().addNewRPr();
        CTColor colorPoi = propiedadesRun.isSetColor()
                ? propiedadesRun.getColor()
                : propiedadesRun.addNewColor();
        colorPoi.setVal(estilo.colorComoTexto());
    }    
           
           
       }
       
       
       
       
   
   
     
     
  

