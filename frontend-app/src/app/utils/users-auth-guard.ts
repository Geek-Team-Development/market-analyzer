import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from "@angular/router";
import {AuthService} from "../services/auth.service";
import {FrontUrls} from "../config/front-config";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root',
})
export class UsersAuthGuard implements CanActivate{

  constructor(private authService: AuthService,
              private router: Router) {}

  canActivate(route: ActivatedRouteSnapshot,
              state: RouterStateSnapshot): boolean {
    let url: string = state.url;
    return this.checkAdmin(url);
  }

  checkAdmin(url: string): boolean {
    if (this.authService.isAdmin()) {
      return true;
    }
    this.authService.redirectUrl = url;
    this.router.navigateByUrl('/' + FrontUrls.SIGN_IN);
    return false;
  }
}
