import {Component, OnInit} from '@angular/core';
import {ProductDto} from "../../dto/product-dto";
import {ProductService} from "../../services/product.service";
import {Sort} from "../../dto/sort";

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  products: ProductDto[] = [];
  isWaiting: boolean = false;

  public sorts = new Map<Sort, string>([
    [Sort.PRICE_ASC, 'по возрастанию цены'],
    [Sort.PRICE_DESC, 'по убыванию цены']
  ]);

  searchName: string = '';
  private currentPage: number = 0;
  public currentSort = Sort.PRICE_ASC;

  constructor(private productService: ProductService) { }

  ngOnInit(): void { }

  search() {
    if (this.searchName !== '') {
      this.reset();
      this.findProducts();
    }
  }

  findProducts() {
    this.isWaiting = true;
    this.productService.getProducts(this.searchName, Sort[this.currentSort], this.currentPage)
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

  nextPage() {
    this.currentPage++;
    this.findProducts();
  }

  selectionChange(value: string) {
    this.currentSort = +value;
    this.search();
  }

  public getCurrentSort() {
    return this.sorts.get(this.currentSort);
  }

  private reset() {
    this.products = [];
    this.currentPage = 0;
  }
}
