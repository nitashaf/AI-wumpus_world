
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Rule {
    static int CaveSize;
    static	HashMap<String, String> ruleForTrue = new HashMap<String, String>();
    static	HashMap<String, String> ruleForFalse = new HashMap<String, String>();
    static boolean[][] noPit;
    static boolean[][] noWumpus;
    static boolean[][] pit;
    static boolean[][] wumpus;
    static boolean[][] wall;


    //constructor will update the rules class with basic rules.
    public Rule(String[] baseRulesTrue, String[] baseRulesFalse, int size) {

        for(int i = 0; i < baseRulesTrue.length; i++) {
            String[] sr = ruleparse(baseRulesTrue[i]);
            ruleForTrue.put(sr[0], sr[1]);
        }

        for(int i = 0; i < baseRulesFalse.length; i++) {
            String[] sr = ruleparse(baseRulesFalse[i]);
            ruleForFalse.put(sr[0], sr[1]);
        }
        CaveSize = size;
        noPit = new boolean[CaveSize][CaveSize];
        noWumpus = new boolean[CaveSize][CaveSize];
        pit = new boolean[CaveSize][CaveSize];
        wumpus = new boolean[CaveSize][CaveSize];
        wall = new boolean[CaveSize][CaveSize];
    }

    //We are storing rules in the form of strings, hence rules need to be parsed
    private static String[] ruleparse(String exp) {

        String[] sppitRule = exp.split("V");

        //if rule has more than 1 or sign, we will put the whole string into the
        //second part of the rule.
        if(sppitRule.length > 2) {
            String appendRule = sppitRule[1];
            for(int i = 2; i < sppitRule.length; i ++) {
                appendRule += " V " +sppitRule[i];
            }
            sppitRule[1] = appendRule;
        }
		/*
		for(int i = 0; i < sppitRule.length; i ++) {
			System.out.print(sppitRule[i] + "\n");
		}
		*/
        return sppitRule;

    }

    //For clarification, we are storing True rules and False rules separately
    //we unify the rules, when having same rules 
    public static void addRuleTrue(String ruleName, int x, int y, boolean[][] visited) {

        String rulekey = "! "+ ruleName + "(x,y) ";
        String newRuleKey;
        String ruleAction = "";
        if(ruleForTrue.containsKey(rulekey)) {

            newRuleKey  = rulekey.replace("x", String.valueOf(x));
            newRuleKey  = newRuleKey.replace("y", String.valueOf(y));

            ruleAction = unifyTrue(rulekey, x, y, visited);
            if(ruleAction != "") {
                ruleForTrue.put(newRuleKey, ruleAction);
                orElimination(ruleName, x, y);
            }else {
                System.out.println("There is issue with base rule");
            }
        }
        else {
            System.out.println("There is no base rule for this rule");
        }
    }

    //function to apply unification and resolving Adj function
    private static String unifyTrue(String baseRuleName, int x, int y, boolean[][] visited) {

        String RuleAction = ruleForTrue.get(baseRuleName);
        String resolvedAction = "";
        
        if(RuleAction.contains("Adj")) {
        	
            String RuleActions[] = RuleAction.split(" V ");
            String[] actionName = RuleAction.split("\\(");
            //System.out.println(actionName[0]);
            
            //resolve adj for x, y
            int[][] adjArray = adjacent(x, y);
            for(int i = 0; i < 4; i ++) {
                if(adjArray[i][0] !=Integer.MAX_VALUE && adjArray[i][1] !=Integer.MAX_VALUE) {
                    //if(!(visited[adjArray[i][0]][adjArray[i][1]])) {

                        if(resolvedAction != "") {
                            resolvedAction += " V ";
                        }
                        resolvedAction += actionName[0]+ "("+adjArray[i][0]+","+adjArray[i][1]+")";
                    //}
                }
            }
            resolvedAction += " V " + RuleActions[1].replace("x", String.valueOf(x));
            resolvedAction = resolvedAction.replace("y", String.valueOf(y));      
        }else {
        	
        if(resolvedAction == "") {
            resolvedAction = RuleAction;
        }

        resolvedAction = resolvedAction.replace("x", String.valueOf(x));
        resolvedAction = resolvedAction.replace("y", String.valueOf(y));
        }

        return resolvedAction;
    }

    //For adding rules, we will first apply the unification with the current rules.
    //after adding rule into the rules, we will also add rule's truth values in 2d array.which will be the
    //and elimination part of FOL system

    public static void addRuleFalse(String ruleName, int x, int y) {

        String rulekey = ruleName + "(x,y) ";
        String newRuleKey;
        String ruleAction = "";
        if(ruleForFalse.containsKey(rulekey)) {

            newRuleKey  = rulekey.replace("x", String.valueOf(x));
            newRuleKey  = newRuleKey.replace("y", String.valueOf(y));

            ruleAction = unifyFalse(rulekey, x, y);
            if(ruleAction != "") {
                ruleForFalse.put(newRuleKey, ruleAction);
                andElimination(ruleName, x, y);
            }else {
                System.out.println("There is issue with base rule");
            }
        }
        else {
            System.out.println("There is no base rule for this rule");
        }
    }




    private static String unifyFalse(String baseRuleName, int x, int y) {

        String RuleAction = ruleForFalse.get(baseRuleName);
        String resolvedAction = "";

        //if the rule action has adj in it, it means it has 2 parts,
        if(RuleAction.contains("Adj")) {

            String RuleActions[] = RuleAction.split(" A ");
            String[] actionName = RuleAction.split("\\(");
            //System.out.println(actionName[0]);

            //resolve adj for x, y
            int[][] adjArray = adjacent(x, y);
            for(int i = 0; i < 4; i ++) {
                if(adjArray[i][0] !=Integer.MAX_VALUE && adjArray[i][1] !=Integer.MAX_VALUE) {

                    if(resolvedAction != "") {
                        resolvedAction += " A ";
                    }
                    resolvedAction += actionName[0]+ "("+adjArray[i][0]+","+adjArray[i][1]+")";
                }
            }
            resolvedAction += " A " + RuleActions[1].replace("x", String.valueOf(x));
            resolvedAction = resolvedAction.replace("y", String.valueOf(y));

        }
        else {
            resolvedAction = RuleAction.replace("x", String.valueOf(x));
            resolvedAction = resolvedAction.replace("y", String.valueOf(y));
        }

        return resolvedAction;
    }
    
    //and elimination will store all the negative values separately into an array
    //I am not using strings here to avoid conversion.
    //Choosing array as data structures simplifies complexity of string rules
    private static void andElimination(String ruleName, int x, int y) {

        int[][] array = adjacent(x, y);
        //storing them separately in a 2d array to search them easily.
        if(ruleName == "Stench") {
            noWumpus[x][y] = true;
            for(int h = 0; h < 4; h ++) {
                if(array[h][0] != Integer.MAX_VALUE && array[h][1] != Integer.MAX_VALUE) {
                    noWumpus[array[h][0]][array[h][1]] = true;
                }
            }

        }else if(ruleName == "Breeze") {
            noPit[x][y] = true;
            for(int h = 0; h < 4; h ++) {
                if(array[h][0] != Integer.MAX_VALUE && array[h][1] != Integer.MAX_VALUE) {
                    noPit[array[h][0]][array[h][1]] = true;
                }
            }
        }

    }
    //or elimination will store all the positive values separately into an array
    //they are used just as helpers, not part of inference
    private static void orElimination(String ruleName, int x, int y) {

        //System.out.println("Rule Name : "+ ruleName);
        int[][] array = adjacent(x, y);
        //storing them separately in a 2d array to search them easily.
        if(ruleName == "Stench") {
            wumpus[x][y] = true;
            for(int h = 0; h < 4; h ++) {
                if(array[h][0] != Integer.MAX_VALUE && array[h][1] != Integer.MAX_VALUE) {
                    wumpus[array[h][0]][array[h][1]] = true;
                }
            }

        }else if(ruleName == "Breeze") {
        	//System.out.println("are we here???");
            pit[x][y] = true;
            for(int h = 0; h < 4; h ++) {
                if(array[h][0] != Integer.MAX_VALUE && array[h][1] != Integer.MAX_VALUE) {
                    pit[array[h][0]][array[h][1]] = true;
                }
            }
        }else if(ruleName == "Scream") {
            //System.out.println("Why we are here");
            //find rule which has only only wumpus and update that wumpus and rule
            wumpus[x][y] = false;
            String ruleKey = "";
            String ruleAction = "";

            ruleKey = findRule("Wumpus", x, y);
            if(ruleKey != "") {
                ruleAction = ruleForTrue.get(ruleKey);

                if(ruleAction.split(" V ").length <= 1 && ruleAction.contains("("+x+","+y+")")) {
                    ruleForTrue.remove(ruleKey);
                }

            }


        }else if(ruleName == "Bump") {
            wall[x][y] = true;
        }

    }
    //finding the exact rule with the clause
    private static String findRule(String name, int x, int y) {

        String regExp = name+"("+x+","+y+")";
        String rulekey = "";
        for (Map.Entry<String, String> setF :
                ruleForTrue.entrySet()) {

            if(setF.getValue().contains(regExp)) {
                rulekey = setF.getKey();
            }
        }
        //System.out.println("Rule Found"+ rulekey);
        return rulekey;
    }

    //Function to return adjacent values of x and y which are possible
    //else saving the largest values so to avoid them
    private static int[][] adjacent(int x, int y){

//		System.out.println("x :" + x + " , y" + y );
        int[][] newvalues = new int [4][2];
        if(x+1 < CaveSize) {
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
        if(y+1 < CaveSize) {
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

//		for(int k = 0; k < 4; k ++) {
//			System.out.println(newvalues[k][0] + " " + newvalues[k][1]);
//		}
        return newvalues;
    }

    //printing true and false rules, just for debugging
    public static void printrules(boolean b) {
        if(b) {
            for (Map.Entry<String, String> set :
                    ruleForTrue.entrySet()) {

                // Printing all elements of a Map
                System.out.println(set.getKey() + " V "
                        + set.getValue());
            }
        }
        else {
            for (Map.Entry<String, String> setF :
                    ruleForFalse.entrySet()) {

                // Printing all elements of a Map
                System.out.println(setF.getKey() + " V "
                        + setF.getValue());
            }
        }
    }

    //main function just used for testing
	public static void main(String[] args) {
//
//		CaveSize = 25;
//		//basic rules
//		String rule1 = "! Stench(x,y) V Wumpus(Adj(x,y))";
//		String rule2 = "! Breeze(x,y) V Pit(Adj(x,y))";
//		String rule3 = "! Glitter(x,y) V Gold(x,y)";
//		String rule4 = "! Scream(x,y) V DeadWumpus()";
//		String rule5 = "Stench(x,y) V ! Wumpus(Adj(x,y))";
//		String rule6 = "Breeze(x,y) V ! Pit(Adj(x,y))";
//		String rule7 = "Glitter(x,y) V ! Gold(x,y)";
//		String rule8 = "Scream(x,y) V ! DeadWumpus()";
//
//
//		ruleForTrue.put(ruleparse(rule1)[0], ruleparse(rule1)[1]);
//		ruleForTrue.put(ruleparse(rule2)[0], ruleparse(rule2)[1]);
//		ruleForTrue.put(ruleparse(rule3)[0], ruleparse(rule3)[1]);
//		ruleForTrue.put(ruleparse(rule4)[0], ruleparse(rule4)[1]);
//
//		ruleForFalse.put(ruleparse(rule5)[0], ruleparse(rule5)[1]);
//		ruleForFalse.put(ruleparse(rule6)[0], ruleparse(rule6)[1]);
//		ruleForFalse.put(ruleparse(rule7)[0], ruleparse(rule7)[1]);
//		ruleForFalse.put(ruleparse(rule8)[0], ruleparse(rule8)[1]);
//
//
//		printrules(true);
//        addRuleTrue("Stench", 1,1);
//        printrules(true);
//        addRuleFalse("Stench", 2,1);
//        printrules(false);
//
}







}
