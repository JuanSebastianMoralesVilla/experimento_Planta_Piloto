package icesi.plantapiloto.experimento.server_manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Random;
import java.util.Stack;
import icesi.plantapiloto.experimento.common.entities.Tag;

public class Server {

	private Stack<Tag> stack;
	private Random randomGenerator;
	private int seed;
	private int frequency;
	private Stack<Tag>  tagsSend;
	private String serverIp;
	private String serverName;
	private String serverPort;
	private TagManager tagManager;
	
	public Server() {
		stack = new Stack<>();
		tagsSend= new Stack<>();
		this.loadConfig();
		randomGenerator = new Random(seed);
	}

	public static void main(String[] args) throws IOException{
		Server server = new Server();
        SocketServerRunnable runnableServer = new SocketServerRunnable(server);
        GenerateNumbers generateNumbers = new GenerateNumbers(server);
		server.tagManager = new TagManager(server);
        generateNumbers.start();
        runnableServer.start();
	}

	private void loadConfig() {
		try {
			// File propFile = new File(System.getProperty("user.dir") + "/Code/Server/src/main/resources/server.properties");
			File propFile = new File("resources/server.properties");

			InputStream stream = new FileInputStream(propFile);
			BufferedReader red = new BufferedReader(new InputStreamReader(stream));

			String line = red.readLine();
			while (line != null && !line.equals("")) {
				String prop[] = line.trim().split("=");
				if (prop[0].contains("SEED")) {
					String seedString = prop[1];
					this.seed=Integer.parseInt(seedString);;

				}else if (prop[0].contains("FREQUENCY")){
					String frequencyString = prop[1];
					this.frequency=Integer.parseInt(frequencyString);
				}
				else if (prop[0].contains("NAME_SERVER")){
					this.serverName = prop[1];
				}
				else if (prop[0].contains("IP_SERVER")){
					this.serverIp = prop[1];
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
		Runnable addMessageRunnable = new Runnable() {
            public void run() {
                try {
                   tagManager.printcsv(tag);
                } catch (IOException e) {
                     e.printStackTrace();
                }
            }
        };
        Thread thread = new Thread(addMessageRunnable);
        thread.start();
		stack.push(tag);
	}
	
	public Tag getLastNumber() {
		return stack.peek();
	}
	
	public Integer getFrequency() {
		return this.frequency;
	}

	public Stack<Tag> getTagSend(){
		return this.tagsSend;
	}

	public String getServerName(){
		return this.serverName;
	}

	public String getServerIp(){
		return this.serverIp;
	}

	public String getServerPort(){
		return this.serverPort;
	}
	public void setPort(String port){
		this.serverPort=port;
	}
}
