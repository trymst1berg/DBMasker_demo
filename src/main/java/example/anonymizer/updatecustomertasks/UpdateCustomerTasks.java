// Generated with g9 DBmasker.

package example.anonymizer.updatecustomertasks;

import no.esito.anonymizer.ITask;
import no.esito.anonymizer.core.AbstractTaskGroup;

public class UpdateCustomerTasks extends AbstractTaskGroup {

    @Override
    public String getName() {
        return "updateCustomerTasks";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public ITask[] getTasks() {
        return new ITask[] {
            new UpdateCustomerNameUsingTwoFiles(),
            new UpdateCustomerEmailNotNullSubset(),
            new UpdateCustomerRandomizeCreated(),
        };
    }
}
