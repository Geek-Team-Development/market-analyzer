import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class SearchProducts {
  url = '/api/v1/product';
  paramNames = class {
    static SEARCH_NAME = 'searchName';
    static SORT_NAME = 'sort';
    static PAGE_NUMBER = 'pageNumber';
  }
}

export const SIGN_UP = '/api/v1/signup';
export const SIGN_IN = '/api/v1/signin';
export const LOGOUT = '/api/v1/logout';
export const FAVORITES = '/api/v1/favorites';
export const USERS = '/api/v1/users';
export const NOTIFICATIONS = '/api/v1/notifications';
