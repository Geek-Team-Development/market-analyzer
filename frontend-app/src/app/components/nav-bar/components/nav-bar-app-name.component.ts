import {Component, Input} from "@angular/core";

@Component({
  selector: 'nav-bar-app-name',
  template: `
    <div class="tw-flex-shrink-0 tw-flex tw-items-center">
      <div class="nav-bar-app-name">{{appName}}</div>
    </div>
  `,
  styles: [`
    .nav-bar-app-name {
      @apply tw-text-white tw-text-4xl;
      font-family: 'Ms Madi', cursive;
    }
  `]
})
export class NavBarAppNameComponent {
  @Input() appName = '';
}
