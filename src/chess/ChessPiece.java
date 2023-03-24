package chess;

import boardgame.Board;
import boardgame.Piece;
import chess.enums.Color;

public class ChessPiece extends Piece {
    private Color color;

    public ChessPiece(Board board, Color color) {
        super(board);
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "Position: [" + position.getRow() + ", " + position.getColumn() + "]" +
                " Color:" + color;
    }
}
