package Simple;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient implements Runnable {
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread client = null;

    private String serverName = "";
    private Integer serverPort = 0;
    private String username = "";

    byte[] message = null;

    //TODO: Gucken was beim Server noch "offen" ist


    public static void main(String args[]) {
        //Meldet den Client an
        ChatClient client = new ChatClient();
        client.setServerName("127.0.0.1");
        client.setServerPort(5555);
        client.setUsername("iBims");
        client.connectToServer();
    }

    public void connectToServer()
    {
        //Stellt verbindung mit dem Server her und startet den In-Output DataStream
        System.out.println("Connect... Please wait");
        try
        {
            socket = new Socket(getServerName(), getServerPort());
            System.out.println("Connected: " + socket);
            start();
            byte[] byteUsername = username.getBytes();
            streamOut.writeInt(byteUsername.length);
            streamOut.write(byteUsername);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            reconnect(getServerName(), getServerPort(), getUsername());
        }
    }

    public void reconnect(String serverName, Integer serverPort, String username)
    {
        //Falls der Server nicht zu erreichen ist wird immer wieder versucht bis der Server erreichbar ist
        //Hat keine Abbruch bedingung mit Absicht
        //Könnte man eine rein machen wenn der server beim zum beispiel 30ten versuch immer noch nicht erreichbar ist
        ChatClient cc = new ChatClient();
        ChatClientThread cct = new ChatClientThread();

        if (thread != null) {
            thread.stop();
            thread.interrupt();
            socket = null;
            thread = null;
            console = null;
            streamOut = null;
            client = null;
            cct.reset();
            cc.setServerName(serverName);
            cc.setServerPort(serverPort);
            cc.setUsername(username);
        }
        try {
            if (console != null) console.close();
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            System.out.println("Error closing ...");
        }

        for(int i = 0; i < 5; i++)
        {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        cc.setServerName(serverName);
        cc.setServerPort(serverPort);
        cc.setUsername(username);
        cc.connectToServer();
    }

    public void run() {
        //Die bytes werden verschickt
        while (thread != null) {
            try {
                message = console.readLine().getBytes();
                streamOut.writeInt(message.length);
                streamOut.write(message);
                streamOut.flush();
            } catch (IOException ioe) {
                System.out.println("Sending error: " + ioe.getMessage());
                stop();
            }
        }
    }

    public void handle(String msg) throws IOException {
        //Hier werden die ankommenden Nachrichten verarbeitet
        if (msg.equals("/bye")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            stop();
        } else if (msg.equalsIgnoreCase("/pic"))
        {
            // convert byte array back to BufferedImage
            InputStream in = new ByteArrayInputStream(msg.getBytes());
            BufferedImage bImageFromConvert = ImageIO.read(in);
            if(bImageFromConvert != null)
            {
                ImageIO.write(bImageFromConvert, "PNG", new File("C:\\Users\\aaron\\Desktop\\asdfasdfawsdfasdfasdgdfshg.png"));
            }
        }
        else {
            System.out.println(msg);
        }
    }

    public void start() throws IOException {
        //Erstellt den DataInput und DataOutput Stream
        console = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            client = new ChatClientThread();
            client.connect(this, socket);
            thread = new Thread(this);
            thread.start();
        }
    }

    public void stop() {
        if (thread != null) {
            thread.stop();
            thread = null;
        }
        try {
            if (console != null) console.close();
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            System.out.println("Error closing ...");
        }
        client.close();
        client.stop();
    }

    private void makeScreenshot() {
        /*
            Macht einen Screenshot wandelt diesen in Bytes um und schickt ihn an den Server
            Problem:
            -> Verarbeitung auf der Server seite funktioniert nicht richtig da die bytes des Screenshots auf dem gleichen Kanal ankommen wie normale "Nachrichten" bytes
            -> Man müsste das "Paket" irgendwie markieren damit der Server den unterschied erkennt
            -> Ähnlich wie bei den Privaten Nachrichten nur noch etwas mehr ausgearbeiteter und ohne das die bytes des Screenshots fehler enthalten
            -> Zum beispiel durch etwas hinzufügen der bytes :D (hat nicht funktioniert)
         */
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

            streamOut.writeInt(imageInByte.length);
            streamOut.write(imageInByte);
            streamOut.flush();

        } catch (IOException | AWTException e) {
            e.printStackTrace();
        }
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public Integer getServerPort() {
        return serverPort;
    }

    public void setServerPort(Integer serverPort) {
        this.serverPort = serverPort;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}


/**
 * bytes senden
 *
 * Zum Senden von bytes:
 *
 * byte[] message = ...
 * Socket socket = ...
 * DataOutputStream dOut = new DataOutputStream(socket.getOutputStream());
 *
 * dOut.writeInt(message.length); // write length of the message
 * dOut.write(message);
 **/

/**
 * bytes empfangen
 *
 * Zum Empfangen von bytes:
 *
 * Socket socket = ...
 * DataInputStream dIn = new DataInputStream(socket.getInputStream());
 *
 * int length = dIn.readInt();                    // read length of incoming message
 * if(length>0) {
 * byte[] message = new byte[length];
 * dIn.readFully(message, 0, message.length); // read the message
 * }
 **/