/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lab_sem7_editor;
import java.awt.Color;
/**
 *
 * @author andres
 */
public class EstiloTexto {
    private String fuente;
    private int tamanoPt;
    private Color color;
    private boolean negrita;
    private boolean cursiva;
    private boolean subrayado;
    public EstiloTexto(String fuente, int tamanoPt, Color color, boolean negrita, boolean cursiva, boolean subrayado){
        this.fuente=(fuente==null || fuente.trim().isEmpty()) ? "Calibri":fuente;
        this.tamanoPt=tamanoPt<=0 ? 11:tamanoPt;
        this.color=color==null ? Color.BLACK:color;
        this.negrita=negrita;
        this.cursiva=cursiva;
        this.subrayado=subrayado;
    }
    public static EstiloTexto porDefecto() {
        return new EstiloTexto("Calibri", 11, Color.BLACK, false, false, false);
    }
    public String colorComoTexto() {
        return String.format("%02X%02X%02X",color.getRed(), color.getGreen(), color.getBlue());
    }
    public static Color textoAColor(String hex) {
        if (hex==null || hex.length()!=6) return Color.BLACK;
        try {
            int rojo=Integer.parseInt(hex.substring(0,2),16);
            int verde=Integer.parseInt(hex.substring(2,4),16);
            int azul=Integer.parseInt(hex.substring(4,6),16);
            return new Color(rojo,verde,azul);
        } catch (NumberFormatException e) {
            return Color.BLACK;
        }
    }
    public String getFuente(){
        return fuente;
    }
    public int getTamano(){ 
        return tamanoPt; 
    }
    public Color getColor(){
        return color; 
    }
    public boolean isNegrita(){ 
        return negrita; 
    }
    public boolean isCursiva(){ 
        return cursiva; 
    }
    public boolean isSubrayado(){ 
        return subrayado;
    }
    public void setFuente(String fuente){
        this.fuente=fuente;
    }
    public void setTamano(int tamano){
        this.tamanoPt=tamano; 
    }
    public void setColor(Color color){ 
        this.color=color; 
    }
    public void setNegrita(boolean negrita){ 
        this.negrita=negrita; 
    }
    public void setCursiva(boolean cursiva){ 
        this.cursiva=cursiva; 
    }
    public void setSubrayado(boolean subrayado){
        this.subrayado=subrayado; 
    }
}
