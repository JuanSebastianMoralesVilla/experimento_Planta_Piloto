package icesi.plantapiloto.experimento.client_manager;

import java.util.TimerTask;

import icesi.plantapiloto.experimento.client_manager.ScheduleManager;
import icesi.plantapiloto.experimento.common.PluginI;
import icesi.plantapiloto.experimento.common.entities.Message;

public class Task extends TimerTask {
    private PluginI plugin;
    private PublisherManager publisher;

    public Task(PluginI p, PublisherManager pi) {
        plugin = p;
        publisher = pi;
    }

    @Override
    public void run() {
        long time = System.currentTimeMillis();
        Message  messages = plugin.getMessage();
 
        // publisher.addMessage(messages);
        System.out.println("tiempo de la tarea: " + (System.currentTimeMillis() - time) + " ms");
    }

}
