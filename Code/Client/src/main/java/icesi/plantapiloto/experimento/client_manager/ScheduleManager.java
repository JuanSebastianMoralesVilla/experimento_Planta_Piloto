package icesi.plantapiloto.experimento.client_manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import icesi.plantapiloto.experimento.common.PluginI;
import icesi.plantapiloto.experimento.common.entities.Tag;

// import icesi.plantapiloto.experimento.common.encoders.ObjectEncoder;
// import icesi.plantapiloto.experimento.common.events.PublisherI;

public class ScheduleManager {
    // private PublisherI publisherI;

    private final static String PATH = "data";
	private final static String FILE_TEST = "XHGRIDClient.csv";
    private Properties properties;
    private Properties pluginList;
    private Properties experiments;
    private Scheduler scheduler;
    private File propFile;

    private List<Tag> tags;

    public ScheduleManager() throws Exception {

        System.out.println("1");
        // Load properties from Schedeler Manager
        properties = new Properties();
        // propFile = new File(System.getProperty("user.dir") + "/Code/Client/src/main/resources/schedule.properties");
        propFile = new File("resources/schedule.properties");
        InputStream stream = new FileInputStream(propFile);
        properties.load(stream);

        // Load List of Plugins
        pluginList = new Properties();
        // propFile = new File(System.getProperty("user.dir") + "/Code/Client/src/main/resources/plugin_list.test.conf");
        propFile = new File("resources/plugin_list.test.conf");
        stream = new FileInputStream(propFile);
        pluginList.load(stream);

        // Load Experiment configuration
        experiments = new Properties();
        // propFile = new File(System.getProperty("user.dir") + "/Code/Client/src/main/resources/experiment.conf");
        propFile = new File("resources/experiment.conf");
        stream = new FileInputStream(propFile);
        experiments.load(stream);

        // String pubClass = properties.getProperty("publisher.class").trim();
        // String pubIp = properties.getProperty("publisher.ip").trim();
        // String pubEncoder = properties.getProperty("publisher.encoder").trim();
        // String pubName = properties.getProperty("publisher.name").trim();

        // if (pubClass == null || pubIp == null || pubEncoder == null || pubName == null) {
        //     System.out.println("No publisher config");
        //     return;
        // }
        // ObjectEncoder encoder = (ObjectEncoder) Class.forName(pubEncoder).getDeclaredConstructor()
        //         .newInstance();
        // publisherI = (PublisherI) Class.forName(pubClass).getDeclaredConstructor().newInstance();

        // publisherI.setEncoder(encoder);
        // publisherI.setHost(pubIp);
        // publisherI.setName(pubName);
        System.out.println("2");
        // scheduler = new Scheduler(publisherI);
        scheduler = new Scheduler(null);
        
        // Iterator<?> keys = properties.keySet().iterator();

        // while (keys.hasNext()) {
        //     String key = (String) keys.next();
        //     if (key.endsWith(".plugin")) {
        //         loadPlugin(key, properties.getProperty(key), false);
        //     }
        // }

        loadPlugins();
    }

    public static void main(String[] args) throws Exception {
        ScheduleManager manager = new ScheduleManager();
        BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
        String line = rd.readLine();
        while (!line.equals("exit")) {
            if (line.startsWith("addPlugin")) {
                String[] plu = line.split("=");
                manager.loadPlugin(plu[0], plu[1], true);
            }
        }
    }

    public void loadPlugin(String key, String path, boolean save) throws Exception {
        
        path = path.trim();

        key = key.trim();
        PluginI pluginI = (PluginI) Class.forName(path).getDeclaredConstructor().newInstance();
        scheduler.addPlugin(pluginI);
        if (save) {
            BufferedWriter wr = new BufferedWriter(new FileWriter(propFile, true));
            wr.append(key + " = " + path);
            wr.newLine();
            wr.flush();
            wr.close();
        }
    }

    public void loadPlugin(String key, String path, String ip, int port, boolean save) throws Exception {
        System.out.println("Loading "+key);
        path = path.trim();

        key = key.trim();
        PluginI pluginI = (PluginI) Class.forName(path).getConstructor(String.class,int.class).newInstance(ip,port);
        scheduler.addPlugin(pluginI);
        if (save) {
            BufferedWriter wr = new BufferedWriter(new FileWriter(propFile, true));
            wr.append(key + " = " + path);
            wr.newLine();
            wr.flush();
            wr.close();
        }
    }

    private void loadPlugins() throws Exception{
        Iterator<?> keys = properties.keySet().iterator();

        System.out.println("Loading plugins");
        String path = "";
        int minPort = 0;
        int maxPort = 0;

        while (keys.hasNext()) {
            
            String key = (String) keys.next();
            
            if (key.endsWith(".plugin")) {
                path = properties.getProperty(key);
            }

            if (key.endsWith(".ports")) {
                String[] ports = properties.getProperty(key).split("-");
                minPort = Integer.parseInt(ports[0]);
                maxPort = Integer.parseInt(ports[1]);
            }
        }
        keys = pluginList.keySet().iterator();
        while (keys.hasNext()) {
            
            String key = (String) keys.next();
            System.out.println("Loading plugin: "+key);
            for(int i = minPort;i<=maxPort;i++){
                loadPlugin(key, path,pluginList.getProperty(key),i, false);
            }
            
        }
    }
        
        
    public void printcsvClient () throws IOException{

        File file= new File(PATH+"/"+FILE_TEST);
        FileWriter fw= new FileWriter(file);
        BufferedWriter bw= new BufferedWriter(fw);
        
        bw.write("TAG;VALUE;DATE\n");

        for(int i= 0; i<tags.size();i++){
            Tag currentTag = tags.get(i);
            bw.write( currentTag.getName()+ ";" +currentTag.getValue()+ ";"+currentTag.getTime()+"\n");                 	
        }
        bw.close();
    }
        
    public void addTag(Tag tag){
        tags.add(tag);
    }
}
