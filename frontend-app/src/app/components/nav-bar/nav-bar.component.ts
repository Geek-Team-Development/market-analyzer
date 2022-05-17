import {Component, OnInit} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {FrontUrls} from "../../config/front-config";
import {AuthService} from "../../services/auth.service";
import {filter} from "rxjs";

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  styleUrls: ['./nav-bar.component.scss']
})
export class NavBarComponent implements OnInit {

  main = '/' + FrontUrls.MAIN;
  signup = '/' + FrontUrls.SIGN_UP;
  signin = '/' + FrontUrls.SIGN_IN;
  favorites = '/' + FrontUrls.FAVORITES;
  users = '/' + FrontUrls.USERS;

  currentUrl: string = this.main;

  leftMenuIsActive: boolean = false;

  constructor(private router: Router,
              public authService: AuthService) {
    router.events
      .pipe(
        filter(event => event instanceof NavigationEnd)
      ).subscribe({
      next: value => {
        this.currentUrl = (value as NavigationEnd).url;
      }
    })
  }

  ngOnInit(): void { }

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
