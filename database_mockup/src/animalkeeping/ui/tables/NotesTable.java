package animalkeeping.ui.tables;

import animalkeeping.model.Note;
import animalkeeping.model.Person;
import animalkeeping.util.TablePreferences;
import javafx.beans.property.ReadOnlyLongWrapper;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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

 * Created by jan on 15.02.17.

 *****************************************************************************/

public class NotesTable<T extends Note> extends TableView<T>{
    private TableColumn<T, Number> idCol;
    private TableColumn<T, String> personCol;
    private TableColumn<T, String> nameCol;
    private TableColumn<T, String> commentCol;
    private TableColumn<T, Date> dateCol;
    private TablePreferences tableLayout;

    public NotesTable() {
        super();
        init();
    }

    public NotesTable(ObservableList<T> items) {
        this();
        this.setItems(items);
    }

    private void init() {
        idCol = new TableColumn<>("id");
        idCol.setCellValueFactory(data -> new ReadOnlyLongWrapper(data.getValue().getId()));
        idCol.prefWidthProperty().bind(this.widthProperty().multiply(0.08));

        nameCol = new TableColumn<>("name");
        nameCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getName()));
        nameCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        dateCol= new TableColumn<>("from");
        dateCol.setCellValueFactory(data -> new ReadOnlyObjectWrapper<Date>(data.getValue().getDate()));
        dateCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        personCol = new TableColumn<>("person");
        personCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getPerson() != null ? data.getValue().getPerson().getLastName() + ", " + data.getValue().getPerson().getFirstName() : ""));
        personCol.prefWidthProperty().bind(this.widthProperty().multiply(0.15));

        commentCol = new TableColumn<>("comment");
        commentCol.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getComment()));
        commentCol.prefWidthProperty().bind(this.widthProperty().multiply(0.47));
        this.getColumns().addAll(idCol, nameCol, dateCol, personCol, commentCol);

        this.getColumns().addListener((ListChangeListener) c -> tableLayout.storeLayout());
        this.setTableMenuButtonVisible(true);
        this.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        for (TableColumn tc : this.getColumns()) {
            tc.visibleProperty().addListener((observable, oldValue, newValue) -> tableLayout.storeLayout());
        }
        this.tableLayout = new TablePreferences(this);
        tableLayout.applyLayout();
    }


    public void setNotes(Set<T> notes) {
        if (getItems() != null) {
            getItems().clear();
            if (notes != null) {
                getItems().addAll(notes);
            }
        } else if (notes != null) {
            getItems().addAll(notes);
        }

    }
}
