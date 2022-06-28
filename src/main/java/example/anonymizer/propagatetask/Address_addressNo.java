// Generated with g9 DBmasker.

package example.anonymizer.propagatetask;

import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IInput;
import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.column.NumberColumn;
import no.esito.anonymizer.core.AbstractMasking;
import no.esito.anonymizer.core.PropagateUpdate;
import no.esito.anonymizer.mask.MaskSequence;

public class Address_addressNo extends AbstractMasking {

    @Override
    public PropagateUpdate[] getPropagatedUpdates() {
        return new PropagateUpdate[] {
            new PropagateUpdate("Customer", "addressNo", "addressNo"),
        };
    }

    @Override
    public IColumn getColumn() {
        return new NumberColumn("addressNo");
    }
    @Override
    public IInput[] getInputs() {
        return new IInput[] {
            new MaskSequence(10000, 1),
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
        return true;
    }

    @Override
    public String getTempId() {
        return "9999999";
    }
}
