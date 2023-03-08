package icesi.plantapiloto.experimento.server_manager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class SocketServerRunnable extends Thread {

    private Server server;
    private Properties props;
    private ExecutorService pool;

    public SocketServerRunnable(Server server) throws IOException {
        this.server = server;
        props = new Properties();
        // File propFile = new File(System.getProperty("user.dir") + "/Code/Server/src/main/resources/server.properties");
        File propFile = new File("resources/server.properties");
        InputStream stream = new FileInputStream(propFile);
        props.load(stream);
    }

    @Override
    public void run() {
        pool = Executors.newCachedThreadPool();
        ServerSocketListener listener;
        String[] ports = props.getProperty("SERVER.ports").split("-");
        int minPort = Integer.parseInt(ports[0].trim());
        int maxPort = Integer.parseInt(ports[1].trim());
        try {
            for(int i = minPort; i<=maxPort;i++){
                listener = new ServerSocketListener(i, server);
                pool.submit(listener);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        pool.shutdown();
        try {
            pool.awaitTermination(24, TimeUnit.HOURS);
            server.stop();
        } catch (InterruptedException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }

    public void stopProcess() throws IOException {
        this.pool.shutdownNow();
        server.stop();
        this.interrupt();
    }
}
