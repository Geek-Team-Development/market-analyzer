package ru.manalyzer.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.manalyzer.dto.PriceNotificationDto;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.dto.UserDto;
import ru.manalyzer.property.MessageProperties;
import ru.manalyzer.telegram.BotSender;
import ru.manalyzer.telegram.command.CardButtonCommand;
import ru.manalyzer.telegram.command.CommandParser;
import ru.manalyzer.telegram.slider.CardSliderNavigator;
import ru.manalyzer.storage.entity.ProductCardSlider;
import ru.manalyzer.storage.repository.ProductCardSliderRepository;

import java.util.List;
import java.util.Optional;

@Service
public class TelegramServiceImpl implements TelegramService {

    private final ProductSearchService productService;

    private final DatabaseFavoritesService databaseFavoritesService;

    private final DatabaseUserService databaseUserService;

    private final ProductCardSliderRepository productCardSliderRepository;

    private final BotSender botSender;

    private final CommandParser commandParser;

    private final MessageProperties messageProperties;

    public TelegramServiceImpl(ProductSearchService productService,
                               DatabaseFavoritesService databaseFavoritesService,
                               DatabaseUserService databaseUserService,
                               ProductCardSliderRepository productCardSliderRepository,
                               BotSender botSender,
                               CommandParser commandParser,
                               MessageProperties messageProperties) {
        this.productService = productService;
        this.databaseFavoritesService = databaseFavoritesService;
        this.databaseUserService = databaseUserService;
        this.productCardSliderRepository = productCardSliderRepository;
        this.botSender = botSender;
        this.commandParser = commandParser;
        this.messageProperties = messageProperties;
    }

    @Override
    public void newSearchRequest(Message message) {
        String chatId = message.getChatId().toString();

        // Disable old Card slider if exist
        disableProductCardSlider(chatId);

        // Create new product card slider
        ProductCardSlider productCardSlider = newProductCardSlider(chatId);

        // find products
        productService.findProducts(message.getText())
                .subscribe(productDto -> addProductToCardSlider(productCardSlider, productDto));
    }

    private void disableProductCardSlider(String chatId) {
        Optional<ProductCardSlider> productCardSliderOpt = productCardSliderRepository.findById(chatId);
        if (productCardSliderOpt.isPresent()) {
            ProductCardSlider productCardSlider = productCardSliderOpt.get();
            productCardSlider.disable();
            botSender.sendProductCardSlider(productCardSlider);
        }
    }

    private ProductCardSlider newProductCardSlider(String chatId) {
        ProductCardSlider productCardSlider = new ProductCardSlider(chatId);
        return productCardSliderRepository.save(productCardSlider);
    }

    private void addProductToCardSlider(ProductCardSlider productCardSlider, ProductDto productDto) {
        productCardSlider.getProducts().add(productDto);
        Optional<Message> msg = botSender.sendProductCardSlider(productCardSlider);

        // if message is first, then set messageId to product card slider
        msg.ifPresent(m -> productCardSlider.setMessageId(m.getMessageId()));

        productCardSliderRepository.save(productCardSlider);
    }

    @Override
    public void callbackRequest(CallbackQuery callbackQuery) {
        String chatId = callbackQuery.getFrom().getId().toString();
        ProductCardSlider productCardSlider = productCardSliderRepository.findById(chatId)
                .orElseThrow(IllegalArgumentException::new);
        CardSliderNavigator cardSliderNavigator = productCardSlider.navigator();

        switch (CardButtonCommand.valueOf(callbackQuery.getData())) {
            case NEXT:
                if (cardSliderNavigator.next()) {
                    sendAndSaveCardSlider(productCardSlider);
                }
                break;
            case PREVIOUS:
                if (cardSliderNavigator.previous()) {
                    sendAndSaveCardSlider(productCardSlider);
                }
                break;
            case ADD_FAVORITES:
                databaseFavoritesService.saveProductToFavoritesCart(productCardSlider);
                botSender.sendProductCardSlider(productCardSlider);
                break;
            case REMOVE_FAVORITES:
                databaseFavoritesService.deleteProductFromFavoritesCart(productCardSlider);
                botSender.sendProductCardSlider(productCardSlider);
                break;
        }
    }

    @Override
    public void commandRequest(Message message) {
            switch (commandParser.parseCommand(message)) {
                case START:
                    botSender.sendInformationMessage(message.getChatId().toString(), messageProperties.getGreetings());
                    break;
                case AUTHORIZATION:
                    break;
                case FAVORITES:
                    sendFavorites(message);
                    break;
            }

    }

    @Override
    public void authorizeRequest(UserDto userDto) {
        botSender.sendInformationMessage(userDto.getTelegramChatId(), messageProperties.getAuthorizedOk());
        productCardSliderRepository.findById(userDto.getTelegramChatId())
                .ifPresent(botSender::sendProductCardSlider);
    }

    @Override
    public void priceNotificationRequest(PriceNotificationDto priceNotificationDto) {
        botSender.sendPriceNotification(priceNotificationDto.getChatId(), priceNotificationDto.getProductDto());
    }

    private void sendAndSaveCardSlider(ProductCardSlider productCardSlider) {
        botSender.sendProductCardSlider(productCardSlider);
        productCardSliderRepository.save(productCardSlider);
    }

    private void sendFavorites(Message message) {
        String chatId = message.getChatId().toString();
        String userId = databaseUserService.findUserIdByChatId(chatId)
                .orElseThrow(() -> new RuntimeException("User not authorized"));
        List<ProductDto> products = databaseFavoritesService.getUserFavoriteProduct(userId);

        if (products.isEmpty()) {
            botSender.sendInformationMessage(chatId, messageProperties.getFavoritesEmpty());
            return;
        }

        // Disable old Card slider if exist
        disableProductCardSlider(chatId);

        // Create new product card slider
        ProductCardSlider productCardSlider = newProductCardSlider(chatId);

        // find favorites
        products.forEach(productDto -> addProductToCardSlider(productCardSlider, productDto));
    }
}
