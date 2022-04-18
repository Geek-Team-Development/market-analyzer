import { Component, OnInit } from '@angular/core';
import {Router} from "@angular/router";
import {FrontUrls} from "../../config/front-config";
import {AuthService} from "../../services/auth.service";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss']
})
export class NavBarComponent implements OnInit {

  main = '/' + FrontUrls.MAIN;
  signup = '/' + FrontUrls.SIGN_UP;
  signin = '/' + FrontUrls.SIGN_IN;

  constructor(private router: Router,
              public authService: AuthService) { }

  ngOnInit(): void {
  }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl(FrontUrls.MAIN);
  }
}
