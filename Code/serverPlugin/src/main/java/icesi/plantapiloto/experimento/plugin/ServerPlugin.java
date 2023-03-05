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
    private Properties props;

    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    private boolean running;

    public ServerPlugin(String ip, int port) throws Exception {

        tags = new ArrayList<>();
        props = new Properties();

        this.ip = ip;
        this.port = port;

        // File propFile = new File(System.getProperty("user.dir") + "/Code/serverPlugin/src/main/resources/plugin.conf");
        File propFile = new File("resources/plugin.conf");
        if (!propFile.exists()) {
            propFile.createNewFile();
        }

        InputStream stream = new FileInputStream(propFile);
        props.load(stream);

        System.out.println("Conectando con "+ip+":"+port);

        socket = new Socket(ip, port);
        OutputStream output = socket.getOutputStream();
        writer = new PrintWriter(output, true);
        InputStream input = socket.getInputStream();
        reader = new BufferedReader(new InputStreamReader(input));
        System.out.println(ip + ":" + port+" connected");
    }

    @Override
    public Message getMessage() {
        Message msg = new Message();
        Timer timer = new Timer();
        try {
            this.running = true;
            msg.setSourceData(name + " " + ip + ":" + port).setTime(Calendar.getInstance().getTime());

            int size = Integer.parseInt(props.getProperty("TAG_AMMOUNT"));


            for (int i = 0; i < size; i++) {
                String tag = "TAG_#" + i;
                System.out.println("Solicitando Tag: " + tag);
                tag = String.format("%1$" + 100 + "s", tag);
                writer.println(tag);
            }
            
            long timeToStop = size<=50 ? 5000 : 5000 + 100*(size-50);
            // Si el proceso no recibe respuesta despues de 5 segundos, termina la tarea
            timer.schedule(new Stopper(this), timeToStop);

            for (int i = 0; i < size && running; i++) {
                String tag = "TAG_#" + i;
                tags.add(tag);
                String value = reader.readLine();
                System.out.println("Respuesta: " + value);
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
    public Properties getSettings() {
        return props;
    }

    @Override
    public void setSettings(Properties props) {
        this.props = props;
    }

    @Override
    public void addSettings(Properties props) {
        Iterator<?> keys = props.keySet().iterator();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            this.props.setProperty(key, props.getProperty(key));
        }
    }
}
