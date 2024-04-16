package lab1;

import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class IntegrationTask extends Thread {
    private double lowerBound;
    private double upperBound;
    private double intervals;
    private int lastRowIndex;
    private double result;
    private DefaultTableModel tableModel;
    private LinkedList<RecIntegral> tableData;
    private int step = 0;

    public IntegrationTask(double lowerBound, double upperBound, double intervals, int lastRowIndex, DefaultTableModel tableModel, LinkedList<RecIntegral> tableData, double result, int step) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.intervals = intervals;
        this.lastRowIndex = lastRowIndex;
        this.tableModel = tableModel;
        this.tableData = tableData;
        this.result = result;
        this.step = step;
    }

    @Override
    public void run() {
        double sum = 0.0;
        double x = lowerBound;
        while (x < upperBound) {
            double fx1 = Math.cos(x); 
            double fx2 = Math.cos(Math.min(x + intervals, upperBound)); 
            sum += (fx1 + fx2) * Math.min(intervals, upperBound - x) / 2;
            x += intervals;
        }
        int decimalPlaces = 4; 
        sum = Math.round(sum * Math.pow(10, decimalPlaces)) / Math.pow(10, decimalPlaces);
        result =  result + sum;
        tableModel.setValueAt(result, lastRowIndex, 3);
        step = step + 1;

       
    }
    public double getFinalResult() {
        return result;
    }
     public int getStep() {
        return step;
    }
}
