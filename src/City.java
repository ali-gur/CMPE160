import java.util.ArrayList;

/**
 * Represents a city with a name, coordinates, distance, neighbors, path, and visited status.
 * Provides methods to calculate the distance between this city and a neighboring city.
 */
public class City {
    // Instance variables
    public String name;             // Name of the city
    public double x;                // x-coordinate of the city
    public double y;                // y-coordinate of the city
    public double distance;         // Distance from the starting city
    public ArrayList<City> neighbors;   // List of neighboring cities
    public ArrayList<City> path;       // Path from the starting city to this city
    public boolean visited;         // Indicates if the city has been visited during traversal

    // Public constructor
    /**
     * Constructs a new City with the given name, x-coordinate, and y-coordinate.
     * Initializes distance to infinity, neighbors to an empty ArrayList, path to an empty ArrayList,
     * and visited status to false.
     *
     * @param name The name of the city.
     * @param x The x-coordinate of the city.
     * @param y The y-coordinate of the city.
     */
    public City(String name, double x, double y) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.distance = Double.POSITIVE_INFINITY; // Initialize distance to infinity
        this.neighbors = new ArrayList<>(); // Initialize neighbors list
        this.path = new ArrayList<>(); // Initialize path list
        this.visited = false; // Initialize visited status
    }

    // Public method
    /**
     * Calculates the distance between this city and a neighboring city.
     *
     * @param neighborCity The neighboring city.
     * @return The Euclidean distance between this city and the neighboring city.
     */
    public double calculateDistance(City neighborCity) {
        return Math.sqrt(Math.pow(neighborCity.x - this.x, 2) + Math.pow(neighborCity.y - this.y, 2));
    }
}

