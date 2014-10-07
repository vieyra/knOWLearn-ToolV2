package Utils.PythonConnection;

import java.io.*;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;

public class ConnectionManager {

   private static ConnectionManager PythonConnection = null;
   
   private static int MIN_PORT_NUMBER = 9050;
   private static int MAX_PORT_NUMBER = 99050;
    
    public static ConnectionManager getConnection() throws IOException, InterruptedException {
        if (PythonConnection == null) {
            PythonConnection = new ConnectionManager();
        }
        return PythonConnection;
    }
    
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket = null;
    private Process process;
    
    public ConnectionManager() throws IOException, InterruptedException {
        this.initialize(50003);
    }

    public ConnectionManager(int port) throws IOException, InterruptedException {
        this.initialize(port);
    }

    private void initialize(int port) throws IOException, InterruptedException {
       while(!isAvailable(port)){
          port += 5;
       }
       process = Runtime.getRuntime().exec("python ConnectionManager.py " + port);
//       process = Runtime.getRuntime().exec("c:/Python27/python.exe ConnectionManager.py " + port);
       Thread.sleep(5000);
       socket = new Socket("localhost", port);
       System.out.println("--- Initializing connection in port " + port + "---");
       out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
       in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public String send(String msg) {
        out.println(msg);
        this.flush();
        try {
            return this.recv();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
            System.err.println(ex.getCause());
            return "Error";
        }
    }

    private void flush() {
        out.flush();
    }

    public String recv() throws IOException {
        return in.readLine();
    }

    public boolean close() {
        try {
            out.close();
            in.close();
            socket.close();
            process.destroy();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
    
    public static boolean isAvailable(int port) {
      if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
         throw new IllegalArgumentException("Invalid start port: " + port);
      }

      ServerSocket ss = null;
      DatagramSocket ds = null;
      try {
         ss = new ServerSocket(port);
         ss.setReuseAddress(true);
         ds = new DatagramSocket(port);
         ds.setReuseAddress(true);
         return true;
      } catch (IOException e) {
      } finally {
         if (ds != null) {
            ds.close();
         }
         if (ss != null) {
            try {
               ss.close();
            } catch (IOException e) {
               /* should not be thrown */
            }
         }
      }
      return false;
   }
   
}