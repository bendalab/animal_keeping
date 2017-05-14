package animalkeeping.ui.forms;

import animalkeeping.logging.Communicator;
import animalkeeping.model.Subject;
import animalkeeping.model.SubjectNote;

import java.util.Date;

import static animalkeeping.util.DateTimeHelper.getDateTime;

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
