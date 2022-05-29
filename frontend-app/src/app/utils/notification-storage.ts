import {Injectable} from "@angular/core";
import {Message} from "../dto/message";

@Injectable({
  providedIn: 'root'
})
export class NotificationStorage {
  private notifications = new Map<string, Message>();

  public set(id: string, message: Message): Map<string, Message> {
    return this.notifications.set(id, message);
  }

  public has(key: string): boolean {
    return this.notifications.has(key);
  }

  public getStorageNotifications() {
    return this.notifications;
  }

  public clear() {
    this.notifications.clear();
  }
}
