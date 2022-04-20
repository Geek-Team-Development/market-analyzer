import { Injectable } from '@angular/core';
import {UserDto} from "../dto/user-dto";
import {map, Observable} from "rxjs";
import {AuthResult} from "../model/auth-result";
import {Credentials} from "../model/credentials";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {LOGOUT, SIGN_IN, SIGN_UP} from "../config/backend-urls";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  public redirectUrl?: string;
  private currentUser?: Credentials;
  private currentUserKey = 'current_user';

  constructor(private http: HttpClient) {
    let data = localStorage.getItem(this.currentUserKey);
    if(data) {
      this.currentUser = JSON.parse(data);
    }
  }

  signUp(registerData: UserDto) : Observable<AuthResult> {
    return this.http.post(SIGN_UP, registerData)
      .pipe(
        map(response => {
          if ('email' in response) {
            let responseCredentials = response as Credentials;
            let credentials = new Credentials();
            credentials.email = responseCredentials.email;
            if(localStorage.getItem(this.currentUserKey)) {
              this.logout();
            }
            return new AuthResult(credentials, this.redirectUrl);
          }
          throw new Error(response.toString());
        })
      );
  }

  signIn(credentials: Credentials) : Observable<AuthResult> {
    const headers = new HttpHeaders(credentials ? {
      authorization: 'Basic ' + btoa(credentials.email + ':' + credentials.password) } : {});
    return this.http.get(SIGN_IN, { headers: headers })
      .pipe(
        map(response => {
          if('email' in response) {
            this.currentUser = response as Credentials;
            localStorage.setItem(this.currentUserKey, JSON.stringify(response));
            return new AuthResult(this.currentUser, this.redirectUrl);
          }
          throw new Error(response.toString());
        })
      );
  }

  logout() {
    if (this.isAuthenticated()) {
      this.currentUser = undefined;
      localStorage.removeItem(this.currentUserKey);
      this.http.post(LOGOUT, {}).subscribe();
    }
  }

  public isAuthenticated(): boolean {
    return !!this.currentUser;
  }
}