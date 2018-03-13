import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';

@Component({
  selector: 'app-closable-controller',
  templateUrl: './closable-controller.component.html',
  styleUrls: ['./closable-controller.component.css']
})
export class ClosableControllerComponent implements OnInit {

  @Input() title: string;
  @Output() closeClicked = new EventEmitter<void>();

  constructor() { }

  ngOnInit() { }

  /**
   * To report close clicked.
   */
  closeMe(): void {
    this.closeClicked.emit(null);
  }

}
