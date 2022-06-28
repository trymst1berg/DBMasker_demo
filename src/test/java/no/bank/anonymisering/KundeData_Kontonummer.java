// Generated with g9 DBmasker.

package no.bank.anonymisering;

import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IInput;
import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.core.AbstractMasking;
import no.esito.anonymizer.mask.MaskRandomInteger;

/**
 * Create random norwegian phone number
 */
public class KundeData_Kontonummer extends AbstractMasking {

    @Override
    public IColumn getColumn() {
        return new TextColumn("Kontonummer");
    }

    @Override
    public IInput[] getInputs() {
        return new IInput[] {
            new MaskRandomInteger("1000","9990"),
            new MaskRandomInteger("10","99"),
            new MaskRandomInteger("10001","99909"),
        };
    }

    @Override
    public ITransformation getTransformation() {
        return null;
    }

    @Override
    public String getFormat() {
        return "%d.%d.%d";
    }

    @Override
    public boolean isUnique() {
        return false;
    }
}
