/*
 * Copyright 2018-2021 Esito AS
 * Licensed under the g9 Anonymizer Runtime License Agreement (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer.core;

import no.esito.anonymizer.IDataSource;
import no.esito.anonymizer.ISarWriter;

public class ConsoleContext extends AbstractContext {

    AbstractSarWriter sarwriter = new ConsoleSarWriter();

    public ConsoleContext(IDataSource datasource) {
        super(datasource);
    }

    @Override
    public ISarWriter getSarWriter() {
        return sarwriter;
    }

}
