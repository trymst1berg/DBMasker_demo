/*
 * Copyright 2018-2021 Esito AS
 * Licensed under the g9 Anonymizer Runtime License Agreement (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer.core;

import no.esito.anonymizer.IContext;
import no.esito.anonymizer.IDataSource;
import no.esito.anonymizer.ISarWriter;

import java.io.IOException;

import static no.esito.anonymizer.ConfigUtil.getConfig;

public abstract class AbstractContext implements IContext {

    private String[] params;

    private RunType runType;

    private boolean repeatable;

    {
        try {
            repeatable = getConfig().getProperty("masking.repeatable").equals("true");
        } catch (Exception e) {
            repeatable = true;
        }
    }

    long randomSeed;

    {
        try {
            randomSeed = Long.parseLong(getConfig().getProperty("masking.randomSeed"));
        } catch (Exception e) {
            randomSeed = 0L;
        }
    }

    private IDataSource datasource;


    @Override
    public IDataSource getDatasource() {
        return datasource;
    }

    public AbstractContext(RunType runtype, String[] params, IDataSource datasource) {
        this.runType = runtype;
        this.params = params;
        this.datasource = datasource;
    }

    public AbstractContext(IDataSource datasource) {
        this.datasource = datasource;
    }

    public AbstractContext() {
        //
    }

    @Override
    public void setRunParams(String[] params) {
        this.params = params;
    }

    @Override
    public String[] getRunParams() {
        return params;
    }

    @Override
    public void setRunType(RunType run) {
        this.runType = run;

    }

    @Override
    public RunType getRunType() {
        return runType;
    }

    @Override
    public boolean isRepeatableRandom() {
        return repeatable;
    }

    @Override
    public long getRandomSeed() {
        return randomSeed;
    }


    @Override
    public void setRepeatableRandom(boolean repeatable) {
        this.repeatable = repeatable;
    }

    @Override
    public ISarWriter getSarWriter() {
        return null;
    }

    /**
     * Factory method for anonymization context.
     *
     * @return anonymization context
     */
    public static IContext createAnonymizeContext(IDataSource datasource) {
        return new AbstractContext(RunType.RUN, null, datasource) {
            //
        };
    }

    /**
     * Factory method for anonymization erase context.
     *
     * @param params     Erase parameters
     * @return erase context
     */
    public static IContext createEraseContext(IDataSource datasource, String[] params) {
        return new AbstractContext(RunType.ERASE, params, datasource) {
            //
        };
    }

    /**
     * Factory method for anonymization SAR context.
     *
     * @param params     SAR parameters
     * @param sarwriter  SAR writer
     * @return SAR context
     */
    public static IContext createSarContext(IDataSource datasource, String[] params, ISarWriter sarwriter) {
        return new AbstractContext(RunType.SAR, params, datasource) {

            @Override
            public ISarWriter getSarWriter() {
                return sarwriter;
            }

        };
    }

}
