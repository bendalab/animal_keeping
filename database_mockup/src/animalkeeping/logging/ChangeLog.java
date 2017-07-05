/******************************************************************************
 Copyright (c) 2017 Neuroethology Lab, University of Tuebingen,
 Jan Grewe <jan.grewe@g-node.org>,
 Dennis Huben <dennis.huben@rwth-aachen.de>

 All rights reserved.

 Redistribution and use in source and binary forms, with or without modification,
 are permitted provided that the following conditions are met:

 1. Redistributions of source code must retain the above copyright notice, this list
 of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice, this
 list of conditions and the following disclaimer in the documentation and/or other
 materials provided with the distribution.

 3. Neither the name of the copyright holder nor the names of its contributors may
 be used to endorse or promote products derived from this software without specific
 prior written permission.

 THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
 BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 DAMAGE.

 * Created by by huben on 30.01.17.

 *****************************************************************************/
package animalkeeping.logging;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;


public class ChangeLog {
    private Long id;
    private String typeOfAction;
    private java.util.Date actionTime;
    private String targetType;
    private String changeSet;
    private Long targetId;
    private String who;

    ChangeLog() {

    }

    ChangeLog(String action,String type,Long targetId, String who)
    {
        this.typeOfAction = action;
        this.actionTime = Timestamp.from(Instant.now());
        this.targetType = type;
        this.targetId = targetId;
        this.who = who;
    }

    ChangeLog(String action,String type,Long targetId, String who, String changeSet)
    {
        this.typeOfAction = action;
        this.actionTime = Timestamp.from(Instant.now());
        this.targetType = type;
        this.targetId = targetId;
        this.who = who;
        this.changeSet = changeSet;
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

    public String getWho() {
        return who;
    }

    private void setWho(String who) {
        this.who = who;
    }

    public String getChangeSet() {
        return changeSet;
    }

    public void setChangeSet(String changeSet) {
        this.changeSet = changeSet;
    }
}
