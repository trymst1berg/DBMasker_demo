/*
 * Copyright 2018-2021 Esito AS
 * Licensed under the g9 Anonymizer Runtime License Agreement (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer.core;

import no.esito.anonymizer.IChildRelation;
import no.esito.anonymizer.IColumn;

public class NotInDelete extends AbstractChildRelation {

    /**
     * Delete using NOT IN
     * @param table name
     * @param parentColumns IColumn[]
     * @param children IChildRelation[]
     * @param childColumns IColumn[]
     */
    public NotInDelete(String table, IColumn[] parentColumns, IChildRelation[] children, IColumn[] childColumns) {
        super(table,parentColumns,children,childColumns);
    }

    /**
     * Set NULL using NOT IN
     * @param table name
     * @param parentColumns IColumn[]
     * @param childColumns IColumn[]
     */
    public NotInDelete(String table, IColumn[] parentColumns, IColumn[] childColumns) {
        super(table,parentColumns,null,childColumns);
    }

}
