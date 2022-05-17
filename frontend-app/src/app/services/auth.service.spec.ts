import {TestBed} from '@angular/core/testing';

import {AuthService} from './auth.service';
import {HttpErrorResponse} from "@angular/common/http";
import {UserDto} from "../dto/user-dto";
import {Credentials} from "../model/credentials";
import {AuthResult} from "../model/auth-result";
import {SIGN_IN, SIGN_UP} from "../config/backend-urls";
import {HttpClientTestingModule, HttpTestingController} from "@angular/common/http/testing";

describe('AuthService', function () {
  let authService: AuthService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [ AuthService ]
    });
    authService = TestBed.inject(AuthService);
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });
});

describe('Sign up', () => {
  let authService: AuthService;
  let controller: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [ AuthService ]
    });
    authService = TestBed.inject(AuthService);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    controller.verify();
  });

  it('successful registration', () => {
    let userDto = ExpectedData.getUserDto();
    authService.redirectUrl = 'testRedirect';
    let mockedResponse: Credentials = { email: userDto.email, password: '' }
    let expectedAuthResult = new AuthResult(mockedResponse, authService.redirectUrl);

    let actualAuthResult: AuthResult | undefined;

    authService.signUp(userDto)
      .subscribe({
        next: authResult => {
          actualAuthResult = authResult;
        },
        error: () => fail('error handler must not be called')
      });

    const request = controller.expectOne(
      (requestCandidate) =>
        requestCandidate.method === 'POST' &&
        requestCandidate.url === SIGN_UP &&
        requestCandidate.body === userDto,
    );
    request.flush(mockedResponse);

    expect(actualAuthResult)
      .withContext('expected auth result')
      .toEqual(expectedAuthResult);
  });

  it('registration with user exist exception', () => {

    const status = 500;
    const statusText = 'User is already exist';
    const errorEvent = new ProgressEvent('User is already exist');

    let actualError: HttpErrorResponse | undefined;

    let userDto = ExpectedData.getUserDto();

    authService.signUp(userDto)
      .subscribe({
        next: () => fail('expected an error, user is already exist'),
        error: err => {
          actualError = err;
        }
      });

    const request = controller.expectOne(
      (requestCandidate) =>
        requestCandidate.method === 'POST' &&
        requestCandidate.url === SIGN_UP &&
        requestCandidate.body === userDto,
    );
    request.error(errorEvent, {status, statusText});

    if(!actualError) {
      throw new Error('Error needs to be defined');
    }

    Verify.expectedError(actualError, errorEvent, status, statusText);
  });
});

describe('Sign in', () => {
  let authService: AuthService;
  let controller: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ HttpClientTestingModule ],
      providers: [ AuthService ]
    });
    authService = TestBed.inject(AuthService);
    controller = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    controller.verify();
  });

  it('successful sign in', () => {
    let credentials = ExpectedData.getCredentials();
    let expectedAuthorizationHeader = 'Basic ' + btoa(credentials.email + ':' + credentials.password)
    let mockedResponse: Credentials = { email: credentials.email, password: '' }
    authService.redirectUrl = 'testRedirect';
    let expectedAuthResult = new AuthResult(mockedResponse, authService.redirectUrl);

    let actualAuthResult: AuthResult | undefined;

    authService.signIn(credentials)
      .subscribe({
        next: authResult => {
          actualAuthResult = authResult;
        },
        error: () => fail('error handler must not be called')
      });

    const request = controller.expectOne(
      (requestCandidate) =>
        requestCandidate.method === 'GET' &&
        requestCandidate.url === SIGN_IN &&
        requestCandidate.headers.get('authorization') === expectedAuthorizationHeader,
    );
    request.flush(mockedResponse);

    expect(actualAuthResult)
      .withContext('expected auth result')
      .toEqual(expectedAuthResult);
  });

  it('sign in with user not found error', () => {

    const status = 404;
    const statusText = 'User not found';
    const errorEvent = new ProgressEvent('User not found');

    let actualError: HttpErrorResponse | undefined;

    let credentials = ExpectedData.getCredentials();
    let expectedAuthorizationHeader = 'Basic ' + btoa(credentials.email + ':' + credentials.password)

    authService.signIn(credentials)
      .subscribe({
        next: () => fail('expected an error, user not found'),
        error: err => {
          actualError = err;
        },
        complete: () => fail('complete handler must not be called')
      });

    const request = controller.expectOne(
      (requestCandidate) =>
        requestCandidate.method === 'GET' &&
        requestCandidate.url === SIGN_IN &&
        requestCandidate.headers.get('authorization') === expectedAuthorizationHeader,
    );

    request.error(errorEvent, {status, statusText});

    if(!actualError) {
      throw new Error('Error needs to be defined');
    }

    Verify.expectedError(actualError, errorEvent, status, statusText);
  });
})

class Verify {
  static expectedError(actualError: HttpErrorResponse,
                       errorEvent: ProgressEvent,
                       status: number,
                       statusText: string) {
    expect(actualError.error).toBe(errorEvent);
    expect(actualError.status).toBe(status);
    expect(actualError.statusText).toBe(statusText);
  }
}

class ExpectedData {
  static getUserDto() {
    let userDto = new UserDto();
    userDto.email = 'test@mail.ru';
    userDto.password = 'password';
    userDto.firstName = 'testFirstName';
    userDto.lastName = 'testLastName';
    userDto.city = 'testCity';
    return userDto;
  }

  static getCredentials() {
    let credentials : Credentials = {
      email: ExpectedData.getUserDto().email,
      password: ExpectedData.getUserDto().password
    }
    return credentials;
  }
}
