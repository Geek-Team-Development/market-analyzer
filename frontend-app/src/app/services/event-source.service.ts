import { Injectable } from '@angular/core';
import {SearchProducts} from "../config/backend-urls";
import {HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class EventSourceService {

  constructor(private searchProducts: SearchProducts) { }

  getEventSource(searchName: string, sort: string, pageNumber: number): EventSource {
    const params = new HttpParams()
      .set(this.searchProducts.paramNames.SEARCH_NAME, searchName)
      .set(this.searchProducts.paramNames.SORT_NAME, sort)
      .set(this.searchProducts.paramNames.PAGE_NUMBER, pageNumber);
    const fullUrl = `${this.searchProducts.url}?${params.toString()}`;
    return new EventSource(fullUrl);
  }
}
