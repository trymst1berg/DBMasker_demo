/*
 * Copyright 2018-2021 Esito AS
 * Licensed under the g9 Anonymizer Runtime License Agreement (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer.core;

import no.esito.anonymizer.IContext;
import no.esito.anonymizer.ITask;
import no.esito.anonymizer.ITaskGroup;
import no.esito.anonymizer.Log;

import java.sql.SQLException;

public abstract class AbstractTaskGroup implements ITaskGroup {

    private ITask[] tasks = new ITask[0];

    private String prefix = "";

    /**
     * Either set the tasks using constructor or set using getter.
     */
    public AbstractTaskGroup() {
    }

    @Override
    public ITask[] getTasks() {
        return tasks;
    }

    public AbstractTaskGroup(ITask[] tasks) {
        this.tasks = tasks;
    }

    @Override
    public void setLogPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public void run(IContext context) throws Throwable {
        String tname = getName();
        Log.info(context.getLogBegin() + (prefix + tname));
        long time1 = System.currentTimeMillis();
        ITask[] tasks = getTasks();
        try {
            for (ITask task : tasks) {
                if (task.shouldRun(context)) {
                    task.setLogPrefix(prefix + context.getLogIndentation());
                    task.run(context);
                }
            }
        } catch (Throwable e) {
            context.getDatasource().dsUndo();
            throw e;
        }
        context.getDatasource().dsCommit(tname);
        long time2 = System.currentTimeMillis();
        Log.info(context.getLogEnd() + (prefix + tname) + " (" + (time2 - time1) + "ms)");
    }

    @Override
    public boolean shouldRun(IContext context) {
    	for (ITask task : getTasks()) {
    		if (task.shouldRun(context)) {
    			return true;
    		}
    	}
        return false;
    }

    public static void sql(IContext context, String sql) throws SQLException, Throwable {
        try {
            Log.debug(sql);
            context.getDatasource().dsExecuteUpdate(sql);
        } catch (SQLException e) {
            e.setNextException(new SQLException("Statement: " + sql));
            throw e;
        }
    }
}