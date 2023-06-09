import javax.swing.*;
import java.awt.*;
import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class ListaZakupow extends JFrame {

    private final String FRAME_NAME = "Shopping List";
    private final String[] UNITS_LIST = {"szt", "kg", "litr"};
    private final String[] CATEGORIES_LIST = {null, "Bread", "Meat", "Fruits", "Vegetables", "Sweets", "Drinks", "Chemicals", "Dairy"};

    private ArrayList<Product> allProductsList;
    private DefaultListModel<Product> displayedProductsList;
    JList<Product> listToSelectProduct;

    JTextField productNameField;
    JTextField amountField;
    JPanel operationsPanel;

    boolean changesAreSaved = true;

    private JComboBox<String> unitsComboBox;
    private JComboBox<String> categoriesComboBox;

    private ObjectOutputStream objectOutputStream;



    public ListaZakupow(ObjectOutputStream objectOutputStream) {

        this.objectOutputStream = objectOutputStream;

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

        JButton deleteAllProductsButton = createNewButton("Delete All Products");
        addButtonToPanel(operationsPanel, deleteAllProductsButton);
        handleDeleteAllProductsButton(deleteAllProductsButton);

        JButton filterButton = createNewButton("Filter By Category");
        addButtonToPanel(operationsPanel, filterButton);
        handleFilterButton(filterButton);

        JButton saveButton = createNewButton("Save Changes");
        addButtonToPanel(operationsPanel, saveButton);
        handleSaveButton(saveButton);

        operationsPanel.setBackground(Color.lightGray);

    }



    public void showList() {
        showProgramWindow();
    }

    public void closeList() {
        dispose();
    }

    public void emergencyClose() {
        InfoBox.error("Connection lost!\nApplication will be closed!");
        dispose();
    }


    public void fillList(ArrayList<Product> list) {

        if (list == null)
            return;

        for (Product product : list)
            addNewProduct(product, false);
    }


    private void showProgramWindow() {
        setVisible(true);
    }

    private JButton createNewButton(String name) {

        JButton button = new JButton(name);
        button.setBackground(Color.DARK_GRAY);
        button.setFont(new Font("Italic", Font.BOLD, 15));
        button.setForeground(Color.white);

        return button;
    }

    private void initializeErrorManager() {
        new InfoBox(ListaZakupow.this);
    }

    private void addButtonToPanel(JPanel panel, JButton button) {
        panel.add(button);
    }


    private void sendList(ArrayList<Product> list) throws InterruptedException {

        try {
            Client.sendListToServer(objectOutputStream, list);
        } catch (IOException ex) {

            if (list == null){
                closeList();
                return;
            }

            InfoBox.error("Server not responding!\nApplication will be closed!");
            closeList();
        }
    }

    private void setFrameBasicFeatures() {
        setTitle(FRAME_NAME);


        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {

                try {
                    if (!changesAreSaved) {

                        int confirm = JOptionPane.showConfirmDialog(ListaZakupow.this,
                                "There are unsaved changes!\nDo you want do save it?",
                                "WARNING",
                                JOptionPane.YES_NO_OPTION);

                        switch (confirm) {
                            case JOptionPane.YES_OPTION -> sendList(allProductsList);
                            case JOptionPane.NO_OPTION -> sendList(null);
                            case JOptionPane.CLOSED_OPTION -> {
                                return;
                            }
                        }

                        closeList();

                    } else {
                        sendList(allProductsList);
                        closeList();
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                    closeList();
                }

            }
        });


        setSize(400, 600);
    }


    private void initializeLists() {
        allProductsList = new ArrayList<>();
        displayedProductsList = new DefaultListModel<>();
    }

    private JPanel initializeMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        add(mainPanel);
        return mainPanel;
    }

    private JPanel createNewProductPanel(JPanel mainPanel) {
        JPanel addProductPanel = new JPanel(new GridLayout(0, 2, 3, 5));
        addProductPanel.setBackground(Color.LIGHT_GRAY);
        addProductPanel.setBorder(BorderFactory.createTitledBorder("Add Product"));
        mainPanel.add(addProductPanel, BorderLayout.NORTH);
        return addProductPanel;
    }

    private void createProductsListPanel(JPanel mainPanel) {

        JPanel produktyPanel = new JPanel(new BorderLayout());
        produktyPanel.setBackground(Color.LIGHT_GRAY);
        produktyPanel.setBorder(BorderFactory.createTitledBorder("List of products"));

        mainPanel.add(produktyPanel, BorderLayout.CENTER);

        listToSelectProduct = new JList<>(displayedProductsList);
        listToSelectProduct.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listToSelectProduct.setBackground(Color.WHITE);
        listToSelectProduct.setForeground(Color.BLACK);
        produktyPanel.add(new JScrollPane(listToSelectProduct), BorderLayout.CENTER);

    }

    private void createNameField(JPanel panel) {
        JLabel productNameLabel = new JLabel("Name:");
        productNameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(productNameLabel);

        productNameField = new JTextField();
        productNameField.setFont(new Font("Airal", Font.PLAIN, 15));
        panel.add(productNameField);
    }

    private void createAmountField(JPanel panel) {
        JLabel amountLabel = new JLabel("Amount:");
        amountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(amountLabel);

        JPanel amountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        amountPanel.setBackground(Color.LIGHT_GRAY);
        panel.add(amountPanel);

        amountField = new JTextField(5);
        amountField.setPreferredSize(new Dimension(20,28));
        amountField.setFont(new Font("Arial", Font.PLAIN, 15));
        amountPanel.add(amountField);

        unitsComboBox = new JComboBox<>(UNITS_LIST);
        unitsComboBox.setFont(new Font("Arial", Font.BOLD, 14));
        amountPanel.add(unitsComboBox);
    }

    private void createCategoriesField(JPanel panel) {
        JLabel categoriesLabel = new JLabel("Category:");
        categoriesLabel.setFont(new Font("Arial", Font.BOLD, 14));
        panel.add(categoriesLabel);

        categoriesComboBox = new JComboBox<>(CATEGORIES_LIST);
        categoriesComboBox.setFont(new Font("Arial", Font.PLAIN, 15));
        panel.add(categoriesComboBox);
    }

    private void createOperationsPanel(JPanel mainPanel) {

        operationsPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        operationsPanel.setBorder(BorderFactory.createTitledBorder("Available Operations"));
        mainPanel.add(operationsPanel, BorderLayout.SOUTH);

    }


    private void newChangesAppeared() {
        changesAreSaved = false;
    }

    private void changesHaveBeenSaved() {
        changesAreSaved = true;
    }


    private String getNewProductName() {
        String name = productNameField.getText();
        if (name.isEmpty()) {
            InfoBox.error("Enter product name!");
            return null;
        }

        return name;
    }

    private String getCategory() {
        String category = (String) categoriesComboBox.getSelectedItem();
        if (category == null) {
            InfoBox.error("Choose category!");
            return null;
        }

        return category;
    }

    private String getUnit() {
        String unit = (String) unitsComboBox.getSelectedItem();
        if (unit == null || unit.isEmpty()) {
            InfoBox.error("Choose unit!");
            return null;
        }

        return unit;
    }

    private String getAmount(String unit) {

        int amountInt = 0;
        double amountDouble = 0;

        try {
            if (unit.equals("szt"))
                amountInt = Integer.parseInt(amountField.getText());
            else
                amountDouble = Double.parseDouble(amountField.getText());
        } catch (NumberFormatException ex) {
            InfoBox.error("This is not a correct value!");
            return null;
        }

        if (amountDouble + amountInt <= 0) {
            InfoBox.error("Product amount is too small!");
            return null;
        }

        return amountInt != 0 ? String.valueOf(amountInt) : String.valueOf(amountDouble);
    }

    private void clearTextFields() {
        productNameField.setText("");
        amountField.setText("");
        categoriesComboBox.setSelectedIndex(0);
    }

    private void addNewProduct(Product product, boolean signalizeNewChanges) {
        allProductsList.add(product);
        displayedProductsList.addElement(product);

        if (signalizeNewChanges)
            newChangesAppeared();
    }


    private void handleConfirmButton(JButton confirmButton) {

        confirmButton.addActionListener(e -> {

            String name = getNewProductName();
            if (name == null) return;

            String category = getCategory();
            if (category == null) return;

            String unit = getUnit();
            if (unit == null) return;

            String amount = getAmount(unit);
            if (amount == null) return;

            addNewProduct(new Product(name, amount, category, unit), true);
            clearTextFields();

        });

    }

    private void handleDeleteSelectedButton(JButton deleteSelectedButton) {

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

    private void handleDeleteCategoryButton(JButton deleteCategoryButton) {

        deleteCategoryButton.addActionListener(e -> {

            String selectedCategory = (String) JOptionPane.showInputDialog(ListaZakupow.this, "Choose category you want to delete:", "Delete Category", JOptionPane.PLAIN_MESSAGE, null, CATEGORIES_LIST, CATEGORIES_LIST[0]);

            if (selectedCategory == null) {
                InfoBox.error("Select a category!");
                return;
            }

            allProductsList.removeIf(produkt -> produkt.getCategory().equals(selectedCategory));
            displayedProductsList.clear();
            allProductsList.forEach(produkt -> displayedProductsList.addElement(produkt));
            newChangesAppeared();

        });

    }

    private void handleDeleteAllProductsButton(JButton deleteAllProductsButton) {

        deleteAllProductsButton.addActionListener(e -> {
            allProductsList.clear();
            displayedProductsList.clear();
            newChangesAppeared();
        });

    }

    private void handleFilterButton(JButton filterButton) {

        filterButton.addActionListener(e -> {

            String[] modifiedCategotyList = new String[CATEGORIES_LIST.length];

            System.arraycopy(CATEGORIES_LIST, 0, modifiedCategotyList, 0, CATEGORIES_LIST.length);
            modifiedCategotyList[0] = "Show All Products";

            String selectedCategory = (String) JOptionPane.showInputDialog(ListaZakupow.this, "Select category to display", "Category Filter", JOptionPane.PLAIN_MESSAGE, null, modifiedCategotyList, modifiedCategotyList[0]);

            if (selectedCategory != null) {

                displayedProductsList.clear();
                if (selectedCategory.equals("Show All Products")) {
                    for (Product product : allProductsList) {
                        displayedProductsList.addElement(product);
                    }
                } else
                    allProductsList.stream().filter(produkt -> produkt.getCategory().equals(selectedCategory)).forEach(produkt -> displayedProductsList.addElement(produkt));

            }
        });

    }

    private void handleSaveButton(JButton saveButton) {

        saveButton.addActionListener(e -> {

            InfoBox.information("File saved successfully!");
            changesHaveBeenSaved();

        });

    }

}


