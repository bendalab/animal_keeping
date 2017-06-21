package animalkeeping.util;

import animalkeeping.model.Housing;
import animalkeeping.model.HousingUnit;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

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
        exportPopulation(unit, wb);
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
        if (sheet == null) {
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
}
