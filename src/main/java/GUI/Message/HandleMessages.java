package GUI.Message;

import GUI.Main;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

public class HandleMessages {

    public void messageHandling(byte[] messageBytes) {
        //TODO: Hier soll die verarbeitung der einzelnen nachrichten stattfinden

        ArrayList<String> messageList;
        String messageTo;
        String message;
        String type;
        Main client = new Main();

        try {
            messageList = unzip(messageBytes);
            if(messageList.size() == 3){
                messageTo = messageList.get(0);
                message = messageList.get(1);
                type = messageList.get(2);

                //verschiedene methoden aufrufen jenach type der message
                //oder immer die gleiche methode aufrufen unnd mit einer switch-case anweisung dann weitere methoden aufrufen

                if(type.equalsIgnoreCase("exit")){
                    Messages.appendTextMessage("Good bye. Close in 2 Seconds...");
                    try {
                        Thread.sleep(2000);
                        System.exit(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Main.appendTextMessage(message);
                }

            } else {
                Main.appendTextMessage("Fehler beim einlesen der Nachricht");
            }
        } catch (IOException e) {
            Main.appendTextMessage(e.getLocalizedMessage());
        }
    }

    private ArrayList<String> unzip(byte[] zipBytes) throws IOException
    {
        ArrayList<String> ergebnisListe = new ArrayList<>();

        if(zipBytes != null){
            InputStream input = new ByteArrayInputStream(zipBytes);
            byte[] daten = new byte[2048];
            ZipInputStream zip = new ZipInputStream(input);
            int length;

            while((zip.getNextEntry()) != null){
                length = zip.read(daten);
                byte[] unzipByte = new byte[length];
                System.arraycopy(daten, 0, unzipByte, 0, length);
                ergebnisListe.add(new String(unzipByte));
            }

            zip.close();
            input.close();
        } else{
            Main.appendTextMessage("Fehler beim Entpacken der Nachricht (byte == null)");
        }

        return ergebnisListe;
    }
}
