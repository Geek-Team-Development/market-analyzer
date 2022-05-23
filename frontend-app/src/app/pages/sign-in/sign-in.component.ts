import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../services/auth.service";
import {FrontUrls} from "../../config/front-config";
import {FormBuilder, Validators} from "@angular/forms";
import {MatDialog} from "@angular/material/dialog";
import {ErrorDialogComponent} from "../../components/error-dialog/error-dialog.component";
import {UsersService} from "../../services/users.service";

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
  private chatId: string = '';

  constructor(private authService: AuthService,
              private userService: UsersService,
              private router: Router,
              private formBuilder: FormBuilder,
              private dialog: MatDialog,
              private route: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.route.queryParams.subscribe(
      (queryParam: any) => {
        this.chatId = queryParam['id'];
        this.addTelegramId();
      }
    )
  }

  addTelegramId() {
    console.log("Current id=" + this.chatId);
    if (this.chatId != undefined || this.chatId != '') {
      if (this.authService.isAuthenticated()) {
        this.userService.addTelegramId(this.chatId).subscribe({
          next: () => {
            this.router.navigateByUrl(FrontUrls.MAIN);
          },
          error: () => {
            console.log("ChatId was not added")
          }
        });

      }
    }
  }

  signIn() {
    if(!this.form.valid) {
      return;
    }
    this.authService.signIn(this.form.value)
      .subscribe({
        next: (signInResult) => {
          this.addTelegramId();
          let navigateUrl = signInResult.redirectUrl ? signInResult.redirectUrl : '/' + FrontUrls.MAIN;
          this.router.navigateByUrl(navigateUrl).then(() => {
            this.authService.redirectUrl = undefined;
          });
        },
        error: (errorResult) => {
          let errorMessage = '';
          if(errorResult.status == 504) {
            errorMessage = 'Сервер не доступен. Попробуйте позже';
            this.openErrorDialog(errorMessage);
            return;
          }
        }
      });
  }

  openErrorDialog(errorMessage: string) {
    this.dialog.open(ErrorDialogComponent, {
      data: errorMessage,
      panelClass: 'custom-modal-dialog'
    });
  }
}
