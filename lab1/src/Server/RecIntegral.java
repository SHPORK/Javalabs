/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package Server;

import java.io.Serializable;
import lab1.InvalidInputException;

  public class RecIntegral implements Serializable {
        private double lowerBound;
        private double upperBound;
        private double intervals;
        private double result;
        public RecIntegral(double lowerBound, double upperBound, double intervals, double result) throws InvalidInputException{
            
            if (lowerBound < 0.000001 ) {throw new InvalidInputException("Entered values are out of bounds: lowerBound < 0.000001");} 
            if (lowerBound > 1000000 ) {throw new InvalidInputException("Entered values are out of bounds: lowerBound > 1000000");} 
            if (upperBound < 0.000001 ) {throw new InvalidInputException("Entered values are out of bounds: upperBound < 0.000001");} 
            if (upperBound > 1000000 ) {throw new InvalidInputException("Entered values are out of bounds: upperBound > 1000000");} 
            if (intervals < 0.000001 ) {throw new InvalidInputException("Entered values are out of bounds: intervals < 0.000001");} 
            if (intervals > 1000000 ) {throw new InvalidInputException("Entered values are out of bounds: interval > 1000000");} 
            if (lowerBound >= upperBound){throw new InvalidInputException("LowerBound is greater than upperBound");} 
            this.lowerBound = lowerBound;
            this.upperBound = upperBound;
            this.intervals = intervals;
            this.result = result;
        }

        public double getLowerBound() {
            return lowerBound;
        }
        public double getResult() {
            return result;
        }

        public double getUpperBound() {
            return upperBound;
        }

        public double getIntervals() {
            return intervals;
        }

    }