package uk.ac.nulondon;

import org.junit.jupiter.api.Test;
import org.assertj.core.api.Assertions;
import java.util.ArrayList;

import java.awt.*;

public class MyImageTest {
    Color[][] preImage = construct();
    Color[][] preImage2 = construct2();
    Color[][] preImageFind = construct3();
    Color[][] preImageFind2 = construct4();



    public static Color[][] construct(){
        Color[][] ret = new Color[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(j % 2 == 0){
                    ret[i][j] = Color.BLUE;
                }
                else{
                    ret[i][j] = Color.RED;
                }
            }
        }
        return ret;
    }

    public static Color[][] construct2(){
        Color[][] ret = new Color[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(j % 3 == 0){
                    ret[i][j] = new Color(0, 255,255);
                }
                else if(j % 3 == 1){
                    ret[i][j] = new Color(255, 0 , 0);
                }
                else{
                    ret[i][j] = new Color(0,0,0);
                }
            }
        }
        return ret;
    }

    public static Color[][] construct3(){
        Color[][] ret = new Color[8][8];
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                if(j < 3){
                    ret[i][j] = Color.BLACK;
                }
                else{
                    ret[i][j] = Color.BLUE;
                }
            }
        }
        return ret;
    }
    public Color[][] construct4(){
        Color[][] ret = new Color[8][8];
        for(int i = 0; i < ret.length; i++){
            for(int j = 0; j < ret[i].length; j++){
                if(j == 0){
                    ret[i][j] = Color.BLUE;
                }
                else if(j < 4){
                    ret[i][j] = new Color(0,0,0);
                }
                else{
                    ret[i][j] = Color.RED;
                }
            }
        }
        return ret;
    }
    //tests find function, is able to get bluest or lowest cost seam
    @Test
    public void findFunctionTest(){
        Image myImage = new Image(preImageFind);
        Image myImage2 = new Image(preImageFind2);
        Assertions.assertThat(myImage.findFunction(false).get(0).myPixel.myPair.col < 2);
        Assertions.assertThat(myImage.findFunction(true).get(0).myPixel.myPair.col > 2);
        Assertions.assertThat(myImage2.findFunction(true).get(0).myPixel.myPair.col).isEqualTo(0);
    }
    //Test for image Constructor
    @Test
    public void myImageConstructorTest(){
        Image myImage = new Image(preImage);
        Node<Pixel> iter = myImage.myImage.get(0).head;
        int counter = 0;
        while(iter != null){
           if(counter % 2 == 0){
                Assertions.assertThat(iter.myPixel.color).isEqualTo(Color.BLUE);
            }
           else{
                Assertions.assertThat(iter.myPixel.color).isEqualTo(Color.RED);
            }
            counter++;
            iter = iter.next;
       }
        Assertions.assertThat(counter).isEqualTo(8);
    }
    //test for getValues
    @Test
    public void getValuesTest(){
        Image myImage = new Image(preImage);
        for(int i = 0; i < myImage.myImage.size();i++){
            double[] currentValues = myImage.getValues(false, i, true);
            for(int j = 0; j < currentValues.length; j++){
                if(j % 2 == 0){
                    Assertions.assertThat(currentValues[j]).isEqualTo(255);
                }
                else{
                    Assertions.assertThat(currentValues[j]).isEqualTo(0);
                }
            }
        }
    }
    //test for getBluestOrLowest
    @Test
    public void getBluestOrLowestTest(){
        Image myImage = new Image(preImage);
        double[] prev = new double[]{5,7,8,2,1,6,9,3};
        Node<Pixel> bluestOrLowest = myImage.getBluestOrLowest(prev,1, 5, true);
        Node<Pixel> lowest = myImage.getBluestOrLowest(prev, 1, 3, false);
        Assertions.assertThat(bluestOrLowest.myPixel.myPair.col).isEqualTo(2);
        Assertions.assertThat(lowest.myPixel.myPair.col).isEqualTo(0);

    }

    /**
     * tests that popped seam from the stack is showed in display when undo is called
     * and that seam is deleted in display when d is called
     * @throws Exception
     */
    @Test
    public void insertAndDeleteSeamTest(){
        Image myImage = new Image(preImage);
        Image alias = new Image(preImage);
        ArrayList<Node<Pixel>>  seam = new ArrayList<Node<Pixel>>();
        ArrayList<Node<Pixel>> seam2 = new ArrayList<Node<Pixel>>();
        for(int i = 0; i < myImage.myImage.size(); i++){
            seam.add(myImage.myImage.get(i).head.next);
            seam2.add(myImage.myImage.get(i).head.next.next);
        }
        myImage.deleteSeam(seam);
        myImage.deleteSeam(seam2);
        myImage.insertSeam();
        myImage.insertSeam();
        for(int i = 0; i < myImage.myImage.size(); i++){
            Node<Pixel> iter1 = myImage.myImage.get(i).head;
            Node<Pixel> iter2 = alias.myImage.get(i).head;
            while(iter1 != null){
                Assertions.assertThat(iter1.myPixel.myPair.col).isEqualTo(iter2.myPixel.myPair.col);
                Assertions.assertThat(iter1.myPixel.myPair.row).isEqualTo(iter2.myPixel.myPair.row);
                Assertions.assertThat(iter1.myPixel.color).isEqualTo(iter2.myPixel.color);
                Assertions.assertThat(iter1.myPixel.getBrightness()).isEqualTo(iter2.myPixel.getBrightness());
                iter2 = iter2.next;
                iter1 = iter1.next;
            }
        }
    }
    //test for calculating energies
    @Test
    public void energiesTest(){
        Image myImage = new Image(preImage2);
        myImage.calculateEnergies();
        Node<Pixel> testNode = myImage.myImage.get(1).head;
        Node<Pixel> testNode2 = myImage.myImage.get(2).head;
        Node<Pixel> testNode3 = myImage.myImage.get(0).head;
        for(int i = 0; i < 7; i++){
            testNode = testNode.next;
            testNode2 = testNode2.next;
            testNode3 = testNode3.next;
        }
        Assertions.assertThat(myImage.myImage.get(1).head.next.myPixel.energy).isEqualTo(680);
        Assertions.assertThat(myImage.myImage.get(0).head.next.myPixel.energy).isEqualTo(510);
        Assertions.assertThat(myImage.myImage.get(7).head.next.myPixel.energy).isEqualTo(510);
        Assertions.assertThat(myImage.myImage.get(1).head.myPixel.energy).isEqualTo(340);
        Assertions.assertThat(testNode.myPixel.energy).isEqualTo(340);
    }
}

