package GUI;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultCaret;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;

public class MainGUI{

    //Objekt Klassen
    Messages msg = new Messages();

    //Variablen
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread client = null;

    private String serverName = "";
    private Integer serverPort = 0;
    private String username = "";

    byte[] message = null;

    //GUI
    public static MainGUI publicGUI;
    public JPanel panel;
    public JButton btn_sendMessage;
    public JTextArea textAreaMessages;
    public JTextField textFieldUsername;
    public JTextField textFieldClientMessage;
    public JButton btn_anmelden;


    public static void main(String[] args) {
        MainGUI mainGUI = new MainGUI();
        mainGUI.guiLoad();
    }

    public void guiLoad() {
        //Try-Block = Windows Design
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Erzeuge die GUI
        publicGUI = new MainGUI();
        JFrame frame = new JFrame("Client");
        frame.setContentPane(publicGUI.panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(frame.getSize());
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);

        //Eigenschaften der Componenten festlegen
        publicGUI.textAreaMessages.setEditable(false);
        publicGUI.textAreaMessages.setWrapStyleWord(true);
        publicGUI.textAreaMessages.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) publicGUI.textAreaMessages.getCaret(); //Auto Scroll von dem Update Log
        caret.setUpdatePolicy(ALWAYS_UPDATE);

        publicGUI.btn_sendMessage.setEnabled(false);

    }

    public void appendTextMessages(String message) {
        publicGUI.textAreaMessages.append(message + "\n");
    }

    public void connectToServer() {
        //Stellt verbindung mit dem Server her und startet den In-Output DataStream
        System.out.println("Connect... Please wait");
        try {
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

    public void reconnect(String serverName, Integer serverPort, String username) {
        //Falls der Server nicht zu erreichen ist wird immer wieder versucht bis der Server erreichbar ist
        //Hat keine Abbruch bedingung mit Absicht
        //Könnte man eine rein machen wenn der server beim zum beispiel 30ten versuch immer noch nicht erreichbar ist
        ChatClientThread cct = new ChatClientThread();

        if (thread != null) {
            thread.stop();
            thread.interrupt();
            socket = null;
            thread = null;
            console = null;
            streamOut = null;
            client = null;
            publicGUI.btn_anmelden.setEnabled(true);
            publicGUI.btn_sendMessage.setEnabled(false);
            cct.reset();
            publicGUI.setServerName(serverName);
            publicGUI.setServerPort(serverPort);
            publicGUI.setUsername(username);
        }
        try {
            if (console != null) console.close();
            if (streamOut != null) streamOut.close();
            if (socket != null) socket.close();
        } catch (IOException ioe) {
            System.out.println("Error closing ...");
        }

        for (int i = 0; i < 5; i++) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        publicGUI.setServerName(serverName);
        publicGUI.setServerPort(serverPort);
        publicGUI.setUsername(username);
        publicGUI.connectToServer();
    }

    public void sendeNachricht() {
        try {
            message = textFieldClientMessage.getText().getBytes();
            streamOut.writeInt(message.length);
            streamOut.write(message);
            streamOut.flush();
        } catch (IOException e) {
            msg.msgbox("Sending error: " + e.toString(), "ERROR", "ERROR");
            stop();
        }
    }

    public void handle(String msg) throws IOException {
        //Hier werden die ankommenden Nachrichten verarbeitet
        if (msg.equals("/bye")) {
            System.out.println("Good bye. Press RETURN to exit ...");
            stop();
        } else if (msg.equalsIgnoreCase("/pic")) {
            // convert byte array back to BufferedImage
            InputStream in = new ByteArrayInputStream(msg.getBytes());
            BufferedImage bImageFromConvert = ImageIO.read(in);
            if (bImageFromConvert != null) {
                ImageIO.write(bImageFromConvert, "PNG", new File("C:\\Users\\aaron\\Desktop\\asdfasdfawsdfasdfasdgdfshg.png"));
            }
        } else {
            publicGUI.textAreaMessages.append(msg);
        }
    }

    public void start() throws IOException {
        //Erstellt den DataInput und DataOutput Stream
        console = new DataInputStream(System.in);
        streamOut = new DataOutputStream(socket.getOutputStream());
        if (thread == null) {
            client = new ChatClientThread();
            client.connect(this, socket);
//            thread = new Thread((Runnable) this);
//            thread.start();
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

    //Listener
    public MainGUI() {
        btn_anmelden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (publicGUI.textFieldUsername.getText().equalsIgnoreCase("")) {
                    msg.msgbox("Gib ein Benutzername ein.", "Benutzername", "WARN");
                } else {
                    publicGUI.setServerName("127.0.0.1");
                    publicGUI.setServerPort(5555);
                    publicGUI.setUsername(publicGUI.textFieldUsername.getText());

                    publicGUI.btn_anmelden.setEnabled(false);
                    publicGUI.btn_sendMessage.setEnabled(true);
                    publicGUI.connectToServer();
                }
            }
        });

        btn_sendMessage.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendeNachricht();
            }
        });
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
