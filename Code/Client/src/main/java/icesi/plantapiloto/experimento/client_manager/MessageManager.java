package icesi.plantapiloto.experimento.client_manager;
import java.util.ArrayDeque;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import icesi.plantapiloto.experimento.common.entities.Message;
import icesi.plantapiloto.experimento.common.entities.Measure;
import icesi.plantapiloto.experimento.common.entities.Experiment;
import icesi.plantapiloto.experimento.common.entities.Tag;
import icesi.plantapiloto.experimento.common.events.PublisherI;
import java.util.ArrayList;

public class MessageManager{
    private final static String PATH = "../data";
    private final static String FILE_TEST = "XHGRIDClient.csv";
    private boolean stop;


    public MessageManager() {
    }

    public synchronized void addTags(Message message) throws IOException{
        if(message==null){
            System.out.println("La escritura en el csv termino exitosamente.");
        }else{
            ArrayList<Tag> tags=new ArrayList<Tag>();
            String nameServer="";
            for (Measure measure : message.getMeasures()) {
                Tag tag=new Tag();
                tag.setName(measure.getName());
                tag.setValue(Integer.parseInt(measure.getValue()));
                tag.setTime(measure.getRequestTime());
                tag.setDataSource(message.getSourceData());
                tags.add(tag);
            }
            printcsv(tags,message);
        }
        
    }

    public void printcsv (ArrayList<Tag> tags,Message message) throws IOException{
        File file = new File(PATH + "/" + FILE_TEST);
        if(!file.exists()){
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file, true); // abrir en modo de agregar
        BufferedWriter bw = new BufferedWriter(fw);
        
        // Verificar si la primera línea del archivo está vacía
        boolean isFirstLineEmpty = (new BufferedReader(new FileReader(file)).readLine() == null);
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
    }
}