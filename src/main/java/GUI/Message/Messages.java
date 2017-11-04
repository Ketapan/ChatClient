package GUI.Message;

import GUI.Main;
import Prozess.ChatClientThread;
import Prozess.SpeziellAction;
import com.sun.org.apache.xpath.internal.SourceTree;
import MessageObject.Message;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class Messages {

    private ObjectOutputStream oos =null;

    public static void msgbox(String message, String title, String type){
        type = type.toUpperCase();
        switch(type)
        {
            case "ERROR":
            case "ERR":
                JOptionPane.showMessageDialog(null, message, title, 0);
                break;
            case "INFO":
                JOptionPane.showMessageDialog(null, message, title, 1);
                break;
            case "WARN":
            case "WARNUNG":
                JOptionPane.showMessageDialog(null, message, title, 2);
                break;
            default:
                JOptionPane.showMessageDialog(null, message, title, 2);
                break;
        }
    }

    public static void appendTextMessage(String message)
    {
        Main.publicGUI.textAreaMessages.append(message + "\n");
    }

    /*
    public static void sendZipMessage(byte[] messageTo, byte[] message, byte[] type){
        byte[] messageByte = null;
        DataOutputStream streamOut = Main.publicGUI.getStreamOut();
        ZipMessage zipMSG = new ZipMessage();
        System.out.println("Before zipping = " + message.length);
        try {
            messageByte = zipMSG.zipAndSendBytes(messageTo, message, type);
            streamOut.writeInt(messageByte.length);
            streamOut.write(messageByte);
            streamOut.flush();
            Main.publicGUI.textFieldClientMessage.setText("");
        } catch (IOException e) {
            msgbox("Sending error: " + e.toString(), "ERROR", "ERROR");
            Main.publicGUI.stop();
        }
    }
    */
/*
    public static void sendMessages(String messageByte,String type, String messageTo){
        switch (type){
            case "pm":
                sendZipMessage(toByte(type,messageTo),toByte(type,messageByte), toByte(type,"pm"));
                break;
            case "pic":
                sendZipMessage(toByte(type,messageTo),SpeziellAction.makeScreenshot(),toByte(type,"pic"));
                break;
            case "msg":
                sendZipMessage(toByte(type,messageTo),toByte(type,messageByte), toByte(type,"msg"));
                break;
        }

    }
    */

    public static byte[] toByte(String equalType, String byteStrom){
        byte[] messageByte = null;
        messageByte = byteStrom.getBytes();
        return messageByte;
    }

    public static void sendObjectStream(Message msg){
        ObjectOutputStream streamOut = Main.publicGUI.getStreamOut();
        try {
            streamOut.writeObject(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
