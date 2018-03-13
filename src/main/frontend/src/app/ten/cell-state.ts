/**
 * Wrapper class for a state,
 */
export class CellState {

  /**
   * State of the cell.
   */
  private state: number;
  /**
   * Whether the cell is in highlight mode.
   */
  private highlight: boolean;

  /**
   * Construct to the default empty state.
   */
  constructor() {
    this.state = 0;
    this.highlight = false;
  }

  /**
   * Obtain the state of the cell.
   *
   * @returns {number} the numerical value of the cell.
   */
  getState(): number {
    return this.state;
  }

  /**
   * Change the state of the cell.
   *
   * @param {number} state the new numerical value of the cell.
   */
  setState(state: number): void {
    this.state = state;
  }

  /**
   * Report whether the cell is highlighted.
   *
   * @returns {boolean} whether the cell is highlighted.
   */
  isHighlighted(): boolean {
    return this.highlight;
  }

  /**
   * Highlight the cell.
   */
  markHighlight(): void {
    this.highlight = true;
  }

  /**
   * Undo highlight on the cell, if it is highlighted.
   */
  unmarkHighLight(): void {
    if (this.highlight) {
      this.highlight = false;
    }
  }

}
