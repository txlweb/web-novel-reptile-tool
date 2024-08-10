package com.teipreader.reptile.lib;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.sql.Time;
import java.time.Instant;

import static com.teipreader.reptile.lib.get_thing.GetString;
import static com.teipreader.reptile.lib.get_thing.GetText;

public class Tddw extends Thread{
    private String retxt = "";
    private long init_time = 0;

    public String getUrl() {
        return url;
    }

    private String url = "";

    private int id = -1;
    private JsonObject jx = null;
    @Override
    public void run() {
        String text = null;
        String title = null;
        try {
            title = GetString(
                    url,
                    jx.get("title").getAsJsonObject().get("start").getAsString(),
                    jx.get("title").getAsJsonObject().get("S1").getAsString(),
                    jx.get("title").getAsJsonObject().get("I1").getAsInt(),
                    jx.get("title").getAsJsonObject().get("S2").getAsString(),
                    jx.get("title").getAsJsonObject().get("I2").getAsInt(),
                    jx.get("title").getAsJsonObject().get("id").getAsInt()
            );
            text = GetText(
                    url,
                    jx.get("text").getAsJsonObject().get("start").getAsString(),
                    jx.get("text").getAsJsonObject().get("end").getAsString()
            );
        } catch (IOException e) {
            init_time =  Instant.now().toEpochMilli()+200000;
            throw new RuntimeException(e);
        }
        JsonArray tt = jx.get("replace").getAsJsonArray();
        //Main.log(ThingIO.out_text);
        //处理文本
        for (int j = 0; j < tt.size(); j++) {
            text = text.replace(tt.get(j).getAsJsonObject().get("from").getAsString(), tt.get(j).getAsJsonObject().get("to").getAsString());
        }
        this.retxt = title + "\r\n" + text + "\r\n";
        super.run();
    }
    public void init(JsonObject jxd,String urld,int iid){
        this.init_time = Instant.now().toEpochMilli();
        this.jx=jxd;
        this.url=urld;
        this.id=iid;
    }

    public String getRetxt() {
        return retxt;
    }

    @Override
    public long getId() {
        return id;
    }

    public long init_time() {
        return init_time;
    }
}
