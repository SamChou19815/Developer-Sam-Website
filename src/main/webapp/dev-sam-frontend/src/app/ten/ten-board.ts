import { CellState } from './cell-state';

/**
 * Represent the local data model of the game.
 */
export class TenBoard {

  /**
   * The most basic board state.
   * @type {CellState[][]} a 2D array of the board state.
   */
  private readonly board: CellState[][] = Array<CellState[]>(9);
  /**
   * The current big square legal position.
   */
  currentBigSquareLegalPosition: number;
  /**
   * The current player identity.
   */
  private _currentPlayerIdentity: number;

  /**
   * Construct itself with a clean copy.
   */
  constructor() {
    for (let i = 0; i < 9; i++) {
      const square3x3 = new Array<CellState>(9);
      for (let j = 0; j < 9; j++) {
        square3x3[j] = new CellState();
      }
      this.board[i] = square3x3;
    }
    this.currentBigSquareLegalPosition = -1;
    this._currentPlayerIdentity = 1;
  }

  /**
   * Obtain the current player identity.
   *
   * @returns {number} the current player identity.
   */
  get currentPlayerIdentity(): number {
    return this._currentPlayerIdentity;
  }

  /**
   * A convenience method to obtain a given cell at the given location.
   *
   * @param {[number , number]} tuple the tuple of location.
   * @returns {CellState} the state of the cell in a wrapper class.
   */
  getCell(tuple: [number, number]): CellState {
    return this.board[tuple[0]][tuple[1]];
  }

  /**
   * Reset the board to the original state.
   *
   * @param {number} currentPlayerIdentity the desired identity of the current player.
   */
  resetBoard(currentPlayerIdentity: number): void {
    for (let i = 0; i < 9; i++) {
      for (let j = 0; j < 9; j++) {
        const cell = this.board[i][j];
        cell.setState(0);
        cell.unmarkHighLight();
      }
    }
    this.currentBigSquareLegalPosition = -1;
    this._currentPlayerIdentity = currentPlayerIdentity;
  }

  /**
   * Remove highlights on the cell completely.
   */
  removeHighlights(): void {
    for (let i = 0; i < 9; i++) {
      for (let j = 0; j < 9; j++) {
        this.board[i][j].unmarkHighLight();
      }
    }
  }

  /**
   * Convert the board into a board data format as part of data to be sent to the server.
   *
   * @returns {TenBoardData} the board data format of the board.
   */
  private toBoardData(): TenBoardData {
    const board2DArray = Array<number[]>(9);
    for (let i = 0; i < 9; i++) {
      const stateSquare3x3 = this.board[i];
      const square3x3 = new Array<number>(9);
      for (let j = 0; j < 9; j++) {
        square3x3[j] = stateSquare3x3[j].getState();
      }
      board2DArray[i] = square3x3;
    }
    return {
      board: board2DArray,
      currentBigSquareLegalPosition: this.currentBigSquareLegalPosition,
      currentPlayerIdentity: -this.currentPlayerIdentity
    };
  }

  /**
   * Generate the client side information to be sent to the server.
   *
   * @param {number} bigSquare the index of the big square of the user move.
   * @param {number} smallSquare the index of the small square inside the big square of the user move.
   * @returns {TenClientMove} the client move data to be sent to the server.
   */
  clientMove(bigSquare: number, smallSquare: number): TenClientMove {
    return {
      boardBeforeHumanMove: this.toBoardData(),
      humanMove: [bigSquare, smallSquare]
    };
  }

}

/**
 * Defines the format of the current board info.
 */
interface TenBoardData {
  board: number[][];
  currentBigSquareLegalPosition: number;
  currentPlayerIdentity: number;
}

/**
 * Defines the format of client move for client-server communication.
 */
export interface TenClientMove {
  boardBeforeHumanMove: TenBoardData;
  humanMove: [number, number];
}

/**
 * Defines the format of server response for client-server communication.
 */
export interface TenServerResponse {
  aiMove: [number, number];
  currentBigSquareLegalPosition: number;
  status: number;
  aiWinningProbability: number;
}
