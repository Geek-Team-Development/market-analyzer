import {Injectable} from '@angular/core';
import {UserDto} from "../dto/user-dto";
import {map, Observable} from "rxjs";
import {AuthResult} from "../model/auth-result";
import {Credentials} from "../model/credentials";
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {LOGOUT, SIGN_IN, SIGN_UP} from "../config/backend-urls";
import {Roles} from "../dto/roles";
import {WebSocketService} from "./web-socket.service";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  public redirectUrl?: string;
  private currentUser?: UserDto;
  private currentUserKey = 'current_user';

  constructor(private http: HttpClient) {
    let data = localStorage.getItem(this.currentUserKey);
    if(data) {
      this.currentUser = JSON.parse(data);
    }
  }

  signUp(registerData: UserDto) : Observable<AuthResult> {
    this.logout();
    return this.http.post(SIGN_UP, registerData)
      .pipe(
        map(response => {
          if ('email' in response) {
            let responseCredentials = response as Credentials;
            let credentials: Credentials = { email: responseCredentials.email, password: '' };
            credentials.email = responseCredentials.email;
            return new AuthResult(credentials, this.redirectUrl);
          }
          throw new Error(response.toString());
        })
      );
  }

  signIn(credentials: Credentials | null) {
    const headers = new HttpHeaders(credentials ? {
      authorization: 'Basic ' + btoa(credentials.email + ':' + credentials.password) } : {});
    return this.http.get(SIGN_IN, { headers: headers })
      .pipe(
        map(response => {
          if(response) {
            this.currentUser = response as UserDto;
            localStorage.setItem(this.currentUserKey, JSON.stringify(response));
            return new AuthResult(this.currentUser, this.redirectUrl);
          }
          let err = new Error(response);
          console.log(err);
          throw err;
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

  public getUsername() {
    return this.currentUser?.email;
  }

  public isAdmin() : boolean {
    return this.isAuthenticated() && this.currentUser?.roles.indexOf(Roles.ADMIN) != -1;
  }

  public getUserId() {
    return this.currentUser?.id;
  }

  public getRoles() {
    return this.currentUser?.roles;
  }
}
