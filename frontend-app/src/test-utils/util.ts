import {DebugElement} from "@angular/core";
import {ComponentFixture} from "@angular/core/testing";
import {By} from "@angular/platform-browser";
import {defer} from "rxjs";

export function debugElement<T>(fixture: ComponentFixture<T>,
                         selector: string): DebugElement {
  return fixture.debugElement.query(By.css(selector));
}

export function debugElements<T>(fixture: ComponentFixture<T>,
                                 selector: string): DebugElement[] {
  return fixture.debugElement.queryAll(By.css(selector));
}

export function nativeElement<T>(fixture: ComponentFixture<T>,
                                 selector: string) {
  return debugElement(fixture, selector).nativeElement;
}

export function setFieldValue<T>(fixture: ComponentFixture<T>,
                                 selector: string, value: string): void {
  const element = nativeElement(fixture, selector);
  element.value = value;
  element.dispatchEvent(new Event('input'));
  fixture.detectChanges();
}

function makeClickEvent(target: EventTarget): Partial<MouseEvent> {
  return {
    preventDefault(): void { },
    stopPropagation(): void { },
    stopImmediatePropagation(): void { },
    type: 'click',
    target,
    currentTarget: target,
    bubbles: true,
    cancelable: true,
    button: 0
  }
}

export function click<T>(fixture: ComponentFixture<T>,
                      selector: string): void {
  const element = debugElement(fixture, selector);
  const event = makeClickEvent(element.nativeElement);
  element.triggerEventHandler('click', event);
  fixture.detectChanges();
}

export function submit<T>(fixture: ComponentFixture<T>,
                       selector: string) {
  debugElement(fixture, selector).triggerEventHandler('submit', {});
  fixture.detectChanges();
}
