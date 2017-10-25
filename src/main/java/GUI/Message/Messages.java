package GUI.Message;

import GUI.Main;

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

    public static void sendMessage()
    {
        byte[] messageByte = null;
        DataOutputStream streamOut = Main.publicGUI.getStreamOut();
        try {
            messageByte = Main.publicGUI.textFieldClientMessage.getText().getBytes();
            streamOut.writeInt(messageByte.length);
            streamOut.write(messageByte);
            streamOut.flush();
            Main.publicGUI.textFieldClientMessage.setText("");
        } catch (IOException e) {
            msgbox("Sending error: " + e.toString(), "ERROR", "ERROR");
            Main.publicGUI.stop();
        }
    }

    public static void send(String messageTo, String message, String messageTyp){
        ZipMessage zipMSG = new ZipMessage();
        DataOutputStream streamOut = Main.publicGUI.getStreamOut();
        try {
            byte[] zippedBytes;
            zippedBytes = zipMSG.zipAndSendBytes(messageTo.getBytes(), message.getBytes(), messageTyp.getBytes());
            streamOut.writeInt(zippedBytes.length);
            streamOut.write(zippedBytes);
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
}
