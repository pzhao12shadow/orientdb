/*
 *
 *  *  Copyright 2010-2016 OrientDB LTD (http://orientdb.com)
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
 *  * For more information: http://orientdb.com
 *
 */

package com.orientechnologies.orient.core.record.impl;

import com.orientechnologies.orient.core.db.ODatabaseDocumentInternal;
import com.orientechnologies.orient.core.db.record.ORecordElement;
import com.orientechnologies.orient.core.metadata.schema.OGlobalProperty;
import com.orientechnologies.orient.core.metadata.schema.OImmutableClass;
import com.orientechnologies.orient.core.metadata.schema.OType;
import com.orientechnologies.orient.core.record.OElement;

import java.util.Map.Entry;
import java.util.Set;

public class ODocumentInternal {

  public static void convertAllMultiValuesToTrackedVersions(ODocument document) {
    document.convertAllMultiValuesToTrackedVersions();
  }

  public static void addOwner(ODocument oDocument, ORecordElement iOwner) {
    oDocument.addOwner(iOwner);
  }

  public static void removeOwner(ODocument oDocument, ORecordElement iOwner) {
    oDocument.removeOwner(iOwner);
  }

  public static void rawField(final ODocument oDocument, final String iFieldName, final Object iFieldValue,
      final OType iFieldType) {
    oDocument.rawField(iFieldName, iFieldValue, iFieldType);
  }

  public static boolean rawContainsField(final ODocument oDocument, final String iFiledName) {
    return oDocument.rawContainsField(iFiledName);
  }

  public static OImmutableClass getImmutableSchemaClass(final ODocument oDocument) {
    if (oDocument == null) {
      return null;
    }
    return oDocument.getImmutableSchemaClass();
  }

  public static OGlobalProperty getGlobalPropertyById(final ODocument oDocument, final int id) {
    return oDocument.getGlobalPropertyById(id);
  }

  public static void fillClassNameIfNeeded(final ODocument oDocument, String className) {
    oDocument.fillClassIfNeed(className);
  }

  public static Set<Entry<String, ODocumentEntry>> rawEntries(final ODocument document) {
    return document.getRawEntries();
  }

  public static void clearTrackData(final ODocument document) {
    document.clearTrackData();
  }

  public static void checkClass(ODocument doc, ODatabaseDocumentInternal database) {
    doc.checkClass(database);
  }

  public static void autoConvertValueToClass(ODatabaseDocumentInternal database, ODocument doc) {
    doc.autoConvertFieldsToClass(database);
  }

  public static Object getRawProperty(ODocument doc, String propertyName) {
    if (doc == null) {
      return null;
    }
    return doc.getRawProperty(propertyName);
  }

  public static ODocument toRawDocument(OElement element) {
    if (element instanceof ODocument) {
      return (ODocument) element;
    } else if (element instanceof OVertexDelegate) {
      return ((OVertexDelegate) element).element;
    } else if (element instanceof OEdgeDelegate) {
      return ((OEdgeDelegate) element).element;
    }
    return null;
  }

}
