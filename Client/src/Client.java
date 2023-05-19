import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {

    private String host;
    private int port;
    private static final int retryTime = 5;

    Socket socket;


    public Client(String host, int port) {
        this.port = port;
        this.host = host;

        connectToTheServer();
    }

    private void connectToTheServer() {

        while (true) {

            try {

                socket = new Socket(host, port);
                break;

            } catch (IOException e) {
                try {
                    retryConnection("Server not responding. Retrying in " + retryTime + " seconds.", retryTime);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return;
                }
            }

        }


        while (true) {

            System.out.println("TRY TO CONNECT");

            try (BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {

                System.out.println("\nConnected to the server!");

                // Sprawdzenie dostępności miejsca

                if (hasServerFreeSlot(input)) {

                    ArrayList<Product> listFromServer = (ArrayList<Product>) objectInputStream.readObject();

                    ListaZakupow listaZakupow = new ListaZakupow();
                    listaZakupow.fillList(listFromServer);
                    listaZakupow.showList();


                    Product product = new Product("Chujew", "1", "Meat", "kg");
                    Product product2 = new Product("Chujewyxd", "1", "Meat", "kg");

                    ArrayList<Product> list = new ArrayList<>();
                    list.add(product);
                    list.add(product2);

                    objectOutputStream.writeObject(list);
                    objectOutputStream.flush();

//                  Oczekiwanie na potwierdzenie z serwera
                    String response = input.readLine();
                    System.out.println("Odpowiedź serwera: " + response);

                    break; // Exit loop

                } else {

                    try {
                        retryConnection("Server is full. Retrying in " + retryTime + " seconds.", retryTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

    }


    private boolean hasServerFreeSlot(BufferedReader input) throws IOException {

        String status = input.readLine();
        System.out.println("STATUS: " + status);

        return status.equalsIgnoreCase("OK");
    }

    public void retryConnection(String message, int time) throws InterruptedException {
        System.out.println(message);
        Thread.sleep(time * 1000);
    }
}
