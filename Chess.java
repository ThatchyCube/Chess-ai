import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

public class Chess {
	// Execute application
	public static void main(String args[]) {
		// Build the frame
  		JFrame frame = new JFrame("Chess");
  		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	

  		// Build the board
  		ChessBoard board = new ChessBoard();
		board.setBackground(new Color(33, 33, 33));

  		// Add the board to the frame and make it visible
  		frame.add(board);
		
		//frame.setSize(900, 900);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);

		// Set a minimum size for the frame
		frame.setMinimumSize(new Dimension(600, 600));

		// Make the frame visible
  		frame.setVisible(true);

	}
}

// Custom panel to draw on (Creates the board)
class ChessBoard extends JPanel {
	// Private data member variables

	// Height and width of the panel
	private int panelWidth, panelHeight;

	// For keeping track of rows and cols
	private int rows = 8, cols = 8;

	// Size and position of each square in the board
	private int sizeSquares = 0, squareX = 0, squareY = 0;
	boolean setPiecesBegin = false;

	// Array to keep track of the piece positions
	private Piece[][] pieces;

	// Variables to keep track of clicks on the board
	private int oldRowSelected, oldColSelected;
	private int newRowSelected, newColSelected;

	// Cache for resized images
    private Map<ImageIcon, ImageIcon> resizedImageCache = new HashMap<>();

 	// Define variables for keeping track of turns
	private boolean isWhiteTurn = true;

	// Declare as a class member
	private JLabel turnIndicator;

	private List<Point> currentLegalMoves = new ArrayList<>();

	// Nested class inheritance hierarchy for making different pieces
	// For building pieces
	abstract class Piece {
		// Information about the piece
		
		// Piece position, color, png (for display) and type
		protected int positionX, positionY;
		protected String color;
		protected ImageIcon displayPiece;
		protected String pieceType = "";

		// For tracking if the current piece has been moved 
		protected boolean movedAlready = false;

		// For king pieces
		protected boolean canCastle = false;

		// Checks for valid move for current piece
		protected abstract boolean isValid();

		protected boolean isOnDiagonal(int r, int c) {
			// For looping between the squares
			int loopRow = r, loopCol = c;

			// For moving to different squares
			int rowPos = oldRowSelected, colPos = oldColSelected;

			// Check if squares on same diagnonal
			for (int i = oldColSelected; i != newColSelected + loopCol; i += loopCol) {
				if (rowPos == newRowSelected && colPos == newColSelected) 
					return true;
				// Increment variables
				rowPos += loopRow;
				colPos += loopCol;
			}
			return false;
		}
	}

	// For white pieces
	class whitePawn extends Piece {
		// Constructor
		public whitePawn() {
			positionX = 0;
			positionY = 0;
			color = "white";
			displayPiece = new ImageIcon(getClass().getResource("img/wP.png"));
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check for diagonal capture
			if (newRowSelected == oldRowSelected - 1
				&& (newColSelected == oldColSelected + 1
				|| newColSelected == oldColSelected - 1)) {
					if (pieces[newRowSelected][newColSelected] != null 
						&& pieces[newRowSelected][newColSelected].color == "black") {
						// Black piece can be diagnally captured
						movedAlready = true;
						return true;
				}
			}

			// Check if a piece is blocking the pawn
			if (pieces[newRowSelected][newColSelected] != null)
				return false;

			// Check if the pawn has been moved
			if (!movedAlready) {
				// Pawn has option to move two squares
				if (newColSelected == oldColSelected
					&& (newRowSelected == oldRowSelected - 2
					|| newRowSelected == oldRowSelected - 1)) {
					// Correct move made
					movedAlready = true;
					return true;
				} else 
					return false;
			} else {
				if (newColSelected == oldColSelected
					&& newRowSelected == oldRowSelected - 1)
					return true;
				else 
					return false;
			}
		}
		// Method to get legal moves
		public List<Point> getLegalMoves() {
			List<Point> moves = new ArrayList<>();
			int x = positionX / sizeSquares;
			int y = positionY / sizeSquares;

			// Forward move by one square
			if (isMoveLegal(x, y - 1)) {
				moves.add(new Point(x, y - 1));
			}

			// First move: forward by two squares
			if (!movedAlready && isMoveLegal(x, y - 2)) {
				moves.add(new Point(x, y - 2));
			}

			// Capture moves
			if (canCapture(x + 1, y - 1)) {
				moves.add(new Point(x + 1, y - 1));
			}
			if (canCapture(x - 1, y - 1)) {
				moves.add(new Point(x - 1, y - 1));
			}

			return moves;
		}

		private boolean isMoveLegal(int x, int y) {
			// Check if the move is within board limits and not blocked
			return x >= 0 && x < cols && y >= 0 && y < rows && pieces[y][x] == null;
		}

		private boolean canCapture(int x, int y) {
			// Check if within board limits and if there's an enemy piece to capture
			return x >= 0 && x < cols && y >= 0 && y < rows && pieces[y][x] != null 
				&& pieces[y][x].color.equals("black");
		}

	}

	class whiteKing extends Piece {
		// Constructor
		public whiteKing() {
			positionX = 0;
			positionY = 0;
			color = "white";
			displayPiece = new ImageIcon(getClass().getResource("img/wK.png"));
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check for ability to castle
			if (newRowSelected == oldRowSelected 
				&& (newColSelected == oldColSelected + 2 || newColSelected == oldColSelected + 3)) {

				// Check for proper castling conditions to the right
				if ((pieces[newRowSelected][oldColSelected + 1] == null && pieces[newRowSelected][oldColSelected + 2] == null)
					&& pieces[newRowSelected][oldColSelected + 3].pieceType == "whiteRook") {
					// Check the king hasn't moved before
					if (movedAlready == false) {
						canCastle = true;
						movedAlready = true;
						return true;
					}
				}
			} else if (newRowSelected == oldRowSelected
				&& (newColSelected == oldColSelected - 2 || newColSelected == oldColSelected - 3
					|| newColSelected == oldColSelected - 4)) {
					
				// Check for proper castling conditions to the left
				if ((pieces[newRowSelected][oldColSelected - 1] == null && pieces[newRowSelected][oldColSelected - 2] == null 
					&& pieces[newRowSelected][oldColSelected - 3] == null) && pieces[newRowSelected][oldColSelected - 4].pieceType == "whiteRook") {
					// Check the king hasn't moved before
					if (movedAlready == false) {
						canCastle = true;
						movedAlready = true;
						return true;
					}
				}	
			}

			// For tracking distance between squares
			int distanceRow = 0, distanceCol = 0;

			// Get positive distances between the squares
			if (newRowSelected > oldRowSelected)
				distanceRow = newRowSelected - oldRowSelected;
			else 
				distanceRow = oldRowSelected - newRowSelected;
			if (newColSelected > oldColSelected)
				distanceCol = newColSelected - oldColSelected;
			else 
				distanceCol = oldColSelected - newColSelected;

			// Check if the squares are one more away
			if (distanceRow > 1 || distanceCol > 1)
				return false;

			// Check for valid move
			if (pieces[newRowSelected][newColSelected] != null 
				&& pieces[newRowSelected][newColSelected].color == "black") {
				movedAlready = true;
				return true;
			} else if (pieces[newRowSelected][newColSelected] == null) {
				movedAlready = true;
				return true;
			}
			
			// Otherwise
			return false;
		}
	}

	class whiteQueen extends Piece {
		// Constructor
		public whiteQueen() {
			positionX = 0;
			positionY = 0;
			color = "white";
			displayPiece = new ImageIcon(getClass().getResource("img/wQ.png"));
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check if the square is a black piece
			if (pieces[newRowSelected][newColSelected] != null
				&& pieces[newRowSelected][newColSelected].color == "black") {
				if (isPathClear())
					return true;
			} else if (pieces[newRowSelected][newColSelected] == null) {
				// Queen moving to an empty square
				if (isPathClear())
					return true;	
			}
			return false;
		}
		
		private boolean isPathClear() {
			// For looping between squares clicked
			int loopTo = 0;

			// Determine the direction of the loop
			if (newColSelected < oldColSelected || newRowSelected < oldRowSelected)
				loopTo = 1;
			else	
				loopTo = -1;
				
			if (newRowSelected == oldRowSelected) {
				// Check for a blocking piece
				for (int i = newColSelected + loopTo; i != oldColSelected; i += loopTo) {
					if (pieces[newRowSelected][i] != null)
						return false;
				}
			} else if (newColSelected == oldColSelected) {
				// Check for a blocking piece
				for (int i = newRowSelected + loopTo; i != oldRowSelected; i += loopTo) {
					if (pieces[i][newColSelected] != null)
						return false;
				}
			} else {
				// Check for diagonal moves

				// For tracking direction of the diagonal
				int loopRow = 0, loopCol = 0;

				// Get the direction of the diagonal
				if (oldRowSelected < newRowSelected)  
					loopRow = 1;
				else
					loopRow = -1;
				if (oldColSelected < newColSelected)
					loopCol = 1;
				else 
					loopCol = -1;

				// Variables to loop through the board
				int rowPos = oldRowSelected + loopRow, colPos = oldColSelected + loopCol;

				// Check if the new and old square are on same diagonal
				if (isOnDiagonal(loopRow, loopCol)) {
					// Search for blocking pieces
					for (int i = colPos; i != newColSelected; i += loopCol) {
						if (pieces[rowPos][colPos] != null) 
							return false;

						// Increment tracking variables
						rowPos += loopCol;
						colPos += loopCol;
					} 
					return true;
				}
			}

			// Check if squares are in the same row or column
			if (newRowSelected != oldRowSelected && newColSelected != oldColSelected)
				return false;

			// Otherwise
			return true;
		}
	}

	class whiteRook extends Piece {
	    // Constructor
	    public whiteRook() {
	        positionX = 0;
	        positionY = 0;
	        color = "white";
	        displayPiece = new ImageIcon(getClass().getResource("img/wR.png"));
			pieceType = "whiteRook";
	    }

	    // Override for valid moves
	    @Override
	    protected boolean isValid() {
	        // Rook moves horizontally or vertically
	        if (newRowSelected == oldRowSelected || newColSelected == oldColSelected) {
	            // Check if the path is clear
	            if (isPathClear(oldRowSelected, oldColSelected, newRowSelected, newColSelected)) {
	                return true;
	            }
	        }
	        return false;
	    }

		private boolean isPathClear(int oldRow, int oldCol, int newRow, int newCol) {
			int rowStep = Integer.compare(newRow, oldRow);
			int colStep = Integer.compare(newCol, oldCol);

			// Start checking from the next cell in the direction of the movement
			int currentRow = oldRow + rowStep;
			int currentCol = oldCol + colStep;

			while (currentRow != newRow || currentCol != newCol) {
				if (pieces[currentRow][currentCol] != null) {
					return false;
				}
				currentRow += rowStep;
				currentCol += colStep;
			}

			// Path is clear
			return true;
		}

		public List<Point> getLegalMoves() {
			List<Point> moves = new ArrayList<>();
			int x = positionX / sizeSquares;
			int y = positionY / sizeSquares;

			// Add all vertical and horizontal moves
			addMovesInDirection(moves, x, y, 0, -1);
			addMovesInDirection(moves, x, y, 0, 1);
			addMovesInDirection(moves, x, y, -1, 0);
			addMovesInDirection(moves, x, y, 1, 0);

			return moves;
		}

		private void addMovesInDirection(List<Point> moves, int x, int y, int dx, int dy) {
			int newX = x + dx;
			int newY = y + dy;
			while (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
				if (pieces[newY][newX] == null) {
					moves.add(new Point(newX, newY));
				} else {
					if (!pieces[newY][newX].color.equals("white")) {
						moves.add(new Point(newX, newY));
					}
					break;
				}
				newX += dx;
				newY += dy;
			}
		}
	}

	class whiteKnight extends Piece {
	    // Constructor
	    public whiteKnight() {
	        positionX = 0;
	        positionY = 0;
	        color = "white";
	        displayPiece = new ImageIcon(getClass().getResource("img/wN.png"));
	    }

	    // Override for valid moves
	    @Override
	    protected boolean isValid() {
	        // Calculate the difference in position
	        int rowDiff = Math.abs(newRowSelected - oldRowSelected);
	        int colDiff = Math.abs(newColSelected - oldColSelected);

	        // Check for Lshaped move
	        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
	            // Check if the destination square is not occupied by a piece of the same color
	            if (pieces[newRowSelected][newColSelected] == null ||
	                !pieces[newRowSelected][newColSelected].color.equals("white")) {
	                return true;
	            }
	        }
	        return false;
	    }
		// Method to get legal moves
		public List<Point> getLegalMoves() {
			List<Point> moves = new ArrayList<>();
			// Potential moves of a knight
			int[][] knightMoves = {
				{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
				{1, -2}, {1, 2}, {2, -1}, {2, 1}
			};
			int x = positionX / sizeSquares;
			int y = positionY / sizeSquares;
			for (int[] move : knightMoves) {
				int newX = x + move[0];
				int newY = y + move[1];
				if (newX >= 0 && newX < cols && newY >= 0 && newY < rows &&
					(pieces[newY][newX] == null || !pieces[newY][newX].color.equals("black"))) {
					moves.add(new Point(newX, newY));
				}
			}
			return moves;
		}
	}

	class whiteBishop extends Piece {
		public whiteBishop() {
			positionX = 0;
			positionY = 0;
			color = "white";
			displayPiece = new ImageIcon(getClass().getResource("img/wB.png"));
		}

		// Override for valid moves
		@Override
		protected boolean isValid() {
			int rowDiff = Math.abs(newRowSelected - oldRowSelected);
			int colDiff = Math.abs(newColSelected - oldColSelected);

			// Bishop moves only diagonally
			if (rowDiff == colDiff) {
				// Check if the path is clear
				if (isPathClear(oldRowSelected, oldColSelected, newRowSelected, newColSelected)) {
					return true;
				}
			}
			return false;
		}

		// Check if the path is clear
		private boolean isPathClear(int oldRow, int oldCol, int newRow, int newCol) {
			int rowStep = Integer.compare(newRow, oldRow);
			int colStep = Integer.compare(newCol, oldCol);

			int currentRow = oldRow + rowStep;
			int currentCol = oldCol + colStep;

			// Check each square along the path for any piece
			while (currentRow != newRow && currentCol != newCol) {
				if (pieces[currentRow][currentCol] != null) {
					return false;
				}
				currentRow += rowStep;
				currentCol += colStep;
			}

			// Check if the dest square has a same color piece
			if (pieces[newRow][newCol] != null && pieces[newRow][newCol].color.equals("white")) {
				return false;
			}
			return true;
		}
		// Method to get legal moves for the white bishop
		public List<Point> getLegalMoves() {
			List<Point> moves = new ArrayList<>();
			int x = positionX / sizeSquares;
			int y = positionY / sizeSquares;

			// Check all four diagonal directions
			addDiagonalMoves(moves, x, y, 1, 1);
			addDiagonalMoves(moves, x, y, 1, -1);
			addDiagonalMoves(moves, x, y, -1, 1);
			addDiagonalMoves(moves, x, y, -1, -1);

			return moves;
		}

		private void addDiagonalMoves(List<Point> moves, int x, int y, int dx, int dy) {
			int newX = x + dx;
			int newY = y + dy;
			while (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
				if (pieces[newY][newX] == null) {
					moves.add(new Point(newX, newY));
				} else {
					if (pieces[newY][newX].color.equals("black")) {
						moves.add(new Point(newX, newY));
					}
					break;
				}
				newX += dx;
				newY += dy;
			}
		}

	}

	// For black pieces
	class blackPawn extends Piece {
		// Constructor
		public blackPawn() {
			positionX = 0;
			positionY = 0;
			color = "black";
			displayPiece = new ImageIcon(getClass().getResource("img/bP.png"));
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check for diagonal capture
			if (newRowSelected == oldRowSelected + 1
				&& (newColSelected == oldColSelected + 1
				|| newColSelected == oldColSelected - 1)) {
					if (pieces[newRowSelected][newColSelected] != null 
						&& pieces[newRowSelected][newColSelected].color == "white") {
						// Black piece can be diagnally captured
						movedAlready = true;
						return true;
				}
			}
			
			// Check if a piece is blocking the pawn
			if (pieces[newRowSelected][newColSelected] != null)
				return false;

			// Check if the pawn has been moved
			if (!movedAlready) {
				// Pawn has option to move two squares
				if (newColSelected == oldColSelected
					&& (newRowSelected == oldRowSelected + 2
					|| newRowSelected == oldRowSelected + 1)) {
					// Correct move made
					movedAlready = true;
					return true;
				} else 
					return false;
			} else {
				if (newColSelected == oldColSelected
					&& newRowSelected == oldRowSelected + 1)
					return true;
				else 
					return false;
			}
		}

		// Method to get legal moves
		public List<Point> getLegalMoves() {
			List<Point> moves = new ArrayList<>();
			int x = positionX / sizeSquares;
			int y = positionY / sizeSquares;

			// Forward move by one square
			if (isMoveLegal(x, y + 1)) {
				moves.add(new Point(x, y + 1));
			}

			// First move: forward by two squares
			if (!movedAlready && isMoveLegal(x, y + 2)) {
				moves.add(new Point(x, y + 2));
			}

			// Capture moves
			if (canCapture(x + 1, y + 1)) {
				moves.add(new Point(x + 1, y + 1));
			}
			if (canCapture(x - 1, y + 1)) {
				moves.add(new Point(x - 1, y + 1));
			}

			return moves;
		}

		private boolean isMoveLegal(int x, int y) {
			// Check if the move is within board limits and not blocked
			return x >= 0 && x < cols && y >= 0 && y < rows && pieces[y][x] == null;
		}

		private boolean canCapture(int x, int y) {
			// Check if within board limits and if there's an enemy piece to capture
			return x >= 0 && x < cols && y >= 0 && y < rows && pieces[y][x] != null 
				&& pieces[y][x].color.equals("white");
		}
	}

	class blackKing extends Piece {
		// Constructor
		public blackKing() {
			positionX = 0;
			positionY = 0;
			color = "black";
			displayPiece = new ImageIcon(getClass().getResource("img/bK.png"));
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check for ability to castle
			if (newRowSelected == oldRowSelected 
				&& (newColSelected == oldColSelected + 2 || newColSelected == oldColSelected + 3)) {

				// Check for proper castling conditions to the right
				if ((pieces[newRowSelected][oldColSelected + 1] == null && pieces[newRowSelected][oldColSelected + 2] == null)
					&& pieces[newRowSelected][oldColSelected + 3].pieceType == "blackRook") {
					// Check the king hasn't moved before
					if (movedAlready == false) {
						canCastle = true;
						movedAlready = true;
						return true;
					}
				}
			} else if (newRowSelected == oldRowSelected
				&& (newColSelected == oldColSelected - 2 || newColSelected == oldColSelected - 3
					|| newColSelected == oldColSelected - 4)) {
					
				// Check for proper castling conditions to the left
				if ((pieces[newRowSelected][oldColSelected - 1] == null && pieces[newRowSelected][oldColSelected - 2] == null 
					&& pieces[newRowSelected][oldColSelected - 3] == null) && pieces[newRowSelected][oldColSelected - 4].pieceType == "blackRook") {
					// Check the king hasn't moved before
					if (movedAlready == false) {
						canCastle = true;
						movedAlready = true;
						return true;
					}
				}	
			}

			// For tracking distance between squares
			int distanceRow = 0, distanceCol = 0;

			// Get positive distances between the squares
			if (newRowSelected > oldRowSelected)
				distanceRow = newRowSelected - oldRowSelected;
			else 
				distanceRow = oldRowSelected - newRowSelected;
			if (newColSelected > oldColSelected)
				distanceCol = newColSelected - oldColSelected;
			else 
				distanceCol = oldColSelected - newColSelected;

			// Check if the squares are one more away
			if (distanceRow > 1 || distanceCol > 1)
				return false;

			// Check for valid move
			if (pieces[newRowSelected][newColSelected] != null 
				&& pieces[newRowSelected][newColSelected].color == "white") {
				movedAlready = true;
				return true;
			} else if (pieces[newRowSelected][newColSelected] == null) {
				movedAlready = true;
				return true;
			}
			
			// Otherwise
			return false;
		}
	}

	class blackQueen extends Piece {
		// Constructor
		public blackQueen() {
			positionX = 0;
			positionY = 0;
			color = "black";
			displayPiece = new ImageIcon(getClass().getResource("img/bQ.png"));
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check if the square is a black piece
			if (pieces[newRowSelected][newColSelected] != null
				&& pieces[newRowSelected][newColSelected].color == "white") {
				if (isPathClear())
					return true;
			} else if (pieces[newRowSelected][newColSelected] == null) {
				// Queen moving to an empty square
				if (isPathClear())
					return true;	
			}
			return false;
		}
		
		private boolean isPathClear() {
			// For looping between squares clicked
			int loopTo = 0;

			// Determine the direction of the loop
			if (newColSelected < oldColSelected || newRowSelected < oldRowSelected)
				loopTo = 1;
			else	
				loopTo = -1;

			// Check if squares are in the same row or column
			if (newRowSelected == oldRowSelected) {
				// Check for a blocking piece
				for (int i = newColSelected + loopTo; i != oldColSelected; i += loopTo) {
					if (pieces[newRowSelected][i] != null)
						return false;
				}
			} else if (newColSelected == oldColSelected) {
				// Check for a blocking piece
				for (int i = newRowSelected + loopTo; i != oldRowSelected; i += loopTo) {
					if (pieces[i][newColSelected] != null)
						return false;
				}
			} else {
				// Check for diagonal moves

				// For tracking direction of the diagonal
				int loopRow = 0, loopCol = 0;

				// Get the direction of the diagonal
				if (oldRowSelected < newRowSelected)  
					loopRow = 1;
				else
					loopRow = -1;
				if (oldColSelected < newColSelected)
					loopCol = 1;
				else 
					loopCol = -1;

				// Variables to loop through the board
				int rowPos = oldRowSelected + loopRow, colPos = oldColSelected + loopCol;

				// Check if the new and old square are on same diagonal
				if (isOnDiagonal(loopRow, loopCol)) {
					// Search for blocking pieces
					for (int i = colPos; i != newColSelected; i += loopCol) {
						if (pieces[rowPos][colPos] != null) 
							return false;

						// Increment tracking variables
						rowPos += loopCol;
						colPos += loopCol;
					} 
					return true;
				}
			}

			// Check if squares are in the same row or column
			if (newRowSelected != oldRowSelected && newColSelected != oldColSelected)
				return false;

			// Otherwise
			return true;
		}
	}

	class blackRook extends Piece {
	    // Constructor
	    public blackRook() {
	        positionX = 0;
	        positionY = 0;
	        color = "black";
	        displayPiece = new ImageIcon(getClass().getResource("img/bR.png"));
			pieceType = "blackRook";
	    }

	    // Override for valid moves
	    @Override
	    protected boolean isValid() {
	        // Rook moves horizontally or vertically
	        if (newRowSelected == oldRowSelected || newColSelected == oldColSelected) {
	            // Check if the path is clear
	            if (isPathClear(oldRowSelected, oldColSelected, newRowSelected, newColSelected)) {
	                return true;
	            }
	        }
	        return false;
	    }

		private boolean isPathClear(int oldRow, int oldCol, int newRow, int newCol) {
			int rowStep = Integer.compare(newRow, oldRow);
			int colStep = Integer.compare(newCol, oldCol);

			// Start checking from the next cell in the direction of the movement
			int currentRow = oldRow + rowStep;
			int currentCol = oldCol + colStep;

			while (currentRow != newRow || currentCol != newCol) {
				// If a piece is encountered, the path is not clear
				if (pieces[currentRow][currentCol] != null) {
					return false;
				}
				currentRow += rowStep;
				currentCol += colStep;
			}

			return true;
		}
		public List<Point> getLegalMoves() {
			List<Point> moves = new ArrayList<>();
			int x = positionX / sizeSquares;
			int y = positionY / sizeSquares;

			// Add all vertical and horizontal moves
			addMovesInDirection(moves, x, y, 0, -1);
			addMovesInDirection(moves, x, y, 0, 1);
			addMovesInDirection(moves, x, y, -1, 0);
			addMovesInDirection(moves, x, y, 1, 0);

			return moves;
		}

		private void addMovesInDirection(List<Point> moves, int x, int y, int dx, int dy) {
			int newX = x + dx;
			int newY = y + dy;
			while (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
				if (pieces[newY][newX] == null) {
					moves.add(new Point(newX, newY));
				} else {
					if (!pieces[newY][newX].color.equals("white")) {
						moves.add(new Point(newX, newY));
					}
					break;
				}
				newX += dx;
				newY += dy;
			}
		}
	}

	class blackKnight extends Piece {
	    // Constructor
	    public blackKnight() {
	        positionX = 0;
	        positionY = 0;
	        color = "black";
	        displayPiece = new ImageIcon(getClass().getResource("img/bN.png"));
	    }

	    // Override for valid moves
	    @Override
	    protected boolean isValid() {
	        // Difference in position
	        int rowDiff = Math.abs(newRowSelected - oldRowSelected);
	        int colDiff = Math.abs(newColSelected - oldColSelected);

	        // Check for Lshaped move
	        if ((rowDiff == 2 && colDiff == 1) || (rowDiff == 1 && colDiff == 2)) {
	            // Check if the destination square is not a piece of the same color
	            if (pieces[newRowSelected][newColSelected] == null ||
	                !pieces[newRowSelected][newColSelected].color.equals("black")) {
	                return true;
	            }
	        }
	        return false;
	    }
		// Method to get legal moves
		public List<Point> getLegalMoves() {
			List<Point> moves = new ArrayList<>();
			// Potential moves of a knight
			int[][] knightMoves = {
				{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2},
				{1, -2}, {1, 2}, {2, -1}, {2, 1}
			};
			int x = positionX / sizeSquares;
			int y = positionY / sizeSquares;

			for (int[] move : knightMoves) {
				int newX = x + move[0];
				int newY = y + move[1];

				if (newX >= 0 && newX < cols && newY >= 0 && newY < rows &&
					(pieces[newY][newX] == null || !pieces[newY][newX].color.equals("white"))) {
					moves.add(new Point(newX, newY));
				}
			}
			return moves;
		}

	}

	class blackBishop extends Piece {
		// Constructor
		public blackBishop() {
			positionX = 0;
			positionY = 0;
			color = "black";
			displayPiece = new ImageIcon(getClass().getResource("img/bB.png"));
		}

		// Override for valid moves
		@Override
		protected boolean isValid() {
			int rowDiff = Math.abs(newRowSelected - oldRowSelected);
			int colDiff = Math.abs(newColSelected - oldColSelected);

			// Bishop moves only diagonally
			if (rowDiff == colDiff) {
				// Check if the path is clear
				if (isPathClear(oldRowSelected, oldColSelected, newRowSelected, newColSelected)) {
					return true;
				}
			}
			return false;
		}

		// Check if the path is clear
		private boolean isPathClear(int oldRow, int oldCol, int newRow, int newCol) {
			int rowStep = Integer.compare(newRow, oldRow);
			int colStep = Integer.compare(newCol, oldCol);

			int currentRow = oldRow + rowStep;
			int currentCol = oldCol + colStep;

			// Check each square along the path for any piece
			while (currentRow != newRow && currentCol != newCol) {
				if (pieces[currentRow][currentCol] != null) {
					return false;
				}
				currentRow += rowStep;
				currentCol += colStep;
			}

			// Check if the dest square has a same color piece
			if (pieces[newRow][newCol] != null && pieces[newRow][newCol].color.equals("black")) {
				return false;
			}
			return true;
		}
		// Method to get legal moves for the white bishop
		public List<Point> getLegalMoves() {
			List<Point> moves = new ArrayList<>();
			int x = positionX / sizeSquares;
			int y = positionY / sizeSquares;

			// Check all four diagonal directions
			addDiagonalMoves(moves, x, y, 1, 1);
			addDiagonalMoves(moves, x, y, 1, -1);
			addDiagonalMoves(moves, x, y, -1, 1);
			addDiagonalMoves(moves, x, y, -1, -1);

			return moves;
		}

		private void addDiagonalMoves(List<Point> moves, int x, int y, int dx, int dy) {
			int newX = x + dx;
			int newY = y + dy;
			while (newX >= 0 && newX < cols && newY >= 0 && newY < rows) {
				if (pieces[newY][newX] == null) {
					moves.add(new Point(newX, newY));
				} else {
					if (pieces[newY][newX].color.equals("black")) {
						moves.add(new Point(newX, newY));
					}
					break;
				}
				newX += dx;
				newY += dy;
			}
		}

	}

	// Constructor
	public ChessBoard() {
		// Build a board full of null pieces
		pieces = new Piece[rows][cols];

		// Add the mouse listeners for piece interaction
		addMouseListener(new MouseAdapter() {
			// Override for clicking
			public void mousePressed (MouseEvent e) {
				// Set up tracking variables 
				oldRowSelected = -1;
				oldColSelected = -1;
				newRowSelected = -1;
				newColSelected = -1;

				// Get the position where user clicks
				int mouseX = e.getX();
				int mouseY = e.getY();

				// Check if clicked inside board
				if ((mouseX >= 0 && mouseX <= sizeSquares * 8) &&
					(mouseY >= 0 && mouseY <= sizeSquares * 8)) {
					// Get the icon that was clicked
					oldRowSelected = mouseY / sizeSquares;
					oldColSelected = mouseX / sizeSquares;
				}
				    // Clear previous legal moves
					currentLegalMoves.clear();

				// Check if a piece is selected
				if (oldRowSelected != -1 && oldColSelected != -1
					&& pieces[oldRowSelected][oldColSelected] != null) {

					// Check if it's white's turn and the piece is white
					if (isWhiteTurn && pieces[oldRowSelected][oldColSelected].color.equals("white")) {
						// Calculate legal moves for white pawns
						if (pieces[oldRowSelected][oldColSelected] instanceof whitePawn) {
							currentLegalMoves = ((whitePawn) pieces[oldRowSelected][oldColSelected]).getLegalMoves();
						}
						// Calculate legal moves for white rooks
						if (pieces[oldRowSelected][oldColSelected] instanceof whiteRook) {
							currentLegalMoves = ((whiteRook) pieces[oldRowSelected][oldColSelected]).getLegalMoves();
						}
					}
					// Check if it's black's turn and the piece is black
					else if (!isWhiteTurn && pieces[oldRowSelected][oldColSelected].color.equals("black")) {
						// Calculate legal moves for black pawns
						if (pieces[oldRowSelected][oldColSelected] instanceof blackPawn) {
							currentLegalMoves = ((blackPawn) pieces[oldRowSelected][oldColSelected]).getLegalMoves();
						}
						// Calculate legal moves for black rooks
						if (pieces[oldRowSelected][oldColSelected] instanceof blackRook) {
							currentLegalMoves = ((blackRook) pieces[oldRowSelected][oldColSelected]).getLegalMoves();
						}
					}

					// Calculate legal moves for knights
					if (pieces[oldRowSelected][oldColSelected] instanceof whiteKnight) {
						currentLegalMoves = ((whiteKnight) pieces[oldRowSelected][oldColSelected]).getLegalMoves();
					} else if (pieces[oldRowSelected][oldColSelected] instanceof blackKnight) {
						currentLegalMoves = ((blackKnight) pieces[oldRowSelected][oldColSelected]).getLegalMoves();
					}

					// Calculate legal moves for bishops
					if (pieces[oldRowSelected][oldColSelected] instanceof whiteBishop) {
						currentLegalMoves = ((whiteBishop) pieces[oldRowSelected][oldColSelected]).getLegalMoves();
					} else if (pieces[oldRowSelected][oldColSelected] instanceof blackBishop) {
						currentLegalMoves = ((blackBishop) pieces[oldRowSelected][oldColSelected]).getLegalMoves();
					}
				}

				repaint();
			}

			// Override for releasing
			@Override
			public void mouseReleased(MouseEvent e) {
				if (oldRowSelected != -1 && oldColSelected != -1) {
					// Get coordinates of current mouse location
					int mouseX = e.getX();
					int mouseY = e.getY();

					// Get the location where the piece is dropped
					if ((mouseX >= 0 && mouseX <= sizeSquares * 8) &&
						(mouseY >= 0 && mouseY <= sizeSquares * 8)) {
						// Get the icon that was clicked
						newRowSelected = mouseY / sizeSquares;
						newColSelected = mouseX / sizeSquares;
					}

					// Check for valid mouse release
					if (pieces[oldRowSelected][oldColSelected] != null 
						&& oldRowSelected != -1 && oldColSelected != -1
						&& newRowSelected != -1 && newColSelected != -1) {
						// Check if the piece was moved onto itself
						if (oldRowSelected == newRowSelected
							&& oldColSelected == newColSelected) {
							// Reset the position
							reset();
							return;
						}

						// Check if the piece belongs to the player depending on turn
						if ((isWhiteTurn && pieces[oldRowSelected][oldColSelected].color.equals("white"))
							|| (!isWhiteTurn && pieces[oldRowSelected][oldColSelected].color.equals("black"))) {

							// Check if move is valid
							if (pieces[oldRowSelected][oldColSelected].isValid()) {
								// Check for attempt to castle
								if (pieces[oldRowSelected][oldColSelected].canCastle) {
									// Check which king is castling	
									if (newColSelected > oldColSelected) {
										// Right castle
										pieces[oldRowSelected][oldColSelected].canCastle = false;
										castleRight();
									} else {
										// Left castle
										pieces[oldRowSelected][oldColSelected].canCastle = false;
										castleLeft();
									}
								} else {
									// Else move the piece
									pieces[newRowSelected][newColSelected] = pieces[oldRowSelected][oldColSelected];
									pieces[oldRowSelected][oldColSelected] = null;
								}

								// Do the turn
								isWhiteTurn = !isWhiteTurn;
							} else {
								reset();
								return;
							}
						} else {
							reset();
							return;
						}
					}
					// Update the turn indicator label
					if (isWhiteTurn) {
						turnIndicator.setText("White's turn");
					} else {
						turnIndicator.setText("Black's turn");
					}
					// Reset row and column trackers
					oldRowSelected = -1;
					oldColSelected = -1;
					newRowSelected = -1;
					newColSelected = -1;


					// Clear legal moves after the piece has been moved
    				currentLegalMoves.clear();

					// Repaint the board
					repaint();
				}
			}
		});

		// Override for dragging
		addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					int mouseX = 0, mouseY = 0;
					// Check for invalid index
					if ((oldRowSelected != -1 && oldColSelected != -1)
						&& (pieces[oldRowSelected][oldColSelected] != null)) {
						// Get the mouse position
						mouseX = e.getX();
						mouseY = e.getY();

						// Get the beginning position
						int originalMouseX = pieces[oldRowSelected][oldColSelected].positionX; 
						int originalMouseY = pieces[oldRowSelected][oldColSelected].positionY; 
						
						// Get the change as the user drags
						int changeX = mouseX - originalMouseX - sizeSquares / 2;
						int changeY = mouseY - originalMouseY - sizeSquares  / 2;

						// Update the position of the piece
						pieces[oldRowSelected][oldColSelected].positionX = (originalMouseX + changeX);
						pieces[oldRowSelected][oldColSelected].positionY = (originalMouseY + changeY);

						repaint();
 				}
			}
		});
		// Initialize the turn indicator label
		turnIndicator = new JLabel("White's turn");
		turnIndicator.setFont(new Font("Serif", Font.BOLD, 20));
		turnIndicator.setForeground(Color.WHITE);
		// Add the label to the ChessBoard panel
		add(turnIndicator);
	}

	// Loads the pieces into the board array
	private void loadPieces() {
		// Set up the black pieces
		pieces[0] = new Piece[] {
			new blackRook(), new blackKnight(), new blackBishop(),
			new blackQueen(), new blackKing(), new blackBishop(),
			new blackKnight(), new blackRook()
		};

		// For the black pawns
		Piece[] blackPawns = new Piece[cols];
		for (int i = 0; i < cols; i++)
			blackPawns[i] = new blackPawn();
		pieces[1] = blackPawns;

		// For the white pawns
		Piece[] whitePawns = new Piece[cols];
		for (int i = 0; i < cols; i++)
			whitePawns[i] = new whitePawn();
		pieces[6] = whitePawns;

		// Set up the white pieces
		pieces[7] = new Piece[] {
			new whiteRook(), new whiteKnight(), new whiteBishop(),
			new whiteQueen(), new whiteKing(), new whiteBishop(),
			new whiteKnight(), new whiteRook()
		};
	}
	
    // Updated resizeIcon method
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        // Check the cache first
        ImageIcon cachedIcon = resizedImageCache.get(icon);
        if (cachedIcon != null) {
            return cachedIcon;
        }

        // Resize the image if not in cache
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(resizedImage);

        // Add to cache and return
        resizedImageCache.put(icon, resizedIcon);
        return resizedIcon;
    }

	// For castling kings to the right
	private void castleRight() {
		// Move the king and the rook
		pieces[oldRowSelected][oldColSelected + 2] = pieces[oldRowSelected][oldColSelected];
		pieces[oldRowSelected][oldColSelected + 1] = pieces[oldRowSelected][oldColSelected + 3];

		// Leave the original squares empty
		pieces[oldRowSelected][oldColSelected] = null;
		pieces[oldRowSelected][oldColSelected + 3] = null;
	}

	// For castling kings to the left
	private void castleLeft() {
		// Move the king and the rook
		pieces[oldRowSelected][oldColSelected - 2] = pieces[oldRowSelected][oldColSelected];
		pieces[oldRowSelected][oldColSelected - 1] = pieces[oldRowSelected][oldColSelected - 4];

		// Leave the original squares empty
		pieces[oldRowSelected][oldColSelected] = null;
		pieces[oldRowSelected][oldColSelected - 4] = null;
	}

	// For resetting a pieces position
	private void reset() {
		// Put the piece back in it's original square and center it
		pieces[oldRowSelected][oldColSelected].positionX = oldColSelected * sizeSquares;
		pieces[oldRowSelected][oldColSelected].positionY = oldRowSelected * sizeSquares;
		repaint();
	}

	// Override for drawing
	protected void paintComponent(Graphics g) {
		// Call superclass's paintComponent
		super.paintComponent(g);

		// Cast g to Graphics 2D
		Graphics2D g2d = (Graphics2D) g;  
		
		// Get width and height of panel
		panelWidth = getWidth();
		panelHeight = getHeight();

		// For getting the color of each square
		int determineColor = 0;    

		// Variables to draw board
		squareX = 0;
		squareY = 0;
		int newSquareSize = ((Math.min(panelWidth, panelHeight) * 4) / 5) / 8;

		// Update cache if square size has changed
		if (this.sizeSquares != newSquareSize) {
			// Clear the cache as the size has changed
			resizedImageCache.clear();
			this.sizeSquares = newSquareSize;
		}

		// For displaying pieces initially
		if (!setPiecesBegin) {
			loadPieces();
			setPiecesBegin = true;
		}

		// Draw the squares
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				// Set the color
				if ((determineColor++) % 2 == 0)
					g2d.setColor(new Color(204, 153, 102));
				else 
					g2d.setColor(new Color(101, 67, 33));

				// Draw each square
				g2d.fillRect(squareX, squareY, sizeSquares, sizeSquares);

				// Set each piece to fit in its square if not being dragged
				if (pieces[i][j] != null && !(i == oldRowSelected && j == oldColSelected)) {
					pieces[i][j].positionX = squareX;
					pieces[i][j].positionY = squareY;
					ImageIcon resizedIcon = resizeIcon(pieces[i][j].displayPiece, sizeSquares, sizeSquares);
					resizedIcon.paintIcon(this, g2d, pieces[i][j].positionX, pieces[i][j].positionY);
				}

				// Move to the next square
				squareX += sizeSquares;
			}
			// Get the color pattern for the next row
			if (i % 2 == 0)
				determineColor = 1;
			else 
				determineColor = 0;

			// Move down a row
			squareY += sizeSquares;
			squareX = 0;
		}

		// Calculate the diameter of the circles
		int circleDiameter = sizeSquares / 3;

		// Draw circles on legal moves
		for (Point move : currentLegalMoves) {
			int moveX = move.x * sizeSquares;
			int moveY = move.y * sizeSquares;

			// Check if the square is either empty or contains an enemy piece
			boolean isSquareEmptyOrEnemy = (pieces[move.y][move.x] == null) || 
										(!pieces[move.y][move.x].color.equals(isWhiteTurn ? "white" : "black"));

			if (isSquareEmptyOrEnemy) {
				// Calculate the top-left corner of the circle to center it in the square
				int circleX = moveX + sizeSquares / 2 - circleDiameter / 2;
				int circleY = moveY + sizeSquares / 2 - circleDiameter / 2;

				g2d.setColor(new Color(0, 0, 0, 128));
				g2d.fillOval(circleX, circleY, circleDiameter, circleDiameter);
			}
		}

		// Ensure the turn indicator label is always visible depending on resolution
		int boardRightEdge = squareX + (sizeSquares * cols);
    	int labelXPosition = boardRightEdge + 20;
    	int labelYPosition = 30;
		// Set the position of the indicator label
		turnIndicator.setBounds(labelXPosition, labelYPosition, turnIndicator.getPreferredSize().width, turnIndicator.getPreferredSize().height);

		// Draw any dragged pieces at the cursor's position
		if (oldRowSelected != -1 && oldColSelected != -1) {
			if (pieces[oldRowSelected][oldColSelected] != null) {
				Piece draggedPiece = pieces[oldRowSelected][oldColSelected];
				ImageIcon resizedIcon = resizeIcon(draggedPiece.displayPiece, sizeSquares, sizeSquares);
				resizedIcon.paintIcon(this, g2d, draggedPiece.positionX, draggedPiece.positionY);
			}
		}
	}
}