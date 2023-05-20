import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {

    private int port;
    private boolean isClientConntected = false;
    private final boolean isDebugActive = true;
    Debug debug;


    public Server(int port) {
        this.port = port;
        this.debug = new Debug(isDebugActive);

        startServer();
    }

    private void startServer() {

        debug.message("Starting server");
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            debug.message("Server started");
            System.out.println("Listening on port: " + port + "...");

            while (true) {
                Socket socket = acceptNewConnection(serverSocket);
                startNewClientThread(socket);
                debug.message("New connection accepted");
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

    private ArrayList<Product> getListOfProducts() {
        ArrayList<Product> list = FileManager.readFile();
        return list;
    }

    private ArrayList<Product> addExampleProduct() {

        ArrayList<Product> list = new ArrayList<>();
        Product product = new Product("Example", "3", "Meat", "litr");
        list.add(product);
        return list;

    }

    private ArrayList<Product> prepareList() {
        ArrayList<Product> list = getListOfProducts();

        if (list == null)
            list = addExampleProduct();

        return list;
    }

    private void sendListToClient(ObjectOutputStream objectOutput, ArrayList<Product> list) throws IOException {
        objectOutput.writeObject(list);
        objectOutput.flush();
    }

    private void saveListToFile(ArrayList<Product> list) {

        if (list == null)
            return;

        FileManager.saveFile(list);
    }

    private void connectionLostMessage() {
        System.out.println("\n\n\n********");
        System.out.println("Connection lost");
        System.out.println("********");
    }


    private class ClientHandler implements Runnable {
        private final Socket socket;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            debug.message("New client thread started");

            try (BufferedWriter output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                 ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                 ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream())) {

                debug.message("Checking for free slot");
                if (!isClientConnected()) {
                    debug.message("Server has free slot");
                    clientConnected();

                    sendServerStatus(output, "OK");

                    debug.message("Preparing list");
                    ArrayList<Product> list = prepareList();

                    try {
                        debug.message(list.get(0).toString());
                    } catch (IndexOutOfBoundsException ignore) {
                        debug.message("List is empty");
                    }

                    debug.message("Sending list");
                    Thread.sleep(1000);
                    sendListToClient(objectOutputStream, list);
                    debug.message("List has been sent");

                    debug.message("Waiting for modified list");
                    list = (ArrayList<Product>) objectInputStream.readObject();
                    debug.message("List has arrived");

                    debug.message("Saving list");
                    saveListToFile(list);

                    // Opcjonalna odpowiedź do klienta
                    sendServerStatus(output, "OK");

                    clientDisconnected();
                    closeSocket(socket);
                    debug.message("End of Thread");

                } else {
                    debug.message("Server is full");
                    sendServerStatus(output, "FULL");
                    closeSocket(socket);
                }

            } catch (IOException e) {
                connectionLostMessage();
                clientDisconnected();
            } catch (ClassNotFoundException e) {
                connectionLostMessage();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
