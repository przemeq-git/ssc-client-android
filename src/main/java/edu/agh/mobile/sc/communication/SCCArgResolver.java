package edu.agh.mobile.sc.communication;

import java.io.IOException;

/**
 * @author Przemyslaw Dadel
 */
public class SCCArgResolver implements ArgResolver {

    private final String arg;
    private final SocialComputingServer server;

    public SCCArgResolver(String arg, SocialComputingServer server) {
        this.arg = arg;
        this.server = server;
    }

    @Override
    public String getArgument() {
        if (arg.startsWith("http")) {
            return fetchArgument(arg);
        }
        return arg;
    }

    private String fetchArgument(String arg) {
        try {
            return server.get(arg);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }
}
