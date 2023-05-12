import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

public class ListaZakupow extends JFrame {

    private final String FRAME_NAME = "Shopping List";
    private final String[] UNITS_LIST = {"szt", "kg", "litr"};
    private final String[] CATEGORIES_LIST = { null, "Bread", "Meat", "Fruits", "Vegetables", "Sweets", "Drinks", "Chemicals"};

    private ArrayList<Product> allProductsList;
    private DefaultListModel<Product> displayedProductsList;
    JList<Product> listToSelectProduct;

    JTextField productNameField;
    JTextField amountField;
    JPanel operationsPanel;

    boolean changesAreSaved = true;

    private JComboBox<String> unitsComboBox;
    private JComboBox<String> categoriesComboBox;


    public ListaZakupow() {

        // Setting Up The Environment
        initializeErrorManager();
        setFrameBasicFeatures();
        initializeLists();

        JPanel mainPanel = initializeMainPanel();

        // New Products Panel
        JPanel addProductPanel = createNewProductPanel(mainPanel);
        createNameField(addProductPanel);
        createAmountField(addProductPanel);
        createCategoriesField(addProductPanel);

        JButton confirmButton = createNewButton("Confirm");
        handleConfirmButton(confirmButton);
        addProductPanel.add(confirmButton);

        // Main View
        createProductsListPanel(mainPanel);
        createOperationsPanel(mainPanel);


        // Operations Panel
        JButton deleteSelectedButton = createNewButton("Delete Selected Product");
        addButtonToPanel(operationsPanel, deleteSelectedButton);
        handleDeleteSelectedButton(deleteSelectedButton);

        JButton deleteCategoryButton = createNewButton("Delete Category");
        addButtonToPanel(operationsPanel, deleteCategoryButton);
        handleDeleteCategoryButton(deleteCategoryButton);

        JButton deleteAllProductsButton = new JButton("Delete All Products");
        addButtonToPanel(operationsPanel, deleteAllProductsButton);
        handleDeleteAllProductsButton(deleteAllProductsButton);

        JButton filterButton = createNewButton("Filter By Category");
        addButtonToPanel(operationsPanel, filterButton);
        handleFilterButton(filterButton);

        JButton saveButton = createNewButton("Save Changes");
        addButtonToPanel(operationsPanel, saveButton);
        handleSaveButton(saveButton);

    }

    public void showList(){
//        addExampleProducts();
        loadDataFromFile();
        showProgramWindow();
    }


//    public void addExampleProducts() {
//
//        addNewProduct(new Product("Jabłka", "2.5", "Fruits", "kg"));
//        addNewProduct(new Product("Gruszki", "1.5", "Fruits", "kg"));
//        addNewProduct(new Product("Pomarańcze", "3.0", "Fruits", "kg"));
//        addNewProduct(new Product("Chleb", "1", "Bread", "szt"));
//        addNewProduct(new Product("Bułki", "4", "Bread", "szt"));
//        addNewProduct(new Product("Rogale", "6", "Bread", "szt"));
//        addNewProduct(new Product("Domestos", "1", "Chemicals", "litr"));
//        addNewProduct(new Product("Mydło", "1", "Chemicals", "szt"));
//        addNewProduct(new Product("Pasta do zębów", "2", "Chemicals", "szt"));
//    }



    private void showProgramWindow() { setVisible(true); }
    private JButton createNewButton(String name){
        return new JButton(name);
    }
    private void initializeErrorManager(){
        new InfoBox(ListaZakupow.this);
    }
    private void addButtonToPanel(JPanel panel, JButton button) { panel.add(button); }
    private void loadDataFromFile(){

        allProductsList = FileManager.readFile();

        if (allProductsList == null)
            return;

        for (Product product : allProductsList)
            displayedProductsList.addElement(product);

    }


    private void setFrameBasicFeatures(){
        setTitle(FRAME_NAME);


        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                if (changesAreSaved == false){

                    int confirm = JOptionPane.showConfirmDialog(ListaZakupow.this,
                            "There are unsaved changes!\nDo you want do save it?",
                            "WARNING",
                            JOptionPane.YES_NO_OPTION);

                    if (confirm == JOptionPane.YES_OPTION)
                        FileManager.saveFile(allProductsList);

                    if (confirm == JOptionPane.CLOSED_OPTION)
                        return;

                    dispose();

                }
                else
                    dispose();

            }
        });


        setSize(600, 600);
    }

    private void initializeLists(){
        allProductsList = new ArrayList<>();
        displayedProductsList = new DefaultListModel<>();
    }

    private JPanel initializeMainPanel(){
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        return mainPanel;
    }

    private JPanel createNewProductPanel(JPanel mainPanel){
        JPanel addProductPanel = new JPanel(new GridLayout(0, 2, 5, 5));
        addProductPanel.setBorder(BorderFactory.createTitledBorder("Add Product"));
        mainPanel.add(addProductPanel, BorderLayout.NORTH);
        return addProductPanel;
    }

    private void createProductsListPanel(JPanel mainPanel){

        JPanel produktyPanel = new JPanel(new BorderLayout());
        produktyPanel.setBorder(BorderFactory.createTitledBorder("List of products"));

        mainPanel.add(produktyPanel, BorderLayout.CENTER);

        listToSelectProduct = new JList<>(displayedProductsList);
        listToSelectProduct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        produktyPanel.add(new JScrollPane(listToSelectProduct), BorderLayout.CENTER);

    }

    private void createNameField(JPanel panel){
        JLabel productNameLabel = new JLabel("Name:");
        panel.add(productNameLabel);

        productNameField = new JTextField();
        panel.add(productNameField);
    }

    private void createAmountField(JPanel panel){
        JLabel amountLabel = new JLabel("Amount:");
        panel.add(amountLabel);

        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(amountPanel);

        amountField = new JTextField(5);
        amountPanel.add(amountField);

        unitsComboBox = new JComboBox<>(UNITS_LIST);
        amountPanel.add(unitsComboBox);
    }

    private void createCategoriesField(JPanel panel){
        JLabel categoriesLabel = new JLabel("Category:");
        panel.add(categoriesLabel);

        categoriesComboBox = new JComboBox<>(CATEGORIES_LIST);
        panel.add(categoriesComboBox);
    }

    private void createOperationsPanel(JPanel mainPanel){

        operationsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        operationsPanel.setBorder(BorderFactory.createTitledBorder("Available Operations"));
        mainPanel.add(operationsPanel, BorderLayout.SOUTH);

    }


    private void newChangesAppeared() { changesAreSaved = false; }
    private void changesHaveBeenSaved() { changesAreSaved = true; }


    private String getNewProductName(){
        String name = productNameField.getText();
        if (name.isEmpty()) {
            InfoBox.error("Enter product name!");
            return null;
        }

        return name;
    }

    private String getCategory(){
        String category = (String) categoriesComboBox.getSelectedItem();
        if (category == null){
            InfoBox.error("Choose category!");
            return null;
        }

        return category;
    }

    private String getUnit(){
        String unit = (String) unitsComboBox.getSelectedItem();
        if (unit == null || unit.isEmpty()){
            InfoBox.error("Choose unit!");
            return null;
        }

        return unit;
    }

    private String getAmount(String unit){

        int amountInt = 0;
        double amountDouble = 0;

        try {
            if (unit.equals("szt"))
                amountInt = Integer.parseInt(amountField.getText());
            else
                amountDouble = Double.parseDouble(amountField.getText());
        }
        catch (NumberFormatException ex) {
            InfoBox.error("This is not a correct value!");
            return null;
        }

        if (amountDouble + amountInt <= 0){
            InfoBox.error("Product amount is too small!");
            return null;
        }

        return amountInt != 0 ? String.valueOf(amountInt) : String.valueOf(amountDouble);
    }

    private void clearTextFields(){
        productNameField.setText("");
        amountField.setText("");
    }

    private void addNewProduct(Product product){
        allProductsList.add(product);
        displayedProductsList.addElement(product);
        newChangesAppeared();
    }


    private void handleConfirmButton(JButton confirmButton){

        confirmButton.addActionListener(e -> {

            String name = getNewProductName();
            if (name == null) return;

            String category = getCategory();
            if (category == null) return;

            String unit = getUnit();
            if (unit == null) return;

            String amount = getAmount(unit);
            if (amount == null) return;

            addNewProduct(new Product(name, amount, category, unit));
            clearTextFields();

        });

    }

    private void handleDeleteSelectedButton(JButton deleteSelectedButton){

        deleteSelectedButton.addActionListener(e -> {
            int selectedIndex = listToSelectProduct.getSelectedIndex();

            if (selectedIndex == -1) {
                InfoBox.error("Select a product!");
                return;
            }

            allProductsList.remove(selectedIndex);
            displayedProductsList.remove(selectedIndex);
            newChangesAppeared();
        });

    }

    private void handleDeleteCategoryButton(JButton deleteCategoryButton){

        deleteCategoryButton.addActionListener(e -> {

            String selectedCategory = (String) JOptionPane.showInputDialog(ListaZakupow.this, "Choose category you want to delete:", "Delete Category", JOptionPane.PLAIN_MESSAGE, null, CATEGORIES_LIST, CATEGORIES_LIST[0]);

            if (selectedCategory == null){
                InfoBox.error("Select a category!");
                return;
            }

            allProductsList.removeIf(produkt -> produkt.getCategory().equals(selectedCategory));
            displayedProductsList.clear();
            allProductsList.forEach(produkt -> displayedProductsList.addElement(produkt));
            newChangesAppeared();

        });

    }

    private void handleDeleteAllProductsButton(JButton deleteAllProductsButton){

        deleteAllProductsButton.addActionListener(e -> {
            allProductsList.clear();
            displayedProductsList.clear();
            newChangesAppeared();
        });

    }

    private void handleFilterButton(JButton filterButton){

        filterButton.addActionListener(e -> {

            String[] modifiedCategotyList = new String[CATEGORIES_LIST.length];

            System.arraycopy(CATEGORIES_LIST, 0, modifiedCategotyList, 0, CATEGORIES_LIST.length);
            modifiedCategotyList[0] = "Show All Products";

            String selectedCategory = (String) JOptionPane.showInputDialog(ListaZakupow.this, "Select category to display", "Category Filter", JOptionPane.PLAIN_MESSAGE, null, modifiedCategotyList, modifiedCategotyList[0]);

            if (selectedCategory != null) {

                displayedProductsList.clear();
                if (selectedCategory.equals("Show All Products")){
                    for(Product product : allProductsList){
                        displayedProductsList.addElement(product);
                    }
                }
                else
                    allProductsList.stream().filter(produkt -> produkt.getCategory().equals(selectedCategory)).forEach(produkt -> displayedProductsList.addElement(produkt));

            }
        });

    }

    private void handleSaveButton(JButton saveButton){

        saveButton.addActionListener(e -> {

            if (FileManager.saveFile(allProductsList)) {
                InfoBox.information("File saved successfully!");
                changesHaveBeenSaved();
            }

        });

    }

}


