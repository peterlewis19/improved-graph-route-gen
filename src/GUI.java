import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PrinterJob;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class GUI extends JFrame {
    private JLabel label;
    private JTextField dataEntry;
    private ArrayList<Node> allNodes;
    private int[][] adjMatrix;
    private ArrayList<Integer> generatedRoute;
    private static int minIndex;// = 0;
    private static int maxIndex;

    public GUI(ArrayList<Node> allNodes, int[][] adjMatrix, String[] allNodeNames ) {
        setTitle("Route Planner");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(null);
        setBounds(150, 50, 800, 700);

        maxIndex = allNodes.size() - 1;
        minIndex = 0;

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

        goButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //this takes the input from the textfields and turns them to a digit
                String start = startEntry.getText();
                String destination = destinationEntry.getText();
                System.out.println(start+ "," + destination);

                /*ArrayList<Integer> bestRoute = new ArrayList<>(Arrays.asList(799, 1503, 795, 792, 803, 786, 787, 788, 1754, 802));

                generatedRoute = bestRoute;

                backgroundMap.setVisible(false);

                routeDrawing.setRoute(bestRoute);
                //System.out.println("should be drawing the best route");
                routeDrawing.setVisible(true);
                routeDrawing.repaint();

                backgroundMap.repaint();
                backgroundMap.setVisible(true);*/

                //search a file for this String, hope it finds a match, which I can then turn into an integer

                //validate input is an integer
                /*boolean isInt = false;
                int startIndex = 0;
                int endIndex = 1;*/

                ///NEW RULES FOR TEXT INPUT
                /// use binary search, if not found then return user the results next to where it ended,
                /// these acts as a primitive form of suggested input

                /*try {
                    startIndex = Integer.parseInt(startEntry.getText());
                    endIndex = Integer.parseInt(destinationEntry.getText());
                    isInt = true;
                } catch (NumberFormatException error) {
                    isInt = false;
                }*/

                //TODO:
                // - linear search name array for input
                // - return index if its there
                // - return nearby results if its not found (USING SORTED ARRAY) EXTENSION


                // if value is present: get index
                // else: return -1

                int indexOfStart = getIndexOfRoadName(start, allNodeNames);
                int indexOfDestination = getIndexOfRoadName(destination, allNodeNames);

                //one of the inputs isnt contained in the array of names
                if (indexOfStart == -1 || indexOfDestination == -1){
                    //error message window TBC
                    System.out.println("At least one of these roads isn't included, try again with a different toad");
                } else{ //both of the names are valid, and associated with existing nodes
                    MainLoopOfGA newGA = new MainLoopOfGA(indexOfStart, indexOfDestination, allNodes, adjMatrix);
                    ArrayList<Integer> bestRoute = newGA.getBestRoute();

                    generatedRoute = bestRoute;

                    backgroundMap.setVisible(false);

                    routeDrawing.setRoute(bestRoute);
                    System.out.println("should be drawing the best route");
                    routeDrawing.setVisible(true);
                    routeDrawing.repaint();

                    backgroundMap.repaint();
                    backgroundMap.setVisible(true);
                }




                /*if (isInt) {
                    //if input is invalid, give error message
                    if (startIndex < 0 || startIndex > maxIndex - 2 || endIndex < 0 || endIndex > maxIndex - 2) {
                        System.out.println("ERROR IN INPUT");
                        createOutOfRangeWindow();
                    } else {
                        MainLoopOfGA newGA = new MainLoopOfGA(Integer.parseInt(startEntry.getText()), Integer.parseInt(destinationEntry.getText()), allNodes, adjMatrix);
                        ArrayList<Integer> bestRoute = newGA.getBestRoute();

                        generatedRoute = bestRoute;

                        backgroundMap.setVisible(false);

                        routeDrawing.setRoute(bestRoute);
                        routeDrawing.setVisible(true);
                        routeDrawing.repaint();

                        backgroundMap.repaint();
                        backgroundMap.setVisible(true);

                        //System.out.println("displaying map...");


                        try {
                            BufferedImage screenshot = getScreenshotOfBothCanvases(backgroundMap, routeDrawing);
                            // write the image as a PNG
                            ImageIO.write(
                                    screenshot,
                                    "png",
                                    new File("screenshot.png"));

                            //Printing this screenshot out
                            //TODO: make this happen on the press of a print button
                            PrinterJob job = PrinterJob.getPrinterJob();
                            /*job.setPrintable(new Printing(screenshot));
                            boolean doPrint = job.printDialog();

                            if (doPrint) {
                                try {
                                    job.print();
                                } catch (PrinterException exc) {
                                    // The job did not successfully
                                    // complete
                                }
                            }
                        } catch(Exception err) {
                            err.printStackTrace();
                        }

                    }
                } else {
                    createNotAnIntWindow();
                }

                //route is found much faster, but this delays it by a lot (roughly 30 seconds)
                // i think theres no way of avoiding this, so I just made more generations to ensure an optimal route still within the time
            }*/
            }
        });
        setVisible(true);
        getContentPane().setComponentZOrder(goButton, 0);


    }

    private static void createOutOfRangeWindow() {
        JFrame frame = new JFrame("Input out of range");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createOutOfRangeUI(frame);
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void createNotAnIntWindow() {
        JFrame frame = new JFrame("Input is not an integer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        createNotAnIntUI(frame);
        frame.setSize(300, 100);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void createOutOfRangeUI(final JFrame frame) {
        //JPanel panel = new JPanel();
        JLabel warningMessage = new JLabel("Input has to be between " + minIndex + " and " + (maxIndex - 2));
        warningMessage.setHorizontalAlignment(JLabel.CENTER);
        frame.add(warningMessage);

        frame.setDefaultCloseOperation(HIDE_ON_CLOSE);

    }

    private static void createNotAnIntUI(final JFrame frame) {
        JPanel panel = new JPanel();
        JLabel warningMessage = new JLabel("Input must be a number, such as 1 or 23");
        warningMessage.setHorizontalAlignment(JLabel.CENTER);
        frame.add(warningMessage);

        frame.setDefaultCloseOperation(frame.HIDE_ON_CLOSE);

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

