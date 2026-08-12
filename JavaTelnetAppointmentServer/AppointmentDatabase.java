import java.io.*;
import java.util.ArrayList;

public class AppointmentDatabase {
    private Appointment[] appointments;
    private int count;
    private final String filename;

    public AppointmentDatabase(String filename) {
        this.filename = filename;
        this.appointments = new Appointment[16];
        this.count = 0;
        load();
    }

    public synchronized boolean add(Appointment a) {
        ensureCapacity();
        appointments[count++] = a;
        save();
        return true;
    }

    public synchronized boolean deleteByIndex(int index) {
        if (index < 0 || index >= count) return false;
        for (int i = index; i < count - 1; i++) {
            appointments[i] = appointments[i + 1];
        }
        appointments[--count] = null;
        save();
        return true;
    }

    public synchronized Appointment[] search(String keyword) {
        ArrayList<Appointment> results = new ArrayList<>();
        String k = keyword.toLowerCase();
        for (int i = 0; i < count; i++) {
            Appointment a = appointments[i];
            if (a.date.toLowerCase().contains(k) ||
                a.time.toLowerCase().contains(k) ||
                a.withWhom.toLowerCase().contains(k) ||
                a.notes.toLowerCase().contains(k)) {
                results.add(a);
            }
        }
        return results.toArray(new Appointment[0]);
    }

    public synchronized Appointment[] all() {
        Appointment[] copy = new Appointment[count];
        System.arraycopy(appointments, 0, copy, 0, count);
        return copy;
    }

    private void ensureCapacity() {
        if (count == appointments.length) {
            Appointment[] bigger = new Appointment[appointments.length * 2];
            System.arraycopy(appointments, 0, bigger, 0, appointments.length);
            appointments = bigger;
        }
    }

    private void load() {
        File f = new File(filename);
        if (!f.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                Appointment a = Appointment.fromFileLine(line);
                if (a != null) {
                    ensureCapacity();
                    appointments[count++] = a;
                }
            }
        } catch (IOException e) {
            // deliberately silent — see DEBUG note in AppointmentServer
        }
    }

    private void save() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(filename, false))) {
            for (int i = 0; i < count; i++) {
                pw.println(appointments[i].toFileLine());
            }
        } catch (IOException e) {
            // deliberately silent
        }
    }
}