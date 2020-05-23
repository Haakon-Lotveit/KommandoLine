package no.haakon.command.commandsets.files;

import no.haakon.command.Command;
import no.haakon.command.KommandoLine;
import no.haakon.command.StandardCommand;

import java.io.File;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Path;

public class FileCommands {

    private final KommandoLine cli;

    public FileCommands(KommandoLine cli) {
        this.cli = cli;
    }

    /**
     * Adds a bunch of standard file commands to the cli.
     * Will also set any environment variables that it need to function properly
     */
    public void addFileCommands() {
        cli.getEnvironment().set("working-directory", System.getProperty("user.dir"));
        changeDirectory("cd");
    }

    /**
     * Creates a command for setting the current directory.
     * @param aliases Aliases you want for said command. "cd" is perhaps the most obvious one.
     */
    public void changeDirectory(String... aliases) {
        Command cd = new StandardCommand(
                "change-directory",
                "Sets the working directory to the first argument. If no arguments are given, goes to user dir. If an invalid directory is supplied, does nothing.",
                cli) {
            @Override
            public int execute(String[] args, InputStream in, PrintStream out, PrintStream err) {
                int exit = -1;
                if(args.length > 1) {
                    File f = new File(args[1]);
                    if(f.exists()) {
                        if(f.isDirectory()) {
                            env().set("working-directory", args[1]);
                            exit = 0;
                        } else {
                            err.printf("%s is not a directory%n", args[1]);
                            exit = 1;
                        }
                    } else {
                        err.printf("%s does not exist%n", args[1]);
                        exit = 2;
                    }
                } else {
                    env().set("working-directory", System.getProperty("user.dir"));
                }
                return exit;
            }
        };

        cli.setCommand(cd);
        for(String alias : aliases) {
            // todo: should be a direct method for aliasing?
            cli.setCommand(cli.getSimpleCommandFactory().alias(cd, alias));
        }
    }

}
