import {Component, OnInit} from '@angular/core';
import {UserDto} from "../../dto/user-dto";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {Util} from "../../utils/util";
import {FrontUrls} from "../../config/front-config";

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss']
})
export class SignUpComponent implements OnInit {

  userDto: UserDto = new UserDto();
  repeatPassword: string = '';
  signUpError: SignUpFormsErrors = new SignUpFormsErrors();

  constructor(private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
  }

  signUp() {
    if (!this.checkValidUserDto()) {
      return;
    }
    this.authService.signUp(this.userDto)
      .subscribe({
        next: (signUpResult) => {
          let navigateUrl = signUpResult.redirectUrl ? signUpResult.redirectUrl : '/' + FrontUrls.SIGN_IN;
          this.router.navigateByUrl(navigateUrl).then(() => {
            this.authService.redirectUrl = undefined;
          });
        },
        error: (errorResult) => {
          console.log(errorResult);
        }
      });
  }

  private checkValidUserDto(): boolean {
    let result = true;
    this.signUpError = new SignUpFormsErrors();
    if (this.userDto.email === '') {
      this.signUpError.emailError =
        Util.mustBeDefinedErrorMessage('Email');
      result = false;
    }
    if (this.userDto.password === '') {
      this.signUpError.passwordError =
        Util.mustBeDefinedErrorMessage('Пароль');
      result = false;
    }
    if(this.repeatPassword !== this.userDto.password) {
      this.signUpError.repeatPasswordError = 'Пароли не совпадают';
      result = false;
    }
    if (this.userDto.firstName === '') {
      this.signUpError.firstNameError =
        Util.mustBeDefinedErrorMessage('Имя');
      result = false;
    }
    if(this.userDto.lastName === '') {
      this.signUpError.lastNameError =
        Util.mustBeDefinedErrorMessage('Фамилия');
      result = false;
    }
    return result;
  }
}

class SignUpFormsErrors {
  emailError: string = '';
  passwordError: string = '';
  repeatPasswordError: string = '';
  firstNameError: string = '';
  lastNameError: string = '';
}
