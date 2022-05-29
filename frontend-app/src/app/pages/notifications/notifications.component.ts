import {Component, OnInit} from '@angular/core';
import {MessageObserverService} from "../../services/message-observer.service";
import {Message} from "../../dto/message";
import {NotificationService} from "../../services/notification.service";
import {FrontUrls} from "../../config/front-config";
import {Router} from "@angular/router";

@Component({
  selector: 'app-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.scss']
})
export class NotificationsComponent implements OnInit {

  messages: Message[] = [];

  favorites = '/' + FrontUrls.FAVORITES;

  constructor(private messageObserverService: MessageObserverService,
              private notificationService: NotificationService,
              private router: Router) {
    messageObserverService.message.subscribe({
      next: message => {
        this.messageReceived(message);
      }
    })
  }

  ngOnInit(): void {
    this.messages = [];
    this.getNotifications();
  }

  private messageReceived(message: Message) {
    this.messages.unshift(message);
    this.sort();
  }

  private getNotifications() {
    this.messageObserverService.reset();
    this.notificationService.getNotifications()
      .subscribe({
        next: message => {
          this.messageObserverService.nextMessage(message);
        }
      })
  }

  private sort() {
    this.messages.sort((a, b) => {
      return <any>new Date(b.notifyMessage.date) - <any>new Date(a.notifyMessage.date);
    })
  }

  public parseDate(date: string) {
    return new Date(date);
  }

  clickNotifyMessage(message: Message) {
    this.router.navigateByUrl('/' + FrontUrls.FAVORITES)
      .then(() => {
        if(!message.read) {
          this.notificationService.markAsReaded(message.notifyMessage.id)
            .subscribe({
              next: () => {
                this.messageObserverService.decrementAndNext();
              }
            })
        }
      });
  }
}
