package animalkeeping.ui;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Subject;
import animalkeeping.model.SubjectNote;
import animalkeeping.ui.Main;
import animalkeeping.ui.NotesFrom;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.Date;

import static animalkeeping.util.DateTimeHelper.getDateTime;
import static animalkeeping.util.Dialogs.showInfo;

/**
 * Created by grewe on 2/15/17.
 */
public class SubjectNotesForm extends NotesFrom<SubjectNote, Subject> {


    public SubjectNotesForm(Subject s) {
        super(s);

    }

    public SubjectNotesForm(SubjectNote sn, Subject s) {
        super(sn, s);
    }

    public SubjectNote persist() {
        if (note_entity == null) {
            note_entity = new SubjectNote();
        }

        if (personComboBox.getValue() == null || nameField.getText() == null || commentArea.getText() == null || datePicker.getValue() == null || timeField.getText() == null){
            System.out.println("Please submit all required information");
            return null;
        }

        note_entity.setPerson(personComboBox.getValue());
        note_entity.setName(nameField.getText());
        note_entity.setComment(commentArea.getText());
        Date d = getDateTime(datePicker.getValue(), timeField.getText());
        note_entity.setDate(d);
        note_entity.setSubject(entity);

        Communicator.pushSaveOrUpdate(note_entity);
        return null;
    }
}
