package sample;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

/**
 * Created by huben on 14.12.16.
 */
public class FishButton extends Button {
    ConnectionManager connector = new ConnectionManager();

    FishButton(ButtonService parent, String text){
        super(text);
        super.setOnAction(new EventHandler<ActionEvent>() {

        @Override
        public void handle(ActionEvent e) {
            parent.update_grid();

            ArrayList<Long> species_count = new ArrayList<Long>();
            ArrayList<String> species_names;
            for (Integer i = 1; i <= connector.getCount("species_id","census_subject"); i++ ) {
                species_count.add(connector.getCount("species_id", "census_subject", "species_id = " + i.toString()));

            }
            species_names = connector.getSpecies("st.name", "census_subject as s, census_speciestype = st", "s.species_id = st.id" );

            for (Integer i = 0; i < species_count.size(); i++){
                Text buff = new Text((String) species_count.get(i).toString());
                FishLink species_id = new FishLink((String) species_names.get(i), parent);
                parent.grid.add(species_id,2,i+3);
                parent.grid.add(buff, 3, i+3);
            }
        }
    });
    }
}
