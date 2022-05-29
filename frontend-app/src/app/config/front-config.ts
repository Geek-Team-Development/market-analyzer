import {Injectable} from "@angular/core";

export const APP_NAME = 'Space Price';

export class FrontUrls {
  static readonly MAIN = 'main';
  static readonly SIGN_UP = 'signup';
  static readonly SIGN_IN = 'signin';
  static readonly FAVORITES = 'favorites';
  static readonly USERS = 'users';
  static readonly NOTIFICATIONS = 'notifications';
  static readonly PROFILE = FrontUrls.USERS + '/:id';
}

@Injectable({
  providedIn: 'root'
})
export class Logos {
  public logos = new Map<string, string>();
  constructor() {
    this.logos.set('MVideo', 'https://cms.mvideo.ru/magnoliaPublic/dam/jcr:3c0c7e7e-d07f-4ecd-aa6d-6c4d11cda8f7');
    this.logos.set('Oldi', 'https://img.oldi.ru/bitrix/templates/oldi_new/images/logo.png');
    this.logos.set('Citilink', 'https://upload.wikimedia.org/wikipedia/ru/e/e7/%D0%9B%D0%BE%D0%B3%D0%BE%D1%82%D0%B8%D0%BF_%D0%A1%D0%B8%D1%82%D0%B8%D0%BB%D0%B8%D0%BD%D0%BA.svg')
  }
}
