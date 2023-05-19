import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private static final int retryTime = 5;


    private static boolean hasServerFreeSlot(BufferedReader input) throws IOException {
        return input.readLine().equalsIgnoreCase("OK");
    }

    public static void main(String[] args) {
        while (true) {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
                 BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream())) {

                System.out.println("\nConnected to the server!");

                // Sprawdzenie dostępności miejsca

                if (hasServerFreeSlot(input)) {

                    // TODO: Odbieranie listy produktow od Servera

                    // Wyswietlanie listy zakupow
                    new ListaZakupow().showList();

                    // TODO: Tworzenie obiektu ze zmodyfikowana lista
                    // TODO: Wysylanie listy do serwera



                    String listOfProducts = "test message xd";
                    output.writeObject(listOfProducts);
                    output.flush();

                    System.out.println("Wysłano obiekt ListOfProducts do serwera.");

                    // Oczekiwanie na potwierdzenie z serwera
                    String response = input.readLine();
                    System.out.println("Odpowiedź serwera: " + response);

                    break; // Exit loop

                } else {

                    try {
                        retryConnection("Server is full. Retrying in " + retryTime + " seconds", retryTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }

                }

            } catch (IOException e) {

                try {
                    retryConnection("Server not responding. Retrying in " + retryTime + " seconds", retryTime);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    break;
                }

            }
        }
    }

    public static void retryConnection(String message, int time) throws InterruptedException {
        System.out.println(message);
        Thread.sleep(retryTime);
    }
}