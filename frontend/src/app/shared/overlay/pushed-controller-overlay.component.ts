import { Component, OnInit } from '@angular/core';
import { PushedControllerOverlayRef } from './push-controller.service';

@Component({
  selector: 'app-pushed-controller-overlay',
  template: '<p>If you see this, then my program has a bug.</p>',
  styleUrls: ['./pushed-controller-overlay.component.scss']
})
export class PushedControllerOverlayComponent implements OnInit {

  /**
   * Construct itself by a handler that can close itself.
   *
   * @param {PushedControllerOverlayRef} ref ref that can close itself.
   */
  constructor(private ref: PushedControllerOverlayRef) { }

  ngOnInit() {}

  /**
   * Pop itself as a controller.
   */
  popController(): void {
    this.ref.close();
  }

}
