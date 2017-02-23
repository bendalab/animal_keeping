package animalkeeping.ui.controller;

import animalkeeping.model.*;
import animalkeeping.ui.*;
import animalkeeping.util.Dialogs;
import animalkeeping.util.EntityHelper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Pair;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;

/**
 * Created by jan on 14.01.17.
 */
public class InventoryController extends VBox implements Initializable, View {
    @FXML private PieChart populationChart;
    @FXML private VBox unitsBox;
    @FXML private VBox chartVbox;
    @FXML private Label allLabel;
    @FXML private ScrollPane tableScrollPane;
    private VBox controls;
    private HashMap<String, HousingUnit> unitsHashMap;
    private ControlLabel animalUseLabel;
    private ControlLabel exportLabel;
    private ControlLabel exportStock;


    public InventoryController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../fxml/Inventory.fxml"));
        loader.setController(this);
        try {
            this.getChildren().add(loader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        unitsHashMap = new HashMap<>();
        controls = new VBox();
        animalUseLabel = new ControlLabel("animal use", true);
        exportLabel = new ControlLabel("export register", true);
        exportStock = new ControlLabel("export stock", false);
        exportStock.setTooltip(new Tooltip("Export current stock list to excel sheet."));
        exportStock.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.PRIMARY)){
                exportStockList();
            }
        });
        controls.getChildren().add(animalUseLabel);
        controls.getChildren().add(exportLabel);
        controls.getChildren().add(exportStock);
        this.fillList();
        listAllPopulation();
    }

    private void fillList() {
        List<HousingUnit> result = null;
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("from HousingUnit where parent_unit_id is NULL").list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }

        if (result != null) {
            for (HousingUnit h : result) {
                unitsHashMap.put(h.getName(), h);
                Label label = new Label(h.getName());
                label.setUnderline(true);
                label.setTextFill(allLabel.getTextFill());
                label.setOnMouseClicked(event -> listPopulation(unitsHashMap.get(h.getName())));
                unitsBox.getChildren().add(label);
                unitsBox.setMargin(label, new Insets(0., 0., 5., 5.0 ));
              }
        } else {
            System.out.println("mist");
        }
    }

    @FXML
    private void listAllPopulation() {
        List<SpeciesType> result = null;

        List<Housing> housings = null;
        Session session = Main.sessionFactory.openSession();
        try {
            session.beginTransaction();
            result = session.createQuery("from SpeciesType").list();
            housings = session.createQuery("from Housing where end_datetime is null").list();
            session.getTransaction().commit();
            session.close();
        } catch (HibernateException e) {
            e.printStackTrace();
            if (session.isOpen()) {
                session.close();
            }
        }
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
        tableScrollPane.setContent(null);
        HousingTable table = new HousingTable(housings);
        tableScrollPane.setContent(table);
    }


    private void listPopulation(HousingUnit housingUnit) {
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
        tableScrollPane.setContent(null);
        HousingTable table = new HousingTable(housings);
        tableScrollPane.setContent(table);
    }

    @Override
    public  VBox getControls() {
        return controls;
    }

    @Override
    public void refresh() {
        //TODO refresh
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
        Pair<Date, Date> interval = Dialogs.getDateInterval();
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


        //Write the workbook in file system
        try {
            FileOutputStream out = new FileOutputStream(new File("Writesheet.xlsx"));
            workbook.write(out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
