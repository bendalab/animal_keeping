package animalkeeping.ui.controller;

import animalkeeping.model.*;
import animalkeeping.ui.*;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by jan on 14.01.17.
 */
public class InventoryController extends VBox implements Initializable, View {
    @FXML private PieChart populationChart;
    @FXML private VBox unitsBox;
    @FXML private ListView<String> unitsList;
    @FXML private VBox chartVbox;
    @FXML private VBox currentHousingsBox;
    @FXML private ScrollPane tableScrollPane;
    private HousingTable housingTable;
    private TreatmentsTable treatmentsTable;
    private VBox controls;
    private HashMap<String, HousingUnit> unitsHashMap;
    //private ControlLabel allLabel;
    private ControlLabel endTreatmentLabel;


    public InventoryController() {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/animalkeeping/ui/fxml/Inventory.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //unitsList = new ListView<String>();
        unitsList.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        unitsList.getItems().add("all");
        unitsList.getSelectionModel().getSelectedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> c) {
                if (c.getList().size() > 0) {
                    listPopulation(c.getList().get(0));
                }
            }
        });

        unitsHashMap = new HashMap<>();

        housingTable = new HousingTable();
        treatmentsTable = new TreatmentsTable(true);
        treatmentsTable.getSelectionModel().getSelectedItems().addListener((ListChangeListener<Treatment>) c -> {
            if (c.getList().size() > 0) {
                treatmentSelected(c.getList().get(0));
            }
        });
        currentHousingsBox.getChildren().add(housingTable);
        tableScrollPane.setContent(treatmentsTable);

        controls = new VBox();
        ControlLabel animalUseLabel = new ControlLabel("export animal use", false);
        animalUseLabel.setTooltip(new Tooltip("export excel sheet containing the animal use per license"));
        animalUseLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)) {
                exportAnimalUse();
            }
        });
        ControlLabel exportStock = new ControlLabel("export stock list", false);
        exportStock.setTooltip(new Tooltip("Export current stock list to excel sheet."));
        exportStock.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                exportStockList();
            }
        });

        endTreatmentLabel  = new ControlLabel("end treatment", true);
        endTreatmentLabel.setTooltip(new Tooltip("end an open treatment"));
        endTreatmentLabel.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                endTreatment();
            }
        });
        controls.getChildren().add(animalUseLabel);
        controls.getChildren().add(exportStock);
        controls.getChildren().add(new Separator(Orientation.HORIZONTAL));
        controls.getChildren().add(endTreatmentLabel);
        //refresh();
    }

    private void fillList() {
        unitsList.getItems().clear();
        unitsList.getItems().add("all");
        List<HousingUnit> result = EntityHelper.getEntityList("from HousingUnit where parent_unit_id is NULL", HousingUnit.class);
        if (result != null) {
            for (HousingUnit h : result) {
                unitsHashMap.put(h.getName(), h);
                unitsList.getItems().add(h.getName());
              }
        }
    }

    private void refreshOpenTreatments() {
        treatmentsTable.getSelectionModel().select(null);
        List<Treatment> treatments = EntityHelper.getEntityList("from Treatment where end_datetime is NULL", Treatment.class);
        treatmentsTable.setTreatments(treatments);
    }

    @FXML
    private void listAllPopulation() {
        List<SpeciesType> result = EntityHelper.getEntityList("from SpeciesType", SpeciesType.class);
        List<Housing> housings = EntityHelper.getEntityList("from Housing where end_datetime is null", Housing.class);
        Integer count = 0;
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        if (result != null) {
            for (SpeciesType st : result) {
                count += st.getCount();
                pieChartData.add(new PieChart.Data(st.getName() + " (" + st.getCount() + ")", st.getCount()));
            }
        }
        populationChart.setTitle("Total population: " + count.toString());
        populationChart.setData(pieChartData);
        housingTable.setSubject(null);
    }


    private void listPopulation(String unitName) {
        if (unitName.toLowerCase().equals("all")) {
            listAllPopulation();
            return;
        }
        HousingUnit housingUnit = unitsHashMap.get(unitName);
        Set<Housing> housings = housingUnit.getAllHousings(true);
        Set<Subject> subjects = new HashSet<>();
        collectSubjects(subjects, housingUnit, true);

        HashMap<String, Integer> counts = new HashMap<>();
        for (Subject s : subjects) {
            if (counts.containsKey(s.getSpeciesType().getName())) {
                Integer c = counts.get(s.getSpeciesType().getName());
                c += 1;
                counts.put(s.getSpeciesType().getName(), c);
            } else {
                counts.put(s.getSpeciesType().getName(), 1);
            }
        }

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (String st : counts.keySet()) {
            pieChartData.add(new PieChart.Data(st + " (" + counts.get(st) + ")", counts.get(st)));
        }

        populationChart.setTitle(housingUnit.getName() + ": " + subjects.size());
        populationChart.setData(pieChartData);
        housingTable.setHousingUnit(housingUnit);
    }

    @Override
    public  VBox getControls() {
        return controls;
    }

    @Override
    public void refresh() {
        fillList();
        refreshOpenTreatments();
        listAllPopulation();
    }

    private void collectSubjects(Set<Subject> subjects, HousingUnit h) {
        collectSubjects(subjects, h, true);
    }

    private void collectSubjects(Set<Subject> subjects, HousingUnit h, Boolean currentOnly) {
        for (Housing housing : h.getAllHousings(true)) {
            subjects.add(housing.getSubject());

        }
    }


    private void exportStockList() {
        Pair<Date, Date> interval = Dialogs.getDateInterval();
        if (interval == null) {
            return;
        }

        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFRow row;
        XSSFSheet overviewsheet = workbook.createSheet("Stock overview");
        Map < String, Object[] > overviewlist = new TreeMap <>();
        overviewlist.put("1", new Object[]{"Species", "Count"});
        List<SpeciesType> types = EntityHelper.getEntityList("from SpeciesType", SpeciesType.class);
        List<Housing> currentHousings = EntityHelper.getEntityList("from Housing where end_datetime is NULL", Housing.class);
        row = overviewsheet.createRow(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        row.createCell(0).setCellValue( "Date:");
        row.createCell(1).setCellValue( sdf.format(new Date()));
        int rowid = 1;
        row = overviewsheet.createRow(rowid++);
        row.createCell(0).setCellValue("Species");
        row.createCell(1).setCellValue("Count");

        for (SpeciesType t : types) {
            row = overviewsheet.createRow(rowid++);
            Object [] objectArr = {t.getName(), t.getCount().toString()};
            int cellid = 0;
            for (Object obj : objectArr) {
                row.createCell(cellid++).setCellValue((String)obj);
            }
        }
        row = overviewsheet.createRow(rowid++);
        row.createCell(0).setCellValue("Total: ");
        row.createCell(1).setCellValue(currentHousings.size());

        // write a detailed stocklist
        try {
            Session session = Main.sessionFactory.openSession();
            String q = "From Housing where end_datetime is NULL OR " +
                    "(end_datetime is not Null AND end_datetime < :end AND end_datetime > :start AND subject not in " +
                    "(select distinct subject from Housing where end_datetime is NULL))";
            Query query = session.createQuery(q, Housing.class);
            query.setParameter("start", interval.getKey());
            query.setParameter("end", interval.getValue());
            session.beginTransaction();
            currentHousings = query.list();
            session.getTransaction().commit();
            session.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
        ObservableList<Housing > masterList = FXCollections.observableArrayList();
        masterList.addAll(currentHousings);
        SortedList<Housing> sortedList = masterList.sorted(Comparator.comparing(o -> o.getSubject().getSpeciesType().getName()));
        XSSFSheet stocklist = workbook.createSheet("Stock list");
        rowid = 0;
        row = stocklist.createRow(rowid++);
        row.createCell(0).setCellValue( "Date:");
        row.createCell(1).setCellValue( "from " + sdf.format(interval.getKey()) + " until " + sdf.format(interval.getValue()));

        row = stocklist.createRow(rowid++);
        row.createCell(0).setCellValue("Species");
        row.createCell(1).setCellValue("SubjectID");
        row.createCell(2).setCellValue("Origin");
        row.createCell(3).setCellValue("Entry");
        row.createCell(4).setCellValue("Exit");
        row.createCell(5).setCellValue("Reason");
        row.createCell(6).setCellValue("Housing unit");
        row.createCell(7).setCellValue("Comment");

        for (Housing h : sortedList) {
            row = stocklist.createRow(rowid++);
            String reason = "";
            if (h.getEnd() != null) {
                ArrayList<Treatment> ts = new ArrayList<>(h.getSubject().getTreatments());
                Treatment last = null;
                if (ts.size() > 0) {
                    last = ts.get(ts.size() - 1);
                }
                if (last != null && last.getEnd() != null) {
                    String treatment_day = df.format(last.getEnd());
                    String housing_end = df.format(h.getEnd());
                    reason = treatment_day.equals(housing_end) ? "used in experiment" : " ";
                }
            }
            Object [] objectArr = {h.getSubject().getSpeciesType().getName(), h.getSubject().getName(),
                                   h.getSubject().getSupplier().getName(), sdf.format(h.getStart()),
                                   h.getEnd() != null ? sdf.format(h.getEnd()) : "", reason,
                                   h.getHousing().getName(), h.getComment()};
            int cellid = 0;
            for (Object obj : objectArr) {
                row.createCell(cellid++).setCellValue((String)obj);
            }
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select output file");
        //chooser.setSelectedExtensionFilter(new FileChooser.ExtensionFilter("xlsx"));
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel doc(*.xlsx)", "*.xlsx"));
        chooser.setInitialFileName("*.xlsx");
        File f = chooser.showSaveDialog(Main.getPrimaryStage());
        if (f != null) {
            //Write the workbook in file system
            try {
                FileOutputStream out = new FileOutputStream(f);
                workbook.write(out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private List<License> getLicenseList(Date start, Date end) {
        String q = "From License where start_date < :end AND end_date > :start";
        try {
            Session session = Main.sessionFactory.openSession();
            Query query = session.createQuery(q, License.class);
            query.setParameter("start", start);
            query.setParameter("end", end);
            session.beginTransaction();
            List<License> l = query.list();
            session.getTransaction().commit();
            session.close();
            return l;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void exportAnimalUse() {
        Pair<Date, Date> interval = Dialogs.getDateInterval();
        if (interval == null) {
            return;
        }
        XSSFWorkbook workbook = new XSSFWorkbook();
        List<License> licenses = getLicenseList(interval.getKey(), interval.getValue());
        for (License l : licenses) {
            String s = l.getNumber();
            String sheetName = l.getName();
            XSSFSheet sheet = workbook.createSheet(sheetName);
            exportLicense(l, sheet, interval.getKey(), interval.getValue());
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select output file");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel spreadsheet (*.xlsx)", "*.xlsx"));
        chooser.setInitialFileName("*.xlsx");
        File f = chooser.showSaveDialog(Main.getPrimaryStage());
        if (f != null) {
            try {
                FileOutputStream out = new FileOutputStream(f);
                workbook.write(out);
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void exportLicense(License l, XSSFSheet sheet, Date start, Date end) {
        int rowid = 0;
        XSSFRow row = sheet.createRow(rowid++);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        row.createCell(0).setCellValue("License:");
        row.createCell(1).setCellValue(l.getName());
        row.createCell(2).setCellValue("file number:");
        row.createCell(3).setCellValue(l.getNumber());
        row = sheet.createRow(rowid++);
        row.createCell(0).setCellValue("Interval start:");
        row.createCell(1).setCellValue(df.format(start));
        row.createCell(2).setCellValue("end:");
        row.createCell(3).setCellValue(df.format(end));
        rowid++;
        row = sheet.createRow(rowid++);
        row.createCell(0).setCellValue("species");
        row.createCell(1).setCellValue("no used in reporting period");
        row.createCell(2).setCellValue("remaining quota");
        row = sheet.createRow(rowid++);
        Set<Quota> quotas = l.getQuotas();
        HashMap<SpeciesType,  Integer> counts = getUsedSpeciesCount(l, start, end);
        for (Quota q : quotas) {
            Integer used = counts.containsKey(q.getSpeciesType()) ? counts.get(q.getSpeciesType()) : 0;
            row.createCell(0).setCellValue(q.getSpeciesType().getName());
            row.createCell(1).setCellValue(used);
            row.createCell(2).setCellValue(q.getNumber() - q.getUsed());
            row = sheet.createRow(rowid++);
        }
        row = sheet.createRow(rowid++);
        row.createCell(0).setCellValue("Treatment");
        row.createCell(1).setCellValue("Subject");
        row.createCell(2).setCellValue("Start date");
        row.createCell(3).setCellValue("End date");

        Set<TreatmentType> types = l.getTreatmentTypes();
        for (TreatmentType t : types) {
            rowid = exportTreatmentType(t, sheet, rowid, start, end);
        }
    }


    private HashMap<SpeciesType, Integer> getUsedSpeciesCount(License l, Date start, Date end) {
        HashMap<SpeciesType, Integer> counts = new HashMap<>();
        List<Treatment> treatments = new LinkedList<>();
        for (TreatmentType tt: l.getTreatmentTypes()) {
            treatments.addAll(tt.getTreatments());
        }
        for (Treatment t : treatments) {
            if (t.getStart().before(end) && (t.getEnd() != null && t.getEnd().before(end) && t.getEnd().after(start))) {
                SpeciesType st = t.getSubject().getSpeciesType();
                if (counts.containsKey(st)) {
                    counts.put(st, counts.get(st) + 1);
                } else {
                    counts.put(st, 1);
                }
            }
        }
        return counts;
    }


    public int exportTreatmentType(TreatmentType t, XSSFSheet sheet, int rowid, Date start, Date end) {
        Set<Treatment> treatments = t.getTreatments();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        if(treatments.size() == 0) {
            return rowid;
        }
        XSSFRow row = sheet.createRow(rowid++);
        row.createCell(0).setCellValue(t.getName());
        for (Treatment treatment : treatments) {
            if (treatment.getEnd() == null || (treatment.getEnd().before(start) || treatment.getEnd().after(end))) {
                continue;
            }
            row.createCell(1).setCellValue(treatment.getSubject().getName());
            row.createCell(2).setCellValue(sdf.format(treatment.getStart()));
            row.createCell(3).setCellValue(sdf.format(treatment.getEnd()));
            row = sheet.createRow(rowid++);
        }
        return rowid;
    }

    private void endTreatment() {
        treatmentsTable.endTreatment(treatmentsTable.getSelectionModel().getSelectedItem());
        refreshOpenTreatments();
    }

    private void treatmentSelected(Treatment t) {
        endTreatmentLabel.setDisable(t == null);
    }
}


