package icesi.plantapiloto.experimento.server_manager;

public class GenerateNumbers implements Runnable{
	private Server server;
	private boolean stop;
	
	public GenerateNumbers(Server server) {
        this.server = server;
    }

	@Override
	public void run() {
		while (!stop) {
			server.generateNumber();
			try {
				Thread.sleep(server.getFrequency());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void stopGenerationNumbers(boolean b) {
		this.stop = b;
	}

}
