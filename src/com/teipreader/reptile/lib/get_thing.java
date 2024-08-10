package com.teipreader.reptile.lib;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.org.apache.xpath.internal.operations.Bool;
import com.teipreader.webget.Main;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import static com.teipreader.reptile.lib.File_use.ReadCFGFile;
import static com.teipreader.reptile.lib.File_use.compressFolder;
import static com.teipreader.reptile.lib.PreText_pro.TextProgressBar;
import static com.teipreader.reptile.lib.ThingIO.close_task;
import static com.teipreader.webget.Boot.atd;
import static com.teipreader.webget.Boot.start_path;
import static com.teipreader.webget.Main.AutoBR;

public class get_thing {
    public static boolean debugM = false;
    public static String ot = null;

    public static String getStringBySplit(String in, String Slips, int id) {
        if (debugM) System.out.println(in);
        if (debugM) System.out.println(Slips);
        if (debugM) System.out.println(id);
        if (id == -1) return in;
        String[] a = in.split(Slips);
        if (a.length <= id) return in;//给的ID太大,直接返回原值
        return a[id];
    }

    public static List<String> GetList(String url, String start_tag, String split_tag_1, int split_id_1, String split_tag_2, int split_id_2, boolean Line_mode) throws IOException {
        String id = UUID.randomUUID().toString();
        new File(start_path+"/"+id).delete();
        Download_file.Dw_File(url, start_path+"/"+id);
        StringBuilder t = new StringBuilder();
        List<String> ret = new ArrayList<>();
        boolean start = false;
        for (String s : ReadCFGFile(start_path+"/"+id)) {
            if (Line_mode) {
                if (s.contains(start_tag)) start = true;//找到关键词才开始
                if (start) {
                    String m = getStringBySplit(getStringBySplit(s, split_tag_1, split_id_1), split_tag_2, split_id_2);
                    if (!Objects.equals(m, s)) ret.add(m);
                }
            } else {
                t.append(s).append("\r\n");
            }
        }
        new File(start_path+"/"+id).delete();
        if (!Line_mode) {
            String m = getStringBySplit(getStringBySplit(t.toString(), split_tag_1, split_id_1), split_tag_2, split_id_2);
            if (!Objects.equals(m, t.toString())) ret.add(m);
        }

        return ret;
    }

    public static String GetString(String url, String start_tag, String split_tag_1, int split_id_1, String split_tag_2, int split_id_2, int ID) throws IOException {
        if (debugM) System.out.println(url);
        String id = UUID.randomUUID().toString();
        new File(start_path+"/"+id).delete();
        Download_file.Dw_File(url, start_path+"/"+id);
        boolean start = false;
        int idd = 0;
        List<String> a = ReadCFGFile(start_path+"/"+id);
        new File(start_path+"/"+id).delete();
        for (String s : a) {
            if (s.contains(start_tag)) start = true;//找到关键词才开始
            if (start) {
                String m = getStringBySplit(getStringBySplit(s, split_tag_1, split_id_1), split_tag_2, split_id_2);
                if (!Objects.equals(m, s)) {
                    if (idd == ID) return m;
                    idd++;
                }
            }
        }
        return "";
    }

    public static String GetText(String url, String start_tag, String end_tag) throws IOException {
        String id = UUID.randomUUID().toString();
        new File(start_path+"/"+id).delete();
        Download_file.Dw_File(url, start_path+"/"+id);
        boolean start = false;
        StringBuilder t = new StringBuilder();
        List<String> f = ReadCFGFile(start_path+"/"+id);
        new File(start_path+"/"+id).delete();
        for (String s : f) {
            if (s.contains(start_tag)) start = true;//找到关键词才开始
            if (start) {
                if (debugM) System.out.println(s);
                t.append(s);
                if (s.contains(end_tag)) {
                    return t.toString();
                }
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
    public static boolean ByJsonDownload(String json_file, String start_url) throws IOException {
        JsonObject jx = null;
        try {
            jx = (JsonObject) new JsonParser().parse(new FileReader(json_file));
        } catch (Exception e) {
            System.out.println("E: 解析json失败");
            return false;
        }
        //解析模式信息
        if (Objects.equals(jx.get("mode").getAsString(), "list")) {//列表模式
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
            System.out.println("标题:" + name);
            String img = GetString(
                    start_url,
                    jx.get("im").getAsJsonObject().get("start").getAsString(),
                    jx.get("im").getAsJsonObject().get("S1").getAsString(),
                    jx.get("im").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("im").getAsJsonObject().get("S2").getAsString(),
                    jx.get("im").getAsJsonObject().get("I2").getAsInt(),
                    jx.get("im").getAsJsonObject().get("id").getAsInt()
            );
            System.out.println("图片:" + img);
            Download_file.Dw_File(img, start_path+"/icon.jpg");
            String by = GetString(
                    start_url,
                    jx.get("by").getAsJsonObject().get("start").getAsString(),
                    jx.get("by").getAsJsonObject().get("S1").getAsString(),
                    jx.get("by").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("by").getAsJsonObject().get("S2").getAsString(),
                    jx.get("by").getAsJsonObject().get("I2").getAsInt(),
                    jx.get("by").getAsJsonObject().get("id").getAsInt()
            );
            System.out.println("作者:" + by);

            String ot = GetString(
                    start_url,
                    jx.get("ot").getAsJsonObject().get("start").getAsString(),
                    jx.get("ot").getAsJsonObject().get("S1").getAsString(),
                    jx.get("ot").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("ot").getAsJsonObject().get("S2").getAsString(),
                    jx.get("ot").getAsJsonObject().get("I2").getAsInt(),
                    jx.get("ot").getAsJsonObject().get("id").getAsInt()
            );
            System.out.println("简介:" + ot);

            //先获取url列表
            List<String> ul = GetList(
                    start_url+jx.get("list_add").getAsString(),
                    jx.get("list").getAsJsonObject().get("start").getAsString(),
                    jx.get("list").getAsJsonObject().get("S1").getAsString(),
                    jx.get("list").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("list").getAsJsonObject().get("S2").getAsString(),
                    jx.get("list").getAsJsonObject().get("I2").getAsInt(),
                    true
            );
            for (int x = 0; x < 50; x++) {
                for (int i = 0; i < ul.size(); i++) {
                    if(ul.get(i).contains("<")||ul.get(i).contains(">")||ul.get(i).contains("\"")){
                        ul.remove(i);
                        break;
                    }
                }
            }

            //新建一个buffer写入
            new File(start_path+"/dw_txt.txt").delete();
            new File(start_path+"/dw_txt.txt").createNewFile();
            FileWriter fileWriter_txt = new FileWriter(start_path+"/dw_txt.txt", true);
            BufferedWriter bufferWriter_txt = new BufferedWriter(fileWriter_txt);
            //创建包含全部内容的表
            List<String> textlist = new ArrayList<>();
            for (int i = 0; i < ul.size(); i++) {
                textlist.add("UNDOWNLOAD");
            }
            List<Tddw> tds = new ArrayList<>();
            int MaxTds = 20;

            //根据URL列表访问并下载
            try {
                for (int i = 0; i < ul.size(); i++) {
                    if (close_task) {
                        close_task = false;
                        return false;
                    }
                    ThingIO.nam = name;
                    ThingIO.nw = i;
                    ThingIO.al = ul.size();
                    if (atd == false) {
                        ThingIO.tdinf = "多线程下载未启用.";
                        System.out.println(ThingIO.out_text);
                        String title = GetString(
                                jx.get("url_add").getAsString() + ul.get(i),
                                jx.get("title").getAsJsonObject().get("start").getAsString(),
                                jx.get("title").getAsJsonObject().get("S1").getAsString(),
                                jx.get("title").getAsJsonObject().get("I1").getAsInt(),
                                jx.get("title").getAsJsonObject().get("S2").getAsString(),
                                jx.get("title").getAsJsonObject().get("I2").getAsInt(),
                                jx.get("title").getAsJsonObject().get("id").getAsInt()
                        );
                        String text = GetText(
                                jx.get("url_add").getAsString() + "/" + ul.get(i),
                                jx.get("text").getAsJsonObject().get("start").getAsString(),
                                jx.get("text").getAsJsonObject().get("end").getAsString()
                        );
                        ThingIO.out_text = "正在下载:" + name + "\r\n作者:" + by + "\r\n简介:" + AutoBR(ot, 34) + "\r\n进度: " + i + "/" + ul.size() + " (" + title + ")\r\n" + TextProgressBar(ul.size(), i);
                        JsonArray tt = jx.get("replace").getAsJsonArray();
                        //Main.log(ThingIO.out_text);
                        //处理文本
                        for (int j = 0; j < tt.size(); j++) {
                            text = text.replace(tt.get(j).getAsJsonObject().get("from").getAsString(), tt.get(j).getAsJsonObject().get("to").getAsString());
                        }
                        bufferWriter_txt.write(title + "\r\n" + text + "\r\n");
                    } else {
                        if (tds.size() < MaxTds) {
                            tds.add(new Tddw());
                            tds.get(tds.size() - 1).init(jx, jx.get("url_add").getAsString() + ul.get(i), i);
                            tds.get(tds.size() - 1).start();
                        } else {
                            i = i - 1;
                        }
                        ThingIO.tdinf = "";
                        //清理已经完成的任务
                        for (int j = 0; j < tds.size(); j++) {
                            if (tds.get(j).init_time() + 20000 < Instant.now().toEpochMilli()) {
                                int id = (int) tds.get(j).getId();
                                //textlist.set((int) tds.get(j).getId(),tds.get(j).getRetxt());
                                tds.remove(j);
                                tds.add(new Tddw());
                                tds.get(tds.size() - 1).init(jx, jx.get("url_add").getAsString() + ul.get(id), id);
                                tds.get(tds.size() - 1).start();
                                ThingIO.tdinf = ThingIO.tdinf + "#" + j + ": (正在重启任务)\r\n";
                                break;
                            }
                            if (!Objects.equals(tds.get(j).getRetxt(), "")) {
                                if (textlist.get((int) tds.get(j).getId()) != "UNDOWNLOAD") {
                                    tds.remove(j);
                                    break;
                                }
                                textlist.set((int) tds.get(j).getId(), tds.get(j).getRetxt());
                                tds.remove(j);
                                ThingIO.tdinf = ThingIO.tdinf + "#" + j + ": (已结束)\r\n";
                                break;
                            }
                            ThingIO.tdinf = ThingIO.tdinf + "#" + j + ": TaskURL = " + tds.get(j).getUrl() + "\r\n";
                        }
                        ThingIO.out_text = "正在下载:" + name + "\r\n作者:" + by + "\r\n简介:" + AutoBR(ot, 34) + "\r\n进度: " + i + "/" + ul.size() + " (UNKNOW)\r\n" + TextProgressBar(ul.size(), i);

                    }
                }
                if(atd){
                    while (!tds.isEmpty()) {
                        ThingIO.tdinf ="";
                        for (int j = 0; j < tds.size(); j++) {
                            if (tds.get(j).init_time() + 20000 < Instant.now().toEpochMilli()) {
                                int id = (int) tds.get(j).getId();
                                //textlist.set((int) tds.get(j).getId(),tds.get(j).getRetxt());
                                tds.remove(j);
                                tds.add(new Tddw());
                                tds.get(tds.size() - 1).init(jx, jx.get("url_add").getAsString() + ul.get(id), id);
                                tds.get(tds.size() - 1).start();
                                ThingIO.tdinf = ThingIO.tdinf + "#" + j + ": (正在重启任务)\r\n";
                                break;
                            }
                            if (!Objects.equals(tds.get(j).getRetxt(), "")) {
                                textlist.set((int) tds.get(j).getId(), tds.get(j).getRetxt());
                                tds.remove(j);
                                ThingIO.tdinf = ThingIO.tdinf + "#" + j + ": (已结束)\r\n";
                                break;
                            }
                            ThingIO.tdinf = ThingIO.tdinf + "#" + j + ": TaskURL = " + tds.get(j).getUrl() + "\r\n";
                        }
                        ThingIO.nam="*正在等待线程全部完毕*";
                        ThingIO.nw=0;
                        ThingIO.al=0;
                        ThingIO.out_text = "正在下载:" + name + "\r\n作者:" + by + "\r\n简介:" + AutoBR(ot, 34) + "\r\n进度: (正在等待全部线程完成) (UNKNOW)\r\n" + TextProgressBar(ul.size(), 0);
                    }
                    System.gc();
                    for (String s : textlist) {
                        //System.out.println(s);
                        bufferWriter_txt.write(s + "\r\n");
                    }
                }
                bufferWriter_txt.close();
                clear_tmps();
                //buffer写入一个配置文件
                new File(start_path+"/resource.ini").delete();
                new File(start_path+"/resource.ini").createNewFile();
                FileWriter fileWriter_info = new FileWriter(start_path+"/resource.ini", true);
                BufferedWriter bufferWriter_info = new BufferedWriter(fileWriter_info);
                bufferWriter_info.write("[conf]\r\n");
                bufferWriter_info.write("icon = icon.jpg");
                bufferWriter_info.write("title = " + name + "\r\n");
                bufferWriter_info.write("by = " + by + "\r\n");
                bufferWriter_info.write("ot = " + ot + "\r\n");
                bufferWriter_info.write("say = 本小说由IDlike提供的工具下载,工具可以从https://github.com/txlweb/web-novel-reptile-tool获取.\r\n");
                bufferWriter_info.close();
                //准备制作teip
                //获取md5
                String MD5 = File_use.getFileMD5(start_path+"/dw_txt.txt");
                new File(start_path+"/"+MD5).mkdir();
                File_use.CopyFileToThis(new File(start_path+"/dw_txt.txt"), new File(start_path+"/" + MD5 + "/main.txt"));
                File_use.CopyFileToThis(new File(start_path+"/resource.ini"), new File(start_path+"/" + MD5 + "/resource.ini"));
                //Download_file.Dw_File(img, start_path+"/icon.jpg");
                new File(start_path+"/" + MD5 + "/main.index").createNewFile();
                FileWriter fileWriter_index = new FileWriter(start_path+"/" + MD5 + "/main.index", true);
                fileWriter_index.write(preTxt(start_path+"/dw_txt.txt",""));
                fileWriter_index.close();
                File_use.CopyFileToThis(new File(start_path+"/icon.jpg"), new File(start_path+"/" + MD5 + "/icon.jpg"));
                try (ZipOutputStream zipOutputStream = new ZipOutputStream(Files.newOutputStream(Paths.get(start_path+"/out_" + MD5 + ".teip2")))) {
                    compressFolder(start_path+"/" + MD5, start_path+"/" + MD5, zipOutputStream);
                } catch (IOException e) {
                    Main.log("F: 打包为teip2时出现问题:"+e);
                    System.out.println("F: 打包为teip2时出现问题:"+e);
                }
            }finally {
                clear_tmps();
            }
//        if(Objects.equals(jx.get("mode").getAsString(), "tool")){
//            //{"mode":"tool","in":"[工具-清除空行]打开一个文件","id":"clnop"}
//            //{"mode":"tool","in":"[工具-批量替换]打开一个文件","id":"repall"}
//
//        }
            ThingIO.out_text = null;
        }

        return true;
    }

    public static void clear_tmps() {
        for (File file : Objects.requireNonNull(new File(start_path).listFiles())){
            if(file.isFile()){
                if(ced(file.getName(),'-',3)){
                    file.delete();
                }
            }
        }
    }
    public static String preTxt(String txt, String Rule) {
        if (Objects.equals(Rule, "")) Rule = ".*第.*章.*";
        List<String> List = ReadCFGFile(txt);
        StringBuilder T_LIST = new StringBuilder();
        for (int i = 0; i < List.size(); i++) {
            Pattern compile = Pattern.compile(Rule);//正则方法
            java.util.regex.Matcher matcher = compile.matcher(List.get(i));
            if (matcher.matches()) {
                //if(List.get(i).contains("第") & List.get(i).contains("章")){//关键字方法
                T_LIST.append(List.get(i)).append("&D&").append(String.valueOf(i).replace(",", "")).append("\r\n");
                //防逗号
                //System.out.println(List.get(i) + "&D&" + i);
            }
        }
        T_LIST.append("\r\n");
        System.out.println("共计: " + T_LIST.length() + "章");
        return T_LIST.toString();
    }
    public static boolean ced(String str,char key,int n) {
        int count = 0;
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == key) {
                count++;
                if (count >= n) {
                    return true;
                }
            }
        }
        return false;
    }
}

