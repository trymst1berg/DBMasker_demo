// Generated with g9 DBmasker.

package example.anonymizer.updateadresstasks;

import no.esito.anonymizer.ITask;
import no.esito.anonymizer.core.AbstractTaskGroup;

/**
 * - - - - Tasks and Rules - - - - //
 */
public class UpdateAdressTasks extends AbstractTaskGroup {

    @Override
    public String getName() {
        return "updateAdressTasks";
    }

    @Override
    public String getDescription() {
        return "- - - - Tasks and Rules - - - - //";
    }

    @Override
    public ITask[] getTasks() {
        return new ITask[] {
            new UpdateAddressFields(),
        };
    }
}
