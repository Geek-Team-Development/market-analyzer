import {Component, OnInit} from '@angular/core';
import {Product} from "../../../model/Product";
import {ProductService} from "../../../service/product.service";
import {ProductFilter} from "../../../model/ProductFilter";
import {Page} from "../../../model/Page";

export const MAIN_PAGE = 'products';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  products: Product[] = [];
  productFilter?: ProductFilter;
  page?: Page;
  pageNumber: number = 1;

  constructor(public productService: ProductService) {
  }

  ngOnInit(): void {
    this.productService.findAll()
      .subscribe(res => {
        console.log("Loading products");
        this.page = res;
        this.products = res.content;
      }, err => {
        console.log(`Can't load products ${err}`);
      });
  }

  filterApplied($event: ProductFilter) {
    console.log($event);
    this.productFilter = $event;
    this.productService.findAll($event, 1).subscribe(res => {
      this.page = res;
      this.products = res.content;
      this.pageNumber = 1;
    }, error => console.error(`Can't load products ${error}`));
  }

}
