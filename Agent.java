

import java.util.Arrays;

public class Agent {

    int movesTaken = 0;
    int wumpiKilled = 0;

    CaveReader cave = new CaveReader();
    boolean alive = true;
    boolean hasGold = false;
    int currRow;
    int currCol;
    int numArrows;
    boolean[] percepts;

    //TODO make a movelist? maybe holds strings then decodes to run methods.


    public Agent(CaveReader cave){
        this.cave = cave;
        //Number of Wumpi equals number of arrows
        this.numArrows = cave.getNumWumpus();
        //Starting location is bottom left of cave
        this.currRow = 0;
        this.currCol = 0;

        this.percepts = cave.getRoom(currRow,currCol).getPercepts();
        initializeAgent();
    }
    //Arrow shooting logic, fire an arrow in a direction. The arrow breaks if it hits a wall. Kills if wumpus hit.
    //Return list of percepts in the order of : BREEZE STENCH GLIMMER BUMP SCREAM
    public boolean[] getAgentPercepts(){
        return this.percepts;
    }
    public void printPerformanceMetrics(){
        System.out.println("Total Actions Taken " + movesTaken);
        System.out.println("Wumpi Killed " + wumpiKilled);
    }
    public void updateAgentPercepts(){
        this.percepts = cave.getRoom(currRow, currCol).getPercepts();
        if (percepts[0]){
            System.out.println("Agent perceives a breeze..");
        }
        if (percepts[1]){
            System.out.println("Agent perceives a stench..");
        }
        if (percepts[2]){
            System.out.println("Agent perceives a glimmer..");
        }
        if (percepts[3]){
            System.out.println("Agent perceives a bump..");
        }
        if (percepts[4]){
            System.out.println("Agent perceives a scream..");
            wumpiKilled++;
        }
    }
    //Update agent's current position
    public void upDateAgentPos(int row, int col){

        this.currRow = row;
        this.currCol = col;
    }
    //Start agent in bottom left corner
    public void initializeAgent(){
        Room start = cave.getRoom(currRow, currCol);
        start.agentHere();
    }
    //Method to move agent and update location in cave. Checks if wall is hit when moving then remains in place
    //TODO make sure agent percieves a bump and remains in square if it tries to move outside the bounds of the cave
    //TODO WORK ON BUMP MECHANIC
    public boolean moveAgent(int newRow, int newCol){
        //Agent dying to wumpus
        if (cave.getRoom(newRow, newCol).hasWumpus()){
            alive = false;
            System.out.println("Ouch! Agent eaten by wumpus");
            //cave.printCave();
            return false;
        }
        //Agent dying to a pit
        if (cave.getRoom(newRow, newCol).hasPit()){
            alive = false;
            System.out.println("Ouch! Agent fell into a pit");
            //cave.printCave();
            //TODO Points here
            return false;
        }
        //scream cannot be heard forever
        cave.getRoom(currRow, currCol).scream = false;
        //Add Boundary Checking PLEASE
        int currentRow = this.currRow;
        int currentCol = this.currCol;
        Room prevRoom = cave.getRoom(currRow, currCol);
        prevRoom.agentMoved();
        Room currRoom = cave.getRoom(newRow, newCol);
        currRoom.agentHere();
        upDateAgentPos(newRow, newCol);
        updateAgentPercepts();

        //If agent feels a bump when trying to move it returns to previous square
        //Figure out a place to put this so the FOL system can slap it in the knowledge base.


        if (percepts[3]){
            System.out.println("Can't move to that space");
            currRoom.agentMoved();
            prevRoom.agentHere();
            upDateAgentPos(currentRow, currentCol);
            updateAgentPercepts();

            return false;
        }
        return true;


    }
    public void shootArrow(String direction){
        //A scream can only be heard for one move
        cave.getRoom(currRow, currCol).scream = false;
        //Make sure agent hasn't shot all their arrows
        if (this.numArrows > 0){
            movesTaken++;
            //shooting down
            if (direction.equals("d")){
                for (int i = 1; i < currRow +1; i++){
                    int arrowCell = currRow - i;
                    if (cave.getRoom(arrowCell, currCol).hasWall()){
                        break;
                    }
                    else if (cave.getRoom(arrowCell, currCol).hasWumpus()){
                        Room wumpusRoom = cave.getRoom(arrowCell, currCol);
                        //Wumpus dies and becomes a wall
                        wumpusRoom.wumpus = false;
                        wumpusRoom.wall = true;
                        wumpusRoom.bump = true;
                        cave.getRoom(currRow, currCol).scream = true;
                        //TODO make the wumpus a wall at arrowCell,currCol
                        break;
                    }
                }
            }
            //shooting up
            else if (direction.equals("u")){
                for (int i = 1; i < cave.getCaveSize() - currRow; i++){
                    int arrowCell = currRow + i;
                    if (cave.getRoom(arrowCell, currCol).hasWall()){
                        break;
                    }
                    else if (cave.getRoom(arrowCell, currCol).hasWumpus()){
                        Room wumpusRoom = cave.getRoom(arrowCell, currCol);
                        //Wumpus dies and becomes a wall
                        wumpusRoom.wumpus = false;
                        wumpusRoom.wall = true;
                        wumpusRoom.bump = true;
                        cave.getRoom(currRow, currCol).scream = true;
                        //TODO Check if surrounding stench dissapears...
                        break;
                    }
                }
                //arrow logic
            }
            //shooting left
            else if (direction.equals("l")){
                for (int i = 1; i < currCol +1; i++){
                    int arrowCell = currCol - i;
                    if (cave.getRoom(currRow, arrowCell).hasWall()){
                        break;
                    }
                    else if (cave.getRoom(currRow, arrowCell).hasWumpus()){
                        Room wumpusRoom = cave.getRoom(currRow, arrowCell);
                        //Wumpus dies and becomes a wall
                        wumpusRoom.wumpus = false;
                        wumpusRoom.wall = true;
                        wumpusRoom.bump = true;
                        cave.getRoom(currRow, currCol).scream = true;
                        //TODO Check if surrounding stench dissapears...
                        break;
                    }
                }
                //arrow logic
            }
            //shooting right
            else if (direction.equals("r")){
                for (int i = 1; i < cave.getCaveSize() - currCol; i++){
                    int arrowCell = currCol + i;
                    if (cave.getRoom(currRow, arrowCell).hasWall()){
                        break;
                    }
                    //TODO MAKE SCREAM, update percepts when an arrow is shot
                    else if (cave.getRoom(currRow, arrowCell).hasWumpus()){
                        Room wumpusRoom = cave.getRoom(currRow, arrowCell);
                        //Wumpus dies and becomes a wall
                        wumpusRoom.wumpus = false;
                        wumpusRoom.wall = true;
                        wumpusRoom.bump = true;
                        cave.getRoom(currRow, currCol).scream = true;
                        //TODO Check if surrounding stench dissapears...
                        //TODO make the wumpus a wall at arrowCell,currCol
                        break;
                    }
                }
                //arrow logic
            }
            this.numArrows = this.numArrows -1;
            updateAgentPercepts();
        }
        else{
            System.out.println("Cannot shoot an arrow");
        }
        //cave.printCave();
    }
    public boolean moveUp(){
        if(moveAgent(currRow +1, currCol)) {
            //cave.printCave();
            movesTaken++;
            return true;
        }
        return false;
    }
    public boolean moveDown(){
        if(moveAgent(currRow -1, currCol)) {
            //cave.printCave();
            movesTaken++;
            return true;
        }
        return false;
    }
    public boolean moveLeft() {
        if(moveAgent(currRow, currCol - 1)) {
            //cave.printCave();
            movesTaken++;
            return true;
        }
        return false;
    }
    public boolean moveRight() {
        if(moveAgent(currRow, currCol +1)) {
            //cave.printCave();
            movesTaken++;
            return true;
        }
        return false;
    }
    public boolean moveBack(int row, int col) {
        //cave.printCave();
        if(moveAgent(row, col)) {
            movesTaken++;
            return true;
        }
        return false;
    }
    public void pickUpGold(){
        if (cave.getRoom(currRow,currCol).hasGold()){
            System.out.println("Gold found. Victory!");
            hasGold = true;
            movesTaken++;
        }
    }

    //function to convert precepts to string
    public String[] getPerceptString() {
        String [] Spercepts =
                {"None", "None", "None", "None", "None",String.valueOf(currRow),String.valueOf(currCol)};

        if (percepts[0]){
            Spercepts[0] = "Breeze";
        }
        if (percepts[1]){
            Spercepts[1] = "Stench";
        }
        if (percepts[2]){
            Spercepts[2] = "Glimmer";
        }
        if (percepts[3]){
            Spercepts[3] = "Bump";
        }
        if (percepts[4]){
            Spercepts[4] = "Scream";
        }

        return Spercepts;
    }


    public int[][] adjacent(){
        int x = this.currRow;
        int y = this.currCol;
//		System.out.println("x :" + x + " , y" + y );
        int[][] newvalues = new int [4][2];
        if(x+1 <= this.cave.caveSize ) {
            newvalues[0][0] = x+1;
            newvalues[0][1] = y;
        }else {
            newvalues[0][0] = Integer.MAX_VALUE;
            newvalues[0][1] = Integer.MAX_VALUE;
        }
        if(x-1 >= 0){
            newvalues[1][0] = x-1;
            newvalues[1][1] = y;
        }else {
            newvalues[1][0] = Integer.MAX_VALUE;
            newvalues[1][1] = Integer.MAX_VALUE;
        }
        if(y+1 <= this.cave.caveSize) {
            newvalues[2][0] = x;
            newvalues[2][1] = y+1;
        }else {
            newvalues[2][0] = Integer.MAX_VALUE;
            newvalues[2][1] = Integer.MAX_VALUE;
        }
        if(y-1 >= 0){
            newvalues[3][0] = x;
            newvalues[3][1] = y-1;
        }
        else{
            newvalues[3][0] = Integer.MAX_VALUE;
            newvalues[3][1] = Integer.MAX_VALUE;
        }
        return newvalues;
    }
}
