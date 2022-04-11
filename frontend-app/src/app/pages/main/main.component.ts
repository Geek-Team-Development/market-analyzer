import {Component, NgZone, OnInit} from '@angular/core';
import {ProductDto} from "../../dto/product-dto";
import {Observable} from "rxjs";

export const MAIN_URL = 'main';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrls: ['./main.component.scss']
})
export class MainComponent implements OnInit {

  searchName: string = '';
  productsArray: ProductDto[] = [];

  constructor(private zone: NgZone) {
  }

  ngOnInit(): void {

  }

  clickSubmit() {
    this.productsArray = [];
    this.getProducts()
      .subscribe(value => {
        this.productsArray.push(value);
      });
  }

  getProducts(): Observable<any> {
    return new Observable<any>((
      observer => {
        console.log('searchName = ' + this.searchName);
        let source = new EventSource('/api/v1/product?searchName=' + this.searchName);
        source.onmessage = event => {
          const json = JSON.parse(event.data);
          let productDto = new ProductDto(
            json['id'],
            json['name'],
            json['price'],
            json['shopName'],
            json['productLink'],
            json['imageLink']);
          this.zone.run(() => {
            observer.next(productDto);
          })
        };
        source.onerror = error => {
          this.zone.run(() => {
            observer.error(error)
          })
        };
        return () => source.close();
      }
    ))
  }
}
