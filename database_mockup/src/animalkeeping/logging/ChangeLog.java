package animalkeeping.logging;

import java.sql.Timestamp;
import java.time.Instant;

/**
 * Created by huben on 30.01.17.
 */
public class ChangeLog {

    public Long id;
    public String typeOfAction;
    public java.util.Date actionTime;
    public String targetType;
    public Long targetId;

    ChangeLog(String action,String type,Long targetId)
    {
        this.typeOfAction = action;
        this.actionTime = Timestamp.from(Instant.now());
        this.targetType = type;
        this.targetId = targetId;
    }
}
