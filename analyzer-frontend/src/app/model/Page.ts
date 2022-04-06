import {Product} from "./Product";

export class Page {

  constructor(public content: Product[],
              public last: boolean,
              public totalPages: number,
              public totalElements: number,
              public numberOfElements: number,
              public number: number,
              public first: boolean,
              public size: number,
              public empty: boolean) {
  }
}
