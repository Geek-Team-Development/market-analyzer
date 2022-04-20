import {ComponentFixture, TestBed} from '@angular/core/testing';

import {MainComponent} from './main.component';
import {ProductService} from "../../services/product.service";
import {Observable} from "rxjs";
import {ProductDto} from "../../dto/product-dto";
import {FormsModule} from "@angular/forms";

let products = new Map<string, ProductDto>();

function setExpectedProducts() {
  products.set('1', new ProductDto('1',
    'Телевизор Samsung',
    '10000',
    'Oldi',
    'https://www.products.tv_samsung',
    'https://www.images.tv_samsung'));
  products.set('2', new ProductDto('2',
    'Телевизор Haier',
    '15000',
    'MVideo',
    'https://www.products.tv_haier',
    'https://www.images.tv_haier'));
  products.set('3', new ProductDto('3',
    'Телевизор HP',
    '20000',
    'Citilink',
    'https://www.products.tv_hp',
    'https://www.images.tv_hp'));
}

describe('MainComponent', () => {
  let component: MainComponent;
  let fixture: ComponentFixture<MainComponent>;

  let productService: ProductService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormsModule],
      declarations: [ MainComponent ],
      providers: [MainComponent, { provide: ProductService, useClass: MockProductService }]
    }).compileComponents();
    productService = TestBed.inject(ProductService);
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
    component.searchName = 'Телевизоры';
    setExpectedProducts();
    component.search();
    expect(component.productsArray.length).toBe(products.size);
    let result = new Map(component.productsArray.map(productDto => [productDto.id, productDto]));
    products.forEach(expectedProduct => {
      let resultProduct = result.get(expectedProduct.id);
      if(resultProduct) {
        expect(expectedProduct.equals(resultProduct)).toBeTrue();
      } else {
        throw new Error("result is undefined");
      }
    })
  });
});

class MockProductService {

  getProducts(searchName: string): Observable<ProductDto> {
    return new Observable<ProductDto>((
      observer => {
        products.forEach(productDto => {
          observer.next(productDto);
        })
      }
    ))
  }
}
