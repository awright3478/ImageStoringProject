package uk.ac.nulondon;
import java.awt.image.BufferedImage;
import java.awt.Color;
import java.util.ArrayList;

public class ImageEditor {
    /**
     * Image field
     * */
    public Image myImage;
    /**
     * boolean determines whether deletion can occur
     * */
    private boolean isHighlighted = false;


    /**
    * constructor for ImageEditor, takes in a BufferedImage, converts to Color[][]
    * and passes into image constructor
    * */
    public ImageEditor(BufferedImage bufferedImage){
        Color[][] myColors = new Color[bufferedImage.getHeight()][bufferedImage.getWidth()];
        for(int i = 0; i < bufferedImage.getHeight(); i++){
            for(int j = 0; j < bufferedImage.getWidth(); j++){
                int rgb = bufferedImage.getRGB(j,i);
                Color c = new Color(rgb);
                myColors[i][j] = c;
            }
        }
        myImage = new Image(myColors);
    }


    /**
     * This function highlights the bluest or lowest energy seam (blue or red)
     */
    public boolean highlight(boolean bluest) throws Exception{
        if(isHighlighted){
            return false;
        }
        ArrayList<Node<Pixel>> seam = myImage.findFunction(bluest);
        myImage.highlight(bluest, seam);
        isHighlighted = true;
        myImage.display();
        return true;
    }


    /**
     * this function removes the highlighted seam, if no seam is highlighted, it returns false
     */
    public boolean remove() throws Exception {
        if(isHighlighted){
            myImage.deleteSeam(myImage.undos.pop());
            isHighlighted = false;
            myImage.display();
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * checks to see if the stack is empty with a boolean if false and the stack is not empty, it calls insert seam, poping the recent edit, if boolean is false,
     * returns nothing, bringing back to main menu
     */
    public boolean undo() throws Exception {
        if(myImage.undos.isEmpty()){
            return false;
        }
        else{
            myImage.insertSeam();
            myImage.display();
            return true;
        }
    }

    /**
     * returns false when quit is chosen, stopping the while loop and ending the program
     */
    public boolean quit() throws Exception{
        myImage.display();
        return false;
    }
}
