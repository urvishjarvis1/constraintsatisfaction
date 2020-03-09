/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai_assignment_2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AI_assignment_2 ai_assignment;
        ai_assignment=new AI_assignment_2();
        Scanner scanner=new Scanner(System.in);
        
        System.out.println("Enter number of variable (under 100)");
        ai_assignment.numberOfVar=scanner.nextInt();
        while (ai_assignment.numberOfVar>100){
            System.out.println("Pleas enter number of variable (under 100)");
            ai_assignment.numberOfVar=scanner.nextInt();
        }
        
        System.out.println("Enter constraint tighness (0<p<1)");
        ai_assignment.p=scanner.nextFloat();
        while(ai_assignment.p>=1f||ai_assignment.p<=0f){
            System.out.println("Please enter constraint tightness (0<p<1)");
            ai_assignment.p=scanner.nextFloat();
        }
        
        System.out.println("Enter constant α (0<α<1)");
        ai_assignment.alpha=scanner.nextFloat();
        while(ai_assignment.alpha>=1f||ai_assignment.alpha<=0f){
            System.out.println("Please enter constant α (0<α<1)");
            ai_assignment.alpha=scanner.nextFloat();
        }
        
        System.out.println("Enter constant r (0<r<1)");
        ai_assignment.r=scanner.nextFloat();
        while(ai_assignment.r>=1f||ai_assignment.r<=0f){
            System.out.println("Please enter constant r(0<r<1)");
            ai_assignment.r=scanner.nextFloat();
        }
        
        System.out.println("inputed values numofvar:"+ai_assignment.numberOfVar+" p"+ai_assignment.p+" alpha"+ai_assignment.alpha+" r"+ai_assignment.r);
        
        ai_assignment.domainSize=(int) Math.pow(ai_assignment.numberOfVar,ai_assignment.alpha);
        System.out.println("Domain Size:"+ai_assignment.domainSize);
        ai_assignment.noOfConstraint=  (int) Math.ceil(ai_assignment.r * ai_assignment.numberOfVar * Math.log(ai_assignment.numberOfVar));
        System.out.println("Number of Constraint:"+ai_assignment.noOfConstraint);
        ai_assignment.incompitableTable=(int) Math.ceil(ai_assignment.p* ai_assignment.domainSize*ai_assignment.domainSize);
        System.out.println("Number of IncompitableTable size:"+ai_assignment.incompitableTable);
        
        //generation of varable
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
            System.out.println("here");
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
            boolean consistant = ai_assignment.performAC3();
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
    private boolean performAC3() {
        List<Constraints> arcs=new ArrayList<Constraints>();
                
        arcs=constaintVariable;
        int i=0;
        while(i<arcs.size()){
            Constraints constraint=arcs.get(i);
            System.out.println("constaint("+constraint.var1+","+constraint.var2+")");
            if(doArcReduce(constraint)){
                if(varDomains.values.get(constraint.var1).isEmpty())
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
    private boolean doArcReduce(Constraints constraint) {
        int count=0;
        for(Integer i:varDomains.values.get(constraint.var1)){
           //do some great stuff here.
           System.out.println("i: "+i);
           for(int j=0;j<constraint.values.size();j++){
               System.out.println("j: "+j+"value: "+constraint.values.get(j).val1);
               if(i==constraint.values.get(j).val1)
                   count++;
               System.out.println("count"+count);
           }
           if(count==domainSize){
               //System.out.println("count same remvoing:"+varDomains.values.get(constraint.var1).get(i)+" form "+varDomains.values.get(constraint.var1)+" "+constraint.var1);
               varDomains.values.get(constraint.var1).remove(i);
               return true;
           }
           count=0;
           
        }
        return false;
    }
    
    
    private void doBacktracking() {
        HashMap<Integer,Integer> resultSet=new HashMap<Integer,Integer>();
        boolean constaintViolated=false;
        //System.out.println("size of vardomain"+varDomains.values.size());
        for(int variables1:variables){
            System.out.println(" var 1:"+variables1);
            for (int value1 = 0; value1 < varDomains.values.get(variables1).size(); value1++) {
                for (int variable = variables1; variable < varDomains.values.size(); variable++) {
                    for (int value = value1; value < varDomains.values.get(variables1).size(); value++) {
                        //System.out.println("variable " + variable + " value " + value);
                        constaintViolated = constraintsViolated(variable, value, resultSet);
                        //System.out.println("Constrained:" + constaintViolated);
                        if (!constaintViolated) {
                            resultSet.put(variable, varDomains.values.get(variable).get(value));
                            //System.out.println("Variable X"+variable+":"+varDomains.values.get(variable).get(value));
                            break;
                        }
                    }
                }
            }
        }
        if(resultSet.size()==numberOfVar){
            System.out.println("Successfully found Solutions.");
        }else{
            System.out.println("Not able to find whole solution.");
        }
        System.out.println("Solution:");
        for(Integer variables:resultSet.keySet()){
            System.out.println("variable X"+variables+": "+resultSet.get(variables));
        }
    }
    
    
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

    private void doForwardChecking() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    private void doFullLookAhead() {
        throw new UnsupportedOperationException("Not supported yet."); 
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
            //System.out.println("var a and b" + a + " " + b);
            //checking if the same variable is not generated
            if (a != b) {
                boolean dup = checkforDuplicationForVariable(a, b);
               // System.out.println("duplication:" + dup);
                j=0;
                if (!dup) {
                    Constraints constraints=new Constraints(a,b);
                    while (j < incompitableTable) {
                        int c = randomGen.nextInt(domainSize);
                        int d = randomGen.nextInt(domainSize);
                        //System.out.println("values a and b" + a + " " + b);
                        dup = checkforDuplicationForVal(c, d, constraints);
                        //System.out.println("duplication for val:" + dup);
                        if (!dup) {
                            constraints.values.add(new Values(c, d));
                            j++;
                            //System.out.println(" j:" + j);
                        } 
                    }
                    constaintVariable.add(constraints);
                    i++;
                   // System.out.println("i:" + i);
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
