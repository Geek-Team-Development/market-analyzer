package ru.manalyzer.telegram.keyboard;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.persist.Product;
import ru.manalyzer.property.CardSliderFavoritesProperties;
import ru.manalyzer.service.DatabaseFavoritesService;
import ru.manalyzer.service.TelegramUserService;
import ru.manalyzer.storage.entity.ProductCardSlider;
import ru.manalyzer.telegram.command.CardButtonCommand;
import ru.manalyzer.utility.AuthorizationLinkBuilder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class SpacePriceFavoritesButton implements ChatButton {

    private final TelegramUserService telegramUserService;

    private final DatabaseFavoritesService databaseFavoritesService;

    private final AuthorizationLinkBuilder authorizationLinkBuilder;

    private final CardSliderFavoritesProperties cardSliderFavoritesProperties;

    @Autowired
    public SpacePriceFavoritesButton(TelegramUserService telegramUserService,
                                     DatabaseFavoritesService databaseFavoritesService,
                                     AuthorizationLinkBuilder authorizationLinkBuilder,
                                     CardSliderFavoritesProperties cardSliderFavoritesProperties) {
        this.telegramUserService = telegramUserService;
        this.databaseFavoritesService = databaseFavoritesService;
        this.authorizationLinkBuilder = authorizationLinkBuilder;
        this.cardSliderFavoritesProperties = cardSliderFavoritesProperties;
    }

    @Override
    public List<InlineKeyboardButton> createButtons(ProductCardSlider productCardSlider) {
        if (!productCardSlider.isActive()) {
            return Collections.emptyList();
        }

        Optional<String> userId = telegramUserService.getUserIdByUserChat(productCardSlider.getId());

        InlineKeyboardButton inlineKeyboardButton;
        if (userId.isEmpty()) {
            inlineKeyboardButton = createAuthorizationButton(productCardSlider.getId());
        } else {
            inlineKeyboardButton = createFavoritesButton(userId.get(), productCardSlider);
        }

        return List.of(inlineKeyboardButton);
    }

    private InlineKeyboardButton createFavoritesButton(String userId, ProductCardSlider productCardSlider) {
        ProductDto currentProduct = productCardSlider.navigator().getCurrentProduct();
        InlineKeyboardButton inlineKeyboardButton;
        if (databaseFavoritesService.userFavoritesContainsProduct(userId, currentProduct)) {
            inlineKeyboardButton =
                    getButton(cardSliderFavoritesProperties.getRemoveFavorites(), CardButtonCommand.REMOVE_FAVORITES);
        } else {
            inlineKeyboardButton =
                    getButton(cardSliderFavoritesProperties.getAddFavorites(), CardButtonCommand.ADD_FAVORITES);
        }

        return inlineKeyboardButton;
    }

    private InlineKeyboardButton createAuthorizationButton(String chatId) {
        return InlineKeyboardButton.builder()
                        .text(cardSliderFavoritesProperties.getAuthorization())
                        .url(authorizationLinkBuilder.withChatId(chatId).build())
                        .build();
    }

    private InlineKeyboardButton getButton(String text, CardButtonCommand callbackCommand) {
        return InlineKeyboardButton.builder()
                .text(text)
                .callbackData(callbackCommand.name())
                .build();
    }
}
