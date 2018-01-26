/******************************************************************************
 animalBase
 animalkeeping.ui.forms

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

 *****************************************************************************/
package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.HousingType;
import animalkeeping.model.HousingUnit;
import animalkeeping.ui.widgets.HousingDropDown;
import animalkeeping.util.EntityHelper;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.util.List;
import java.util.Set;
import java.util.Vector;


public class HousingUnitForm extends VBox {
    private TextField nameField, dimensionsField;
    private Label idLabel;
    private TextArea descriptionArea;
    private HousingDropDown parentUnitComboBox;
    private ComboBox<HousingType> typeComboBox;
    private HousingUnit unit;
    private boolean isEdit;


    public HousingUnitForm() {
        super();
        this.setFillWidth(true);
        this.unit = new HousingUnit();
        init();
        this.isEdit = false;
    }

    public HousingUnitForm(HousingUnit unit) {
        this();
        setHousingUnit(unit);
        this.isEdit = unit != null;
    }

    private void init() {
        idLabel = new Label();
        nameField = new TextField();
        dimensionsField = new TextField();
        descriptionArea = new TextArea();
        descriptionArea.setWrapText(true);
        parentUnitComboBox = new HousingDropDown();

        typeComboBox = new ComboBox<>();
        typeComboBox.setCellFactory(new Callback<ListView<HousingType>, ListCell<HousingType>>() {
            @Override
            public ListCell<HousingType> call(ListView<HousingType> param) {
                final ListCell<HousingType> cell = new ListCell<HousingType>() {
                    @Override
                    protected void updateItem(HousingType t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            setText(t.getName());
                        } else {
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
        typeComboBox.setConverter(new StringConverter<HousingType>() {
            @Override
            public String toString(HousingType object) {
                return object.getName();
            }

            @Override
            public HousingType fromString(String string) {
                return null;
            }
        });
        List<HousingType> housingTypes = EntityHelper.getEntityList("from HousingType order by name asc", HousingType.class);
        typeComboBox.getItems().addAll(housingTypes);

        GridPane grid = new GridPane();
        ColumnConstraints column1 = new ColumnConstraints(100,100, Double.MAX_VALUE);
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints(100, 150, Double.MAX_VALUE);
        column2.setHgrow(Priority.ALWAYS);
        ColumnConstraints column3 = new ColumnConstraints(30, 30, Double.MAX_VALUE);
        column3.setHgrow(Priority.NEVER);
        grid.getColumnConstraints().addAll(column1, column2, column3);

        grid.setPadding(new Insets(5, 0, 5, 0));
        grid.add(new Label("ID"), 0, 0);
        grid.add(idLabel, 1, 0);

        grid.add(new Label("parent"), 0, 1);
        grid.add(parentUnitComboBox, 1, 1, 2,1);

        grid.add(new Label("name (*)"), 0, 2);
        grid.add(nameField, 1, 2, 2,1);

        grid.add(new Label("type (*)"), 0, 3);
        grid.add(typeComboBox, 1, 3, 2, 1);

        grid.add(new Label("dimensions"), 0, 4);
        grid.add(dimensionsField, 1, 4, 2, 1);

        grid.add(new Label("description"), 0, 5);
        grid.add(descriptionArea, 0, 6, 3, 3);

        idLabel.prefWidthProperty().bind(column2.maxWidthProperty());
        parentUnitComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        nameField.prefWidthProperty().bind(column2.maxWidthProperty());
        typeComboBox.prefWidthProperty().bind(column2.maxWidthProperty());
        dimensionsField.prefWidthProperty().bind(column2.maxWidthProperty());

        Label l = new Label("(*) required");
        l.setFont(new Font(Font.getDefault().getFamily(), 10));
        grid.add(l, 0, 9);
        this.getChildren().add(grid);
    }


    public void setHousingUnit(HousingUnit unit) {
        this.unit = unit != null ? unit : new HousingUnit();
        this.nameField.setText(unit != null ? unit.getName() : "");
        this.idLabel.setText(unit != null ? unit.getId().toString() : "");
        this.descriptionArea.setText(unit != null ? unit.getDescription() : "");
        this.dimensionsField.setText(unit != null ? unit.getDimensions() : "");
        this.parentUnitComboBox.setHousingUnit((unit != null) ? unit.getParentUnit() : null);
        this.typeComboBox.getSelectionModel().select(unit != null ? unit.getHousingType() : null);
    }


    public void setParentUnit(HousingUnit parent) {
        this.parentUnitComboBox.setHousingUnit(parent);
    }


    public HousingUnit getHousingUnit() {
        if (nameField.getText().isEmpty() || typeComboBox.getSelectionModel().isEmpty()) {
            return null;
        }
        unit.setName(nameField.getText());
        unit.setDescription(descriptionArea.getText());
        unit.setDimensions(dimensionsField.getText());
        unit.setHousingType(typeComboBox.getSelectionModel().getSelectedItem());
        unit.setParentUnit(parentUnitComboBox.getHousingUnit());
        return unit;
    }

    public HousingUnit persistHousingUnit() {
        HousingUnit unit = getHousingUnit();
        Communicator.pushSaveOrUpdate(unit);
        return unit;
    }

    public boolean validate(Vector<String> messages) {
        boolean valid = true;
        if (parentUnitComboBox.getHousingUnit() != null && !parentUnitComboBox.getHousingUnit().getHousingType().getCanHaveChildUnits()) {
            messages.add("The selected parent unit can not have sub-units!");
            valid = false;
        }
        if (nameField.getText().isEmpty()) {
            messages.add("The name of the housing unit must not be empty!");
            valid = false;
        }
        if (typeComboBox.getValue() == null) {
            messages.add("The type of the Housing unit must be specified, please select an existing, or create a new!");
            valid = false;
        }
        if (!nameField.getText().isEmpty() && !isEdit) {
            Vector<String> params = new Vector<>();
            params.add("name");
            Vector<Object> objects = new Vector<>();
            objects.add(nameField.getText());
            if (EntityHelper.getEntityList("From HousingUnit where name like :name", params, objects, HousingUnit.class).size() > 0) {
                messages.add("HousingUnit name is already used! Select another name!");
                valid = false;
            }
        }
        if (parentUnitComboBox.getHousingUnit() != null) {
            Set<HousingUnit> child_units = getHousingUnit().getChildHousingUnits(true);
            if (child_units.contains(parentUnitComboBox.getHousingUnit())) {
                messages.add("A Housing unit can not be child of it's own child units!");
                valid = false;
            }
        }
        return valid;
    }
}
