// Generated with g9 DBmasker.

package example.anonymizer.updateadresstasks;

import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IInput;
import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.core.AbstractMasking;
import no.esito.anonymizer.mask.MaskFileRandom;

public class UpdateAddressUsingFile extends AbstractMasking {

    @Override
    public IColumn getColumn() {
        return new TextColumn("homeAddress");
    }
    @Override
    public IInput[] getInputs() {
        return new IInput[] {
            new MaskFileRandom("addresses.txt",null),
        };
    }

    @Override
    public ITransformation getTransformation() {
        return null;
    }

    @Override
    public String getFormat() {
        return "%s";
    }

    @Override
    public boolean isUnique() {
        return false;
    }
}
