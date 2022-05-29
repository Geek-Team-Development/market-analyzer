import {Injectable} from "@angular/core";
import {BehaviorSubject, ReplaySubject} from "rxjs";
import {Message} from "../dto/message";
import {NotificationStorage} from "../utils/notification-storage";

@Injectable({providedIn: 'root'})
export class MessageObserverService {
  message = new ReplaySubject<Message>(1);
  unreadedMessageCount = new BehaviorSubject<number>(0);

  constructor(private notificationStorage: NotificationStorage) {
  }

  public decrementAndNext() {
    let currentCount = this.unreadedMessageCount.value;
    if(currentCount > 0) {
      currentCount--;
      this.unreadedMessageCount.next(currentCount);
    }
  }

  public incrementAndNext() {
    let currentCount = this.unreadedMessageCount.value + 1;
    this.unreadedMessageCount.next(currentCount);
  }

  public nextMessage(message: Message) {
    this.message.next(message);
    if(!this.notificationStorage.has(message.notifyMessage.id)) {
      this.notificationStorage.set(message.notifyMessage.id, message);
      if(!message.read) {
        this.incrementAndNext();
      }
    }
  }

  public reset() {
    this.unreadedMessageCount.next(0);
    this.notificationStorage.clear();
  }
}
