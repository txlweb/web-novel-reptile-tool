package com.teipreader.reptile.lib;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class File_use {
    private static final char[] hexCode = "0123456789abcdef".toCharArray();

    public static void allClose(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String ReadFileText(String strFilePath) {
        String r = "";
        List<String> a = ReadCFGFile(strFilePath);
        for (String s : a) {
            r = r + s;
        }
        return r;
    }

    public static List<String> ReadCFGFile(String strFilePath) {
        File file = new File(strFilePath);
        List<String> rstr = new ArrayList<>();
        if (!file.exists() || file.isDirectory()) {
            System.out.println((char) 27 + "[31m[E]: 找不到文件." + (char) 27 + "[39;49m");
            return rstr;
        } else {
            FileInputStream fileInputStream = null;
            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileInputStream = new FileInputStream(file);
                inputStreamReader = new InputStreamReader(fileInputStream, EncodingDetect.getJavaEncode(strFilePath));
                bufferedReader = new BufferedReader(inputStreamReader);
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    rstr.add(str);
                }
                return rstr;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                allClose(bufferedReader, inputStreamReader, fileInputStream);
            }
        }
        return rstr;
    }


    public static void CopyFileToThis(File source, File dest) throws IOException {
        try (FileChannel inputChannel = new FileInputStream(source).getChannel(); FileChannel outputChannel = new FileOutputStream(dest).getChannel()) {
            outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
        }
    }

    public static String getFileMD5(String fileName) {
        File file = new File(fileName);
        try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    public static void compressFolder(String sourceFolder, String folderName, ZipOutputStream zipOutputStream) throws IOException {
        File folder = new File(sourceFolder);
        File[] files = folder.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // 压缩子文件夹
                    compressFolder(file.getAbsolutePath(), folderName + "/" + file.getName(), zipOutputStream);
                } else {
                    // 压缩文件
                    addToZipFile(folderName + "/" + file.getName(), file.getAbsolutePath(), zipOutputStream);
                }
            }
        }
    }

    public static void addToZipFile(String fileName, String fileAbsolutePath, ZipOutputStream zipOutputStream) throws IOException {
        // 创建ZipEntry对象并设置文件名
        ZipEntry entry = new ZipEntry(fileName);
        zipOutputStream.putNextEntry(entry);

        // 读取文件内容并写入Zip文件
        try (FileInputStream fileInputStream = new FileInputStream(fileAbsolutePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                zipOutputStream.write(buffer, 0, bytesRead);
            }
        }

        // 完成当前文件的压缩
        zipOutputStream.closeEntry();
    }
}
