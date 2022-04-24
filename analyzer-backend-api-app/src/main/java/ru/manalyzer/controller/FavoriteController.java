package ru.manalyzer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.manalyzer.dto.ProductDto;
import ru.manalyzer.service.FavoritesService;

@Secured({"ROLE_USER", "ROLE_ADMIN"})
@RestController
@RequestMapping("/favorites")
public class FavoriteController {

    private final FavoritesService favoritesService;

    @Autowired
    public FavoriteController(FavoritesService favoritesService) {
        this.favoritesService = favoritesService;
    }

    @GetMapping
    public Flux<ProductDto> getFavoritesCart(Authentication authentication) {
        return favoritesService.getFavoritesCartOfUser(((User) authentication.getPrincipal()).getUsername());
    }

    @PostMapping(consumes = "application/json")
    public void addProductToFavoritesCart(@RequestBody ProductDto productDto, Authentication authentication) {
        favoritesService.saveProductToFavoritesCart(productDto, ((User) authentication.getPrincipal()).getUsername());
    }
}
