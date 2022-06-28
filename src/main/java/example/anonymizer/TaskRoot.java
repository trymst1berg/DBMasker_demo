// Generated with g9 DBmasker.

package example.anonymizer;

import example.anonymizer.propagatetask.PropagateTask;
import example.anonymizer.updateadresstasks.UpdateAdressTasks;
import example.anonymizer.updatecustomertasks.UpdateCustomerTasks;

import no.esito.anonymizer.ITask;
import no.esito.anonymizer.core.AbstractTaskGroup;

public class TaskRoot extends AbstractTaskGroup {

    @Override
    public String getName() {
        return "Tasks";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public ITask[] getTasks() {
        return new ITask[] {
            new UpdateAdressTasks(),
            new UpdateCustomerTasks(),
            new PropagateTask(),
        };
    }
}
