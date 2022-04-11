export class ProductDto {
  constructor(public id: string,
              public name: string,
              public price: string,
              public shopName: string,
              public productLink: string,
              public imageLink: string) {
  }
}
