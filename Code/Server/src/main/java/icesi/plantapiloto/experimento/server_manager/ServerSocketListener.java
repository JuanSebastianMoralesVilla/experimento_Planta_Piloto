package icesi.plantapiloto.experimento.server_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;

import icesi.plantapiloto.experimento.common.encoders.JsonEncoder;
import icesi.plantapiloto.experimento.common.entities.Measure;
import icesi.plantapiloto.experimento.common.entities.Tag;

public class ServerSocketListener extends Thread{
    private Server server;
    private boolean running;
    ServerSocket listener;
    BufferedReader reader;
    PrintWriter writer;
    JsonEncoder encoder;

    public ServerSocketListener(int port, Server server) throws IOException {
        this.server = server;
        running = false;
        encoder = new JsonEncoder();
        listener = new ServerSocket(port);
        System.out.println("Listening in Port: "+port);
    }

    private String processRequest() throws IOException {
        // Leer el tag enviado por el cliente
        System.out.println("Type input");
        String request = reader.readLine();
        System.out.println("Input: " + request);
        if(request.equalsIgnoreCase("close") || request.isEmpty()){
            return null;
        }
        Measure measure = new Measure();

        
        Timestamp time = new Timestamp(System.currentTimeMillis());
        measure.setRequestTime(time);
        Tag value = server.getLastNumber();
        measure.setValue(value.getValue()+"");
        measure.setName(request.trim());
        
        server.getTagSend().add(value);
        String result = encoder.encode(measure);
        // Formatear el valor aleatorio para tener una longitud específica de 100 bytes
        String formattedValue = String.format("%1$-100s", result);
        return formattedValue;
    }

    @Override
    public void run() {
        this.running = true;
        Socket socket;
        try {

            socket = listener.accept();
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer = new PrintWriter(socket.getOutputStream(), true);

            boolean runningCurrentSocket = true;
            while (runningCurrentSocket) {
                String response = processRequest();
                if (response == null) {
                    runningCurrentSocket = false;
                } else {
                    writer.println(response);
                }
            }
            System.out.println("Closing with current");
            socket.close();
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopProcess() {
        this.running = false;
    }
}
