import {NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MainComponent} from './pages/main/main.component';
import {FormsModule} from "@angular/forms";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NavBarComponent } from './components/nav-bar/nav-bar.component';
import { SignUpComponent } from './pages/sign-up/sign-up.component';
import {HttpClientModule} from "@angular/common/http";
import { SignInComponent } from './pages/sign-in/sign-in.component';
import { HeaderComponent } from './components/header/header.component';
import {MatProgressSpinnerModule} from "@angular/material/progress-spinner";

@NgModule({
  declarations: [
    AppComponent,
    MainComponent,
    NavBarComponent,
    SignUpComponent,
    SignInComponent,
    HeaderComponent
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        FormsModule,
        BrowserAnimationsModule,
        HttpClientModule,
        MatProgressSpinnerModule
    ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
