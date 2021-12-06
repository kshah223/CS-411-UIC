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
	
	/* Function that performs the IDDFS used in AIMA 4th addition */
	public static boolean ids(listNode node) {
		  LinkedList < listNode > linkedList = new LinkedList < listNode > ();
		  listNode solution = new listNode();
		  long startTime = System.currentTimeMillis();
		  long timeTaken = 0;
		  int depth = 0;
		  //deciding 100 as random max depth
		  int maxDepth = 100;
		  while (depth < maxDepth){
			  LinkedList < listNode > frontierstack = new LinkedList < listNode > ();
			  // frontier a LIFO queue (stack) with NODE(problem.INITIAL) as an element
			  frontierstack.add(node);
			  
			  // while not IS-EMPTY(frontier) do
			  while (!frontierstack.isEmpty() && timeTaken < 180000) {
				  timeTaken = System.currentTimeMillis() - startTime;
				  listNode curr = frontierstack.removeLast(); //LIFO removes the last inserted element
				  
				  // if problem.IS-GOAL(node.STATE) then return node
				  if (goals(curr.state)) {
					  path(curr);
					  timeTaken = System.currentTimeMillis() - startTime;
					  long afterUsedMem = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
					  solution.memory = (afterUsedMem) / 1024;
					  display(solution, timeTaken, true);
					
					  return true;
				  } 
		
				  // else if not IS-CYCLE(node) do
				  else if(curr.exploredLevel < depth) 
				  {
					  	// to check for repeated states
					    if (!linkedList.contains(curr))
					    	linkedList.add(curr);
					    
					    // evaluate child function
					    children(curr);
					    solution.totalNodes++;
					
					    // add child to frontier
					    if (curr.moveLeft != null)
					    	frontierstack.add(curr.moveLeft);
					    if (curr.moveRight != null)
					    	frontierstack.add(curr.moveRight);
					    if (curr.moveUp != null)
					    	frontierstack.add(curr.moveUp);
					    if (curr.moveDown != null)
					    	frontierstack.add(curr.moveDown);
				  }
			  }
			  depth++;
		  }
		  return false;
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
		  
		  boolean isSolution = ids(node);
	 }

}