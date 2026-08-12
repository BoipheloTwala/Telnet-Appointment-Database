import java.io.*;

public class Screen {
    private final PrintWriter writer;
    private static final char ESC = 27;

    public Screen(OutputStream out) {
        this.writer = new PrintWriter(new OutputStreamWriter(out), true);
    }

    public void clear() {
        writer.print(ESC + "[2J");
        writer.flush();
    }

    // ESC[y;xH — moves cursor to column x, row y
    public void moveTo(int x, int y) {
        writer.print(ESC + "[" + y + ";" + x + "H");
        writer.flush();
    }

    public void printAt(int x, int y, String text) {
        moveTo(x, y);
        writer.print(text);
        writer.flush();
    }

    public void print(String text) {
        writer.print(text);
        writer.flush();
    }

    public void println(String text) {
        writer.print(text);
        writer.print("\r\n"); // Telnet terminals expect CRLF, not just \n
        writer.flush();
    }
}