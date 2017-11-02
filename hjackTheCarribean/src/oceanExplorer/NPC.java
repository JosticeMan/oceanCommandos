package oceanExplorer;

public class NPC {

	//Fields relating to navigation 
	private OceanTerritory[][] floor; //Where the NPC roams
	private int currentRow;
	private int currentCol;
	private NPCRoom currentRoom;
	
	//Fields relating to character
	private boolean active;
	private String activeDescription;
	private String inactiveDescription;
	
	//Default constructor 
	/**
	 * Note: You can make custom constructors later on
	 * For example:
	 * public NPC(String description, String inactiveDescription) {
	 * 
	 * }
	 */
	public NPC() {
		this.floor = OceanExplorerMain.territories;
		this.activeDescription = "There is a person waiting to talk to you.";
		this.inactiveDescription = "The person you spoke to earlier is standing here";
		//To indicate the NPC doesn't have a position yet, use coordinates (-1, -1)
		this.currentRow = -1;
		this.currentCol = -1;
		this.currentRoom = null;
		this.active = true;
	}
	
	public void interact() {
		OceanExplorerMain.print("Hi! I'm an NPC. I say nothing at all till you say 'bye'.");
		String response = OceanExplorerMain.in.nextLine();
		while(!response.equalsIgnoreCase("bye")) {
			OceanExplorerMain.print("...");
			response = OceanExplorerMain.in.nextLine();
		}
		OceanExplorerMain.print("Well, that was fun. Later!");
		active = false;
	}

	public boolean isActive() {
		return active;
	}

	//Type Casting - Using something like (int) to change the type of a certain variable to fit the one that you need
	public void setPosition(int row, int col) {
		if(row >= 0 && row < floor.length && col >= 0 && col < floor[row].length
				&& floor[row][col] instanceof NPCRoom) {
			//Remove the npc from current room
			if(currentRoom != null) {
				currentRoom.leaveNPC();
			}
			currentRow = row;
			currentCol = col;
			currentRoom = (NPCRoom) floor[row][col];
			currentRoom.enterNPC(this);
		}
	}
	
	public String getInactiveDescription() {
		return inactiveDescription;
	}

	public String getActiveDescription() {
		return activeDescription;
	}

	public void act() {
		if(active) {
			int[] move = calculateMovement();
			int newRow = currentRow + move[0];
			int newCol = currentCol + move[1];
			setPosition(newRow, newCol);
		}
	}

	public int[] calculateMovement() {
		int[] moves = new int[2];
		int[][] possibleMoves = {{-1, 0}, {0,1}, {1, 0}, {0, -1}, {0, 0}};
		int rand = (int) (Math.random() * possibleMoves.length);
		moves[0] = possibleMoves[rand][0] + currentRow;
		moves[1] = possibleMoves[rand][1] + currentCol;
		while(currentRoom.getRiptide(rand) == null || !(OceanExplorerMain.territories[moves[0]][moves[1]] instanceof NPCRoom)) {
			rand = (int) (Math.random() * possibleMoves.length);
			moves[0] = possibleMoves[rand][0] + currentRow;
			moves[1] = possibleMoves[rand][1] + currentCol;
		}
		return moves;
	}

}