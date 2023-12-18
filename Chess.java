import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Chess {
	private static boolean gameStarted = false;

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

// Inheritance hierarchy for making different pieces

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
	public boolean isValid() {
		return true;
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
	public boolean isValid() {
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
	public boolean isValid() {
		return true;
	}
}

class whiteBishop extends Piece {
	// Constructor
	public whiteBishop() {
		positionX = 0;
		positionY = 0;
		color = "white";
		displayPiece = new ImageIcon(getClass().getResource("img/wB.png"));
	}

	// Override for valid moves
	public boolean isValid() {
		return true;
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
	public boolean isValid() {
		return true;
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
	public boolean isValid() {
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
	public boolean isValid() {
		return true;
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
	public boolean isValid() {
		return true;
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

	// Constructor
	public ChessBoard() {
		// Build a board full of null pieces
		pieces = new Piece[rows][cols];
	}
	// Loads the pieces into the board array
	public void loadPieces() {
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

		// For setting up correct piece positions
		int setX = 0, setY = 0;

		// Set the coordinates for each piece
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < cols; j++) {
				if (pieces[i][j] != null) {
					// Set the piece positions
					pieces[i][j].positionX = setX;
					pieces[i][j].positionY = setY;

					// Move to the next square
					setX += sizeSquares;
				}
			}
			// Move down a row
				setY += sizeSquares;
				setX = 0;
		}	
	}

	// Override for drawing
	public void paintComponent(Graphics g) {
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
		sizeSquares = ((Math.min(panelWidth, panelHeight) * 4) / 5) / 8;

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

		// Draw the pieces 
		for (int i = 0; i < rows; i++) {
      			for (int j = 0; j < cols; j++) {
				// Draw the piece
		   		if (pieces[i][j] != null) {
							ImageIcon currentPiece = pieces[i][j].displayPiece;
                			currentPiece.paintIcon(this, g2d, pieces[i][j].positionX, pieces[i][j].positionY);
       			}
    		}
		}
	}
}