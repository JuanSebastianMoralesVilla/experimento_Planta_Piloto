package icesi.plantapiloto.experimento.plugin;

import icesi.plantapiloto.experimento.common.PluginI;
import icesi.plantapiloto.experimento.common.entities.Measure;
import icesi.plantapiloto.experimento.common.entities.Message;

import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.File;
import java.io.OutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import java.net.Socket;

public class ServerPlugin implements PluginI {

    private String ip;
    private int port;
    private String name;
    private int tag_ammount;
    private List<String> tags;
    private Properties props;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    private boolean running;

    public ServerPlugin(String name,String ip, int port, int tag_ammount) throws Exception {
        tags = new ArrayList<>();
        props = new Properties();

        this.name = name;
        this.ip = ip;
        this.port = port;
    }

    @Override
    public Message getMessage() {
        Message msg = new Message();
        Timer timer = new Timer();
        try {
            this.running = true;
            msg.setSourceData(name + " " + ip + ":" + port).setTime(Calendar.getInstance().getTime());

            // int size = Integer.parseInt(props.getProperty("TAG_AMMOUNT"));
            int size = 5;

            for (int i = 0; i < size; i++) {
                String tag = "TAG_#" + i;
                tag = String.format("%1$" + 100 + "s", tag);
                writer.println(tag);
            }
            
            long timeToStop = size<=50 ? 5000 : 5000 + 100*(size-50);
            // Si el proceso no recibe respuesta despues de 5 segundos, termina la tarea / agrega 100ms por cada cantidad adicional superior a 50
            timer.schedule(new Stopper(this), timeToStop);

            for (int i = 0; i < size && running; i++) {
                String tag = "TAG_#" + i;
                tags.add(tag);
                String value = reader.readLine();
                Measure measure = new Measure();
                measure.setName(tag);
                measure.setValue(value);
                msg.addMeasure(measure);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        timer.cancel();
        this.running = false;
        return msg;
    }
    private class Stopper extends TimerTask{
        ServerPlugin server;
        Stopper(ServerPlugin ser){
            server = ser;
        }

        @Override
        public void run() {
            server.stop();
        }
    }

    private void stop(){
        this.running = false;
    }

    @Override
    public void connect() throws IOException{
        // System.out.println("Conectando con "+ip+":"+port);
        socket = new Socket(ip, port);
        OutputStream output = socket.getOutputStream();
        writer = new PrintWriter(output, true);
        InputStream input = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input));
        // System.out.println(ip + ":" + port+" connected");
    }

    @Override
    public void disconnet() throws IOException {
       socket.close();
    }

    // @Override
    // public Properties getSettings() {
    //     return props;
    // }

    // @Override
    // public void setSettings(Properties props) {
    //     this.props = props;
    // }

    // @Override
    // public void addSettings(Properties props) {
    //     Iterator<?> keys = props.keySet().iterator();

    //     while (keys.hasNext()) {
    //         String key = (String) keys.next();
    //         this.props.setProperty(key, props.getProperty(key));
    //     }
    // }
}
