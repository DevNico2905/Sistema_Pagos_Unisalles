import { CanActivateFn } from '@angular/router';
import { Injectable } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable()
export class AuthorizationGuard {

  constructor(private authService: AuthService, private router: Router) {

  }

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    if (this.authService.isAuthenticated) {
      let requiredRoles = route.data['roles'];
      let userRoles = this.authService.roles;

      for(let role of userRoles){
        if(requiredRoles.includes(role)){
          return true;
        }
      }
    }
    return false;
  }
}