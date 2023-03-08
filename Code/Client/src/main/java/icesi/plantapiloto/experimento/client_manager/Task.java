package icesi.plantapiloto.experimento.client_manager;

import java.util.TimerTask;
import java.io.IOException;
import icesi.plantapiloto.experimento.common.PluginI;
import icesi.plantapiloto.experimento.common.entities.Message;
import icesi.plantapiloto.experimento.common.entities.Experiment;

public class Task extends TimerTask {
    private PluginI plugin;
    private MessageManager messageManager;
    private Experiment experiment;

    public Task(PluginI p, MessageManager mm,Experiment exp) {
        plugin = p;
        messageManager = mm;
        experiment=exp;
    }
    @Override
    public void run() {
        long time = System.currentTimeMillis();
        //Se almacenan las respuestas
        Message  messages = plugin.getMessage();
        messages.setExperiment(experiment);
        Runnable addMessageRunnable = new Runnable() {
            public void run() {
                try {
                    messageManager.addTags(messages);
                } catch (IOException e) {
                     e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(addMessageRunnable);
        thread.start();
        // publisher.addMessage(messages);
        // System.out.println("tiempo de la tarea: " + (System.currentTimeMillis() - time) + " ms");
    }

}
