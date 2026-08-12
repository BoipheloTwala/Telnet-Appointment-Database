import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private final Socket socket;
    private final AppointmentDatabase db;
    private static final boolean DEBUG = true; // set to false before demo

    private static final int IAC = 255, WILL = 251, DONT = 254, ECHO = 1, SGA = 3;

    public ClientHandler(Socket socket, AppointmentDatabase db) {
        this.socket = socket;
        this.db = db;
    }

    public void run() {
        try (Socket s = socket) {
            InputStream in = s.getInputStream();
            OutputStream out = s.getOutputStream();
            Screen screen = new Screen(out);

            // Negotiate: server will echo, client should stop echoing locally
            out.write(new byte[]{ (byte) IAC, (byte) WILL, (byte) ECHO });
            out.write(new byte[]{ (byte) IAC, (byte) WILL, (byte) SGA });
            out.write(new byte[]{ (byte) IAC, (byte) DONT, (byte) ECHO });
            out.flush();

            TelnetLineReader reader = new TelnetLineReader(in, screen);

            if (DEBUG) System.out.println("Client connected: " + s.getRemoteSocketAddress());

            boolean running = true;
            while (running) {
                showMenu(screen);
                screen.print("Choice: ");
                String choice = reader.readLine().trim();

                switch (choice) {
                    case "1": doAdd(screen, reader); break;
                    case "2": doSearch(screen, reader); break;
                    case "3": doDelete(screen, reader); break;
                    case "4": doList(screen); pause(screen, reader); break;
                    case "5":
                        screen.println("Goodbye!");
                        running = false;
                        break;
                    default:
                        screen.println("Invalid choice.");
                }
            }
        } catch (IOException e) {
            if (DEBUG) e.printStackTrace();
        }
    }

    private void showMenu(Screen screen) {
        screen.clear();
        screen.moveTo(1, 1);
        screen.println("---- Appointment Menu ----");
        screen.println("1. Add appointment");
        screen.println("2. Search appointments");
        screen.println("3. Delete appointment");
        screen.println("4. List all appointments");
        screen.println("5. Exit");
    }

    private void doAdd(Screen screen, TelnetLineReader reader) throws IOException {
        screen.print("Date (YYYY-MM-DD): ");
        String date = reader.readLine().trim();
        screen.print("Time (HH:MM): ");
        String time = reader.readLine().trim();
        screen.print("With whom: ");
        String with = reader.readLine().trim();
        screen.print("Notes: ");
        String notes = reader.readLine().trim();

        db.add(new Appointment(date, time, with, notes));
        screen.println("Appointment added.");
        pause(screen, reader);
    }

    private void doSearch(Screen screen, TelnetLineReader reader) throws IOException {
        screen.print("Search keyword: ");
        String keyword = reader.readLine().trim();
        Appointment[] results = db.search(keyword);
        if (results.length == 0) {
            screen.println("No matches.");
        } else {
            for (Appointment a : results) screen.println(a.toString());
        }
        pause(screen, reader);
    }

    private void doDelete(Screen screen, TelnetLineReader reader) throws IOException {
        doList(screen);
        screen.print("Index to delete (0-based): ");
        String s = reader.readLine().trim();
        try {
            int idx = Integer.parseInt(s);
            screen.println(db.deleteByIndex(idx) ? "Deleted." : "Invalid index.");
        } catch (NumberFormatException e) {
            screen.println("Not a number.");
        }
        pause(screen, reader);
    }

    private void doList(Screen screen) {
        Appointment[] all = db.all();
        if (all.length == 0) {
            screen.println("No appointments.");
        }
        for (int i = 0; i < all.length; i++) {
            screen.println(i + ": " + all[i].toString());
        }
    }

    private void pause(Screen screen, TelnetLineReader reader) throws IOException {
        screen.print("Press Enter to continue...");
        reader.readLine();
    }
}