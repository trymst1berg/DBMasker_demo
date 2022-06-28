/*
 * Copyright 2018-2021 Esito AS
 * Licensed under the g9 Anonymizer Runtime License Agreement (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer.conversions;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import no.esito.anonymizer.IConversion;
import no.esito.anonymizer.Log;

/**
 * Parse a LocalTime from the string input.
 * If conversion fails returns default value
 */
public class String2Time implements IConversion {

    public static final String LABEL = "String2Time - Convert to LocalTime";

    final static DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    public LocalTime convert(String txt) {
        if (txt != null && !txt.isEmpty()) {
            try {
                return LocalTime.parse(txt.split("\\.")[0], fmt);
            } catch (DateTimeParseException e) {
                Log.warning("Time? '" + txt + "'", e);
            }
        }
        return LocalTime.now();
    }
}
