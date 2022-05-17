package ru.manalyzer.storage.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.utility.CardSliderNavigator;
import ru.manalyzer.utility.Navigable;

import java.util.ArrayList;
import java.util.List;

@RedisHash("ProductCardSlider")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProductCardSlider implements Navigable {

    // chatId
    private String id;

    private Integer messageId;

    private List<ProductDto> products = new ArrayList<>();

    private int currentPosition;

    private boolean active = true;

    public ProductCardSlider(String id) {
        this.id = id;
    }

    public void disable() {
        active = false;
    }

    @Override
    public CardSliderNavigator navigator() {
        return new CardSliderNavigator() {
            @Override
            public boolean next() {
                if (hasNext()) {
                    incrementCurrentPosition();
                    return true;
                }
                return false;
            }

            @Override
            public boolean previous() {
                if (hasPrevious()) {
                    decrementCurrentPosition();
                    return true;
                }
                return false;
            }

            @Override
            public boolean hasNext() {
                return currentPosition < size() - 1;
            }

            @Override
            public boolean hasPrevious() {
                return currentPosition > 0;
            }

            @Override
            public int size() {
                return products.size();
            }

            @Override
            public ProductDto getCurrentProduct() {
                return products.get(currentPosition);
            }

            private void incrementCurrentPosition() {
                currentPosition++;
            }

            private void decrementCurrentPosition() {
                currentPosition--;
            }
        };
    }
}
