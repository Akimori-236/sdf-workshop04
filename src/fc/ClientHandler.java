package fc;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientHandler extends Thread {
    Socket s;
    DataInputStream dis;
    DataOutputStream dos;
    String filename;

    public ClientHandler(Socket s, DataInputStream dis, DataOutputStream dos, String filename) {
        this.s = s;
        this.dis = dis;
        this.dos = dos;
        this.filename = filename;
    }

    @Override
    public void run() {
        // socket communication
        while (true) {
            try {
                String fromClient = dis.readUTF();
                String threadName = Thread.currentThread().getName();
                if (fromClient.equalsIgnoreCase("exit")) {
                    break;
                }
                System.out.println("Msg received from " + threadName + " -> " + fromClient);
                if (fromClient.equalsIgnoreCase("get-cookie")) {
                    // get random cookie
                    // String cookie = "Dummy cookie";
                    Cookie c = new Cookie();
                    String cookie = c.getRandomCookie(this.filename);
                    // send cookie
                    dos.writeUTF(cookie);
                    dos.flush();
                } else {
                    dos.writeUTF("SERVER> Invalid command");
                    dos.flush();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
