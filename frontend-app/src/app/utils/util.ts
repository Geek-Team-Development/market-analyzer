import {ProductDto} from "../dto/product-dto";

export class Util {
  public static mustBeDefinedErrorMessage(fieldName: string) : string {
    return fieldName + " обязательно для заполнения";
  }

  public static parseProduct(data: string): ProductDto {
    const json = JSON.parse(data);
    return new ProductDto(json['id'],
      json['name'],
      json['price'],
      json['shopName'],
      json['productLink'],
      json['imageLink']);
  }
}
