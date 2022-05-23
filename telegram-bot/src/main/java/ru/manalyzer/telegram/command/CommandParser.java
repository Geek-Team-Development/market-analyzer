package ru.manalyzer.telegram.command;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.manalyzer.telegram.command.BotCommand;

public interface CommandParser {

    BotCommand parseCommand(Message message);
}
