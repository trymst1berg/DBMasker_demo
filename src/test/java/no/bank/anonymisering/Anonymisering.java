// Generated with g9 DBmasker.

package no.bank.anonymisering;

import no.esito.anonymizer.ITask;
import no.esito.anonymizer.core.AbstractTaskGroup;

public class Anonymisering extends AbstractTaskGroup {

    @Override
    public String getName() {
        return "Anonymisering";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public ITask[] getTasks() {
        return new ITask[] {
            new Update_KundeData(),
        };
    }
}
