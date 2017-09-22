package Client;

import javax.swing.*;
import javax.swing.text.DefaultCaret;

import static javax.swing.text.DefaultCaret.ALWAYS_UPDATE;

public class MainGUI {

    //GUI
    public static MainGUI publicGUI;
    public JPanel panel;
    public JButton sendMessage;
    public JTextArea textAreaMessages;
    public JTextField textFieldUsername;
    public JTextField textFieldClientMessage;
    public JButton anmelden;


    public static void main(String[] args)
    {
        MainGUI mainGUI = new MainGUI();
        mainGUI.guiLoad();
    }

    public void guiLoad()
    {
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

        //Eigenschaften der Componenten festlegen
        publicGUI.textAreaMessages.setEditable(false);
        publicGUI.textAreaMessages.setWrapStyleWord(true);
        publicGUI.textAreaMessages.setLineWrap(true);
        DefaultCaret caret = (DefaultCaret) publicGUI.textAreaMessages.getCaret(); //Auto Scroll von dem Update Log
        caret.setUpdatePolicy(ALWAYS_UPDATE);

        frame.setVisible(true);
    }

    public void appendTextMessages(String message) {
        publicGUI.textAreaMessages.append(message + "\n");
    }

}
