import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class AppointmentServer {
    private static final int PORT = 2323; // avoid needing admin rights for port 23
    private static final boolean DEBUG = true; // set to false before demo

    public static void main(String[] args) throws IOException {
        AppointmentDatabase db = new AppointmentDatabase("appointments.txt");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            if (DEBUG) System.out.println("Server listening on port " + PORT);
            while (true) {
                Socket client = serverSocket.accept();
                Thread t = new Thread(new ClientHandler(client, db));
                t.setDaemon(true);
                t.start();
            }
        }
    }
}