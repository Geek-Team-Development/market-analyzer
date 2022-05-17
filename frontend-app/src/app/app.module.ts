import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MainComponent} from './pages/main/main.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {NavBarComponent} from './components/nav-bar/nav-bar.component';
import {SignUpComponent} from './pages/sign-up/sign-up.component';
import {HTTP_INTERCEPTORS, HttpClientModule} from "@angular/common/http";
import {SignInComponent} from './pages/sign-in/sign-in.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";
import {MatButtonModule} from "@angular/material/button";
import {MatMenuModule} from "@angular/material/menu";
import {UnauthorizedInterceptor} from "./utils/unauthorized-interceptor";
import {MatDividerModule} from "@angular/material/divider";
import { FavoritesComponent } from './pages/favorites/favorites.component';
import { ProductCardComponent } from './components/product-card/product-card.component';

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    NavBarComponent,
    SignUpComponent,
    SignInComponent,
    FavoritesComponent,
    ProductCardComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatProgressSpinnerModule,
        MatButtonModule,
        MatMenuModule,
        MatDividerModule,
        ReactiveFormsModule
    ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: UnauthorizedInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
