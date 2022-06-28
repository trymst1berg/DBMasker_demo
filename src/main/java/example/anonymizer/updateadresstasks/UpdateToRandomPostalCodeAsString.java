// Generated with g9 DBmasker.

package example.anonymizer.updateadresstasks;

import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IInput;
import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.core.AbstractMasking;
import no.esito.anonymizer.mask.MaskRandomInteger;

public class UpdateToRandomPostalCodeAsString extends AbstractMasking {

    @Override
    public IColumn getColumn() {
        return new TextColumn("postalCode");
    }
    @Override
    public IInput[] getInputs() {
        return new IInput[] {
            new MaskRandomInteger("1000","9999"),
        };
    }

    @Override
    public ITransformation getTransformation() {
        return null;
    }

    @Override
    public String getFormat() {
        return "%d";
    }

    @Override
    public boolean isUnique() {
        return false;
    }
}
