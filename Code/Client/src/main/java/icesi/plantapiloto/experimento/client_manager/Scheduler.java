package icesi.plantapiloto.experimento.client_manager;

import icesi.plantapiloto.experimento.common.PluginI;
import icesi.plantapiloto.experimento.common.entities.Experiment;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {
    // private PublisherI publisher;
    private MessageManager messageManager;
    private ScheduleManager manager;
    private List<PluginI> plugins;

    public Scheduler(MessageManager messageManager, ScheduleManager manager) {
        this.messageManager = messageManager;
        this.plugins = new ArrayList<>();
        this.manager = manager;
        // this.publusherManager = new PublisherManager(this.publisher);
        // this.publusherManager.start();
    }

    public void addPlugin(PluginI pugI) {
        plugins.add(pugI);

    }

    public void runTasks(long lapse, long duration,int server_ammount,String testId, Experiment exp){
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(plugins.size()*5);
        // Timer timer = new Timer();
        for(int i = 0;i<plugins.size()&&i<server_ammount;i++){
            Task task = new Task(plugins.get(i), messageManager,exp);
            executorService.scheduleWithFixedDelay(task,0,lapse,TimeUnit.MILLISECONDS);
            // timer.schedule(task, 0,lapse);
        }
        
        TimerTask cancelTask = new TimerTask() {
            @Override
            public void run(){
                executorService.shutdown();
                // timer.cancel();
                try {
                    plugins = new LinkedList<>();
                    manager.runNextExperiment();
                } catch (Exception e) {
                    System.out.println("Somthing was wrong");
                    e.printStackTrace();
                }
            }
        };

        Timer cancelTimer = new Timer();
        cancelTimer.schedule(cancelTask, duration*1000);
    }

    public void clearPluings(){
        plugins = new ArrayList<>();
    }
}
