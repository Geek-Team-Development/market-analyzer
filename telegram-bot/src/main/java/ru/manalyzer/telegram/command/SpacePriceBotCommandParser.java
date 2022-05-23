package ru.manalyzer.telegram.command;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import ru.manalyzer.telegram.command.BotCommand;
import ru.manalyzer.telegram.command.CommandParser;

import java.util.Optional;

@Component
public class SpacePriceBotCommandParser implements CommandParser {

    @Override
    public BotCommand parseCommand(Message message) {
        Optional<MessageEntity> commandEntity = message.getEntities().stream()
                .filter(entity -> "bot_command".equals(entity.getType()))
                .findFirst();

        BotCommand botCommand;
        if (commandEntity.isPresent()) {
            String commandString = message.getText()
                    .substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
            botCommand = BotCommand.valueOfLabel(commandString);
        } else {
            botCommand = BotCommand.UNSUPPORTED;
        }
        return botCommand;
    }
}
