package animalkeeping.util;

import animalkeeping.model.*;
import animalkeeping.ui.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Pair;
import org.hibernate.Session;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class Dialogs {

    public static void showInfo(String info) {
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
        Dialog<List<Treatment>> dialog = new Dialog<>();

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

        Optional<List<Treatment>> result = dialog.showAndWait();
        if (result.isPresent()) {
            showInfo("Successfully created a batch treatment!");
        }
    }


    public static HousingType editHousingTypeDialog(HousingType type) {
        HousingTypeDialog htd = new HousingTypeDialog(type);
        Dialog<HousingType> dialog = new Dialog<>();
        dialog.setTitle("Housing type");
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
            return result.get();
        }
        return null;
    }


    public static HousingUnit editHousingUnitDialog(HousingUnit unit) {
        return editHousingUnitDialog(unit, unit.getParentUnit());
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
                return hud.persistHousingUnit();
            }
            return null;
        });
        Optional<HousingUnit> result = dialog.showAndWait();
        return result.orElse(null);
    }


    public static License editLicenseDialog(License l) {
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
            return null;
        }
        return result.get();
    }

    public static Quota editQuotaDialog(Quota q) {
        return editQuotaDialog(q, null);
    }

    public static Quota editQuotaDialog(License l) {
        return editQuotaDialog(null, l);
    }

    private static Quota editQuotaDialog(Quota q, License l) {
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
            return null;
        }
        return result.get();
    }


    public static Pair<Date, Date> getDateInterval() {
        Label startLabel = new Label("Start date:");
        DatePicker sdp = new DatePicker(LocalDate.now().minusYears(1));

        DatePicker edp = new DatePicker(LocalDate.now());
        Label endLabel = new Label("End date:");

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(column1, column2);
        edp.prefWidthProperty().bind(column2.maxWidthProperty());
        sdp.prefWidthProperty().bind(column2.maxWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);
        grid.add(startLabel, 0, 0);
        grid.add(sdp, 1, 0);
        grid.add(endLabel, 0, 1);
        grid.add(edp, 1, 1);

        Dialog<Pair<Date, Date>> dialog = new Dialog<>();
        dialog.setTitle("Specify a time interval ... ");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(grid);
        dialog.setWidth(100);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                Date start = DateTimeHelper.localDateToUtilDate(sdp.getValue());
                Date end = DateTimeHelper.localDateToUtilDate(edp.getValue());
                if (end.before(start)) {
                    Dialogs.showInfo("End date is before start date. Cancelled!");
                    return null;
                }
                Pair<Date, Date> interval = new Pair<>(start, end);
                return interval;
            }
            return null;
        });

        Optional<Pair<Date, Date>> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return null;
        }
        return result.get();
    }

    public static Pair<Date, Date> getDateTimeInterval(Date start, Date end) {
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
        LocalDate sd = start != null ? DateTimeHelper.toLocalDate(start) : LocalDate.now().minusYears(1);
        LocalDate ed = end != null ? DateTimeHelper.toLocalDate(end) : LocalDate.now();
        String st = start != null ? timeFormat.format(start) : timeFormat.format(new Date());
        String et = end != null ? timeFormat.format(end) : timeFormat.format(new Date());

        DatePicker sdp = new DatePicker(sd);
        DatePicker edp = new DatePicker(ed);
        TextField sdf = new TextField(st);
        TextField edf = new TextField(et);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100, 100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(column1, column2);
        edp.prefWidthProperty().bind(column2.maxWidthProperty());
        sdp.prefWidthProperty().bind(column2.maxWidthProperty());
        sdf.prefWidthProperty().bind(column2.prefWidthProperty());
        edf.prefWidthProperty().bind(column2.prefWidthProperty());

        grid.setVgap(5);
        grid.setHgap(2);

        grid.add(new Label("Start date"), 0, 0);
        grid.add(sdp, 1, 0);
        grid.add(new Label("Start time"), 0, 1);
        grid.add(sdf, 1, 1);

        grid.add(new Label("End date"), 0, 2);
        grid.add(edp, 1, 2);
        grid.add(new Label("End time"), 0, 3);
        grid.add(edf, 1, 3);

        Dialog<Pair<Date, Date>> dialog = new Dialog<>();
        dialog.setTitle("Specify a time interval ... ");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(grid);
        dialog.setWidth(100);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                Date startDate = DateTimeHelper.getDateTime(sdp.getValue(), sdf.getText());
                Date endDate = DateTimeHelper.getDateTime(edp.getValue(), edf.getText());
                if (endDate.before(startDate)) {
                    Dialogs.showInfo("End date is before start date. Cancelled!");
                    return null;
                }
                Pair<Date, Date> interval = new Pair<>(startDate, endDate);
                return interval;
            }
            return null;
        });

        Optional<Pair<Date, Date>> result = dialog.showAndWait();
        if (!result.isPresent()) {
            return null;
        }
        return result.get();
    }

    public static SubjectType editSubjectTypeDialog(SubjectType type) {
        SubjectTypeForm std = new SubjectTypeForm(type);
        Dialog<SubjectType> dialog = new Dialog<>();
        dialog.setTitle("Subject type");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(std);
        dialog.setWidth(200);
        std.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return std.persistSubjectType();
            }
            return null;
        });
        Optional<SubjectType> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Session session = Main.sessionFactory.openSession();
                session.beginTransaction();
                session.saveOrUpdate(result.get());
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result.get();
        }
        return null;
    }


    public static SpeciesType editSpeciesTypeDialog(SpeciesType type) {
        SpeciesTypeForm std = new SpeciesTypeForm(type);
        Dialog<SpeciesType> dialog = new Dialog<>();
        dialog.setTitle("Species type");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(std);
        dialog.setWidth(200);
        std.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return std.persistSpeciesType();
            }
            return null;
        });
        Optional<SpeciesType> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Session session = Main.sessionFactory.openSession();
                session.beginTransaction();
                session.saveOrUpdate(result.get());
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result.get();
        }
        return null;
    }


    public static SupplierType editSupplierTypeDialog(SupplierType type) {
        SupplierTypeForm std = new SupplierTypeForm(type);
        Dialog<SupplierType> dialog = new Dialog<>();
        dialog.setTitle("Supplier type");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(std);
        dialog.setWidth(200);
        std.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return std.persistSupplierType();
            }
            return null;
        });
        Optional<SupplierType> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Session session = Main.sessionFactory.openSession();
                session.beginTransaction();
                session.saveOrUpdate(result.get());
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result.get();
        }
        return null;
    }

    public static Person editPersonDialog(Person person) {
        PersonForm pf = new PersonForm(person);
        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Create/edit person...");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(pf);
        dialog.setWidth(300);
        pf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return pf.persistPerson();
            }
            return null;
        });
        Optional<Person> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Session session = Main.sessionFactory.openSession();
                session.beginTransaction();
                session.saveOrUpdate(result.get());
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result.get();
        }
        return null;
    }

    public static TreatmentType editTreatmentTypeDialog(TreatmentType type) {
        TreatmentTypeForm ttf = new TreatmentTypeForm(type);
        Dialog<TreatmentType> dialog = new Dialog<>();
        dialog.setTitle("Create/edit treatment type...");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(ttf);
        dialog.setWidth(300);
        ttf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return ttf.persistType();
            }
            return null;
        });
        Optional<TreatmentType> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public static Treatment editTreatmentDialog() {
        TreatmentForm tf = new TreatmentForm();
        return editTreatmentDialog(tf);
    }

    public static Treatment editTreatmentDialog(Treatment treatment) {
        TreatmentForm tf = new TreatmentForm(treatment);
        return editTreatmentDialog(tf);
    }


    public static Treatment editTreatmentDialog(Subject subject) {
        TreatmentForm tf = new TreatmentForm(subject);
        return editTreatmentDialog(tf);
    }


    public static Treatment editTreatmentDialog(TreatmentType type) {
        TreatmentForm tf = new TreatmentForm(type);
        return editTreatmentDialog(tf);
    }


    public static Treatment editTreatmentDialog(TreatmentForm form) {
        Dialog<Treatment> dialog = new Dialog<>();
        dialog.setTitle("Create/edit treatment ...");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(form);
        dialog.setWidth(300);
        form.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return form.persistTreatment();
            }
            return null;
        });
        Optional<Treatment> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                Session session = Main.sessionFactory.openSession();
                session.beginTransaction();
                session.saveOrUpdate(result.get());
                session.getTransaction().commit();
                session.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
            return result.get();
        }
        return null;
    }

    public static Subject editSubjectDialog(Subject s) {
        SubjectForm sf = new SubjectForm(s);

        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Add/edit subject ...");
        dialog.setHeight(200);
        dialog.setWidth(400);
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(sf);
        sf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return sf.persistSubject();
            }
            return null;
        });
        Optional<Subject> result = dialog.showAndWait();
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

}

