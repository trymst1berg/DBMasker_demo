/*
 * Copyright 2018-2021 Esito AS Licensed under the g9 Anonymizer Runtime License Agreement (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of the License at
 * http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;

import no.esito.anonymizer.IAnonymization;
import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IContext;
import no.esito.anonymizer.IInput;
import no.esito.anonymizer.IPreScan;
import no.esito.anonymizer.IRandom;
import no.esito.anonymizer.IWorkTask;
import no.esito.anonymizer.Log;
import no.esito.anonymizer.mask.MaskColumn;

/**
 * Base functionality for JDBC reads, updates, inserts.
 */
public abstract class AbstractWorkTask implements IWorkTask {

    protected String prefix = "";

    @Override
    public void setLogPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getWhere() {
        return null;
    }

    @Override
    public String getSelectionKey() {
        return null;
    }

    @Override
    public IColumn[] getAllColumns() {
        return new IColumn[0];
    }

    @Override
    public String getDescription() {
        return null;
    }

    private IColumn[] columns;

    long time1;

    public void start(IContext context, String indentation) {
        Log.info(context.getLogBegin() + (indentation + getName()));
        time1 = System.currentTimeMillis();
    }

    public void finish(IContext context, String indentation) {
        long time2 = System.currentTimeMillis();
        Log.info(context.getLogEnd() + (indentation + getName()) + " (" + (time2 - time1) + "ms)");
    }

    /**
     * Gets an array of columns necessary to read from the table.
     *
     * @return array of Column descriptors
     */
    public IColumn[] getColumns() {
        if (columns == null) {
            Collection<IColumn> cols = new LinkedHashSet<>();
            initCols(cols);
            columns = cols.toArray(new IColumn[cols.size()]);
        }
        return columns;
    }

    /**
     * Abstract method to assemble list of columns necessary.
     *
     * @param cols list of column definitions
     */
    protected abstract void initCols(Collection<IColumn> cols);

    /**
     * Utility method to register anonymization methods and if necessary add the column to the list.
     *
     * @param cols columns
     * @param anos anonymizations
     */
    protected static void registerAnonymizations(Collection<IColumn> cols, IAnonymization[] anos) {
        for (IAnonymization ano : anos) {
            cols.add(ano.getColumn());
            if (ano instanceof AbstractMasking) {
                for (IInput input : ((AbstractMasking) ano).getInputs()) {
                    if (input instanceof MaskColumn) {
                        cols.add(((MaskColumn) input).getColumn());
                    }
                }
            }
        }
    }

    /**
     * Prepare inputs that are based on current values.
     *
     * @param context contains Connection
     * @param anonymizations array of anonymizations
     * @throws Throwable SQL error
     */
    protected void prepareInputs(IContext context, IAnonymization[] anonymizations) throws Throwable {
        ArrayList<String> pfs = new ArrayList<>();
        for (IAnonymization ano : anonymizations) {
            if (ano instanceof AbstractMasking) {
                AbstractMasking anomask = (AbstractMasking) ano;
                anomask.inputs = anomask.getInputs();
                for (IInput input : anomask.inputs) {
                    if (input instanceof IPreScan) {
                        String colname = ano.getColumn().getName();
                        if (!pfs.contains(colname))
                            pfs.add(colname);
                    }
                    if (input instanceof IRandom) {
                        ((IRandom) input).setRandom(context.isRepeatableRandom() ? new Random(context.getRandomSeed()) : new Random());
                    }
                }
            }
        }
        if (!pfs.isEmpty()) {
            List<String[]> rows = context.getDatasource().dsReadRows(getTable(), pfs, null);
            for (IAnonymization ano : anonymizations) {
                if (ano instanceof AbstractMasking) {
                    AbstractMasking anomask = (AbstractMasking) ano;
                    for (IInput input : anomask.inputs) {
                        if (input instanceof IPreScan) {
                            ((IPreScan) input).scan(pfs.indexOf(anomask.getColumn().getName()), rows);
                        }
                    }
                }
            }
        }
    }

    /**
     * Substitute parameters in WHERE clause. The parameters are numbered and coded %PARAMETERn% in the WHERE clause.
     * The unnumbered form %PARAMETER% can be used for parameter 1.
     *
     * @param where clause
     * @param parameters array of strings
     * @return WHERE clause with %PARAMETERn% replaced with content from parameter
     */
    protected static String getWhere(String where, String[] parameters) {
        if (parameters.length > 0) {
            where = where.replaceAll("%PARAMETER1?%", parameters[0].trim());
            for (int i = 1; i < parameters.length; i++) {
                where = where.replaceAll("%PARAMETER" + (i + 1) + "%", parameters[i].trim());
            }
        }
        return where;
    }

    public static void sql(IContext context, String sql) throws Throwable {
        try {
            Log.debug(sql);
            context.getDatasource().dsExecuteUpdate(sql);
        } catch (SQLException e) {
            e.setNextException(new SQLException("Statement: " + sql));
            throw e;
        }
    }
}
