import {Component, OnInit} from '@angular/core';
import {ProductDto} from "../../dto/product-dto";
import {ProductService} from "../../services/product.service";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  searchName: string = '';
  products: ProductDto[] = [];
  isWaiting: boolean = false;

  constructor(private productService: ProductService) {
  }

  ngOnInit(): void { }

  search() {
    if (this.searchName !== '') {
      this.products = [];
      this.isWaiting = true;
      this.productService.getProducts(this.searchName)
        .subscribe({
          next: value => {
            this.products.push(value);
          }, error: e => {
            console.log(e);
            this.isWaiting = false;
          }, complete: () => {
            this.isWaiting = false;
          }
        });
    }
  }
}
