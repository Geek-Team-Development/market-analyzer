import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MAIN_URL, MainComponent} from "./pages/main/main.component";

const routes: Routes = [
  {path: "", pathMatch: "full", redirectTo: MAIN_URL},
  {path: MAIN_URL, pathMatch: 'full', component: MainComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
