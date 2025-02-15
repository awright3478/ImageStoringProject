package uk.ac.nulondon;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
public class Client {
    /**
     * scanner feild
     */
    static Scanner scan = new Scanner(System.in);
    //ImageEditor feild
    private static ImageEditor ie;
    //prints options to menu and returns user input
    public static String printMenu() {
        System.out.println("Please enter a command");
        System.out.println("b - highlight bluest seam");
        System.out.println("l - highlight seam with the lowest energy");
        System.out.println("d - remove the highlighted seam");
        System.out.println("u - undo previous edit");
        System.out.println("q - quit");
        String input = scan.nextLine();
        return input;
    }
    //gets File from user until valid file given
    public static void getFile() throws Exception {
        boolean invalid = true;
        BufferedImage tryImage = null;
        while(invalid) {
            System.out.println("Please enter image file path:");
            String filePath = scan.nextLine();
            File myFile = new File(filePath);
            try{
                tryImage = ImageIO.read(myFile);
                invalid = false;
            }
            catch(IOException e){
                invalid = true;
                System.err.println("FileNotFoundException: please enter an existing file path");
            }
        }
        ie = new ImageEditor(tryImage);
    }
    //determines next case
    public static int determineNext(String input) {
        if (input.equalsIgnoreCase("b")) {
            return 0;
        } else if (input.equalsIgnoreCase("l")) {
            return 1;
        } else if (input.equalsIgnoreCase("d")) {
            return 2;
        } else if (input.equalsIgnoreCase("u")) {
            return 3;
        } else if (input.equalsIgnoreCase("q")) {
            return 4;
        } else {
            return 5;
        }
    }
    //handles cases, if invalid given it calls recursively, returns false when quit is called
    public static boolean handleCase(int myCase) throws Exception {
        if (myCase == 5) {
            System.out.println("invalid input, please select one of the options");
            String input = printMenu();
            int newCase = determineNext(input);
            return handleCase(newCase);
        } else if (myCase == 4) {
            return ie.quit();
        } else if (myCase == 3) {
            if (ie.undo() == false) {
                System.out.println("cannot undo, delete a seam before attempting to undo");
                String input = printMenu();
                int newCase = determineNext(input);
                return handleCase(newCase);
            }
            else {
                return true;
            }
        } else if (myCase == 2) {
            if(ie.remove() == false) {
                System.out.println("cannot delete, highlight a seam before attempting to delete");
                String input = printMenu();
                int newCase = determineNext(input);
                return handleCase(newCase);
            } else return true;

        } else if (myCase == 1) {
            if(ie.highlight(false) == false){
                System.out.println("cannot highlight andother row, please delete highlighted row first");
                String input = printMenu();
                int newCase = determineNext(input);
                return handleCase(newCase);
            }
            return true;
        }
        else{
            if(ie.highlight(true) == false){
                System.out.println("cannot highlight another row, please delete highlighted row first");
                String input = printMenu();
                int newCase = determineNext(input);
                return handleCase(newCase);
            }
            else {
                return true;
            }
        }
    }
    //main method
    public static void main(String[] args) throws Exception{
       getFile();
       boolean isRunning = true;
        while ( isRunning ) {
            String input = printMenu();
            int myCase = determineNext(input);
            isRunning = handleCase(myCase);
        }
    }
}
