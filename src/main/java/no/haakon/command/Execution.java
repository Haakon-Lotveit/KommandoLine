package no.haakon.command;

import java.io.InputStream;
import java.io.PrintStream;

@FunctionalInterface
public interface Execution {
    int execute(String[] args, InputStream in, PrintStream out, PrintStream err);
}
