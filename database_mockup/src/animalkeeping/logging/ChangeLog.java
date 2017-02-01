package animalkeeping.logging;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

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

    public Long getId() {
        return id;
    }

    private void setId(Long id) {
        this.id = id;
    }

    public String getTypeOfAction() {
        return typeOfAction;
    }

    private void setTypeOfAction(String typeOfAction) {
        this.typeOfAction = typeOfAction;
    }

    public Date getActionTime() {
        return actionTime;
    }

    private void setActionTime(Date actionTime) {
        this.actionTime = actionTime;
    }

    public String getTargetType() {
        return targetType;
    }

    private void setTargetType(String targetType) {
        this.targetType = targetType;
    }

    public Long getTargetId() {
        return targetId;
    }

    private void setTargetId(Long targetId) {
        this.targetId = targetId;
    }
}
