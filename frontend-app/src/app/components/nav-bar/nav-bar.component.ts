import { Component, OnInit } from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
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

  currentUrl: string = this.main;

  leftMenuIsActive: boolean = false;

  constructor(private router: Router,
              public authService: AuthService) {
    router.events.subscribe({
      next: value => {
        if(value instanceof NavigationEnd) {
          let navEnd = value as NavigationEnd;
          this.currentUrl = navEnd.url;
        }
      }
    })
  }

  ngOnInit(): void {
  }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl(FrontUrls.MAIN);
  }


  clickLeftMenu() {
    this.leftMenuIsActive = !this.leftMenuIsActive;
  }

  getUsername() {
    return this.authService.getUsername();
  }
}
