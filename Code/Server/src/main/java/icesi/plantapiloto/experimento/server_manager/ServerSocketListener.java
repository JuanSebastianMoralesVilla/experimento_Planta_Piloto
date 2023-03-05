package icesi.plantapiloto.experimento.server_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import icesi.plantapiloto.experimento.common.entities.Tag;

public class ServerSocketListener extends Thread{
    private Server server;
    private boolean running;
    private int port;
    ServerSocket listener;
    BufferedReader reader;
    PrintWriter writer;

    public ServerSocketListener(int port, Server server) throws IOException {
        this.server = server;
        this.port = port;
        running = false;
        listener = new ServerSocket(port);
        System.out.println("Listening in port: "+port);
    }

    private String processRequest() throws IOException {
        // Leer el tag enviado por el cliente
        String tag = reader.readLine();
        
        if(tag.equalsIgnoreCase("close") || tag.isEmpty()){
            return null;
        }

        System.out.println("Requested Tag: " + tag);
        Tag value = server.getLastNumber();
        server.getTagSend().add(value);

        // Formatear el valor aleatorio para tener una longitud específica de 100 bytes
        String formattedValue = String.format("%1$-100s", value.getValue());
        return formattedValue;
    }

    @Override
    public void start() {
        this.running = true;
        super.start();
    }

    @Override
    public void run() {
        this.running = true;
        Socket socket;
        try {
            socket = listener.accept();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);
            while (running) {
                String response = processRequest();
                if (response == null) {
                    running = false;
                } else {
                    System.out.println("Response: " + response);
                    writer.println(response);
                }
            }
            System.out.println("Closing port: "+port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopProcess() {
        this.running = false;
    }
}
