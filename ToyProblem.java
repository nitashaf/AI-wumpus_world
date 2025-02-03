import java.util.ArrayList;
import java.util.Iterator;

public class ToyProblem {
	
	//rules given for Toy Problem
    static String[] rules= {
    		"Animal(F(x)) ∨ Loves(G(x),x)",
    		"!Loves(x,F(x)) ∨ Loves(G(x),x)",
    		"!Loves(y,x) ∨ !Animal(z) ∨ !Kills(x,z)",
    		"!Animal(x) ∨ Loves(Jack,x)",
    		"Kills(Jack,Tuna) ∨ Kills(Curiosity,Tuna)",
    		"Cat(Tuna)",
    		"!Cat(x) ∨ Animal(x)",
    		"!Kills(Curiosity,Tuna)"};
    
    static ArrayList<String> predicates;
    
    //first we will parse all predicates from the rules and update them into
    //array list
    static private void updatePredicates() {
    	predicates = new ArrayList<String>();
    	String[] sppitRule;
    	for(int i = 0; i < rules.length; i++) {
    		sppitRule = rules[i].split("∨");
    		
    		for(int l= 0; l < sppitRule.length; l++) {
    			if(!sppitRule[l].equals("")) {
    			predicates.add(sppitRule[l]);
    			}
    		}
    	}
    }
    //Print predicate function
    static private void printPredicates() {
    	System.out.println("-------------Predicates-------------");
    	for(int i = 0; i < predicates.size(); i++) {   
    	    System.out.println(predicates.get(i));
    	} 
    }
    //Print Rule Function
    static private void printRules() {
    	System.out.println("-------------Rules------------");
    	for(int r = 0; r < rules.length; r ++) {
    		System.out.println(rules[r]);
    	}
    }

    //Its unification and works for 2 variables as well as for single as well
    static private boolean unification() {
    	
    	System.out.println("---------------------In Unification-----------");
    	
    	printPredicates();
    	if(predicates.isEmpty()) {
    		System.out.print("Predicates are empty");
    		return true;
    	}
    	printRules();
    	//if predicate has same name substitute the value of variable with constant
		String Part []; 
		String newrule;
		String newPredicate;
		boolean isChange = false;
    	for (Iterator iterator = predicates.iterator(); iterator.hasNext();) {
			
    		String predicate = (String) iterator.next();
    		
    		
    		//each predicate is divided into first and second part
    		System.out.println(predicate);
    		Part = parts(predicate);
    		Part[0] = Part[0].replace(" ", "");
    		Part[1] = Part[1].replace(" ", "");
    		
    		//System.out.println(Part[0]);
    		//System.out.println(Part[1]);
    		//if the second part doesn't contains variables, it means they have only constant
    		if(!Part[1].contains("x")  && !Part[1].contains("z") && !Part[1].contains("(y")) {
    			//System.out.println("Second part with constant is:"+ Part[1]);
    			System.out.println("Predicate with Constant is: "+predicate);
    			
				//System.out.println("Original : "+ Part[1]);
				//System.out.println("Original: "+ Part[0]);
				if(Part[0].concat("!") != null) {
					Part[0] = Part[0].replace("!", "");
				}
    			
    			//Find same predicate with variables in order to unify
    			for(int p = 0; p < predicates.size(); p++) {
    				System.out.println("Finding the error: "+ predicates.get(p));
    				String[] part2 = parts(predicates.get(p));
    				part2[0] = part2[0].replace(" ", "");
    				part2[1] = part2[1].replace(" ", "");
    				
    				//System.out.println("Finding the error: "+ part2[1]);
    				//System.out.println("Finding the error: "+ part2[0]);
    						
    				if((part2[0].contains(Part[0])) && (part2[1].contains("x") || part2[1].contains("z") || part2[1].contains("(y"))){
    					//case when both are variables
    					String var = parts(predicates.get(p))[1];
    					boolean bool1 = true;
    					boolean bool2 = true;
    					if(var.contains(",")) {
    						String[] Parts = var.split(",");
    						if( Parts[0].contains("x") || Parts[0].contains("(y") || Parts[0].contains("z")) {
    							bool1 = true;
    						}else {
    							bool1 = false;
    						}
    						if(Parts[1].contains("x") || Parts[1].contains("(y") || Parts[1].contains("z")) {
    							bool2 = true;
    						}else {
    							bool2 = false;
    						}
    					}
    					//if predicate has 2 variables, then check if both are variable or just one is
    					if(bool1 && bool2) {
    					System.out.println("Same Predicate with Variables "+predicates.get(p));
    					 
    					 					
    					String[] consts = variables(Part[1]);
    					String[] vars = variables(parts(predicates.get(p))[1]);
 
    					//String newRule = rules[m].replace(firstPart, secondPart)
    					//find rule with the predicate (variable) and replace them in rule
    					int m = findRule(predicates.get(p));
    					
    					//updating the predicates list as well as the rules
    					if(m != Integer.MAX_VALUE) {
    						//System.out.println("Rule with this predicate is "+ rules[m]);
    						newrule = rules[m];
    						//newPredicate = predicates.get(p);
    						for(int k= 0; k < 2; k++) {
    							if(consts[k] != null && vars[k] != null) {
    								System.out.println("Variables: "+ vars[k]);
    								System.out.println("Constants: "+ consts[k]);
    	    						
    								newrule = newrule.replace(vars[k],consts[k]);
    	    						if(vars[k].equals("F(x)")) {
    	    							
    	    							newrule = newrule.replace("G(x)",consts[k]);
    	    							newrule = newrule.replace("x",consts[k]);
    	    						}
    	    						if(vars[k].equals("G(x)")) {
    	    							newrule = newrule.replace("F(x)",consts[k]);
    	    							newrule = newrule.replace("x",consts[k]);
    	    						}
    	    						if(vars[k].equals("x")) {
    	    							newrule = newrule.replace("F("+consts[k]+")",consts[k]);
    	    							newrule = newrule.replace("G("+consts[k]+")",consts[k]);
    	    						}
    	    						
    	    						//newPredicate = predicates.get(p).replace(vars[k],consts[k]);
    	    						
    							}
    						}
    						rules[m] = newrule;
    						//predicates.set(p, newPredicate);
    						isChange = true;
    						updatePredicates();
    						System.out.println("New Rule after unification is "+ newrule);
    					}
    				}
    				}
    			}
    			

    			//second case when predicate has one constant and other variable
    		}
    		else {
    			if(Part[1].contains(",")){
					boolean boolC1 = true;
					boolean boolC2 = true;
					boolean boolV1 = true;
					boolean boolV2 = true;
					
					String[] Parts = Part[1].split(",");
					if( Parts[0].contains("x") || Parts[0].contains("(y") || Parts[0].contains("z")) {
						boolC1 = true;
					}else {
						boolC1 = false;
					}
					if(Parts[1].contains("x") || Parts[1].contains("(y") || Parts[1].contains("z")) {
						boolC2 = true;
					}else {
						boolC2 = false;
					}
					if((boolC1 && !boolC2) || (!boolC1 && boolC2) ) {
						System.out.println("Predicate with one const is: "+predicate);
						
						//now find the same predicate with opp variables and const to unify
						for(int p = 0; p < predicates.size(); p++) {
					
		    				String[] part2 = parts(predicates.get(p));
		    				part2[0] = part2[0].replace(" ", "");
		    				part2[1] = part2[1].replace(" ", "");
		    				
		    				//name is same 		
		    				if((part2[0].contains(Part[0]))){
		    					
		    					System.out.println("Found the predicate with same name: "+ predicates.get(p));
		    					//now check if we have opp variables and const
		    					String[] Part2s = part2[1].split(",");

		    					if( Part2s[0].contains("x") || Part2s[0].contains("(y") || Part2s[0].contains("z")) {
		    						boolV1 = true;
		    					}else {
		    						boolV1 = false;
		    					}
		    					if(Part2s[1].contains("x") || Part2s[1].contains("(y") || Part2s[1].contains("z")) {
		    						boolV2 = true;
		    					}else {
		    						boolV2 = false;
		    					}
		    					//for the second predicate also, there should be just one variable and one const
		    					
		    					if(((boolV1 && !boolV2) || (!boolV1 && boolV2) ) && ((boolV1 && boolC2) || (boolV2 && boolC1)) ) {
		    					
		    					//if opp variables are true update the rule
		    					if( (boolC1 && boolV2) || (boolC2 && boolV1) ) {
		    						
		    						System.out.println("Conditions match for "+ predicates.get(p));
		        					String[] consts = variables(Part[1]);
		        					String[] vars = variables(part2[1]);
		     
		        					//String newRule = rules[m].replace(firstPart, secondPart)
		        					//find rule with the predicate (variable) and replace them in rule
		        					int m = findRule(predicates.get(p));
		        					int n = findRule(predicate);
		        					

		        					if(m != Integer.MAX_VALUE) {
		        						//updating the rules as well as the predicates list
		        						//System.out.println("Rule with this predicate is "+ rules[m]);
		        						newrule = rules[m];
		        						System.out.println("-----------------Finding Error-------------");
		        						System.out.println(vars[0] + " , "+ vars[1] );
		        						System.out.println(consts[0] + " , "+ consts[1]);
		        						if(boolV1) {
		        							newrule = newrule.replace(vars[0],consts[0]);
		        						}
		        						if(boolV2) {
		        							newrule = newrule.replace(vars[1],consts[1]);
		        						}
		        						rules[m] = newrule;
		        						System.out.println("New Rule after unification is "+ newrule);
		        						String newrule2 = rules[n];
		        						
		        						if(boolC1) {
		        							newrule2 = newrule2.replace(consts[0],vars[0]);
		        						}
		        						if(boolC2) {
		        							newrule2 = newrule2.replace(consts[1],vars[1]);
		        						}
		        						rules[n] = newrule2;
		        						System.out.println("New Rule after unification is "+ newrule2);

		        						isChange = true;
		        						updatePredicates();
		        						System.out.println("New Rule after unification is "+ newrule);
		        					}
		        				}
		    						
		    				}	
		    						
		    						
		    						
		    					}
		    				}

							
						}
						
					}
    				
    			}
    		//////////////////else part
    		
    		
    		
    		
    		}
    		
    	return isChange;
    	}
    	
    	
    
    //helper function to get the first and second part of predicate 
    static private String[] parts(String predicate) {
    	//System.out.println("In the Parts function to see error"+ predicate);
		String[] Parts = predicate.split("\\(");
		String[] nameVariable = new String[2];
		//first part name of predicate, second part are the variables or constants
		nameVariable[0] = Parts[0];
		nameVariable[1] = "";
		
		if(Parts.length > 2) {
			for(int p = 1; p < Parts.length; p ++) {
				nameVariable[1] += "("+ Parts[p];
			}

		}else {
			nameVariable[1] ="("+ Parts[1];
		}
		return nameVariable;
    }
    
    //helper function just to get two variables values of a predicate
    static private String[] variables(String st) {
    	
    	System.out.println("Inside the variable function:"+st);
    	String[] var = new String[2];
		String[] Parts = st.split(",");
		
		//First check if both are variables or just one is		
		if(Parts.length <= 1) {
			var[0] = Parts[0].replaceFirst("\\(", "");
			var[0] = var[0].replaceFirst("\\)", "");
			var[0] = var[0].replace(" ", "");
			//System.out.println(var[0]);
		}else {
			var[0] = Parts[0].replaceFirst("\\(", "");
			var[0] = var[0].replace(" ", "");
			var[1] = Parts[1].replaceFirst("\\)", "");
			var[1] = var[1].replace(" ", "");
		}
		return var;
    }
    static private boolean unitResolution() {
    	//once substitute the variables with constants, check if they are being resolved 
    	//anywhere in given sets. 
    	//if we find the contradictory rules update the rules.
    
    	System.out.println("In Unit Resolution");
    	System.out.println("----------------------");
    	printRules();
    	System.out.println("----------------------");
    	//updatePredicates();
    	//printPredicates();
    	String negPredicate;
    	boolean sF = false;
    	boolean uf = false;
    	//base Condition when no more predicate are left for unification or resolution.
    	if(predicates.isEmpty()) {
    		System.out.println("Predicates are empty");
    		sF = true;
    		return true;
    	}
    	//if unification will return false, it means we can't resolve it further
    	if(uf) {
    		return false;
    	}
    	
    	
    	for (Iterator iterator = predicates.iterator(); iterator.hasNext();) {
			
    		String predicate = (String) iterator.next();
    		
    		
    		//first check if negative of the predicate is present

    		if(predicate.contains("!")) {
    			negPredicate = predicate.replace("!", "");
    			negPredicate = negPredicate.replace(" ", "");
    		}
    		else {
    			negPredicate = "!"+predicate;
    			negPredicate = negPredicate.replace(" ", "");
    		}
    		    		
    		System.out.println("----------------------");
    		System.out.println(negPredicate);
    		for(int j = 0; j < predicates.size(); j++) {
    			if (predicates.get(j).replace(" ", "").equals(negPredicate) ) {
    				
    				//System.out.println(predicates.get(i));
    				System.out.println("This predicate has negative of "+predicates.get(j));
    				//if present, update the rule by resolution
        			//find rule with these predicates having variables.
    				int m = findRule(predicate);
    				int n = findRule(predicates.get(j));
    				
    				if(m != Integer.MAX_VALUE) {
    					String newRule = rules[m].replace(predicate, "");
    					//System.out.println(newRule.split("∨").length);
    					if(newRule.split("∨").length <= 1) {
    						newRule = newRule.replace("∨", "");
    					}
    					System.out.println("New rule: "+ newRule);
    					rules[m] = newRule;
    					
    				}
    				if(n != Integer.MAX_VALUE) {
    					String newRule = rules[n].replace(predicates.get(j), "");
    					
    					//System.out.println(newRule.split("∨").length);
    					if(newRule.split("∨").length <= 1) {
    						newRule = newRule.replace("∨", "");
    					}
    					System.out.println("New rule: "+ newRule);
    					rules[n] = newRule;
    				}
    				

    				updatePredicates();
    			}

    			//negative of predicate is not present,
    		}//checked all predicates
    	}//All predicates negatives have been checked with all predicates
    	if(unification()){
    		System.out.println("Unification Called and returned +");
    		if(unitResolution()) {
    			return true;
    		}
    	}else {
    		//if unification returns false, it means we can't do anything further and return false
    		uf = true;
    		return false;
    	}
    	
		return sF;
		
    	
    }
    
    
    static private int findRule(String neg) {
    	
		for(int m = 0; m < rules.length; m++) {
			
			if(rules[m].contains(neg)){
				System.out.println("Rule which has: "+ neg+" is " +rules[m]);

				return m;
			}
		}
		return Integer.MAX_VALUE;
    }
    //asking inference engine if curiosity kills Tuna (which is Cat)
    static private boolean ask(String predicate, String x, String y){
		return false;
    	
    }
    
    //testing the toy problem
    public static void main(String[] args) {
    	//variables(parts("Animal(F(x)) ")[1]);
    	
		updatePredicates();
		//printPredicates();
		if(unitResolution()) {
			System.out.println("Solution Found");
		}
		else {
			System.out.println("Solution NotFound");
		}
		//System.out.println("------------------------------");
		//unification();
	}
}
