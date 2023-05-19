import java.io.Serializable;

class Product implements Serializable {
    private String name;
    private String amount;
    private String category;
    private String unit;

    public Product(String name, String amount, String category, String unit) {
        this.name = name;
        this.amount = amount;
        this.category = category;
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public String toString() { return name + " - " + amount + " " + unit + " (" + category + ")"; }

    public String getStringLine(){ return name + ";" + amount + ";" + category + ";" + unit; }
}