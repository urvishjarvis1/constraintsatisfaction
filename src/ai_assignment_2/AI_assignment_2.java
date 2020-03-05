/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai_assignment_2;

import java.util.ArrayList;
import java.util.Arrays;
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
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AI_assignment_2 ai_assignment;
        ai_assignment=new AI_assignment_2();
        Scanner scanner=new Scanner(System.in);
        
        System.out.println("Enter number of variable (under 1000)");
        ai_assignment.numberOfVar=scanner.nextInt();
        while (ai_assignment.numberOfVar>100){
            System.out.println("Pleas enter number of variable (under 1000)");
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
            System.out.println("Please enter constraint tightness (0<p<1)");
            ai_assignment.alpha=scanner.nextFloat();
        }
        
        System.out.println("Enter constant r (0<r<1)");
        ai_assignment.r=scanner.nextFloat();
        while(ai_assignment.r>=1f||ai_assignment.r<=0f){
            System.out.println("Please enter constraint tightness (0<p<1)");
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
        ai_assignment.constaintVariable=new ArrayList<Constraints>();
        //constraint generation
        System.out.println("\nConstraints");
        ai_assignment.generateConstraints();
        
        //printing constraints
        ai_assignment.printConstrains();
        
        
    }

    private void generateConstraints() {
        Random randomGen=new Random();
        int i=0,j=0;
        while (i < noOfConstraint) {
            int a = randomGen.nextInt(numberOfVar);
            int b = randomGen.nextInt(numberOfVar);
            //System.out.println("var a and b" + a + " " + b);
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
                        if (c != d) {
                            dup = checkforDuplicationForVal(c, d,constraints);
                            //System.out.println("duplication for val:" + dup);
                            if(!dup){
                                constraints.values.add(new Values(c, d));
                                j++;
                               //System.out.println(" j:" + j);
                            }
                        }
                    }
                    constaintVariable.add(constraints);
                    i++;
                   // System.out.println("i:" + i);
                }
            }

        }
    }

    private void printConstrains() {
        for (Constraints c : constaintVariable){
            System.out.print("\n("+c.var1+","+c.var2+") :");
            for(Values v: c.values){
                System.out.print(v.val1+","+v.val2+"\t");
            }
        }
    }

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
    
    class Constraints{
        int var1,var2;
        ArrayList<Values> values=new ArrayList<Values>();

        private Constraints(int a, int b) {
            this.var1=a;
            this.var2=b;
        }
        
    }
    
    class Values{
        int val1,val2;

        private Values(int a, int b) {
            this.val1=a;
            this.val2=b;
        }
    }
}
