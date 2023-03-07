package icesi.plantapiloto.experimento.plugin;

import icesi.plantapiloto.experimento.common.PluginI;
import icesi.plantapiloto.experimento.common.encoders.JsonEncoder;
import icesi.plantapiloto.experimento.common.entities.Measure;
import icesi.plantapiloto.experimento.common.entities.Message;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.net.SocketTimeoutException;

import java.net.Socket;

public class ServerPlugin implements PluginI {

    private String ip;
    private int port;
    private String name;
    private int tag_ammount;
    private List<String> tags;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;
    private JsonEncoder encoder;

    private boolean running;

    public ServerPlugin(String name,String ip, int port, int tag_ammount) throws Exception {
        tags = new ArrayList<>();
        encoder = new JsonEncoder();
        this.name = name;
        this.ip = ip;
        this.port = port;
        this.tag_ammount = tag_ammount;
    }

    @Override
    public Message getMessage() {
        Message msg = new Message();
        Timer timer = new Timer();
        try {
            this.running = true;
            msg.setSourceData(name + " " + ip + ":" + port).setTime(Calendar.getInstance().getTime());
            
            for (int i = 0; i < tag_ammount; i++) {
                String tag = "TAG_#" + i;
                tag = String.format("%1$" + 100 + "s", tag);
                writer.println(tag);
            }
            
            long timeToStop = tag_ammount<=50 ? 5000 : 5000 + 100*(tag_ammount-50);
            // Si el proceso no recibe respuesta despues de 5 segundos, termina la tarea / agrega 100ms por cada cantidad adicional superior a 50
            timer.schedule(new Stopper(this), timeToStop);

            for (int i = 0; i < tag_ammount && running; i++) {
                String tag = "TAG_#" + i;
                tags.add(tag);
                String value = reader.readLine();

                Measure measure = encoder.decode(value, Measure.class);
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
        System.out.println("Desconectando server: "+port);
        writer.println("close");
        reader.close();
        writer.close();
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
