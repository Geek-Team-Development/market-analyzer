import {ComponentFixture, fakeAsync, flush, TestBed, tick} from '@angular/core/testing';

import {NavBarComponent} from './nav-bar.component';
import {RouterTestingModule} from "@angular/router/testing";
import {AuthService} from "../../services/auth.service";
import {NavigationEnd, Router, Routes} from "@angular/router";
import {FrontUrls} from "../../config/front-config";
import {filter} from "rxjs";
import {MatMenuModule} from "@angular/material/menu";
import {MatDividerModule} from "@angular/material/divider";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {click, debugElement} from "../../../test-utils/util";

describe('NavBarComponent', () => {
  let component: NavBarComponent;
  let fixture: ComponentFixture<NavBarComponent>;
  let authServiceSpy = jasmine.createSpyObj('AuthService',
    ['logout', 'getUsername', 'isAuthenticated']);
  let isAuthSpy = authServiceSpy.isAuthenticated;
  let router: Router;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports:[
        RouterTestingModule.withRoutes([
          { path: '', pathMatch: 'full', redirectTo: FrontUrls.MAIN },
          { path: FrontUrls.MAIN, component: {} },
          { path: FrontUrls.SIGN_IN, component: {} },
          { path: FrontUrls.SIGN_UP, component: {} },
          { path: FrontUrls.FAVORITES, component: {} }
        ] as Routes),
        MatMenuModule,
        MatDividerModule,
        BrowserAnimationsModule
      ],
      declarations: [ NavBarComponent ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();
    router = TestBed.inject(Router);
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(NavBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create',() => {
    expect(component).toBeTruthy();
    router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(value => {
      let currentUrl = (value as NavigationEnd).url;
      expect(ExpectedUrls.mainUrl).toEqual(currentUrl);
    });
    expect(component.currentUrl).toEqual(component.main);
  });

  it('should navigating by menu', () => {
    isAuthSpy.and.returnValue(false);
    fixture.detectChanges();
    expect(debugElement(fixture, '[data-tid="user-menu"]'))
      .toBeNull();
    RoutingChecker.check(fixture, '[data-tid="big-sign-in"]',
      ExpectedUrls.signInUrl, router);
    RoutingChecker.check(fixture, '[data-tid="big-sign-up"]',
      ExpectedUrls.signUpUrl, router);
    RoutingChecker.check(fixture, '[data-tid="big-main"]',
      ExpectedUrls.mainUrl, router);
    RoutingChecker.check(fixture, '[data-tid="mobile-sign-in"]',
      ExpectedUrls.signInUrl, router);
    RoutingChecker.check(fixture, '[data-tid="mobile-sign-up"]',
      ExpectedUrls.signUpUrl, router);
    RoutingChecker.check(fixture, '[data-tid="mobile-main"]',
      ExpectedUrls.mainUrl, router);
  });

  it('should navigate by user menu when authenticated', async () => {
    authServiceSpy.getUsername.and.returnValue('user');
    authServiceSpy.isAuthenticated.and.returnValue(true);
    authServiceSpy.logout.and.return;
    fixture.detectChanges();
    expect(debugElement(fixture, '[data-tid="big-sign-in"]'))
      .toBeNull();
    expect(debugElement(fixture, '[data-tid="user-menu"]'))
      .not.toBeNull();
    click(fixture, '[data-tid="user-menu-button"]');
    RoutingChecker.check(fixture, '[data-tid="favorites"]',
      ExpectedUrls.favoriteUrl, router);
    click(fixture, '[data-tid="user-menu-button"]');
    click(fixture, '[data-tid="logout"]');
    expect(authServiceSpy.logout).toHaveBeenCalled();
    router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe(value => {
      let currentUrl = (value as NavigationEnd).url;
      expect(ExpectedUrls.mainUrl).toEqual(currentUrl);
    });
  });
});

class RoutingChecker {
  static check(fixture: ComponentFixture<NavBarComponent>,
               selector: string, expectedUrl: string, router: Router) {
    fakeAsync(() => {
      click(fixture, selector);
      tick();
      expect(router.url)
        .withContext(`${router.url} equals ${expectedUrl}`)
        .toEqual(expectedUrl);
      flush();
    }).call(null);
  }
}

class ExpectedUrls {
  static mainUrl = '/' + FrontUrls.MAIN;
  static signInUrl = '/' + FrontUrls.SIGN_IN;
  static signUpUrl = '/' + FrontUrls.SIGN_UP;
  static favoriteUrl = '/' + FrontUrls.FAVORITES;
}
