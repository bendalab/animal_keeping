package sample;

import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;

/**
 * Created by huben on 15.12.16.
 */
public abstract class InternalLink extends Hyperlink{
    ButtonService buttonS;

    InternalLink(){
        super();
    }

    InternalLink(String label){
        super(label);
        }

    InternalLink(String label, ButtonService parent) {
        super(label);

    }

    public void setButtonService(ButtonService buttonS){
        this.buttonS = buttonS;
    }

    public void setLabel(String label){
        setText(label);
    }
}
