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

  public findAll(productFilter?: ProductFilter, page?: number): Observable<Page> {
    let params = new HttpParams();
    if (productFilter?.namePattern != null) {
      params = params.set('namePattern', productFilter?.namePattern);
    }
    params = params.set("page", page != null ? page : 1);
    params = params.set("size", 6);
    return this.http.get<Page>('/product', {params: params});
  }
}
