package animalkeeping.util;

import javafx.scene.control.Alert;

public class Dialogs {

    public static void showInfo(String  info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(info);
        alert.show();
    }
}

