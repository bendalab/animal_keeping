package sample;

import javafx.scene.control.Hyperlink;

/**
 * Created by huben on 15.12.16.
 */
public class IndividualLink extends InternalLink {
    ConnectionManager connector = new ConnectionManager();


    IndividualLink() {
        super();
        super.setOnAction(event -> {

                    buttonS.update_grid();
                    //field "alias" results in NullPointerException!!
                    try {
                        connector.linkTableFromDatabase(buttonS.grid, "t.id, tt.name, t.person_id, p.first, p.last, t.start_datetime, t.end_datetime", "census_treatment as t, census_treatmenttype as tt, census_subject as s, census_person as p", "p.id = t.person_id AND t.type_id = tt.id AND t.subject_id = s.id AND s.id = " + getText(), "person_id", PersonLink.class, buttonS, getText().toString());
                    }
                    catch (Exception e) {
                        System.out.println( e.getMessage() );
                    }
                }


        );
    }

    IndividualLink(String label) {
        super(label);
    }
    IndividualLink(String label, ButtonService parent) {
        this();
        setButtonService(parent);
        setLabel(label);
    }





}