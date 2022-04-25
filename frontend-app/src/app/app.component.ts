import { Component } from '@angular/core';
import {APP_NAME} from "./config/front-config";
import {AuthService} from "./services/auth.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = APP_NAME;

  constructor(private authService: AuthService) {
    authService.signIn(null)
      .subscribe({
        error: () => {
          authService.logout();
        }
      });
  }
}
