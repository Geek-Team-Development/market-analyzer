package ru.manalyzer.telegram.command;

import java.util.Arrays;

public enum BotCommand {

    START("/start"),
    AUTHORIZATION("/authorization"),
    FAVORITES("/favorites"),
    UNSUPPORTED("");

    private final String label;

    BotCommand(String command) {
        this.label = command;
    }

    public static BotCommand valueOfLabel(String label) {
        return Arrays.stream(values())
                .filter(value -> value.label.equals(label))
                .findFirst()
                .orElse(UNSUPPORTED);
    }
}
