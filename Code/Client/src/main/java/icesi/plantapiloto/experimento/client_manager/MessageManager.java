package icesi.plantapiloto.experimento.client_manager;
import java.util.ArrayDeque;
import icesi.plantapiloto.experimento.common.entities.Message;
import icesi.plantapiloto.experimento.common.events.PublisherI;

public class MessageManager extends Thread {
    private ArrayDeque<Message> messages;
    private boolean stop;
    private ScheduleManager scheduleManager;

    public MessageManager(ScheduleManager sm) {
        messages = new ArrayDeque<>();
        scheduleManager= sm;
    }
    
    public void addMessage(Message message) {
        synchronized (messages) {
            messages.add(message);
        }
    }

    public void run() {
        while (!stop) {
            try {
                synchronized (messages) {
                    if (!messages.isEmpty()) {
                         while (!messages.isEmpty()) {
                            // Guarda el mensaje en algún medio de almacenamiento, como una base de datos o un archivo.
                            Message message = messages.poll();
                            scheduleManager.addTags(message);
                         }
                    } else {
                        Thread.yield();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("ERROR AL GUARDAR");
            }
        }
    }

     public void stopTask(boolean c) {
        stop = c;
    }

}