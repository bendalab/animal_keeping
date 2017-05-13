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

 * Created by jan on 27.12.16.

 *****************************************************************************/
package animalkeeping.logging;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ChangeLogInterceptor extends EmptyInterceptor{
    private HashMap<Object, String> inserts = new HashMap<>();
    private HashMap<Object, String> updates = new HashMap<>();
    private Set<Object> deletes = new HashSet<>();

    @Override
    public boolean onSave(Object entity,Serializable id, Object[] state,String[] propertyNames,Type[] types)
    {
        if (entity instanceof ChangeLogInterface){
            inserts.put(entity, estimateQuery(state, propertyNames, types));
        }
        return false;

    }
    @Override
    public boolean onFlushDirty(Object entity,Serializable id,
                                Object[] currentState,Object[] previousState,
                                String[] propertyNames,Type[] types)
            throws CallbackException {
        if (entity instanceof ChangeLogInterface){
            updates.put(entity, estimateQuery(currentState, propertyNames, types));
        }
        return false;

    }
    @Override
    public void onDelete(Object entity, Serializable id,
                         Object[] state, String[] propertyNames,
                         Type[] types) {
        if (entity instanceof ChangeLogInterface){
            deletes.add(entity);
        }
    }

    //called before commit into database
    @Override
    public void preFlush(Iterator iterator) {
        //System.out.println("preFlush");
    }

    //called after committed into database
    @Override
    public void postFlush(Iterator iterator) {
        try{
            for (Object insert : inserts.keySet()) {
                ChangeLogInterface entity = (ChangeLogInterface) insert;
                ChangeLogUtil.LogIt("Saved", entity, inserts.get(insert));
            }
            for (Object update : updates.keySet()) {
                ChangeLogInterface entity = (ChangeLogInterface) update;
                ChangeLogUtil.LogIt("Updated", entity, updates.get(entity));
            }
            for (Object delete : deletes) {
                ChangeLogInterface entity = (ChangeLogInterface) delete;
                ChangeLogUtil.LogIt("Deleted", entity);
            }
        } finally {
            inserts.clear();
            updates.clear();
            deletes.clear();
        }
    }

    private String estimateQuery(Object[] state, String[] propertyNames, Type[] types) {
        String params = "SET(";
        String vals = "VALUE(";
        for (int i = 0; i < types.length; i++) {
            String tname = types[i].getName().toLowerCase();
            if (tname.contains("set")) {
                continue;
            }
            params = params.concat("'" + propertyNames[i] + "' ");
            vals = vals.concat(state[i] != null ? ("'" + state[i].toString() + "' ") : "NULL ");
        }
        return params.concat(") ").concat(vals).concat(")");
    }
}

