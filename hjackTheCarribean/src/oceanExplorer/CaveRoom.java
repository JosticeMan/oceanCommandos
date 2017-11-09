package oceanExplorer;

public class CaveRoom {

	private String description; //Tells the room looks like
	private String directions; //Tells what you can do 
	private String contents; //A symbol representing what's in the room
	private String defaultContents;
	//The rooms are organized by direction, 'null' signifies no room/doors in that direction
	private CaveRoom[] borderingRooms;
	private Door[] doors;
	
	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;
	
	public CaveRoom(String description) {
		this.description = description;
		setDefaultContents(" ");
		contents = defaultContents;
		//Difference between defaultContents and contents is "contents" becomes
		//an x when you are inside this room, when you leave it goes back to default
		//contents
		
		//Note: By default, arrays will populate with 'null', meaning there are no connections
		borderingRooms = new CaveRoom[4];
		doors = new Door[4];
		setDirections();
	}

	/**
	 * For every door in doors[], appends a String to "directions" describing the access
	 * For example: 
	 *  "There is a door to the north" 
	 *  "There is a door to the south" ... etc
	 *  
	 *  If there are no doors at all, directions should say: 
	 * 		"There are no doors, you are trapped in here." 
	 */
	public void setDirections() {
		directions = "";
		boolean doorFound = false;
		for(int i = NORTH; i < WEST + 1; i++) {
			if(doors[i] != null) {
				doorFound = true;
				directions += "\nShipmate: I see the ocean is " + doors[i].getDescription() + " to " + toDirection(i) + ". " + doors[i].getDetails();
			}
		}
		if(!doorFound) {
			directions = "There are no doors in your room. You're trapped";
		}
	}
	
	/**
	 * Converts an int to a direction
	 *    toDirection(0) -> "the North"
	 * @param dir
	 * @return
	 */
	public static String toDirection(int dir) {
		String[] direction = {"the North", "the East", "the South", "the West"};
		return direction[dir];
	}
	
	public void enter() {
		contents = "x";
	}
	
	public void leave() {
		contents = defaultContents;
	}
	
	/**
	 * Gives this room access to anotherRoom (and vice-versa)
	 * and sets a door between them, updating the directions
	 * @param direction
	 * @param anotherRoom
	 * @param door
	 */
	public void setConnection(int direction, CaveRoom anotherRoom, Door door) {
		addRoom(direction, anotherRoom, door);
		anotherRoom.addRoom(oppositeDirection(direction), this, door);
	}
	
	public int oppositeDirection(int direction) {
		//int[] opposites = {2, 3, 0, 1};
		//return opposites[direction];
		return (direction + 2)%4;
	}

	public void addRoom(int direction, CaveRoom cave, Door door) {
		borderingRooms[direction] = cave;
		doors[direction] = door;
		setDirections();
	}
	
	public void interpretInput(String input) {
		while(!isValid(input)) {
			printAllowedEntry();
			input = CaveExplorer.in.nextLine();
		}
		int direction = determineDirection(input, validKeys());
		//Task: convert user input into a direction
		//don't use more than one
		respondToKey(manageCurrentRoomShift(direction));
	}
	
	/**
	 * Override to add more keys
	 * @return
	 */
	public String validKeys() {
		return "wdsa";
	}
	
	/**
	 * Override to change the key types that the user can enter
	 */
	public void printAllowedEntry() {
		System.out.println("You can only enter 'w', 'a', 's', or 'd'");
	}
	
	public void respondToKey(int direction) {
		//First, protect against null pointer exception
		//(user cannot go through a non existent door)
		if(direction < 4) {
			if(borderingRooms[direction] != null && 
					doors[direction] != null) {
				CaveExplorer.currentRoom.leave();
				CaveExplorer.currentRoom = borderingRooms[direction];
				CaveExplorer.currentRoom.enter();
				CaveExplorer.inventory.updateMap();
			}
		} 
		else {
			performAction(direction);
		}
	} 
	
	public int manageCurrentRoomShift(int direction) {
		return direction;
	}
	
	/**
	 * Override to give response to keys other than wasd
	 * @param direction
	 */
	public void performAction(int direction) {
		System.out.println("That key does nothing.");
	}

	/**
	 * This will be where your group sets up all the caves
	 * and all the connections
	 */
	public static void setUpCaves(int level) {
		//ALL OF THIS CODE CAN BE CHANGED
		//1. Decide how big your caves should be
		CaveExplorer.caves = new CaveRoom[5][5];
		//2. Populate with caves and a default description: hint: when starting, use coordinates (helps debugging)
		for(int row = 0; row < CaveExplorer.caves.length; row++) {
			//PLEASE PAY ATTENTION TO THE DIFFERENCE:
			for(int col = 0; col < CaveExplorer.caves[row].length; col++) {
				//create a "default" cave
				CaveExplorer.caves[row][col] = 
						new CaveRoom("Your compass tells you that you are located in coords ("+row+","+col+")");
			}
		}
		//Steven room goes here
		CaveExplorer.caves[1][2]=new StevenRoom("This is Steven's");
		CaveExplorer.caves[0][1].setConnection(EAST,CaveExplorer.caves[0][2],new Door());
		CaveExplorer.caves[0][2].setConnection(SOUTH,CaveExplorer.caves[1][2],new Door());
		//end
		//Dan roome
		
		CaveExplorer.caves[0][3]=new DanRoom("This is fountain. Interact with 'e' so you can restore your health.");
		CaveExplorer.caves[0][2].setConnection(EAST,CaveExplorer.caves[0][3],new Door());
		
		
		
		//end
		//Andrew room
		CaveExplorer.caves[0][4]=new AndrewRoom("You feel dizzy.");
		CaveExplorer.caves[0][4].setConnection(SOUTH,CaveExplorer.caves[1][4],new Door());
		CaveExplorer.caves[0][4].setConnection(WEST,CaveExplorer.caves[0][3],new Door());
		CaveExplorer.caves[0][3].setConnection(WEST,CaveExplorer.caves[0][2],new Door());
		//end
		//Kevin room
		CaveExplorer.caves[0][0] = new KevinRoom("This is Kevin Room");
		CaveExplorer.caves[0][0].setConnection(EAST, CaveExplorer.caves[0][1], new Door());
		CaveExplorer.caves[0][0].setConnection(SOUTH, CaveExplorer.caves[1][0], new Door());
			
		//end
		//Sunny's Room
		CaveRoom sRoom = new SunnyRoom("You have reached the corner edge of the ocean.");
		CaveExplorer.caves[4][4] = sRoom;
		CaveExplorer.caves[4][4].setConnection(WEST, CaveExplorer.caves[4][3], new Door());
		CaveExplorer.caves[4][3].setConnection(NORTH, CaveExplorer.caves[3][3], new Door());
		CaveExplorer.caves[3][3].setConnection(NORTH, CaveExplorer.caves[2][3], new Door());
		//end
		//Justin's Room (This will be the room for boss fights)
		CaveRoom jRoom = new JustinBossRoom("Captain Duran: You've entered the territory of a commander! The commander is coming soon. Prepare to play a game of battleship or run!", level);
		CaveExplorer.caves[2][4] = jRoom;



		CaveExplorer.caves[1][1].setConnection(EAST,CaveExplorer.caves[1][2],new Door());
		CaveExplorer.caves[1][2].setConnection(SOUTH,CaveExplorer.caves[2][2],new Door());
		CaveExplorer.caves[2][2].setConnection(EAST,CaveExplorer.caves[2][3],new Door());
		CaveExplorer.caves[2][3].setConnection(EAST,CaveExplorer.caves[2][4],new Door());

		//end
		//4. Set your starting room:
		CaveExplorer.currentRoom = CaveExplorer.caves[0][1];
		CaveExplorer.currentRoom.enter();
		//5. Set up doors
		CaveRoom[][] c = CaveExplorer.caves;
		c[0][1].setConnection(SOUTH, c[1][1], new Door());
		/**
		 * Special requests:
		 * moving objects in caves
		 * what happens when you lose?
		 * can another object move toward you?
		 */
	}
	
	public int determineDirection(String input, String key) {
		return key.indexOf(input);
	}
	
	public boolean isValid(String input) {
		String validEntries = validKeys();
		return validEntries.indexOf(input) > - 1 && input.length() == 1;
	}

	public String getDescription() {
		return description + "\n" + directions;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getContents() {
		return contents;
	}

	public void setContents(String contents) {
		this.contents = contents;
	}

	public void setDefaultContents(String defaultContents) {
		this.defaultContents = defaultContents;
	}

	public Door getDoor(int direction) {
		if(direction >= 0 && direction < doors.length) {
			return doors[direction];
		}
		else {
			return null;
		}
	}

}
