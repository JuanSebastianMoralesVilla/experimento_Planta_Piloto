package icesi.plantapiloto.experimento.client_manager;

import icesi.plantapiloto.experimento.common.PluginI;
import icesi.plantapiloto.experimento.common.events.PublisherI;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Scheduler {
    // private PublisherI publisher;
    private PublisherManager publusherManager;
    private List<TimerTask> plugins;
    private Timer timer;

    public Scheduler(PublisherI publisher) {
        // this.publisher = publisher;
        this.plugins = new ArrayList<>();
        this.timer = new Timer();
        // this.publusherManager = new PublisherManager(this.publisher);
        // this.publusherManager.start();
    }

    public void addPlugin(PluginI pugI) {
        Task task = new Task(pugI, publusherManager);
        plugins.add(task);
    }

    public void runTasks(long lapse, long duration){
        for(TimerTask task : plugins){
            timer.schedule(task, 0, lapse);
        }
        timer.schedule(new TimerTask() {
            @Override
            public void run(){
                timer.cancel();
            }
        }, duration);
    }
}
