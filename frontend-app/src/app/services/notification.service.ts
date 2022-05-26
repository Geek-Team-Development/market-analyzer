import {Injectable, NgZone} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {FAVORITES, NOTIFICATIONS} from "../config/backend-urls";
import {Observable, Observer} from "rxjs";
import {NotifyMessage} from "../dto/notifyMessage";
import {ProductDto} from "../dto/product-dto";
import {Util} from "../utils/util";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private http: HttpClient,
              private ngZone: NgZone) { }

  getNotifications(): Observable<NotifyMessage> {
    return new Observable<NotifyMessage>((
      observer => {
        let source = new EventSource(NOTIFICATIONS);
        return this.subscriber(observer, source);
      }
    ));
  }

  markAsReaded(notifyMessageId: string) {
    return this.http.post(NOTIFICATIONS + `/` + notifyMessageId, {});
  }

  private subscriber(observer: Observer<NotifyMessage>, source: EventSource) {
    source.onmessage = event => {
      let notifyMessage = Util.parseNotifyMessage(event.data);
      this.ngZone.run(() => {
        observer.next(notifyMessage);
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
