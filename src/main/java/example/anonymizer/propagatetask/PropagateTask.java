// Generated with g9 DBmasker.

package example.anonymizer.propagatetask;

import no.esito.anonymizer.ITask;
import no.esito.anonymizer.core.AbstractTaskGroup;

public class PropagateTask extends AbstractTaskGroup {

    @Override
    public String getName() {
        return "propagateTask";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public ITask[] getTasks() {
        return new ITask[] {
            new Update_Address(),
        };
    }
}
