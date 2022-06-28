// Generated with g9 DBmasker.

package example.anonymizer.updatecustomertasks;

import no.esito.anonymizer.IColumn;
import no.esito.anonymizer.IInput;
import no.esito.anonymizer.ITransformation;
import no.esito.anonymizer.column.TextColumn;
import no.esito.anonymizer.core.AbstractMasking;
import no.esito.anonymizer.mask.MaskFileRandom;

public class UpdateNamesUsingFiles extends AbstractMasking {

    @Override
    public IColumn getColumn() {
        return new TextColumn("name");
    }
    @Override
    public IInput[] getInputs() {
        return new IInput[] {
            new MaskFileRandom("firstnames.txt",null),
            new MaskFileRandom("lastnames.txt",null),
        };
    }

    @Override
    public ITransformation getTransformation() {
        return null;
    }

    @Override
    public String getFormat() {
        return "%s %s";
    }

    @Override
    public boolean isUnique() {
        return false;
    }

    @Override
    public String getMappingFile() {
        return "name_map.txt";
    }

    @Override
    public MappingFileUsage getMappingFileUsage() {
        return MappingFileUsage.OUTPUT;
    }            
}
