import javax.swing.*;
import java.awt.*;

public class InfoBox {

    private static Component component;

    public InfoBox(Component component){
        InfoBox.component = component;
    }

    public static void error(String message){
        JOptionPane.showMessageDialog(component, message, "ERROR", JOptionPane.ERROR_MESSAGE);
    }

    public static void information(String message){
        JOptionPane.showMessageDialog(component, message, "INFORMATION", JOptionPane.INFORMATION_MESSAGE);
    }

}
