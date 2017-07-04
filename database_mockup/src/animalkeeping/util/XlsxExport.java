package animalkeeping.util;

import animalkeeping.model.*;
import animalkeeping.ui.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import org.apache.poi.xssf.usermodel.*;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/******************************************************************************
 database_mockup
 animalkeeping.util

 Copyright (c) 2017 Neuroethology Lab, University of Tuebingen,
 Jan Grewe <jan.grewe@g-node.org>,
 Dennis Huben <dennis.huben@rwth-aachen.de>

 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list
 of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions and the following disclaimer in the documentation and/or other
 materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without specific
 prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 DAMAGE.

 * Created by jan on 21.06.17.

 *****************************************************************************/

public class XlsxExport {

    public static XSSFWorkbook exportPopulation(HousingUnit unit) {
        XSSFWorkbook wb = new XSSFWorkbook();
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                exportPopulation(unit, wb);
                return null;
            }
        };
        exportTask.setOnSucceeded(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select output file");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel doc(*.xlsx)", "*.xlsx"));
            chooser.setInitialFileName(unit.getName() + "_population.xlsx");
            File f = chooser.showSaveDialog(Main.getPrimaryStage());
            if (f != null) {
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    wb.write(out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        new Thread(exportTask).run();
        return wb;
    }

    public static void exportPopulation(HousingUnit unit, XSSFWorkbook wb) {
        if (unit == null)
            return;
        if (wb == null) {
            wb = new XSSFWorkbook();
        }
        XSSFSheet sheet = wb.createSheet(unit.getName());
        exportPopulation(unit, sheet);
    }

    public static void exportPopulation(HousingUnit unit, XSSFSheet sheet) {
        if (sheet == null || unit == null) {
            return;
        }
        Set<Housing> housings = unit.getAllHousings(true);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        int rowid = sheet.getLastRowNum();

        XSSFRow row = sheet.createRow(rowid++);
        row.createCell(0).setCellValue( "Housing unit:" );
        row.createCell(1).setCellValue( unit.getName() );

        row = sheet.createRow(rowid++);
        row.createCell(0).setCellValue( "Date:");
        row.createCell(1).setCellValue( sdf.format(new Date()));

        rowid++;
        row = sheet.createRow(rowid++);
        row.createCell(0).setCellValue("Subject Identifier");
        row.createCell(1).setCellValue("Species");
        row.createCell(2).setCellValue("Import date");
        row.createCell( 3).setCellValue("Subunit");
        row.createCell( 4).setCellValue("In housing unit since");

        for (Housing h : housings) {
            row = sheet.createRow(rowid++);
            row.createCell(0).setCellValue(h.getSubject().getName());
            row.createCell(1).setCellValue(h.getSubject().getSpeciesType().getName());
            row.createCell(2).setCellValue(sdf.format(h.getSubject().getImportDate()));
            if (!Objects.equals(h.getHousing().getId(), unit.getId())) {
                row.createCell(3).setCellValue(h.getHousing().getName());
            }
            row.createCell(4).setCellValue(sdf.format(h.getStart()));

        }
        row = sheet.createRow(rowid++);
        row.createCell(0).setCellValue("Total population:");
        row.createCell(1).setCellValue(housings.size());

        for (int i = 0; i < 5; i++) {
            sheet.autoSizeColumn(i);
        }
    }


    public static void exportAnimalUse(Pair<Date, Date> interval) {
        if (interval == null) {
            return;
        }
        XSSFWorkbook[] workbooks = {null};

        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(100);
                workbooks[0] = new XSSFWorkbook();
                List<License> licenses = getLicenseList(interval.getKey(), interval.getValue());
                for (License l : licenses) {
                    String sheetName = l.getName();
                    XSSFSheet sheet = workbooks[0].createSheet(sheetName);
                    exportLicense(l, sheet, interval.getKey(), interval.getValue());
                }
                return  null;
            }
        };
        exportTask.setOnSucceeded(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select output file");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel doc(*.xlsx)", "*.xlsx"));
            chooser.setInitialFileName( "animal_use.xlsx");
            File f = chooser.showSaveDialog(Main.getPrimaryStage());
            if (f != null) {
                try {
                    FileOutputStream out = new FileOutputStream(f);
                    workbooks[0].write(out);
                    out.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        new Thread(exportTask).run();
    }


    public static void exportAnimalUse() {
        Pair<Date, Date> interval = Dialogs.getDateInterval();
        exportAnimalUse(interval);
    }


    public static void exportLicense(License l, XSSFSheet sheet, Date start, Date end) {
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
        row.createCell(1).setCellValue("used in reporting period");
        row.createCell(2).setCellValue("remaining quota");
        row = sheet.createRow(rowid++);
        Set<Quota> quotas = l.getQuotas();
        HashMap<SpeciesType,  Integer> counts = getUsedSpeciesCount(l, start, end);
        for (Quota q : quotas) {
            Integer used = counts.containsKey(q.getSpeciesType()) ? counts.get(q.getSpeciesType()) : 0;
            row.createCell(0).setCellValue(q.getSpeciesType().getName());
            row.createCell(1).setCellValue(used);
            row.createCell(2).setCellValue((q.getNumber() != null ? q.getNumber() : 0) - q.getUsed());
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
        for (int i = 0; i < 4; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    private static List<License> getLicenseList(Date start, Date end) {
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
        return new ArrayList<>();
    }

    private static HashMap<SpeciesType, Integer> getUsedSpeciesCount(License l, Date start, Date end) {
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

    public static int exportTreatmentType(TreatmentType t, XSSFSheet sheet, int rowid, Date start, Date end) {
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


    public static void exportStockList() {
        Pair<Date, Date> interval = Dialogs.getDateInterval();
        if (interval == null) {
            return;
        }
        exportStockList(interval);
    }


    public static void exportStockList(Pair<Date, Date> interval) {
        if (interval == null) {
            return;
        }
        final XSSFWorkbook workbook = new XSSFWorkbook();
        Task<Void> exportTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(50);
                XSSFRow row;
                XSSFSheet overviewsheet = workbook.createSheet("Stock overview");
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
                for (int i = 0; i < 4; i++) {
                    overviewsheet.autoSizeColumn(i);
                }
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
                row.createCell(1).setCellValue( "from " + sdf.format(interval.getKey()));
                row.createCell(2).setCellValue( "until " + sdf.format(interval.getValue()));

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
                for (int i = 0; i < 8; i++) {
                    stocklist.autoSizeColumn(i);
                }
                return null;
            }
        };
        exportTask.setOnSucceeded(event -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select output file");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel doc(*.xlsx)", "*.xlsx"));
            chooser.setInitialFileName("stock_list.xlsx");
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
        });
        new Thread(exportTask).run();
    }

}
