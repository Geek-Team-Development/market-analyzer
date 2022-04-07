import {Component, OnInit} from '@angular/core';
import {Product} from "../../../model/Product";
import {ProductService} from "../../../service/product.service";
import {ProductFilter} from "../../../model/ProductFilter";

export const MAIN_PAGE = 'products';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  products: Product[] = [];
  productFilter?: ProductFilter;
  pageNumber: number = 1;

  constructor(public productService: ProductService) {
  }

  ngOnInit(): void {
  }

  filterApplied($event: ProductFilter) {
    console.log($event);
    this.productFilter = $event;
    this.productService.findAll($event).subscribe(res => {
      this.products = res.content;
      this.pageNumber = 1;
    }, error => console.error(`Can't load products ${error}`));
  }

}
