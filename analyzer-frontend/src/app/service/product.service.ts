import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from "@angular/common/http";
import {Observable} from "rxjs";
import {Page} from "../model/Page";
import {ProductFilter} from "../model/ProductFilter";

@Injectable({
  providedIn: 'root'
})
export class ProductService {

  constructor(private http: HttpClient) {
  }

  public findAll(productFilter?: ProductFilter): Observable<Page> {
    let params = new HttpParams();
    if (productFilter?.searchName != null) {
      params = params.set('searchName', productFilter?.searchName);
    }
    return this.http.get<Page>('/product', {params: params});
  }
}
