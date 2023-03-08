package icesi.plantapiloto.experimento.client_manager;
import java.util.ArrayDeque;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import icesi.plantapiloto.experimento.common.entities.Message;
import icesi.plantapiloto.experimento.common.entities.Measure;
import icesi.plantapiloto.experimento.common.entities.Tag;
import java.util.ArrayList;

public class MessageManager extends Thread {
    private final static String PATH = "../data";
    private final static String FILE_TEST = "XHGRIDClient.csv";
    private ArrayDeque<Message> messages;
    private boolean stop;


    public MessageManager() {
        messages = new ArrayDeque<>();
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
                            addTags(message);
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

    public void addTags(Message message) throws IOException{
        ArrayList<Tag> tags=new ArrayList<Tag>();
        String nameServer="";
        for (Measure measure : message.getMeasures()) {
            Tag tag=new Tag();
            tag.setName(measure.getName());
            tag.setValue(Integer.parseInt(measure.getValue()));
            tag.setTime(measure.getRequestTime());
            nameServer = message.getSourceData().split(" ")[0];
            tag.setDataSource(nameServer);
            tags.add(tag);
        }
        printcsv(tags,message);
    }

    public void printcsv (ArrayList<Tag> tags,Message message) throws IOException{
        File file = new File(PATH + "/" + FILE_TEST);
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file, true); // abrir en modo de agregar
        BufferedWriter bw = new BufferedWriter(fw);
        BufferedReader br = new BufferedReader(new FileReader(file));
        // Verificar si la primera línea del archivo está vacía
        boolean isFirstLineEmpty = (br.readLine() == null);
         //Solo escribe la cabecera del archivo la primera vez que se llama a la función
        if (isFirstLineEmpty) {
            bw.write("EXPERIMENT NUMBER;SERVER;TAG;VALUE;DATE;DURATION;REPEATS;TAGS AMMOUNT;LAPSE;SERVER AMMOUNT\n");
        }       
        for(int i= 0; i<tags.size();i++){
            Tag currentTag = tags.get(i);
            int repetition=message.getExperiment().getRepeats()+1;
            bw.write(message.getExperiment().getExperimentName()+ ";" +currentTag.getDataSource()+ ";" +currentTag.getName()+ ";" +currentTag.getValue()+ ";"+currentTag.getTime()+";"+
            message.getExperiment().getDurationSecond()+";"+repetition+";"+message.getExperiment().getTagAmount()+";"+
            message.getExperiment().getLapse()+";"+message.getExperiment().getServerAmount()+"\n");                 	
        }
        bw.close();
        br.close();
    }
}