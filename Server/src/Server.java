import java.io.*;
import java.net.*;

public class Server {
    private static final int SERVER_PORT = 12345;
    private static boolean isCLientConntected = false;


    public static void main(String[] args) {

        try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
            System.out.println("Listening on port: " + SERVER_PORT + "...");

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("New connection: " + socket);

                Thread clientThread = new Thread(new ClientHandler(socket));
                clientThread.start();
            }
        }
        catch (IOException e) { e.printStackTrace(); clientDisconnected(); }

    }


    private static boolean isClientConnected() { return isCLientConntected; }
    private static void clientConnected() { isCLientConntected = true; }
    private static void clientDisconnected() { isCLientConntected = false; }



    private static class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) { this.socket = socket; }

        @Override
        public void run() {
            try(BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream())) {

                if (!isClientConnected()) {
                    clientConnected();

                    output.write("Miejsce dostępne");
                    output.newLine();
                    output.flush();

                    Object object = input.readObject();

                    if (object instanceof String) {
                        String listOfProducts = (String) object;
                        System.out.println("Odebrano obiekt ListOfProducts:");

                        System.out.println("[DATA]: " + listOfProducts);
                    }

                    // Opcjonalna odpowiedź do klienta
                    output.write("Odebrano obiekt ListOfProducts. Dziękujemy!");
                    output.newLine();
                    output.flush();

                    clientDisconnected();
                }
                else {
                    output.write("Brak dostępnego miejsca");
                    output.newLine();
                    output.flush();
                    socket.close();
                    System.out.println("Zakończono połączenie: " + socket + "\n\n\n");
                    return;
                }

                socket.close();
                System.out.println("Zakończono połączenie: " + socket + "\n\n\n");
                clientDisconnected();

            }
            catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
                clientDisconnected();
            }
        }
    }

}
