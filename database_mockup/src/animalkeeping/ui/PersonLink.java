package animalkeeping.ui;

/**
 * Created by huben on 15.12.16.
 */
public class PersonLink extends InternalLink {
    ConnectionManager connector = new ConnectionManager();
    String actualName;

    PersonLink() {
        super();
        super.setOnAction(event -> {

                    buttonS.update_grid();
                    //field "alias" results in NullPointerException!!
                    try {
                        connector.linkTableFromDatabase(buttonS.grid, "t.id, tt.name as Treatment , t.subject_id, s.name as \"Subject Name\", t.start_datetime as \"Start\", t.end_datetime as \"End\", p.first, p.last", "census_treatment as t, census_person as p, census_subject as s , census_treatmenttype as tt", "t.type_id = tt.id  AND p.id = t.person_id AND t.subject_id = s.id AND p.id = " + getText(), "subject_id", IndividualLink.class, buttonS, actualName);
                    }
                    catch (Exception e) {
                        System.out.println( e.getMessage() );
                    }
                }


        );
    }

    PersonLink(String label) {
        super(label);
    }
    PersonLink(String label, ButtonService parent) {
        this();
        this.setText(label);

    }

    @Override
    public void setLabel(String label) {
        super.setLabel(label);
        actualName = connector.getName(label);
    }
}
