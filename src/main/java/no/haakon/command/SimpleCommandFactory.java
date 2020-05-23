package no.haakon.command;

import java.io.InputStream;
import java.io.PrintStream;

/**
 * A simple factory for creating simple commands.
 * Takes a name, a string describing usage and an {@link Execution} and creates an argument from that.
 */
public class SimpleCommandFactory {
    public Command createSimpleCommand(String name, String usage, Execution action) {
        return new SimpleCommand(name, usage, action);
    }

    public Command alias(Command command, String alias) {
        Execution callAliased =
                (String[] args, InputStream in, PrintStream out, PrintStream err) -> command.execute(args, in, out, err);
        return new SimpleCommand(alias, command.usageString(), callAliased);
    }

    /**
     * This is as simple as a command can get.
     */
    public static class SimpleCommand implements Command {

        private final String name;
        private final String usageString;
        private final Execution execution;

        public SimpleCommand(String name, String usageString, Execution execution) {
            this.name = name;
            this.usageString = usageString;
            this.execution = execution;
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
        public int execute(String[] args, InputStream in, PrintStream out, PrintStream err) {
            return execution.execute(args, in, out, err);
        }
    }
}
