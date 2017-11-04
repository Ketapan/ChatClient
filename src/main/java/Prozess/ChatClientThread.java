package Prozess;

import GUI.Main;
import GUI.Message.UnzipMessage;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class ChatClientThread extends Thread {
    private Socket socket = null;
    private Main client = null;
    //private DataInputStream streamIn = null;
    private ObjectOutputStream streamIn =null;

    private boolean isListening = false;

    private byte[] messageBytes = null;

    public void connect(Main _client, Socket _socket)
    {
        isListening = true;
        client = _client;
        socket = _socket;
        open();
        start();
    }

    public void open() {
        try {
            streamIn = new ObjectOutputStream(socket.getOutputStream());
            //streamIn = new DataInputStream(socket.getInputStream());
        } catch (IOException ioe) {
            System.out.println("Error getting input stream: " + ioe);
            client.stop();
        }
    }

    public void close() {
        try {
            if (streamIn != null) streamIn.close();
        } catch (IOException ioe) {
            System.out.println("Error closing input stream: " + ioe);
        }
    }

    public void reset(){
        this.socket = null;
        this.client = null;
        close();
    }

    public void run() {
        while (isListening) {
            try {
                ArrayList messageList;
                UnzipMessage unzipMSG = new UnzipMessage();
                //int length = streamIn.readInt();
                String messageAsString = "";
                String messageType = "";
                byte[] messageAsByte = null;

                if (streamIn != null) {
                    //messageBytes = new byte[length];
                    //streamIn.readFully(messageBytes, 0, length);
                    messageList = unzipMSG.unzip(messageBytes);
                    messageAsByte = unzipMSG.unzipEntry2(messageBytes);
                    messageAsString = messageList.get(1).toString();
                    messageType = messageList.get(2).toString();
                }
                client.handle(messageAsString, messageType, messageAsByte);
            } catch (IOException ioe) {
                Main.publicGUI.textAreaMessages.append("Listening error: " + ioe.getMessage());
//                client.stop();
                isListening = false;
                client.reconnect(client.getServerName(), client.getServerPort(), client.getUsername());
                break;
            }

        }
    }
}
