import {NotifyMessage} from "./notifyMessage";

export class Message {
  notifyMessage: NotifyMessage = new NotifyMessage();
  object: any;
  read: boolean = false;
}
