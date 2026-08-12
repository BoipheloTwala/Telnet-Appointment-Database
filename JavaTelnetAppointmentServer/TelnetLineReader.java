import java.io.*;

public class TelnetLineReader {
    private final InputStream in;
    private final Screen screen;
    private static final int IAC = 255;

    public TelnetLineReader(InputStream in, Screen screen) {
        this.in = in;
        this.screen = screen;
    }

    public String readLine() throws IOException {
        StringBuilder sb = new StringBuilder();
        int b;
        while ((b = in.read()) != -1) {
            if (b == IAC) {
                in.read(); // command byte
                in.read(); // option byte
                continue;  // swallow Telnet negotiation, don't echo it
            }
            if (b == '\r') {
                int next = in.read(); // consume the LF that follows CR
                screen.print("\r\n");
                break;
            }
            if (b == '\n') {
                screen.print("\r\n");
                break;
            }
            if (b == 8 || b == 127) { // Backspace / Delete
                if (sb.length() > 0) {
                    sb.deleteCharAt(sb.length() - 1);
                    screen.print("\b \b"); // erase the character visually
                }
                continue;
            }
            char c = (char) b;
            sb.append(c);
            screen.print(String.valueOf(c)); // the server does the echoing
        }
        return sb.toString();
    }
}