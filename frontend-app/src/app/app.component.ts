import {Component} from '@angular/core';
import {APP_NAME} from "./config/front-config";
import {InitService} from "./services/init.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  title = APP_NAME;

  constructor(private initService: InitService) {
    this.initService.init();
  }
}
