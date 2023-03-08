package icesi.plantapiloto.experimento.server_manager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import icesi.plantapiloto.experimento.common.entities.Tag;

public class TagManager {

    private final static String PATH = "../data";
	private final static String FILE_TEST = "XHGRID.csv";

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
            
            BufferedReader br = new BufferedReader(new FileReader(file));
            // Verificar si la primera línea del archivo está vacía
            boolean isFirstLineEmpty = (br.readLine() == null);
            //Solo escribe la cabecera del archivo la primera vez que se llama a la función
            if (isFirstLineEmpty) {
                bw.write("VALUE;DATE\n");
            } 		
		    bw.write(tag.getValue()+ ";"+tag.getTime()+"\n");    
            
            br.close();
		    bw.close();
        }
		
    }

}