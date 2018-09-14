package animalkeeping.util;

import animalkeeping.model.*;
import animalkeeping.ui.forms.*;
import animalkeeping.ui.widgets.HousingDropDown;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.util.Callback;
import javafx.util.Pair;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Vector;


public class Dialogs {

    public static void showInfo(String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(info);
        alert.show();
    }

    public static void showErrorMessages(String title, Vector<String> messages) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ValidationError");
        alert.setHeaderText(title);
        StringBuilder messageText = new StringBuilder();
        int count = 1;
        for (String m : messages) {
            messageText.append(count).append(") ").append(m).append("\n\n");
            count++;
        }
        alert.setContentText(messageText.toString());
        alert.show();
    }

    public static void importSubjectsDialog(HousingUnit unit) {
        AddSubjectsForm htd = new AddSubjectsForm(unit);
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Import subjects");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(htd);
        dialog.setWidth(400);
        htd.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!htd.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return htd.persistSubjects();
            } else if (b == buttonTypeCancel) {
                return 1;
            }
            return null;
        });

        Optional<Integer> result = dialog.showAndWait();
        /*
        if (!result.isPresent() || result.get() < 0) {
            showInfo("Something went wrong while creating new subjects!");
        } else if (result.get() == 0) {
            showInfo("Successfully created subjects.");
        }
        */
    }

    public static void batchTreatmentDialog(HousingUnit unit) {
        BatchTreatmentForm btf = new BatchTreatmentForm(unit);
        Dialog<Boolean> dialog = new Dialog<>();

        dialog.setTitle("Batch Treatment");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(btf);
        dialog.setWidth(400);
        btf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!btf.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return btf.persist();
            }
            return null;
        });

        Optional<Boolean> result = dialog.showAndWait();
    }

    public static HousingType editHousingTypeDialog(HousingType type) {
        HousingTypeForm htd = new HousingTypeForm(type);
        Dialog<HousingType> dialog = new Dialog<>();
        dialog.setTitle("Housing type");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(htd);
        dialog.setWidth(350);
        htd.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!htd.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return htd.persist();
            }
            return null;
        });
        Optional<HousingType> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static HousingUnit editHousingUnitDialog(HousingUnit unit) {
        return editHousingUnitDialog(unit, unit != null ? unit.getParentUnit() : null);
    }

    public static HousingUnit editHousingUnitDialog(HousingUnit unit, HousingUnit parent) {
        HousingUnitForm hud = new HousingUnitForm(unit);
        if (unit == null && parent != null) {
            hud.setParentUnit(parent);
        }
        Dialog<HousingUnit> dialog = new Dialog<>();
        dialog.setTitle("Housing unit");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(hud);
        hud.prefWidthProperty().bind(dialog.widthProperty());
        dialog.setWidth(400);
        dialog.setHeight(500);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!hud.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
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
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(lf);
        dialog.setWidth(400);
        lf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!lf.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return lf.persistLicense();
            }
            return null;
        });

        Optional<License> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static Quota editQuotaDialog(Quota q) {
        return editQuotaDialog(q,  q.getLicense());
    }

    public static Quota editQuotaDialog(License l) {
        return editQuotaDialog(null, l);
    }

    public static Quota editQuotaDialog(Quota q, License l) {
        QuotaForm qf;
        if (q != null)
            qf = new QuotaForm(q);
        else if (l != null)
            qf = new QuotaForm(l);
        else
            qf = new QuotaForm();
        Dialog<Quota> dialog = new Dialog<>();
        dialog.setTitle("Add/Edit quota ... ");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(qf);
        dialog.setWidth(400);
        qf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!qf.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return qf.persistQuota();
            }
            return null;
        });

        Optional<Quota> result = dialog.showAndWait();
        return result.orElse(null);
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
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(grid);
        dialog.setWidth(300);

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
        return result.orElse(null);
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
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(grid);
        dialog.setWidth(300);

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
        return result.orElse(null);
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
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!std.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return std.persistSubjectType();
            }
            return null;
        });
        Optional<SubjectType> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static SpeciesType editSpeciesTypeDialog(SpeciesType type) {
        SpeciesTypeForm std = new SpeciesTypeForm(type);
        Dialog<SpeciesType> dialog = new Dialog<>();
        dialog.setTitle("Species type");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(std);
        dialog.setWidth(350);
        std.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!std.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return std.persistSpeciesType();
            }
            return null;
        });
        Optional<SpeciesType> result = dialog.showAndWait();
        return result.orElse(null);
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
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!std.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return std.persistSupplierType();
            }
            return null;
        });
        Optional<SupplierType> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static Person editPersonDialog(Person person) {
        PersonForm pf = new PersonForm(person);
        Dialog<Person> dialog = new Dialog<>();
        dialog.setTitle("Create/edit person...");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(pf);
        dialog.setWidth(400);
        pf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!pf.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return pf.persistPerson();
            }
            return null;
        });
        Optional<Person> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static TreatmentType editTreatmentTypeDialog(TreatmentType type) {
        TreatmentTypeForm ttf;
        if (type == null) {
            System.out.println("type is null!!!");
            ttf = new TreatmentTypeForm();
        }
        else
            ttf = new TreatmentTypeForm(type);
        Dialog<TreatmentType> dialog = new Dialog<>();
        dialog.setTitle("Create/edit treatment type...");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(ttf);
        dialog.setWidth(350);
        ttf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        TreatmentTypeForm finalTtf = ttf;
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!ttf.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return finalTtf.persistType();
            }
            return null;
        });
        Optional<TreatmentType> result = dialog.showAndWait();
        return result.orElse(null);
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
        TreatmentForm tf;
        if (type == null)
            tf = new TreatmentForm();
        else
            tf = new TreatmentForm(type);
        return editTreatmentDialog(tf);
    }

    public static Treatment editTreatmentDialog(TreatmentForm form) {
        Dialog<Treatment> dialog = new Dialog<>();
        dialog.setTitle("Create/edit treatment ...");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(form);
        dialog.setWidth(400);
        form.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!form.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return form.persistTreatment();
            }
            return null;
        });
        Optional<Treatment> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static Subject editSubjectDialog(Subject s) {
        SubjectForm sf = new SubjectForm(s);

        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Add/edit subject ...");
        dialog.setHeight(300);
        dialog.setWidth(400);
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(sf);
        sf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!sf.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return sf.persistSubject();
            }
            return null;
        });
        Optional<Subject> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static SubjectNote editSubjectNoteDialog(SubjectNote note, Subject subject) {
        SubjectNoteForm snf = new SubjectNoteForm(note, subject);
        return editSubjectNoteDialog(snf);
    }

    public static SubjectNote editSubjectNoteDialog(Subject subject) {
        SubjectNoteForm snf = new SubjectNoteForm(subject);
        return editSubjectNoteDialog(snf);
    }

    public static SubjectNote editSubjectNoteDialog(SubjectNoteForm snf) {
        if (snf == null) {
            return null;
        }
        Dialog<SubjectNote> dialog = new Dialog<>();
        dialog.setTitle("Add/edit note ...");
        dialog.setHeight(400);
        dialog.setWidth(400);
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(snf);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);

        dialog.setResultConverter(new Callback<ButtonType, SubjectNote>() {
            @Override
            public SubjectNote call(ButtonType b) {
                if (b == buttonTypeOk) {
                    return snf.persist();
                }
                return null;
            }
        });
        Optional<SubjectNote> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static  Housing editHousing(Housing housing) {
        HousingForm form = new HousingForm(housing);

        Dialog<Housing> dialog = new Dialog<>();
        dialog.setTitle("Create/edit housing ...");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(form);
        dialog.setWidth(400);
        form.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!form.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return form.persistHousing();
            }
            return null;
        });
        Optional<Housing> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static Subject reportSubjectDead(Subject s) {
        ExportSubjectForm esf = new ExportSubjectForm(s);

        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Remove subject from stock ...");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(esf);
        dialog.setWidth(300);
        esf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!esf.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return esf.persist();
            }
            return null;
        });
        Optional<Subject> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static Subject relocateSubjectDialog(Subject s) {
        RelocateSubjectForm rsf = new RelocateSubjectForm(s);

        Dialog<Subject> dialog = new Dialog<>();
        dialog.setTitle("Relocate subject ...");
        dialog.setResizable(false);
        dialog.getDialogPane().setContent(rsf);
        dialog.setWidth(300);
        rsf.prefWidthProperty().bind(dialog.widthProperty());

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            Vector<String> messages = new Vector<>();
            if (!rsf.validate(messages)) {
                Dialogs.showErrorMessages("Input not valid.", messages);
                event.consume();
            }
        });
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return rsf.persist();
            }
            return null;
        });
        Optional<Subject> result = dialog.showAndWait();
        return result.orElse(null);
    }

    public static void showAboutDialog( ) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setResizable(false);
        dialog.setTitle("About animalBase");
        About about = new About();
        dialog.getDialogPane().setContent(about);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.show();
    }

    public static HousingUnit selectHousingUnit() {
        GridPane grid = new GridPane();
        HousingDropDown hdd = new HousingDropDown();

        grid.add(hdd, 0, 0);

        Dialog<HousingUnit> dialog = new Dialog<>();
        dialog.setTitle("Select a housing unit ... ");
        dialog.setResizable(true);
        dialog.getDialogPane().setContent(grid);
        dialog.setWidth(100);

        ButtonType buttonTypeOk = new ButtonType("ok", ButtonBar.ButtonData.OK_DONE);
        ButtonType buttonTypeCancel = new ButtonType("cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        dialog.setResultConverter(b -> {
            if (b == buttonTypeOk) {
                return hdd.getHousingUnit();
            }
            return null;
        });

        Optional<HousingUnit> result = dialog.showAndWait();
        return result.orElse(null);
    }
}


