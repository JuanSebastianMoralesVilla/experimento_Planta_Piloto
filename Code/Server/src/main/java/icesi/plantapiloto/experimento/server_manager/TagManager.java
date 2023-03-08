package icesi.plantapiloto.experimento.server_manager;
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
import icesi.plantapiloto.experimento.common.entities.Tag;
import icesi.plantapiloto.experimento.common.events.PublisherI;
import java.util.ArrayList;

public class TagManager {

    private final static String PATH = "../data";
	private final static String FILE_TEST = "XHGRID.csv";
    private boolean stop;
    private Server server;

    public TagManager(Server server) {
        this.server=server;
        System.out.println(server.getServerPort());
    }

    public synchronized void printcsv(Tag tag)throws IOException{
        if(tag==null){
            System.out.println("La escritura en el csv termino exitosamente.");
        }else{
            File file= new File(PATH+"/"+FILE_TEST);
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file, true); // abrir en modo de agregar
            BufferedWriter bw = new BufferedWriter(fw);
        
            // Verificar si la primera línea del archivo está vacía
            boolean isFirstLineEmpty = (new BufferedReader(new FileReader(file)).readLine() == null);
            //Solo escribe la cabecera del archivo la primera vez que se llama a la función
            if (isFirstLineEmpty) {
                bw.write("SERVER NAME;IP;PORT;VALUE;DATE\n");
            } 		
		    bw.write(server.getServerName()+ ";" +server.getServerIp()+ ";" +server.getServerPort()+";" +tag.getValue()+ ";"+tag.getTime()+"\n");                 	
		    bw.close();
        }
		
    }

}