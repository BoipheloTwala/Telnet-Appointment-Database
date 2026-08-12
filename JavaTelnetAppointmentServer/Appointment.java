public class Appointment {
    String date;      // e.g. 2026-08-15
    String time;      // e.g. 14:30
    String withWhom;
    String notes;

    public Appointment(String date, String time, String withWhom, String notes) {
        this.date = date;
        this.time = time;
        this.withWhom = withWhom;
        this.notes = notes;
    }

    // Encode as one line for file storage
    public String toFileLine() {
        return date + "|" + time + "|" + withWhom + "|" + notes;
    }

    public static Appointment fromFileLine(String line) {
        String[] parts = line.split("\\|", -1);
        if (parts.length < 4) return null;
        return new Appointment(parts[0], parts[1], parts[2], parts[3]);
    }

    public String toString() {
        return String.format("%-12s %-8s %-15s %s", date, time, withWhom, notes);
    }
}