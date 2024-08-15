package com.teipreader.reptile.lib;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class Download_file {
    public static long getFileSize(String url) {
        long fileSize = 0;
        try {
            URL u = new URL(url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestMethod("HEAD");//请求行：HEAD/xxxHTTP/1.1
            fileSize = con.getContentLength();//获取响应的大小
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileSize;
    }
    public static boolean Dw_File(String dw_url, String save_as) throws MalformedURLException {
        int byteread;
        long longer=0;
        int r = 0;
        URL url = new URL(dw_url);
        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream(save_as);
            byte[] buffer = new byte[1024];
            while ((byteread = inStream.read(buffer)) != 1) {
                if (byteread == -1) {
                    break;
                }
                if (r >= 100) {
                    r = 0;
                }
                r = r + 1;
                fs.write(buffer, 0, byteread);
            }
            System.out.println("[Task]: "+dw_url + "->" + save_as + " [OK]");
        } catch (IOException e) {
            System.out.println("[Task]: "+dw_url + "->" + save_as + " [FAIL]");
            return false;
        }
        return true;
    }
}