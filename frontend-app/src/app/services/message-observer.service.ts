import {Injectable} from "@angular/core";
import {BehaviorSubject, ReplaySubject} from "rxjs";
import {Message} from "../dto/message";

@Injectable({providedIn: 'root'})
export class MessageObserverService {
  message = new ReplaySubject<Message>(1);
  unreadedMessageCount = new BehaviorSubject<number>(0);

  public decrementAndNext() {
    let currentCount = this.unreadedMessageCount.value - 1;
    if(currentCount > 0) {
      this.unreadedMessageCount.next(currentCount);
    }
  }

  public incrementAndNext() {
    let currentCount = this.unreadedMessageCount.value + 1;
    console.log(currentCount);
    this.unreadedMessageCount.next(currentCount);
  }
}
