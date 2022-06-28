/*
 * Copyright 2018-2021 Esito AS
 * Licensed under the g9 Anonymizer Runtime License Agreement (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer;

import no.esito.anonymizer.IContext.RunType;
import no.esito.anonymizer.core.AbstractContext;

/**
 * Factory methods for running standalone.
 */
public class ContextFactory {

    /**
     * Factory method for anonymization context.
     *
     * @param connection connection
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
     * @param connection connection
     * @param params Erase parameters
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
     * @param connection connection
     * @param params SAR parameters
     * @param sarwriter SAR writer
     * @return SAR context
     */
    public static IContext createSarContext(IDataSource datasource, String[] params, ISarWriter sarwriter) {
        return new AbstractContext(RunType.SAR, params,datasource) {

            @Override
            public ISarWriter getSarWriter() {
                return sarwriter;
            }
        };
    }

}
