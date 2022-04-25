import {TestBed} from '@angular/core/testing';
import {RouterTestingModule} from '@angular/router/testing';
import {AppComponent} from './app.component';
import {Component} from "@angular/core";
import {APP_NAME} from "./config/front-config";
import {AuthService} from "./services/auth.service";
import {of, throwError} from "rxjs";
import {AuthResult} from "./model/auth-result";
import {HttpClientTestingModule} from "@angular/common/http/testing";
import any = jasmine.any;
import Spy = jasmine.Spy;

describe('Testing AppComponent creating', () => {

  let authServiceSpy: any;
  let signInSpy: Spy;
  let logoutSpy: Spy;

  beforeEach(() => {
    authServiceSpy = jasmine.createSpyObj('AuthService',
      ['signIn', 'logout']);
    signInSpy = authServiceSpy.signIn;
    logoutSpy = authServiceSpy.logout;
  });

  it('should logout when user is not authenticated on server', function () {
    signInSpy.and.returnValue(throwError(() => new Error('null')));
    new AppComponent(authServiceSpy);
    expect(signInSpy.calls.count())
      .withContext('signIn method was called once')
      .toBe(1);
    expect(logoutSpy.calls.count())
      .withContext('logout method was called once')
      .toBe(1);
  });

  it('should not logout when user is authenticated in server', () => {
    signInSpy.and.returnValue(of(any(AuthResult)));
    new AppComponent(authServiceSpy);
    expect(signInSpy.calls.count())
      .withContext('signIn method was called once')
      .toBe(1);
    expect(logoutSpy.calls.count())
      .withContext('logout method was not called')
      .toBe(0);
  })
});

describe('AppComponent', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        RouterTestingModule,
        HttpClientTestingModule
      ],
      declarations: [
        AppComponent,
        MockAppNavBarComponent
      ]
    });
  });

  const appName = APP_NAME;

  it('should create the AppComponent;' +
    ' should have have app-nav-bar, router-outlet;' +
    ' should have as title ' + `${appName}`, () => {
    const fixture = TestBed.createComponent(AppComponent);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
    const compiled = fixture.nativeElement as HTMLElement;
    expect(compiled.querySelector('app-nav-bar'))
      .withContext('<app-nav-bar> is existing')
      .toBeTruthy();
    expect(compiled.querySelector('router-outlet'))
      .withContext('<router-outlet> is existing')
      .toBeTruthy();
    expect(app.title)
      .withContext('Title is equal ' + `${appName}`)
      .toEqual(appName);
  });
});

@Component({
  selector: 'app-nav-bar',
  template: ''
})
class MockAppNavBarComponent {}
