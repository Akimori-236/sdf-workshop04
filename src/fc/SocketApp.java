package fc;

import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class SocketApp {
    public static void main(String[] args) {

        // run error message
        String usage = """
                -------------------
                SERVER
                <program> <server> <port> <cookie-file.txt>
                -------------------
                MULTI THREADED SERVER
                <program> <threaded-server> <port> <cookie-file.txt>
                -------------------
                CLIENT
                <program> <client> <host> <port>
                -------------------
                """;
        // check cmdline input length
        if (args.length != 3) {
            System.err.println("Incorrect usage, please check commands");
            System.err.println(usage);
            return; // end program
        }

        String type = args[0];
        if (type.equalsIgnoreCase("server")) {
            // SERVER
            int port = Integer.parseInt(args[1]);
            String filename = args[2];
            StartServer(port, filename);
        } else if (type.equalsIgnoreCase("threaded-server")) {
            // MULTI-THREADED SERVER
            int port = Integer.parseInt(args[1]);
            String filename = args[2];
            StartMultiThreadServer(port, filename);
        } else if (type.equalsIgnoreCase("client")) {
            // CLIENT
            String hostname = args[1];
            int port = Integer.parseInt(args[2]);
            StartClient(hostname, port);
        } else {
            System.err.println("Invalid arguments");
        }
    }

    public static void StartMultiThreadServer(int port, String filename) {
        System.out.println("MULTI THREADED SERVER STARTED");
        ServerSocket server;
        try {
            server = new ServerSocket(port);

            while (true) {
                Socket socket = server.accept();
                // OUTPUT STREAM
                DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
                // INPUT STREAM
                DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

                Thread handler = new ClientHandler(socket, dis, dos, filename);
                System.out.println("Client connected");
                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void StartServer(int port, String filename) {
        System.out.println("SERVER STARTED");
        try {
            ServerSocket server = new ServerSocket(port);
            Socket socket = server.accept();

            // OUTPUT STREAM
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            // INPUT STREAM
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            while (true) {
                String fromClient = dis.readUTF();
                // exit command
                if (fromClient.equalsIgnoreCase("exit")) {
                    System.out.println("Client initiated exit");
                    break;
                }

                System.out.println("Client Message> " + fromClient);
                if (fromClient.equalsIgnoreCase("get-cookie")) {
                    // get random cookie
                    // String cookie = "Dummy cookie";
                    Cookie c = new Cookie();
                    String cookie = c.getRandomCookie(filename);
                    
                    // send cookie
                    dos.writeUTF(cookie);
                    dos.flush();
                } else {
                    dos.writeUTF("SERVER> Invalid command");
                    dos.flush();
                }
            }
            socket.close();
            // Cookie c = new Cookie(filename);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void StartClient(String host, int port) {
        System.out.println("CLIENT STARTED");
        try {
            Socket socket = new Socket(host, port);

            // OUTPUT STREAM
            DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            // INPUT STREAM
            DataInputStream dis = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            Scanner sc = new Scanner(System.in);
            boolean stop = false;
            while (!stop) {
                String line = sc.nextLine();
                if (line.equalsIgnoreCase("exit")) {
                    dos.writeUTF("exit");
                    dos.flush();
                    stop = true;
                    break;
                } else if (line.equalsIgnoreCase("get-cookie")) {
                    System.out.println("Getting cookie...");
                } else {
                    System.err.println("Invalid command: -> " + line);
                }
                // Send to server
                dos.writeUTF(line);
                dos.flush();
                // Wait for server response
                String fromServer = dis.readUTF();
                System.out.println("Server Message> " + fromServer);
            }
            
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
