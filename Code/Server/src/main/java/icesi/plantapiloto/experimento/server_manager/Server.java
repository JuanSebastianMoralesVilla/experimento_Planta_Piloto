package icesi.plantapiloto.experimento.server_manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Stack;

public class Server {

	private final static String PATH = "data";
	private final static String TEST = "test";
	private final static String FILE_TEST = "XHGRID.csv";

	private Stack<Double> stack;
	private Random randomGenerator;
	private int seed;
	private int frequency;
	private Stack<Double> tagsGenerate;
	private Stack<Tag>  tagsSend;
	
	public Server() {
		tagsGenerate = new Stack<>();
		tagsSend= new Stack<>();
		this.loadConfig();
		randomGenerator = new Random(seed);
	}

	private void loadConfig() {
		try {
			File propFile = new File("server.conf");

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void generateNumber() {
		double number = randomGenerator.nextDouble();
		stack.push(number);
	}
	
	public Double getLastNumber() {
		if (!stack.empty()) {
			return stack.peek();
		} else {
			return (double) -1;
		}
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
		

			bw.write( currentTag.getName()+ ";" +currentTag.getValue()+ ";"+currentTag.getTimeTag()+"\n");                 	
			}

		bw.write("________________________________________________________________");
		bw.close();
    }

	public Stack<Tag> getTagSend(){
		return tagsSend;
	}
}
