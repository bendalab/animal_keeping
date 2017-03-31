package animalkeeping.util;

import animalkeeping.model.Migration;
import animalkeeping.ui.Main;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Created by jan on 26.03.17.
 */
public class Version {
    private Properties version_props;
    private Boolean available;
    private String databaseMigrationState;

    public Version() {
        version_props = new Properties();
        available = false;
        databaseMigrationState = "";
        try{
            InputStream in = Main.class.getResourceAsStream("/animalkeeping/version.properties");
            version_props.load(in);
            in.close();
            available = true;
        } catch (IOException e) {
            e.printStackTrace();
            available = false;
        }
    }

    public Boolean getAvailable() {
        return available;
    }

    public String getMigrationState() {
        if (getAvailable()) {
            return version_props.containsKey("migration_state") ? version_props.get("migration_state").toString() : null;
        }
        return null;
    }

    public int getMigrationNumber() {
        String migration = getMigrationState();
        if (migration != null) {
            String[] parts = migration.split("-");
            return Integer.parseInt(parts[0]);
        }
        return 0;
    }

    public String getVersion() {
        if (getAvailable()) {
            return version_props.containsKey("version") ? version_props.get("version").toString() : null;
        }
        return null;
    }

    public int getVersionMajor() {
        String version = getVersion();
        if (version != null) {
            String[] parts = version.split("\\.");
            return Integer.parseInt(parts[0]);
        }
        return 0;
    }

    public int getVersionMinor() {
        String version = getVersion();
        if (version != null) {
            String[] parts = version.split("\\.");
            if (parts.length > 0)
                return Integer.parseInt(parts[1]);
        }
        return 0;
    }

    /**
     * @return int < 0 if app migration is below db migration state, zero if matching, > 0 if app
     * migration state newer than  database
     */
    public int checkMigrationState() {
        Version version = new Version();
        if (!version.getAvailable()) {
            Dialogs.showInfo("Version information could not be found. Some things may not work properly...");
            return -1;
        }
        List<Migration> migrations = EntityHelper.getEntityList("from Migration order by migrationDate asc", Migration.class);
        int dbMigrationNo = -1;
        for (Migration m : migrations) {
            int number = Integer.parseInt(m.getName().split("-")[0]);
            if (number > dbMigrationNo) {
                dbMigrationNo = number;
                databaseMigrationState = m.getName();
            }
        }
        int migrationNo = getMigrationNumber();
        return migrationNo - dbMigrationNo;
    }

    public String getDatabaseMigrationState() {
        return databaseMigrationState;
    }
}
