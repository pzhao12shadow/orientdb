/*
 *
 *  *  Copyright 2014 Orient Technologies LTD (info(at)orientechnologies.com)
 *  *
 *  *  Licensed under the Apache License, Version 2.0 (the "License");
 *  *  you may not use this file except in compliance with the License.
 *  *  You may obtain a copy of the License at
 *  *
 *  *       http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *  Unless required by applicable law or agreed to in writing, software
 *  *  distributed under the License is distributed on an "AS IS" BASIS,
 *  *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *  See the License for the specific language governing permissions and
 *  *  limitations under the License.
 *  *
 *  * For more information: http://www.orientechnologies.com
 *
 */
package com.orientechnologies.orient.server.distributed;

import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.id.ORID;
import com.orientechnologies.orient.server.distributed.task.ODistributedRecordLockedException;
import com.orientechnologies.orient.server.distributed.task.ORemoteTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Stores a transaction request that is waiting for the "completed" message (2-phase) by the leader node. Objects of this class are
 * immutable.
 *
 * @author Luca Garulli (l.garulli--at--orientechnologies.com)
 */
public class ODistributedTxContext {
  private final ODistributedDatabase  db;
  private final ODistributedRequestId reqId;
  private final List<ORemoteTask>     undoTasks     = new ArrayList<ORemoteTask>();
  private final List<ORID>            acquiredLocks = new ArrayList<ORID>();

  public ODistributedTxContext(final ODistributedDatabase iDatabase, final ODistributedRequestId iRequestId) {
    db = iDatabase;
    reqId = iRequestId;
  }

  public void lock(final ORID rid) {
    if (!db.lockRecord(rid, reqId))
      throw new ODistributedRecordLockedException(rid);

    acquiredLocks.add(rid);
  }

  public void addUndoTask(final ORemoteTask undoTask) {
    undoTasks.add(undoTask);
  }

  public ODistributedRequestId getReqId() {
    return reqId;
  }

  public void commit() {
    ODistributedServerLog.debug(this, db.getManager().getLocalNodeName(), null, ODistributedServerLog.DIRECTION.NONE,
        "Distributed transaction: Committing transaction %s", reqId);
    unlock();
    undoTasks.clear();
  }

  public void fix() {
    unlock();
    undoTasks.clear();
  }

  public int rollback(final ODatabaseDocumentTx database) {
    ODistributedServerLog.info(this, db.getManager().getLocalNodeName(), null, ODistributedServerLog.DIRECTION.NONE,
        "Distributed transaction: rolling back transaction %s (%d ops)", reqId, undoTasks.size());

    for (ORemoteTask task : undoTasks) {
      try {
        task.execute(reqId, db.getManager().getServerInstance(), db.getManager(), database);

      } catch (Exception e) {
        ODistributedServerLog.error(this, db.getManager().getLocalNodeName(), null, ODistributedServerLog.DIRECTION.NONE,
            "Error on rolling back transaction %s task %s", e, reqId, task);
      }
    }
    unlock();

    return undoTasks.size();
  }

  protected void unlock() {
    for (ORID lockedRID : acquiredLocks)
      db.unlockRecord(lockedRID);
  }
}