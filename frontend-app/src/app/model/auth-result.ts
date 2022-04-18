import {Credentials} from "./credentials";

export class AuthResult {

  constructor(public credentials: Credentials,
              public redirectUrl?: string) {
  }
}
