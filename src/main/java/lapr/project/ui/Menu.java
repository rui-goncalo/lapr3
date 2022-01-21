package lapr.project.ui;

import lapr.project.data.LoadDBFiles;
import lapr.project.data.MakeDBConnection;
import lapr.project.model.*;
import lapr.project.structures.AVL;
import lapr.project.structures.AdjacencyMatrixGraph;
import lapr.project.structures.KDTree;
import lapr.project.utils.*;


import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

/**
 * @author Rui Gonçalves - 1191831
 * @author João Teixeira - 1180590
 */
public class Menu {

    // Declaring ANSI_RESET so that we can reset the color
    public static final String ANSI_RESET = "\u001B[0m";

    // Declaring the color
    // Custom declaration
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_RED_BACKGROUND = "\u001B[41m";

    private static final String BIG_SHIP_FILE = "data/bships.csv";
    private static final String SMALL_SHIP_FILE = "data/sships.csv";
    private static final String BIG_PORTS_FILE = "data/bports.csv";
    private static final String SMALL_PORTS_FILE = "data/sports.csv";

    private static ArrayList<Ship> shipArray = new ArrayList<>();
    private static ArrayList<Port> portsArray = new ArrayList<>();

    private static final AVL<ShipMMSI> mmsiAVL = new AVL<>();
    private static final AVL<ShipIMO> imoAVL = new AVL<>();
    private static final AVL<ShipCallSign> csAVL = new AVL<>();

    private static final KDTree<Port> portTree = new KDTree<>();
    private static Ship currentShip = null;

    private static AdjacencyMatrixGraph<String, Integer> capitalBordersMatrix = null;
    private static AdjacencyMatrixGraph<Port, Integer> portMatrix = null;

    /**
     * Opens the main menu with all the options for users.
     */
    public static void mainMenu() {
        try (Scanner sc = new Scanner(System.in)) {
            int choice;
            do {
                String[] options = {"Exit\n", "Imports", "Management", "DataBase Queries"};
                printFrontMenu("Main Menu", options, true);
                choice = getInput("Please make a selection: ", sc);

                switch (choice) {
                    case 0:
                        break;
                    case 1:
                        menuImport(sc);
                        break;
                    case 2:
                        if (shipArray.isEmpty() && portsArray.isEmpty()) {
                            System.out.println(ANSI_RED_BACKGROUND
                                    + "Please import Ships and Ports first."
                                    + ANSI_RESET);
                            break;
                        }
                        if (shipArray.isEmpty()) {
                            System.out.println(ANSI_RED_BACKGROUND
                                    + "Please import Ships first."
                                    + ANSI_RESET);
                            break;
                        }
                        if (portsArray.isEmpty()) {
                            System.out.println(ANSI_RED_BACKGROUND
                                    + "Please import Ports first."
                                    + ANSI_RESET);
                            break;
                        }
                        menuManageCargo(sc);
                        break;
                    case 3:
                        if (shipArray.isEmpty()) {
                            System.out.println(ANSI_RED_BACKGROUND
                                    + "Please import Ships and Ports first."
                                    + ANSI_RESET);
                            break;
                        }
                        dbQueriesMenu(sc);
                }

            } while (choice != 0);
        }
    }

    /**
     * Opens the menu for imports.
     *
     * @param sc scanner to read input from the user
     */
    private static void menuImport(Scanner sc) {
        int choice;

        String[] options = {"Go Back\n", "Small Ship File CSV", "Big Ship File CSV", "Small Ports File CSV",
                "Big Ports File CSV\n", "Load Ships from Database", "Load Ports from Database",
                "Print Border Map"};

        FunctionsGraph.populateGraph();

        printMenu("Import Ships", options, true);

        choice = getInput("Please make a selection: ", sc);

        switch (choice) {
            case 0:
                break;
            case 1:
                if (!shipArray.isEmpty()) {
                    shipArray.clear();
                }
                try {
                    shipArray = CSVReaderUtils.readShipCSV(SMALL_SHIP_FILE);
                    insertShips();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 2:
                if (!shipArray.isEmpty()) {
                    shipArray.clear();
                }
                try {
                    shipArray = CSVReaderUtils.readShipCSV(BIG_SHIP_FILE);
                    insertShips();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 3:
                if (!portsArray.isEmpty()) {
                    portsArray.clear();
                }
                try {
                    portsArray = CSVReaderUtils.readPortCSV(SMALL_PORTS_FILE);
                    insertPorts();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 4:
                if (!portsArray.isEmpty()) {
                    portsArray.clear();
                }
                try {
                    portsArray = CSVReaderUtils.readPortCSV(BIG_PORTS_FILE);
                    insertPorts();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 5:
                if (!shipArray.isEmpty()) {
                    shipArray.clear();
                }
                shipArray = LoadDBFiles.readShipDB();
                System.out.println("Ships are imported with success");
                break;
            case 6:
                if (!portsArray.isEmpty()) {
                    portsArray.clear();
                }
                portsArray = LoadDBFiles.readPortDB();
                System.out.println("Ports are imported with success");
                break;
            case 7:
                FunctionsGraph.getBorderMap();
        }
    }

    /**
     * Opens the menu for managing Ships.
     *
     * @param sc scanner to read input from the user
     */
    private static void menuManageCargo(Scanner sc) {
        int choice;

        do {

            String[] options = {"Go Back\n", "Show all Ships", "Search by Ship", "Search Ship Pairs\n",
                    "Create Summary of Ships", "View Summaries by Ship", "Get TOP N Ships\n",
                    "Get Nearest Port\n", "Print N Closest Port Matrix", "Print Ports Closest to Capital - same country - Matrix",
                    "Print Capital and Borders Matrix\n", "Vessel Type", "Calculation Center of Mass","Position Containers","Energy Needed to Containers"};
            printMenu("Manage Ships", options, true);
            choice = getInput("Please make a selection: ", sc);

            switch (choice) {
                case 0:
                    break;
                case 1:
                    for (Ship ship : Menu.mmsiAVL.inOrder()) {
                        ship.printShip();
                    }
                    break;
                case 2:
                    menuSearch(sc);
                    break;
                case 3:
                    ArrayList<Calculator.ShipPair> pairs = Calculator.searchShipPairs(shipArray);
                    for (Calculator.ShipPair shipPair : pairs) {
                        System.out.println(shipPair.getFirstShip().getMmsi() + " + " + shipPair.getSecondShip().getMmsi());
                    }
                    break;
                case 4:
                    generateSummaries();
                    break;
                case 5:
                    Ship currentShip = null;
                    choice = getInput("Ship's MMSI: ", sc);
                    for (Ship ship : shipArray) {
                        if (ship.getMmsi() == choice) {
                            currentShip = ship;
                        }
                    }

                    if (currentShip != null) {
                        if (currentShip.getSummary() != null) {
                            printSummary(currentShip.getSummary());
                        } else {
                            System.out.println(ANSI_RED_BACKGROUND
                                    + "Please import Summaries first."
                                    + ANSI_RESET);
                        }
                    } else {
                        System.out.println(ANSI_RED_BACKGROUND
                                + "Sorry, no Ship found with this MMSI."
                                + ANSI_RESET);
                    }
                    break;
                case 6:
                    if (shipArray.get(0).getSummary() == null) {
                        System.out.println("Summaries must be created first.");
                        break;
                    }
                    choice = getInput("TOP N Ships:\nN = ", sc);
                    getTopNShips(choice);
                    break;
                case 7:
                    Scanner scanner = new Scanner(System.in);
                    System.out.print(" > Please insert ship's CallSign: ");
                    String callSign = scanner.nextLine();

                    if (csAVL.find(new ShipCallSign(callSign)) != null) {
                        currentShip = csAVL.find(new ShipCallSign(callSign));
                        LocalDateTime date = DateMenu.readDate(scanner, "Insert date: ");
                        ShipData data = currentShip.getDataByDate(date);

                        if (data != null) {
                            Port nearestPort = portTree.findNearestNeighbour(
                                    data.getLatitude(),
                                    data.getLongitude());
                            System.out.println("Nearest Port: " + nearestPort.getName() + "\n" + "Latitude: " + nearestPort.getLatitude() + "\n" + "Longitude: " + nearestPort.getLongitude());
                        }
                    } else {
                        System.out.println("Ship not found");
                    }
                    break;
                case 8:
                    int number = getInput("Insert N Ports: \n", sc);
                    System.out.println(FunctionsGraph.getNClosestPortMatrix(number).toString());
                    break;
                case 9:
                    System.out.println(FunctionsGraph.getClosestPortsFromCapital().toString());
                    break;
                case 10:
                    System.out.println(FunctionsGraph.getCapitalBordersMatrix().toString());
                    break;
                case 11:
                    vesselTypesMenu(sc);
                case 12:
                    menuCenterOfMass(sc);
                case 13:
                    menuPosContainers(sc);
                case 14:
                    menuEnergyNeeded(sc);
            }

        } while (choice != 0);
    }

    private static void menuCenterOfMass(Scanner scan) {
        int type;
        double c_height = 0, c_width = 0, r_length = 0, r_height = 0, t_height = 0, m1 = 0, m2 = 0, m3 = 0, xCM = 0, yCM = 0;

        int choice;
        do {
            String[] options = {"Go Back\n", "Bow", "Mid", "Stern"};
            //String[] options = {"Choose one type of vessel.\n", "1. Bow", "2. Mid", "3. Stern\n"};
            printMenu("Calculation Center of Mass", options, true);
            choice = getInput("Please make a selection: ", scan);
            Scanner input = new Scanner(System.in);

            switch (choice) {
                case 1:
                    System.out.println("Enter the height of the cabin crew (m).");
                    c_height = input.nextDouble();
                    System.out.println("Enter the width of the cabin crew (m). ");
                    c_width = input.nextDouble();
                    System.out.println("Enter the length of the rectangle (m).");
                    r_length = input.nextDouble();
                    System.out.println("Enter the height of the rectangle (m).");
                    r_height = input.nextDouble();
                    System.out.println("Enter the height of the triangle (m).");
                    t_height = input.nextDouble();
                    System.out.println("Enter the mass of the cabin crew (kg).");
                    m1 = input.nextDouble();
                    System.out.println("Enter the mass of the rectangle (kg).");
                    m2 = input.nextDouble();
                    System.out.println("Enter the mass of the triangle (kg).");
                    m3 = input.nextDouble();
                    if (r_height == t_height) {
                        xCM = ((r_length + (t_height - (c_width / 2))) * m1 + (r_length / 2) * m2 + ((r_length + r_length + (r_length + t_height)) / 3) * m3) / (m1 + m2 + m3);
                        yCM = ((r_height + (c_height / 2)) * m1 + (r_height / 2) * m2 + ((t_height + t_height) / 3) * m3) / (m1 + m2 + m3);

                        System.out.printf("The center of mass is: (" + xCM + ", " + yCM + ").");
                    } else {
                        System.out.println("The height of the triangle and rectangle doesn't match.");
                        break;
                    }
                    break;
                case 2:
                    System.out.println("Enter the height of the cabin crew (m).");
                    c_height = input.nextDouble();
                    System.out.println("Enter the width of the cabin crew (m). ");
                    c_width = input.nextDouble();
                    System.out.println("Enter the length of the rectangle (m).");
                    r_length = input.nextDouble();
                    System.out.println("Enter the height of the rectangle (m).");
                    r_height = input.nextDouble();
                    System.out.println("Enter the height of the triangle (m).");
                    t_height = input.nextDouble();
                    System.out.println("Enter the mass of the cabin crew (kg).");
                    m1 = input.nextDouble();
                    System.out.println("Enter the mass of the rectangle (kg).");
                    m2 = input.nextDouble();
                    System.out.println("Enter the mass of the triangle (kg).");
                    m3 = input.nextDouble();
                    if (r_height == t_height) {
                        xCM = (((r_length + t_height) / 2) * m1 + (r_length / 2) * m2 + ((r_length + r_length + (r_length + t_height)) / 3) * m3) / (m1 + m2 + m3);
                        yCM = ((r_height + (c_height / 2)) * m1 + (r_height / 2) * m2 + ((t_height + t_height)/3) * m3) / (m1 + m2 + m3);
                        System.out.printf("The center of mass is: (" + xCM + ", " + yCM + ").");
                    } else {
                        System.out.println("The height of the triangle and rectangle doesn't match.");
                        break;
                    }
                    break;
                case 3:
                    System.out.println("Enter the height of the cabin crew (m).");
                    c_height = input.nextDouble();
                    System.out.println("Enter the width of the cabin crew (m). ");
                    c_width = input.nextDouble();
                    System.out.println("Enter the length of the rectangle (m).");
                    r_length = input.nextDouble();
                    System.out.println("Enter the height of the rectangle (m).");
                    r_height = input.nextDouble();
                    System.out.println("Enter the height of the triangle (m).");
                    t_height = input.nextDouble();
                    System.out.println("Enter the mass of the cabin crew (kg).");
                    m1 = input.nextDouble();
                    System.out.println("Enter the mass of the rectangle (kg).");
                    m2 = input.nextDouble();
                    System.out.println("Enter the mass of the triangle (kg).");
                    m3 = input.nextDouble();
                    if (r_height == t_height) {
                        xCM = ((c_width / 2) * m1 + (r_length / 2) * m2 + ((r_length + r_length + (r_length + t_height)) / 3) * m3) / (m1 + m2 + m3);
                        yCM = ((r_height + (c_height / 2)) * m1 + (r_height / 2) * m2 + ((t_height + t_height) / 3) * m3) / (m1 + m2 + m3);
                        System.out.printf("The center of mass is: (" + xCM + ", " + yCM + ").");
                    } else {
                        System.out.println("The height of the triangle and rectangle doesn't match.");
                        break;
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option, choose again.");
                    break;
            }
        } while (choice != 0);
    }

    private static void menuPosContainers(Scanner scan)
    {
        double contHeight=2.385;
        double contLength=5.896;
        double contWidth = 2.350;
        double s_length,s_width, contCmXX = 0,contCmYY = 0,sCmXX,sCmYY;
        int choice,nContainers;
        ArrayList<Calculator.ContainerInfo> containerInfos = new ArrayList<Calculator.ContainerInfo>();
        do {
            String[] options = {"Go Back\n", "User Default Container Measurements\n Container Height:" + contHeight + "\n Container Length: " +contLength+ "\n Container Width: " + contWidth, "Enter New Container Measurements"};
            printMenu("Calculation position of containers on the vessel", options, true);
            choice = getInput("Please make a selection: ", scan);
            Scanner input = new Scanner(System.in);
            switch (choice) {
                case 1:
                    System.out.println("Enter the number of containers on the vessel.");
                    nContainers = input.nextInt();
                    System.out.println("Enter the length of the ships rectangle (m).");
                    s_length = input.nextDouble();
                    System.out.println("Enter the width of the ships rectangle (m). ");
                    s_width = input.nextDouble();

                    containerInfos = Calculator.calculateContainersPosition(nContainers,contHeight,contLength,contWidth,s_length,s_width);
                    if(containerInfos == null)
                    {
                        System.out.println(ANSI_RED_BACKGROUND
                                + "Impossible to add that many containers to the specified ship."
                                + ANSI_RESET);

                    } else {
                        int n =0;
                        for(Calculator.ContainerInfo containerInfo : containerInfos)
                        {
                            n++;
                            contCmXX +=containerInfo.getXxCm();
                            contCmYY += containerInfo.getYyCm();
                            System.out.println(containerInfo);

                        }
                        contCmXX = contCmXX /nContainers;
                        contCmYY = contCmYY / nContainers;
                        sCmXX = s_length /2;
                        sCmYY = s_width/2;
                        System.out.println("Containers total Center of Mass XX : " + contCmXX);
                        System.out.println("Containers total Center of Mass YY : " +contCmYY);
                        System.out.println("Ship Center of Mass XX: " + sCmXX);
                        System.out.println("Ship Center of Mass YY: " + sCmYY);


                    }


                    break;
                case 2:
                    System.out.println("Enter the height of the Container (m).");
                   double  contHeight1 = input.nextInt();
                    System.out.println("Enter the length of the Container (m).");
                   double contLength1 = input.nextInt();
                    System.out.println("Enter the width of the Container (m).");
                   double contWidth1 = input.nextInt();
                    System.out.println("Enter the number of containers on the vessel.");
                    nContainers = input.nextInt();
                    System.out.println("Enter the length of the ships rectangle (m).");
                    s_length = input.nextDouble();
                    System.out.println("Enter the width of the ships rectangle (m). ");
                    s_width = input.nextDouble();
                    containerInfos = Calculator.calculateContainersPosition(nContainers,contHeight1,contLength1,contWidth1,s_length,s_width);
                    if(containerInfos == null)
                    {

                        System.out.println(ANSI_RED_BACKGROUND
                                + "Impossible to add that many containers to the specified ship."
                                + ANSI_RESET);
                    } else {
                        int n = 0;
                        for(Calculator.ContainerInfo containerInfo : containerInfos)
                        {
                            n++;
                            contCmXX +=containerInfo.getXxCm();
                            contCmYY += containerInfo.getYyCm();
                            System.out.println(containerInfo);

                        }
                        contCmXX = contCmXX /nContainers;
                        contCmYY = contCmYY / nContainers;
                        sCmXX = s_length /2;
                        sCmYY = s_width/2;
                        System.out.println("Containers total Center of Mass XX : " + contCmXX);
                        System.out.println("Containers total Center of Mass YY : " +contCmYY);
                        System.out.println("Ship Center of Mass XX: " + sCmXX);
                        System.out.println("Ship Center of Mass YY: " + sCmYY);

                    }



                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option, choose again.");
                    break;
            }
        } while (choice != 0);

    }

    private static void menuEnergyNeeded(Scanner scan)
    {

        int choice;
        do {
            String[] options = {"Go Back\n", "Energy needed to a container in a trip of 2h30m with external temp of 20ºC", "Energy needed to a vessel with X containers in an established trip", "Energy needed to a vessel in role of containers position","How many auxiliar equipments of X kW are needed to power Y Containers in temperature of 7ºC and Z Containers inf temperature of -5ºC "};
            printMenu("Energy Needed to transport of goods", options, true);
            choice = getInput("Please make a selection: ", scan);
            Scanner input = new Scanner(System.in);

            switch (choice) {
                case 1:
                    calculateEnergyNeeded(9000,20,1);
                    break;
                case 2:
                    System.out.println("Enter the number of containers on the vessel.");
                    int nContainers = input.nextInt();
                    System.out.println("Enter the average Temperature of the trip");
                    double temperature = input.nextInt();
                    Ship currentShip = null;
                    choice = getInput("Ship's MMSI: ", scan);
                    for (Ship ship : shipArray) {
                        if (ship.getMmsi() == choice) {
                            currentShip = ship;
                        }
                    }

                    if (currentShip != null) {
                        if (currentShip.getSummary() != null) {
                            long seconds = currentShip.getSummary().getMinutes()*60;
                            seconds += currentShip.getSummary().getDays()*24*60*60;
                            seconds += currentShip.getSummary().getHours()*60*60;
                            long secondsPos = ~(seconds - 1);
                            calculateEnergyNeeded(secondsPos,temperature,nContainers);
                        } else {
                            System.out.println(ANSI_RED_BACKGROUND
                                    + "Please import Summaries first."
                                    + ANSI_RESET);
                        }
                    } else {
                        System.out.println(ANSI_RED_BACKGROUND
                                + "Sorry, no Ship found with this MMSI."
                                + ANSI_RESET);
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option, choose again.");
                    break;
            }


        } while (choice != 0);

    }

    /**
     * Opens the menu for searching ships.
     *
     * @param sc scanner to read input from the user
     */
    private static void menuSearch(Scanner sc) {
        int choice;
        Scanner scan = new Scanner(System.in);

        do {
            String[] options = {"Go Back\n", "Search by MMSI", "Search by IMO", "Search by Call Sign"};
            printMenu("Search Ship", options, true);
            choice = getInput("Please make a selection: ", sc);

            switch (choice) {
                case 0:
                    break;
                case 1:
                    System.out.print("Please insert ship's MMSI: ");
                    String mmsi = scan.nextLine();
                    if (mmsiAVL.find(new ShipMMSI(Integer.parseInt(mmsi))) != null) {
                        Menu.currentShip = mmsiAVL.find(new ShipMMSI(Integer.parseInt(mmsi)));
                        menuShowShip(sc);
                    } else {
                        System.out.println("Ship not found");
                    }
                    break;
                case 2:
                    System.out.print("Please insert ship's IMO: ");
                    String imo = scan.nextLine();
                    if (imoAVL.find(new ShipIMO(Integer.parseInt(imo))) != null) {
                        Menu.currentShip = imoAVL.find(new ShipIMO(Integer.parseInt(imo)));
                        menuShowShip(sc);
                    } else {
                        System.out.println("Ship not found");
                    }
                    break;
                case 3:
                    System.out.print("Please insert ship's CallSign:");
                    String callSign = scan.nextLine();
                    if (csAVL.find(new ShipCallSign(callSign)) != null) {
                        Menu.currentShip = csAVL.find(new ShipCallSign(callSign));
                        menuShowShip(sc);
                    } else {
                        System.out.println("Ship not found");
                    }
                    break;

            }
        } while (choice != 0);
    }

    /**
     * Opens the menu for acessing ship information.
     *
     * @param sc scanner to read input from the user
     */
    private static void menuShowShip(Scanner sc) {
        int choice;

        do {

            String[] options = {"Go Back\n", "Current Ship Information", "Current Ship Records"};
            printMenu("Show Ship", options, true);
            choice = getInput("Please make a selection: ", sc);


            switch (choice) {
                case 0:
                    break;
                case 2:
                    System.out.println("Ship MMSI: " + Menu.currentShip.getMmsi());
                    for (ShipData data : Menu.currentShip.getDynamicShip()) {
                        System.out.println(data.toString());
                    }
                    break;
                case 1:
                    System.out.println(Menu.currentShip.toString());
                    break;
            }
        } while (choice != 0);
    }

    /**
     * Opens the menu for database queries.
     *
     * @param sc scanner to read input from the user
     */
    private static void dbQueriesMenu(Scanner sc) {
        int choice;
        Scanner scan = new Scanner(System.in);
        do {
            String[] options = {"Go Back\n", "Current situation of a specific container",
                    "Containers to be offloaded in the next Port",
                    "Containers to be loaded in the next Port",
                    "C.Manifest transported during a given year and the average number of Containers per Manifest",
                    "Occupancy rate of a given Ship for a given Cargo Manifest.",
                    "Occupancy rate of a given Ship for a given Cargo Manifest.",
                    "Ships will be available on Monday next week\n\n"};
            printMenu("Show Ships", options, true);
            choice = getInput("Please make a selection: ", sc);
            Connection connection = MakeDBConnection.makeConnection();

            switch (choice) {
                case 1:
                    int containerNumber = getInput("Container Number: \n", sc);
                    String us204 = "{? = call func_client_container(" + containerNumber + ")}";

                    try (CallableStatement callableStatement = connection.prepareCall(us204)) {
                        callableStatement.registerOutParameter(1, Types.VARCHAR);
                        callableStatement.execute();
                        System.out.println(callableStatement.getString(1));
                    } catch (SQLException e) {
                        System.out.println("Failed to create a statement: " + e);
                    } finally {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            System.out.println("Failed to access database: " + e);
                        }
                    }
                    break;
                case 2:
                    int mmsiUnloading = getInput("Insert Ship's MMSI: ", sc);
                    if (mmsiAVL.find(new ShipMMSI(mmsiUnloading)) != null) {
                        currentShip = mmsiAVL.find(new ShipMMSI(mmsiUnloading));
                        FunctionsDB.getGetContainersNextPort(currentShip, "unloading");
                    }
                    break;
                case 3:
                    int mmsiLoading = getInput("Insert Ship's MMSI: ", sc);
                    if (mmsiAVL.find(new ShipMMSI(mmsiLoading)) != null) {
                        currentShip = mmsiAVL.find(new ShipMMSI(mmsiLoading));
                        FunctionsDB.getGetContainersNextPort(currentShip, "loading");
                    }
                    break;
                case 4:
                    int year = getInput("Select a Year: \n", sc);
                    String us207 = "{? = call func_avg_cm_container(" + year + ")}";


                    try (CallableStatement callableStatement = connection.prepareCall(us207)) {
                        callableStatement.registerOutParameter(1, Types.VARCHAR);
                        callableStatement.execute();
                        System.out.println(callableStatement.getString(1));
                    } catch (SQLException e) {
                        System.out.println("Failed to create a statement: " + e);
                    } finally {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            System.out.println("Failed to access database: " + e);
                        }
                    }
                    break;
                case 5:
                    int mmsi = getInput("Insert Ship's MMSI: \n", sc);
                    int container = getInput("Insert Cargo Manifest: \n", sc);
                    String us208 = "{? = call func_ratio(" + container + " , "+ mmsi +")}";


                    try (CallableStatement callableStatement = connection.prepareCall(us208)) {
                        callableStatement.registerOutParameter(1, Types.VARCHAR);
                        callableStatement.execute();
                        System.out.println(callableStatement.getString(1));
                    } catch (SQLException e) {
                        System.out.println("Failed to create a statement: " + e);
                    } finally {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            System.out.println("Failed to access database: " + e);
                        }
                    }
                    break;
                case 6:
                    int idShip = getInput("Insert Ship's MMSI: \n", sc);
                    int idContainer = getInput("Insert Cargo Manifest: \n", sc);
                    String us209 = "{? = call func_ratio_moment(" + idContainer + " , "+ idShip +")}";


                    try (CallableStatement callableStatement = connection.prepareCall(us209)) {
                        callableStatement.registerOutParameter(1, Types.VARCHAR);
                        callableStatement.execute();
                        System.out.println(callableStatement.getString(1));
                    } catch (SQLException e) {
                        System.out.println("Failed to create a statement: " + e);
                    } finally {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            System.out.println("Failed to access database: " + e);
                        }
                    }
                    break;
                case 7:
                    FunctionsDB.shipsAvailableMonday();
                    break;
                case 8:
                    String us305 = "{? = call func_check_container(" + 16 + " , "+ 1 +")}";


                    try (CallableStatement callableStatement = connection.prepareCall(us305)) {
                        callableStatement.registerOutParameter(1, Types.VARCHAR);
                        callableStatement.execute();
                        System.out.println(callableStatement.getString(1));
                    } catch (SQLException e) {
                        System.out.println("Failed to create a statement: " + e);
                    } finally {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            System.out.println("Failed to access database: " + e);
                        }
                    }
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Sorry, this option is invalid.");
                    break;
            }
        } while (choice != 0);
    }

    /**
     * Utility to print the front menu in an organized manner.
     *
     * @param title    menu title to be shown
     * @param options  number of options
     * @param showExit whether to show exit option or not
     */
    private static void printFrontMenu(String title, String[] options, boolean showExit) {

        System.out.println(
                "\n+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+\n" +
                        "           CARGO APP 103 > " + title +
                        "\n+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+");

        for (int i = 0; i < options.length; i++) {
            if (i == 0 && showExit || i > 0) {
                System.out.println("  " + i + " > " + options[i]);
            }
        }

        System.out.println(ANSI_YELLOW
                + "\n   Note: Please import ships and ports first."
                + ANSI_RESET);
        System.out.println("+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+");

    }

    /**
     * Utility to print the menus in an organized manner.
     *
     * @param title    menu title to be shown
     * @param options  number of options
     * @param showExit whether to show exit option or not
     */
    private static void printMenu(String title, String[] options, boolean showExit) {

        System.out.println(
                "\n+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+\n" +
                        "  CARGO APP 103 > " + title +
                        "\n+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+");

        for (int i = 0; i < options.length; i++) {
            if (i == 0 && showExit || i > 0) {
                System.out.println("  " + i + " > " + options[i]);
            }
        }

        System.out.println("+~~~~~~~~~~~~~~~~~~~~~~~~~~~~~+");

    }

    /**
     * Prompts for and veriies the user input.
     *
     * @param prompt Prompt to be shown to the user
     * @param sc     user input
     * @return user input
     */
    public static int getInput(String prompt, Scanner sc) {
        System.out.print(prompt);
        while (!sc.hasNextInt()) {
            System.out.println("Invalid input.");
            sc.next();
            System.out.print(prompt);
        }

        return sc.nextInt();
    }

    /**
     * Inserts the ships from shipArray into the trees.
     */
    private static void insertShips() {
        for (Ship ship : Menu.shipArray) {
            Menu.mmsiAVL.insert(new ShipMMSI(ship));
            Menu.imoAVL.insert(new ShipIMO(ship));
            Menu.csAVL.insert(new ShipCallSign(ship));
        }
    }

    /**
     * Generates ship summaries.
     */
    private static void generateSummaries() {
        for (Ship ship : shipArray) {
            ship.setSummary(new Summary(ship));
        }
        shipArray.sort(new ShipCompare().reversed());
        System.out.println("Summaries created.");
    }

    /**
     * Returns the top n ships in most distance travelled.
     *
     * @param n number of ships to return.
     */
    private static void getTopNShips(int n) {
        if (n > shipArray.size()) {
            System.out.println("The chosen number is great than the amount of ships available.");
            return;
        }
        System.out.println();
        for (int i = 0; i < n; i++) {
            Ship current = shipArray.get(i);
            System.out.printf("- %.2fkm > %s\n", current.getSummary().getTravelledDistance(), current.getName());
        }
    }

    /**
     * Inserts ports from the portsArray into the KDtree.
     *
     * @return false if collection is empty or true if it sucessfully inserted
     */
    private static boolean insertPorts() {

        if (portsArray == null) return false;

        List<KDTree.Node<Port>> nodesPorts = new ArrayList<>();
        for (Port port : portsArray) {
            KDTree.Node<Port> node = new KDTree.Node<>(port.getLatitude(), port.getLongitude(), port);
            nodesPorts.add(node);
        }
        portTree.buildTree(nodesPorts);
        return true;
    }

    /**
     * Prints a ship's summary.
     *
     * @param summaryShip ship summary to be printed
     */
    private static void printSummary(Summary summaryShip) {
        System.out.println(
                "\nDeparture Latitude: " + summaryShip.getDepartLat() +
                        "\nDeparture Longitude: " + summaryShip.getDepartLon() +
                        "\nArrival Latitude: " + summaryShip.getArrLat() +
                        "\nArrival Longitude: " + summaryShip.getArrLon() +
                        "\nDeparture Time: " + summaryShip.getDepartureTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                        "\nArrival Schedule: " + summaryShip.getArrivalTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                        "\nTravel's Time: " + summaryShip.getDays() + " days " + summaryShip.getHours() + " hours " + summaryShip.getMinutes() + " minutes" +
                        "\nMax SOG: " + summaryShip.getMaxSog() +
                        "\nMean SOG: " + summaryShip.getMeanSog() +
                        "\nMax COG: " + summaryShip.getMaxCog() +
                        "\nMean COG: " + summaryShip.getMeanCog() +
                        "\nTravelled distance: " + summaryShip.getTravelledDistance() +
                        "\nDelta distance: " + summaryShip.getDeltaDistance()
        );
    }

    public static void vesselTypesMenu(Scanner sc) {
        Ship containerVessel = new Ship(
                636091400,
                new ArrayList<>(),
                "RHL AGILITAS",
                9373486,
                "A8ND5",
                70,
                176f,
                27f,
                11.89,
                10.0);

        Ship fishingVessel = new Ship(
                303221000,
                new ArrayList<>(),
                "ARTIC SEA",
                7819216,
                "WDG5171",
                30,
                37f,
                9f,
                3f,
                10.0);

        Ship tugVessel = new Ship(
                499929694,
                new ArrayList<>(),
                "TANERLIQ",
                9178445,
                "WDF2025",
                52,
                45f,
                14f,
                0f,
                10.0);

        int choice;
        do {
            String[] options = {"Go Back\n", "Container Vessel", "Tug Vessel", "Ro-Ro Vessel \n"};
            printMenu("Manage Ships", options, true);
            choice = getInput("Please make a selection: ", sc);

            switch (choice) {
                case 1:
                    printType(containerVessel, fishingDescription);
                    break;
                case 2:
                    printType(fishingVessel, tugDescription);
                    break;
                case 3:
                    printType(tugVessel, RoRoDescription);
                    break;
                case 0:
                    break;
                default:
                    System.out.println("Invalid option, choose again.");
                    break;
            }
        } while (choice != 0);
    }

    private static void printType(Ship ship, String desc) {
        System.out.println("Ship Name: " + ship.getName() +
                "\nMMSI Code: " + ship.getMmsi() +
                "\nIMO Code: " + ship.getImo() +
                "\nCallSign: " + ship.getCallSign() +
                "\nLength: " + ship.getLength() +
                "\nWidth: " + ship.getWidth() +
                "\nDraft: " + ship.getDraft() +
                "\nVessel Type: " + ship.getVessel() +
                "\n\nDescription: " + desc);
    }

    private static final String fishingDescription = "Containers can accommodate anything from foodstuffs to electrical equipment to automobiles. They are also used to transport\n" +
            "bagged and palatalised goods, as well as liquids and refrigerated cargo.\n\n" +
            "Standard containers are measured as TEUs (Twenty-foot Equivalent Units) and are generally 20 feet (1 TEU) or 40 feet (2 TEUs) long.\n" +
            "All standard shipping containers are 8 feet wide and 8 feet 6 inches tall. There are also longer, taller and even shorter standard\n" +
            "sizes, but these are less common.\n\n" +
            "Container ships are made up of several holds, each equipped with “cell guides” which allow the containers to slot into place. Once\n" +
            "the first layers of containers have been loaded and the hatches closed, extra layers are loaded on top of the hatches. Each container\n" +
            "is then lashed to the vessel but also to each other to provide integrity. Containers are usually loaded by specialized cranes or even\n" +
            "general purpose cranes with container lifting attachments. Some small container vessels are geared to allow self-loading and discharging.\n";

    private static final String tugDescription = "Even with the advent of highly maneuverable vessels, the tug is still vitally important to the maritime industry. Modern tugs are highly\n" +
            "maneuverable with pulling power that can exceed 100 tonnes! Harbor tugs are very common at ports around the world, and generally less powerful.\n" +
            "These vessels assist in docking, undocking and moving large vessels within port limits. Tugs are also used to assist vessels during bad weather\n" +
            "or when carrying dangerous or polluting cargo. Harbor tugs are also employed to move barges, floating cranes and personnel around ports. Larger\n" +
            "units are kept on standby in strategic locations to act as deep-sea rescue and salvage tugs.\n\n" +
            "Tugs are also used to tow barges from port to port and move large structures such as offshore platforms and floating storage units. Some tugs\n" +
            "can push barges; this is particularly common on rivers where the tug is able to exert more turning force on the tow. There are also tugs that\n" +
            "are designed to ‘slot’ into a barge or hull. Once secured, this composite unit behaves and is treated like a standard powered vessel. These\n" +
            "composite units are common on North American river and coastal trade.\n";

    private static final String RoRoDescription = "Roll on-Roll off or Ro-Ro vessels come in many forms. They include vehicle ferries and cargo ships carrying truck trailers. The car\n" +
            "carrier is the most commonly-used ro-ro vessel. These slab-sided vessels feature multiple vehicle decks comprising parking lanes, linked by internal\n" +
            "ramps with access to shore provided by one or more loading ramps. Cargo capacity of such vessels is measured in Car Equivalent Units (CEU) and the\n" +
            "largest car carriers afloat today have a capacity of over 6,000 CEU.\n";



    public static void calculateEnergyNeeded(long duration, double temperature, int nContainers)
    {
        System.out.println("Energy needed to a container so it maintains a determined difference of temperature from the outside");
        System.out.println();
        System.out.println("E = Q*T");
        System.out.println(" E -> Energy (J) ; Q -> Quant. Heat Flow (W or J/s) ; t -> time (s)");
        System.out.println();
        System.out.println("Heat Flow is directionally proportional to the reason between temp gradient and thermal resistance");
        System.out.println("I = ∆T- RT");
        System.out.println("I -> Heat Flow (W or J/s) ; ∆T ->Temp difference (K) ; Rt ->Total resistance (K/W)");
        System.out.println();
        System.out.println("Energy Needed to 7ºC");
        System.out.println("Materials used and its thermal resistances in the Container:");
        System.out.println("Exterior:");
        System.out.println("Steel ; Thermal Resistance : 0.00000259 K/W ; Thickness : 0.010 m");
        System.out.println("Intermediate:");
        System.out.println("Expanded polyester ; Thermal Resistance : 0.063 K/W ; Thickness : 0.14 m");
        System.out.println("Interior:");
        System.out.println("Polypropylene ; Thermal Resistance : 0.00613 K/W ; Thickness : 0.05 m");
        System.out.println();
        System.out.println("E=∆T/RT * t");
        System.out.println("∆T = " + temperature + " - 7 = " + (temperature -7) + "K");
        System.out.println("Rt = Rexterior + Rintermediate + Rinterior = 0.06913259 K/W");
        double energy = ((temperature-7)/0.06913259 * duration);
        System.out.println("E = " +(temperature - 7) + " / 0.06913259 * " + duration + " = " + energy + "J");
        System.out.println();
        System.out.println();
        System.out.println();
        System.out.println("Energy Needed to -5ºC");
        System.out.println("Materials used and its thermal resistances in the Container:");
        System.out.println("Exterior:");
        System.out.println("Steel ; Thermal Resistance : 0.00000259 K/W ; Thickness : 0.010 m");
        System.out.println("Intermediate:");
        System.out.println("Polyurethane foam ; Thermal Resistance : 0.0756 K/W ; Thickness : 0.14 m");
        System.out.println("Interior:");
        System.out.println("Polypropylene ; Thermal Resistance : 0.00613 K/W ; Thickness : 0.05 m");
        System.out.println();
        System.out.println("E=∆T/RT * t");
        System.out.println("∆T = " + temperature + " - (-5) = " + (temperature -(-5)) + "K");
        System.out.println("Rt = Rexterior + Rintermediate + Rinterior = 0.08173259 K/W");
        energy = ((temperature-(-5))/0.08173259 * duration);
        System.out.println("E = " +(temperature -(-5)) + " / 0.06913259 * " + duration + " = " + energy + " J");

        if(nContainers >1)
        {
            System.out.println();
            System.out.println("Etotal = nContainers * Econtainer = " + energy*nContainers + " J");
        }


    }
}
