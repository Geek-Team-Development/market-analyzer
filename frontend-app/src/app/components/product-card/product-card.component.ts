import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {ProductDto} from "../../dto/product-dto";
import {Logos} from "../../config/front-config";
import {FavoritesService} from "../../services/favorites.service";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.scss']
})
export class ProductCardComponent implements OnInit {

  @Input() product: ProductDto = new ProductDto();
  @Input() isAddedToFavorites: boolean = false;
  @Output() productDeleted: EventEmitter<ProductDto> = new EventEmitter<ProductDto>();

  constructor(private logos: Logos,
              private favoriteService: FavoritesService,
              public authService: AuthService) { }

  ngOnInit(): void { }

  getLogo(shopName: string) {
    return this.logos.logos.get(shopName);
  }

  addToFavorites($event: MouseEvent) {
    $event.stopPropagation();
    $event.preventDefault();
    this.favoriteService.add(this.product)
      .subscribe({
        error: err => {
          console.log(err);
        }
      });
  }

  removeFromFavorites(product: ProductDto, $event: MouseEvent) {
    $event.stopPropagation();
    $event.preventDefault();
    this.favoriteService.remove(this.product.id, this.product.shopName)
      .subscribe({
        next: () => {
          this.productDeleted.emit(this.product);
        },
        error: err => {
          console.log(err);
        }
      });
  }
}
