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

public class SocketServerRunnable extends Thread {

    private Server server;
    private boolean runing;
    private ServerSocket listener;
    private int port;

    public SocketServerRunnable(Server server){
        this.server = server;
        loadConfig();
        runing = false;
    }

    public void initialize(){
        try {
            listener  = new ServerSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
		try {
			File propFile = new File(System.getProperty("user.dir")+"/Code/Server/src/main/resources/server.conf");

            System.out.println("PATH: "+propFile.getAbsoluteFile());
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

    private String processRequest(Socket socket) throws IOException{
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // Leer el tag enviado por el cliente
        String tag = in.readLine();
        System.out.println("Tag Solicitado: " + tag);

        // Generar un número aleatorio double
        Tag value = server.getLastNumber();

        // Almacena el valor a enviar enviado
        server.getTagSend().add(value);
        
        // Formatear el valor aleatorio para tener una longitud específica de 100 bytes
        String formattedValue = String.format("%1$-100s", value.getValue());

        return formattedValue;
    }

    private void sendResponse(Socket socket, String response) throws IOException {
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        
        // Enviar el dato aleatorio al cliente
        out.println(response);
    }   

    @Override
    public void start(){
        this.runing = true;
        super.start();
    }

    @Override
    public void run() {
        
        try {
            listener  = new ServerSocket(port);
            while (runing) {
                Socket socket = null;
                try {
                    System.out.println("Esperando");
                     // Aceptar una conexión entrante
                    socket = listener.accept();

                    String response = processRequest(socket);
                    sendResponse(socket, response);
                } finally {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            System.out.println("Ha ocurrido un error de conexión");
        }finally {
            try {
                listener.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("No se ha podido cerrar la conexión");
            }
        }
    }

   
    public void stopProcess(){
        this.runing = false;
    }
    
}
