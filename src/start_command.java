import com.teipreader.reptile.lib.ThingIO;

import static com.teipreader.reptile.lib.get_thing.ByJsonDownload;
public class start_command extends Thread implements start_command_ {
    public String jf = "";
    public String su="";
    @Override
    public void run() {
        try{
            if(!ByJsonDownload(jf,su)){
                Main.Is_fail = true;
            }
        } catch (Exception e) {
            System.out.println("E: 无法下载!");
        }finally {
            ThingIO.out_text = null;
        }
    }
    @Override
    public void task(String json_file, String start_url) {
        jf=json_file;
        su=start_url;
    }
}
