package oceanExplorer;

public class JustinBossRoom extends CaveRoom {

	private JustinSunnyCommander currentCommander;
	
	public static final String[] FIRST_START = {"J", "M", "Chr", "T", "Th", "D", "Fr"};
	public static final String[] FIRST_MIDDLE = {"usti", "ist", "oma", "o", "e", "or", "aphin"};
	public static final String[] FIRST_END = {"n", "y", "er", "old", "tian", "s", "d"};
	
	public JustinBossRoom(String description, int level) {
		super(description); //This manages the description
		currentCommander = new JustinSunnyCommander(randomFirstName(), level);
	}
	
	public static String randomFirstName() {
		return CaveExplorer.randomString(FIRST_START) + CaveExplorer.randomString(FIRST_MIDDLE) + CaveExplorer.randomString(FIRST_END);
	}
	
	//-------------------------
	//Override Methods below
	//-------------------------
	
	public String validKeys() {
		return "wsae";
	}
	
	public void printAllowedEntry() {
		System.out.println("You can only enter 'w', 'a', or 's' to run or, you can enter 'e' to start the game!");
	}
	
	public int manageCurrentRoomShift(int direction) {
		if(direction == 0) {
			return direction;
		}
		return direction + 1;
	}
	
	public void performAction(int direction) {
		if(direction == 4) {
		    currentCommander.interact();
		} else {
			System.out.println("Random Sailor: Are you really that afraid!? What are you pressing?");
		}
	}
	
	public String getContents() {
		return "B";
	}
	
	/*
	 public String getDescription() {
		if(containsNPC() && !presentNPC.isActive()) {
			return super.getDescription() + "\n" + presentNPC.getInactiveDescription();
		} else {
			String npcDesc = "";
			if(presentNPC != null) {
				npcDesc = presentNPC.getActiveDescription();
			}
			return super.getDescription() + "\n" + npcDesc;
		}
	}
	*/
}