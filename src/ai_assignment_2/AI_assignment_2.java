/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai_assignment_2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author urvis
 */
public class AI_assignment_2 {
    int numberOfVar,domainSize,noOfConstraint,incompitableTable;
    float p,alpha,r;
    ArrayList<Integer> variables=new ArrayList <Integer>();
    ArrayList<Integer> domains=new ArrayList <Integer>();
    ArrayList<Constraints> constaintVariable;
    Domains varDomains=new Domains();
    long startTime=0,endTime=0;
    
    /**
     * Main class for initiating all the variables to run the code.
     * 
     */
    public static void main(String[] args) {
        AI_assignment_2 ai_assignment;
        ai_assignment=new AI_assignment_2();
        Scanner scanner=new Scanner(System.in);
        
        System.out.println("Enter number of variable (under 1000)");
        ai_assignment.numberOfVar=scanner.nextInt();
        while (ai_assignment.numberOfVar>1000){
            System.out.println("Pleas enter number of variable (under 1000)");
            ai_assignment.numberOfVar=scanner.nextInt();
        }
        
        System.out.println("Enter constraint tighness (0<p<1)");
        ai_assignment.p=scanner.nextFloat();
        while(ai_assignment.p>=1f||ai_assignment.p<=0f){
            System.out.println("Please enter constraint tightness (0<p<1)");
            ai_assignment.p=scanner.nextFloat();
        }
        
        System.out.println("Enter constant alpha (0<alpha<1)");
        ai_assignment.alpha=scanner.nextFloat();
        while(ai_assignment.alpha>=1f||ai_assignment.alpha<=0f){
            System.out.println("Please enter constant alpha (0<alpha<1)");
            ai_assignment.alpha=scanner.nextFloat();
        }
        
        System.out.println("Enter constant r (0<r<1)");
        ai_assignment.r=scanner.nextFloat();
        while(ai_assignment.r>=1f||ai_assignment.r<=0f){
            System.out.println("Please enter constant r(0<r<1)");
            ai_assignment.r=scanner.nextFloat();
        }
        
        //couting domain size from alpha and number of variables
        ai_assignment.domainSize=
                (int) Math.pow(ai_assignment.numberOfVar,ai_assignment.alpha); /* N^alpha */ 
        System.out.println("Domain Size:"+ai_assignment.domainSize);
        
        
        //calculationg nubmer of constaints based on constant r and number of variable.
        ai_assignment.noOfConstraint=  
                (int) Math.ceil(ai_assignment.r * ai_assignment.numberOfVar * Math.log(ai_assignment.numberOfVar)); /* rn ln n*/
        System.out.println("Number of Constraint:"+ai_assignment.noOfConstraint);
        
        //calculating number of imcompitable tuples based on constraint tightness p and domain size of variable 
        ai_assignment.incompitableTable=(int) Math.ceil(ai_assignment.p* ai_assignment.domainSize*ai_assignment.domainSize); /* pd^2 */
        System.out.println("Number of IncompitableTable size:"+ai_assignment.incompitableTable);
        
        //generation of variable
        System.out.print("Variables:");
        for(int i=0;i<ai_assignment.numberOfVar;i++){
            ai_assignment.variables.add(i);
            System.out.print(" X"+ai_assignment.variables.get(i)+",");
        }
        
        //generation of domain of variables
        System.out.print("\nDomains:");
        for(int i=0;i<ai_assignment.domainSize;i++){
            ai_assignment.domains.add(i);
            System.out.print(" "+ai_assignment.domains.get(i)+",");
        }
        
        //assigning domains to each variables
        for(int i=0;i<ai_assignment.variables.size();i++){
             ai_assignment.varDomains.values.add(new ArrayList<Integer>(ai_assignment.domains));
        }
        
        //constraint generation
        ai_assignment.constaintVariable=new ArrayList<Constraints>();
        ai_assignment.generateConstraints();
        
        //printing constraints
        System.out.println("\nConstraints:");
        ai_assignment.printConstrains();
        
        
        System.out.println("\nAre you want to run Arc Constitency before the sreach?y/n");
        char choice=scanner.next().charAt(0);
        if(choice=='y'){
            //performing arc consistency 
            boolean consistant = ai_assignment.performAC3(ai_assignment.constaintVariable,ai_assignment.varDomains);
            System.out.println("\nProblem is consistent "+consistant+" Using Arc consistency");

            for(int i=0;i<ai_assignment.varDomains.values.size();i++){
                System.out.print("\nvar X"+i+": {");
                for(int j=0;j<ai_assignment.varDomains.values.get(i).size();j++){
                    System.out.print(ai_assignment.varDomains.values.get(i).get(j)+",");
                }
                System.out.print("}");
            }
        }
        
        System.out.println("\nPlease select on the method to find a solution from the problem");
        System.out.println("1. BackTracking ");
        System.out.println("2. Forward Checking ");
        System.out.println("3. Full Look Ahead ");
        int choiceOfAlgo=scanner.nextInt();
        //starting timer
        ai_assignment.startTime=System.currentTimeMillis();
       switch(choiceOfAlgo){
           case 1:
               ai_assignment.doBacktracking();
               break;
           case 2:
               ai_assignment.doForwardChecking();
               break;
           case 3:
               ai_assignment.doFullLookAhead();
               break;
           case 123:
               ai_assignment.doBacktracking();
               ai_assignment.doForwardChecking();
               ai_assignment.doFullLookAhead();
           default:
               break;
       }
        
    }
    /**
     * Arc Consistency
     * @return false->domain is empty for one variable
     *          true-> arc-consistent.
     * 
     * followed https://en.wikipedia.org/wiki/AC-3_algorithm site to get understanding of the algorithm
     */
    private boolean performAC3(ArrayList<Constraints> arcs,Domains tempVarDomain) {
        int i=0;
        while(i<arcs.size()){
            Constraints constraint=arcs.get(i);
            //System.out.println("constaint("+constraint.var1+","+constraint.var2+")");
            if(doArcReduce(constraint, tempVarDomain)){
                if(tempVarDomain.values.get(constraint.var1).isEmpty())
                     return false;
            }
            i++;
        }
        
        return true;
        
    }
    
    /**
     * to reduce the domain of the variable.
     * @param constraint = for which we are performing arc consistency
     * @return true-> if the removal of the value from the domain;
     *         false-> if there is no removal
     */
    private boolean doArcReduce(Constraints constraint, Domains tempVarDomain) {
        int count=0;
        for(Integer i:tempVarDomain.values.get(constraint.var1)){
           for(int j=0;j<constraint.values.size();j++){
               //checking for value of every possible of x in Dx we get y from Dy.
               if(i==constraint.values.get(j).val1)
                   count++;
           }
           //count is same means that all the possible of value of x is in incompitable tuples i.e. (X,Y): (0,0) (0,1) where domain of x is (0,1)
           if(count==tempVarDomain.values.get(constraint.var1).size()){
               //removing from the domain of X since we don't having any value from y.
               tempVarDomain.values.get(constraint.var1).remove(i);
               return true;
           }
           count=0;
           
        }
        return false;
    }
    
    /**
     * Backtracking algorithm for searching a solution which satisfy all the constraints
     * 
     * First we will start with assigning value to the first variable then checking if the constraints are violated or not
     * if constraint violated backtrack.
     * do same for N variable.
     */
    private void doBacktracking() {
        HashMap<Integer,Integer> resultSet=new HashMap<Integer,Integer>();
        boolean constaintViolated=false;
        for(int variables1:variables){
            for (int value1 = 0; value1 < varDomains.values.get(variables1).size(); value1++) {
                for (int variable = variables1; variable < varDomains.values.size(); variable++) {
                    for (int value = value1; value < varDomains.values.get(variables1).size(); value++) {
                        constaintViolated = constraintsViolated(variable, value, resultSet);
                        if (!constaintViolated) {
                            resultSet.put(variable, varDomains.values.get(variable).get(value));
                            break;
                        }
                    }
                }
            }
        }
        endTime=System.currentTimeMillis();
        if(resultSet.size()==numberOfVar){
            System.out.println("Successfully found Solutions. Problem is Consistent");
        }else{
            System.out.println("Not able to find whole solution. Problem is not consistent");
        }
        System.out.print("Solution: {");
        for(Integer variables:resultSet.keySet()){
            System.out.print(" X"+variables+": "+resultSet.get(variables)+",");
        }
        System.out.println("}");
        long timeElapsed = endTime-startTime;
        System.out.println("Time taken to execute:"+timeElapsed+" milliseconds");
    }
    
    //Cheking for constraint violation for assigned variable
    private boolean constraintsViolated(int variable1, Integer value,HashMap<Integer,Integer> solutionSet) {
        for(Integer variable2:solutionSet.keySet()){
            for(Constraints constraint:constaintVariable){
                //System.out.println("var 1:"+variable1+" var 2:"+variable2+" val1:"+value+" val2:"+solutionSet.get(variable2));
                if((constraint.var1==variable1&&constraint.var2==variable2)||(constraint.var1==variable2&&constraint.var2==variable1)){
                    for (Values values : constraint.values) {
                        if((values.val1==value&&values.val2==solutionSet.get(variable2))||(values.val2==value&&values.val1==solutionSet.get(variable2))){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
    /**
     * Forward Checking:
     * Firstly start by assigning the value to variable then for next variable we are performing reduced arc consistency.
     * In reduced arc consistency we are removing value form the domain of the next variable which is not compatible with currently assigned variable's value.
     * if we found the domain of variable became empty we are backtracking to the upper layer.
     */
    private void doForwardChecking() {
        HashMap<Integer,Integer> resultMap=new HashMap<Integer,Integer>();
        Domains tempVarDomain=varDomains;
        boolean constaintViolated;
        for(int variables1:variables){
            for (int value1 = 0; value1 < varDomains.values.get(variables1).size(); value1++) {
                for (int variable = variables1+1; variable < varDomains.values.size(); variable++) {   
                    //System.out.println("variable " + variable + " value " + value1);
                    constaintViolated = constraintFound(variable, tempVarDomain, value1);
                    //System.out.println("consistent:"+constaintViolated);
                    if(!constaintViolated){
                        resultMap.put(variables1, value1);
                        break;
                    }           
                }
            }
        }
        endTime=System.currentTimeMillis();
        if(resultMap.size()==numberOfVar){
            System.out.println("Successfully found Solutions.Problem is consistent");
        }else{
            System.out.println("Not able to find whole solution.Problem is not consistent");
        }
        System.out.print("Solution: {");
        for(Integer variables:resultMap.keySet()){
            System.out.print(" X"+variables+": "+resultMap.get(variables)+",");
        }
        System.out.println("}");
        long timeElapsed = endTime-startTime;
        System.out.println("Time taken to execute:"+timeElapsed+" milliseconds");
    }
    
    //reduced arc consitency perfomed here.
    private boolean constraintFound(Integer variable, Domains tempVarDomain, Integer value){
        for (Constraints constraints : constaintVariable) {
            if ((constraints.var1 == variable && constraints.var2 == variable + Integer.valueOf(1)) || (constraints.var2 == variable && constraints.var1 == variable + Integer.valueOf(1))) {
                ArrayList<Constraints> tempConstraintses=new ArrayList<Constraints>();
                tempConstraintses.add(constraints);
                if (performAC3(tempConstraintses,tempVarDomain)) {
                    return true;
                }else{
                    return false;
                } 
            }
        }
        return false;
    }
    
    
    /**
     * Full look ahead
     * 
     * Firstly we are assigning the value to the variable then we are applying arc consistency for next all the variables.
     * during if we find a in consistency we backtrack and try new solution.
     * 
     */
    private void doFullLookAhead() {
        HashMap<Integer,Integer> resultMap=new HashMap<Integer,Integer>();
        Domains tempVarDomain=varDomains;
        ArrayList<Constraints> constraintsList=new ArrayList<Constraints>();
        boolean consistant=true;
        
        for(int variables1:variables){
            for (int value1 = 0; value1 < varDomains.values.get(variables1).size(); value1++) {
                        resultMap.put(variables1, value1);
                        //updating the domain of variable temporariy 
                        tempVarDomain.values.get(variables1).clear();
                        tempVarDomain.values.get(variables1).add(value1);
                for (int variable = variables1+1; variable < varDomains.values.size(); variable++) {   
                    constraintsList=findConstraintVariable(variables1+1);
                    consistant=performAC3(constraintsList, tempVarDomain);
                    if(consistant){
                        
                    }else{
                        resultMap.remove(variables1);
                        tempVarDomain.values.get(variables1).addAll(domains);
                        break;
                        
                    }         
                }
            }
        }
        endTime=System.currentTimeMillis();
        if(resultMap.size()==numberOfVar){
            System.out.println("Successfully found Solutions. Problem is consistent");
        }else{
            System.out.println("Not able to find whole solution. Problem is not consistent");
        }
        
        System.out.print("Solution: {");
        for(Integer variables:resultMap.keySet()){
            System.out.print(" X"+variables+": "+resultMap.get(variables)+",");
        }
        System.out.println("}");
        long timeElapsed = endTime-startTime;
        System.out.println("Time taken to execute:"+timeElapsed+" milliseconds");
    }
    /**
     * to perform full arc-consistency 
     * @param variable = the variable which has value assigned.
     * @return true->consistent
     *         false-> not consistent
     */
    private ArrayList<Constraints> findConstraintVariable(int variable) {
        ArrayList<Constraints> tempConstraintses=new ArrayList<Constraints>();
        for(int var=variable;var<numberOfVar;var++){
        for (Constraints constraints : constaintVariable) {
            //System.out.println("var:"+var+" constaint var1:" + constraints.var1 + " var 2:" + constraints.var2);
            if ((constraints.var1 == var)) {
                //System.out.println("Constraint present: for "+constraints.var1+" and "+constraints.var2);
                tempConstraintses.add(constraints);
            }else if(constraints.var2 == var){
                //System.out.println("Constraint present: for "+constraints.var1+" and "+constraints.var2);
                tempConstraintses.add(constraints);
            }
        }
        
        }
        return tempConstraintses;
    }
    
    /**'
     * for generation of constraint variable, generating two random constraints, a and b.
     * checking duplicity and based on that generating incompatible tuples from the domain.
     */
    private void generateConstraints() {
        Random randomGen=new Random();
        int i=0,j;
        while (i < noOfConstraint) {
            int a = randomGen.nextInt(numberOfVar);
            int b = randomGen.nextInt(numberOfVar);
            //checking if the same variable is not generated
            if (a != b) {
                boolean dup = checkforDuplicationForVariable(a, b);
                j=0;
                if (!dup) {
                    Constraints constraints=new Constraints(a,b);
                    while (j < incompitableTable) {
                        int c = randomGen.nextInt(domainSize);
                        int d = randomGen.nextInt(domainSize);
                        dup = checkforDuplicationForVal(c, d, constraints);
                        if (!dup) {
                            constraints.values.add(new Values(c, d));
                            j++;
                        } 
                    }
                    constaintVariable.add(constraints);
                    i++;
                }
            }

        }
    }
    
    
    /**
     * printing constraints of the problem 
     */
    private void printConstrains() {
        for (Constraints c : constaintVariable){
            System.out.print("\n("+c.var1+","+c.var2+") :");
            for(Values v: c.values){
                System.out.print(v.val1+","+v.val2+"\t");
            }
        }
    }
    
    
    /**
     * Function for checking duplicity in side the stored value such that same
     * constraint variable will not appear for multiple times.
     * @param a = randomly generated variable
     * @param b = randomly generated variable
     * @return -> true -> there's duplicity
     *            false-> there's no duplicity
     */
    private boolean checkforDuplicationForVariable(int a, int b) {
        //System.out.println("checking a and b"+a+ " "+b);
        //System.out.println("length"+constaintVariable.size());
        for(Constraints c:constaintVariable){
            //System.out.println("checking pair:"+c.var1+" "+c.var2);
            if(c.var1==a && c.var2==b){
                //System.out.println("true karu 6u");
                return true;
            }else if(c.var1==b&&c.var2==a){
                //System.out.println("2ja ma true karu 6u");
                return true;
            }
               
        }
        return false;
    }
    
    
    /**
     * Function for checking duplicity in side the stored value such that same
     * incompatible tuple will not appear for multiple times.
     * @param a = randomly generated value
     * @param b = randomly generated value
     * @param constraints = constraint object for which we are producing incompatible tuple
     * @return -> true -> there's duplicity
     *            false-> there's no duplicity
     */
    private boolean checkforDuplicationForVal(int a, int b, Constraints constraints) {
        //System.out.println("checking a and b"+a+ " "+b);
        //System.out.println("length"+constaintVariable.size());
        for(Values c:constraints.values){
            //System.out.println("checking pair:"+c.val1+" "+c.val2);
            if(c.val1==a && c.val2==b){
                //System.out.println("val true karu 6u");
                return true;
            }else if(c.val1==b&&c.val2==a){
                //System.out.println("val 2ja ma true karu 6u");
                return true;
            }
               
        }
        return false;
    }

    

    

    /**
     * Class for storing constraints i.e. incompatible 
     */
    class Constraints{
        int var1,var2;
        ArrayList<Values> values=new ArrayList<Values>();

        private Constraints(int a, int b) {
            this.var1=a;
            this.var2=b;
        }
        
    }
    /**
     * Class for storing incompatible tuples (constraints) value
     */
    class Values{
        int val1,val2;

        private Values(int a, int b) {
            this.val1=a;
            this.val2=b;
        }
    }
    /**
     * Class for storing values of variables
     */
    class Domains{
         ArrayList<ArrayList<Integer>> values=new ArrayList<ArrayList<Integer>>(variables.size());
    }
    
}
