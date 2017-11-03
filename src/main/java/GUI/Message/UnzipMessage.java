package GUI.Message;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipInputStream;

public class UnzipMessage {

    public ArrayList unzip(byte[] pDaten) throws IOException
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

    public byte[] unzipEntry2(byte[] pDaten) throws IOException
    {
        ArrayList<byte[]> byteListe = new ArrayList<>();
        byte[] entry2 = null;
        InputStream input = new ByteArrayInputStream(pDaten);
        byte[] daten = new byte[2048];
        ZipInputStream zip = new ZipInputStream(input);
        int anzahl = 0;

        while((zip.getNextEntry()) != null){
            anzahl = zip.read(daten);
            byte[] bla = new byte[anzahl];
            System.arraycopy(daten, 0, bla, 0, anzahl);
            byteListe.add(bla);
        }

        zip.close();
        input.close();

        entry2 = byteListe.get(1);
        return entry2;
    }
}
