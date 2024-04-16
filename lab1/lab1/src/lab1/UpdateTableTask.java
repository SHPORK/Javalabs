package lab1;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.LinkedList;
import javax.swing.table.DefaultTableModel;

public class UpdateTableTask extends Thread {
    private DefaultTableModel tableModel;
    private DatagramSocket socket;

    public UpdateTableTask(DefaultTableModel tableModel, DatagramSocket socket) {
        this.tableModel = tableModel;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            while (true) {

                byte[] receiveBuffer = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                socket.receive(receivePacket);

                ByteArrayInputStream byteIn = new ByteArrayInputStream(receivePacket.getData());
                ObjectInputStream objectIn = new ObjectInputStream(byteIn);
                Object obj = objectIn.readObject();

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

                    // Добавляем запись только если хотя бы одно из значений не равно нулю
                    if (!exists && newData.getLast().getLowerBound()!= 0.0 &&
                        newData.getLast().getUpperBound()!= 0.0 &&
                        newData.getLast().getIntervals()!= 0.0 &&
                        newData.getLast().getResult() != 0.0) {
                        tableModel.addRow(new Object[]{newData.getLast().getLowerBound(), newData.getLast().getUpperBound(), newData.getLast().getIntervals(), newData.getLast().getResult()});
                    }
                }
            }
        } catch (SocketException se) {
            System.out.println("Disconnected from the server.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        }
    }
}
