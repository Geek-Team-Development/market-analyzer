import { Component, OnInit } from '@angular/core';
import {APP_NAME} from "../../config/front-config";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {

  header: string = APP_NAME;

  constructor() { }

  ngOnInit(): void {
  }

}
