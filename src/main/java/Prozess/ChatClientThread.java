package Prozess;

import GUI.Main;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

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

    private ArrayList<String> unzip(byte[] pDaten) throws IOException
    {
        InputStream input = new ByteArrayInputStream(pDaten);
        byte[] daten = new byte[2048];
        ZipInputStream zip = new ZipInputStream(input);
        int anzahl = 0;
        ArrayList<String> ergebnisListe = new ArrayList<>();

        while((zip.getNextEntry()) != null){
            anzahl = zip.read(daten);
            byte[] bla = new byte[anzahl];
            System.arraycopy(daten, 0, bla, 0, anzahl);
            ergebnisListe.add(new String(bla));
        }

        zip.close();
        input.close();

        return ergebnisListe;
    }
}
