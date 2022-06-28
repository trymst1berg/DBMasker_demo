/*
 * Copyright 2018-2021 Esito AS
 * Licensed under the g9 Anonymizer Runtime License Agreement (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer.core;

import no.esito.anonymizer.*;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;

/**
 * Base functionality for Masking a column.
 */
public abstract class AbstractMasking extends AbstractAnonymization {

    /**
     * @return - Inputs for constructing the value
     */
    public abstract IInput[] getInputs();

    /**
     * @return - Transformation to be used (can be null)
     */
    public abstract ITransformation getTransformation();

    /**
     * @return - Format string for Java Formatter
     */
    @SuppressWarnings("static-method")
    public String getFormat() {
        return null;
    }

    /**
     * Unique value means that it will need to check what it currently has. If it exists it will run the inputs again up
     * to 10 times before it creates an Exception.
     *
     * @return - true if this should retry to get a unique value
     */
    public abstract boolean isUnique();

    IInput[] inputs;

    @Override
    public void run(IContext context, List<String> columns, List<String[]> rows) throws Exception {
        IColumn column = getColumn();
        ITransformation transformation = getTransformation();
        initMappingFile();
        int col = columns.indexOf(column.getName());
        HashSet<String> uniquecols = isUnique() ? new HashSet<>() : null;
        for (IInput input : inputs) {
            if (input instanceof IPreScan) {
                ((IPreScan) input).scan(col, rows);
            }
        }
        if (transformation instanceof IPreScan) {
            ((IPreScan) transformation).scan(col, rows);
        }
        for (String[] row : rows) { // updates the value of a specific column for every row
            String before = row[col];
            if (hasMapping((before))) {
                row[col] = getMapping(before);
                continue;
            }
            String after = mask(columns, row);
            if (uniquecols != null) { // if this column is unique, and masking produces duplicate, try again 10 times
                int loop = 10;
                while (loop-- > 0) {
                    if (uniquecols.contains(after)) {
                        after = mask(columns, row);
                        continue;
                    }
                    uniquecols.add(after);
                    break;
                }
            }
            addMapping(before, after);
            row[col] = after;
        }
        saveMappingFile();
    }

    private String mask(List<String> columns, String[] row) throws Exception {
        ITransformation transformation = getTransformation();
        String format = getFormat();
        Object[] args = new Object[inputs.length];
        boolean isNull = false;
        for (int i = 0; i < inputs.length; i++) {
            args[i] = inputs[i].next(columns, row);
            if (args[i] == null) {
                isNull = true;
                break;
            }
        }
        try {
            String text = format == null || isNull || "NULL".equals(format) ? null : String.format(Locale.US, format, args);
            return transformation == null ? text : transformation.transform(text);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
