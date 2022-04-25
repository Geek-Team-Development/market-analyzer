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
    this.favoriteService.clear()
      .subscribe({
        next: value => {
          this.products = [];
        },
        error: err => {
          console.log(err);
        }
      })
  }

  productDeleted($event: ProductDto) {
    for(let i = 0; i < this.products.length; i++) {
      if(this.products[i].id == $event.id && this.products[i].shopName == $event.id) {
        delete this.products[i];
        break;
      }
    }
  }
}
