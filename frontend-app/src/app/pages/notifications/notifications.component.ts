import {Component, OnInit} from '@angular/core';
import {NotifyMessage} from "../../dto/notifyMessage";
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

  messages: NotifyMessage[] = [];

  favorites = '/' + FrontUrls.FAVORITES;

  date = new Date();

  constructor(private messageObserverService: MessageObserverService,
              private notificationService: NotificationService,
              private router: Router) {
    // let message = new NotifyMessage();
    // message.message = "Hello kjsahdkjashdkjashdkjashdkjashdkjahsdkjahsdkjahsdkjahsdkjahskdjhaksjdhakjsdhakjsdhkajsdhkajsdhaksjdhaksjdhaskdjh";
    // message.date = "12.01.2020";
    // message.read = true;
    // this.messages.unshift(message);

    // message = new NotifyMessage();
    // message.message = "Привет kjsahdkjashdkjashdkjashdkjashdkjahsdkjahsdkjahsdkjahsdkjahskdjhaksjdhakjsdhakjsdhkajsdhkajsdhaksjdhaksjdhaskdjh";
    // message.date = "14.01.2020";
    // message.read = false;
    // this.messages.unshift(message);

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
    this.messages = this.messages.sort((a, b) => {
      return +a.date - +b.date;
    })
    this.messages.unshift(message.notifyMessage);
  }

  private getNotifications() {
    this.notificationService.getNotifications()
      .subscribe({
        next: message => {
          this.messages.unshift(message);
        }
      })
  }

  public parseDate(date: string) {
    return new Date(date);
  }

  clickNotifyMessage(id: string) {
    this.router.navigateByUrl('/' + FrontUrls.FAVORITES)
      .then(() => {
        this.notificationService.markAsReaded(id)
          .subscribe({
            next: () => {
              this.messageObserverService.decrementAndNext();
            }
          })
      });
  }
}
