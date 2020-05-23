package no.haakon.command;

import java.io.InputStream;
import java.io.PrintStream;

public interface Command {
    String name();
    String usageString();
    int execute(String[] args, InputStream in, PrintStream out, PrintStream err);
}
