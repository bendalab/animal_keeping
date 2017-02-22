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

    public static void openConnection() {
        SuperUserForm suf = new SuperUserForm();
        output = new String[2];
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Please input username and password of user with create user privilege");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(suf);
        dialog.setWidth(300);
        suf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                Connection connection = suf.openDatabaseUserDialog(suf.getSuperUserName(), suf.getSuperUserPassword());
                AddDatabaseUserDialog.addDatabaseUser(connection);
                return true;
            }
            return false;
        });

        Optional<Boolean> result = dialog.showAndWait();
        if (!result.isPresent() && !result.get()) {
            showInfo("Something went wrong while adding a new user to the database!");
        } else {
            showInfo("Successfully added user to database!");
        }
    }
}
