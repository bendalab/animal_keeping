package animalkeeping.logging;

import animalkeeping.ui.Main;
import org.hibernate.Session;


import java.sql.Connection;
import java.util.Date;

/**
 * Created by huben on 30.01.17.
 */
public class ChangeLogUtil {


public static void LogIt(String action,
 ChangeLogInterface entity ){

Session tempSession = Main.sessionFactory.openSession();

 try {

ChangeLog auditRecord = new ChangeLog(action, entity.getType(),entity.getId());
tempSession.save(auditRecord);
tempSession.flush();

 } finally {
tempSession.close();
 }
}
}

