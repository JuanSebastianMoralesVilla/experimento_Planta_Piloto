package icesi.plantapiloto.experimento.client_manager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import java.util.ArrayList;
import icesi.plantapiloto.experimento.common.entities.Message;
import icesi.plantapiloto.experimento.common.entities.Measure;
import icesi.plantapiloto.experimento.common.entities.Experiment;
import icesi.plantapiloto.experimento.common.entities.Tag;
import icesi.plantapiloto.experimento.common.PluginI;

public class ScheduleManager {

    private Properties properties;
    private Properties pluginList;
    private Queue<Experiment> experiments;
    private Queue<PluginI> pluginIs;
    private Scheduler scheduler;
    private MessageManager messageManager;
    private File propFile;
    private boolean running;


    public ScheduleManager() throws Exception {

        this.running = false;
        // Load properties from Schedeler Manager
        properties = new Properties();
        // propFile = new File(System.getProperty("user.dir") +
        // "/Code/Client/src/main/resources/schedule.properties");
        propFile = new File("resources/schedule.properties");
        InputStream stream = new FileInputStream(propFile);
        properties.load(stream);
        // Load List of Plugins
        pluginList = new Properties();
        // propFile = new File(System.getProperty("user.dir") +
        // "/Code/Client/src/main/resources/plugin_list.test.conf");
        propFile = new File("resources/plugin_list.test.conf");
        stream = new FileInputStream(propFile);
        pluginList.load(stream);

        // Load Experiment configuration
        // propFile = new File(System.getProperty("user.dir") +
        // "/Code/Client/src/main/resources/experiment.conf");
        propFile = new File("resources/experiment.conf");

        experiments = new LinkedList<>();
        pluginIs = new LinkedList<>();
        BufferedReader br = new BufferedReader(new FileReader(propFile));
        String line = br.readLine();
        boolean change = true;
        Experiment exp = null;
        while (line != null && !line.isEmpty()) {
            String[] aux = line.split("=");
            if (change) {
                exp = new Experiment();
            }

            switch (aux[0]) {
                case "START":
                    exp.setExperimentName(aux[1]);
                    change = false;
                    break;
                case "END":
                    change = true;
                    experiments.add(exp);
                    break;
                case "SERVER_AMMOUNT":
                    exp.setServerAmount(Integer.parseInt(aux[1]));
                    break;
                case "LAPSE":
                    exp.setLapse(Long.parseLong(aux[1]));
                    break;
                case "TAG_AMMOUNT":
                    exp.setTagAmount(Integer.parseInt(aux[1]));
                    break;
                case "DURATION_SECOND":
                    exp.setDurationSecond(Long.parseLong(aux[1]));
                    break;
                case "REPEATS":
                    exp.setRepeats(Integer.parseInt(aux[1]));
                    break;

                default:
                    System.out.println("Something was wrong in the eperiment file read");
            }
            line = br.readLine();
        }
        br.close();
        
        messageManager=new MessageManager();
        messageManager.start();
        scheduler = new Scheduler(messageManager,this);

    }
    public static void main(String[] args) throws Exception {
        ScheduleManager manager = new ScheduleManager();
        BufferedReader rd = new BufferedReader(new InputStreamReader(System.in));
        String line = "";
        do{
            if(!manager.isRunning()){
                showMenu();
            }
            line = rd.readLine();
            if(!manager.isRunning()){
                executeMenu(manager, line);
            }
        }while (manager.isRunning() );
        rd.close();
        System.out.println("SALIO xd");
    }

    private static void showMenu() {
        System.out.println("-------------Experiment-------------");
        System.out.println("Option menu:\n"
                + "0. Exit\n"
                + "1. Run Experiments\n");
    }

    private static void executeMenu(ScheduleManager manager, String line) {
        try {
            int input = Integer.parseInt(line);
            switch (input) {
                case 0:
                    manager.stop();
                    break;
                case 1:
                    manager.start();
            }
        } catch (NumberFormatException e) {
            System.out.println("Bad input, please try again, remember just type the number");
        } catch (Exception e) {
            e.printStackTrace();
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

    public void loadPlugin(String key, String path, String ip, int port,  int tag_ammount, boolean save) throws Exception {
        // System.out.println("Loading " + key);
        path = path.trim();

        key = key.trim();
        PluginI pluginI = (PluginI) Class.forName(path).getConstructor(String.class,String.class,int.class, int.class).newInstance(key, ip, port, tag_ammount);
        scheduler.addPlugin(pluginI);
        pluginI.connect();
        pluginIs.add(pluginI);
        if (save) {
            BufferedWriter wr = new BufferedWriter(new FileWriter(propFile, true));
            wr.append(key + " = " + path);
            wr.newLine();
            wr.flush();
            wr.close();
        }
    }

    private void loadPlugins(Experiment exp) throws Exception {
        Iterator<?> keys = properties.keySet().iterator();

        // System.out.println("Loading plugins");
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
        
        for (int i = minPort; i <= maxPort; i++) {
            keys = pluginList.keySet().iterator();
            while (keys.hasNext()) {
                String key = (String) keys.next();
                // System.out.println("Loading plugin: " + key);
                loadPlugin(key, path, pluginList.getProperty(key), i, exp.getTagAmount(), false);
            }
        }
    }

    public void runNextExperiment() throws Exception {
        if(experiments.isEmpty()){
            System.out.println("Finished");
            this.running = false;
            turnOffPlugins();
            return;
        }
        
        Experiment exp = experiments.peek();
        // System.out.println("Runnign "+exp.experiment_name);
        System.out.println(
                "\nNAME: "+exp.getExperimentName() + 
                "\nDURATION: "+exp.getDurationSecond()+ 
                "\nREPEATS: "+exp.getRepeats() +
                "\nTAGS AMMOUNT: "+exp.getTagAmount()+
                "\nLAPSE: "+exp.getLapse()+
                "\nSERVER AMMOUNT: "+exp.getServerAmount());
                
        desconectPluings();
        loadPlugins(exp);
        
        if(exp.getRepeats() == 1){
            experiments.remove();
        }

       
        scheduler.runTasks(exp.getLapse(),exp.getDurationSecond(),exp.getServerAmount(),exp.getExperimentName(),exp);
        exp.setRepeats(exp.getRepeats()-1);
    }

    public void desconectPluings() throws IOException{
        while(!pluginIs.isEmpty()){
            pluginIs.poll().disconnet();
        }
    }

    public void turnOffPlugins() throws IOException{
        while(!pluginIs.isEmpty()){
            pluginIs.poll().turnOff();
        }
    }

    public boolean isRunning() {
        return this.running;
    }

    public void start() throws Exception{
        this.running = true;
        runNextExperiment();
    }

    public void stop(){
        this.running = false;
        this.messageManager.stopTask(true);
    }
}