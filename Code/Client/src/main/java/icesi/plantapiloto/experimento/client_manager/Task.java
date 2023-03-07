package icesi.plantapiloto.experimento.client_manager;

import java.util.TimerTask;

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
                // Llamar al método addMessage() del MessageManager
                messageManager.addMessage(messages);
            }
        };
        Thread thread = new Thread(addMessageRunnable);
        thread.start();
        // publisher.addMessage(messages);
        // System.out.println("tiempo de la tarea: " + (System.currentTimeMillis() - time) + " ms");
    }

}
