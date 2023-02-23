package icesi.plantapiloto.experimento.plugin;

import icesi.plantapiloto.experimento.common.PluginI;
import icesi.plantapiloto.experimento.common.entities.Measure;
import icesi.plantapiloto.experimento.common.entities.Message;

import java.util.HashMap;
import java.util.List;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import java.net.Socket;

public class ServerPlugin implements PluginI {
   
    private String ip;
    private int port;
    private String name;
    private List<String> tags;
    private HashMap<String, String> props;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader; 

    public ServerPlugin() {
        try {
            tags = new ArrayList<>();
            props = new HashMap<>();
            loadTags();

            socket = new Socket(ip,port);
            OutputStream output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadTags() {
        try {
            File propFile = new File(System.getProperty("user.dir") + "/Code/serverPlugin/src/main/resources/plugin.conf");

            InputStream stream = new FileInputStream(propFile);
            BufferedReader red = new BufferedReader(new InputStreamReader(stream));
            String line = red.readLine();
            while (line != null && !line.equals("")) {
                String prop[] = line.trim().split("=");
                if (prop[0].contains("SERVER_IP")) {
                    ip = prop[1].trim();
                } else if (prop[0].contains("SERVER_PORT")){
                    port = Integer.parseInt(prop[1].trim());
                }else {
                    props.put(prop[0].trim(), prop[1].trim());
                }
                line = red.readLine();
            }
            red.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Message getMessage() {
        Message msg = new Message();
        try {
            msg.setSourceData(name + " " + ip)
                    .setTime(Calendar.getInstance().getTime());
                    // .setTopic(props.get("topic"));
            int size = Integer.parseInt(props.get("TAG_AMMOUNT"));
            for (int i = 0; i<size ;i++) {
                String tag =  "TAG_#"+i;
                System.out.println("Solicitando Tag: "+tag);
                tag = String.format("%1$" + 100 + "s", tag); 
                writer.println(tag);
            }   
            for (int i = 0; i<size ;i++){
                String tag =  "TAG_#"+i;
                tags.add(tag);
                String value = reader.readLine();
                System.out.println("Respuesta: "+value);
                Measure measure = new Measure();
                measure.setName(tag);
                measure.setValue(value);
                msg.addMeasure(measure);

                
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return msg;
    }

    public HashMap<String, String> getSettings() {
        return props;
    }
}
