package icesi.plantapiloto.experimento.server_manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Stack;

public class Server {
	
	private Stack<Double> stack;
	private Random randomGenerator;
	private int seed;
	private int frequency;
	
	public Server() {
		stack = new Stack<>();
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
					this.frequency=Integer.parseInt(frequencyString);;
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

}
