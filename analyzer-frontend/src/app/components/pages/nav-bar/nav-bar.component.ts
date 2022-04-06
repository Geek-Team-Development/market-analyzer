import {Component, EventEmitter, OnInit, Output} from '@angular/core';
import {ProductFilter} from "../../../model/ProductFilter";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit {

  @Output() filterApplied = new EventEmitter<ProductFilter>();

  productFilter: ProductFilter = new ProductFilter("");

  constructor() {
  }

  ngOnInit(): void {
  }

  applyFilter() {
    this.filterApplied.emit(this.productFilter);
  }

}
