import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class FileManager {

    public static final String FILENAME = "shopping_list.txt";
    public static File file;

    private static boolean openFile() {

        try{
            file = new File(FILENAME);
            file.createNewFile();
            return true;
        }
        catch (IOException ignored) {
            System.out.println("Cannot to open a file!");
            return false;
        }

    }

    public static void saveFile(ArrayList<Product> productsList) {

        if (!openFile())
            return;

        try (FileWriter myWriter = new FileWriter(FILENAME)) {
            for (Product product : productsList){
                String data = product.getStringLine();
                myWriter.write(data + "\n");
            }
        }
        catch (IOException ignored) {
            System.out.println("Cannot to write to file!");
            return;
        }

        file = null;
    }

    public static ArrayList<Product> readFile(){

        if (!openFile())
            return null;

        ArrayList<Product> listOfProducts = new ArrayList<>();

        try(Scanner myReader = new Scanner(file)) {

            while (myReader.hasNextLine()) {
                String lineFromFile = myReader.nextLine();
                String product = lineFromFile.replaceAll("\n", "");

                String[] serializedLine = product.split(";");

                try{
                    listOfProducts.add(new Product(serializedLine[0], serializedLine[1], serializedLine[2], serializedLine[3]));
                }
                catch (ArrayIndexOutOfBoundsException ignore){}

            }

        }
        catch (IOException e){
            System.out.println("Unable to read the file!");
            return null;
        }

        file = null;
        return listOfProducts;
    }

}
