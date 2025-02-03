
import java.io.File;
import java.util.Scanner;

public class CaveReader{
    String caveName; //cave file name
    int caveSize = 5;//default cave size
    int numWumpus;
    int numSafeSpace;

    Room[][] cave = new Room[5][5];

    public int getCaveSize(){
        return this.caveSize;
    }
    public int getNumWumpus(){
        return this.numWumpus;
    }
    public Room getRoom(int row, int col){
        return cave[row][col];
    }
    public void loadCave(String caveName){
        try{
            File file = new File("C:\\Users\\nitas\\Downloads\\Caves/" + caveName + ".cave");//Selects desired File
            Scanner read = new Scanner(file);
            String[] line1 = read.nextLine().split(",");
            for(int i = 0; i< line1.length ; i++){
                if (line1[i].startsWith("Cave Size: ")){
                    caveSize = Integer.parseInt(line1[i].split("x")[1]);
                }
                else if (line1[i].contains("Wumpus")){
                    numWumpus = Integer.parseInt(line1[i].split(": ")[1]);
                }
                else if (line1[i].contains("safe")){
                    numSafeSpace = Integer.parseInt(line1[i].split(": ")[1]);
                }
            }
            //System.out.println("Cave Size: " + caveSize );

            cave = new Room[caveSize][caveSize];

            int row = 0;

            while(read.hasNextLine()){

                int col = 0;

                String line = read.nextLine();
                line = line.substring(1, line.length() -1);//Cut off extra brackets

                String[] cells = line.split("]");

                for (String cell : cells){
                    String[] currCell = cell.replace("[", "").replace("]", "")
                            .split(",");//Remove brackets and split on commas

                    //Percept list for a given cell
                    //-----------------------------
                    boolean breeze = false;
                    boolean pit = false;
                    boolean stench = false;
                    boolean wumpus = false;
                    boolean glimmer = false;
                    boolean gold = false;
                    boolean wall = false;
                    boolean scream = false;
                    //-----------------------------

                    for (String percept : currCell){//Mark each cell with a given percept if present
                        if (percept.contains("breeze")){
                            breeze = true;
                        }
                        if (percept.contains("pit")){
                            pit = true;
                        }
                        if (percept.contains("stench")){
                            stench = true;
                        }
                        if (percept.contains("wumpus")){
                            wumpus = true;
                        }
                        if (percept.contains("glimmer")){
                            glimmer = true;
                        }
                        if (percept.contains("gold")){
                            gold = true;
                        }
                        if (percept.contains("wall")){
                            wall = true;
                        }
                    }
                    //Create new room within cave
                    cave[row][col] = new Room(breeze, pit, stench, wumpus, glimmer, gold, wall, scream);
                    //Filling the cave right to left, top to bottom
                    col++;
                }
                row++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void printCave(){
        for (int borderNum = 0; borderNum< caveSize+2; borderNum++){
            System.out.print("@");
            System.out.print("\t");
        }
        System.out.print("\n");
        for (int x = caveSize-1; x > -1; x--){
            System.out.print("@");
            System.out.print("\t");
            for (int y = 0; y < caveSize; y++){
                cave[x][y].printRoom();
                System.out.print("\t");
            }
            System.out.print("@");
            System.out.print("\t");
            System.out.println();
        }
        for (int borderNum = 0; borderNum< caveSize+2; borderNum++){
            System.out.print("@");
            System.out.print("\t");
        }
        System.out.print("\n");
    }
}