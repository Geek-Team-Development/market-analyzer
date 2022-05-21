import {Component, Inject} from "@angular/core";
import {MAT_DIALOG_DATA} from "@angular/material/dialog";

@Component({
  selector: 'error-dialog',
  template: `
    <div class="tw-m-0">
      <div class="dialog-header-panel">
        <div class="dialog-header">Ошибка!</div>
      </div>
      <div class="dialog-content-panel">
        <div class="dialog-content">{{message}}</div>
      </div>
      <div mat-dialog-actions class="dialog-footer-panel">
        <button mat-button mat-dialog-close="" class="dialog-ok-btn">OK</button>
      </div>
    </div>
  `,
  styleUrls: ['./error-dialog.component.scss']
})
export class ErrorDialogComponent {
  message = '';

  constructor(@Inject(MAT_DIALOG_DATA) private errorMessage: string) {
    this.message = errorMessage;
  }
}
