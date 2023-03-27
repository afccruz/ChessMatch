package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.enums.Color;
import chess.pieces.King;
import chess.pieces.Pawn;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
    private int turn;
    private boolean check;
    private boolean checkMate;
    private Color currentPlayer;
    private Board board;
    private List<Piece> piecesOnTheBoard;
    private List<Piece> capturedPieces;

    public ChessMatch() {
        // Create a 8x8 board
        this.board = new Board(8, 8);
        this.turn = 1;
        this.check = false;
        this.checkMate = false;
        this.currentPlayer = Color.WHITE;
        this.piecesOnTheBoard = new ArrayList<>();
        this.capturedPieces = new ArrayList<>();

        initialSetup();
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck() {
        return check;
    }

    public boolean getCheckMate() {
        return checkMate;
    }

    /**
     * Get the pieces of each position to print
     *
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
    private void initialSetup() {
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK));

    }

    /**
     * Place the piece on the board convert the human-readable position to the matrix position
     *
     * @param column
     * @param row
     * @param piece
     */
    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPosition());
        piecesOnTheBoard.add(piece);
    }

    /**
     * Perform chess move
     *
     * @param startPosition
     * @param targetPosition
     * @return
     */
    public ChessPiece performChessMove(ChessPosition startPosition, ChessPosition targetPosition) {
        Position source = startPosition.toPosition();
        Position target = targetPosition.toPosition();

        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPlayer)) {
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        this.check = testCheck(opponent(currentPlayer)) ? true : false;

        if (testCheckMate(opponent(currentPlayer))) this.checkMate = true;
        else nextTurn();

        return (ChessPiece) capturedPiece;
    }

    /**
     * Make the chess move requested by the user
     *
     * @param source
     * @param target
     * @return
     */
    private Piece makeMove(Position source, Position target) {
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();

        Piece capturedPiece = board.removePiece(target);

        board.placePiece(p, target);

        if (capturedPiece != null) {
            piecesOnTheBoard.remove(capturedPiece);
            this.capturedPieces.add(capturedPiece);
        }

        return capturedPiece;
    }

    /**
     * Undo previous move
     *
     * @param source
     * @param target
     * @param capturedPiece
     */
    private void undoMove(Position source, Position target, Piece capturedPiece) {
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCount();

        board.placePiece(p, source);

        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add(capturedPiece);
        }
    }

    /**
     * Validate if a piece exists in the required position
     *
     * @param position
     */
    private void validateSourcePosition(Position position) {
        ChessPiece p = (ChessPiece) board.piece(position);

        if (!board.thereIsAPiece(position)) {
            throw new ChessException("There is no piece on source position");
        }
        if (p.getColor() != currentPlayer) {
            throw new ChessException("The chosen piece is not yours");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible moves for the chosen piece");
        }
    }

    /**
     * Validate if the destination is valid or not
     *
     * @param source
     * @param target
     */
    private void validateTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMove(target)) {
            throw new ChessException("The piece cannot move to the target position");
        }
    }

    /**
     * Returns all the possible moves of the piece
     *
     * @param sourcePosition
     * @return
     */
    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPosition();

        validateSourcePosition(position);

        return board.piece(position).possibleMoves();
    }

    /**
     * Move to the next turn of the game
     */
    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color) {
        return color == Color.WHITE ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        List<Piece> pieces = (piecesOnTheBoard.stream().filter(x -> ((ChessPiece) x).getColor() == color)).collect(Collectors.toList());

        for (Piece p : pieces) {
            if (p instanceof King) return (ChessPiece) p;
        }

        throw new IllegalStateException("There is no King " + color + " in the board");
    }

    private boolean testCheck(Color color) {
        Position kingPos = king(color).getChessPosition().toPosition();

        List<Piece> opponentPieces = (piecesOnTheBoard.stream()
                .filter(x -> ((ChessPiece) x)
                        .getColor() == opponent(color)))
                .collect(Collectors.toList());

        for (Piece p : opponentPieces) {
            boolean[][] mat = p.possibleMoves();

            if (mat[kingPos.getRow()][kingPos.getColumn()]) return true;
        }

        return false;
    }

    private boolean testCheckMate(Color color) {
        if (!testCheck(color)) return false;

        List<Piece> list = (piecesOnTheBoard.stream()
                .filter(x -> ((ChessPiece) x)
                        .getColor() == color))
                .collect(Collectors.toList());

        for (Piece p : list) {
            boolean[][] mat = p.possibleMoves();

            for (int i = 0; i < board.getRows(); i++) {
                for (int j = 0; j < board.getColumns(); j++) {
                    if (mat[i][j]) {
                        Position source = ((ChessPiece) p).getChessPosition().toPosition();
                        Position target = new Position(i, j);

                        Piece capturePiece = makeMove(source, target);

                        boolean testCheck = testCheck(color);

                        undoMove(source, target, capturePiece);

                        if (!testCheck) {
                            return false;
                        }
                    }
                }
            }
        }

        return true;
    }
}
