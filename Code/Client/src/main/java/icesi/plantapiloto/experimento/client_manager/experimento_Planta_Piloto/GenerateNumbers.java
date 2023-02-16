package experimento_Planta_Piloto;




public class GenerateNumbers implements Runnable{
	private Server server;
	private boolean run = true;
	
	public GenerateNumbers(Server server) {
        this.server = server;
    }

	@Override
	public void run() {
		while (!run) {
			server.generateNumber();
			try {
				Thread.sleep(server.getFrequency());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public void stopGenerationNumbers() {
		run = false;
	}

}
