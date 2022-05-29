import { Injectable } from '@angular/core';
import {FrontUrls} from "../config/front-config";
import {filter, map, Observable, Subscription} from "rxjs";
import {NavigationEnd, Router} from "@angular/router";
import {NotificationService} from "./notification.service";
import {MessageObserverService} from "./message-observer.service";
import {WebSocketService} from "./web-socket.service";
import {NotificationStorage} from "../utils/notification-storage";
import {Message} from "../dto/message";

@Injectable({
  providedIn: 'root'
})
export class InitService {

  constructor(private router: Router,
              private notificationService: NotificationService,
              private messageObserverService: MessageObserverService,
              private webSocketService: WebSocketService) {
  }

  public init() {
    this.initNotifications();
  }

  private initNotifications() {
    let notifications = '/' + FrontUrls.NOTIFICATIONS;
    let subscription = this.router.events
      .pipe(
        filter(event => event instanceof NavigationEnd)
      ).
      subscribe({
        next: value => {
          let navigationEnd = value as NavigationEnd;
          let currentUrl = navigationEnd.url;
          if(currentUrl !== notifications) {
            this.getNotifications(subscription);
          }
        }
      });
  }

  private getNotifications(subscription: Subscription) {
    this.notificationService.getNotifications()
      .subscribe({
        next: message => {
          this.messageObserverService.nextMessage(message);
        },
        error: () => {
          subscription.unsubscribe();
          this.webSocketService.connectToStompEndpoint();
        },
        complete: () => {
          subscription.unsubscribe();
          this.webSocketService.connectToStompEndpoint();
        }
      })
  }
}
