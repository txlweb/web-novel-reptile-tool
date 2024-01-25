package com.teipreader.reptile.lib;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.tools.javac.Main;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import static com.teipreader.reptile.lib.File_use.compressFolder;
import static com.teipreader.reptile.lib.ThingIO.close_task;

public class get_thing {
    public static boolean debugM = false;
    public static String ot = null;
    public static String getStringBySplit(String in, String Slips, int id){
        if(debugM) System.out.println(in);
        if(debugM) System.out.println(Slips);
        if(debugM) System.out.println(id);
        if(id == -1) return in;
        String[] a = in.split(Slips);
        if(a.length <= id) return in;//给的ID太大,直接返回原值
        return a[id];
    }
    public static List<String> GetList (String url,String start_tag,String split_tag_1,int split_id_1,String split_tag_2,int split_id_2,boolean Line_mode) throws MalformedURLException {
        new File("./tmp-list.txt").delete();
        Download_file.Dw_File(url,"./tmp-list.txt");
        StringBuilder t = new StringBuilder();
        List<String> ret = new ArrayList<>();
        boolean start = false;
        for (String s : File_use.ReadCFGFile("./tmp-list.txt")) {
            if(Line_mode){
                if(s.contains(start_tag)) start=true;//找到关键词才开始
                if(start) {
                    String m = getStringBySplit(getStringBySplit(s, split_tag_1, split_id_1), split_tag_2, split_id_2);
                    if (!Objects.equals(m, s)) ret.add(m);
                }
            }else{
                t.append(s).append("\r\n");
            }
        }
        if(!Line_mode){
            String m = getStringBySplit(getStringBySplit(t.toString(),split_tag_1,split_id_1),split_tag_2,split_id_2);
            if(!Objects.equals(m, t.toString())) ret.add(m);
        }
        return ret;
    }
    public static String GetString (String url,String start_tag,String split_tag_1,int split_id_1,String split_tag_2,int split_id_2,int ID) throws MalformedURLException {
        if(debugM) System.out.println(url);
        new File("./tmp-string.txt").delete();
        Download_file.Dw_File(url,"./tmp-string.txt");
        boolean start = false;
        int idd = 0;
        for (String s : File_use.ReadCFGFile("./tmp-string.txt")) {
            if(s.contains(start_tag)) start=true;//找到关键词才开始
            if(start) {
                String m = getStringBySplit(getStringBySplit(s, split_tag_1, split_id_1), split_tag_2, split_id_2);
                if (!Objects.equals(m, s)) {
                    if (idd == ID) return m;
                    idd++;
                }
            }
        }
        return "";
    }
    public static String GetText (String url,String start_tag,String end_tag) throws MalformedURLException {
        new File("./tmp-string.txt").delete();
        Download_file.Dw_File(url,"./tmp-string.txt");
        boolean start = false;
        StringBuilder t = new StringBuilder();
        List<String> f = File_use.ReadCFGFile("./tmp-string.txt");
        for (String s : f) {
            if(s.contains(start_tag)) start=true;//找到关键词才开始
            if(start) {
                if(debugM) System.out.println(s);
                t.append(s);
                if(s.contains(end_tag)){return t.toString();}
            }
        }
        return t.toString();
    }
    //{
    // "mode":"list",
    // "list":{"start":"","S1":"","S2":"","I1":"","I2":""},
    // "title":{"start":"","S1":"","S2":"","I1":"","I2":"","id:"0"},
    // "text":{"start":"","end":""},
    // "name":{"start":"","S1":"","S2":"","I1":"","I2":"","id:"0"},
    // "by":{"start":"","S1":"","S2":"","I1":"","I2":"","id:"0"},
    // "ot":{"start":"","S1":"","S2":"","I1":"","I2":"","id:"0"},
    // "im":{"start":"","S1":"","S2":"","I1":"","I2":"","id:"0"},
    // "url_add":""
    // }
    public static boolean ByJsonDownload(String json_file,String start_url) throws IOException {
        JsonObject jx = null;
        try {
            jx = (JsonObject) new JsonParser().parse(new FileReader(json_file));
        } catch (Exception e) {
            System.out.println("E: 解析json失败");
            return false;
        }
        //解析模式信息
        if(Objects.equals(jx.get("mode").getAsString(), "list")){//列表模式
            ThingIO.out_text = "正在获取数据...";
            //获取标题/图片/作者/简介
            String name = GetString(
                    start_url,
                    jx.get("name").getAsJsonObject().get("start").getAsString(),
                    jx.get("name").getAsJsonObject().get("S1").getAsString(),
                    jx.get("name").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("name").getAsJsonObject().get("S2").getAsString(),
                    jx.get("name").getAsJsonObject().get("I2").getAsInt(),
                    jx.get("name").getAsJsonObject().get("id").getAsInt()
            );
            System.out.println("标题:"+name);
            String img = GetString(
                    start_url,
                    jx.get("im").getAsJsonObject().get("start").getAsString(),
                    jx.get("im").getAsJsonObject().get("S1").getAsString(),
                    jx.get("im").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("im").getAsJsonObject().get("S2").getAsString(),
                    jx.get("im").getAsJsonObject().get("I2").getAsInt(),
                    jx.get("im").getAsJsonObject().get("id").getAsInt()
            );
            System.out.println("图片:"+img);

            String by = GetString(
                    start_url,
                    jx.get("by").getAsJsonObject().get("start").getAsString(),
                    jx.get("by").getAsJsonObject().get("S1").getAsString(),
                    jx.get("by").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("by").getAsJsonObject().get("S2").getAsString(),
                    jx.get("by").getAsJsonObject().get("I2").getAsInt(),
                    jx.get("by").getAsJsonObject().get("id").getAsInt()
            );
            System.out.println("作者:"+by);

            String ot = GetString(
                    start_url,
                    jx.get("ot").getAsJsonObject().get("start").getAsString(),
                    jx.get("ot").getAsJsonObject().get("S1").getAsString(),
                    jx.get("ot").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("ot").getAsJsonObject().get("S2").getAsString(),
                    jx.get("ot").getAsJsonObject().get("I2").getAsInt(),
                    jx.get("ot").getAsJsonObject().get("id").getAsInt()
            );
            System.out.println("简介:"+ot);

            //先获取url列表
            List<String> ul = GetList(
                    start_url,
                    jx.get("list").getAsJsonObject().get("start").getAsString(),
                    jx.get("list").getAsJsonObject().get("S1").getAsString(),
                    jx.get("list").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("list").getAsJsonObject().get("S2").getAsString(),
                    jx.get("list").getAsJsonObject().get("I2").getAsInt(),
                    true
            );

            //新建一个buffer写入
            new File("./dw_txt.txt").delete();
            new File("./dw_txt.txt").createNewFile();
            FileWriter fileWriter_txt = new FileWriter(new File("./dw_txt.txt").getName(), true);
            BufferedWriter bufferWriter_txt = new BufferedWriter(fileWriter_txt);
            //根据URL列表访问并下载
            for (int i = 0; i < ul.size(); i++) {
                if(close_task){
                    close_task=false;
                    return false;
                }
                System.out.println(i+"/"+ul.size());
                String title = GetString(
                        jx.get("url_add").getAsString()+ul.get(i),
                        jx.get("title").getAsJsonObject().get("start").getAsString(),
                        jx.get("title").getAsJsonObject().get("S1").getAsString(),
                        jx.get("title").getAsJsonObject().get("I1").getAsInt(),
                        jx.get("title").getAsJsonObject().get("S2").getAsString(),
                        jx.get("title").getAsJsonObject().get("I2").getAsInt(),
                        jx.get("title").getAsJsonObject().get("id").getAsInt()
                );
                String text = GetText(
                        jx.get("url_add").getAsString()+"/"+ul.get(i),
                        jx.get("text").getAsJsonObject().get("start").getAsString(),
                        jx.get("text").getAsJsonObject().get("end").getAsString()
                );
                ThingIO.out_text = "正在下载:"+name+"\r\n作者:"+by+"\r\n简介:"+ot+"\r\n进度: "+i+"/"+ul.size()+" ("+title+")";
                bufferWriter_txt.write(title+"\r\n"+text+"\r\n");
                //System.out.println(text);
            }
            bufferWriter_txt.close();
            //buffer写入一个配置文件
            new File("./resource.ini").delete();
            new File("./resource.ini").createNewFile();
            FileWriter fileWriter_info = new FileWriter(new File("./resource.ini").getName(), true);
            BufferedWriter bufferWriter_info = new BufferedWriter(fileWriter_txt);
            bufferWriter_info.write("[conf]\r\n");
            bufferWriter_info.write("icon = icon.jpg");
            bufferWriter_info.write("title = "+name+"\r\n");
            bufferWriter_info.write("by = "+by+"\r\n");
            bufferWriter_info.write("ot = "+ot+"\r\n");
            bufferWriter_info.write("say = 本小说由IDlike提供的工具下载,工具可以从https://github.com/txlweb/web-novel-reptile-tool获取.\r\n");
            bufferWriter_info.close();
            //准备制作teip
            //获取md5
            String MD5 = File_use.getFileMD5("./dw_txt.txt");
            new File(MD5).mkdir();
            File_use.CopyFileToThis(new File("./dw_txt.txt"),new File("./"+MD5+"/main.txt"));
            File_use.CopyFileToThis(new File("./resource.ini"),new File("./"+MD5+"/resource.ini"));
            Download_file.Dw_File(img,"icon.jpg");
            File_use.CopyFileToThis(new File("./icon.jpg"),new File("./"+MD5+"/icon.jpg"));
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream("out_"+MD5+".teip2"))) {
                compressFolder("./"+MD5, "./"+MD5, zipOutputStream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(Objects.equals(jx.get("mode").getAsString(), "tool")){
            //{"mode":"tool","in":"[工具-清除空行]打开一个文件","id":"clnop"}
            //{"mode":"tool","in":"[工具-批量替换]打开一个文件","id":"repall"}

        }
        ThingIO.out_text = null;
        return true;
    }
}

