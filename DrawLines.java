import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.lang.Math;
import java.io.*;
import java.util.Scanner;

public class DrawLines extends JPanel implements ActionListener {

    private JFrame frame;
    private JLabel label;
    private JPanel bottonPanel;
    private ArrayList<Point> startPoint, endPoint, lineStart, lineEnd;
    private JMenuItem saveItem, loadItem;
    private JMenuBar bar;
    private JMenu fileMenu;
    private JComboBox<String> comboBox;
    private String[] options = { "Add Start Point", "Add End", "Add Line", "Delete point/line", "Move point" };
    private Point point1, point2, deletePoint, movePoint1, movePoint2;

    private JPopupMenu popUp1, popUp2;
    JMenuItem j1, j2, j3, j4;

    JButton button;
    boolean bool = false;

    public DrawLines() {
        // Initialize frame
        frame = new JFrame("Draw Lines");
        frame.setSize(500, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Initialize Panel
        this.setPreferredSize(new Dimension(500, 450));
        this.setBackground(new Color(212, 187, 182));

        // Initialize ArrayList
        startPoint = new ArrayList<>();
        endPoint = new ArrayList<>();
        lineStart = new ArrayList<>();
        lineEnd = new ArrayList<>();

        // Initialize Label and ComboBox
        label = new JLabel(" Click and drag to draw."); // Create label for instruction prompt
        comboBox = new JComboBox<>(options);
        comboBox.setSize(40, 50);
        button = new JButton("Clear all");

        // Initialize panel that carries comboBox and label
        bottonPanel = new JPanel(new BorderLayout());
        bottonPanel.setBackground(new Color(212, 187, 182));
        bottonPanel.add(label, BorderLayout.NORTH);
        bottonPanel.add(comboBox, BorderLayout.EAST);
        bottonPanel.add(button, BorderLayout.WEST);

        // Initialize JMenuItems, JMenuBar, etc..
        saveItem = new JMenuItem("Save");
        loadItem = new JMenuItem("Load");
        bar = new JMenuBar();
        fileMenu = new JMenu("File");
        fileMenu.add(saveItem);
        fileMenu.add(loadItem);
        bar.add(fileMenu);

        // Popul menu

        popUp1 = new JPopupMenu();
        j1 = new JMenuItem("Delete all");
        j1.addActionListener(this);
        j1.setActionCommand("Delete all");
        j3 = new JMenuItem("Add start point");
        j3.addActionListener(this);
        j3.setActionCommand("Add start point");
        j4 = new JMenuItem("Add end point");
        j4.addActionListener(this);
        j4.setActionCommand("Add end point");
        popUp1.add(j1);
        popUp1.add(j3);
        popUp1.add(j4);

        popUp2 = new JPopupMenu();
        j2 = new JMenuItem("Delete point");
        j2.addActionListener(this);
        j2.setActionCommand("Delete point");
        popUp2.add(j2);

        // Add components to frame
        frame.setJMenuBar(bar);
        frame.add(this, BorderLayout.CENTER);
        frame.add(bottonPanel, BorderLayout.PAGE_END);

        // Add corresponding actionListeners
        saveItem.addActionListener(this);
        loadItem.addActionListener(this);
        comboBox.addActionListener(this);
        button.addActionListener(this);
        MouseControls mouse = new MouseControls();
        this.addMouseListener(mouse);

        frame.setVisible(true); // Set frame visible
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        for (int i = 0; i < lineStart.size(); i++) { // Draw lines
            g2.setColor(Color.BLACK);
            g2.drawLine(lineStart.get(i).x, lineStart.get(i).y, lineEnd.get(i).x, lineEnd.get(i).y);
        }
        for (int i = 0; i < startPoint.size(); i++) { // Draw start points
            g2.setColor(Color.red);
            g2.fillOval(startPoint.get(i).x, startPoint.get(i).y, 10, 10);
        }
        for (int i = 0; i < endPoint.size(); i++) { // Draw end points
            g2.setColor(Color.BLUE);
            g2.fillOval(endPoint.get(i).x, endPoint.get(i).y, 10, 10);
        }

        if (comboBox.getSelectedIndex() == 2 && bool) {
            AddLine(startPoint, endPoint, point1);
            bool = false;
        } else if (comboBox.getSelectedIndex() == 3) { // For deleting point and lines
            DeletPoint(startPoint, deletePoint);
            DeletPoint(endPoint, deletePoint);
            DeleteLine(lineStart, lineEnd, deletePoint);
            DeleteLine(lineEnd, lineStart, deletePoint);
        } else if (comboBox.getSelectedIndex() == 4) { // For moving a point
            // Note: for some reason a point must be deleted before this function works
            MovePoint(startPoint, movePoint1, movePoint2);
            MovePoint(endPoint, movePoint1, movePoint2);
            MovePoint(lineStart, movePoint1, movePoint2);
            MovePoint(lineEnd, movePoint1, movePoint2);
        }
    }

    // Method Used to Delete points
    public void DeletPoint(ArrayList<Point> arrayPoint, Point checkPoint) {
        for (int i = 0; i < arrayPoint.size(); i++) {
            if (checkPoint != null && checkPoint.x >= arrayPoint.get(i).x
                    && checkPoint.x <= arrayPoint.get(i).x + 10 && checkPoint.y >= arrayPoint.get(i).y
                    && checkPoint.y <= arrayPoint.get(i).y + 10) {

                arrayPoint.remove(i);
                repaint();
            }
        }
    }

    // Method Used to Delete lines corresponding to points
    public void DeleteLine(ArrayList<Point> arrayPoint1, ArrayList<Point> arrayPoint2, Point checkPoint) {
        for (int i = 0; i < arrayPoint1.size(); i++) {
            if (checkPoint != null && checkPoint.x >= arrayPoint1.get(i).x
                    && checkPoint.x <= arrayPoint1.get(i).x + 10 && checkPoint.y >= arrayPoint1.get(i).y
                    && checkPoint.y <= arrayPoint1.get(i).y + 10) {

                arrayPoint1.remove(i);
                arrayPoint2.remove(i);
                repaint();
            }
        }
    }

    // Method Used to Delete points
    public void MovePoint(ArrayList<Point> arrayPoint, Point checkPoint, Point movePoint) {
        for (int i = 0; i < arrayPoint.size(); i++) {
            if (deletePoint != null && checkPoint.x >= arrayPoint.get(i).x
                    && checkPoint.x <= arrayPoint.get(i).x + 10 && checkPoint.y >= arrayPoint.get(i).y
                    && checkPoint.y <= arrayPoint.get(i).y + 10) {
                arrayPoint.remove(i);
                arrayPoint.add(i, movePoint);
                repaint();
            }
        }
    }

    // Method to add lines to ArrayList and find end point closest to begining point
    public void AddLine(ArrayList<Point> arrayPoint, ArrayList<Point> arrayPoint2, Point checkPoint) {
        Integer point1 = null, point2 = 0;
        for (int i = 0; i < arrayPoint.size(); i++) {
            if (checkPoint != null && checkPoint.x >= arrayPoint.get(i).x
                    && checkPoint.x <= arrayPoint.get(i).x + 10 && checkPoint.y >= arrayPoint.get(i).y
                    && checkPoint.y <= arrayPoint.get(i).y + 10) {
                for (int j = 1; j < arrayPoint2.size(); j++) {
                    point1 = i;
                    point1 = 0;
                    double min = Math.hypot(arrayPoint2.get(0).x - arrayPoint.get(i).x, arrayPoint2.get(0).y -
                            arrayPoint.get(i).y);
                    double hyp1 = Math.hypot(arrayPoint2.get(j).x - arrayPoint.get(i).x, arrayPoint2.get(j).y -
                            arrayPoint.get(i).y);

                    if (hyp1 < min) {
                        min = hyp1;
                        point1 = i;
                        point2 = j;
                        // lineStart.add(arrayPoint.get(point1));
                        // lineEnd.add(arrayPoint2.get(point2));
                    }
                }
            }
        }
        if (point1 != null) {
            lineStart.add(arrayPoint.get(point1));
            lineEnd.add(arrayPoint2.get(point2));
        }

        repaint();
    }

    public boolean isPoint(ArrayList<Point> arrayPoint, Point checkPoint) {
        for (int i = 0; i < arrayPoint.size(); i++) {
            if (checkPoint.x >= arrayPoint.get(i).x
                    && checkPoint.x <= arrayPoint.get(i).x + 10 && checkPoint.y >= arrayPoint.get(i).y
                    && checkPoint.y <= arrayPoint.get(i).y + 10) {
                return true;
            }
        }

        return false;
    }

    public void deleteAll() {
        startPoint.clear();
        endPoint.clear();
        lineStart.clear();
        lineEnd.clear();
        repaint();
    }

    // ActionPerformed method used for menuBar and comboBox
    @Override
    public void actionPerformed(ActionEvent e) {
        // To save a file
        if (e.getSource() == saveItem) {
            label.setText(" Save file as .txt");
            JFileChooser saveChooser = new JFileChooser();
            int response = saveChooser.showSaveDialog(null); // select place to save
            if (response == JFileChooser.APPROVE_OPTION) {
                try {
                    FileWriter fileWrite = new FileWriter(saveChooser.getSelectedFile().getAbsolutePath());
                    for (int i = 0; i < startPoint.size(); i++) {
                        fileWrite.write(Integer.toString(startPoint.get(i).x));
                        fileWrite.write(" ");
                        fileWrite.write(Integer.toString(startPoint.get(i).y));
                        fileWrite.write(" ");
                    }
                    fileWrite.write("\n");
                    for (int i = 0; i < endPoint.size(); i++) {
                        fileWrite.write(Integer.toString(endPoint.get(i).x));
                        fileWrite.write(" ");
                        fileWrite.write(Integer.toString(endPoint.get(i).y));
                        fileWrite.write(" ");
                    }
                    fileWrite.write("\n");
                    for (int i = 0; i < lineStart.size(); i++) {
                        fileWrite.write(Integer.toString(lineStart.get(i).x));
                        fileWrite.write(" ");
                        fileWrite.write(Integer.toString(lineStart.get(i).y));
                        fileWrite.write(" ");
                    }
                    fileWrite.write("\n");
                    for (int i = 0; i < lineEnd.size(); i++) {
                        fileWrite.write(Integer.toString(lineEnd.get(i).x));
                        fileWrite.write(" ");
                        fileWrite.write(Integer.toString(lineEnd.get(i).y));
                        fileWrite.write(" ");
                    }
                    fileWrite.write("\n");

                    fileWrite.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            // To load a file
        } else if (e.getSource() == loadItem) {
            JFileChooser loadChooser = new JFileChooser();

            int response = loadChooser.showOpenDialog(null); // select file to open
            if (response == JFileChooser.APPROVE_OPTION) {
                try {
                    Scanner read = new Scanner(new File(loadChooser.getSelectedFile().getAbsolutePath()));
                    deleteAll();
                    int count = 1;

                    while (read.hasNextInt()) {
                        Scanner lineScanner = new Scanner(read.nextLine());
                        while (lineScanner.hasNextInt()) {
                            if (count == 1) {
                                int x = lineScanner.nextInt();
                                int y = lineScanner.nextInt();
                                startPoint.add(new Point(x, y));

                            } else if (count == 2) {
                                int x = lineScanner.nextInt();
                                int y = lineScanner.nextInt();
                                endPoint.add(new Point(x, y));
                            } else if (count == 3) {
                                int x = lineScanner.nextInt();
                                int y = lineScanner.nextInt();
                                lineStart.add(new Point(x, y));
                            } else if (count == 4) {
                                int x = lineScanner.nextInt();
                                int y = lineScanner.nextInt();
                                lineEnd.add(new Point(x, y));
                            }
                        }
                        count++;
                    }
                    read.close();

                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            // For clear button
        } else if (e.getSource() == button) {
            deleteAll();
        }

        // For popupMenu
        String command = e.getActionCommand();
        if (command == "Delete all") {
            deleteAll();
        } else if (command == "Add start point") {
            startPoint.add(point2);
            repaint();
        } else if (command == "Add end point") {
            endPoint.add(point2);
            repaint();
        } else if (command == "Delete point") {
            DeleteLine(lineStart, lineEnd, point2);
            DeleteLine(lineEnd, lineStart, point2);
            DeletPoint(startPoint, point2);
            DeletPoint(endPoint, point2);
        }

        // For combobox options
        if (comboBox.getSelectedIndex() == 0) {
            label.setText(" Click to add start point (red).");
        } else if (comboBox.getSelectedIndex() == 1) {
            label.setText(" Click to add start point (blue).");
        } else if (comboBox.getSelectedIndex() == 2) {
            label.setText(" Click a start point to add a line (minimum TWO end points needed).");
        } else if (comboBox.getSelectedIndex() == 3) {
            label.setText(" Click any point to delete.");
        } else if (comboBox.getSelectedIndex() == 4) {
            label.setText(" Click and drag to move (delete a point first to work).");
        }
    }

    // Private inner class for MouseAdapter
    private class MouseControls extends MouseAdapter {
        @Override
        public void mousePressed(MouseEvent e) {
            super.mouseClicked(e);
            point2 = e.getPoint();
            if (SwingUtilities.isRightMouseButton(e)) { // Right click for popUpMenu
                if (isPoint(startPoint, point2) == true || isPoint(endPoint, point2) == true) {
                    popUp2.show(frame, point2.x, point2.y);
                } else if (isPoint(startPoint, point2) == false || isPoint(endPoint, point2) == false) {
                    popUp1.show(frame, point2.x, point2.y);
                }
            } else {
                if (comboBox.getSelectedIndex() == 0) {
                    startPoint.add(e.getPoint());
                    repaint();
                } else if (comboBox.getSelectedIndex() == 1) {
                    endPoint.add(e.getPoint());
                    repaint();
                } else if (comboBox.getSelectedIndex() == 2) {
                    point1 = e.getPoint();
                    bool = true;
                    // AddLine(startPoint, endPoint, point1);
                } else if (comboBox.getSelectedIndex() == 3) {
                    deletePoint = e.getPoint();
                    repaint();
                } else if (comboBox.getSelectedIndex() == 4) {
                    movePoint1 = e.getPoint();
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            if (comboBox.getSelectedIndex() == 4) {
                movePoint2 = e.getPoint();
                repaint();
            } else if (comboBox.getSelectedIndex() == 2) {
                point2 = e.getPoint();
                repaint();
            }
        }
    }

    public static void main(String[] args) {
        new DrawLines();
    }
}
