import { Component, OnInit } from '@angular/core';
import { TenService } from './ten.service';

@Component({
  selector: 'app-ten',
  templateUrl: './ten.component.html',
  styleUrls: ['./ten.component.css']
})
export class TenComponent implements OnInit {

  // private blockingOverlayService: BlockingOverlayService;
  /**
   * A tuple array to map from location to a tuple representing the location on the abstract board.
   */
  readonly tupleArray: [number, number][];

  /**
   * Initialize itself with an injected ten service.
   *
   * @param {TenService} tenService ten service used to record board data.
   */
  constructor(public readonly tenService: TenService) {
    const tupleArray: [number, number][] = [];
    // Initialize a mapping between UI cell sequence and the actual underlying sequence.
    for (let i = 0; i < 9; i++) {
      for (let j = 0; j < 9; j++) {
        const a: number = Math.floor(i / 3) * 3 + Math.floor(j / 3);
        const b: number = Math.floor(i % 3 * 3 + j % 3);
        const tuple: [number, number] = [a, b];
        tupleArray.push(tuple);
      }
    }
    this.tupleArray = tupleArray;
  }

  ngOnInit() {}

}
