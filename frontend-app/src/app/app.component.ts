import {Component, OnInit} from '@angular/core';
import {APP_NAME} from "./config/front-config";
import {BehaviorSubject, filter, first, Observable, switchMap} from "rxjs";
import {Client, Stomp, StompSubscription} from "@stomp/stompjs";
import * as SockJS from "sockjs-client";
import {SocketClientState} from "./model/socket-client-state";
import {SocketClientStateService} from "./services/socket-client-state.service";
import {WebSocketService} from "./services/web-socket.service";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  title = APP_NAME;

  constructor(private webSocketService: WebSocketService) { }

  ngOnInit(): void {
    this.webSocketService.connectToStompEndpoint();
  }
}
