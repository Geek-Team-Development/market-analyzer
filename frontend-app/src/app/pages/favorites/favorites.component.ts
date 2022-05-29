import {Component, OnInit} from '@angular/core';
import {ProductDto} from "../../dto/product-dto";
import {FavoritesService} from "../../services/favorites.service";
import {MessageObserverService} from "../../services/message-observer.service";

@Component({
  selector: 'app-favorites',
  templateUrl: './favorites.component.html',
  styleUrls: ['./favorites.component.scss']
})
export class FavoritesComponent implements OnInit {

  products: ProductDto[] = [];

  constructor(private favoriteService: FavoritesService,
              private messageObserverService: MessageObserverService) { }

  ngOnInit(): void {
    this.getAll();
  }

  getAll() {
    this.favoriteService.getAll()
      .subscribe({
        next: response => {
          this.pushProduct(response);
        },
        error: err => {
          console.log(err);
        },
        complete: () => {
          this.messageObserverService.decrementAndNext();
        }
      })
  }

  pushProduct(product: ProductDto) {
    let index = this.products.findIndex(e => e.id === product.id && e.shopName === product.shopName);
    if (index === -1) {
      this.products.push(product);
    } else {
      this.products[index] = product;
    }
  }

  clear() {
    if(this.products.length > 0) {
      this.favoriteService.clear()
        .subscribe({
          next: () => {
            this.products = [];
          },
          error: err => {
            console.log(err);
          }
        });
    };
  }

  productDeleted($event: ProductDto) {
    this.products.forEach((product,index)=>{
      if(product.id == $event.id && product.shopName == $event.shopName) {
        this.products.splice(index, 1);
      }
    });
  }
}
