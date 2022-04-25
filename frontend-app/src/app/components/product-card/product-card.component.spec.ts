import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ProductCardComponent} from './product-card.component';
import {ProductDto} from "../../dto/product-dto";
import {FavoritesService} from "../../services/favorites.service";
import {By} from "@angular/platform-browser";
import {of} from "rxjs";
import any = jasmine.any;

describe('ProductCardComponent', () => {
  let component: ProductCardComponent;
  let fixture: ComponentFixture<ProductCardComponent>;
  let favoriteServiceSpy = jasmine.createSpyObj<FavoritesService>('FavoriteService', ['add']);

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProductCardComponent ],
      providers: [
        { provide: FavoritesService, useValue: favoriteServiceSpy }
      ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProductCardComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should add product to favorites', () => {
    let expectedProduct = TestingProduct.getProduct();
    component.product = expectedProduct;
    let addSpy = favoriteServiceSpy.add;
    addSpy.and.returnValue(of(any(Object)));
    fixture.detectChanges();
    let favoriteButton = fixture.debugElement
      .query(By.css('.tw-block'));

    expect(favoriteButton)
      .withContext('favorite button is defined')
      .toBeTruthy();

    const event = Util.makeClickEvent(favoriteButton.nativeElement);
    favoriteButton.triggerEventHandler('click', event);
    expect(addSpy)
      .withContext(`add on favorite service called with ${expectedProduct}`)
      .toHaveBeenCalledWith(expectedProduct);
  });
});

class TestingProduct {
  static getProduct(): ProductDto {
    let productDto = new ProductDto();
    productDto.id = '1';
    productDto.name = 'Телевизор';
    productDto.price = '20000';
    productDto.shopName = 'Oldi';
    productDto.productLink = 'https://product.tv.ru';
    productDto.imageLink = 'https://img.oldi.ru';
    return productDto;
  }
}

class Util {

  static makeClickEvent(target: EventTarget) : Partial<MouseEvent> {
    return {
      preventDefault(): void { },
      stopPropagation(): void { },
      stopImmediatePropagation(): void { },
      type: 'click',
      target,
      currentTarget: target,
      bubbles: true,
      cancelable: true,
      button: 0
    }
  }
}
