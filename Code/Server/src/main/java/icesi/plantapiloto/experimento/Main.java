package icesi.plantapiloto.experimento;

import icesi.plantapiloto.experimento.server_manager.GenerateNumbers;
import icesi.plantapiloto.experimento.server_manager.Server;
import icesi.plantapiloto.experimento.server_manager.SocketServerRunnable;

public class Main {

    public static void main(String[] args){
        Server server = new Server();
        SocketServerRunnable runnableServer = new SocketServerRunnable(server);
        GenerateNumbers generateNumbers = new GenerateNumbers(server);
        generateNumbers.start();
        runnableServer.start();
    }
}
