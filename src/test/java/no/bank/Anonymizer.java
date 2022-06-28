// Generated with g9 DBmasker.

package no.bank;

import no.esito.anonymizer.ITaskGroup;
import no.esito.anonymizer.Log;
import no.esito.anonymizer.core.AbstractAnonymizer;
import no.esito.anonymizer.core.ConsoleCommandHandler;

/**
 * Anonymizer - entry point for running JAR on the command line.
 *
 * For running Anonymizer from other Java programs using APIs:
 * <ul>
 * <li> Use Connect.createDefaultConnection() for connection from config.properties
 * <li> Use ContextFactory methods for creating a Context
 * </ul>
 *
 * Example running a task called MyTask: <code> new MyTask().run(context);</code>
 */
public class Anonymizer extends AbstractAnonymizer {

    @Override
    public ITaskGroup getTaskRoot() {
        return new TaskRoot();
    }

    public static void main(String[] args) {
        Log.configureConsoleLoghandler();
        Anonymizer anonymizer = new Anonymizer();
        try {
            new ConsoleCommandHandler(anonymizer).run(args);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }


    @Override
    public String getEmptyDbTables() {
        return "HOTELCHAIN,QUESTIONTEMPLATE,RANKMAPPING,ROOMCATEGORY,ANSWER,CUSTOMER,HOTEL,INVOICE,QUESTIONNAIRE,ROOM,TEMPLATEBOOKING,HOTELROOMCATEGORY,ADDRESS2,BOOKING,COMPANY,CUSTOMERSATISFACTIONQUESTIONNR,STAY,NIGHT";
    }
}
