import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {FrontUrls} from "../../config/front-config";
import {FormBuilder, Validators} from "@angular/forms";

const { required, email } = Validators;

@Component({
  selector: 'app-sign-in',
  templateUrl: './sign-in.component.html',
  styleUrls: ['./sign-in.component.scss']
})
export class SignInComponent implements OnInit {

  public form = this.formBuilder.group({
    email: [ null, [required, email] ],
    password: [ null ]
  });

  public formControls = this.form.controls;

  errorSignInMessage: string = '';

  constructor(private authService: AuthService,
              private router: Router,
              private formBuilder: FormBuilder) {
  }

  ngOnInit(): void { }

  signIn() {
    if(!this.form.valid) {
      return;
    }
    this.authService.signIn(this.form.value)
      .subscribe({
        next: (signInResult) => {
          let navigateUrl = signInResult.redirectUrl ? signInResult.redirectUrl : '/' + FrontUrls.MAIN;
          this.router.navigateByUrl(navigateUrl).then(() => {
            this.authService.redirectUrl = undefined;
          });
        },
        error: (errorResult) => {
          if(errorResult.status == 504) {
            errorResult.message = 'Сервер не доступен. Попробуйте позже'
          }
          this.errorSignInMessage = errorResult.message;
        }
      });
  }
}
