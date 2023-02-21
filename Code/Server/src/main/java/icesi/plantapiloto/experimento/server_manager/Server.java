package icesi.plantapiloto.experimento.server_manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Stack;

public class Server {

	private final static String PATH = "data";
	private final static String FILE_TEST = "XHGRID.csv";

	private Stack<Tag> stack;
	private Random randomGenerator;
	private int seed;
	private int frequency;
	private Stack<Tag>  tagsSend;
	
	public Server() {
		stack = new Stack<>();
		tagsSend= new Stack<>();
		this.loadConfig();
		randomGenerator = new Random(seed);
	}

	private void loadConfig() {
		try {
			File propFile = new File("src/resources/server.conf");

			InputStream stream = new FileInputStream(propFile);
			BufferedReader red = new BufferedReader(new InputStreamReader(stream));

			String line = red.readLine();
			while (line != null && !line.equals("")) {
				String prop[] = line.trim().split("=");
				if (prop[0].contains("SEED")) {
					String seedString = prop[1];
					this.seed=Integer.parseInt(seedString);;

				}else {
					String frequencyString = prop[1];
					this.frequency=Integer.parseInt(frequencyString);
				}
				line = red.readLine();
			}
			red.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void generateNumber() {
		Integer number = randomGenerator.nextInt(100);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		Tag tag = new Tag();
		tag.setValue(number);
		tag.setTime(timestamp);
		stack.push(tag);
	}
	
	public Tag getLastNumber() {
		return stack.peek();
	}
	
	public Integer getFrequency() {
		return this.frequency;
	}

	public void printcsv()throws IOException{

		File file= new File(PATH+"/"+FILE_TEST);
        FileWriter fw= new FileWriter(file);
        BufferedWriter bw= new BufferedWriter(fw);
		
		bw.write("TAG;VALUE;DATE\n");

		for(int i= 0; i<tagsSend.size();i++){


			Tag currentTag = tagsSend.pop();			
		

			bw.write( currentTag.getName()+ ";" +currentTag.getValue()+ ";"+currentTag.getTime()+"\n");                 	
			}
		bw.close();
    }

	public Stack<Tag> getTagSend(){
		return tagsSend;
	}
}
