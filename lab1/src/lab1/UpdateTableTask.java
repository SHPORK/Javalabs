package lab1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.LinkedList;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class UpdateTableTask extends Thread {
    private DefaultTableModel tableModel;
    private Socket socket;

    public UpdateTableTask(DefaultTableModel tableModel, Socket socket) {
        this.tableModel = tableModel;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            while (true) {
                Object obj = in.readObject();
                if (obj instanceof LinkedList<?>) {
                LinkedList<RecIntegral> newData = (LinkedList<RecIntegral>) obj;
                
                boolean exists = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    Object lowerBound = tableModel.getValueAt(i, 0);
                    Object upperBound = tableModel.getValueAt(i, 1);
                    Object intervals = tableModel.getValueAt(i, 2);
                    Object result = tableModel.getValueAt(i, 3);
                    
                    if (lowerBound.equals(newData.getLast().getLowerBound()) &&
                        upperBound.equals(newData.getLast().getUpperBound()) &&
                        intervals.equals(newData.getLast().getIntervals()) &&
                        result.equals(newData.getLast().getResult())) {
                        
                        exists = true;
                        break;
                    }
                }
                
                if (!exists) {
                    tableModel.addRow(new Object[]{newData.getLast().getLowerBound(), newData.getLast().getUpperBound(), newData.getLast().getIntervals(), newData.getLast().getResult()});
                }                    
                }
            }
        } catch (IOException | ClassNotFoundException e) {
          
            if (e instanceof SocketException && e.getMessage().equals("Socket closed")) {
                System.out.println("Socket closed. Exiting thread.");
            } else {
                e.printStackTrace();
            }
        }
    }


}
