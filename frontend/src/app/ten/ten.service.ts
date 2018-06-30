import { Injectable } from '@angular/core';
import { TenBoard } from './ten-board';
import { CellState } from './cell-state';
import { TenNetworkService } from './ten-network.service';

@Injectable()
export class TenService {

  /**
   * The data class of the game.
   * @type {TenBoard}
   */
  readonly board = new TenBoard();
  /**
   * The message shown under the title.
   * @type {string}
   */
  private _message = 'Select your first move.';
  /**
   * The probability of AI winning the game.
   * @type {number}
   */
  private _aiWinningProbability = 50;
  /**
   * Whether the blocker should be active.
   * @type {boolean}
   */
  private _blockerActive = false;
  /**
   * Whether the controls should be blocked.
   */
  private _controlBlocked = false;

  /**
   * Initialize itself with injected ten network service.
   *
   * @param {TenNetworkService} networkService the injected ten network service.
   */
  constructor(private networkService: TenNetworkService) { }

  /**
   * Obtain the message to be displayed.
   *
   * @returns {string} the message to be displayed.
   */
  get message(): string {
    return this._message;
  }

  /**
   * Obtain the AI winning probability.
   *
   * @returns {number} the winning probability of AI.
   */
  get aiWinningProbability(): number {
    return this._aiWinningProbability;
  }

  /**
   * Report whether the block on board is active.
   *
   * @returns {boolean} whether the block on board is active.
   */
  get blockerActive(): boolean {
    return this._blockerActive;
  }

  /**
   * Report whether the block on control is active.
   *
   * @returns {boolean} whether the block on control is active.
   */
  get controlBlocked(): boolean {
    return this._controlBlocked;
  }

  /**
   * Reset the data to the good default one and select a side for a new round of game.
   *
   * @param {number} side 1 for black, -1 for white.
   */
  resetDataAndSelectSide(side: number): void {
    this._message = 'Select your first move.';
    this._aiWinningProbability = 50;
    this._blockerActive = false;
    this.board.resetBoard(side);
    if (side === -1) {
      // AI will always choose this.
      // Don't send a request to the server.
      this.board.getCell([4, 4]).setState(1);
      this.board.currentBigSquareLegalPosition = 4;
    }
  }

  /**
   * Submit a move to the server.
   *
   * @param {number} bigSquare the index of the big square.
   * @param {number} smallSquare the index of the small square inside the big square.
   */
  submitMove(bigSquare: number, smallSquare: number): void {
    const cell: CellState = this.board.getCell([bigSquare, smallSquare]);
    const originalState: number = cell.getState();
    if (originalState !== 0) {
      this._message = 'Illegal move!';
      return;
    }
    const myIdentity = this.board.currentPlayerIdentity;
    const clientMove = this.board.clientMove(bigSquare, smallSquare);
    cell.setState(myIdentity);
    this._message = 'Waiting for server response...';
    this._blockerActive = true;
    this._controlBlocked = true;
    // Setup all the current status, waiting for server response.
    this.networkService.getGameResponse(clientMove, (resp) => {
      this._blockerActive = false;
      this._controlBlocked = false;
      const status = resp.status;
      const aiMove = resp.aiMove;
      if (status === 2) {
        // Illegal move, reset back to the original move.
        cell.setState(originalState);
        this._message = 'Illegal move!';
        return;
      }
      if (status === 1 || status === -1) {
        // Someone wins
        if (aiMove[0] !== -1 || aiMove[1] !== -1) {
          // Player does not win, let AI make the last move.
          this.board.removeHighlights();
          const aiCell = this.board.getCell(aiMove);
          aiCell.setState(-myIdentity);
          aiCell.markHighlight();
        }
        this._message = status === 1 ? 'Black wins!' : 'White wins!';
        this._blockerActive = true;
      } else {
        // A normal move in the game.
        this.board.currentBigSquareLegalPosition = resp.currentBigSquareLegalPosition;
        this.board.removeHighlights();
        const aiCell = this.board.getCell(aiMove);
        aiCell.setState(-myIdentity);
        aiCell.markHighlight();
        this._message = 'Select your next move.';
        this._aiWinningProbability = resp.aiWinningProbability;
      }
    });
  }

}
