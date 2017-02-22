package animalkeeping.util;

import animalkeeping.model.HousingType;
import animalkeeping.model.HousingUnit;
import animalkeeping.model.License;
import animalkeeping.model.Quota;
import animalkeeping.ui.*;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import org.hibernate.Session;

import java.util.Optional;

public class Dialogs {

    public static void showInfo(String  info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(info);
        alert.show();
    }


    public static void importSubjectsDialog(HousingUnit unit) {
        AddSubjectsForm htd = new AddSubjectsForm(unit);
        Dialog<Boolean> dialog = new Dialog<>();
        dialog.setTitle("Import subjects");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(htd);
        dialog.setWidth(300);
        htd.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return htd.persistSubjects();
            }
            return null;
        });

        Optional<Boolean> result = dialog.showAndWait();
        if (!result.isPresent() && !result.get()) {
            showInfo("Something went wrong while creating new subjects!");
        } else {
            showInfo("Successfully created new subjects!");
        }
    }


    public static void batchTreatmentDialog(HousingUnit unit) {
        BatchTreatmentForm btf = new BatchTreatmentForm(unit);
        Dialog<Boolean> dialog = new Dialog<>();

        dialog.setTitle("Batch Treatment");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(btf);
        dialog.setWidth(300);
        btf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return btf.persist();
            }
            return null;
        });

        Optional<Boolean> result = dialog.showAndWait();
        if (!result.isPresent() && !result.get()) {
            showInfo("Something went wrong while creating the treatment!");
        } else {
            showInfo("Successfully created a batch treatment!");
        }
    }


    public static void editHousingTypeDialog(HousingType type) {
        HousingTypeDialog htd = new HousingTypeDialog(type);
        Dialog<HousingType> dialog = new Dialog<>();
        dialog.setTitle("Housing type");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(htd);
        dialog.setWidth(200);
        htd.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return htd.getHousingType();
            }
            return null;
        });
        Optional<HousingType> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Session session = Main.sessionFactory.openSession();
                session.beginTransaction();
                session.saveOrUpdate(result.get());
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static HousingUnit editHousingUnitDialog(HousingUnit unit) {
        return editHousingUnitDialog(unit, null);
    }


    public static HousingUnit editHousingUnitDialog(HousingUnit unit, HousingUnit parent) {
        HousingUnitDialog hud = new HousingUnitDialog(unit);
        hud.setParentUnit(parent);
        Dialog<HousingUnit> dialog = new Dialog<>();
        dialog.setTitle("Housing unit");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(hud);
        hud.prefWidthProperty().bind(dialog.widthProperty());
        dialog.setWidth(200);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return hud.getHousingUnit();
            }
            return null;
        });
        Optional<HousingUnit> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Session session = Main.sessionFactory.openSession();
                session.beginTransaction();
                session.saveOrUpdate(result.get());
                session.getTransaction().commit();
                session.close();
                return result.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    public static void editLicenseDialog(License l) {
        LicenseForm lf = new LicenseForm(l);
        Dialog<License> dialog = new Dialog<>();
        dialog.setTitle("Add/Edit licence... ");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(lf);
        dialog.setWidth(200);
        lf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return lf.persistLicense();
            }
            return null;
        });

        Optional<License> result = dialog.showAndWait();
        if (!result.isPresent()) {
            showInfo("Something went wrong while creating the License!");
        }
    }

    public static void editQuotaDialog(Quota q) {
        editQuotaDialog(q, null);
    }

    public static void editQuotaDialog(License l) {
        editQuotaDialog(null, l);
    }

    private static void editQuotaDialog(Quota q, License l) {
        QuotaForm qf;
        if (q != null)
            qf = new QuotaForm(q);
        else if (l != null)
            qf = new QuotaForm(l);
        else
            qf = new QuotaForm();
        Dialog<Quota> dialog = new Dialog<>();
        dialog.setTitle("Add/Edit quota ... ");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(qf);
        dialog.setWidth(200);
        qf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return qf.persistQuota();
            }
            return null;
        });

        Optional<Quota> result = dialog.showAndWait();
        if (!result.isPresent()) {
            showInfo("Something went wrong while creating the quota!");
        }
    }
}

