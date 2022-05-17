import {ComponentFixture, TestBed} from '@angular/core/testing';

import {ProductCardComponent} from './product-card.component';
import {ProductDto} from "../../dto/product-dto";
import {FavoritesService} from "../../services/favorites.service";
import {first, of} from "rxjs";
import {click} from "../../../test-utils/util";
import any = jasmine.any;

describe('ProductCardComponent', () => {
  let component: ProductCardComponent;
  let fixture: ComponentFixture<ProductCardComponent>;
  let favoriteServiceSpy = jasmine.createSpyObj<FavoritesService>('FavoriteService', ['add', 'remove']);

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
    click(fixture, '[data-tid="add-favorites-btn"]');
    expect(addSpy)
      .withContext(`add on favorite service called with ${expectedProduct}`)
      .toHaveBeenCalledWith(expectedProduct);
  });

  it('should emit product remove event from favorites' +
    ' when removing on server is successful', () => {
    let expectedProduct = TestingProduct.getProduct();
    component.product = expectedProduct;
    component.isAddedToFavorites = true;
    let removeSpy = favoriteServiceSpy.remove;
    removeSpy.and.returnValue(of(any(Object)));
    fixture.detectChanges();
    component.productDeleted
      .pipe(first())
      .subscribe(value => {
        expect(value).toEqual(expectedProduct);
      });
    click(fixture, '[data-tid="remove-favorites-btn"]');
    expect(removeSpy.calls.count()).toEqual(1);
    expect(removeSpy)
      .toHaveBeenCalledWith(expectedProduct.id, expectedProduct.shopName);
  })
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
