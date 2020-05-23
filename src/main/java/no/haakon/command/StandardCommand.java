package no.haakon.command;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * A standard command gets to know certain things about the world. Whereas the {@link SimpleCommandFactory} is
 * there to create very simple commands that have nothing to do with anything around them, StandardCommands
 * get to know about the context they run in. They get to speak to the command line runner that runs them,
 * and access its environment. This lets it do just about anything, making it a powerful enough class that most
 * needs can be met using it.
 *
 * This abstract class is meant to do the boring housekeeping for you, so you can concentrate on the important parts.
 */
public abstract class StandardCommand implements Command {

    private final String name;
    private final String usageString;

    protected final KommandoLine kommandoLine;


    public StandardCommand(String name, String usageString, KommandoLine kommandoLine) {
        this.name = name;
        this.usageString = usageString;
        this.kommandoLine = kommandoLine;
    }

    protected CommandEnvironment env() {
        return kommandoLine.getEnvironment();
    }


    @Override
    public String name() {
        return name;
    }

    @Override
    public String usageString() {
        return usageString;
    }

    @Override
    public abstract int execute(String[] args, InputStream in, PrintStream out, PrintStream err);

    // utility methods of various sorts.

    /**
     * Checks if you have enough arguments set.
     * Remember that the name of the command is the first argument. So if you require a single extra argument,
     * you need to ask for 2 here.
     * @param number The number of arguments required, including the name of the command.
     * @param args The arguments given.
     * @return true if enough arguments are supplied, otherwise false.
     */
    protected boolean requireArgs(int number, String[] args) {
        return args.length >= number;
    }
}
