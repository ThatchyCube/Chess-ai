import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;

public class Chess {
	// Execute application
	public static void main(String args[]) {
		// Build the frame
  		JFrame frame = new JFrame("Chess");
  		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);	

  		// Build the board
  		ChessBoard board = new ChessBoard();

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

// For building pieces
abstract class Piece {
	// Information about the piece
	
	// Piece position, color, png (for display)
	protected int positionX, positionY;
	protected String color;
	protected ImageIcon displayPiece;

	// Checks for valid move for current piece
	public abstract boolean isValid();
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

	// Nested class inheritance hierarchy for making different pieces
	// For white pieces
	class whitePawn extends Piece {
		// For tracking if the current pawn has been moved
		private boolean movedAlready;

		// Constructor
		public whitePawn() {
			positionX = 0;
			positionY = 0;
			color = "white";
			movedAlready = false;
			displayPiece = new ImageIcon(getClass().getResource("img/wP.png"));
		}

		// Override for valid moves
		public boolean isValid() {
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
		public boolean isValid() {
			return true;
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
		public boolean isValid() {
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
	    }

	    // Override for valid moves
	    @Override
	    public boolean isValid() {
	        // Rook moves horizontally or vertically
	        if (newRowSelected == oldRowSelected || newColSelected == oldColSelected) {
	            // Check if the path is clear
	            if (isPathClear(oldRowSelected, oldColSelected, newRowSelected, newColSelected)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    // Method to check if the path is clear
		private boolean isPathClear(int oldRow, int oldCol, int newRow, int newCol) {
			int rowStep = Integer.compare(newRow, oldRow);
			int colStep = Integer.compare(newCol, oldCol);

			int currentRow = oldRow + rowStep;
			int currentCol = oldCol + colStep;

			// Check each square along the path for any piece
			while (currentRow != newRow || currentCol != newCol) {
				// If the current square has a piece
				if (pieces[currentRow][currentCol] != null) {
					// If it's the last square (destination) check if it's a different color
					if (currentRow == newRow - rowStep && currentCol == newCol - colStep) {
						return !pieces[currentRow][currentCol].color.equals("white");
					}
					// If it's not the destination square, the path is blocked
					return false;
				}
				currentRow += rowStep;
				currentCol += colStep;
			}

	        if (pieces[newRow][newCol] != null) {
	            // Check if the destination square is not a piece of the same color
	            if (pieces[newRow][newCol].color.equals("white")) {
	                return false;
	            }
	        }
	        // Path is clear
	        return true;
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
	    public boolean isValid() {
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
		public boolean isValid() {
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
	}



	// For black pieces

	class blackPawn extends Piece {
		// For tracking if the current pawn has been moved
		private boolean movedAlready;

		// Constructor
		public blackPawn() {
			positionX = 0;
			positionY = 0;
			color = "black";
			movedAlready = false;
			displayPiece = new ImageIcon(getClass().getResource("img/bP.png"));
		}

		// Override for valid moves
		public boolean isValid() {
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
		public boolean isValid() {
			return true;
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
		public boolean isValid() {
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
	    }

	    // Override for valid moves
	    @Override
	    public boolean isValid() {
	        // Rook moves horizontally or vertically
	        if (newRowSelected == oldRowSelected || newColSelected == oldColSelected) {
	            // Check if the path is clear
	            if (isPathClear(oldRowSelected, oldColSelected, newRowSelected, newColSelected)) {
	                return true;
	            }
	        }
	        return false;
	    }

	    // Method to check if the path is clear
		private boolean isPathClear(int oldRow, int oldCol, int newRow, int newCol) {
			int rowStep = Integer.compare(newRow, oldRow);
			int colStep = Integer.compare(newCol, oldCol);

			int currentRow = oldRow + rowStep;
			int currentCol = oldCol + colStep;

			// Check each square along the path for any piece
			while (currentRow != newRow || currentCol != newCol) {
				// If the current square has a piece
				if (pieces[currentRow][currentCol] != null) {
					// If it's the last square (destination) check if it's a different color
					if (currentRow == newRow - rowStep && currentCol == newCol - colStep) {
						return !pieces[currentRow][currentCol].color.equals("black");
					}
					// If it's not the destination square, the path is blocked
					return false;
				}
				currentRow += rowStep;
				currentCol += colStep;
			}

	        if (pieces[newRow][newCol] != null) {
	            // Check if the destination square is not a piece of the same color
	            if (pieces[newRow][newCol].color.equals("black")) {
	                return false;
	            }
	        }
	        // Path is clear
	        return true;
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
	    public boolean isValid() {
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
		public boolean isValid() {
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
								// Else move the piece
								pieces[newRowSelected][newColSelected] = pieces[oldRowSelected][oldColSelected];
								pieces[oldRowSelected][oldColSelected] = null;

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
		turnIndicator.setForeground(Color.BLACK);
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

	// For swapping two piece positions
	private void swapPieces(int nrs, int ncs, int ors, int ocs) {
		// Save the original piece information
		int tempPositionX = pieces[nrs][ncs].positionX;
		int tempPositionY = pieces[nrs][ncs].positionY;
		String tempColor = pieces[nrs][ncs].color;
		ImageIcon tempDisplay = pieces[nrs][ncs].displayPiece;

		// Swap the original piece with the new position
		pieces[nrs][ncs].positionX = pieces[ors][ocs].positionX;
		pieces[nrs][ncs].positionY = pieces[ors][ocs].positionY;
		pieces[nrs][ncs].color = pieces[ors][ocs].color;
		pieces[nrs][ncs].displayPiece = pieces[ors][ocs].displayPiece;

		// Put the new location's piece in the old spot
		pieces[nrs][ncs].positionX = tempPositionX;
		pieces[nrs][ncs].positionY = tempPositionY;
		pieces[nrs][ncs].color = tempColor;
		pieces[nrs][ncs].displayPiece = tempDisplay;
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