package ru.manalyzer.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.manalyzer.utility.CardButtonCommand;
import ru.manalyzer.storage.entity.ProductCardSlider;
import ru.manalyzer.storage.repository.ProductCardSliderRepository;
import ru.manalyzer.telegram.SpacePriceBotSender;
import ru.manalyzer.utility.CardSliderNavigator;

import java.util.Optional;

@Service
public class TelegramServiceImpl implements TelegramService {

    private final ProductService productService;

    private final ProductCardSliderRepository productCardSliderRepository;

    private final SpacePriceBotSender spacePriceBotSender;

    public TelegramServiceImpl(ProductService productService,
                               ProductCardSliderRepository productCardSliderRepository,
                               SpacePriceBotSender spacePriceBotSender) {
        this.productService = productService;
        this.productCardSliderRepository = productCardSliderRepository;
        this.spacePriceBotSender = spacePriceBotSender;
    }

    @Override
    public void newSearchRequest(Message message) {
        String chatId = message.getChatId().toString();

        // Disable old Card slider if exist
        productCardSliderRepository.findById(chatId).ifPresent(productCardSlider -> {
            productCardSlider.disable();
            spacePriceBotSender.sendProductCardSlider(productCardSlider);
        });

        // Create new product card slider
        ProductCardSlider productCardSlider = new ProductCardSlider(chatId);
        productCardSliderRepository.save(productCardSlider);

        // find products
        productService.findProducts(message.getText())
                .subscribe(productDto -> {
                    productCardSlider.getProducts().add(productDto);
                    Optional<Message> msg = spacePriceBotSender.sendProductCardSlider(productCardSlider);

                    // if message is first, then set messageId to product card slider
                    msg.ifPresent(m -> productCardSlider.setMessageId(m.getMessageId()));

                    productCardSliderRepository.save(productCardSlider);
                });
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
        }
    }

    private void sendAndSaveCardSlider(ProductCardSlider productCardSlider) {
        spacePriceBotSender.sendProductCardSlider(productCardSlider);
        productCardSliderRepository.save(productCardSlider);
    }

}
