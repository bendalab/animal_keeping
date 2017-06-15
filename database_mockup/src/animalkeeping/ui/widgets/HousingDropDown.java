/******************************************************************************
 database_mockup
 animalkeeping.ui.widgets

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

 * Created by jan on 15.06.17.
 *****************************************************************************/

package animalkeeping.ui.widgets;

import animalkeeping.model.HousingUnit;
import animalkeeping.ui.Main;
import animalkeeping.util.EntityHelper;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.control.*;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.util.StringConverter;

import java.util.List;


public class HousingDropDown extends Button {
    private TreeView<HousingUnit> tree;
    private HousingUnit selectedUnit = null;

    public HousingDropDown() {
        super();
        init();
    }

    private void init() {
        Popup popup = new Popup();
        tree = new TreeView<>();
        tree.setEditable(false);
        tree.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        tree.setShowRoot(false);
        tree.setCellFactory(param -> new TextFieldTreeCell<>(new StringConverter<HousingUnit>() {
            @Override
            public String toString(HousingUnit object) {
                param.setTooltip(new Tooltip(object.getDescription().isEmpty() ? "no description" : object.getDescription()));
                return object.getName();
            }

            @Override
            public HousingUnit fromString(String string) {
                return null;
            }
        }));
        tree.getSelectionModel().getSelectedItems().addListener((ListChangeListener<TreeItem<HousingUnit>>) c -> {
            setHousingUnit(c.getList().size() > 0 ? c.getList().get(0).getValue() : null);
            if (popup.isShowing()) {
                popup.hide();
            }
        });
        tree.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.BACK_SPACE) {
                tree.getSelectionModel().clearSelection();
            }
        });
        popup.setHideOnEscape(true);
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.getContent().add(tree);

        ImageView img = new ImageView(new Image(Main.class.getResource("/resources/drop_down.png").toExternalForm()));
        img.setFitHeight(11.5);
        img.setPreserveRatio(true);
        img.setSmooth(true);
        this.setText("");
        this.setGraphic(img);
        this.setTextAlignment(TextAlignment.LEFT);
        this.setContentDisplay(ContentDisplay.RIGHT);
        this.setEllipsisString(" ");
        this.setOnAction(event -> {
            Bounds sourceNodeBounds = this.localToScreen(this.getBoundsInLocal());
            popup.show(this, sourceNodeBounds.getMinX(), sourceNodeBounds.getMaxY());
        });
        applyPadding();
        fillTree();
    }

    private void applyPadding() {
        String text = this.getText();
        int i = 0;
        while (i < 10) {
            text = text.concat("    ");
            i++;
        }

        this.setText(text);
    }

    private void fillTree(){
        final TreeItem<HousingUnit> root = new TreeItem<>();
        root.setExpanded(true);
        tree.setRoot(root);
        List<HousingUnit> units = EntityHelper.getEntityList("from HousingUnit where parentUnit = null", HousingUnit.class);
        for (HousingUnit hu : units) {
            TreeItem<HousingUnit> child = new TreeItem<>(hu);

            root.getChildren().add(child);
            fillRecursive(hu, child);
        }

    }


    private void fillRecursive(HousingUnit unit, TreeItem<HousingUnit> item) {
        for (HousingUnit hu : unit.getChildHousingUnits()) {
            TreeItem<HousingUnit> it = new TreeItem<>(hu);
            item.getChildren().add(it);
            fillRecursive(hu, it);
        }
    }

    public void setHousingUnit(HousingUnit unit) {
        selectedUnit = unit;
        this.setText(selectedUnit != null ? unit.getName() : "");
        this.applyPadding();
    }

    public HousingUnit getHousingUnit() {
        return selectedUnit;
    }

}
