package GUI.Message;

import GUI.Main;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    private byte[] zip(byte[] messageTo, byte[] message, byte[] type) throws IOException
    {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ZipOutputStream zip = new ZipOutputStream( output );

        zip.setMethod( ZipOutputStream.DEFLATED );

        ZipEntry entry = new ZipEntry("messageTO");
        ZipEntry entry2 = new ZipEntry("message");
        ZipEntry entry3 = new ZipEntry("type");

        zip.putNextEntry( entry );
        zip.write(messageTo);
        zip.closeEntry();

        zip.putNextEntry(entry2);
        zip.write(message);
        zip.closeEntry();

        zip.putNextEntry(entry3);
        zip.write(type);
        zip.closeEntry();

        byte[] bytes = output.toByteArray();

        zip.close();
        output.close();

        return bytes;
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

    public static void sendMessabeBytes(byte[] messageByte){
        DataOutputStream streamOut = Main.publicGUI.getStreamOut();
        try {
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
}
