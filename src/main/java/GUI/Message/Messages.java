package GUI.Message;

import GUI.Main;
import Prozess.SpeziellAction;

import javax.swing.*;
import java.io.DataOutputStream;
import java.io.IOException;

public class Messages {

    public static void msgbox(String message, String title, String type)
    {
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

    public static void sendZipMessage(byte[] messageTo, byte[] message, byte[] type){
        byte[] messageByte = null;
        DataOutputStream streamOut = Main.publicGUI.getStreamOut();
        ZipMessage zipMSG = new ZipMessage();
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

    public static void sendPrivateMessage(){
        byte[] messageByte = null;
        DataOutputStream streamOut = Main.publicGUI.getStreamOut();
        try{
            String temp = "-pm" + Main.publicGUI.userList.getSelectedValue() + ": " + Main.publicGUI.textFieldClientMessage.getText();
            //-pmdeiMudda: abcdefg
            messageByte = temp.getBytes();
            streamOut.writeInt(messageByte.length);
            streamOut.write(messageByte);
            streamOut.flush();
            Main.publicGUI.textFieldClientMessage.setText("");
        } catch (IOException e){
            msgbox("Sending error: " + e.toString(), "ERROR", "ERROR");
            Main.publicGUI.stop();
        }
    }

    public static void sendPrivateMessageBytes(byte[] messageByte){
        DataOutputStream streamOut = Main.publicGUI.getStreamOut();
        try{
//            String temp = "-pm" + Main.publicGUI.userList.getSelectedValue();
//            //-pmdeiMudda: abcdefg
//            messageByte = temp.getBytes();
            streamOut.writeInt(messageByte.length);
            streamOut.write(messageByte);
            streamOut.flush();
            Main.publicGUI.textFieldClientMessage.setText("");
        } catch (IOException e){
            msgbox("Sending error: " + e.toString(), "ERROR", "ERROR");
            Main.publicGUI.stop();
        }
    }
    public static void sendMessages(String messageByte,String type, String messageTo){
        switch (type){
            case "pm":
                sendZipMessage(toByte(type,messageTo),toByte(type,messageByte), toByte("pm", type));
                break;
            case "pic":
                //SpeziellAction.makeScreenshot(messageTo,type,);
                break;
            case "msg":
                sendZipMessage(toByte(type,messageTo),toByte(type,messageByte), toByte("msg", type));
                break;
        }

    }
    public static byte[] toByte(String type, String messageOrPicture){
        byte[] messageByte = null;
        if (type.equals("pic")){

        }else {
            messageByte = messageOrPicture.getBytes();
        }
        return messageByte;
    }
}
