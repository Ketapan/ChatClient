package GUI.Message;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
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
        System.out.println("pdaten   "+pDaten.length);
        ArrayList<byte[]> byteListe = new ArrayList<>();
        byte[] entry2 = null;

        InputStream input = new ByteArrayInputStream(pDaten);
        ZipInputStream zip = new ZipInputStream(input);

        int anzahl = 0;

        while((zip.getNextEntry()) != null){
            byte[] daten = new byte[400000];
            anzahl = zip.read(daten);
            System.out.println("anzahl  "+anzahl);
            byte[] bla = new byte[anzahl];
            System.arraycopy(daten, 0, bla, 0, anzahl);
            System.out.println("bla length  "+bla.length);
            byteListe.add(bla);
        }

        zip.close();
        input.close();

        entry2 = byteListe.get(1);
        System.out.println("afterzip = "+ entry2.length);
        return entry2;
    }
}
