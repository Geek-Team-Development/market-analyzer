import {ComponentFixture, TestBed} from '@angular/core/testing';
import {MainComponent} from './main.component';
import {ProductService} from "../../services/product.service";
import {Observable} from "rxjs";
import {ProductDto} from "../../dto/product-dto";
import {FormsModule} from "@angular/forms";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import SpyObj = jasmine.SpyObj;
import {ProductCardComponent} from "../../components/product-card/product-card.component";
import {By} from "@angular/platform-browser";

describe('MainComponent', () => {
  let component: MainComponent;
  let fixture: ComponentFixture<MainComponent>;
  let productServiceSpy = jasmine.createSpyObj<ProductService>('ProductService', ['getProducts']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        FormsModule,
        HttpClientTestingModule
      ],
      declarations: [ MainComponent, ProductCardComponent ],
      providers: [
        MainComponent,
        { provide: ProductService, useValue: productServiceSpy }
      ]
    }).compileComponents();
    productServiceSpy = TestBed.inject(ProductService) as SpyObj<ProductService>;
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MainComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should return correct products', function () {
    let expectedProducts = ExpectedProducts.getProducts();
    let getProductSpy = productServiceSpy.getProducts;
    getProductSpy.and.returnValue(new Observable<ProductDto>((
      observer => {
        expectedProducts.forEach(productDto => {
          observer.next(productDto);
        })
      }
    )));
    let searchName = 'Телевизоры';
    const compiled = fixture.nativeElement as HTMLElement;
    let htmlInputElement = compiled.querySelector('input')!;
    htmlInputElement.value = searchName;
    htmlInputElement.dispatchEvent(new Event('input'));
    fixture.detectChanges();
    component.search();

    expect(getProductSpy)
      .withContext('#getProduct called with param ' + `${searchName}`)
      .toHaveBeenCalledWith(searchName);

    expect(component.products.length)
      .withContext('products in component have size ' + `${expectedProducts.length}`)
      .toBe(expectedProducts.length);

    for(let i = 0; i < expectedProducts.length; i++) {
      expect(component.products[i])
        .withContext(`${component.products[i]}` + ' equal ' + `${expectedProducts[i]}`)
        .toEqual(expectedProducts[i]);
    }
  });

  it('should assign correct products in product cards', () => {
    let expectedProducts = ExpectedProducts.getProducts();
    component.products = expectedProducts;
    fixture.detectChanges();
    let productCards = fixture.debugElement.queryAll(By.css('app-product-card'));
    expect(productCards.length)
      .withContext(`Product cards length must be ${expectedProducts.length}`)
      .toBe(component.products.length);

    for(let i = 0; i < expectedProducts.length; i++) {
      let productCard = productCards[i].componentInstance;
      expect(productCard.product)
        .withContext(`${productCard.product} must be ${expectedProducts[i]}`)
        .toEqual(expectedProducts[i]);
    }
  });
});

class ExpectedProducts {
  static getProducts(): ProductDto[] {
    let products = [];
    let productDto = new ProductDto();
    productDto.id = '1';
    productDto.name = 'Телевизор Samsung';
    productDto.price = '10000';
    productDto.shopName = 'Oldi';
    productDto.productLink = 'https://www.products.tv_samsung';
    productDto.imageLink = 'https://www.images.tv_samsung';
    products.push(productDto);

    productDto = new ProductDto();
    productDto.id = '2';
    productDto.name = 'Телевизор Haier';
    productDto.price = '15000';
    productDto.shopName = 'MVideo';
    productDto.productLink = 'https://www.products.tv_haier';
    productDto.imageLink = 'https://www.images.tv_haier';
    products.push(productDto);

    productDto = new ProductDto();
    productDto.id = '3';
    productDto.name = 'Телевизор HP';
    productDto.price = '20000';
    productDto.shopName = 'Citilink';
    productDto.productLink = 'https://www.products.tv_hp';
    productDto.imageLink = 'https://www.images.tv_hp';
    products.push(productDto);
    return products;
  }
}
