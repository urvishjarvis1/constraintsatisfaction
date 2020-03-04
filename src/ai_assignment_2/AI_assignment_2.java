/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai_assignment_2;

import java.util.Scanner;

/**
 *
 * @author urvis
 */
public class AI_assignment_2 {
    int numberOfVar,domainSize,noOfConstraint,incompitableTable;
    float p,alpha,r;
    /**
     * @param args the command line arguments
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
    }
    
}
