import {Injectable} from "@angular/core";
import {ReplaySubject} from "rxjs";
import {Message} from "../dto/message";

@Injectable({providedIn: 'root'})
export class MessageObserverService {
  message = new ReplaySubject<Message>(1);
}
