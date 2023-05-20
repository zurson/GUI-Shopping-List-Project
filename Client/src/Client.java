import javax.swing.plaf.synth.SynthOptionPaneUI;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client {

    private final String host;
    private final int port;
    private final int retryTime = 5;
    private final boolean isDebugActive = true;
    Debug debug;
    ListaZakupow listaZakupow;

    public Client(String host, int port) {
        this.port = port;
        this.host = host;
        this.debug = new Debug(isDebugActive);

        connectToTheServer();
    }

    private void connectToTheServer() {

        while (true) {

            try (Socket socket = new Socket(host, port);
                 BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream())) {


                System.out.println("\nConnected to the server!");

//                 Sprawdzenie dostępności miejsca

                if (hasServerFreeSlot(input)) {

                    debug.message("Waiting for a list from server");
                    ArrayList<Product> listFromServer = (ArrayList<Product>) objectInputStream.readObject();
                    debug.message("List has arrived");

                    debug.message("Opening GUI");
                    openGUI(listFromServer, objectOutputStream);
                    debug.message("GUI opened");

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

            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                emergencyListClose();

                try {
                    retryConnection("Server not responding. Retrying in " + retryTime + " seconds.", retryTime);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    return;
                }
            }

        }

    }


    private void emergencyListClose() {
        if (listaZakupow != null) {
            listaZakupow.emergencyClose();
            listaZakupow = null;
        }
    }

    private void openGUI(ArrayList<Product> list, ObjectOutputStream objectOutputStream) {
        listaZakupow = new ListaZakupow(objectOutputStream);
        listaZakupow.fillList(list);
        listaZakupow.showList();
    }

    public static void sendListToServer(ObjectOutputStream objectOutputStream, ArrayList<Product> list) throws IOException {
        objectOutputStream.writeObject(list);
        objectOutputStream.flush();
    }

    private boolean hasServerFreeSlot(BufferedReader input) throws IOException {

        String status = input.readLine();
        debug.message("Server status - " + status);

        return status.equalsIgnoreCase("OK");
    }

    public void retryConnection(String message, int time) throws InterruptedException {
        System.out.println(message);
        Thread.sleep(time * 1000);
    }

}
