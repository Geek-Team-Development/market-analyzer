import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class SearchProducts {
  url = '/api/v1/product';
  paramNames = class {
    static SEARCH_NAME = 'searchName';
  }
}

export const SIGN_UP = '/api/v1/signup';
export const SIGN_IN = '/api/v1/signin';
export const LOGOUT = '/api/v1/logout';
