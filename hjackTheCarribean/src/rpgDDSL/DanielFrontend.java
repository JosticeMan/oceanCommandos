package rpgDDSL;

import oceanExplorer.CaveExplorer;
import oceanExplorer.Ship;

import java.util.Scanner;


public class DanielFrontend implements StevenSupport{

	private String map;
	private RPGRoom[][] room;
	private StevenBackend backend;
	private boolean won;
	private int num;
	private boolean viewed;
	private Ship a;
	private boolean lost;
	private int level;
	
	public void intro()
	{	
		System.out.println("You've encountered rogue pirates! Do your best to defeat them all.\n");
		System.out.println("Your goal is to beat up all the pirates that invaded your ship.\n"
				+ "You are represented by 'H' while enemies are represented by 'E.'\nYour field of view is locked because of all the fog around you, represented by X."
				+ "\nLocate the pirates and then engage in battle with them by inputting the enemy number once you can attack them."
				+ "\nDepending on your speed, you can move a certain amount of times. Pay attention to your stats!");

	}
	public void play() 
	{
		if (!viewed)
		{
			intro();
			viewed = true;
		}
		backend.setFrontend(this);
		updateMap();
		enemyCount();
		while(!won) {
			haveHealth();
			enemyAlive();
			if(lost)
			{
				System.out.println("You lost...");
				System.exit(0);
			}
			if(!won) {
				backend.movement();
			}
		}
		if(won) {
			System.out.println("You won!");
		}
		CaveExplorer.inventory.updateMap();
			
	}
	public boolean isWon() {
		return won;
	}
	public void setWon(boolean won) {
		this.won = won;
	}
	public DanielFrontend(Ship ship,int level)
	{
		num = 3*level;
		a = ship;
		backend = new StevenBackend(this, num,level);

		won = false;
		this.level=level;
	}
	public int getLevel() {
		return level;
	}
	public void visionIfRow2()
	{
		int humanX = backend.getHuman()[0];
		int humanY = backend.getHuman()[1];
		
		if (humanX - 1 > 0)
		{
			backend.getMap()[humanX-1][humanY].setType(3);
			isEnemy(humanX-1, humanY);
			backend.getMap()[humanX+1][humanY].setType(3);
			isEnemy(humanX+1, humanY);
			backend.getMap()[humanX][humanY+1].setType(3);
			isEnemy(humanX, humanY+1);
		}
		else
		{
			backend.getMap()[humanX+1][humanY].setType(3);
			isEnemy(humanX+1, humanY);
			backend.getMap()[humanX][humanY+1].setType(3);		
			isEnemy(humanX, humanY+1);
		}
	}
	
	
	public void setRegularVision()
	{
		int humanX = backend.getHuman()[0];
		int humanY = backend.getHuman()[1];
		int[] humanXNums = {-1,1,1,0,-1,0,1,-1};
		int[] humanYNums = {0,1,0,1,1,-1,-1,-1};

		int humanXSub2 = humanX-2;
		int humanYSub2 = humanY-2;

		int humanXAdd2 = humanX+2;
		int humanYAdd2 = humanY+2;
		
		int[] humanXNums2 = {-2,-2,-2,2,2,2};
		int[] humanYNums2 = {-1,0,1,-1,0,1};
		
		for (int i = 0; i < 8; i++)
		{
			backend.getMap()[humanX+(humanXNums[i])][humanY+(humanYNums[i])].setType(3);
			isEnemy((humanX+(humanXNums[i])),(humanY+(humanYNums[i])));

		}
		
		if (humanYAdd2 < backend.getMap()[0].length && humanXAdd2 < backend.getMap().length && humanXSub2 > 0 && humanYSub2 > 0)
		{
			for (int i = 0; i < humanYNums2.length; i++)
			{
				backend.getMap()[humanX+(humanXNums2[i])][humanY+(humanYNums2[i])].setType(3);
				isEnemy((humanX+(humanXNums2[i])),(humanY+(humanYNums2[i])));
			}
		}
	}
	public void visionBottom()
	{
		int humanX = backend.getHuman()[0];
		int humanY = backend.getHuman()[1];
		
		backend.getMap()[humanX][humanY+1].setType(3); 
		isEnemy(humanX, humanY+1);
		
		backend.getMap()[humanX-1][humanY+1].setType(3);
		isEnemy(humanX-1, humanY+1);
				
		backend.getMap()[humanX-1][humanY].setType(3);
		isEnemy(humanX-1, humanY);
		
	}
	
	public void visionRightWall()
	{
		int humanX = backend.getHuman()[0];
		int humanY = backend.getHuman()[1];
		
		if (humanX + 1 < backend.getMap().length)
		{
			backend.getMap()[humanX][humanY-1].setType(3); 
			isEnemy(humanX, humanY-1);
			
			backend.getMap()[humanX+1][humanY].setType(3);
			isEnemy(humanX+1, humanY);
			
			backend.getMap()[humanX+1][humanY-1].setType(3);
			isEnemy(humanX+1, humanY-1);
		}
		else
		{
			backend.getMap()[humanX][humanY-1].setType(3); 
			isEnemy(humanX, humanY-1);
			
			backend.getMap()[humanX-1][humanY].setType(3);
			isEnemy(humanX-1, humanY);
			
			backend.getMap()[humanX-1][humanY-1].setType(3);
			isEnemy(humanX-1, humanY-1);
		}
	}
	
	public void isEnemy(int x, int y)
	{
		for (int i = 0; i < backend.getEnemyPosition().length; i++)
		{

				if (x == backend.getEnemyPosition()[i][0] && y == backend.getEnemyPosition()[i][1])
				{
					if (backend.getEnemyValue()[i][0] >= 0)
					{
						backend.getMap()[x][y].setType(2);
					}
				}
		}
	}
	public void fogOfWar()
	{ 
		
		int humanX = backend.getHuman()[0];
		int humanY = backend.getHuman()[1];
		
		//SETS FOG
		for (int i = 0; i < backend.getMap().length; i++)
		{
			for (int j = 0; j < backend.getMap()[0].length; j++)
			{
				backend.getMap()[i][j].setType(0);
			}
		}
		
		//SETS HUMAN
		backend.getMap()[humanX][humanY].setType(1);
		
		//SETS FLASHLIGHT VISION FOR PLAYER 
		
		
		//STARTING POSITION VISION
		if (humanX + 1 < backend.getMap().length && humanY + 1 < backend.getMap()[0].length)
		{
			backend.getMap()[humanX][humanY+1].setType(3); 
			isEnemy(humanX, humanY+1);
			
			backend.getMap()[humanX+1][humanY].setType(3);
			isEnemy(humanX+1, humanY);
			
			backend.getMap()[humanX+1][humanY+1].setType(3);
			isEnemy(humanX+1, humanY+1);
			
		}
		
		if (humanX != 0 && humanY != backend.getMap().length)
		{
		
			if (humanY+1 < backend.getMap().length && humanX + 1 < backend.getMap()[0].length)
			{	
				/* field of view is 
				 * 		0		1		2		
				 *1     view	view	view
				 *2 	view	PLAYER	view
				 *3		view	view	view
				 */		
				if (humanX-1 > 0 && humanX+1 < backend.getMap().length && humanY - 1 > 0 && humanY+1 < backend.getMap().length - 1)
				{
					setRegularVision();
				}
				else
				{		
					visionIfRow2();
				}
			}
		}
		if(humanX == backend.getMap().length - 1 && humanY + 1 < backend.getMap().length)
		{
			visionBottom();
		}
		if (humanY == backend.getMap()[0].length - 1)
		{
			visionRightWall();
		}

		 
		
	}
	public void updateMap() {
		map = " ";
		
		//CLEAR 2D ARRAY
		for (RPGRoom[] a: backend.getMap())
		{
			for (RPGRoom b: a)
			{
				b.setType(3);
			}
		}
		
		//SETS TYPES 
		setType();
		backend.setTypeEnemy();
		//CREATES FOG + HUMAN
		fogOfWar();
		

		//MAP CREATION
		
		
		for (int i = 0; i < backend.getMap().length; i++)
		{
			if (i != 0 || i == backend.getMap().length - 1)
			{
				map += "|";
			}
			
			
			for (int j = 0; j < backend.getMap()[0].length; j++)
			{
				
				if (j == backend.getMap()[0].length - 1 && i != 0)
				{
					map += backend.getMap()[i][j].toString() + "|";
				}
				else
				{
					
					if (i == 0 || i == backend.getMap().length - 1)
					{
						if (i == backend.getMap().length - 1 && j != backend.getMap()[0].length - 1)
						{
							map += backend.getMap()[i][j].toString() + "___";
						}
						if (i == 0 && j != backend.getMap()[0].length - 1)
						{
							map += backend.getMap()[i][j].toString() + "� �";
						}
						if (j == backend.getMap()[0].length - 1)
						{
							map += backend.getMap()[i][j].toString() + "�  ";
						}
					}
					else
					{
						map += backend.getMap()[i][j].toString() + "   ";
					}
				}
			}
			map += "\n";
		}
		
		
			
		/*
		for (int row = 0; row < backend.getMap().length; row++)
		{
			for (int col = 0; col < backend.getMap()[0].length; col++)
			{
				if (fogCoords[row][col] == 0)
				{
					backend.getMap()[fogCoords[0][col]][col].setType(3);
				}
			}
		}
		*/
		
		System.out.println(map);
	}
	
	public void setType()
	{		
		backend.getMap()[backend.getHuman()[0]][backend.getHuman()[1]].setType(1);
		
		for (int[] a : backend.getEnemyPosition())
		{
			backend.getMap()[a[0]][a[1]].setType(2);
		}
	}
	
	public DanielFrontend getFront()
	{
		return this;
	}
	@Override
	public Ship getShip() 
	{
		
		return a;
	}
	// gets coordinates for enemies in range of player
	public int[][] getCoords()
	{
		int[][] holder;
		holder = new int[backend.getEnemyPosition().length][2];
		
		int[][] enemyCoords;
		int ctr = 0;
		int humanX = backend.getHuman()[0];
		int humanY = backend.getHuman()[1];
		
		int humanXSub1 = humanX-1;
		int humanYSub1 = humanY-1;
		int humanXSub2 = humanX-2;
		int humanYSub2 = humanY-2;
		
		int humanXAdd1 = humanX+1;
		int humanYAdd1 = humanY+1;
		int humanXAdd2 = humanX+2;
		int humanYAdd2 = humanY+2;
		
		for (int i = 0; i < backend.getEnemyPosition().length; i++)
		{
			if (humanX+2 < backend.getMap().length && humanX-2 > 0 && humanY-2 > 0 && humanY+2 < backend.getMap()[0].length)
			{
				// Check directions around human in a 2 by 2 grid
				if ((humanXSub2 == backend.getEnemyPosition()[i][0] && humanYSub2 == backend.getEnemyPosition()[i][1])
					||  (humanXSub2 == backend.getEnemyPosition()[i][0] && humanY == backend.getEnemyPosition()[i][1])					
					||  (humanXAdd2 == backend.getEnemyPosition()[i][0] && humanY == backend.getEnemyPosition()[i][1])								
					||	(humanXAdd2 == backend.getEnemyPosition()[i][0] && humanYSub1 == backend.getEnemyPosition()[i][1])
					||  (humanXAdd2 == backend.getEnemyPosition()[i][0] && humanYAdd1 == backend.getEnemyPosition()[i][1])					
					||  (humanXSub2 == backend.getEnemyPosition()[i][0] && humanYAdd1 == backend.getEnemyPosition()[i][1])
					||  (humanXSub2 == backend.getEnemyPosition()[i][0] && humanYSub1 == backend.getEnemyPosition()[i][1])
					||  (humanXSub1 == backend.getEnemyPosition()[i][0] && humanYSub1 == backend.getEnemyPosition()[i][1])
					||  (humanXSub1 == backend.getEnemyPosition()[i][0] && humanY == backend.getEnemyPosition()[i][1])
					||  (humanXSub1 == backend.getEnemyPosition()[i][0] && humanYAdd1 == backend.getEnemyPosition()[i][1])
					||  (humanX == backend.getEnemyPosition()[i][0] && humanYSub1 == backend.getEnemyPosition()[i][1]) 
					||  (humanX == backend.getEnemyPosition()[i][0] && humanYAdd1 == backend.getEnemyPosition()[i][1])
					||  (humanXAdd1 == backend.getEnemyPosition()[i][0] && humanYSub1 == backend.getEnemyPosition()[i][1])
					||  (humanXAdd1 == backend.getEnemyPosition()[i][0] && humanY == backend.getEnemyPosition()[i][1])
					|| 	(humanXAdd1 == backend.getEnemyPosition()[i][0] && humanYAdd1 == backend.getEnemyPosition()[i][1]))
				{
					holder[i][0] = backend.getEnemyPosition()[i][0];
					holder[i][1] = backend.getEnemyPosition()[i][1];					
				}
				
				// int[] humanXNums2 = {-2,-2,-2,2,2,2};
				// int[] humanYNums2 = {-1,0,1,-1,0,1};
			}
			else
			{
				if (humanX+1 < backend.getMap().length && humanX-1 > 0 && humanY-1 > 0 && humanY+1 < backend.getMap()[0].length)
				{
					// checks 8 directions
					if ((humanXSub1 == backend.getEnemyPosition()[i][0] && humanYSub1 == backend.getEnemyPosition()[i][1])
							||  (humanXSub1 == backend.getEnemyPosition()[i][0] && humanY == backend.getEnemyPosition()[i][1])
							||  (humanXSub1 == backend.getEnemyPosition()[i][0] && humanYAdd1 == backend.getEnemyPosition()[i][1])
							||  (humanX == backend.getEnemyPosition()[i][0] && humanYSub1 == backend.getEnemyPosition()[i][1]) 
							||  (humanX == backend.getEnemyPosition()[i][0] && humanYAdd1 == backend.getEnemyPosition()[i][1])
							||  (humanXAdd1 == backend.getEnemyPosition()[i][0] && humanYSub1 == backend.getEnemyPosition()[i][1])
							||  (humanXAdd1 == backend.getEnemyPosition()[i][0] && humanY == backend.getEnemyPosition()[i][1])
							|| 	(humanXAdd1 == backend.getEnemyPosition()[i][0] && humanYAdd1 == backend.getEnemyPosition()[i][1]))
					{
						holder[i][0] = backend.getEnemyPosition()[i][0];
						holder[i][1] = backend.getEnemyPosition()[i][1];	
					}
				}
			}
		}
		
		for (int j = 0; j < holder.length; j++)
		{
			if (holder[j][0] != 0 && holder[j][1] != 0)
			ctr++;
		}
		enemyCoords = new int[ctr][2];
		
		int counter = 0;
		for (int k = 0; k < holder.length; k++)
		{
			if (holder[k][0] != 0 && holder[k][1] != 0)
				{
					enemyCoords[counter][0] = holder[k][0];
					enemyCoords[counter][1] = holder[k][1];
					counter++;
				}
			
		}

		return enemyCoords;			
	}
	
	public void displayHumanStats()
	{
		if (a.getHp() >= 0)
		System.out.println("You currently have "+
			a.getHp()+"\nYour attack is "+a.getAttack()+ " points.\n"+"Your speed is "+a.getSpeed()+" points."	
				);
	}
	public void displayEnemyStats()
	{
		for (int i = 0; i < backend.getEnemyPosition().length; i++)
		{
			if (backend.getEnemyValue()[i][0] >= 0)
			System.out.println( "Enemy "+(i+1)+"'s health is "+backend.getEnemyValue()[i][0]+"\nTheir attack is equal to "+backend.getEnemyValue()[i][1]+" points.");
		}
	}
	
	public void enemyCount()
	{
		if (backend.getEnemyPosition().length == 0)
		{
			won = true;
		}
		else
		{
			won = false;
		}
	}
	
	public void haveHealth()
	{
		if (a.getHp() <= 0)
		{
			lost = true;
		}
	}
	
	public void enemyAlive()
	{
		int ctr = 0;
		for (int i = 0; i < backend.getEnemyPosition().length; i++)
		{
			if (backend.getEnemyValue()[i][0] <= 0)
			{
				ctr++;
			}
		}
		
		if (ctr == num)
		{
			won = true;
		}
	}
}
