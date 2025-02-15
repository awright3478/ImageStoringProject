package uk.ac.nulondon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Stack;
import java.awt.Color;
import javax.imageio.ImageIO;


public class Image {
    /**data strutcure
     *
     */
    public ArrayList<MyLinkedList> myImage = new ArrayList<MyLinkedList>();
    /**
     * Stack of deleted seams
     */
    public Stack<ArrayList<Node<Pixel>>> undos = new Stack<ArrayList<Node<Pixel>>>();
    /**
    /*width of image
     */
    int width;
    /**
    * counts number of times image is displayed (for writing to file)
    * */
    int displayCounter = 0;


    /**
    /*image constructor, takes in a Color[][] and converts to an arrayList of MyLinkedLists
     **/
    public Image(Color[][] picture){
        width = picture[0].length;
        for(int i = 0; i < picture.length; i++){
            Pair firstPair = new Pair(i,0);
            Color tempC = picture[i][0];
            Pixel firstPix = new Pixel(firstPair, tempC);
            MyLinkedList inner = new MyLinkedList(firstPix);
            for(int j = 1; j < picture[i].length; j++){
                Pair tempPair = new Pair(i, j);
                Color c = picture[i][j];
                Pixel newPixel = new Pixel(tempPair, c);
                inner.add(newPixel);
            }
            myImage.add(inner);
        }
    }

    /**
     * This function removes the provided seam from myImage
    * */
    public void deleteSeam(ArrayList<Node<Pixel>> seam){
        for(int i = 0; i < seam.size(); i++){
            Node<Pixel> toBeDeleted = seam.get(i);
            if(toBeDeleted.prev != null){
                toBeDeleted.prev.setNext(toBeDeleted.next);
            }
            else{
                myImage.get(i).head = toBeDeleted.next;
            }
            if(toBeDeleted.next != null) {
                toBeDeleted.next.setPrev(toBeDeleted.prev);
            }
        }
        undos.push(seam);
        width--;
    }


    /**
     * This function pops the latest undo from the stack and re-inserts it back into the image
     * */
    public void insertSeam(){
        ArrayList<Node<Pixel>> seam = undos.pop();
        for(int i = 0; i < seam.size(); i++){
            Node<Pixel> deleted = seam.get(i);
            if(deleted.next!= null){
                deleted.next.setPrev(deleted);
            }
            if(deleted.prev != null){
                deleted.prev.setNext(deleted);
            }
            else{
                myImage.get(deleted.myPixel.myPair.row).head = deleted;
            }

        }
        width++;
    }

    /**
     * This function takes in a boolean(representing whether the bluest or lowest cost seam is being removed)
     * and a List of Pixels(desired seam to highlight), and highlights specific seam accordinly, while pushing
     * original seam (original colors) into stack of undos
     * */
    public void highlight(boolean bluest, ArrayList<Node<Pixel>> seam) {
        ArrayList<Node<Pixel>> oldSeam = new ArrayList<Node<Pixel>>();
        for(int i = 0; i < seam.size();i++) {
            Node<Pixel> newPixel = seam.get(i);
            Node<Pixel> highlightNode = new Node<Pixel>(newPixel.myPixel);
            if(bluest){
                highlightNode.myPixel = new Pixel(highlightNode.myPixel.myPair ,Color.blue);
            }
            else{
                highlightNode.myPixel = new Pixel(highlightNode.myPixel.myPair, Color.red);
            }
            if(newPixel.next!= null) {
                highlightNode.next = newPixel.next;
            }
            if(newPixel.prev != null) {
                highlightNode.prev = newPixel.prev;
            }
            if(newPixel.prev != null) {
                newPixel.prev.next = highlightNode;
            }
            else{
                myImage.get(i).head = highlightNode;
            }
            if(newPixel.next != null) {
                newPixel.next.prev = highlightNode;
            }
            oldSeam.add(newPixel);
        }
        undos.push(oldSeam);
    }

    /**
     * This function finds the bluest seam in the image or the lowest cost seam and returns it
     * */
    public ArrayList<Node<Pixel>> findFunction(boolean isBlue) {
        calculateEnergies();
        int row = 1;
        double[] previousValues = getValues(true, row, isBlue);
        double[] currentValues = getValues(false, row, isBlue);
        ArrayList<ArrayList<Node<Pixel>>> previousSeams = getPreviousSeams();
        ArrayList<ArrayList<Node<Pixel>>> currentSeams = new ArrayList<>();
        for(int i = 1; i < myImage.size(); i++){
            MyLinkedList rowIter = myImage.get(i);
            Node<Pixel> colIter = rowIter.head;
            int col = 0;
            while(colIter != null){
                Node<Pixel> blueOrLowestValue;
                blueOrLowestValue = getBluestOrLowest(previousValues, col, row, isBlue);
                currentValues[col] +=previousValues[blueOrLowestValue.myPixel.myPair.col];
                ArrayList<Node<Pixel>> currentInnerSeam = previousSeams.get(blueOrLowestValue.myPixel.myPair.col);
                ArrayList<Node<Pixel>> currentInnerSeamAlias = new ArrayList<Node<Pixel>>();
                currentInnerSeamAlias.addAll(currentInnerSeam);
                currentInnerSeamAlias.add(colIter);
                currentSeams.add(currentInnerSeamAlias);
                col++;
                colIter = colIter.next;
            }
            previousSeams.clear();
            previousSeams.addAll(currentSeams);
            currentSeams.clear();
            for(int j = 0; j < previousValues.length; j++){
                previousValues[j] = currentValues[j];
            }
            row++;
            if(row < myImage.size()){
                currentValues = getValues(false, row, isBlue);
            }
        }
        double maxOrMin = previousValues[0];
        if(!isBlue) {
            for (int i = 1; i < previousValues.length; i++) {
                if (previousValues[i] < maxOrMin) {
                    maxOrMin = previousValues[i];
                }
            }
        }
        else{
            for(int i = 1; i < previousValues.length; i++){
                if(previousValues[i] > maxOrMin){
                    maxOrMin = previousValues[i];
                }
            }
        }
        int ind = 0;
        for(int i = 0; i < previousValues.length; i++){
            if(previousValues[i] == maxOrMin){
                ind = i;
            }
        }
        return previousSeams.get(ind);
    }
    /**
     * Calculates energies for each pixel, stores them into in their pixel
     * */
    public void calculateEnergies(){
        int innerCounter;
        int outerCounter = 0;
        for(int i = 0; i < myImage.size(); i++){
            MyLinkedList outerIter = myImage.get(i);
            Node<Pixel> innerIter = outerIter.head;
            Node<Pixel> aboveInnerIter;
            Node<Pixel> belowInnerIter;
            if(i == 0){
                aboveInnerIter = myImage.get(0).head;
            }
            else{
                aboveInnerIter = myImage.get(i-1).head;
            }
            if(i == myImage.size()-1){
                belowInnerIter = myImage.get(i).head;
            }
            else{
                belowInnerIter = myImage.get(i+1).head;
            }
            innerCounter = 0;
            while(innerIter != null){
                innerIter.myPixel.myPair.col = innerCounter;
                double[] brightnessVals = getBrightnessVals(innerIter, outerCounter, innerCounter, aboveInnerIter, belowInnerIter);
                double energy = calcEnergy(brightnessVals);
                innerIter.myPixel.setEnergy(energy);
                innerIter = innerIter.next;
                innerCounter++;
                aboveInnerIter = aboveInnerIter.next;
                belowInnerIter = belowInnerIter.next;
            }
            outerCounter++;;
        }
    }
    /**
     * calculates the energy for a specific pixel given its ABCDFGHI VALUES
     * */
    public double calcEnergy(double[] vals){
        double horiz = (vals[0] + (2 * vals[3]) + vals[5]) - (vals[2] + (2 * vals[4]) + vals[7]);
        double verti = (vals[0] + (2 * vals[1]) + vals[2]) - (vals[5] + (2 * vals[6]) + vals[7]);
        horiz = horiz * horiz;
        verti = verti * verti;
        return Math.sqrt(horiz + verti);
    }
    /**
    * gets the appropriate brightness values to calculate energy for a specific pixel
    * */
    public double[] getBrightnessVals(Node<Pixel> node, int row, int col, Node<Pixel> aboveNode, Node<Pixel> belowNode){
        double[] ret = new double[8];
        double def = node.myPixel.getBrightness();
        boolean nextExists = (node.next != null);
        boolean prevExists = (node.prev != null);
        boolean aboveExists = (row != 0);
        boolean belowExists = (row != width -1);
        if(!aboveExists){
            ret[6] = belowNode.myPixel.getBrightness();
            ret[1] = def;
            ret[2] = def;
            ret[0] = def;
            if(!prevExists){
                ret[3] = def;
                ret[5] = def;
            }
            else{
                ret[3] = node.prev.myPixel.getBrightness();
                ret[5] = belowNode.prev.myPixel.getBrightness();
            }
            if(!nextExists){
                ret[4] = def;
                ret[7] = def;
            }
            else{
                ret[4] = node.next.myPixel.getBrightness();
                ret[7] = belowNode.next.myPixel.getBrightness();
            }
        }
        else if(!belowExists){
            ret[1] = aboveNode.myPixel.getBrightness();
            ret[5] = def;
            ret[6] = def;
            ret[7] = def;
            if(!prevExists){
                ret[0] = def;
                ret[3] = def;
            }
            else{
                ret[0] = aboveNode.prev.myPixel.getBrightness();
                ret[3] = node.prev.myPixel.getBrightness();
            }
            if(!nextExists){
                ret[2] = def;
                ret[4] = def;
            }
            else{
                ret[2] = aboveNode.next.myPixel.getBrightness();
                ret[4] = node.next.myPixel.getBrightness();
            }
        }
        else{
            ret[1] = aboveNode.myPixel.getBrightness();
            ret[6] = belowNode.myPixel.getBrightness();
            if(!prevExists){
                ret[0] = def;
                ret[3] = def;
                ret[5] = def;
            }
            else{
                ret[0] = aboveNode.prev.myPixel.getBrightness();
                ret[3] = node.prev.myPixel.getBrightness();
                ret[5] = belowNode.prev.myPixel.getBrightness();
            }
            if(!nextExists){
                ret[2] = def;
                ret[4] = def;
                ret[7] = def;
            }
            else{
                ret[2] = aboveNode.next.myPixel.getBrightness();
                ret[4] = node.next.myPixel.getBrightness();
                ret[7] = belowNode.next.myPixel.getBrightness();
            }
        }
        return ret;
    }

    /**
     * gets the pixel with the lowest/highest bluevalue/cost seam for each corresponding pixel
     * */
    public Node<Pixel> getBluestOrLowest(double[] prev, int col, int row, boolean isBlue){
        double lowestOrBluest;
        if(col == 0){
            if(isBlue){
                lowestOrBluest = Math.max(prev[col], prev[col+1]);
            }
            else{
                lowestOrBluest = Math.min(prev[col], prev[col +1]);
            }
        }
        else if(col == prev.length -1){
            if(isBlue){
                lowestOrBluest = Math.max(prev[col], prev[col-1]);
            }
            else{
                lowestOrBluest = Math.min(prev[col], prev[col-1]);
            }
        }
        else{
            if(isBlue){
                lowestOrBluest = Math.max(prev[col-1], Math.max(prev[col], prev[col+1]));
            }
            else{
                lowestOrBluest = Math.min(prev[col-1], Math.min(prev[col], prev[col + 1]));
            }
        }
        int indx = col;
        int j = col -1;
        if(col == 0){
            j = col;
        }
        int k = col+2;
        if(col == width -1){
            k = col+1;
        }
        for(int i = j; i < k; i++){
            if(prev[i] == lowestOrBluest){
                indx = i;
            }
        }
        MyLinkedList rowList = myImage.get(row -1);
        Node<Pixel> iter = rowList.head;
        for(int i = 0; i < indx; i++){
            iter = iter.next;
        }
        return iter;
    }
    /**
     * gets the values either previous to or of the current row, blue or cost
     * */
    public double[] getValues(boolean previous, int row, boolean isBlue) {
        double[] values = new double[width];
        int j = row;
        if (previous) {
            j = j - 1;
        }
        int counter = 0;
        MyLinkedList valueRow = myImage.get(j);
        Node<Pixel> iter = valueRow.head;
        while (iter != null){
            if (isBlue) {
                values[counter] = iter.myPixel.color.getBlue();
            }
            else{
                values[counter] = iter.myPixel.energy;
            }
            iter = iter.next;
            counter++;
        }
        return values;
    }
    /**
     * initializes previousSeams with the pixels of the 0th row
     * */
    public ArrayList<ArrayList<Node<Pixel>>> getPreviousSeams(){
        ArrayList<ArrayList<Node<Pixel>>> prev = new ArrayList<ArrayList<Node<Pixel>>>();
        Node<Pixel> myPix = myImage.get(0).head;
        while(myPix != null){
            ArrayList<Node<Pixel>> myInner = new ArrayList<Node<Pixel>>();
            myInner.add(myPix);
            prev.add(myInner);
            myPix = myPix.next;
        }
        return prev;
    }
    /**
     * displays the image
     * */
    public void display() throws Exception {
        BufferedImage displayImage = new BufferedImage(width, myImage.size(), BufferedImage.TYPE_INT_RGB);
        for(int i = 0; i < displayImage.getHeight(); i++){
            Node<Pixel> iter = myImage.get(i).head;
            if(iter.prev!=null){
                iter=iter.prev;
            }
            for(int j = 0; j < displayImage.getWidth(); j++){
                iter.myPixel.myPair.col = j;
                Color pixelColor = iter.myPixel.color;
                displayImage.setRGB(j,i,pixelColor.getRGB());
                iter = iter.next;
            }
        }
        String fileString = "newImg(" + displayCounter +").png";
        File myFile = new File(fileString);
        ImageIO.write(displayImage, "png", myFile);
        displayCounter++;
    }
}






