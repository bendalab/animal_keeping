package animalkeeping.ui;

import javafx.event.ActionEvent;
import javafx.scene.control.Hyperlink;

/**
 * Created by huben on 15.12.16.
 */
public class FishLink extends InternalLink {
    String species_name;
    Integer species_id;
    ConnectionManager connector = new ConnectionManager();


    FishLink(String label, ButtonService parent) {
        super(label);
        species_name = label;
        this.species_id = species_id;
        super.setOnAction(event -> {
            parent.update_grid();
            //"alias" gibt NullPointerException!!
            try {
                connector.<IndividualLink>linkTableFromDatabase(parent.grid, "s.id, s.name, st.name as \"species_name\", s.source_id", "census_subject as s, census_speciestype as st", "s.species_id = st.id AND st.name = \"" + species_name + "\"", "id", IndividualLink.class, parent);
            }
            catch (Exception e) {
                System.out.println( e.getMessage() );
            }
                    }

        );
    }




}
