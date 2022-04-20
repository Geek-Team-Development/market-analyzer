import {Component, NgZone, OnInit} from '@angular/core';
import {ProductDto} from "../../dto/product-dto";
import {Observable} from "rxjs";
import {SearchProducts} from "../../config/backend-urls";
import {HttpParams} from "@angular/common/http";
import {EventSourceService} from "../../services/event-source.service";
import {ProductService} from "../../services/product.service";
import {Logos} from "../../config/front-config";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  searchName: string = '';
  productsArray: ProductDto[] = [];
  isWaiting: boolean = false;

  constructor(private productService: ProductService,
              private logos: Logos) {
  }

  ngOnInit(): void {

  }

  search() {
    if(this.searchName !== '') {
      this.productsArray = [];
      this.isWaiting = true;
      this.productService.getProducts(this.searchName)
        .subscribe({
          next: value => {
            this.productsArray.push(value);
          }, error: e => {
            console.log(e);
            this.isWaiting = false;
          }, complete: () => {
            this.isWaiting = false;
          }
        });
    }
  }

  getLogo(shopName: string) {
    return this.logos.logos.get(shopName);
  }
}
