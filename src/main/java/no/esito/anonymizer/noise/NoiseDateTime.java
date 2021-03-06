/*
 * Copyright 2018-2021 Esito AS
 * Licensed under the g9 Anonymizer Runtime License Agreement (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://download.esito.no/licenses/anonymizerruntimelicense.html
 */
package no.esito.anonymizer.noise;

import java.time.LocalDateTime;

import no.esito.anonymizer.Log;

/**
 * Noise for DateTime. <br>
 * Noise numbers are rounded up to a second.
 */
public class NoiseDateTime extends AbstractNoise {

    double offset;

    double fixed;

    /**
     * Noise constructor.
     *
     * @param offset simply added
     * @param fixed deviation
     */
    public NoiseDateTime(Double offset, Double fixed) {
        this(offset, fixed, 0.0);
    }

    public NoiseDateTime(Double offset, Double fixed, Double percentage) {
        this.offset = offset;
        this.fixed = fixed;
    }

    @Override
    public Object randomize(Object input) {
        try {
            LocalDateTime dt = (LocalDateTime) input;
            double r = getRandom().nextGaussian();
            return dt.plusSeconds(Math.round(offset + fixed * r));
        } catch (NumberFormatException e) {
            Log.error(e);
        }
        return null;
    }

}
