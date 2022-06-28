// Generated with g9 DBmasker.

package example.anonymizer.updatecustomertasks;

import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IInput;
import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.core.AbstractMasking;
import no.esito.anonymizer.mask.MaskColumn;
import no.esito.anonymizer.transformations.Email;

public class UpdateEmailsBasedOnNameColumn extends AbstractMasking {

    @Override
    public IColumn getColumn() {
        return new TextColumn("email");
    }
    @Override
    public IInput[] getInputs() {
        return new IInput[] {
            new MaskColumn(new TextColumn("name"),null),
        };
    }

    @Override
    public ITransformation getTransformation() {
        return new Email();
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
