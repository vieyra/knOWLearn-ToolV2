package Utils.PythonConnection;

import java.io.*;
import java.net.Socket;

public class ConnectionManager {

    private static ConnectionManager PythonConnection = null;

    public static ConnectionManager getConnection() throws IOException {
        if (PythonConnection == null) {
            PythonConnection = new ConnectionManager();
        }
        return PythonConnection;
    }
    private PrintWriter out;
    private BufferedReader in;
    private Socket socket = null;

    public ConnectionManager() throws IOException {
        this.initialize(50003);
    }

    public ConnectionManager(int port) throws IOException {
        this.initialize(port);
    }

    private void initialize(int port) throws IOException {
        socket = new Socket("localhost", port);
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
            this.out.close();
            this.in.close();
            this.socket.close();
            return true;
        } catch (IOException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}