import java.io.*;
import java.net.*;

public class Client {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        while (true) {
            try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT)) {

                System.out.println("\nConnected to the server!");

                // Sprawdzenie dostępności miejsca
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String response = reader.readLine();
                System.out.println("Odpowiedź serwera: " + response);

                if (response.equalsIgnoreCase("Miejsce dostępne")) {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());

                    String listOfProducts = "test message xd";

                    Thread.sleep(10000);
                    output.writeObject(listOfProducts);
                    output.flush();

                    System.out.println("Wysłano obiekt ListOfProducts do serwera.");

                    // Oczekiwanie na potwierdzenie z serwera (opcjonalne)
                    response = reader.readLine();
                    System.out.println("Odpowiedź serwera: " + response);

                    break; // Wyjście z pętli, jeśli komunikacja z serwerem przebiegła pomyślnie
                } else {
                    System.out.println("Brak dostępnego miejsca. Ponawianie próby za 5 sekund...");
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("Nie można połączyć się z serwerem. Ponawianie próby za 5 sekund...");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    break;
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}