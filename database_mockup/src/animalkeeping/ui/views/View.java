package animalkeeping.ui.views;

import javafx.scene.layout.VBox;

/**
 * Created by grewe on 2/21/17.
 */
public interface View {

    VBox getControls();

    void refresh();
}
