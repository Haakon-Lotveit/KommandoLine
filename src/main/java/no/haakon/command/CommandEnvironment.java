package no.haakon.command;

import java.util.HashMap;
import java.util.Map;

/**
 * The command environment of a class are the variables that are set there.
 * This can be things like the current working directory, or a system user,
 * but really, it can be whatever you need for your system.
 *
 * Everything is stored as a string, because that's 99% of the needs right there. If you need to store something else,
 * this is probably the wrong thing.
 */
public class CommandEnvironment {
    Map<String, String> values = new HashMap<>();

    /**
     * Returns the value stored under the given name, if none exist, returns the empty string.
     * @param name The name of the value.
     * @return the value if it exists, otherwise the empty string ("").
     */
    public String get(String name) {
        return values.getOrDefault(name, "");
    }

    public void set(String name, String value) {
        values.put(name, value);
    }
}
