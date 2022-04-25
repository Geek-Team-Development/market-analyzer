import {Component, OnInit} from '@angular/core';
import {ProductDto} from "../../dto/product-dto";
import {FavoritesService} from "../../services/favorites.service";

@Component({
  selector: 'app-favorites',
  templateUrl: './favorites.component.html',
  styleUrls: ['./favorites.component.scss']
})
export class FavoritesComponent implements OnInit {

  products: ProductDto[] = [];

  constructor(private favoriteService: FavoritesService) { }

  ngOnInit(): void {
    this.getAll();
  }

  getAll() {
    this.favoriteService.getAll()
      .subscribe({
        next: response => {
          this.products.push(response);
        },
        error: err => {
          console.log(err);
        }
      })
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
