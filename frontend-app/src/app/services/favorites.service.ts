import {Injectable, NgZone} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {ProductDto} from "../dto/product-dto";
import {FAVORITES} from "../config/backend-urls";
import {Observable, Observer} from "rxjs";
import {Util} from "../utils/util";

@Injectable({
  providedIn: 'root'
})
export class FavoritesService {

  constructor(private http: HttpClient,
              private ngZone: NgZone) { }

  add(product: ProductDto) {
    return this.http.post(FAVORITES, product);
  }

  getAll(): Observable<ProductDto> {
    return new Observable<ProductDto>((
      observer => {
        let source = new EventSource(FAVORITES);
        return this.subscriber(observer, source);
      }
    ));
  }

  remove(productId: string, shopName: string) {
    let url = FAVORITES + `/${productId}/${shopName}`;
    return this.http.delete(url);
  }

  clear() {
    return this.http.delete(FAVORITES);
  }

  private subscriber(observer: Observer<ProductDto>, source: EventSource) {
    source.onmessage = event => {
      let productDto = Util.parseProduct(event.data);
      this.ngZone.run(() => {
        observer.next(productDto);
      })
    };
    source.onerror = error => {
      this.ngZone.run(() => {
        observer.error(error)
      })
    };
    return () => source.close();
  }
}
