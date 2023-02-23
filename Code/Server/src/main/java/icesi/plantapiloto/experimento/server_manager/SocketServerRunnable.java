package icesi.plantapiloto.experimento.server_manager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketServerRunnable extends Thread {

    private Server server;
    private boolean runing;
    private int port;

    public SocketServerRunnable(Server server) {
        this.server = server;
        loadConfig();
        runing = false;
    }

    private void loadConfig() {
        try {
            File propFile = new File(System.getProperty("user.dir") + "/Code/Server/src/main/resources/server.conf");

            System.out.println("PATH: " + propFile.getAbsoluteFile());
            InputStream stream = new FileInputStream(propFile);
            BufferedReader red = new BufferedReader(new InputStreamReader(stream));

            String line = red.readLine();
            while (line != null && !line.equals("")) {
                String prop[] = line.trim().split("=");
                if (prop[0].contains("PORT")) {
                    String portString = prop[1];
                    this.port = Integer.parseInt(portString);
                }
                line = red.readLine();
            }
            red.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String processRequest(BufferedReader in) throws IOException {
        
        // Leer el tag enviado por el cliente
        String tag = in.readLine();

        if(tag.equalsIgnoreCase("close") || tag.isEmpty()){
            return null;
        }

        System.out.println("Tag Solicitado: " + tag);

        // Generar un número aleatorio double
        Tag value = server.getLastNumber();

        // Almacena el valor a enviar enviado
        server.getTagSend().add(value);

        // Formatear el valor aleatorio para tener una longitud específica de 100 bytes
        String formattedValue = String.format("%1$-100s", value.getValue());

        return formattedValue;
    }

    private void sendResponse(PrintWriter out, String response) throws IOException {
        System.out.println("Respuesta: " + response);
        out.println(response);
    }

    @Override
    public void start() {
        this.runing = true;
        super.start();
    }

    @Override
    public void run() {
        ExecutorService executorService = Executors.newCachedThreadPool();
        try (ServerSocket listener = new ServerSocket(port)) {
            System.out.println("Escuchando en el puerto: "+port);
            while (runing) {
                
                Socket socket = listener.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                executorService.submit(() -> {
                    boolean running = true;
                    try {
                        while (running) {
                            
                            String response = processRequest(in);
                            if (response == null) {
                                running = false;
                            } else {
                                sendResponse(out, response);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            System.out.println("Conexión cerrada para: "+socket.getLocalAddress().getHostName());
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdownNow();
        }
    }

    public void stopProcess() {
        this.runing = false;
    }

}
