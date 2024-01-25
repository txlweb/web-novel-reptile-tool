import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.teipreader.reptile.lib.ThingIO;
import com.teipreader.reptile.lib.get_thing;
import org.ice1000.jimgui.*;
import org.ice1000.jimgui.flag.JImWindowFlags;
import org.ice1000.jimgui.util.JniLoader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static com.teipreader.reptile.lib.File_use.ReadFileText;
import static com.teipreader.reptile.lib.get_thing.ByJsonDownload;
import static com.teipreader.reptile.lib.get_thing.getStringBySplit;

public class Main {

    static boolean Is_fail = false;
    static String dw_p = null;
    public static void main(String[] args) throws IOException {
        new File("imgui.ini").delete();
        new File("./rules/").mkdir();

        JniLoader.load();
        JImGui imGui = new JImGui("web reptile tool   By.IDlike Github: https://github.com/txlweb/");
        JImGuiIO imGio = imGui.getIO();
        //导出字库
        if (Main.class.getClassLoader().getResource("msyh.ttc") != null || new File("./msyh.ttc").isFile())
            imGio.getFonts().addFontFromFile("./msyh.ttc",20.0f,new JImFontConfig(),imGio.getFonts().getGlyphRangesForChineseFull());
        else//测试环境用的
            imGio.getFonts().addFontFromFile("./font/msyh.ttc",20.0f,new JImFontConfig(),imGio.getFonts().getGlyphRangesForChineseFull());
        NativeBool use_debug = new NativeBool();
        while (!imGui.windowShouldClose()) {
            imGui.initNewFrame();
            get_thing.debugM=use_debug.accessValue();
            if(ThingIO.ot()==null) {
                imGui.begin("下载小说");
                imGui.text("选择模板");
                imGui.text("注意:这些模板都不是由作者提供的,被爬的网站也不是作者找的,所以不保证能爬成功,也不保证是否有风险.模板存放在./rules文件夹,编写规则在readme.md中已提供.");
                imGui.text("注意:可以读取不等于可以用于下载,即使可以读取也可能不能下载.");
                imGui.checkbox("在控制台打印调试信息", use_debug);
                File file = new File("./rules/");
                File[] files = file.listFiles();
                if (files != null) {
                    for (File value : files) {
                        if (value.isFile()) {
                            imGui.text("------------------------------------");
                            boolean x = true;
                            try {
                                JsonObject jx = (JsonObject) new JsonParser().parse(new FileReader("./rules/" + value.getName()));
                            } catch (Exception e) {
                                x = false;
                                imGui.button("[无法解析]" + value.getName());
                            }
                            if (x) {
                                if (imGui.button(value.getName()) && get_thing.ot == null) {
                                    start_command t = new start_command();
                                    t.task("./rules/" + value.getName(), Window_Input(imGui, "输入起始URL", "输入起始URL", ""));
                                    t.start();
                                }
                            }

                        }
                    }
                }
            }
            if(Is_fail){
                Is_fail=Window_y_n(imGui,"下载失败!","下载失败,请检查规则文件和初始链接是否正常.");
            }
            if(ThingIO.ot()!=null){
                imGui.begin("下载中...");
                imGui.text(AutoBR(ThingIO.ot(),20));
                imGui.setWindowSize("下载中...",500,300);
                if(imGui.button("取消下载")){
                    ThingIO.close_task=true;
                }
            }
            imGui.render();
        }
        return;
    }
    public static void NS_add_string(NativeString ns,String text){
        //逆天!居然得一个一个字节压进去(
        byte[] a = text.getBytes();
        for (byte b : a) {
            ns.append(b);
        }
    }
    public static String Window_Input(JImGui imGui,String Title,String say,String auto_thing){
        NativeString out = new NativeString();
        NS_add_string(out,auto_thing);
        while (!imGui.windowShouldClose()) {
            imGui.initNewFrame();
            imGui.begin(Title,new NativeBool(), JImWindowFlags.NoTitleBar);
            imGui.setWindowSize(Title,400,300);
            imGui.text(Title);
            imGui.text("");
            imGui.text(AutoBR(say, 40));
            imGui.inputText("", out);
            if (imGui.button("提 交")) {
                return out.toString();
            }
            imGui.sameLine();

            imGui.render();
        }
        return out.toString();
    }
    public static String AutoBR(String text,int line_size){
        String[] t = text.split("");
        int s = 0;
        StringBuilder rt= new StringBuilder();
        for (String string : t) {
            s++;
            if (s > line_size) {
                s = 0;
                rt.append("\r\n");
            }
            rt.append(string);
        }
        return rt.toString();
    }
    public static boolean Window_y_n(JImGui imGui,String Title,String say){
        while (!imGui.windowShouldClose()) {
            imGui.initNewFrame();
            imGui.begin(Title,new NativeBool(), JImWindowFlags.NoTitleBar);
            imGui.setWindowSize(Title,400,300);
            imGui.text(Title);
            imGui.text("");
            imGui.text(say);
            if (imGui.button("是(Y)")) {
                return true;
            }
            imGui.sameLine();
            if (imGui.button("否(N)")) {
                return false;
            }
            imGui.render();
        }
        return false;
    }
}