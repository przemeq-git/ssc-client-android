package edu.agh.mobile.sc.executor;

import edu.agh.mobile.sc.communication.ArgResolver;
import edu.agh.mobile.sc.communication.MessageBuilder;
import edu.agh.mobile.sc.communication.ResponseServer;

/**
 * @author Przemyslaw Dadel
 */
public interface Executor {

    /**
     * @param script
     * @param argResolver
     * @param responseServer
     * @param messageBuilder
     * @param timeout in millisconds
     */
    void execute(String script, ArgResolver argResolver, ResponseServer responseServer, MessageBuilder messageBuilder, long timeout);

}
