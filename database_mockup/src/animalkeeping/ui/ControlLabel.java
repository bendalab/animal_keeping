package animalkeeping.ui;


import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.text.Font;


public class ControlLabel extends Label {
    private Font defaultFont = new Font(Font.getDefault().getFamily(), 12);

    public ControlLabel(String text) {
        this(text, false);
    }

    public ControlLabel(String text, Boolean disabled) {
        super(text);
        this.setFont(defaultFont);
        this.setDisable(disabled);
        this.setOnMouseEntered(event -> {
            getScene().setCursor(Cursor.HAND);
            this.setUnderline(true);
        });
        this.setOnMouseExited(event -> {
            getScene().setCursor(Cursor.DEFAULT);
            this.setUnderline(false);
        });
    }

}
