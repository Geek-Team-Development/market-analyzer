import {Injectable} from '@angular/core';
import {BehaviorSubject} from 'rxjs';
import {SocketClientState} from "../model/socket-client-state";

@Injectable({providedIn: 'root'})
export class SocketClientStateService {
  state = new BehaviorSubject<SocketClientState>(SocketClientState.ATTEMPTING);
}
