import { Component } from '@angular/core';
import {APP_NAME, FrontUrls} from "./config/front-config";
import {AuthService} from "./services/auth.service";
import {Router} from "@angular/router";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = APP_NAME;

  constructor(private authService: AuthService,
              private router: Router) {
    // authService.signIn(null)
    //   .subscribe({
    //     error: () => {
    //       authService.logout();
    //       this.router.navigateByUrl(FrontUrls.MAIN);
    //     }
    //   });
  }
}
