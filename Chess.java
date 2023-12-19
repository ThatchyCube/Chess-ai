import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

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

	// Variables to keep track of clicks on the board
	private int originalRowSelected = -1, originalColSelected = -1;
	private int newRowSelected = -1, newColSelected = -1;

	// Constructor
	public ChessBoard() {
		// Build a board full of null pieces
		pieces = new Piece[rows][cols];

		// Add the mouse listeners for piece interaction

		addMouseListener(new MouseAdapter() {
			// Override for clicking
			public void mousePressed (MouseEvent e) {
				// Get the position where user clicks
				int mouseX = e.getX();
				int mouseY = e.getY();

				// Check if clicked inside board
				if ((mouseX >= 0 && mouseX <= sizeSquares * 8) &&
					(mouseY >= 0 && mouseY <= sizeSquares * 8)) {
					// Get the icon that was clicked
					originalRowSelected = mouseY / sizeSquares;
					originalColSelected = mouseX / sizeSquares;
				}
			}

			// Overrid for releasing
			public void mouseReleased(MouseEvent e) {
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
				if (pieces[originalRowSelected][originalColSelected] != null 
					&& originalRowSelected != -1 && originalColSelected != -1
					&& newRowSelected != -1 && newColSelected != -1) {
						// Move the piece
						pieces[newRowSelected][newColSelected] = pieces[originalRowSelected][originalColSelected];
						pieces[originalRowSelected][originalColSelected] = null;
					}

				// Reset row and column trackers
				originalRowSelected = -1;
				originalColSelected = -1;
				newRowSelected = -1;
				newColSelected = -1;
		
				// Repaint the board
				repaint();
			}
		});

		// Override for dragging
		addMouseMotionListener(new MouseMotionAdapter() {
				public void mouseDragged(MouseEvent e) {
					int mouseX = 0, mouseY = 0;
					// Check for invalid index
					if (originalRowSelected != -1 && originalColSelected != -1) {
						// Get the mouse position
						mouseX = e.getX();
						mouseY = e.getY();

						// Get the beginning position
						int originalMouseX = pieces[originalRowSelected][originalColSelected].positionX; 
						int originalMouseY = pieces[originalRowSelected][originalColSelected].positionY; 
						
						// Get the change as the user drags
						int changeX = mouseX - originalMouseX - sizeSquares / 2;
						int changeY = mouseY - originalMouseY - sizeSquares  / 2;

						// Update the position of the piece
						pieces[originalRowSelected][originalColSelected].positionX = (originalMouseX + changeX);
						pieces[originalRowSelected][originalColSelected].positionY = (originalMouseY + changeY);

						repaint();
 				}
			}
		});
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
	}
	
    // For resizing pieces 
    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
		// Get the piece and resize it
        Image img = icon.getImage();
        Image resizedImage = img.getScaledInstance(width, height, java.awt.Image.SCALE_SMOOTH);

		// Return the resized image as a new ImageIcon
        return new ImageIcon(resizedImage);
    }

	// For swapping two piece positions
	public void swapPieces(int nrs, int ncs, int ors, int ocs) {
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

				// Set each piece to fit in it's square 
				if (pieces[i][j] != null) {
					pieces[i][j].positionX = squareX;
					pieces[i][j].positionY = squareY;
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
		
		// Draw the pieces 
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (pieces[i][j] != null) {
                    // Resize the piece icon
                    ImageIcon resizedIcon = resizeIcon(pieces[i][j].displayPiece, sizeSquares, sizeSquares);
                    resizedIcon.paintIcon(this, g2d, pieces[i][j].positionX, pieces[i][j].positionY);
					System.out.println(pieces[i][j].positionX);
                }
            }
        }
	}
}