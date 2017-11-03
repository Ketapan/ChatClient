package GUI;

import GUI.Message.Messages;
import Listener.AnmeldenActionListener;
import Prozess.ChatClientThread;
import Prozess.SpeziellAction;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;

public class Main {

    //TODO: Reconnect Thread schreiben damit sich nicht die GUI aufhängt

    //Variablen
    private Socket socket = null;
    private Thread thread = null;
    private DataInputStream console = null;
    private DataOutputStream streamOut = null;
    private ChatClientThread client = null;

    private String serverName = "";
    private Integer serverPort = 0;
    private String username = "";

    //GUI
    public static Main publicGUI;
    public JPanel panel;
    public JTextArea textAreaMessages;
    public JTextField textFieldUsername;
    public JTextField textFieldClientMessage;
    public JButton btn_anmelden;
    public JTextField textFieldIPAdresse;
    public JTextField textFieldPort;
    public DefaultListModel<String> userlistModel = new DefaultListModel<>();
    public JList<String> userList;
    private JButton btn_clearSelectionUserlist;

    public static void main(String[] args) {
        Main main = new Main();
        main.guiLoad();
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
        publicGUI = new Main();
        JFrame frame = new JFrame("Client");
//        frame.setPreferredSize(new Dimension(700,450));
        frame.setContentPane(publicGUI.panel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(frame.getSize());
        frame.setMinimumSize(frame.getSize());
        frame.setVisible(true);

        //Für Testzwecke IP, PORT, USERNAME bereits eingetragen
        publicGUI.textFieldIPAdresse.setText("127.0.0.1");
        publicGUI.textFieldPort.setText("5555");
        publicGUI.textFieldUsername.setText("deiMudda");

        //Eigenschaften der Componenten festlegen
        publicGUI.textAreaMessages.setEditable(false);
        publicGUI.textAreaMessages.setWrapStyleWord(true);
        publicGUI.textAreaMessages.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) publicGUI.textAreaMessages.getCaret(); //Auto Scroll
        caret.setUpdatePolicy(ALWAYS_UPDATE);

        publicGUI.userList.setModel(publicGUI.userlistModel);
        publicGUI.userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        publicGUI.userList.setLayoutOrientation(JList.VERTICAL);
        publicGUI.userList.setVisibleRowCount(-1);

        publicGUI.textFieldClientMessage.setEnabled(false);

        frame.setVisible(true);
    }

    public void connectToServer() {
        //Stellt verbindung mit dem Server her und startet den In-Output DataStream
        Messages.appendTextMessage("Connect... Please wait");
        try {
            socket = new Socket(getServerName(), getServerPort());
            Messages.appendTextMessage("Connected: " + socket);
            start();
            byte[] byteUsername = username.getBytes();
            streamOut.writeInt(byteUsername.length);
            streamOut.write(byteUsername);

//            Thread stThread = new Thread(speichernThread);
//            stThread.start();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            reconnect(getServerName(), getServerPort(), getUsername());
        }
    }

    public void reconnect(String serverName, Integer serverPort, String username) {
        //Falls der Server nicht zu erreichen ist wird immer wieder versucht bis der Server erreichbar ist
        //Hat keine Abbruch bedingung mit Absicht
        //Man könnte eine rein machen wenn der server beim zum beispiel 30ten versuch immer noch nicht erreichbar ist
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
            publicGUI.textFieldClientMessage.setEnabled(false);
            publicGUI.textFieldUsername.setEnabled(true);
            publicGUI.textFieldPort.setEnabled(true);
            publicGUI.textFieldIPAdresse.setEnabled(true);
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
            Messages.appendTextMessage("Error closing..." + ioe.getMessage());
        }

        int z = 0;
        for (int i = 0; i < 30000; i++) {
            z++;
        }
        publicGUI.setServerName(serverName);
        publicGUI.setServerPort(serverPort);
        publicGUI.setUsername(username);
        publicGUI.connectToServer();
    }

    public void handle(String msg) throws IOException {
        //Hier werden die ankommenden Nachrichten verarbeitet
        if (msg.equals("/bye")) {
            Messages.appendTextMessage("Good bye. Close in 2 Seconds...");
//            stop();
            try {
                Thread.sleep(2000);
                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (msg.startsWith("/pic")) {
            String temp = msg;
            temp = temp.substring(4, temp.length());
            BufferedImage bImageFromConvert = SpeziellAction.base64StringToImg(temp);
            if (bImageFromConvert != null) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image img = toolkit.createImage(bImageFromConvert.getSource());
                //Erzeuge die GUI
                JFrame frame = new JFrame("Screenshot");
                frame.getContentPane().add(new PicturePanel(img));
                frame.setSize(800, 400);
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            }
        } else if (msg.startsWith("/addwho")) {
            msg = msg.substring(7, msg.length());
            publicGUI.userlistModel.addElement(msg);
        } else if (msg.startsWith("/refreshList")) {
            publicGUI.userlistModel.clear();
        } else if(msg.equalsIgnoreCase("/vergeben")){
            Messages.appendTextMessage("Username bereits vergeben");
            Messages.appendTextMessage("Verbindung zum Server getrennt");
            stop();
        }
        else {
            Messages.appendTextMessage(msg);
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
            Messages.appendTextMessage("Error closing ..." + ioe.getMessage());
        }
        publicGUI.btn_anmelden.setEnabled(true);

        publicGUI.textFieldClientMessage.setEnabled(false);
        publicGUI.textFieldUsername.setEnabled(true);
        publicGUI.textFieldPort.setEnabled(true);
        publicGUI.textFieldIPAdresse.setEnabled(true);

        Messages.appendTextMessage("");
        client.close();
        client.stop();
    }


    //Listener
    public Main() {
        btn_anmelden.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AnmeldenActionListener anmeldenListener = new AnmeldenActionListener();
                anmeldenListener.anmelden();
            }
        });

        btn_clearSelectionUserlist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Main.publicGUI.userList.clearSelection();
            }
        });

        textFieldClientMessage.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    if (publicGUI.textFieldClientMessage.getText().equals("")) {
                        Messages.msgbox("Gib zuerst eine Nachricht ein.", "Nachicht", "WARN");
                    } else {
                        if(publicGUI.userList.isSelectionEmpty())
                        {
//                            Messages.sendMessage();
                            Messages.sendZipMessage("alle", publicGUI.textFieldClientMessage.getText(), "msg");
                        } else {
                            SpeziellAction.handleSendingPrivateMessages(publicGUI.textFieldClientMessage.getText());
                        }
                    }
                }
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

    public DataOutputStream getStreamOut() {
        return streamOut;
    }
}
