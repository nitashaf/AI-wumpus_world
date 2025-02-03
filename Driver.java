

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;


//This class is acting as Knowledge base agent, or reactive agent by using the functions and properties
//of both Agent class and Knowledge Base.
public class Driver {
    static Agent agent;
    static Agent agentR;
    static KnowlegeBase KB;
    static int CaveSize;
    static Stack<Integer> stack1 = new Stack<Integer>();
    static int totalSafeSpaces;
    static boolean[][] Lfrontier;
    static boolean[][] LVisited;
    static int LnoVisited;
    static Random rand = new Random();



    static String[] caveNames = {"05x05-1","05x05-2", "10x10-1","10x10-2",
            "15x15-1", "15x15-2","20x20-1", "20x20-2","25x25-1", "25x25-2"};
    //static int[]caveSizes = {5,5,10,10,15,15,20,20,25,25};

    //this is the main function which decides what the next step will be according to the answers
    //of the Knowledge base inferences and knowledge
    public static boolean solveWumpus() {

        //System.out.println("In the main function");
        //System.out.println(agent.currRow + " , " + agent.currCol);

        //Update the precepts received from the current cell of cave to Knowledge Base
        KB.tell(agent.getPerceptString());

        //Asking KB if we find the Gold
        if(KB.ask("Gold",agent.currRow, agent.currCol)) {
            System.out.println("Gold Found");
            agent.pickUpGold();
        }

        //base conditions if Agent is not alive
        if(!agent.alive ) {
            System.out.println("Agent is not alive");
            return false;
        }
        //base condition if Agent found Gold, Game over
        if(agent.hasGold ) {
            System.out.println("Agent has Gold");
            return true;
        }

        //check if right move is possible, safe and not visited before
        if(agent.currCol +1 < CaveSize) {
            //System.out.println("Moving right");

            if(KB.ask(agent.currRow, agent.currCol +1) && !(KB.Visited[agent.currRow][agent.currCol +1]) ){
                System.out.println("Moving right is safe");

                //if the right movement was successful, means no wall, no wumpus and no pit
                if(agent.moveRight()) {


                    //keep track of the path, in case we need to backtrack
                    stack1.push(agent.currRow);
                    stack1.push(agent.currCol);
                    //recursion, moving forward
                    if(solveWumpus()) {
                        return true;
                    }
                    //movement is false when agent is not alive or face wall
                    else if(!agent.alive){
                        return false;
                    }
                }else {
                    //Also updating the frontier (for random movements)
                    if(KB.frontier[agent.currRow][agent.currCol +1]) {
                        KB.frontier[agent.currRow][agent.currCol +1] = false;
                    }
                }

            }

            //if not safe then ask is there a wumpus or pit ?
            //if we are confirmed of Wumpus in a single cell. shoot it and
            //check if perceive scream
            else if(KB.ask("Wumpus",agent.currRow, agent.currCol +1)) {
                agent.shootArrow("r");
                //agent.updateAgentPercepts();
                if(agent.percepts[4]) {
                    KB.tell("Scream",agent.currRow +1 , agent.currCol);
                   //recursion, moving forward
        			if(solveWumpus()) {
        				return true;
        			}else if(!agent.alive) {
        				return false;
        			}
                }
            }

//        	else if(KB.ask("Pit",agent.currRow, agent.currCol +1)) {
//
//        	}


        }//check if move Up is possible, safe and not visited
        if(agent.currRow +1 < CaveSize) {
            //System.out.println("Moving Down");
            if(KB.ask(agent.currRow +1, agent.currCol) && !(KB.Visited[agent.currRow + 1][agent.currCol])){
                System.out.println("Moving Up is safe");
                //if the up movement was successful, means no wall, no wumpus and no pit
                if(agent.moveUp()) {
                    //keep track of the path, in case we need to backtrack
                    stack1.push(agent.currRow);
                    stack1.push(agent.currCol);
                  //recursion, moving forward
                    if(solveWumpus()) {
                        return true;
                    }
                  //movement is false when agent is not alive or face wall
                    else if(!agent.alive){
                        return false;
                    }
                }else {
                    //Also updating the frontier (for random movements)
                    if(KB.frontier[agent.currRow + 1][agent.currCol]) {
                        KB.frontier[agent.currRow + 1][agent.currCol] = false;
                    }
                }
            }
            //if not safe then ask is there a wumpus or pit ?
            //if we are confirmed of Wumpus in a single cell. shoot it and
            //check if perceive scream
            else if(KB.ask("Wumpus",agent.currRow +1, agent.currCol)) {
                System.out.println("Upper cell has Wompus");
                agent.shootArrow("u");

                if(agent.percepts[4]) {
                    KB.tell("Scream",agent.currRow +1 , agent.currCol);
                    if(solveWumpus()) {
        				return true;
        			}else if(!agent.alive) {
        				return false;
        			}
                }
            }

//        	else if(KB.ask("Pit",agent.currRow + 1, agent.currCol)) {
//        		System.out.println("Upper cell has pit");
//            	if(solveWumpus()) {
//            		return true;
//            	}
//        	}
        }
        //check if move left is possible, safe, not visited
        if(agent.currCol -1 >=0) {
            //System.out.println("Moving left");
            if(KB.ask(agent.currRow, agent.currCol -1) && !(KB.Visited[agent.currRow][agent.currCol -1]) ){
                System.out.println("Moving left is safe");
                //if the left movement was successful, means no wall, no wumpus and no pit
                if(agent.moveLeft()) {
                    //keep track of the path, in case we need to backtrack
                    stack1.push(agent.currRow);
                    stack1.push(agent.currCol);
                  //recursion, moving forward
                    if(solveWumpus()) {
                        return true;
                    }//movement is false when agent is not alive or face wall
                    else if(!agent.alive){
                        return false;
                    }
                }else {
                    //Also updating the frontier (for random movements)
                    if(KB.frontier[agent.currRow][agent.currCol -1]) {
                        KB.frontier[agent.currRow][agent.currCol -1] = false;
                    }
                }
            }

            //if not safe then ask is there a wumpus or pit ?
            //if we are confirmed of Wumpus in a single cell. shoot it and
            //check if perceive scream
            else if(KB.ask("Wumpus",agent.currRow, agent.currCol -1)) {
                agent.shootArrow("l");
                //agent.updateAgentPercepts();
                if(agent.percepts[4]) {
                    KB.tell("Scream",agent.currRow +1 , agent.currCol);
                    if(solveWumpus()) {
        				return true;
        			}else if(!agent.alive) {
        				return false;
        			}
                }

            }

//        	else if(KB.ask("Pit",agent.currRow, agent.currCol -1)) {
//            	if(solveWumpus()) {
//            		return true;
//            	}
//        	}
        }//check if move down is possible, safe and not visited
        if(agent.currRow -1 >=0) {
            //System.out.println("Moving up");
            if(KB.ask(agent.currRow -1, agent.currCol) && !(KB.Visited[agent.currRow - 1][agent.currCol]) ){
                System.out.println("Moving up is safe");
                //if the down movement was successful, means no wall, no wumpus and no pit
                if(agent.moveDown()) {
                    //keep track of the path, in case we need to backtrack
                    stack1.push(agent.currRow);
                    stack1.push(agent.currCol);
                  //recursion, moving forward
                    if(solveWumpus()) {
                        return true;
                    }else if(!agent.alive){
                        return false;
                    }
                }else {
                    //Also updating the frontier (for random movements)
                    if(KB.frontier[agent.currRow-1][agent.currCol]) {
                        KB.frontier[agent.currRow-1][agent.currCol] = false;
                    }
                }


            }

            //if not safe then ask is there a wumpus or pit ?
            //if we are confirmed of Wumpus in a single cell. shoot it and
            //check if perceive scream
            else if(KB.ask("Wumpus",agent.currRow -1, agent.currCol)) {
                agent.shootArrow("d");
                //agent.updateAgentPercepts();
                if(agent.percepts[4]) {
                    KB.tell("Scream",agent.currRow +1 , agent.currCol);
                    if(solveWumpus()) {
                        return true;
                    }else if(!agent.alive) {
                        return false;
                    }
                }
            }

//        	else if(KB.ask("Pit",agent.currRow -1, agent.currCol)) {
//            	if(solveWumpus()) {
//            		return true;
//            	}
//        	}
        }

        //when no other movement is risk free, then backtrack
        System.out.println("Moving back ");


        //since we have discovered all the possible moves of this cell,
        //it is not possible to move further with this

        if(!stack1.empty()) {
            //System.out.println("Stack is not Empty()");
            int y = stack1.pop();
            int x = stack1.pop();

            //just added as a patch to not pop the same cell again
            if(y == agent.currCol && x == agent.currRow) {
                if(!stack1.empty()) {
                    y = stack1.pop();
                    x = stack1.pop();
                }
            }

            //System.out.println("Stack has : " + x +" , "+ y);
            //move back when it is possible
            agent.moveBack(x, y);
            if(solveWumpus()) {
                return true;
            }else if(!agent.alive) {
                return false;
            }

            //when backtracking is also complete, and we checked all possible
            //alternate safe routes as well, then we will move random;y using frontier
        }else {
            //randomly move and if safe move, then return back to recursion
            System.out.println("Taking random move");
            if(randomMove()) {
                System.out.println("Since Agent Random Move was successful");
                //if random move was successful, then we will get back into the recursive loop
                stack1.push(agent.currRow);
                stack1.push(agent.currCol);
                if(solveWumpus()) {
                    return true;
                }else if(!agent.alive) {
                    return false;
                }
            }else {
                System.out.println("Since Agent Random Move was not successful");
                return false;
            }
        }

        return false;
    }
    
    //This is reactive agent and moves according precepts without consulting KB 
    public static boolean solveWumpusReactive() {

    	System.out.println("In current Cell: "+agentR.currRow +" , "+agentR.currCol);
        //base conditions if Agent is not alive
        if(!agentR.alive ) {
            System.out.println("Agent is not alive");
            return false;
        }
        //base condition if Agent found Gold, Game over
        if(agentR.hasGold ) {
            System.out.println("Agent has Gold");
            return true;
        }
        //if in the current cell we found Gold pick Gold
        if(agentR.percepts[2]) {
        	agentR.pickUpGold();
        	solveWumpusReactive();
        }
        
        //if we have't received Stench or Breeze move up / right left or down
        if(!agentR.percepts[0] && (!agentR.percepts[1]) ){
        	
        	//check if move right is possible, move right update visited and frontier
        	if(agentR.currCol+1 < CaveSize && !(LVisited[agentR.currRow][agentR.currCol+1])){
        		if(agentR.moveRight()) { 
        			System.out.println("Moved Right");
        			//update path and frontier and visited
        			stack1.push(agentR.currRow);
        			stack1.push(agentR.currCol);
        			LnoVisited ++;
        			LVisited[agentR.currRow][agentR.currCol] = true;
        			updatelocalFrontier(agentR.currRow, agentR.currCol);
        			
        			if(solveWumpusReactive()) {
        				return true;
        			}else {
        				if(!agentR.alive) {
        					return false;
        				}
        			}
        			
        		}
        	}
        	
        	//check if move left is possible move left update visited and frontier
        	else if(agentR.currCol-1 >= 0  && !(LVisited[agentR.currRow][agentR.currCol-1])){
        		if(agentR.moveLeft()) { 
        			System.out.println("Moved Left");
        			//update path, visited and frontier
        			stack1.push(agentR.currRow);
        			stack1.push(agentR.currCol);
        			LnoVisited ++;
        			LVisited[agentR.currRow][agentR.currCol] = true;
        			updatelocalFrontier(agentR.currRow, agentR.currCol);
        			
        			if(solveWumpusReactive()) {
        				return true;
        			}else {
        				if(!agentR.alive) {
        					return false;
        				}
        			}
        			
        		}
        	}
        	
        	//check if move up is possible move up update visited and frontier
        	else if(agentR.currRow+1 < CaveSize && !(LVisited[agentR.currRow+1][agentR.currCol])){
        		if(agentR.moveUp()) { 
        			System.out.println("Moved Up");
        			//update path, visited and frontier
        			stack1.push(agentR.currRow);
        			stack1.push(agentR.currCol);
        			LnoVisited ++;
        			LVisited[agentR.currRow][agentR.currCol] = true;
        			updatelocalFrontier(agentR.currRow, agentR.currCol);
        			
        			if(solveWumpusReactive()) {
        				return true;
        			}else {
        				if(!agentR.alive) {
        					return false;
        				}
        			}
        			
        		}
        	}
        	
        	//check if move down is possible move down update visited and frontier
        	
        	else if(agentR.currRow-1 >= 0 && !(LVisited[agentR.currRow-1][agentR.currCol])){
        		if(agentR.moveDown()) { 
        			System.out.println("Moved Down");
        			//update path, visited and frontier
        			stack1.push(agentR.currRow);
        			stack1.push(agentR.currCol);
        			LnoVisited ++;
        			LVisited[agentR.currRow][agentR.currCol] = true;
        			updatelocalFrontier(agentR.currRow, agentR.currCol);
        			
        			if(solveWumpusReactive()) {
        				return true;
        			}else {
        				if(!agentR.alive) {
        					return false;
        				}
        			}
        			
        		}
        	}
        	
        }else {
        	//move back,
        	if(!stack1.empty()) {
        	 int y = stack1.pop();
        	 int x = stack1.pop();
        	 System.out.println("Moving Back to: " + x + " , "+ y);
        	 agentR.moveBack(x, y);
    			if(solveWumpusReactive()) {
    				return true;
    			}else {
    				if(!agentR.alive) {
    					return false;
    				}
    			}
        	}
        	//if move back is also not possible, stack is empty
        	//take random move
        	if(localRandomMove()) {
        		System.out.println("Random Moved");
        		//update path, visited and frontier
    			stack1.push(agentR.currRow);
    			stack1.push(agentR.currCol);
    			LnoVisited ++;
    			LVisited[agentR.currRow][agentR.currCol] = true;
    			updatelocalFrontier(agentR.currRow, agentR.currCol);
        		
    			if(solveWumpusReactive()) {
    				return true;
    			}else {
    				if(!agentR.alive) {
    					return false;
    				}
    			}
        	}
        	//if random move was not successful
        	else if(!agentR.alive) {
        		return false;
        	}
        	}
       return false;
    	
    }
    
    //Updating Local Frontier without consulting KB
    static private void updatelocalFrontier(int x, int y) {
        Lfrontier[x][y] = false;

        if((y+1 < CaveSize) && (!LVisited[x][y+1]) ) {
            Lfrontier[x][y+1] = true;
        }

        if((y-1 >=0) && (!LVisited[x][y-1])) {
            Lfrontier[x][y-1] = true;
        }

        if(x+1 < CaveSize && (!LVisited[x+1][y])) {
            Lfrontier[x+1][y] = true;
        }

        if(x-1 >=0  && (!LVisited[x-1][y]))  {
            Lfrontier[x-1][y] = true;
        }
    }
    
    //same logic as of Random move, here we are not asking KB
    static private boolean localRandomMove() {
    	
    	if(LnoVisited < totalSafeSpaces) {
    		
            Hashtable<Integer, Integer[]> pmovements = new Hashtable<Integer, Integer[]>();
            Integer[] array = new Integer[2];
            int count = 0;
            
            //add all possible movements in the hashtable
            for(int k = 0; k < CaveSize; k ++) {
                for (int l = 0; l < CaveSize; l ++) {
                    if(Lfrontier[k][l]) {
                        array[0] = k;
                        array[1] = l;
                        //System.out.println(k + "," + l);
                        pmovements.put(++count, array.clone());
                    }
                    
                }
            }
            
            //randomly select the cell from possible movements and move to that cell
            int random = rand.nextInt(1,count+1);
            System.out.println("Taking new move to "+ pmovements.get(random)[0] + " , " + pmovements.get(random)[1]);
            if(agentR.moveAgent(pmovements.get(random)[0], pmovements.get(random)[1]))
            {
                System.out.println("Agent Random Move was successful");
                return true;

            }
            else {
                System.out.println("Agent Random Move was not successful");
                return false;
            }           
    	}
    	//if no space cell is left
    	System.out.println("No safe cell is left");
    	return false;
    }

    static private boolean randomMove() {

        //if we have safe cells in the cave to be explored
        if(KB.noVisited < totalSafeSpaces) {
            //pick one of the frontier cell randomly and move to that cell

            Hashtable<Integer, Integer[]> pmovements = new Hashtable<Integer, Integer[]>();
            Integer[] array = new Integer[2];
            int count = 0;

            //get all the possible movements from frontier
            for(int k = 0; k < CaveSize; k ++) {
                for (int l = 0; l < CaveSize; l ++) {
                    if(KB.frontier[k][l]) {

                        if(KB.ask("Wumpus", k,l ) || KB.ask("Pit", k,l)) {
                            KB.frontier[k][l] = false;
                        }else {

                            array[0] = k;
                            array[1] = l;
                            //System.out.println(k + "," + l);
                            pmovements.put(++count, array.clone());
                        }
                    }
                }
            }
//			printFrontier();
//			Rule.printrules(false);
//			System.out.println("-------------------------------");
//			Rule.printrules(true);

            for (Map.Entry<Integer, Integer[]> set :
                    pmovements.entrySet()) {

                // Printing all elements of a Map
                System.out.println(set.getKey() + " = "
                        + set.getValue()[0] + " , "+ set.getValue()[1]);
            }


            //randomly select one movement from frontier
            int random = rand.nextInt(1,count+1);
            System.out.println("Taking new move to "+ pmovements.get(random)[0] + " , " + pmovements.get(random)[1]);
            if(agent.moveAgent(pmovements.get(random)[0], pmovements.get(random)[1]))
            {
                System.out.println("Agent Random Move was successful");
                return true;

            }
            else {
                System.out.println("Agent Random Move was not successful");
                return false;
            }
        }
        return false;
        //System.out.println("---------------------------------");
        //printVisited();
        //System.out.println("---------------------------------");

    }

    private static void printFrontier() {

        System.out.println("-------------------\n");
        for(int k = 0; k < CaveSize; k ++) {
            for( int l = 0; l < CaveSize; l ++) {
                System.out.print(KB.frontier[k][l] + "\t");
            }
            System.out.print("\n");
        }
    }

    private static void printVisited() {

        System.out.println("-------------------\n");
        for(int k = 0; k < CaveSize; k ++) {
            for( int l = 0; l < CaveSize; l ++) {
                System.out.print(KB.Visited[k][l] + "\t");
            }
            System.out.print("\n");
        }
    }

    public static void main(String[] args) {
        int[][] expMoves;

        CaveReader c1 = new CaveReader();
        //TODO make cave not hard-coded in
        c1.loadCave(caveNames[3]);
        CaveSize = c1.caveSize;
        
        //local frontier and local visited record of cells for reactive agent
        Lfrontier = new boolean[CaveSize][CaveSize];
        LVisited = new boolean[CaveSize][CaveSize];
        
        totalSafeSpaces = c1.numSafeSpace;
//        KB = new KnowlegeBase(CaveSize);
//        agent = new Agent(c1);
//        stack1.push(0);
//        stack1.push(0);
//        //c1.printCave();
//        if(solveWumpus()) {
//            System.out.println("Solved");
//            agent.printPerformanceMetrics();
//        }else {
//            System.out.println("Couldn't find the solution");
//            agent.printPerformanceMetrics();
//        }
        
        
        
        agentR = new Agent(c1);
        stack1.push(0);
        stack1.push(0);
        updatelocalFrontier(0,0);
        //c1.printCave();
        if(solveWumpusReactive()) {
            System.out.println("Solved");
            //agent.printPerformanceMetrics();
        }else {
            System.out.println("Couldn't find the solution");
            //agent.printPerformanceMetrics();
        }
    }
}
