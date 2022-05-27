import {Component, OnInit} from '@angular/core';
import {Router} from "@angular/router";
import {FrontUrls} from "../../config/front-config";
import {AuthService} from "../../services/auth.service";
import {MessageObserverService} from "../../services/message-observer.service";

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

  unreadedMessageCount: number = 0;

  constructor(private router: Router,
              public authService: AuthService,
              private messageObserverService: MessageObserverService) {
    messageObserverService.unreadedMessageCount.subscribe({
      next: value => {
        this.unreadedMessageCount = value;
      }
    })
  }

  ngOnInit(): void { }

  logout() {
    this.authService.logout();
    this.router.navigateByUrl(FrontUrls.MAIN);
  }

  clickSmallMenu() {
    this.leftMenuIsActive = !this.leftMenuIsActive;
  }

  getUsername() {
    return this.authService.getUsername();
  }

  notifyClick() {
    this.router.navigateByUrl('/' + FrontUrls.NOTIFICATIONS);
  }
}
