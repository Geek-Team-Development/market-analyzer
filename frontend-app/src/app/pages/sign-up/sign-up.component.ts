import {Component, OnInit} from '@angular/core';
import {UserDto} from "../../dto/user-dto";
import {AuthService} from "../../services/auth.service";
import {Router} from "@angular/router";
import {SIGN_IN} from "../../config/backend-urls";
import {Util} from "../../utils/util";

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
          let navigateUrl = signUpResult.redirectUrl ? signUpResult.redirectUrl : '/' + SIGN_IN;
          this.router.navigateByUrl(navigateUrl);
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
        Util.mustBeDefinedErrorMessage('Password');
      result = false;
    }
    if(this.repeatPassword !== this.userDto.password) {
      this.signUpError.repeatPasswordError = 'Passwords is not mismatch';
      result = false;
    }
    if (this.userDto.firstName === '') {
      this.signUpError.firstNameError =
        Util.mustBeDefinedErrorMessage('First name');
      result = false;
    }
    if(this.userDto.lastName === '') {
      this.signUpError.lastNameError =
        Util.mustBeDefinedErrorMessage('Last name');
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
