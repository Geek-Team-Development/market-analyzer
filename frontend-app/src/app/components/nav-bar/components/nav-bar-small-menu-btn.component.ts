import {Component} from "@angular/core";

@Component({
  selector: 'nav-bar-small-menu-btn',
  template: `
    <div class="nav-bar-sm-menu-panel">
      <button type="button" class="nav-bar-left-menu-btn"
              aria-controls="mobile-menu" aria-expanded="false">
        <span class="tw-sr-only">Open main menu</span>
        <svg class="tw-block tw-h-6 tw-w-6" xmlns="http://www.w3.org/2000/svg"
             fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M4 6h16M4 12h16M4 18h16"/>
        </svg>
        <svg class="tw-hidden tw-h-6 tw-w-6" xmlns="http://www.w3.org/2000/svg"
             fill="none" viewBox="0 0 24 24" stroke="currentColor" aria-hidden="true">
          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                d="M6 18L18 6M6 6l12 12"/>
        </svg>
      </button>
    </div>
  `,
  styles: [`
    .nav-bar-sm-menu-panel {
      @apply tw-absolute tw-inset-y-0 tw-left-0 tw-flex tw-items-center sm:tw-hidden;
    }

    .nav-bar-left-menu-btn {
      @apply tw-inline-flex tw-items-center tw-justify-center tw-p-2 tw-rounded-md;
      @apply tw-text-gray-400 hover:tw-text-white hover:tw-bg-gray-700;
      @apply focus:tw-outline-none focus:tw-ring-2 focus:tw-ring-inset focus:tw-ring-white;
    }
  `]
})
export class NavBarSmallMenuBtnComponent { }
