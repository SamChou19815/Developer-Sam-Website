import { Injectable } from '@angular/core';
import { CellState } from './cell-state';
import { TenBoard, TenServerResponse } from './ten-board';
import { TenNetworkService } from './ten-network.service';

@Injectable({
  providedIn: 'root'
})
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
  message = 'Select your first move.';
  /**
   * The probability of AI winning the game.
   * @type {number}
   */
  aiWinningProbability = 50;
  /**
   * Whether the blocker should be active.
   * @type {boolean}
   */
  blockerActive = false;
  /**
   * Whether the controls should be blocked.
   */
  controlBlocked = false;

  /**
   * Initialize itself with injected ten network service.
   *
   * @param {TenNetworkService} networkService the injected ten network service.
   */
  constructor(private networkService: TenNetworkService) {
  }

  /**
   * Reset the data to the good default one and select a side for a new round of game.
   *
   * @param {number} side 1 for black, -1 for white.
   */
  resetDataAndSelectSide(side: number): void {
    this.message = 'Select your first move.';
    this.aiWinningProbability = 50;
    this.blockerActive = false;
    this.board.resetBoard(side);
    if (side === -1) {
      // AI will always choose this.
      // Don't send a request to the server.
      this.board.getCell([4, 4]).setState(1);
      this.board.currentBigSquareLegalPosition = 4;
    }
  }

  /**
   * Asynchronously submit a move to the server.
   *
   * @param {number} bigSquare the index of the big square.
   * @param {number} smallSquare the index of the small square inside the big square.
   */
  private async asyncSubmitMove(bigSquare: number, smallSquare: number): Promise<void> {
    const cell: CellState = this.board.getCell([bigSquare, smallSquare]);
    const originalState: number = cell.getState();
    if (originalState !== 0) {
      this.message = 'Illegal move!';
      return;
    }
    const myIdentity = this.board.currentPlayerIdentity;
    const clientMove = this.board.clientMove(bigSquare, smallSquare);
    cell.setState(myIdentity);
    this.message = 'Waiting for server response...';
    this.blockerActive = true;
    this.controlBlocked = true;
    // Setup all the current status, waiting for server response.
    const resp: TenServerResponse = await this.networkService.getGameResponse(clientMove);
    this.blockerActive = false;
    this.controlBlocked = false;
    const status = resp.status;
    const aiMove = resp.aiMove;
    if (status === 2) {
      // Illegal move, reset back to the original move.
      cell.setState(originalState);
      this.message = 'Illegal move!';
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
      this.message = status === 1 ? 'Black wins!' : 'White wins!';
      this.blockerActive = true;
    } else {
      // A normal move in the game.
      this.board.currentBigSquareLegalPosition = resp.currentBigSquareLegalPosition;
      this.board.removeHighlights();
      const aiCell = this.board.getCell(aiMove);
      aiCell.setState(-myIdentity);
      aiCell.markHighlight();
      this.message = 'Select your next move.';
      this.aiWinningProbability = resp.aiWinningProbability;
    }
  }

  /**
   * Submit a move to the server.
   *
   * @param {number} bigSquare the index of the big square.
   * @param {number} smallSquare the index of the small square inside the big square.
   */
  submitMove(bigSquare: number, smallSquare: number): void {
    this.asyncSubmitMove(bigSquare, smallSquare).then(() => {
    });
  }

}
