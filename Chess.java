import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

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
		frame.setSize(900, 900);
		
		// Set a minimum size for the frame
		frame.setMinimumSize(new Dimension(600, 600));
		//frame.setResizable(false);

		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		// Make the frame visible
  		frame.setVisible(true);
	}
}

// For storing coordinates of each piece
class Coordinates {
	private int rowCoordinate = 0, colCoordinate = 0;
	
	public void setRowCoordinate(int c) {
		rowCoordinate = c;
	}

	public void setColCoordinate(int c) {
		colCoordinate = c;
	}

	public int getRowCoordinate() {
		return rowCoordinate;
	}

	public int getColCoordinate() {
		return colCoordinate;
	}
}

// For building pieces
abstract class Piece {
	// Information about the piece
	
	// Piece position, color, png (for display)
	private Coordinates position;
	private String color;
	private ImageIcon displayPiece;

	// Checks for valid move for current piece
	public abstract boolean isValid();
}

// Inheritance hierarchy for making different pieces

// For white pieces
class whitePawn extends Piece {

}

class whiteKing extends Piece {

}

class whiteQueen extends Piece {

}

class whiteRook extends Piece {

}

class whiteKnight extends Piece {

}

class whiteBishop extends Piece {

}

// For black pieces
class blackPawn extends Piece {

}

class blackKing extends Piece {

}

class blackQueen extends Piece {

}

class blackRook extends Piece {

}

class blackKnight extends Piece {

}

class blackBishop extends Piece {

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
	}
}

