package ru.geekbrains.akaramanov.chatappjfx;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum ChatCommand {

    AUTH(Pattern.compile("/auth\\s\\w+\\s\\w+$")),
    AUTH_OK(Pattern.compile("/authok\\s\\w+$")),
    END(Pattern.compile("/end$")),
    WRITE(Pattern.compile("/write\\s\\w+\\s")),
    CLIENTS(Pattern.compile("/clients\\s")),
    ERROR(Pattern.compile("/error\\s"));

    public static final String COMMAND_PREFIX = "/";

    private final Pattern pattern;

    ChatCommand(Pattern compile) {
        pattern = compile;
    }

    public static boolean isCommand(String message) {
        return message.startsWith(COMMAND_PREFIX);
    }

    public static ChatCommand getCommand(String str) {
        return isCommand(str) ? Arrays.stream(values())
                .filter(command -> command.getMatch(str).find(0))
                .findAny().orElseThrow(() -> new RuntimeException("Несуществующая команда: " + str))
                : null;
    }

    public Matcher getMatch(String input) {
        return getPattern().matcher(input);
    }

    @Override
    public String toString() {
        return "/" + super.toString().replaceAll("_+", "").toLowerCase();
    }

    public Pattern getPattern() {
        return pattern;
    }
}
