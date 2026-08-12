## Telnet Appointment Database

An appointment "database" (add / search / delete) served entirely over a raw Telnet session, all interaction happens through telnet, with no console input/output once the server is running (debug logging aside).

## How it works
ServerSocket accepts Telnet connections and negotiates Telnet options so the server handles character echo (IAC WILL ECHO) instead of the client.
Screen wraps ANSI/VT100 escape sequences (ESC[2J to clear, ESC[y;xH to position the cursor) so the rest of the code doesn't deal with raw escape codes directly.
TelnetLineReader reads raw bytes off the socket, filters out Telnet negotiation bytes, handles backspace, and echoes each typed character back itself.
Appointments are stored in a manually-grown array and persisted to appointments.txt after every change.

## Running
bash

javac *.java

java AppointmentServer

## Connect with:

bash

telnet localhost 2323

(Windows: enable the Telnet Client via Control Panel → Programs → Turn Windows features on or off, or use PuTTY in Telnet mode.)

## Notes
Port 2323 is used instead of the standard 23 to avoid needing administrator privileges.
Set DEBUG = false in ClientHandler / AppointmentServer before a formal demo, per the assignment's requirement that the server produce no console output once deployed.
