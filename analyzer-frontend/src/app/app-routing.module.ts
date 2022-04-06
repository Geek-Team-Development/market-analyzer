import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {MAIN_PAGE, MainPageComponent} from "./components/pages/main-page/main-page.component";

const routes: Routes = [
  {path: "", pathMatch: "full", redirectTo: MAIN_PAGE},
  {path: MAIN_PAGE, component: MainPageComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
