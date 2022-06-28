/*
 * Copyright (c) 2005-2020 Esito AS. All rights reserved.
 */
package no.esito.anonymizer;

public interface IDataSourceHandler{
    IDataSource getDataSource() throws Throwable;
}