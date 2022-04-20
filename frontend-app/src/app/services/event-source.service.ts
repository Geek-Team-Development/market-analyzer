import { Injectable } from '@angular/core';
import {SearchProducts} from "../config/backend-urls";
import {HttpParams} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class EventSourceService {

  constructor(private searchProducts: SearchProducts) { }

  getEventSource(searchName: string): EventSource {
    const params = new HttpParams()
      .set(this.searchProducts.paramNames.SEARCH_NAME, searchName);
    const fullUrl = `${this.searchProducts.url}?${params.toString()}`;
    return new EventSource(fullUrl);
  }
}
