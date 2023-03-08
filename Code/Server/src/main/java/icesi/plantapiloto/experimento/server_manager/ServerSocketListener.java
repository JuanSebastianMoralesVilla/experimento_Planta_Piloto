package icesi.plantapiloto.experimento.server_manager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;


import icesi.plantapiloto.experimento.common.encoders.JsonEncoder;
import icesi.plantapiloto.experimento.common.encoders.ObjectEncoder;
import icesi.plantapiloto.experimento.common.entities.Measure;
import icesi.plantapiloto.experimento.common.entities.Tag;

public class ServerSocketListener extends Thread{
    private Server server;
    ServerSocket listener;
    BufferedReader reader;
    PrintWriter writer;
    ObjectEncoder encoder;

    public ServerSocketListener(int port, Server server) throws IOException {
        this.server = server;
        encoder = new JsonEncoder();
        listener = new ServerSocket(port);
        System.out.println("Listening in Port: "+port);
    }

    private String processRequest() throws IOException {
        // Leer el tag enviado por el cliente
        String request = reader.readLine().trim();
        if(request.equalsIgnoreCase("close") || request.equalsIgnoreCase("off")){
            return request.trim().toLowerCase();
        }

        Measure measure = new Measure();
        Timestamp time = new Timestamp(System.currentTimeMillis());
        // System.out.println(time.toString());
        measure.setRequestTime(time);
        Tag value = server.getLastNumber();
        measure.setValue(value.getValue()+"");
        measure.setName(request);
        
        server.getTagSend().add(value);
        String result = encoder.encode(measure);
        // Formatear el valor aleatorio para tener una longitud específica de 100 bytes
        String formattedValue = String.format("%1$-100s", result);
        return formattedValue;
    }

    @Override
    public void run() {
        Socket socket = null;
        boolean running = true;
        try {
            while(running){
                socket = listener.accept();
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                writer = new PrintWriter(socket.getOutputStream(), true);

                boolean runningCurrentSocket = true;
                while (runningCurrentSocket) {
                    String response = processRequest();
                    if (response.equals("close")) {
                        runningCurrentSocket = false;
                    } else if (response.equals("off")){
                        runningCurrentSocket = false;
                        running = false;
                    } else {
                        writer.println(response);
                    }
                }
            }
            
            System.out.println("Turning off");
            socket.close();
            listener.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
