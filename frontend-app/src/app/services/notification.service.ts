import {Injectable, NgZone} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {FAVORITES, NOTIFICATIONS} from "../config/backend-urls";
import {filter, Observable, Observer} from "rxjs";
import {NotifyMessage} from "../dto/notifyMessage";
import {ProductDto} from "../dto/product-dto";
import {Util} from "../utils/util";
import {Message} from "../dto/message";
import {FrontUrls} from "../config/front-config";
import {NavigationEnd, Router} from "@angular/router";
import {MessageObserverService} from "./message-observer.service";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private http: HttpClient,
              private ngZone: NgZone) { }

  getNotifications(): Observable<Message> {
    return new Observable<Message>((
      observer => {
        let source = new EventSource(NOTIFICATIONS);
        return this.subscriber(observer, source);
      }
    ));
  }

  markAsReaded(notifyMessageId: string) {
    return this.http.post(NOTIFICATIONS + `/` + notifyMessageId, {});
  }

  private subscriber(observer: Observer<Message>, source: EventSource) {
    source.onmessage = event => {
      let message = JSON.parse(event.data);
      this.ngZone.run(() => {
        observer.next(message);
      })
    };
    source.onerror = error => {
      this.ngZone.run(() => {
        observer.error(error)
      })
    };
    return () => source.close();
  }
}
