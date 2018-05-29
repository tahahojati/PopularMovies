package com.tpourjalali.popularmovies;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class NetUtils {
    private NetUtils(){}
    public static String openPage(String urlstr) throws IOException {
        URL url = new URL(urlstr);
        InputStream os = url.openStream();
        ByteArrayOutputStream bas = new ByteArrayOutputStream();
        int character = os.read();
        while(character != -1){
            bas.write(character);
            character = os.read();
        }
        return bas.toString();
    }
}
