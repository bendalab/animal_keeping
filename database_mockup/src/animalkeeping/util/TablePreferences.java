package animalkeeping.util;

import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import java.util.HashMap;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/******************************************************************************
 animalBase
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

 * Created by jan on 30.08.17.

 *****************************************************************************/

public class TablePreferences {
    private TableView t;
    private Preferences prefs;
    private HashMap<String, ColumnSpec> columnSpecs;

    public TablePreferences(TableView table) {
        this.t = table;
        prefs = Preferences.userNodeForPackage(t.getClass());
        if (!prefs.name().equalsIgnoreCase(t.getClass().getSimpleName())) {
            prefs = prefs.node(t.getClass().getSimpleName());
        }
        columnSpecs = new HashMap<>(t.getColumns().size());
        readPreferences();
    }

    public void storeLayout() {
        ObservableList<TableColumn<?, ?>> tcs = t.getColumns();
        for (int i = 0; i < tcs.size(); i++) {
            TableColumn tc = tcs.get(i);
            String key = tc.getText().replace(" ", "_");
            prefs.putInt(key + "__index", i);
            prefs.putDouble(key + "__width", tc.getWidth()/t.getWidth());
            prefs.putBoolean(key + "__visible", tc.isVisible());
        }
        try {
            prefs.flush();
        } catch (Exception e) { e.printStackTrace(); }
    }

    public void applyLayout( ) {
        ObservableList<TableColumn<?, ?>> tcs = t.getColumns();
        for (int i = 0; i < tcs.size(); i++) {
            TableColumn tc = tcs.get(i);
            if (columnSpecs.keySet().contains(tc.getText())) {
                ColumnSpec cs = columnSpecs.get(tc.getText());
                //tc.setPrefWidth(cs.getWidth() * t.getWidth());
                tc.setVisible(cs.getVisible());
                if (cs.getIndex() != i) {
                    tcs.remove(i);
                    tcs.add(cs.getIndex(), tc);
                }
            }
        }
    }

    private void readPreferences() {
        try {
            for (String s : prefs.keys()) {
                String colName = s.substring(0, s.indexOf("__"));
                colName = colName.replace("_", " ");
                String field = s.substring(s.indexOf("__") + 2);
                if (!columnSpecs.keySet().contains(colName)) {
                    columnSpecs.put(colName, new ColumnSpec(colName));
                }
                if (field.equalsIgnoreCase("index")) {
                    columnSpecs.get(colName).setIndex(prefs.getInt(s, -1));
                } else if (field.equalsIgnoreCase("width")) {
                    columnSpecs.get(colName).setWidth(prefs.getDouble(s, 50.0));
                } else {
                    columnSpecs.get(colName).setVisible(prefs.getBoolean(s, true));
                }
            }
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }


    private class ColumnSpec {
        private String name;
        private int index;
        private double width;
        private Boolean visible;

        ColumnSpec(String name) {
            this.name = name;
        }

        int getIndex() {
            return index;
        }

        void setIndex(int index) {
            this.index = index;
        }

        double getWidth() {
            return width;
        }

        void setWidth(double width) {
            this.width = width;
        }

        String getName() {
            return name;
        }

        void setName(String name) {
            this.name = name;
        }

        Boolean getVisible() {
            return visible;
        }

        void setVisible(Boolean visible) {
            this.visible = visible;
        }

        public String toString() {
           return getName() + " index: " + getIndex() + " width: " + getWidth() + " visible: " + getVisible();
        }
    }
}
