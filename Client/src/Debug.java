public class Debug {

    private boolean isActive;

    public Debug(boolean active) {
        this.isActive = active;
    }

    public void message(String message) {

        if (!isActive)
            return;

        System.out.print(Color.CYAN);
        System.out.println("(Debug) " + message);
        System.out.print(Color.RESET);
    }

}
