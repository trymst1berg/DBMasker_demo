// Generated with g9 DBmasker.

package no.bank.anonymisering;

import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IConversion;
import no.esito.anonymizer.column.DateColumn;
import no.esito.anonymizer.conversions.String2Date;
import no.esito.anonymizer.core.AbstractRandomization;
import no.esito.anonymizer.noise.AbstractNoise;
import no.esito.anonymizer.noise.NoiseDate;

/**
 * Create random norwegian phone number
 */
public class KundeData_Fodselsdag extends AbstractRandomization {

    @Override
    public IColumn getColumn() {
        return new DateColumn("Fodselsdag");
    }

    @Override
    public String getFormat() {
        return "%tF";
    }

    @Override
    public IConversion getConversion() {
        return new String2Date();
    }

    @Override
    public AbstractNoise getNoise() {
        return new NoiseDate(300.0,5.0,0.0);
    }
}
