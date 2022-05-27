import {ProductDto} from "../dto/product-dto";

export class Util {
  public static mustBeDefinedErrorMessage(fieldName: string) : string {
    return fieldName + " обязательно для заполнения";
  }

  public static parseProduct(data: string): ProductDto {
    const json = JSON.parse(data);
    let productDto = new ProductDto();
    productDto.id = json['id'];
    productDto.name = json['name'];
    productDto.price = json['price'];
    productDto.shopName = json['shopName'];
    productDto.productLink = json['productLink'];
    productDto.imageLink = json['imageLink'];
    return productDto;
  }
}
