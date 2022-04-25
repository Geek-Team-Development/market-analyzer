import {HttpEvent, HttpHandler, HttpInterceptor, HttpRequest} from "@angular/common/http";
import {Injectable} from "@angular/core";
import {Observable, throwError} from "rxjs";
import {Router} from "@angular/router";
import {catchError} from "rxjs/operators";
import {AuthService} from "../services/auth.service";
import {FrontUrls} from "../config/front-config";

@Injectable()
export class UnauthorizedInterceptor implements HttpInterceptor {

  constructor(private router: Router,
              private auth: AuthService) {
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request).pipe(catchError(err => {
      if (err.status === 401) {
        console.log('Intercepted unauthorized error');
        this.auth.logout();
        this.router.navigateByUrl('/' + FrontUrls.SIGN_IN);
      }
      return throwError(err);
    }))
  }

}
