import {Component} from '@angular/core';
import {APP_NAME} from "./config/front-config";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {
  title = APP_NAME;

  constructor() {  }
}
