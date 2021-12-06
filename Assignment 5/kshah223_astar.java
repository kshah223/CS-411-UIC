import java.util.*;
import java.io.*;

/* list nodes which declares teh required variables needed for the solution */
class listNode {
	 int[][] state = new int[4][4];
	 int exploredLevel = 0;
	 int horizontal = -1;
	 int vertical = -1;
	 listNode parent, moveUp, moveDown, moveLeft, moveRight;
	 char currentMove = '\0';
	 long timeTaken = 0;
	 long memory = 0;
	 long totalNodes = 0;
}


/* Class which contains the various operations carried out in 15 puzzle */
public class Main {
	
	/* To reverse the initial state and subsequent states on how we move */
	public static listNode reverseState(int[][] currentState, int row, int column, char move) {
		  listNode reverseArr = new listNode();
		  for (int i = 0; i < 4; i++)
			  reverseArr.state[i] = currentState[i].clone();
		
		  if (move == 'L') {
			   reverseArr.state[row][column] = reverseArr.state[row][column - 1];
			   reverseArr.state[row][column - 1] = 0;
		  } 
		  else if (move == 'R') {
			   reverseArr.state[row][column] = reverseArr.state[row][column + 1];
			   reverseArr.state[row][column + 1] = 0;
		  } 
		  else if (move == 'U') {
			   reverseArr.state[row][column] = reverseArr.state[row - 1][column];
			   reverseArr.state[row - 1][column] = 0;
		  } 
		  else if (move == 'D') {
			   reverseArr.state[row][column] = reverseArr.state[row + 1][column];
			   reverseArr.state[row + 1][column] = 0;
		  }
		  return reverseArr;
	}

	
	
	/* Function that expands the parentnode displaying the moves for the child based on the actions recieved (move left,move right,move up,move down) */
	public static void children(listNode curr) {
		  int row = curr.horizontal;
		  int column = curr.vertical;
		
		
		  if (curr.vertical != 0) {
			   curr.moveLeft = new listNode();
			   listNode recArr = new listNode();
			   curr.moveLeft.currentMove = 'L';
			   curr.moveLeft.exploredLevel = curr.exploredLevel + 1;
			   curr.moveLeft.horizontal = curr.horizontal;
			   curr.moveLeft.vertical = curr.vertical - 1;
			   curr.moveLeft.parent = curr;
			   recArr = reverseState(curr.state, curr.horizontal, curr.vertical, curr.moveLeft.currentMove);
			   curr.moveLeft.state = recArr.state;
		  }
		  if (curr.vertical != 3) {
			   curr.moveRight = new listNode();
			   listNode recArr = new listNode();
			   curr.moveRight.currentMove = 'R';
			   curr.moveRight.exploredLevel = curr.exploredLevel + 1;
			   curr.moveRight.horizontal = curr.horizontal;
			   curr.moveRight.vertical = curr.vertical + 1;
			   curr.moveRight.parent = curr;
			
			   recArr = reverseState(curr.state, curr.horizontal, curr.vertical, curr.moveRight.currentMove);
			   curr.moveRight.state = recArr.state;
		  }
		  if (curr.horizontal != 0) {
			   curr.moveUp = new listNode();
			   listNode recArr = new listNode();
			   curr.moveUp.currentMove = 'U';
			   curr.moveUp.exploredLevel = curr.exploredLevel + 1;
			   curr.moveUp.horizontal = curr.horizontal - 1;
			   curr.moveUp.vertical = curr.vertical;
			   curr.moveUp.parent = curr;
			   recArr = reverseState(curr.state, curr.horizontal, curr.vertical, curr.moveUp.currentMove);
			   curr.moveUp.state = recArr.state;
		  }
		  if (curr.horizontal != 3) {
			   curr.moveDown = new listNode();
			   listNode recArr = new listNode();
			   curr.moveDown.currentMove = 'D';
			   curr.moveDown.exploredLevel = curr.exploredLevel + 1;
			   curr.moveDown.horizontal = curr.horizontal + 1;
			   curr.moveDown.vertical = curr.vertical;
			   curr.moveDown.parent = curr;
			   recArr = reverseState(curr.state, curr.horizontal, curr.vertical, curr.moveDown.currentMove);
			   curr.moveDown.state = recArr.state;
		  }
	}	
	
	/* To derive and display the path in which the goal is reached */
	public static void path(listNode curr) {
		  LinkedList < listNode > movesArr = new LinkedList < listNode > ();
		  String movesarr = "";
		  while (curr != null) {
			  movesArr.addFirst(curr);
			  curr = curr.parent; // traversing from node through the parent of each node 
		  }
		  while (!movesArr.isEmpty()) {
			  listNode temp = movesArr.removeFirst();
			  movesarr = movesarr + temp.currentMove;
		  }
		  System.out.println("Moves: " + movesarr);
	}
	
	/* Function which does the search for the misplaced tiles */
	public static boolean astarMisplacedTiles(listNode node) {
		  listNode sol = new listNode();
		  long startTime = System.currentTimeMillis();
		  long timeTaken = 0;
		  int depth = 0;
		  //deciding 100 as random max depth
		  int maxDepth = 100;
		  LinkedList < listNode > frontier = new LinkedList < listNode > ();
		  frontier.add(node);
		  listNode curr = new listNode();
		  
		  // while timetaken is less than 180000 perform the looping
		  while (timeTaken < 180000) {
			  
			   // using approximate maxNumTiles  
			   int maxNumTiles = 1000;
			   timeTaken = System.currentTimeMillis() - startTime;
			   for (int i = 0; i < frontier.size() ; i++) { 
			       listNode replace = frontier.get(i);
			       if(misplacedTiles(replace) + replace.exploredLevel < maxNumTiles){
			    	   maxNumTiles =  replace.exploredLevel + misplacedTiles(replace);
			            curr = replace;
			       }
			   }
			   
			   // pops from the front of frontier
			   frontier.removeFirstOccurrence(curr);
			   
			   // if goal is not achieved but here it is misplaced tiles
			   if (misplacedTiles(curr) != 0) {
				   	// evaluate child function
				   	children(curr);
				    sol.totalNodes++;
				    
				    // add child to frontier
				    if (curr.moveLeft != null)
				     frontier.add(curr.moveLeft);
				    if (curr.moveRight != null)
				     frontier.add(curr.moveRight);
				    if (curr.moveUp != null)
				     frontier.add(curr.moveUp);
				    if (curr.moveDown != null)
				     frontier.add(curr.moveDown);
			   }
			   
			   // if goal is achieved return true
			   else
			   {
				   	path(curr);
				    timeTaken = System.currentTimeMillis() - startTime; 
				    long memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				    sol.memory = (memUsed) / 1024;
				    display(sol, timeTaken, true);
				    return true;
			   }
		  }
		  return false;
	}
	
	// function which finds the misplaced tiles count
	public static int misplacedTiles(listNode curr){
	     int[][] finalGoal = new int[4][4];
		 int counter = 1;
		 // Goal state initialized
		 for (int i = 0; i < 4; i++) {
			 for (int j = 0; j < 4; j++) {
				 finalGoal[i][j] = counter++;
			 }
		 }
		 finalGoal[3][3] = 0;
		 int numTiles = 0;
		 for (int i = 0; i < 4; i++){
			 for (int j = 0; j < 4; j++){
				 if(finalGoal[i][j] != curr.state[i][j])
					 numTiles++;                                          //For each misplaced tile add number of tiles misplaced
		     }
		 }
	     return numTiles;
	}
	
	public static boolean astarManhattanDistance(listNode node) {
		listNode sol = new listNode();
		  long startTime = System.currentTimeMillis();
		  long timeTaken = 0;
		  int depth = 0;
		  //deciding 100 as random max depth
		  int maxDepth = 100;
		  LinkedList < listNode > frontier = new LinkedList < listNode > ();
		  frontier.add(node);
		  listNode curr = new listNode();

		  // while timetaken is less than 180000 perform the looping
		  while (timeTaken < 180000) {
			  
			   // using approximate maxNumTiles  
			   int maxNumTiles = 1000;
			   timeTaken = System.currentTimeMillis() - startTime;
			   for (int i = 0; i < frontier.size() ; i++) { 
			       listNode replace = frontier.get(i);
			       if(manhattanPath(replace) + replace.exploredLevel < maxNumTiles){
			    	   maxNumTiles =  replace.exploredLevel + manhattanPath(replace);
			            curr = replace;
			       }
			   }
			   
			   // pops from the front of frontier
			   frontier.removeFirstOccurrence(curr);
			   
			   // if goal is not achieved but here it is manhattan path
			   if (manhattanPath(curr) != 0) {
				// evaluate child function
				   	children(curr);
				    sol.totalNodes++;
				    
				    // add child to frontier
				    if (curr.moveLeft != null)
				     frontier.add(curr.moveLeft);
				    if (curr.moveRight != null)
				     frontier.add(curr.moveRight);
				    if (curr.moveUp != null)
				     frontier.add(curr.moveUp);
				    if (curr.moveDown != null)
				     frontier.add(curr.moveDown);
			   }
			   
			   // if goal is achieved return true
			   else
			   {
				   	path(curr);
				    timeTaken = System.currentTimeMillis() - startTime; 
				    long memUsed = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
				    sol.memory = (memUsed) / 1024;
				    display(sol, timeTaken, true);
				    return true;
			   }
		  }
		  return false;
	}
	
	// function which helps to find the path for the manhattan distance
	public static int manhattanPath(listNode curr){
        int finalManhattanDistance = 0;
        int row = 0;
        int col = 0;
        int val = 0;
        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 4 ; j++) {
                val = curr.state[i][j];
                if(val > 0 && val < 5) {
                    row = 0;
                    col = val - 1;
                }
                else if(val > 4 && val < 9) {
                    row = 1;
                    col = val - 5;
                }
                else if(val > 8 && val < 13) {
                    row = 2;
                    col = val - 9;
                }
                else if(val > 12 && val < 16) {
                    row = 3;
                    col = val - 13;
                }
                else if(val == 0) {
                	row = 3;
                	col = 3;
                }
                else{
                	return 0;
                }
                finalManhattanDistance = finalManhattanDistance + Math.abs(i - row) + Math.abs(j - col);
            }
        }
        return finalManhattanDistance;
	}
	
	/* Tests whether goal has been reached returns true or false */
	public static boolean goals(int[][] state) {
		  int[][] goal = new int[4][4];
		  int count = 1;
		  for (int i = 0; i < 4; i++) {
			  for (int j = 0; j < 4; j++) {
				  goal[i][j] = count++;
			  }
		  }
		  goal[3][3] = 0;
		  return Arrays.deepEquals(state, goal);
	}	
	
	/* Displays the output in desired format */
	public static void display(listNode solution, long timeTaken, boolean display) {
		  System.out.println("Time taken: " + timeTaken + " ms");
		  System.out.println("Expanded nodes: " + solution.totalNodes);
		  System.out.println("Memory Used: " + solution.memory + " kB");
	}
	
	/* Main function */
	public static void main(String[] args) throws Exception {
		  listNode node = new listNode();
		  BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		  String input = br.readLine();
		  String inputArray[] = new String[15];
		  int arrayPointer = 0;
		  if (input != null) {
			  inputArray = input.split(" ");
		  }
		
		  for (int i = 0; i < 4; i++) {
			   for (int j = 0; j < 4; j++) {
				    int k = Integer.parseInt(inputArray[arrayPointer++]);
				    node.state[i][j] = k;
				    if (node.state[i][j] == 0) {
					     node.horizontal = i;
					     node.vertical = j;
					}
			   }
		  }
		  
		  System.out.println("Misplaced tiles heuristic");
		  boolean isSolution = astarMisplacedTiles(node);
		  System.out.println("Manhattan distance heuristic");
		  boolean isSolution1 = astarManhattanDistance(node);
	 }
}