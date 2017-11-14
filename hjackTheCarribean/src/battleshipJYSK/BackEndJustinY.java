package battleshipJYSK;

import oceanExplorer.*;

public class BackEndJustinY implements SunnySupporter {

	//THIS PART OF THE CODE IS TO BE PRIMARILY MANAGED BY JUSTIN YAU 
	//PERIOD 4 & 5 

	/* 
	 PLANNING:
	 
	 11/14/2017
	 METHODs NEEDED:
	 - Method that manages the power-ups - Should not conflict with player choice unless it has to
	 - Method that handles the computer moves
	 - Method that returns whether or not there is a winner
	 - Method that hits a certain coordinate on the spot
	 
	 GENERAL: 
	 Commanders will be fought at the end of 3 levels. Beating a commander will reward a random ship to the player.
	 There will be 3 levels of Commanders that the player will face:
	 Level 1: Commander will randomly attack the player's field (not in the hit places)  
	 Level 2: Commander will remember places that he attacked and not attack areas around the areas hit
	 Level 3: Commander will always hit a ship 
	 ------------------------------------------
	 
	 FOR THE GAMEBOARD:
	 Use 2D arrays to keep track of the ship placements and areas hit. 
	 ------------------------------------------
	 KEY: 
	 X - Areas hit
	 (A-Z) - A ship with special properties
	 ------------------------------------------
	 [X], [ ], [ ], [ ], [ ],
	 [A], [A], [ ], [B], [ ],
	 [ ], [ ], [ ], [B], [ ],
	 
	 ------------------------------------------
	 FOR THE PLAYER TO SELECT THEIR SHIP POSITION:
	 Have the player enter the coordinates of one end of the ship and ask whether they want it to face NORTH, EAST, SOUTH, OR WEST
	 Do not allow the player to place ships on top of each other.
	 
	 Referee: Where would you like to place one end of your ship, (replace with ship name here), with size 2 (replace size here)?
	 User inputs [1,2]
	 Would you like to make it face NORTH (N) , EAST (E) , SOUTH (S), OR WEST (W)?
	 User inputs N
	 
	 Referee: Here's your ship placement. Are you happy with your choice?
	 [ ], [ ], [X], [ ], [ ],
	 [ ], [ ], [X], [ ], [ ],
	 [ ], [ ], [ ], [ ], [ ],
	 
	 SPECIAL CASE:
	 Referee: Where would you like to place one end of your ship, (replace with ship name here), with size (replace size here)?
	 User inputs [0,0]
	 Would you like to make it face SOUTH or EAST?
	 ------------------------------------------
	 TO SELECT AN AREA TO BE HIT:
	 Have the player input the coordinates of the place they would like to hit
	 Do not allow the player to hit the same place again 
	 Display H in every spot of where a ship used to be if the player did manage to sink a ship
	 ------------------------------------------
	 KEY:
	 X - Place to nuke
	 H - Marks where the player already striked
	 S - Areas that the player hit a ship
	 ------------------------------------------
	 
	 Referee: Where would you like to missile?
	 [H], [ ], [H], [S], [ ],
	 [ ], [ ], [ ], [ ], [ ],
	 [ ], [ ], [ ], [ ], [ ],	 
	 
	 ------------------------------------------
	 POWERUPS:
	 Track powerups in another 2D Array (Should be retrieved from another class) and when the user triggers the powerup during the turn, activate effect
	 ------------------------------------------
	 PLANNED POWERUPS:
	 BoinkRadar - Gives the player a general idea of where one of the opponent's ship is. 
	 CriticalMissile - Sets a missile off that will guarantee a hit on a boat in a turn but the user cannot do anything in that time. 
     Stormcaller - The opponent's battleships are surrounded by bad weather and unable to make a player move for one turn. 
	 ------------------------------------------
	 IF TIME ALLOWS IT: 
	 Give commanders a special text art (icon) so that the dialogues are more appealing, cosmetic wise. 
	 ------------------------------------------
	*/
	
	private JustinSupporter frontend;

	private static JustinSunnyPlot[][] thePlayerGameBoard; //This will monitor the game board of the player
	private static JustinSunnyPlot[][] theOpponentGameBoard; //This will monitor the game board of the AI
	private int[] previousMove;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	
	public BackEndJustinY(JustinSupporter frontend) {
		this.frontend = frontend;
		generateMap();
		
		int[] mtemp = {0, 0};
		previousMove = mtemp;
	}
	
	/**
	 * Return the number of ships that the user has
	 * @return
	 */
	public int numberOfShips() {
		return 1; //Should be something like CaveExplorer.inventory.getShips().length;
	}
	
	/**
	 * Return the length of one given ship that the user has 
	 * HELPER METHOD to assist when plotting the ships
	 * @param e - The ship that the user wants to place
	 * @return
	 */
	public int lengthOfShip(Ship e) {
		return ((e.getHp() - (e.getHp() % 10)) / 10);
	}

	//Might be possible to make more helper methods out of this
	/**
	 * Attempts to place the ship at the given coordinate facing the direction
	 * This method checks for outOfBoundsExceptions and makes sure the slot isn't already occupied
	 * @param row - Y coordinate of the Ship
	 * @param col - X coordinate of the Ship
	 * @param direction - Direction the user wants the ship to face
	 * @param shipLength - Length of the Ship to be placed
	 * @param playerBoard - The appropriate player board to place the ship on
	 * @return - Returns whether or not the program was able to place the ship or not
	 */
	public boolean tryShipPlacement(int row, int col, int direction, int shipLength, JustinSunnyPlot[][] playerBoard) {
		JustinSunnyPlot[][] previousBoard = playerBoard;
		if(direction == NORTH) {
			if(row - shipLength >= 0) {
				for(int shipRow = row; shipRow >= row - shipLength; shipRow--) {
					if(!(attemptShipPlacementAtCoordinate(shipRow, col, playerBoard, previousBoard))) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		else if(direction == EAST) {
			if(col + shipLength < playerBoard[0].length) {
				for(int shipCol = col; shipCol < col + shipLength; shipCol++) {
					if(!(attemptShipPlacementAtCoordinate(row, shipCol, playerBoard, previousBoard))) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		else if(direction == SOUTH) {
			if(row + shipLength < playerBoard.length) {
				for(int shipRow = row; shipRow < row + shipLength; shipRow++) {
					if(!(attemptShipPlacementAtCoordinate(shipRow, col, playerBoard, previousBoard))) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		else if(direction == WEST) {
			if(col - shipLength >= 0) {
				for(int shipCol = col; shipCol >= col - shipLength; shipCol--) {
					if(!(attemptShipPlacementAtCoordinate(row, shipCol, playerBoard, previousBoard))) {
						return false;
					}
				}
				return true;
			}
			return false;
		}
		return false;
	}
	
	/**
	 * Method attempts to occupy the coordinate on the board. If it can't, it tells the program to revert the board back to the original state and stop
	 * @param row - Y Coordinate of the Ship
	 * @param col - X Coordinate of the Ship
	 * @param playerBoard - The appropriate player board to place the ship on
	 * @param previousBoard - Original state of the board
	 * @return
	 */
	public boolean attemptShipPlacementAtCoordinate(int row, int col, JustinSunnyPlot[][] playerBoard, JustinSunnyPlot[][] previousBoard) {
		if(playerBoard[row][col].isShipOccupied()) {
			playerBoard = previousBoard;
			return false;
		}
		//If the ship isn't occupied, then make it occupied and report the successful change
		playerBoard[row][col].setShipOccupied(true);
		return true;
	}
	
	/**
	 * Interprets the user input and returns the corresponding direction to which the user will want to place his ship at a certain coordinate
	 * @return
	 */
	public int interpretDirectionInput() {
		String input = CaveExplorer.in.nextLine();
		if(!isValid(input)) {
			printValidEntries();
			input = CaveExplorer.in.nextLine();
		}
		return determineDirection(input, validEntries());
	}
	
	/**
	 * Similar method to the one in Cave Room, this method tells the user the keys and the directions they represent. 
	 */
	public void printValidEntries() {
		CaveExplorer.print("Captain Duran: You are only allowed to type 'n' for NORTH, 'e' for EAST, 's' for SOUTH, and 'w' for WEST!");
	}
	
	/**
	 * String of all the valid inputs for the direction input
	 * @return
	 */
	public String validEntries() {
		return "neswNESW";
	}
	
	public boolean isValid(String input) {
		String validInput = validEntries();
		return validInput.indexOf(input) > - 1 && input.length() == 1;
	}

	public int determineDirection(String input, String key) {
		return key.indexOf(input) % 4;
	}
	
	/**
	 * This generates a new 2D array of the appropriate board size; 
	 */
	public void generateMap() {
		int dimension = 5+(frontend.getCommanderLevel() - 1);
		thePlayerGameBoard = new JustinSunnyPlot[dimension][dimension];
		theOpponentGameBoard = new JustinSunnyPlot[dimension][dimension];
	}
	
	/**
	 * This method will return the coordinates that the user inputs
	 * CREDITS TO: NOCKLES for providing the method through his example
	 * @return
	 */
	public int[] getCoordInput() {
		String input = CaveExplorer.in.nextLine();
		int[] coords = toCoords(input);
		while(coords == null){
			CaveExplorer.print("Captain Duran: You must enter cordinates of the form:\n          <row>,<col>"
					+ "\n<row> and <col> should be integers.");
			input = CaveExplorer.in.nextLine();
			coords = toCoords(input);
		}
		return coords;
	}
	
	/**
	 * This method will convert the user input into an integer array for processing by the program
	 * CREDITS TO: NOCKLES for providing the method through his example
	 * @param input
	 * @return
	 */
	private int[] toCoords(String input) {
		try{
			int a = Integer.parseInt(input.substring(0,1));
			int b = Integer.parseInt(input.substring(2,3));
			if(input.substring(1,2).equals(",") && input.length() ==3){
				int[] coords = {a,b};
				return coords;
			}
			else
			{
				return null;
			}
		}catch(Exception e){
			return null;
		}
	}
	
	public JustinSunnyPlot[][] getPlayerPlots() {
		return thePlayerGameBoard;
	}
	
	public JustinSunnyPlot[][] getCommanderPlots() {
		return theOpponentGameBoard;
	}
	
	/**
	 * Returns the number of not hit ship spots on the board
	 * @param playerBoard - Player board that you want to check
	 * @return
	 */
	public int countOfShipSpotsNotHit(JustinSunnyPlot[][] playerBoard) {
		int count = 0;
		for(int row = 0; row < theOpponentGameBoard.length; row++) {
			for(int col = 0; col < theOpponentGameBoard[0].length; col++) {
				if(theOpponentGameBoard[row][col].isShipOccupied() && !(theOpponentGameBoard[row][col].isHasBeenHit())) {
					count++;
				}
			}
		}
		return count;
	}
	
	/**
	 * TO BE USED FOR THE CRITICAL MISSILE POWER-UP OR LEVEL 3 AI
	 * Returns the coordinates of all the computer's ships that has not been hit yet 
	 * @param playerBoard - Player board that you want to check
	 * @return
	 */
	public int[][] allPossibleShipsNotHit(JustinSunnyPlot[][] playerBoard) {
		int unHitSpots = countOfShipSpotsNotHit(playerBoard);
		int[][] possibleMoves = new int[unHitSpots][2];
		int numOfCoordinates = 0;
		for(int row = 0; row < playerBoard.length; row++) {
			for(int col = 0; col < playerBoard[row].length; col++) {
				if(playerBoard[row][col].isShipOccupied() && !(playerBoard[row][col].isHasBeenHit())) {
					int[] cTemp = {row, col}; 
					possibleMoves[numOfCoordinates] = cTemp;
					numOfCoordinates++;
				}
			}
		}
		return possibleMoves;
	}
	
	/**
	 * Returns whether or not the player has at least one of the appropriate powerup
	 * @param type
	 * @return
	 */
	public static boolean handlePowerUp(int type) {
		return oceanExplorer.Inventory.getBossPowerUps()[type] > 0;
	}
	
	/**
	 * Decrements the count of a particular power up
	 * @param type
	 */
	public static void decrementPowerUp(int type) {
		int[] newPowerUpCount = oceanExplorer.Inventory.getBossPowerUps();
		newPowerUpCount[type]--;
		oceanExplorer.Inventory.setBossPowerUps(newPowerUpCount);
	}
	
}
