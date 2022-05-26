import {Injectable} from '@angular/core';
import {SocketClientStateService} from "./socket-client-state.service";
import {SocketClientState} from "../model/socket-client-state";
import {AuthService} from "./auth.service";
import {filter, first, Observable, switchMap} from "rxjs";
import {Client, Stomp, StompSubscription} from "@stomp/stompjs";
import * as SockJS from "sockjs-client";
import {MessageObserverService} from "./message-observer.service";
import {HttpXsrfTokenExtractor} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {

  private client: any;

  constructor(private socketClientStateService: SocketClientStateService,
              private authService: AuthService,
              private sharedService: SocketClientStateService,
              private messageObserverService: MessageObserverService,
              private xsrfTokenExtractor: HttpXsrfTokenExtractor) {
    socketClientStateService.state.subscribe({
      next: value => {
        if(value == SocketClientState.CONNECTED) {
          this.getNotifyMessages();
        }
      }
    })
  }

  public connectToStompEndpoint() {
    this.client = Stomp.over(new SockJS("/api/v1/notifies"));
    let xsrf = this.xsrfTokenExtractor.getToken();
    this.client.connect({'X-XSRF-TOKEN': xsrf}, () => {
      this.sharedService.state.next(SocketClientState.CONNECTED);
    });
  }

  private getNotifyMessages() {
    this.onMessage('/queue/front.notify.queue.' + this.authService.getUserId())
      .subscribe(msg => {
        this.messageObserverService.message.next(msg);
        this.messageObserverService.incrementAndNext();
      });
  }

  private connect(): Observable<Client> {
    return new Observable<Client>(observer => {
      this.sharedService.state.pipe(filter(state => state === SocketClientState.CONNECTED))
        .subscribe(() => {
          observer.next(this.client);
        });
    });
  }

  onMessage(topic: string): Observable<any> {
    return this.connect().pipe(first(), switchMap(client => {

      return new Observable<any>(observer => {
        let headers = { durable: 'false', 'auto-delete': 'false', exclusive: 'false' };
        const subscription: StompSubscription = client.subscribe(topic, message => {
          console.log(message);
          observer.next(JSON.parse(message.body));
        }, headers);
        return () => client.unsubscribe(subscription.id);
      });
    }));
  }
}
