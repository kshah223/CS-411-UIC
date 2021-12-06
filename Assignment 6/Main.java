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
	
	public static long count = 0; 
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
	private static boolean idaStar(listNode root) {
		listNode sol = new listNode();
		long startTime = System.currentTimeMillis();
		long timeTaken = 0;
		List<listNode> frontier = new ArrayList<>();
		int pathDistance = manhattanPath(root);
		frontier.add(root);

		while(true) {
		    int tmp = finalPath(root,0,pathDistance);
		    if(tmp == 0) {
		    	timeTaken = System.currentTimeMillis() - startTime; 
		    	long memUsed = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/2;
		    	sol.memory = memUsed / 1024;
		    	display(sol, timeTaken, true);
		    	return true;
		    }
		    else if(tmp == Integer.MAX_VALUE) {
		        return false;
		    }
		    pathDistance = tmp;
		}
	}
	
	public static int manhattanPath(listNode curr){
        int finalManhattanDistance = 0;
        
        for (int i = 0; i < 4 ; i++) {
            for (int j = 0; j < 4 ; j++) {
            	int row;
                int col;
                int val = curr.state[i][j];
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
	
	private static int finalPath(listNode curr, int val, int pathDistance) {
	    
	    int finalVal = val + manhattanPath(curr);

	    if(finalVal > pathDistance) { //returns new threshold if current cost exceeds
	        return finalVal;
	    }

	    if(goals(curr.state)) { //checks goal state
	        path(curr);
	        return 0;
	    }  

	    int max = Integer.MAX_VALUE;
	    for(listNode i: findNodeLinks(curr)) { //find all successors

	    	int tmp = finalPath(i ,val + i.exploredLevel,pathDistance);
	    	if(tmp == 0){    
	    		return 0;
	    	}

	    	if(tmp < max){
	    		count++;
	    		max = tmp;
	    	}
	    }
	    return max;
	}
	
	private static List<listNode> findNodeLinks(listNode curr) {
		children(curr);
		List<listNode>  tmp = new ArrayList<>();

		if(curr.moveLeft!=null) {
			int tmpLeft = curr.moveLeft.exploredLevel + manhattanPath(curr.moveLeft);
		    tmp.add(curr.moveLeft);     
		}

	    if(curr.moveRight!=null) {
		    int tmpRight = curr.moveRight.exploredLevel + manhattanPath(curr.moveRight);
		    tmp.add(curr.moveRight);     
	    }

		if(curr.moveUp!=null) {
		    int tmpUp = curr.moveUp.exploredLevel + manhattanPath(curr.moveUp);
		    tmp.add(curr.moveUp);
		}
		   
		if(curr.moveDown!=null) {
		    int tmpDown = curr.moveDown.exploredLevel + manhattanPath(curr.moveDown);
		    tmp.add(curr.moveDown);
		}
		return tmp;
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
		  System.out.println("Expanded nodes: " + count/4);
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
		  
		  boolean isSolution = idaStar(node);
	 }
}