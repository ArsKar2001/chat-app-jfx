package ru.geekbrains.akaramanov.chatappjfx;

import java.util.regex.Pattern;

public enum ChatCommand {

    AUTH(Pattern.compile("/auth\\s\\w+\\s\\w+")),
    AUTH_OK(Pattern.compile("/authok\\s\\w+")),
    END(Pattern.compile("/end")),
    WRITE(Pattern.compile("/write\\s\\w+\\s"));

    public static final Pattern COMMAND_PATTERN = Pattern.compile("/\\w+");

    private final Pattern pattern;

    ChatCommand(Pattern compile) {
        pattern = compile;
    }

    public static boolean isCommand(String message) {
        return COMMAND_PATTERN.matcher(message).find(0);
    }

    public static ChatCommand getCommand(String str) {
        for (ChatCommand value : values())
            if (str.startsWith(value.toString())) return value;
        return valueOf(str);
    }

    @Override
    public String toString() {
        return "/" + super.toString().replaceAll("_+", "").toLowerCase();
    }

    public Pattern getPattern() {
        return pattern;
    }
}
