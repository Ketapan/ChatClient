package Listener;

import GUI.Main;
import GUI.Message.Messages;


public class AnmeldenActionListener {
    private Messages messages = new Messages();

    public void anmelden() {
        String username = Main.publicGUI.textFieldUsername.getText();
        String ip = Main.publicGUI.textFieldIPAdresse.getText();
        String port = Main.publicGUI.textFieldPort.getText();

        if (username.equals("") || ip.equals("") || port.equals("")) {
            messages.msgbox("Trag zuerst die IP-Adresse, Port und Benutzername ein.", "Benutzername", "WARN");
        }
        else {
            Main.publicGUI.setServerName(ip);
            Main.publicGUI.setServerPort(Integer.parseInt(port));
            Main.publicGUI.setUsername(Main.publicGUI.textFieldUsername.getText());

            Main.publicGUI.connectToServer();
            Main.publicGUI.btn_anmelden.setEnabled(false);
            Main.publicGUI.textFieldClientMessage.setEnabled(true);
            Main.publicGUI.textFieldUsername.setEnabled(false);
            Main.publicGUI.textFieldIPAdresse.setEnabled(false);
            Main.publicGUI.textFieldPort.setEnabled(false);
        }
    }
}
