package GUI;

import javax.swing.*;

public class Messages {

    public void msgbox(String message, String title, String type)
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
}
