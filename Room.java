
public class Room {
    boolean breeze;
    boolean pit;
    boolean stench;
    boolean wumpus;
    boolean glimmer;
    boolean gold;
    boolean wall;
    boolean agent = false;
    boolean bump;
    boolean scream;

    Room(boolean breeze, boolean pit, boolean stench,
         boolean wumpus, boolean glimmer, boolean gold, boolean wall, boolean scream){
        this.breeze = breeze;
        this.pit = pit;
        this.stench = stench;
        this.wumpus = wumpus;
        this.glimmer = glimmer;
        this.gold = gold;
        this.wall = wall;
        if (wall){
            this.bump = true;
        }
    }

    public void agentHere(){
        this.agent = true;
    }
    public void agentMoved(){
        this.agent = false;
    }
    public boolean[] getPercepts(){
        return new boolean[]{this.breeze, this.stench, this.glimmer, this.bump, this.scream};
    }
    public boolean hasWumpus(){
        return wumpus;
    }
    public boolean hasPit(){
        return pit;
    }
    public boolean hasWall(){
        return wall;
    }
    public boolean hasGold() {
        return gold;
    }
    public void printRoom(){
        if (breeze){
            System.out.print("~");
        }
        if (pit){
            System.out.print("P");
        }
        if (stench){
            System.out.print("s");

        }
        if (wumpus){
            System.out.print("W");

        }
        if (glimmer){
            System.out.print("g");

        }
        if (gold){
            System.out.print("G");

        }
        if (wall){
            System.out.print("|");

        }
        if (agent){
            System.out.print("A");
        }
        if (!agent & !breeze & !pit & !stench & !wumpus & !glimmer & !gold & !wall){
            System.out.print("-");
        }
    }
}
