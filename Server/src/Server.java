import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private int port;
    private boolean isClientConntected = false;


    public Server(int port) {
        this.port = port;
        startServer();
    }

    private void startServer() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Listening on port: " + port + "...");

            while (true) {
                Socket socket = acceptNewConnection(serverSocket);
                startNewClientThread(socket);
            }
        } catch (IOException e) {
            e.printStackTrace();
            clientDisconnected();
        }

    }


    private Socket acceptNewConnection(ServerSocket serverSocket) throws IOException {
        Socket socket = serverSocket.accept();
        System.out.println("New connection: " + socket.getInetAddress().getHostAddress());
        return socket;
    }

    private void startNewClientThread(Socket socket) {
        Thread clientThread = new Thread(new ClientHandler(socket));
        clientThread.start();
    }

    private boolean isClientConnected() {
        return isClientConntected;
    }

    private void clientConnected() {
        isClientConntected = true;
    }

    private void clientDisconnected() {
        isClientConntected = false;
    }



    private void sendServerStatus(BufferedWriter output, String message) throws IOException {
        output.write(message);
        output.newLine();
        output.flush();
    }

    private void closeSocket(Socket socket) throws IOException {
        System.out.println("Ending connection: " + socket + "\n\n\n");
        socket.close();
    }

    private ArrayList<Product> getListOfProducts(){
        ArrayList<Product> list = FileManager.readFile();
        return list;
    }

    private ArrayList<Product> addExampleProduct(){

        ArrayList<Product> list = new ArrayList<>();
        Product product = new Product("Example", "3", "Meat", "litr");
        list.add(product);
        return list;

    }

    private ArrayList<Product> prepareList(){
        ArrayList<Product> list = getListOfProducts();

        if (list == null)
            list = addExampleProduct();

        return list;
    }

    private void sendListToClient(ObjectOutputStream objectOutput, ArrayList<Product> list) throws IOException {
        objectOutput.writeObject(list);
        objectOutput.flush();
    }

    private void saveListToFile(ArrayList<Product> list){
        FileManager.saveFile(list);
    }




    private class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

                if (!isClientConnected()) {
                    clientConnected();

                    sendServerStatus(output, "OK");

                    ArrayList<Product> list = prepareList();
                    sendListToClient(objectOutputStream, list);

                    list = (ArrayList<Product>) objectInputStream.readObject();
                    saveListToFile(list);

                    // Opcjonalna odpowiedź do klienta
                    output.write("Odebrano obiekt ListOfProducts. Dziękujemy!");
                    output.newLine();
                    output.flush();

                    clientDisconnected();

                } else {
                    sendServerStatus(output, "FULL");
                    closeSocket(socket);
                    return;
                }

                closeSocket(socket);
                clientDisconnected();

            } catch (IOException e) {
                e.printStackTrace();
                clientDisconnected();
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
