import { Component, Input, OnInit } from '@angular/core';
import { TenService } from '../ten.service';
import { CellState } from '../cell-state';

@Component({
  selector: 'app-ten-cell',
  templateUrl: './ten-cell.component.html',
  styleUrls: ['./ten-cell.component.css']
})
export class TenCellComponent implements OnInit {

  /**
   * The cell state of itself.
   */
  @Input() state: CellState;
  /**
   * The index of the big square.
   */
  @Input() bigSquare: number;
  /**
   * The index of the small square inside the big square.
   */
  @Input() smallSquare: number;

  /**
   * Construct itself by injecting a submit move service.
   *
   * @param {TenService} tenService the ten service used to submit move.
   */
  constructor(private tenService: TenService) { }

  ngOnInit() {}

  /**
   * Submit the move.
   */
  submitMove(): void {
    this.tenService.submitMove(this.bigSquare, this.smallSquare);
  }

}
