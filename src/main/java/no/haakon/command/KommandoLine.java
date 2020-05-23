package no.haakon.command;

import no.haakon.command.commandsets.files.FileCommands;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * This is the class that is responsible for actually providing a textual interface.
 * This implementation runs in a shell, and hence can't have things like auto-completion or other goodies.
 * That's too bad, but it can have some good things regardless.
 */
public class KommandoLine {
    private final CommandEnvironment environment;
    private final Map<String, Command> commands;
    private final Command errorCommand;
    private final SimpleCommandFactory simpleCommandFactory;

    /**
     * This is the standard, dumb constructor.
     * It's meant for internal use, but it's made public now to experiment with stuff.
     * If you're for some reason using this code, other constructors are probably better.
     * @param environment The environment you want to use. Cannot be null.
     * @param commands The commands you want ot use. Cannot be null.
     */
    public KommandoLine(CommandEnvironment environment, Map<String, Command> commands, Command errorCommand) {
        this.environment = environment;
        this.commands = commands;
        this.simpleCommandFactory = new SimpleCommandFactory();
        this.errorCommand = errorCommand;
    }

    // factory methods that are probably nicer to use, and their helpers:

    /**
     * Creates a standard no fuzz error message for use when the system cannot find a command.
     * Takes an errorMessageFormat to support localization. You'd think that you could set the name and usage-string,
     * but no, you can't. You're not supposed to use this command anyway, the system is. The name and usage string
     * are mostly there so that if you poke at this in a debugger, you'll have some hints about what this command is.
     *
     * @param errorMessageFormat The format for the error message. Must have one and only one %s directive.
     *                           Should end with a newline (%n).
     * @return a standard-ish command for reporting that it cannot find the command.
     */
    private static Command standardError(String errorMessageFormat) {
        return new Command() {
            @Override
            public String name() {
                return "command-not-found-error";
            }

            @Override
            public String usageString() {
                return "used by the command line itself when it cannot find a command";
            }

            @Override
            public int execute(String[] args, InputStream in, PrintStream out, PrintStream err) {
                err.printf(errorMessageFormat, args[0]);
                return 0;
            }
        };
    }

    public static KommandoLine standardKommandoLine() {
        String format = "Cannot find command with the name '%s'.%n";
        KommandoLine cli = new KommandoLine(new CommandEnvironment(), new HashMap<>(), standardError(format));
        cli.setCommand(cli.helpFunction());
        cli.setCommand(cli.simpleCommandFactory.createSimpleCommand(
                "exit",
                "In a mockery of UX, tells you to hit Ctrl + D to exit, just like in Python",
                ((args, in, out, err) -> {
                    out.println("To exit, hit CTRL + D.");
                    return 0;
                })));
        cli.setStandardEnvironmentVariables();

        new FileCommands(cli).addFileCommands();
        return cli;
    }

    // standard commands that should always be available

    private Command helpFunction() {
        Execution execution = (String[] args, InputStream in, PrintStream out, PrintStream err) -> {
            out.println("Commands with usage:");
            out.println(commands.values().stream()
                    .sorted(Comparator.comparing(Command::name))
                    .map(command -> String.format("%s:%n%s%n", command.name(), command.usageString()))
                    .collect(Collectors.joining("--------\n")));
            return 0;
        };
        return getSimpleCommandFactory().createSimpleCommand("help", "Lists all commands. Ignores all arguments", execution);
    }

    // methods

    public void setCommand(Command command) {
        commands.put(command.name(), command);
    }

    public void setStandardEnvironmentVariables() {
        environment.set("prompt", "-> ");
    }

    public void run() {

        // TODO: Obviously these should be set somewhere else...
        InputStream in = System.in;
        PrintStream out = System.out;
        PrintStream err = System.err;

        try(Scanner inputScanner = new Scanner(in)) {
            out.print(environment.get("prompt"));
            while(inputScanner.hasNext()) {
                String input = inputScanner.nextLine();
                String[] command = input.split("\\s+");
                if(command.length > 0) {
                    int result = commands.getOrDefault(command[0], errorCommand).execute(command, in, out, err);
                    environment.set("last-command", "" + result);
                }
            }
        }
    }

    // getters

    public CommandEnvironment getEnvironment() {
        return environment;
    }

    public SimpleCommandFactory getSimpleCommandFactory() {
        return simpleCommandFactory;
    }

    public static void main(String[] args) {
        System.out.println("This demonstrates a commandline, enjoy.");
        KommandoLine.standardKommandoLine().run();
    }
}
