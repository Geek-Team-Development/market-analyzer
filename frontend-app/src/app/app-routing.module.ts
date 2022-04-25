import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MainComponent} from "./pages/main/main.component";
import {FrontUrls} from "./config/front-config";
import {SignUpComponent} from "./pages/sign-up/sign-up.component";
import {SignInComponent} from "./pages/sign-in/sign-in.component";
import {FavoritesComponent} from "./pages/favorites/favorites.component";

const routes: Routes = [
  { path: "", pathMatch: "full", redirectTo: FrontUrls.MAIN },
  { path: FrontUrls.MAIN, component: MainComponent },
  { path: FrontUrls.SIGN_UP, component: SignUpComponent },
  { path: FrontUrls.SIGN_IN, component: SignInComponent },
  { path: FrontUrls.FAVORITES, component: FavoritesComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
