package lab1;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;





public class Lab1 extends JFrame {
    public DefaultTableModel tableModel;
    private LinkedList<RecIntegral> tableData; 
    private Socket socket;
    private static ObjectOutputStream out;
    public Lab1() {
        
        setTitle("Integration Calculator - cos(x) ");                             
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Файл");
        JMenu editMenu = new JMenu("Правка");
        JMenu networkMenu = new JMenu("Сеть");

        
        
        
        JMenuItem saveMenuItem = new JMenuItem("Сохранить");
        JMenuItem saveBinaryMenuItem = new JMenuItem("Сохранить в двоичном виде");
        
        JMenuItem loadMenuItem = new JMenuItem("Открыть...");
        JMenuItem loadBinaryMenuItem = new JMenuItem("Открыть в двоичном виде...");

        
        JMenuItem clearMenuItem = new JMenuItem("Очистить");
        JMenuItem connectMenuItem = new JMenuItem("Подключиться");
        JMenuItem disconnectMenuItem = new JMenuItem("Отключиться");



        tableData = new LinkedList<>();
        
        
    
        String[] columns = {"Нижняя граница", "Верхняя граница", "Длина интервала", "Результат"};
        
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0){
            @Override
            public boolean isCellEditable(int row, int column){
                
                return column != 3;
            }
        };
        
        JTable table = new JTable(tableModel){
            @Override
            public TableCellRenderer getCellRenderer(int row, int column){
                if (column == 3){
                    // запрещаем редактирование 4го столбца
                    return getDefaultRenderer(Object.class);
                }
                else{
                    return super.getCellRenderer(row, column);
                }
            }
            
            @Override
            public TableCellEditor getCellEditor(int row, int column){
                if (column == 3){
                    return getDefaultEditor(Object.class);
                }
                else{
                    return super.getCellEditor(row, column);
                }
            }
        };

        TableColumn column = table.getColumnModel().getColumn(3);
        column.setCellEditor(null);
        
        // Создаем текстовые поля для ввода данных
        JTextField lowerBoundField = new JTextField(10);
        JTextField upperBoundField = new JTextField(10);
        JTextField intervalField = new JTextField(10);

        
       
        clearMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0); // Очищаем таблицу
            
            }
        });

        
        connectMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                 String serverAddress = "localhost"; 
                 int serverPort = 4540; 
                    try {
                              socket = new Socket(serverAddress, serverPort);
                              System.out.println("Connected to server.");
                              setTitle("Integration Calculator - cos(x) ONLINE ");                             

                              out = new ObjectOutputStream(socket.getOutputStream());
                              UpdateTableTask UTT = new UpdateTableTask(tableModel, socket) ;
                              UTT.start();            



                          } catch (IOException ex) {
                              ex.printStackTrace();
                          }
            }
        });
        
       disconnectMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                        System.out.println("Disconnectected from server.");
                        setTitle("Integration Calculator - cos(x) ");                             

                    } else {
                        System.out.println("Already disconnected from server.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
       
        saveBinaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(selectedFile))) {
                        oos.writeObject(tableData);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                }
            });
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showSaveDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try (PrintWriter writer = new PrintWriter(new FileOutputStream(selectedFile))) {
                        writer.println(tableData);
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        
        loadMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile))) {
                        tableData = (LinkedList<RecIntegral>) ois.readObject();
                        for (RecIntegral rec : tableData) {
                            tableModel.addRow(new Object[]{rec.getLowerBound(), rec.getUpperBound(), rec.getIntervals(), rec.getResult()});
                        }
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
                    }
        }
                
        }
        });        
   
        
        loadBinaryMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile))) {
                        tableData = (LinkedList<RecIntegral>) ois.readObject();
                    } catch (FileNotFoundException ex) {
                        ex.printStackTrace();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                    }
                }
                
        }
        });         
        JButton calculateButton = new JButton("Вычислить");    
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int lastRowIndex;
                if(table.getRowCount() > 1){        
                    lastRowIndex = table.getRowCount() - 1;
                }else{ lastRowIndex = table.getRowCount();}
                double lowerBound = Double.parseDouble(lowerBoundField.getText());
                double upperBound = Double.parseDouble(upperBoundField.getText());
                double intervals =  Double.parseDouble(intervalField.getText());
                tableModel.addRow(new Object[]{lowerBound, upperBound, intervals, ""});
                
                try {
                    RecIntegral newElement = new RecIntegral(lowerBound, upperBound, intervals, 0.0);
                    tableData.add(newElement);
                } catch (InvalidInputException ex) {
                    JOptionPane.showMessageDialog(null, "Exception occurred while constructing a new Class instance.\n" + ex.toString());
                }                
                
                 // Создание и запуск потоков
                IntegrationTask thread1 = new IntegrationTask(lowerBound, upperBound / 3, intervals , lastRowIndex, tableModel, tableData, 0.0, 0);
                thread1.start();
                try {               
                    thread1.join();            
                } catch (InterruptedException d) {
                    d.printStackTrace();
                }
                IntegrationTask thread2 = new IntegrationTask(upperBound / 3, 2 * upperBound / 3, intervals, lastRowIndex, tableModel, tableData,  thread1.getFinalResult(), thread1.getStep());
                thread2.start();
                try {                              
                    thread2.join();            
                } catch (InterruptedException d) {
                    d.printStackTrace();
                }
                IntegrationTask thread3 = new IntegrationTask(2 * upperBound / 3, upperBound, intervals, lastRowIndex, tableModel, tableData, thread2.getFinalResult(), thread2.getStep());
                thread3.start();
                try {                  
                    thread3.join();            
                } catch (InterruptedException d) {
                    d.printStackTrace();
                }
                
                try {
                    if(thread3.getStep() == 3){
                        LinkedList<RecIntegral> newData = new LinkedList<>();
                        newData.add(new RecIntegral(lowerBound, upperBound, intervals, thread3.getFinalResult()));
                        out.writeObject(newData);
                        out.flush();}
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    } catch (InvalidInputException ex) {              
                    Logger.getLogger(Lab1.class.getName()).log(Level.SEVERE, null, ex);
                }              
                
                

                       
               
            }
        });
        
        JButton deleteButton = new JButton("-");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = table.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
            
                }
            }
        });
        
       
       
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(networkMenu);        
        setJMenuBar(menuBar);
        
        fileMenu.add(loadMenuItem);
        fileMenu.add(loadBinaryMenuItem);
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveBinaryMenuItem);
        editMenu.add(clearMenuItem);
        networkMenu.add(connectMenuItem);
        networkMenu.add(disconnectMenuItem);

        
        
        JPanel panel = new JPanel();
        panel.add(lowerBoundField);
        panel.add(upperBoundField);
        panel.add(intervalField);
        panel.add(deleteButton);
        panel.add(calculateButton);
        add(panel, "South");
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane);

        setSize(700, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
       
    }

 

 public static void main(String[] args) {
       
        Lab1 FormL = new Lab1();

      
    }
}