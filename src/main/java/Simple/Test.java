package Simple;

// https://stackoverflow.com/questions/13596660/sending-an-image-as-a-byte-array-between-client-and-server
// https://www.tutorials.de/threads/datei-mittels-tcp-uebertragen.245134/
// https://stackoverflow.com/questions/10247123/java-convert-bufferedimage-to-byte-without-writing-to-disk

// https://www.mkyong.com/java/how-to-convert-byte-to-bufferedimage-in-java/

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class Test {
    public static void main(String[] args)
    {
        Test test = new Test();
        test.makeScreenshot();
    }

    private void bildInArrayUndWiederZurueck()
    {
        try {

            byte[] imageInByte;
            BufferedImage originalImage = ImageIO.read(new File(
                    "C:\\Users\\aaron\\Desktop\\defg.png"));

            // convert BufferedImage to byte array
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(originalImage, "PNG", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();

            // convert byte array back to BufferedImage
            InputStream in = new ByteArrayInputStream(imageInByte);
            BufferedImage bImageFromConvert = ImageIO.read(in);

            ImageIO.write(bImageFromConvert, "PNG", new File(
                    "C:\\Users\\aaron\\Desktop\\hijk.png"));

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void makeScreenshot() {
        try {
            byte[] imageInByte;

            Robot awt_robot = new Robot();
            BufferedImage screenshot = awt_robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
//            ImageIO.write(screenshot, "PNG", new File("C:\\Users\\aaron\\Desktop\\Entire_Screen.png"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "PNG", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();

        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }
}
