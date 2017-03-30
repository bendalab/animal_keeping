package animalkeeping.util;

import animalkeeping.ui.AddDatabaseUserForm;
import animalkeeping.ui.SuperUserForm;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Created by huben on 22.02.17.
 */
public class SuperUserDialog extends Dialogs {
    static String[] output;

    public static Connection openConnection() {
        SuperUserForm suf = new SuperUserForm();
        output = new String[2];
        Dialog<Connection> dialog = new Dialog<>();
        dialog.setTitle("Please provide name and password of an admin user");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(suf);
        dialog.setWidth(400);
        suf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                Connection connection = suf.openDatabaseUserDialog(suf.getSuperUserName(), suf.getSuperUserPassword());
                if (connection!= null){
                    return connection;
                }
                else {
                    return null;
                }

            }
            return null;
        });

        Optional<Connection> result = dialog.showAndWait();
        return result.isPresent() ? result.get(): null;
    }
}
