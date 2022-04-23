import {Component, OnInit} from '@angular/core';
import {Credentials} from "../../model/credentials";
import {Util} from "../../utils/util";
import {Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {FrontUrls} from "../../config/front-config";

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss']
})
export class SignInComponent implements OnInit {

  credentials: Credentials = new Credentials();
  signInError: SignInError = new SignInError();

  constructor(private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
  }

  signIn() {
    if (!this.checkValidCredentials()) {
      return;
    }
    this.authService.signIn(this.credentials)
      .subscribe({
        next: (signInResult) => {
          let navigateUrl = signInResult.redirectUrl ? signInResult.redirectUrl : '/' + FrontUrls.MAIN;
          console.log(navigateUrl);
          this.router.navigateByUrl(navigateUrl).then(() => {
            this.authService.redirectUrl = undefined;
          });
        },
        error: (errorResult) => {
          console.log(errorResult);
        }
      });
  }

  private checkValidCredentials(): boolean {
    let result = true;
    this.signInError = new SignInError();
    if (this.credentials.email === '') {
      this.signInError.emailError = Util.mustBeDefinedErrorMessage('Email');
      result = false;
    }
    if (this.credentials.password === '') {
      this.signInError.passwordError = Util.mustBeDefinedErrorMessage('Пароль');
      result = false;
    }
    return result;
  }
}

class SignInError {
  emailError: string = '';
  passwordError: string = '';
}
