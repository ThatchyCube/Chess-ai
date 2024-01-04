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

	// For drawing available piece paths
	private List<Point> currentLegalMoves = new ArrayList<>();

	// For en passant
	private boolean enPassant = false;
	private boolean enPassantReset = false;

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

		// For pawn pieces
		protected boolean enPassantLeft = false;
		protected boolean enPassantRight = false;
		int enPassantTracker = 0;


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
			pieceType = "whitePawn";
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check for diagonal capture
			if (newRowSelected == oldRowSelected - 1
				&& (newColSelected == oldColSelected + 1
				|| newColSelected == oldColSelected - 1)) {
					if (pieces[newRowSelected][newColSelected] != null 
						&& pieces[newRowSelected][newColSelected].color.equals("black")) {
						// Black piece can be diagnally captured

						// Check for queening
						int currentX = pieces[oldRowSelected][oldColSelected].positionX;
						int currentY = pieces[oldRowSelected][oldColSelected].positionY;

						if (pieces[oldRowSelected][oldColSelected] != null 
							&& newRowSelected == 0) {
							// Pawn can queen
							pieces[oldRowSelected][oldColSelected] = new whiteQueen();
							pieces[oldRowSelected][oldColSelected].positionX = currentX;
							pieces[oldRowSelected][oldColSelected].positionY = currentY;
						}

						movedAlready = true;
						return true;
				}
			}

			// Check if a piece is blocking the pawn
			if (pieces[newRowSelected][newColSelected] != null)
				return false;

			// Check if can take en passant
			if (enPassantLeft && newRowSelected == oldRowSelected - 1 
				&& newColSelected == oldColSelected - 1) {

				// Check for queening
				if (newRowSelected == 0) {
					int currentX = pieces[oldRowSelected][oldColSelected].positionX;
					int currentY = pieces[oldRowSelected][oldColSelected].positionY;

					pieces[oldRowSelected][oldColSelected] = new whiteQueen();
					pieces[oldRowSelected][oldColSelected].positionX = currentX;
					pieces[oldRowSelected][oldColSelected].positionY = currentY;
				}

				// Do the move
				pieces[newRowSelected][newColSelected] = pieces[oldRowSelected][oldColSelected];
				pieces[oldRowSelected][oldColSelected] = null;
				pieces[oldRowSelected][newColSelected] = null;

				// Set the flag
				enPassant = true;
				enPassantLeft = false;
				movedAlready = true;
				return true;
			} else if (enPassantRight && newRowSelected == oldRowSelected - 1
				&& newColSelected == oldColSelected + 1) {

				// Check for queening
				if (newRowSelected == 0) {
					int currentX = pieces[oldRowSelected][oldColSelected].positionX;
					int currentY = pieces[oldRowSelected][oldColSelected].positionY;

					pieces[oldRowSelected][oldColSelected] = new whiteQueen();
					pieces[oldRowSelected][oldColSelected].positionX = currentX;
					pieces[oldRowSelected][oldColSelected].positionY = currentY;
				}

				// Do the move
				pieces[newRowSelected][newColSelected] = pieces[oldRowSelected][oldColSelected];
				pieces[oldRowSelected][oldColSelected] = null;
				pieces[oldColSelected][newColSelected] = null;

				// Set the flag
				enPassant = true;
				enPassantRight = false;
				movedAlready = true;
				return true;
			}

			// Check if the pawn has been moved
			if (!movedAlready) {
				// Pawn has option to move two squares
				if (newColSelected == oldColSelected
					&& newRowSelected == oldRowSelected - 2) {
				
					// Check if moved next to a pawn for en passant
					if (pieces[newRowSelected][newColSelected - 1] != null
						&& pieces[newRowSelected][newColSelected - 1].pieceType.equals("blackPawn")) {
						pieces[newRowSelected][newColSelected - 1].enPassantRight = true;
					} else if (pieces[newRowSelected][newColSelected + 1] != null
						&& pieces[newRowSelected][newColSelected + 1].pieceType.equals("blackPawn")) {
						pieces[newRowSelected][newColSelected + 1].enPassantLeft = true;
					}

					// Correct move made
					movedAlready = true;
					return true;
				} else if (newColSelected == oldColSelected
					&& newRowSelected == oldRowSelected - 1) {
					// Pawn can move one square

					// Check for queening
					int currentX = pieces[oldRowSelected][oldColSelected].positionX;
					int currentY = pieces[oldRowSelected][oldColSelected].positionY;

					if (pieces[oldRowSelected][oldColSelected] != null 
						&& newRowSelected == 0) {
						// Pawn can queen
						pieces[oldRowSelected][oldColSelected] = new whiteQueen();
						pieces[oldRowSelected][oldColSelected].positionX = currentX;
						pieces[oldRowSelected][oldColSelected].positionY = currentY;
					}

					movedAlready = true;
					return true;
				} else 
					return false;
			} else if (newColSelected == oldColSelected
				&& newRowSelected == oldRowSelected - 1) {
				// Pawn can move one square

				// Check for queening
				int currentX = pieces[oldRowSelected][oldColSelected].positionX;
				int currentY = pieces[oldRowSelected][oldColSelected].positionY;

				if (pieces[oldRowSelected][oldColSelected] != null 
					&& newRowSelected == 0) {
					// Pawn can queen
					pieces[oldRowSelected][oldColSelected] = new whiteQueen();
					pieces[oldRowSelected][oldColSelected].positionX = currentX;
					pieces[oldRowSelected][oldColSelected].positionY = currentY;
				}

				movedAlready = true;
				return true;
			} else
				return false;
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
			pieceType = "whiteKing";
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check for ability to castle
			if (newRowSelected == oldRowSelected 
				&& (newColSelected == oldColSelected + 2 || newColSelected == oldColSelected + 3)) {

				// Check for proper castling conditions to the right
				if ((pieces[newRowSelected][oldColSelected + 1] == null && pieces[newRowSelected][oldColSelected + 2] == null)
					&& pieces[newRowSelected][oldColSelected + 3].pieceType.equals("whiteRook")) {
					// Check the king hasn't moved before
					if (movedAlready == false) {
						// Check if the king is trying to move through a check
						for (int i = 1; i <= 2; i++) {
							if (isAttacked(oldRowSelected, oldColSelected + i, "white"))
								return false;
						}

						// Else
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
					&& pieces[newRowSelected][oldColSelected - 3] == null) && pieces[newRowSelected][oldColSelected - 4].pieceType.equals("whiteRook")) {
					// Check the king hasn't moved before
					if (movedAlready == false) {
						// Check if the king is trying to move through a check
						for (int i = 1; i <= 3; i++) {
							if (isAttacked(oldRowSelected, oldColSelected - i, "white"))
								return false;
						}

						// Else
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
				&& pieces[newRowSelected][newColSelected].color.equals("black")) {
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
			pieceType = "whiteQueen";
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check if the square is a black piece
			if (pieces[newRowSelected][newColSelected] != null
				&& pieces[newRowSelected][newColSelected].color.equals("black")) {
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
						rowPos += loopRow;
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
			pieceType = "whiteKnight";
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
			pieceType = "whiteBishop";
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
			pieceType = "blackPawn";
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check for diagonal capture
			if (newRowSelected == oldRowSelected + 1
				&& (newColSelected == oldColSelected + 1
				|| newColSelected == oldColSelected - 1)) {
					if (pieces[newRowSelected][newColSelected] != null 
						&& pieces[newRowSelected][newColSelected].color.equals("white")) {
						// Black piece can be diagnally captured

						// Check for queening
						int currentX = pieces[oldRowSelected][oldColSelected].positionX;
						int currentY = pieces[oldRowSelected][oldColSelected].positionY;

						if (pieces[oldRowSelected][oldColSelected] != null 
							&& newRowSelected == 7) {
							// Pawn can queen
							pieces[oldRowSelected][oldColSelected] = new blackQueen();
							pieces[oldRowSelected][oldColSelected].positionX = currentX;
							pieces[oldRowSelected][oldColSelected].positionY = currentY;
						}

						
						movedAlready = true;
						return true;
				}
			}
			
			// Check if a piece is blocking the pawn
			if (pieces[newRowSelected][newColSelected] != null)
				return false;

			// Check if can take en passant
			if (enPassantLeft && newRowSelected == oldRowSelected + 1 
				&& newColSelected == oldColSelected - 1) {

				// Check for queening
				if (newRowSelected == 7) {
					int currentX = pieces[oldRowSelected][oldColSelected].positionX;
					int currentY = pieces[oldRowSelected][oldColSelected].positionY;

					pieces[oldRowSelected][oldColSelected] = new blackQueen();
					pieces[oldRowSelected][oldColSelected].positionX = currentX;
					pieces[oldRowSelected][oldColSelected].positionY = currentY;
				}

				// Do the move
				pieces[newRowSelected][newColSelected] = pieces[oldRowSelected][oldColSelected];
				pieces[oldRowSelected][oldColSelected] = null;
				pieces[newRowSelected - 1][newColSelected] = null;

				// Set the flag
				enPassant = true;
				enPassantLeft = false;
				movedAlready = true;
				return true;
			} else if (enPassantRight && newRowSelected == oldRowSelected + 1
				&& newColSelected == oldColSelected + 1) {

				// Check for queening
				if (newRowSelected == 7) {
					int currentX = pieces[oldRowSelected][oldColSelected].positionX;
					int currentY = pieces[oldRowSelected][oldColSelected].positionY;

					pieces[oldRowSelected][oldColSelected] = new blackQueen();
					pieces[oldRowSelected][oldColSelected].positionX = currentX;
					pieces[oldRowSelected][oldColSelected].positionY = currentY;
				}

				// Do the move
				pieces[newRowSelected][newColSelected] = pieces[oldRowSelected][oldColSelected];
				pieces[oldRowSelected][oldColSelected] = null;
				pieces[newRowSelected - 1][newColSelected] = null;

				// Set the flag
				enPassant = true;
				enPassantRight = false;
				movedAlready = true;
				return true;
			}

			// Check if the pawn has been moved
			if (!movedAlready) {
				// Pawn has option to move two squares
				if (newColSelected == oldColSelected
					&& newRowSelected == oldRowSelected + 2) {

					// Check if moved next to a pawn for en passant
					if (pieces[newRowSelected][newColSelected - 1] != null
						&& pieces[newRowSelected][newColSelected - 1].pieceType.equals("whitePawn")) {
						pieces[newRowSelected][newColSelected - 1].enPassantRight = true;
					} else if (pieces[newRowSelected][newColSelected + 1] != null
						&& pieces[newRowSelected][newColSelected + 1].pieceType.equals("whitePawn")) {
						pieces[newRowSelected][newColSelected + 1].enPassantLeft = true;
					}
						 
					// Correct move made
					movedAlready = true;
					return true;
				} else if (newColSelected == oldColSelected
					&& newRowSelected == oldRowSelected + 1) {
					// Pawn can move one square

					// Check for queening
					int currentX = pieces[oldRowSelected][oldColSelected].positionX;
					int currentY = pieces[oldRowSelected][oldColSelected].positionY;

					if (pieces[oldRowSelected][oldColSelected] != null 
						&& newRowSelected == 7) {
						// Pawn can queen
						pieces[oldRowSelected][oldColSelected] = new blackQueen();
						pieces[oldRowSelected][oldColSelected].positionX = currentX;
						pieces[oldRowSelected][oldColSelected].positionY = currentY;
					}

					movedAlready = true;
					return true;
				} else 
					return false;
			} else if (newColSelected == oldColSelected
				&& newRowSelected == oldRowSelected + 1) {
				// Pawn can move one square

				// Check for queening
				int currentX = pieces[oldRowSelected][oldColSelected].positionX;
				int currentY = pieces[oldRowSelected][oldColSelected].positionY;

				if (pieces[oldRowSelected][oldColSelected] != null 
					&& newRowSelected == 7) {
					// Pawn can queen
					pieces[oldRowSelected][oldColSelected] = new blackQueen();
					pieces[oldRowSelected][oldColSelected].positionX = currentX;
					pieces[oldRowSelected][oldColSelected].positionY = currentY;
				}

				movedAlready = true;
				return true;
			} else 
				return false;
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
			pieceType = "blackKing";
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check for ability to castle
			if (newRowSelected == oldRowSelected 
				&& (newColSelected == oldColSelected + 2 || newColSelected == oldColSelected + 3)) {

				// Check for proper castling conditions to the right
				if ((pieces[newRowSelected][oldColSelected + 1] == null && pieces[newRowSelected][oldColSelected + 2] == null)
					&& pieces[newRowSelected][oldColSelected + 3].pieceType.equals("blackRook")) {
					// Check the king hasn't moved before
					if (movedAlready == false) {
						// Check if the king is trying to move through a check
						for (int i = 1; i <= 2; i++) {
							if (isAttacked(oldRowSelected, oldColSelected + i, "black"))
								return false;
						}

						// Else
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
					&& pieces[newRowSelected][oldColSelected - 3] == null) && pieces[newRowSelected][oldColSelected - 4].pieceType.equals("blackRook")) {
					// Check the king hasn't moved before
					if (movedAlready == false) {
						// Check if the king is trying to move through a check
						for (int i = 1; i <= 3; i++) {
							if (isAttacked(oldRowSelected, oldColSelected - i, "black"))
								return false;
						}

						// Else
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
				&& pieces[newRowSelected][newColSelected].color.equals("white")) {
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
			pieceType = "blackQueen";
		}

		// Override for valid moves
		protected boolean isValid() {
			// Check if the square is a black piece
			if (pieces[newRowSelected][newColSelected] != null
				&& pieces[newRowSelected][newColSelected].color.equals("white")) {
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
						rowPos += loopRow;
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
			pieceType = "blackKnight";
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
			pieceType = "blackBishop";
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

							if (pieces[oldRowSelected][oldColSelected].isValid()) {
								// Check if move is valid

								// Check for en passant
								if (enPassant) {
									enPassant = false;

									// Do the turn
									isWhiteTurn = !isWhiteTurn;
								} else if (pieces[oldRowSelected][oldColSelected].canCastle) {
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
									// For storing the piece in the square we move to 
									Piece tempPiece = pieces[newRowSelected][newColSelected];

									// Else move the piece
									pieces[newRowSelected][newColSelected] = pieces[oldRowSelected][oldColSelected];
									pieces[oldRowSelected][oldColSelected] = null;
									
									// Check if the king is in check
									if (isInCheck()) {
										// Invalid move made, king is still in check
										pieces[oldRowSelected][oldColSelected] = pieces[newRowSelected][newColSelected];
    									pieces[newRowSelected][newColSelected] = tempPiece;
									} else {
										// Do the turn
										isWhiteTurn = !isWhiteTurn;
									}
								}
							} else {
								reset();
								return;
							}
						} else {
							reset();
							return;
						}
					}

					// Reset the en passant trackers
					if (enPassantReset) {
						enPassant = false;
						enPassantReset = false;
					}

					// Check if en passant was not done
					if (enPassantReset == false && enPassant == true) {
						enPassantReset = true;
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
			pieces[oldRowSelected][oldColSelected + 2] = pieces[oldRowSelected][oldColSelected]; // Move the king
			pieces[oldRowSelected][oldColSelected + 1] = pieces[oldRowSelected][oldColSelected + 3]; // Move the rook

			// Leave the original squares empty
			pieces[oldRowSelected][oldColSelected] = null;
			pieces[oldRowSelected][oldColSelected + 3] = null;

			// Do the turn
			isWhiteTurn = !isWhiteTurn;
	}

	// For castling kings to the left
	private void castleLeft() {
		pieces[oldRowSelected][oldColSelected - 2] = pieces[oldRowSelected][oldColSelected]; // Move the king
		pieces[oldRowSelected][oldColSelected - 1] = pieces[oldRowSelected][oldColSelected - 4]; // Move the rook

		// Leave the original squares empty
		pieces[oldRowSelected][oldColSelected] = null;
		pieces[oldRowSelected][oldColSelected - 4] = null;

		// Do the turn
		isWhiteTurn = !isWhiteTurn;
	}

	// For resetting a pieces position
	private void reset() {
		// Put the piece back in it's original square and center it
		pieces[oldRowSelected][oldColSelected].positionX = oldColSelected * sizeSquares;
		pieces[oldRowSelected][oldColSelected].positionY = oldRowSelected * sizeSquares;
		repaint();
	}

	private boolean isInCheck() {
		// For checking attacking pieces
		String currentColor;

		// Do the check
		if (isWhiteTurn) {
			currentColor = "white";

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					if (pieces[i][j] != null && pieces[i][j].pieceType.equals("whiteKing")) {
						// Check lateral movements
						if (checkLateralMoves(i, j, currentColor))
							return true;
						// Check diagonal movements
						if (checkDiagonalMoves(i, j, currentColor))
							return true;
						// Check knight movements
						if (checkKnightMoves(i, j, currentColor))
							return true;
						// Check pawn movements
						if (checkPawnMoves(i, j, currentColor))
							return true;
					}
				}
			}
		} else {
			currentColor = "black";

			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < cols; j++) {
					if (pieces[i][j] != null && pieces[i][j].pieceType.equals("blackKing")) {
						// Check lateral movements
						if (checkLateralMoves(i, j, currentColor))
							return true;
						// Check diagonal movements
						if (checkDiagonalMoves(i, j, currentColor))
							return true;
						// Check knight movements
						if (checkKnightMoves(i, j, currentColor))
							return true;
						// Check pawn movements
						if (checkPawnMoves(i, j, currentColor))
							return true;
					}
				}
			}
		}
		return false;
	}

	// Checks lateral moves for the king
	private boolean checkLateralMoves(int i, int j, String currentColor) {
		String currentRook, currentQueen;

		// Get the piece opposition to check
		if (currentColor.equals("white")) {
			currentRook = "blackRook";
			currentQueen = "blackQueen";
		} else {
			currentRook = "whiteRook";
			currentQueen = "whiteQueen";
		}

		// Upwards
		for (int k = i - 1; k >= 0; k--) {
			if (pieces[k][j] != null) {
				if (pieces[k][j].pieceType.equals(currentRook) || pieces[k][j].pieceType.equals(currentQueen)) {
					return true;
				} else {
					break;
				}
			}
		}
		// Downwards
		for (int k = i + 1; k < rows; k++) {
			if (pieces[k][j] != null) {
				if (pieces[k][j].pieceType.equals(currentRook) || pieces[k][j].pieceType.equals(currentQueen)) {
					return true;
				} else {
					break;
				}
			}
		}
		// Left
		for (int k = j - 1; k >= 0; k--) {
			if (pieces[i][k] != null) {
				if (pieces[i][k].pieceType.equals(currentRook) || pieces[i][k].pieceType.equals(currentQueen)) {
					return true;
				} else {
					break;
				}
			}
		}
		// Right
		for (int k = j + 1; k < cols; k++) {
			if (pieces[i][k] != null) {
				if (pieces[i][k].pieceType.equals(currentRook) || pieces[i][k].pieceType.equals(currentQueen)) {
					return true;
				} else {
					break;
				}
			}
		}
		
		// Otherwise
		return false;
	}

	// Checks diagnonal moves for the king
	private boolean checkDiagonalMoves(int i, int j, String currentColor) {
		// Get the current color to check for
		String currentBishop, currentQueen;

		if (currentColor.equals("white")) {
			currentBishop = "blackBishop";
			currentQueen = "blackQueen";
		} else {
			currentBishop = "whiteBishop";
			currentQueen = "whiteQueen";
		}

		// Upper left diagonal
		for (int k = i - 1, l = j - 1; k >= 0 && l >= 0; k--, l--) {
			if (pieces[k][l] != null) {
				if (pieces[k][l].pieceType.equals(currentBishop) || pieces[k][l].pieceType.equals(currentQueen)) {
					return true;
				} else {
					break;
				}
			}
		}
		// Upper right diagonal
		for (int k = i - 1, l = j + 1; k >= 0 && l < cols; k--, l++) {
			if (pieces[k][l] != null) {
				if (pieces[k][l].pieceType.equals(currentBishop) || pieces[k][l].pieceType.equals(currentQueen)) {
					return true;
				} else {
					break;
				}
			}
		}
		// Lower left diagonal
		for (int k = i + 1, l = j - 1; k < rows && l >= 0; k++, l--) {
			if (pieces[k][l] != null) {
				if (pieces[k][l].pieceType.equals(currentBishop) || pieces[k][l].pieceType.equals(currentQueen)) {
					return true;
				} else {
					break;
				}
			}
		}
		// Lower right diagonal
		for (int k = i + 1, l = j + 1; k < rows && l < cols; k++, l++) {
			if (pieces[k][l] != null) {
				if (pieces[k][l].pieceType.equals(currentBishop) || pieces[k][l].pieceType.equals(currentQueen)) {
					return true;
				} else {
					break;
				}
			}
		}

		// Otherise
		return false;
	}

	// For checking knight moves
	private boolean checkKnightMoves(int i, int j, String currentColor) {
		// Array to track possible knight move offsets
		int[][] knightMoves = {{-2, -1}, {-2, 1}, {-1, -2}, {-1, 2}, {1, -2}, {1, 2}, {2, -1}, {2, 1}};

		// Get the opposing knight color
		String opposingKnight;
		if (currentColor.equals("white")) 
			opposingKnight = "blackKnight";
		else
			opposingKnight = "whiteKnight";

		// Do the check
		for (int m = 0; m < knightMoves.length; m++) {
			int newRow = i + knightMoves[m][0];
			int newCol = j + knightMoves[m][1];

			if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < cols &&
				pieces[newRow][newCol] != null && pieces[newRow][newCol].pieceType.equals(opposingKnight)) {
				// Threatening opposing knight found
				return true; 
			}
		}

		// Otherwise
    	return false;
	}

	// Check for opposing pawn checks
	private boolean checkPawnMoves(int i, int j, String currentColor) {
		// Get the opposing pawn color
		String opposingPawn;
		if (currentColor.equals("white"))
			opposingPawn = "blackPawn";
		else	
			opposingPawn = "whitePawn";

		// Check for threatening pawns based on their capturing direction
		int pawnDirection = (currentColor.equals("white")) ? -1 : 1;

		// Diagonal left capture
		int leftCaptureRow = i + pawnDirection;
		int leftCaptureCol = j - 1;

		if (leftCaptureRow >= 0 && leftCaptureRow < rows && leftCaptureCol >= 0 && leftCaptureCol < cols &&
			pieces[leftCaptureRow][leftCaptureCol] != null &&
			pieces[leftCaptureRow][leftCaptureCol].pieceType.equals(opposingPawn)) {
			// Threatening opposing pawn found
			return true; 
		}

		// Diagonal right capture
		int rightCaptureRow = i + pawnDirection;
		int rightCaptureCol = j + 1;

		if (rightCaptureRow >= 0 && rightCaptureRow < rows && rightCaptureCol >= 0 && rightCaptureCol < cols &&
			pieces[rightCaptureRow][rightCaptureCol] != null &&
			pieces[rightCaptureRow][rightCaptureCol].pieceType.equals(opposingPawn)) {
			// Threatening opposing pawn found
			return true; 
		}

		return false;
	}

	// Checks whether a square is being attacked
	private boolean isAttacked(int rowAttacked, int colAttacked, String currentColor) {
		// Check lateral moves from the current square
		boolean isLateralAttack = checkLateralMoves(rowAttacked, colAttacked, currentColor);

		// Check diagonal moves from the current square
		boolean isDiagonalAttack = checkDiagonalMoves(rowAttacked, colAttacked, currentColor);

		// Check knight moves from the current square
		boolean isKnightAttack = checkKnightMoves(rowAttacked, colAttacked, currentColor);

		// Check pawn moves from the current square
		boolean isPawnAttack = checkPawnMoves(rowAttacked, colAttacked, currentColor);

		// Return true if any attack is detected
		return isLateralAttack || isDiagonalAttack || isKnightAttack || isPawnAttack;
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