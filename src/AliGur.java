/**
 * @author Ali Gur Student ID: 2022400120
 * @since 04.03.2024
 */

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class AliGur {
    public static double width = 2377;
    public static double height = 1055;
    public static ArrayList<City> cities = new ArrayList<>();
    public static City startingCity = null, destinationCity = null;

    /**
     * The main method for running the navigation system.
     * It loads data, displays the map, prompts the user to enter starting and destination cities,
     * performs Dijkstra's algorithm to find the shortest path, and displays the navigation path on the map.
     *
     * @param args The command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        // Load data from files
        loadData();

        // Display the map
        showMap();

        // Prompt the user to enter the starting city
        askStartingCity();

        // Prompt the user to enter the destination city
        askDestinationCity();

        // Perform Dijkstra's algorithm to find the shortest path
        dijkstra();

        // Display the navigation path on the map
        showNavigation();
    }
    /**
     * Loads city coordinates and connections from external files.
     * Reads data from files named "city_coordinates.txt" and "city_connections.txt"
     * Coordinates are loaded into City objects and added to the 'cities' ArrayList.
     * Connections between cities are established by updating the 'neighbors' list of each city.
     * If the files are not found or there is an error while reading, a RuntimeException is thrown.
     */
    public static void loadData() {
        // Load city coordinates from the "city_coordinates.txt" file
        File coordinates = new File("city_coordinates.txt");
        try {
            Scanner scanner = new Scanner(coordinates);
            // Parse each line of the file
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                // Create a new City object with the parsed coordinates
                City city = new City(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
                // Add the City object to the 'cities' ArrayList
                cities.add(city);
            }
        } catch (FileNotFoundException e) {
            // If the file is not found, throw a RuntimeException
            throw new RuntimeException(e);
        }

        // Load city connections from the "city_connections.txt" file
        File connections = new File("city_connections.txt");
        try {
            // Initialize variables to store the first and second cities of each connection
            City firstCity = null;
            City secondCity= null;
            Scanner scanner = new Scanner(connections);
            // Parse each line of the file
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                String firstCityName = parts[0];
                String secondCityName = parts[1];

                // Iterate through the 'cities' ArrayList to find the first and second cities
                for (City city : cities) {
                    if (city.name.equals(firstCityName))
                        firstCity = city;
                    if (city.name.equals(secondCityName))
                        secondCity = city;
                }

                // Add the second city to the neighbors list of the first city if not already present
                if (!firstCity.neighbors.contains(secondCity)) {
                    firstCity.neighbors.add(secondCity);
                }

                // Add the first city to the neighbors list of the second city if not already present
                if (!secondCity.neighbors.contains(firstCity)) {
                    secondCity.neighbors.add(firstCity);
                }
            }
        } catch (FileNotFoundException e) {
            // If the file is not found, throw a RuntimeException
            throw new RuntimeException(e);
        }
    }


    /**
     * Displays a map with cities and their connections.
     * City coordinates and names are displayed along with connections between cities.
     */
    private static void showMap() {
        // Set canvas size and scale for drawing
        StdDraw.setCanvasSize(2377/2, 1055/2);
        StdDraw.setXscale(0, 2377);
        StdDraw.setYscale(0, 1055);

        // Enable double buffering for smooth rendering
        StdDraw.enableDoubleBuffering();

        // Load map image and draw it on the canvas
        StdDraw.picture(width/2, height/2, "map.png", width, height);

        // Set pen color and font for city names
        StdDraw.setPenColor(Color.GRAY);
        StdDraw.setFont(new Font("Serif", Font.BOLD, 12));

        // Iterate through each city
        for (City firstCity : cities) {
            // Draw filled circle representing the city
            StdDraw.filledCircle(firstCity.x, firstCity.y, 5);
            // Display city name above the circle
            StdDraw.text(firstCity.x , firstCity.y + 20, firstCity.name);
            // Iterate through each neighbor of the city
            for (City secondCity : firstCity.neighbors) {
                // Draw a line connecting the current city to its neighbor
                StdDraw.line(firstCity.x, firstCity.y, secondCity.x, secondCity.y);
            }
        }
        // Show the drawn map
        StdDraw.show();
    }

    /**
     * Prompts the user to enter a starting city.
     * Reads the user input from the console and searches for the corresponding city in the 'cities' ArrayList.
     * If the city is not found, repeatedly prompts the user until a valid city name is entered.
     */
    private static void askStartingCity() {
        // Create a Scanner object to read user input from the console
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter a starting city
        System.out.print("Enter starting city: ");
        String startingCityName = scanner.nextLine();

        // Search for the starting city in the 'cities' ArrayList
        for (City city : cities) {
            if (city.name.equalsIgnoreCase(startingCityName))
                startingCity = city;
        }

        // If the starting city is not found, repeatedly prompt the user until a valid city name is entered
        if (startingCity == null) {
            do {
                // Display an error message and prompt the user to enter a valid city name
                System.out.println("City named '" + startingCityName + "' not found. Please enter a valid city name.");
                System.out.print("Enter starting city: ");
                startingCityName = scanner.nextLine();

                // Search for the starting city again in the 'cities' ArrayList
                for (City city : cities) {
                    if (city.name.equalsIgnoreCase(startingCityName))
                        startingCity = city;
                }
            } while (startingCity == null);
        }
    }

    /**
     * Prompts the user to enter a destination city.
     * Reads the user input from the console and searches for the corresponding city in the 'cities' ArrayList.
     * If the city is not found, repeatedly prompts the user until a valid city name is entered.
     */
    private static void askDestinationCity() {
        // Create a Scanner object to read user input from the console
        Scanner scanner = new Scanner(System.in);

        // Prompt the user to enter a destination city
        System.out.print("Enter destination city: ");
        String destinationCityName = scanner.nextLine();

        // Search for the destination city in the 'cities' ArrayList
        for (City city : cities) {
            if (city.name.equalsIgnoreCase(destinationCityName))
                destinationCity = city;
        }

        // If the destination city is not found, repeatedly prompt the user until a valid city name is entered
        if (destinationCity == null) {
            do {
                // Display an error message and prompt the user to enter a valid city name
                System.out.println("City named '" + destinationCityName + "' not found. Please enter a valid city name.");
                System.out.print("Enter destination city: ");
                destinationCityName = scanner.nextLine();

                // Search for the destination city again in the 'cities' ArrayList
                for (City city : cities) {
                    if (city.name.equalsIgnoreCase(destinationCityName))
                        destinationCity = city;
                }
            } while (destinationCity == null);
        }
    }

    /**
     * Implements Dijkstra's algorithm to find the shortest path from the starting city to the destination city.
     * Updates the distance and path of each city based on the shortest path found.
     */
    private static void dijkstra() {
        // Initialize variables for current and next cities
        City currentCity;
        City nextCity;

        // Set the distance of the starting city to 0 and add it to the path
        startingCity.distance = 0;
        startingCity.path.add(startingCity);
        currentCity = startingCity;

        // Main loop of Dijkstra's algorithm
        while (true) {
            // Mark the current city as visited
            currentCity.visited = true;

            // Update distances of neighbors of the current city
            updateNeighbourDistances(currentCity);

            // Find the next unvisited city with the shortest distance
            nextCity = findNextCity();

            // If the next city is the destination or no more unvisited cities, exit the loop
            if (nextCity == destinationCity || nextCity == null) {
                break;
            } else {
                // Move to the next city
                currentCity = nextCity;
            }
        }
    }

    /**
     * Updates the distances and paths of neighboring cities based on the current city.
     * Calculates the distance from the current city to each neighboring city,
     * updates their distances and paths if a shorter path is found.
     *
     * @param currentCity The current city whose neighbors' distances and paths are being updated.
     */
    public static void updateNeighbourDistances(City currentCity) {
        // Variable to store the current distance from the current city
        double currentDistance;

        // Iterate through each neighbor of the current city
        for (City city : currentCity.neighbors) {
            // Calculate the distance from the current city to the neighbor and add it to the current city's distance
            currentDistance = city.calculateDistance(currentCity) + currentCity.distance;

            // If the calculated distance is shorter than the neighbor's current distance, update it
            if (currentDistance < city.distance) {
                // Update the neighbor's distance
                city.distance = currentDistance;
                // Update the neighbor's path by copying the current city's path and adding the neighbor
                city.path = new ArrayList<>(currentCity.path);
                city.path.add(city);
            }
        }
    }

    /**
     * Finds the next unvisited city with the shortest distance from the starting city.
     * Iterates through all cities, excluding visited ones, and returns the city with the shortest distance.
     *
     * @return The next unvisited city with the shortest distance, or null if all cities are visited.
     */
    public static City findNextCity() {
        // Initialize variables to store the city with the shortest distance and its minimum distance
        City minCity = null;
        double minDist;
        minDist = Double.POSITIVE_INFINITY;

        // Iterate through each city in the 'cities' ArrayList
        for (City city : cities) {
            // Skip visited cities
            if (city.visited) {
                continue;
            }
            // Update the city with the shortest distance if found
            if (city.distance < minDist) {
                minDist = city.distance;
                minCity = city;
            }
        }
        // Return the city with the shortest distance (or null if all cities are visited)
        return minCity;
    }

    /**
     * Displays the navigation path from the starting city to the destination city.
     * Draws the path on the map using StdDraw library and prints the total distance and path string.
     * If no path could be found to the destination city, prints an appropriate message.
     */
    public static void showNavigation() {
        // Set pen color
        StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);

        // Check if a path could be found to the destination city
        if (destinationCity.distance == Double.POSITIVE_INFINITY) {
            // If no path could be found, print a message
            System.out.println("No path could be found.");
        } else {
            // If a path exists, initialize variables for previous city and path string
            City previousCity = destinationCity.path.get(0);
            String pathString = "";

            // Iterate through each city in the path
            for (int i = 0; i < destinationCity.path.size(); i++) {
                if (i == 0) {
                    // For the first city in the path, draw a filled circle and display its name
                    StdDraw.setPenRadius(0.005);
                    StdDraw.filledCircle(destinationCity.path.get(0).x, destinationCity.path.get(0).y, 5);
                    StdDraw.text(destinationCity.path.get(0).x, destinationCity.path.get(0).y + 20, destinationCity.path.get(0).name);
                    pathString += destinationCity.path.get(0).name;
                    continue;
                }
                // Draw the city's name and a filled circle
                StdDraw.text(destinationCity.path.get(i).x, destinationCity.path.get(i).y + 20, destinationCity.path.get(i).name);
                StdDraw.filledCircle(destinationCity.path.get(i).x, destinationCity.path.get(i).y, 5);
                // Draw a line from the previous city to the current city
                StdDraw.line(previousCity.x, previousCity.y, destinationCity.path.get(i).x, destinationCity.path.get(i).y);
                // Add the city's name to the path string
                pathString += " -> " + destinationCity.path.get(i).name;
                // Update the previous city
                previousCity = destinationCity.path.get(i);
            }

            // Show the drawn map
            StdDraw.show();

            // Print the total distance and path string
            System.out.printf("Total distance: %.2f. " + "Path: " + pathString + "\n", destinationCity.distance);
        }
    }
}
