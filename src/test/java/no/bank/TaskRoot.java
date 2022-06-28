// Generated with g9 DBmasker.

package no.bank;

import no.bank.anonymisering.Anonymisering;
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
            new Anonymisering(),
        };
    }
}
