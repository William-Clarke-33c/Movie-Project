/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package finalproject;

public class Matrix implements Comparable<Matrix> {
    public int id;
    public double distance;
 

    @Override
    public int compareTo(Matrix m) {
        return Double.compare(distance, m.distance);
    }
    
}
