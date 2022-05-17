import {ComponentFixture, TestBed} from '@angular/core/testing';

import {SignInComponent} from './sign-in.component';
import {RouterTestingModule} from "@angular/router/testing";
import {AuthService} from "../../services/auth.service";
import {Routes} from "@angular/router";
import {setFieldValue, submit} from "../../../test-utils/util";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {Credentials} from "../../model/credentials";
import {of} from "rxjs";
import {AuthResult} from "../../model/auth-result";

describe('SignInComponent', () => {
  let component: SignInComponent;
  let fixture: ComponentFixture<SignInComponent>;
  let authServiceSpy = jasmine.createSpyObj<AuthService>('AuthService',
    ['logout', 'getUsername', 'isAuthenticated', 'signIn']);
  const testRedirectUrl = 'test';

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [
        RouterTestingModule.withRoutes([
          { path: testRedirectUrl, component: {} }
        ] as Routes),
        ReactiveFormsModule,
        FormsModule
      ],
      declarations: [ SignInComponent ],
      providers: [
        { provide: AuthService, useValue: authServiceSpy }
      ]
    })
    .compileComponents();
    fixture = TestBed.createComponent(SignInComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('successful sign in', () => {
    let signInSpy = authServiceSpy.signIn;
    let email = 'test@test.ru';
    let password = 'password';
    let credentials : Credentials = { email: email, password: password }
    signInSpy.and.returnValue(of(new AuthResult(credentials, testRedirectUrl)));
    setFieldValue(fixture, '[data-tid="email"]', email);
    setFieldValue(fixture, '[data-tid="password"]', password);
    submit(fixture, '[data-tid="form"]');
    expect(signInSpy)
      .toHaveBeenCalledWith(credentials);
  });
});
