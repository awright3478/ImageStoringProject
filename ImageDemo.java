package uk.ac.nulondon;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageDemo {
    // Main throws Exception which handles exceptions from
    // File, ImageIO, Color, and BufferedImage
    // Your code would handle these differently
    public static void main(String args[]) throws Exception {

        // Code to read image into an image buffer
        File originalFile = new File("src/main/resources/beach.png");
        BufferedImage oldImg = ImageIO.read(originalFile);

        // new file to store altered image
        File newFile = new File("newImg.png");

        // new buffer for alteredImage
        BufferedImage newImg = new BufferedImage( oldImg.getWidth()
                , oldImg.getHeight()
                , BufferedImage.TYPE_INT_RGB);

        // This loops through the image pixel by pixel
        // we will read in the original color then change
        // the color and store into new buffer
        for (int y = 0; y < oldImg.getHeight(); y++) {
            for (int x = 0; x < oldImg.getWidth(); x++) {

                // Retrieving contents of a pixel
                int pixel = oldImg.getRGB(x, y);

                // Creating a Color object from pixel value
                Color originalColor = new Color(pixel);

                // Retrieving the Red Green and Blue values
                int red   = originalColor.getRed();
                int green = originalColor.getGreen();
                int blue  = originalColor.getBlue();

                // Alter the color and then store in new image buffer
                Color newColor = new Color(red / 2, green / 2, blue / 2);
                newImg.setRGB(x, y, newColor.getRGB());
            }
        }

        // Save to file and announce that it has been saved
        ImageIO.write(newImg, "png", newFile);
        System.out.println("Altered image stored");
    }
}