import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {Injectable} from "@angular/core";
import {AuthService} from "../services/auth.service";
import {FrontUrls} from "../config/front-config";

@Injectable({
  providedIn: 'root',
})
export class ProfileAuthGuard implements CanActivate{

  constructor(private authService: AuthService,
              private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot,
              state: RouterStateSnapshot): boolean {
    if(this.authService.isAdmin()) {
      return true;
    }
    let id = state.url.substring(('/' + FrontUrls.USERS + '/').length);
    return this.checkCurrentUser(id);
  }

  private checkCurrentUser(id: string): boolean {
    return this.authService.getUserId() === id;
  }
}
