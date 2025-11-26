import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.ArrayList;

public class GUI extends JFrame {
    private ArrayList<Integer> generatedRoute;

    public GUI(ArrayList<Node> allNodes, int[][] adjMatrix, String[] allNodeNames ) {
        setTitle("Route Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(null);
        setBounds(150, 50, 800, 700);

        JLabel fieldCaption = new JLabel("Enter your start location: ");
        fieldCaption.setBounds(10, 10, 200, 30);

        JTextField startEntry = new JTextField();
        startEntry.setBounds(160, 10, 200, 30);

        JLabel destinationCaption = new JLabel("Enter your destination: ");
        destinationCaption.setBounds(370, 10, 200, 30);

        JTextField destinationEntry = new JTextField();
        destinationEntry.setBounds(510, 10, 200, 30);


        JButton goButton = new JButton("GO");
        goButton.setBounds(720, 10, 55, 30);

        JPanel panel = new JPanel();
        panel.setBackground(Color.lightGray);
        panel.setBounds(0, 0, 800, 50);


        //Dont add the canvas until after
        //Draw Nodes then draw route once it has been entered
        Canvas backgroundMap = new Canvas(allNodes, adjMatrix);
        //, allNodeNames);
        backgroundMap.setBounds(10, 70, 800, 650);
        backgroundMap.setOpaque(true);

        RouteCanvas routeDrawing = new RouteCanvas(generatedRoute, allNodes);
        routeDrawing.setBounds(10, 70, 800, 650);
        routeDrawing.setBackground(Color.white);
        routeDrawing.setOpaque(false);

        add(fieldCaption);
        add(startEntry);
        add(destinationCaption);
        add(destinationEntry);
        add(backgroundMap);
        add(routeDrawing);
        add(panel);
        add(goButton);
        backgroundMap.setVisible(true);

        setVisible(true);
        routeDrawing.setVisible(false);

        goButton.addActionListener(e -> {
            //this takes the input from the text fields and turns them to a digit
            String start = startEntry.getText();
            String destination = destinationEntry.getText();
            //System.out.println(start+ "," + destination);


            int indexOfStart = getIndexOfRoadName(start, allNodeNames);
            int indexOfDestination = getIndexOfRoadName(destination, allNodeNames);

            //one of the inputs isnt contained in the array of names
            if (indexOfStart == -1 || indexOfDestination == -1){
                //error message window TBC
                //System.out.println("At least one of these roads isn't included, try again with a different toad");
                createWrongRoadWindow();


            } else { //both of the names are valid, and associated with existing nodes
                MainLoopOfGA newGA = new MainLoopOfGA(indexOfStart, indexOfDestination, allNodes, adjMatrix);
                ArrayList<Integer> bestRoute = newGA.getBestRoute();
                generatedRoute = bestRoute;

                //z-order is determined by order of painting,
                // so in order to draw route on top of map, have to repaint it over
                backgroundMap.setVisible(false);

                routeDrawing.setRoute(bestRoute);
                routeDrawing.setVisible(true);
                routeDrawing.repaint();

                backgroundMap.repaint();
                backgroundMap.setVisible(true);

                try {
                    BufferedImage screenshot = getScreenshotOfBothCanvases(backgroundMap, routeDrawing);
                    // write the image as a PNG
                    ImageIO.write(
                            screenshot,
                            "png",
                            new File("screenshot.png"));

                    SwingUtilities.invokeLater(() -> {
                        PrinterJob job = PrinterJob.getPrinterJob();
                        job.setPrintable(new Printing(screenshot));
                        boolean doPrint = job.printDialog();

                        if (doPrint) {
                            try {
                                job.print();
                            } catch (PrinterException exc) {
                                // The job did not successfully
                                // complete
                                createUnableToScreenshotWindow();
                            }
                        }
                    });
                } catch (Exception err) {
                    //err.printStackTrace();
                    createUnableToScreenshotWindow();
                }
            }
        });
        //setVisible(true);
        getContentPane().setComponentZOrder(goButton, 0);


    }

    private static void createWrongRoadWindow() {
        JFrame frame = new JFrame("Road Name Error");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createWrongRoadUI(frame);
        frame.setSize(400, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void createWrongRoadUI(final JFrame frame) {
        JLabel warningMessage = new JLabel("One of these roads isn't included in the database, please try again.");
        warningMessage.setHorizontalAlignment(JLabel.CENTER);
        frame.add(warningMessage);

        frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    private static void createUnableToScreenshotWindow() {
        JFrame frame = new JFrame("Screenshot Error");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createUnableToScreenshotUI(frame);
        frame.setSize(400, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void createUnableToScreenshotUI(final JFrame frame) {
        JLabel warningMessage = new JLabel("Currently unable to print the screen off, please close and open the program.");
        warningMessage.setHorizontalAlignment(JLabel.CENTER);
        frame.add(warningMessage);

        frame.setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    public BufferedImage getScreenshotOfBothCanvases(Component component1, Component component2) {
        int width = Math.max(component1.getWidth(), component2.getWidth());
        int height = Math.max(component1.getHeight(), component2.getHeight());

        BufferedImage combined = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = combined.createGraphics();

        //set white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);


        // paint route on top if it exists
        if (generatedRoute != null) {
            component2.printAll(g2d);
        }

        //paint bacnground first, then generated route
        component1.printAll(g2d);

        g2d.dispose();
        return combined;
    }

    //returns index or otherwise returns -1
    // will return the first istance of a roadName
    private static int getIndexOfRoadName(String roadName, String[] arrayOfRoadNames){
        boolean isFound = false;
        int count = 0;

        while (count < arrayOfRoadNames.length && !isFound){
            if (arrayOfRoadNames[count].equals(roadName)){
                isFound = true;
            }else {
                count++;
            }
        }

        if (count == arrayOfRoadNames.length){
            count = -1;
        }

        return count;
    }


}

