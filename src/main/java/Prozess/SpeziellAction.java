package Prozess;

import GUI.Main;
import GUI.Message.Messages;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;

public class SpeziellAction {

    public static byte[] makeScreenshot() {
        String temp = "";
        byte[] imageInByte = null;
        try {

            Robot awt_robot = new Robot();
            BufferedImage screenshot = awt_robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
//            ImageIO.write(screenshot, "PNG", new File("C:\\Users\\aaron\\Desktop\\Entire_Screen.png"));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenshot, "PNG", baos);
            baos.flush();
            imageInByte = baos.toByteArray();
            baos.close();
        } catch (AWTException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageInByte;
    }

    private static String imgToBase64String(final RenderedImage img, final String formatName)
    {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try
        {
            ImageIO.write(img, formatName, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        }
        catch (final IOException ioe)
        {
            throw new UncheckedIOException(ioe);
        }
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }
}
