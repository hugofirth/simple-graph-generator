package org.edbt.summerschool.simple_graph_generator.generator.deg;

import com.sun.javafx.geom.transform.BaseTransform;

import java.util.ArrayList;
import java.util.Random;

/**
 */
public class DegreeDeficitVector {
    private ArrayList<Integer> degreeDeficit = new ArrayList<>();
    private Integer totalDegreeDeficit = 0;

    public int addDegree(Integer d) {
        totalDegreeDeficit += d;
        degreeDeficit.add(d);
        return degreeDeficit.size()-1;
    }

    public int getDegree(Integer position) {
        return degreeDeficit.get(position);
    }

    public int update(Integer position, Integer d) {
        totalDegreeDeficit += d - degreeDeficit.get(position);
        degreeDeficit.set(position, d);
        return position;
    }

    public void decrease(Integer position) {
        degreeDeficit.set(position, degreeDeficit.get(position) - 1);
    }

    public Integer totalDegreeDeficit() {
        return totalDegreeDeficit;
    }

    public Integer randomNodePosition() {
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(totalDegreeDeficit()); // TODO: check if range is as desired?
        System.out.print("Randomint:" + Integer.toString(randomInt));
        int prefixSum = 0;
        int position = 0;
        Integer returnPosition = null;
        for (Integer degree : degreeDeficit) {
            if (prefixSum < randomInt && randomInt <= prefixSum + degree) // TODO: check if this is a desired
                returnPosition = new Integer(position);
            prefixSum += degree;
            position ++;
        }
        if (returnPosition != null) {
            System.out.print(" | returnPosition: " + Integer.toString(returnPosition));
            System.out.println(" | degree of position: " + Integer.toString(degreeDeficit.get(returnPosition)));
        }

        return returnPosition;
    }

    public DegreeDeficitVector clone() {
        DegreeDeficitVector clone = new DegreeDeficitVector();
        clone.degreeDeficit = new ArrayList<Integer>(this.degreeDeficit);
        clone.totalDegreeDeficit = this.totalDegreeDeficit;
        return clone;
    }

    public void testPrint() {
        System.out.print("deficit: ");
        for (Integer degree : degreeDeficit) {
            System.out.print(Integer.toString(degree) + " ");
        }
        System.out.println();
    }
}