import {Injectable, NgZone} from '@angular/core';
import {EventSourceService} from "./event-source.service";
import {ProductDto} from "../dto/product-dto";
import {Observable} from "rxjs";
import {Util} from "../utils/util";
import {Sort} from "../dto/sort";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(private eventSourceService: EventSourceService,
              private ngZone: NgZone) { }

  getProducts(searchName: string, sort: string, pageNumber: number) : Observable<ProductDto> {
    return new Observable<ProductDto>((
      observer => {
        let source = this.eventSourceService.getEventSource(searchName, sort.toLowerCase(), pageNumber);
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
    ));
  }
}
