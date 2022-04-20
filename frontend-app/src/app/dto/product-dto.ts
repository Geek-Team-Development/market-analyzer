export class ProductDto {
  constructor(public id: string,
              public name: string,
              public price: string,
              public shopName: string,
              public productLink: string,
              public imageLink: string) {
  }

  equals(productDto: ProductDto): boolean {
    return !(this.id !== productDto.id ||
      this.name !== productDto.name ||
      this.price !== productDto.price ||
      this.productLink !== productDto.productLink ||
      this.imageLink !== productDto.imageLink ||
      this.shopName !== productDto.shopName);

  }
}
