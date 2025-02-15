package uk.ac.nulondon;
import java.awt.Color;

public class Pixel {
    /**
     * Color of pixel field
     * */
    public Color color;
    /**
     * indexes of pixel field
     * */
    public Pair myPair;
    /**
     * energy feild
     * */
    public double energy;
    /**
     * constructor with pair and color
     * */
    public Pixel(Pair pair, Color c){
        myPair = pair;
        color = c;
    }
    /**
     * constructor with row, col and color
     * */
    public Pixel(int row, int col, Color c){
        myPair = new Pair(row, col);
        color = c;
    }
    /**
     * sets energy of a given pixel after computed
     * */
    public void setEnergy(double e) {
        energy = e;
    }
    /***
     * calculates brightness of pixel
     */
    public double getBrightness() {
        double brightness = (color.getRed() + color.getBlue() + color.getGreen()) / 3.0;
        return brightness;
    }

}



