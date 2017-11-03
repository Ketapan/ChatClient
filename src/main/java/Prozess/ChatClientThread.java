package Prozess;

import GUI.Main;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

public class ChatClientThread extends Thread {
    private Socket socket = null;
    private Main client = null;
    private DataInputStream streamIn = null;

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
            streamIn = new DataInputStream(socket.getInputStream());
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
                int length = streamIn.readInt();
                String messageAsString = "";
                if (length > 0) {
                    messageBytes = new byte[length];
                    streamIn.readFully(messageBytes, 0, length);
                    messageAsString = new String(messageBytes);
                }
                client.handle(messageAsString);
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
