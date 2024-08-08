package com.teipreader.webget;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

import static com.teipreader.reptile.lib.get_thing.ByJsonDownload;

public class Boot {
    public static String start_path = "./tmp/";
    public static void main(String[] args) throws IOException {
        System.setProperty("file.encoding", "UTF-8");
        System.out.println("commands (NoUI)");
        System.out.println("this <JsonFilePath> <StartURL>");
        //分支-不用imgui的
        if (args.length == 2) {
            ByJsonDownload(args[0], args[1]);
            return;
        }

        if (Main.class.getClassLoader().getResource("msyh.ttc") != null) {
            InputStream in = Objects.requireNonNull(Main.class.getClassLoader().getResource("msyh.ttc")).openStream();
            try (OutputStream ot = Files.newOutputStream(Paths.get("./msyh.ttc"))) {
                byte[] bytes = new byte[1024];
                int byteread;
                while ((byteread = in.read(bytes)) != -1) ot.write(bytes, 0, byteread);
            }
        }
        if (Main.class.getClassLoader().getResource("core-v0.22.0.jar") != null) {
            InputStream in = Objects.requireNonNull(Main.class.getClassLoader().getResource("core-v0.22.0.jar")).openStream();
            try (OutputStream ot = Files.newOutputStream(Paths.get("./core-v0.22.0.jar"))) {
                byte[] bytes = new byte[1024];
                int byteread;
                while ((byteread = in.read(bytes)) != -1) ot.write(bytes, 0, byteread);
            }
        }


        Main.start_window();
    }
}
