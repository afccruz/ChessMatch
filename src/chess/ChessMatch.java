package chess;

import boardgame.Board;
import boardgame.Position;
import chess.enums.Color;
import chess.pieces.King;
import chess.pieces.Rook;

public class ChessMatch {
    private int turn;
    private Board board;

    public ChessMatch() {
        // Create an 8x8 board
        this.board = new Board(8, 8);

        initialSetup();
    }

    /**
     * Get the pieces of each position to print
     * @return matrix of pieces
     */
    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];

        for (int i = 0; i < board.getRows(); i++) {
            for (int j = 0; j < board.getColumns(); j++) {
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }

        return mat;
    }

    /**
     * Creates the pieces in the initial position
     */
    private void initialSetup(){
        board.placePiece(new Rook(board, Color.WHITE), new Position(2,1));
        board.placePiece(new King(board, Color.BLACK), new Position(0 ,4));
        board.placePiece(new King(board, Color.WHITE), new Position(7 ,4));
    }
}
