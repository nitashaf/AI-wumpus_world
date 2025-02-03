import java.util.HashMap;
import java.util.Map;


public class KnowlegeBase {
    static int CaveSize;
    static Rule rule;
    static boolean [][] Safe ;
    static boolean [][] Visited;
    static boolean[][] frontier;
    static int noVisited;



    //BaseRules for wumpus World
    static String[] ruleTrue= {
            "! Stench(x,y) V Wumpus(Adj(x,y)) V Wumpus(x,y)",
            "! Breeze(x,y) V Pit(Adj(x,y)) V Pit(x,y)",
            "! Glimmer(x,y) V Gold(x,y)",
            "! Scream(x,y) V DeadWumpus()",
            "! Bump(x,y) V Wall(x,y)"};

    static String[] ruleFalse= {
            "Stench(x,y) V ! Wumpus(Adj(x,y)) A ! Wumpus(x,y)",
            "Breeze(x,y) V ! Pit(Adj(x,y)) A ! Pit(x,y)",
            "Glimmer(x,y) V ! Gold(x,y)",
            "Scream(x,y) V ! DeadWumpus()",
            "Bump(x,y) V ! Wall(x,y)"};

    
    static	HashMap<String, String> tempRules = new HashMap<String, String>();

    public KnowlegeBase(int size) {
        CaveSize = size;
        rule = new Rule(ruleTrue, ruleFalse, size);
        // TODO Auto-generated constructor stub
        this.Safe = new boolean[size][size];
        this.Visited = new boolean[size][size];
        this.frontier = new boolean[size][size];
        noVisited = 0;
    }

    //Tell function is query to KB from agent, Agent informs the KB about the Percepts using this fucntion
    public static void tell(String[] percepts) {

        int x = Integer.parseInt(percepts[5]);
        int y = Integer.parseInt(percepts[6]);
        //System.out.println("X: "+ x + "Y:"+ y);

        //when KB receives the precepts, it means it is safe and visited
        if(!Visited[x][y]) {

            Safe[x][y] = true;
            Visited[x][y] = true;
            //add to frontier the next possible cells and mark cell safe i.e
            //no wumpus and no pit
            updateCellSafe(x,y);
            updateFrontier(x,y);
            noVisited++;

            for(int k = 0; k < 5; k ++) {

                if(percepts[k] != "None") {
                    rule.addRuleTrue(percepts[k],x, y, Visited);
                }
                else {
                    if(k == 0) {
                        rule.addRuleFalse("Breeze",x,y);
                    }
                    if(k == 1) {
                        rule.addRuleFalse("Stench",x,y);
                    }
                    if(k == 2) {
                        rule.addRuleFalse("Glimmer",x,y);
                    }
                    if(k == 3) {
                        rule.addRuleFalse("Bump",x,y);
                    }
                    if(k == 4) {
                        rule.addRuleFalse("Scream",x,y);
                    }
                }
            }
        }
        for(int l = 0; l < percepts.length; l++) {
            System.out.print(percepts[l] + " , ");
        }
//		rule.printrules(false);
//		System.out.println("-------------------------------");
//		rule.printrules(true);
    }

    //Frontier are all possible next cell movements which have not yet visited, 
    //and borders the visited cell,
    //doesn't for sure has the wumpus or pit,
    //can be risk, but not sure of dangers
    static private void updateFrontier(int x, int y) {

        //mark the visited cell as false and possible moves true
        frontier[x][y] = false;

        if((y+1 < CaveSize) && (!Visited[x][y+1]) && (!ask("Wumpus", x, y+1)) && (!ask("Pit", x, y+1)) ) {
            frontier[x][y+1] = true;
        }

        if((y-1 >=0) && (!Visited[x][y-1]) && !ask("Wumpus", x, y-1) && !ask("Pit", x, y-1 )) {
            frontier[x][y-1] = true;
        }

        if(x+1 < CaveSize && (!Visited[x+1][y]) && !ask("Wumpus", x+1, y) && !ask("Pit", x+1, y )) {
            frontier[x+1][y] = true;
        }

        if(x-1 >=0  && (!Visited[x-1][y]) && !ask("Wumpus", x-1, y) && !ask("Pit", x-1, y))  {
            frontier[x-1][y] = true;
        }

    }

    //if we receive precepts from this cell, it means it is safe
    // hence we will remove all the pits and wumpus from it if there are any
    //and update the rules also
    private static void updateCellSafe(int x, int y) {
    	if(rule.pit[x][y]) {    		
    		String ruleKey = findRule("Pit", x,y);
            if(ruleKey != "") {
        		
        		rule.pit[x][y] = false;
        		rule.noPit[x][y] = true;
        		//also update the rule which has the same Pit
                unitResolution(ruleKey, "Pit", x, y);
            }else {
                System.out.println("Couldn't find the rule in Visited for Pit in " +x + " , " + y);
            }
    		
    	}
    	if(rule.wumpus[x][y]) {
    		
    		
    		String ruleKey = findRule("Wumpus", x,y);
            if(ruleKey != "") {
        		
            	System.out.println("We have Wumpus marked true for " + x + " , " + y);
        		rule.wumpus[x][y] = false;
        		rule.noWumpus[x][y] = true;
        		//also update the rule which has the same Wumpus
                unitResolution(ruleKey, "Wumpus", x, y);
            }else {
                System.out.println("Couldn't find the rule in Visited for Wumpus in " +x + " , " + y);
            }
    		

    	}
    }

    //Tell and Ask functions of the KB
    public static void tell(String percept, int x, int y) {
        rule.addRuleTrue(percept,x, y, Visited);
    }
    
    //Simple Ask function is, if the cell safe ?
    public static boolean ask(int a, int b) {
        //if the cell at location x,y is safe ?
        String x = String.valueOf(a);
        String y = String.valueOf(b);
        boolean isSafe;


        isSafe = search(a,b);
        //System.out.println(isSafe);
        return isSafe;
    }

    //Asking specific query from KB, that whether this cell has Gold, Wumpus or Pit
    public static boolean ask(String action, int a, int b) {

        String ruleKey = "";
        String ruleAction = "";

        if(action == "Gold") {
            //System.out.print("Ask Golding in "+ a + " , " + b );
            ruleKey = "! Glimmer("+a+","+b+") ";
            if(rule.ruleForTrue.containsKey(ruleKey)) {
                return true;
            }

        }
        else if(action == "Wumpus") {
        	
        	//First we call inference
            inference();
            //Find specify rule with same cell wumpus
            ruleKey = findRule(action, a, b);
            if(ruleKey != "") {
                ruleAction = rule.ruleForTrue.get(ruleKey);
                //update the rule by removing the specific clause which has same Danger using resolution
                if(ruleAction.split(" V ").length <= 1 && ruleAction.contains("("+a+","+b+")")) {
                    System.out.println("Wumpus Found  in " + a + " , " + b);

                    //if we have any possible pit here as well in the cell where Wumpus is found
                    //we will remove that
                    if(rule.pit[a][b] ) {
                        rule.pit[a][b] = false;
                        rule.noPit[a][b] = true;
                        ruleKey = findRule("Pit", a,b);
                        if(ruleKey != "") {
                            unitResolution(ruleKey, "Pit", a, b);
                        }else {
                            System.out.println("Couldn't find the rule for Pit in " +a + " , " + b);
                        }
                    }
                    return true;
                }

            }
        }
        else if(action == "Pit") {
        	//First we call inference
            inference();
          //Find specify rule with same cell which has Pit
            ruleKey = findRule(action, a, b);
            if(ruleKey != "") {
                ruleAction = rule.ruleForTrue.get(ruleKey);
              //update the rule by removing the specific clause which has same Danger using resolution
                if(ruleAction.split(" V ").length <= 1 && ruleAction.contains("("+a+","+b+")")) {
                    System.out.println("Pit Found in "+ a + " , " + b);
                    return true;
                }

            }
        }
        return false;

    }

    //search if the cell is safe or not (for giving the answer of is Safe (ask)
    private static boolean search(int x, int y) {

        boolean isSafe = false;

        if(rule.wumpus[x][y] || rule.pit[x][y]) {
            //inference();
            return isSafe;
        }else if(rule.noWumpus[x][y] && rule.noPit[x][y]) {
            isSafe = true;
        }
        return isSafe;
    }


    //Inference is when we find the negative of the same clause we delete it from the KB storage
    //and rules
    private static void inference() {

        String ruleKey;
        for(int r = 0; r < CaveSize; r ++) {
            for(int c =0; c < CaveSize; c ++) {
            	//we do it for both Pits and Wumnpus
                if(rule.pit[r][c] && rule.noPit[r][c] ) {
                    
                	//if we have complementary (/negative clause for same cell) find rule and update
                    ruleKey = findRule("Pit", r,c);
                    if(ruleKey != "") {
                    	rule.pit[r][c] = false;
                    	//unit resolution update the rule by deleting updating the clauses
                        unitResolution(ruleKey, "Pit", r, c);
                    }else {
                        System.out.println("Couldn't find the rule for Pit in " +r + " , " + c);
                    }
                }
                if(rule.wumpus[r][c] && rule.noWumpus[r][c] ) {

                    //System.out.println("Wumpus and no wumpus Found at "+ r + " , " + c);
                	//if we have complementary (/negative clause for same cell) find rule and update
                    ruleKey = findRule("Wumpus", r,c);
                    if(ruleKey != "") {
                    	rule.wumpus[r][c] = false;
                    	//unit resolution update the rule by deleting updating the clauses
                        unitResolution(ruleKey, "Wumpus", r, c);
                    }else {
                        System.out.println("Couldn't find the rule for Wumpus" +r + " , " + c);
                    }
                }
            }
        }

    }

//just printing functions  to help see how KB is updating the helping arrays
    static private void printWompus(boolean var) {
        if(var) {
            for(int r = 0; r < CaveSize; r ++) {
                for(int c =0; c < CaveSize; c ++) {
                    System.out.print(rule.wumpus[r][c] + "\t");
                }
                System.out.print("\n");
            }
        }
        else {
            for(int r = 0; r < CaveSize; r ++) {
                for(int c =0; c < CaveSize; c ++) {
                    System.out.print(rule.noWumpus[r][c] + "\t");
                }
                System.out.print("\n");
            }
        }
    }

  //just printing functions  to help see how KB is updating the helping arrays
    static private void printPit(boolean var) {
        if(var) {
            for(int r = 0; r < CaveSize; r ++) {
                for(int c =0; c < CaveSize; c ++) {
                    System.out.print(rule.pit[r][c] + "\t");
                }
                System.out.print("\n");
            }
        }
        else {
            for(int r = 0; r < CaveSize; r ++) {
                for(int c =0; c < CaveSize; c ++) {
                    System.out.print(rule.noPit[r][c] + "\t");
                }
                System.out.print("\n");
            }
        }
    }
    //Unit resolution updates the rules when same cell has negative and positive clause of danger
    private static void unitResolution(String ruleKey, String clause, int x, int y) {

        String ruleClause = rule.ruleForTrue.get(ruleKey);
        String newClause = "";
        tempRules.put(ruleKey, ruleClause);
        //it updates all clauses one by one
        String[] ruleClausParts = ruleClause.split(" V ");
        for(int l = 0; l < ruleClausParts.length; l ++) {
        	//if we are left with one clause only, it means danger is found in that cell
            if(!(ruleClausParts[l].contains(clause+"("+x+","+y+")"))){
                if(newClause != "") {
                    newClause += " V ";
                }
                newClause += ruleClausParts[l];
            }
        }
        //System.out.println("--------------New Update"+ newClause);
        rule.ruleForTrue.put(ruleKey, newClause);
    }
    //this function is just for finding the rules when we dont know the key but just the action part
    //it finds the key part using action
    private static String findRule(String name, int x, int y) {

        String regExp = name+"("+x+","+y+")";
        String rulekey = "";
        for (Map.Entry<String, String> setF :
                rule.ruleForTrue.entrySet()) {

            if(setF.getValue().contains(regExp)) {
                rulekey = setF.getKey();
            }
        }
        //System.out.println("Rule Found"+ rulekey);
        return rulekey;
    }

    //used just to test functionalities
    public static void main(String[] args) {
//		//BREEZE STENCH GLIMMER BUMP SCREAM
//		rule = new Rule(ruleTrue, ruleFalse, 10);
//		CaveSize = 10;
//	     Safe = new boolean[CaveSize][CaveSize];
//	     Visited = new boolean[CaveSize][CaveSize];
//	     frontier = new boolean[CaveSize][CaveSize];
//	     noVisited = 0;

//		String[] percept1 = {"None", "Stench", "None", "None", "None","0","0"};
//
//		String[] percept2 = {"Breeze", "None", "None", "None", "None","0","1"};
//
//		String[] percept3 = {"Breeze","Stench","None", "None", "None", "1","1"};
//		String[] percept4 = {"None" , "Stench" , "None" , "None" , "Scream" , "2" , "4"};
//
//
//		tell(percept1);
		
		
//		System.out.println("------Wumpus-----");
//		printWompus(false);
//		System.out.println("---------------------------");
//		printWompus(true);
////		ask(1,1);
//		System.out.println("------Pit-----");
//		printPit(false);
//		System.out.println("---------------------------");
//		printPit(true);
//		ask("Pit",0,1);
////		if(!(ask(0,1)))
////		{
////			ask("Pit",0,1);

////		}
//		tell(percept2);
//		ask("Wumpus",0,1);
//		printWompus(false);
////		if(!(ask(0,2)))
////		{
////			ask("Pit",0,2);
////			ask("Wumpus",0,2);
////		}
//		//ask(1,1);
//		//ask(1,0);
//		tell(percept3);
//		System.out.println("------Wumpus-----");
//		printWompus(false);
//		System.out.println("---------------------------");
//		printWompus(true);
////		ask(1,1);
//		System.out.println("------Pit-----");
//		printPit(false);
//		System.out.println("---------------------------");
//		printPit(true);
//		ask("Pit",0,1);
//		ask("Wumpus",1,0);
//
//		ask("Pit",0,2);
//		ask("Wumpus",1,0);
//		//ask("Gold",1,0);
//
//		//tell(percept3);
//
//		rule.printrules(false);
//		System.out.println("-------------------------------");
//		rule.printrules(true);
    }

}
