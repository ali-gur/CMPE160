import java.io.*;
import java.util.*;

public class AliGur {
    public static String fileName = "input01.txt";
    public static int n;
    public static int chosenMethod;
    public static int chosenGraph;
    public static long startTime;
    public static long endTime;
    public static long elapsedTime;
    public static double alpha;
    public static double beta;
    public static double degradationFactor;
    public static double Q;
    public static double initialPheromoneDensity;
    public static int iterationCount;
    public static int antCount;
    public static double[][] weightMatrix;
    public static double[][] pheromoneMatrix;
    public static double bestDistance = Double.POSITIVE_INFINITY;
    public static double bruteBestDistance = Double.POSITIVE_INFINITY;
    public static ArrayList<Integer> bestPath = new ArrayList<>();
    public static ArrayList<Double> bestDistances = new ArrayList<>();
    public static ArrayList<double[]> coordinates = new ArrayList<>();
    private static ArrayList<Integer> bruteBestPath = new ArrayList<>();

    /**
     * The main method of the program.
     * It initializes the graphical environment, loads data, and runs either the brute-force method or the ant colony optimization method based on the chosen method.
     * After finding the shortest path, it displays the result and the time taken to find it.
     *
     * @param args Command-line arguments (not used in this program)
     */
    public static void main(String[] args) {
        StdDraw.setXscale(0, 1);
        StdDraw.setYscale(0, 1);

        StdDraw.enableDoubleBuffering(); // Enable double buffering for smoother graphics rendering

        loadData();
        initializeWeightMatrix();

        chosenMethod = 2; // Set the chosen method (1 for brute-force, 2 for ant colony optimization)
        chosenGraph = 2; // Set the chosen graph type (1 for displaying the final path, 2 for displaying the pheromone graph)

        if (chosenMethod == 1) {
            // If the brute-force method is chosen
            int[] visited = new int[coordinates.size()]; // Initialize an array to track visited cities
            ArrayList<Integer> path = new ArrayList<>(); // Initialize the path to store the best path found by brute force
            startTime = System.currentTimeMillis();
            bruteForce(visited, path, 0, 0); // Execute the brute-force method to find the shortest path
            endTime = System.currentTimeMillis();
            elapsedTime = endTime - startTime;
            displayPath(bruteBestPath);
            System.out.println("Method: Brute-Force Method");
            System.out.printf("Shortest Distance: %.5f%n", bruteBestDistance);
            System.out.println("Shortest Path: " + bruteBestPath);
            System.out.println("Time it takes to find the shortest path: " + (float) elapsedTime / 1000 + " seconds");
        } else {
            // If the ant colony optimization method is chosen
            setHyperParameters();
            initializePheromoneMatrix();
            startTime = System.currentTimeMillis();
            train(); // Train the ant colony optimization algorithm to find the shortest path
            endTime = System.currentTimeMillis();
            elapsedTime = endTime - startTime;
            reorderPath(); // Reorder the path based on the best path found
            System.out.println("Method: Ant Colony Optimization");
            System.out.printf("Shortest Distance: %.5f%n", bestDistance);
            System.out.println("Shortest Path: " + bestPath);
            System.out.println("Time it takes to find the shortest path: " + (float) elapsedTime / 1000 + " seconds");

            if (chosenGraph == 1) {
                // If the final path is chosen to be displayed
                displayPath(bestPath);
            } else {
                // If the pheromone graph is chosen to be displayed
                displayPheromoneGraph();
            }
        }
    }


    /**
     * Displays the given path on a 2D plane using StdDraw.
     *
     * @param path The path to be displayed, represented as a list of node indices
     */
    private static void displayPath(ArrayList<Integer> path) {
        StdDraw.setPenRadius(0.005); // Set the pen radius for drawing the path

        // Draw lines connecting consecutive cities in the path
        for (int i = 0; i < path.size() - 1; i++) {
            StdDraw.line(
                    coordinates.get(path.get(i) - 1)[0], coordinates.get(path.get(i) - 1)[1], // Start point coordinates
                    coordinates.get(path.get(i + 1) - 1)[0], coordinates.get(path.get(i + 1) - 1)[1] // End point coordinates
            );
        }

        int m = 0; // Initialize a variable to track node indices
        // Draw circles representing nodes on the graph
        for (double[] node : coordinates) {
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            if (m == 0) // Highlight the starting node with a different color
                StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            StdDraw.filledCircle(node[0], node[1], 0.025);
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(node[0], node[1], String.valueOf(m + 1)); // Display the node index next to the circle
            m++; // Increment the node index
        }
        StdDraw.show(); // Show the graph
    }


    /**
     * Sets the hyperparameters used in the ant colony optimization algorithm.
     * These parameters determine the behavior of the algorithm and can be adjusted for different problem instances.
     */
    private static void setHyperParameters() {
        alpha = 0.9; // Alpha parameter controls the influence of pheromone on ant's decision-making
        beta = 2.9; // Beta parameter controls the influence of distance on ant's decision-making
        Q = 0.01; // Q parameter represents the amount of pheromone deposited by each ant
        initialPheromoneDensity = 0.1; // Initial pheromone density on all edges in the graph
        degradationFactor = 0.7; // Factor by which pheromone evaporates or degrades after each iteration
        iterationCount = 100; // Number of iterations or cycles of the algorithm
        antCount = 50; // Number of ants used in each iteration of the algorithm
    }



    /**
     * Loads data from a file into the coordinates list.
     * Each line of the file is expected to contain two comma-separated values representing x and y coordinates.
     *
     * @throws RuntimeException if the file specified by fileName is not found
     */
    private static void loadData() {
        try {
            // Open the file using Scanner
            Scanner scanner = new Scanner(new File(fileName));
            while (scanner.hasNextLine()) {
                // Read each line and split it by comma to extract x and y coordinates
                String[] line = scanner.nextLine().split(",");
                double x = Double.parseDouble(line[0]);
                double y = Double.parseDouble(line[1]);
                // Add the coordinates to the list
                coordinates.add(new double[]{x, y});
            }
            scanner.close(); // Close the scanner to release resources
        } catch (FileNotFoundException e) {
            // Throw a runtime exception if the file is not found
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the weight matrix based on the Euclidean distance between coordinates.
     * The weight matrix represents the distance between each pair of coordinates.
     */
    private static void initializeWeightMatrix() {
        n = coordinates.size(); // Get the number of coordinates
        weightMatrix = new double[n][n]; // Initialize the weight matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Retrieve the coordinates for the current indices
                double[] coordinates1 = coordinates.get(i);
                double[] coordinates2 = coordinates.get(j);
                // Calculate the Euclidean distance between the coordinates
                double distance = Math.sqrt(Math.pow(coordinates1[0] - coordinates2[0], 2) + Math.pow(coordinates1[1] - coordinates2[1], 2));
                // Store the distance in the weight matrix
                weightMatrix[i][j] = distance;
            }
        }
    }


    /**
     * Initializes the pheromone matrix with a uniform initial density.
     * The pheromone matrix represents the amount of pheromone on each edge.
     */
    private static void initializePheromoneMatrix() {
        pheromoneMatrix = new double[n][n]; // Initialize the pheromone matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Set the initial pheromone density for each edge
                pheromoneMatrix[i][j] = initialPheromoneDensity;
            }
        }
    }


    /**
     * Displays a graph representing the pheromone distribution and nodes on a 2D plane.
     * Nodes are represented as filled circles, and pheromone trails between nodes are drawn with varying thickness.
     */
    private static void displayPheromoneGraph() {
        // Draw pheromone trails between nodes
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Retrieve coordinates of the nodes
                double[] coordinates1 = coordinates.get(i);
                double[] coordinates2 = coordinates.get(j);
                // Set pen radius based on pheromone density
                StdDraw.setPenRadius(0.1 * pheromoneMatrix[i][j]);
                // Draw line between nodes
                StdDraw.line(coordinates1[0], coordinates1[1], coordinates2[0], coordinates2[1]);
            }
        }

        // Display nodes on the graph
        int m = 0;
        for (double[] node : coordinates) {
            // Draw filled circle representing each node
            StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
            StdDraw.filledCircle(node[0], node[1], 0.025);
            // Display node index next to the circle
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.text(node[0], node[1], String.valueOf(m + 1));
            m++;
        }

        StdDraw.show(); // Show the graph
    }


    /**
     * Reorders the best path to start with the first node (node 1) and end with it,
     * ensuring that the path remains valid.
     */
    private static void reorderPath() {
        // Shift node indices by 1 to match human-readable numbering
        for (int i = 0; i < bestPath.size(); i++) {
            bestPath.set(i, bestPath.get(i) + 1);
        }

        // Initialize variables
        int count = 0;
        int i = 0;
        ArrayList<Integer> newBestPath = new ArrayList<>();

        // Reorder the path
        while (count < 2) {
            if (count == 0 && bestPath.get(i) != 1) {
                i++; // Skip until the first node is found
            }
            if (count == 0 && bestPath.get(i) == 1) {
                newBestPath.add(bestPath.get(i)); // Add the first node to the new path
                i++;
                count++; // Increment count
            }
            if (count == 1 && bestPath.get(i % bestPath.size()) == 1) {
                newBestPath.add(1); // Add the first node to the end of the path
                count++; // Increment count
            }
            if (count == 1) {
                // Add the remaining nodes to the new path
                if (i == n) {
                    i++;
                    continue;
                }
                newBestPath.add(bestPath.get(i % bestPath.size()));
                i++;
            }
        }

        // Update the best path with the reordered path
        bestPath = newBestPath;
    }

    /**
     * Trains the ant colony optimization algorithm by running a specified number of iterations.
     * In each iteration, ants construct solutions by traversing the graph and laying pheromone trails.
     */
    private static void train() {
        // Run a specified number of iterations
        for (int i = 0; i < iterationCount; i++) {
            cycleAnts(); // Construct solutions by ants
        }
    }

    /**
     * Performs a cycle of ant behavior by letting each ant construct a solution.
     * After all ants have completed their cycles, the pheromone density is degraded, and the best solution length is recorded.
     */
    private static void cycleAnts() {
        // Let each ant construct a solution
        for (int i = 0; i < antCount; i++) {
            cycleAnt(); // Construct a solution with the current ant
        }

        degradePheromoneDensity();

        // Record the length of the best solution found in this iteration
        bestDistances.add(bestDistance);
    }

    /**
     * Degrades the pheromone density on all edges in the pheromone matrix.
     * This is done by multiplying the existing pheromone density by (1 - degradationFactor).
     */
    private static void degradePheromoneDensity() {
        // Iterate over each edge in the pheromone matrix
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                // Degrade the pheromone density on the edge
                pheromoneMatrix[i][j] *= (1 - degradationFactor);
            }
        }
    }


    /**
     * Represents a single cycle of an ant constructing a solution.
     * The ant selects the next node to visit based on probabilities and updates the solution path accordingly.
     * After completing the cycle, the pheromone matrix is updated, and the best solution length and path are updated if necessary.
     */
    private static void cycleAnt() {
        double distance = 0;
        ArrayList<Integer> path = new ArrayList<>();
        Random rand = new Random();
        int nextNode = rand.nextInt(n); // Select a random starting node
        int startingNode = nextNode; // Store the starting node
        int previousNode = 0; // Initialize the previous node
        int[] visited = new int[n]; // Initialize an array to track visited cities
        double[] probabilities; // Initialize an array to store probabilities for selecting the next node

        // While there are still unvisited nodes
        while (nextNode > -1) {
            path.add(nextNode); // Add the current node to the path
            visited[nextNode] = 1; // Mark the current node as visited
            probabilities = calculateProbabilities(nextNode, visited); // Calculate probabilities for selecting the next node
            previousNode = nextNode; // Update the previous node
            nextNode = findNext(probabilities, n); // Select the next node based on probabilities

            // If there is a next node, update the distance of the path
            if (nextNode > -1) {
                distance += weightMatrix[previousNode][nextNode];
            }
        }

        distance += weightMatrix[startingNode][previousNode]; // Add the distance from the last node back to the starting node
        path.add(startingNode); // Add the starting node to complete the path
        updatePheromoneMatrix(path, distance); // Update the pheromone matrix based on the ant's path

        // Update the best solution distance and path if the current path is better
        if (distance < bestDistance) {
            bestDistance = distance;
            bestPath = path;
        }
    }

    /**
     * Calculates the probabilities of selecting each unvisited node as the next destination for an ant.
     * Probabilities are based on the amount of pheromone on the edge and the inverse of the distance to the node.
     *
     * @param next The index of the current node
     * @param visited An array indicating which nodes have been visited (1 for visited, 0 for unvisited)
     * @return An array of probabilities for selecting each unvisited node
     */
    private static double[] calculateProbabilities(int next, int[] visited) {
        double[] probabilities = new double[n]; // Initialize an array to store probabilities
        double sum = 0; // Initialize a variable to store the sum of probabilities

        // Calculate probabilities for each unvisited node
        for (int i = 0; i < n; i++) {
            if (visited[i] == 1) {
                probabilities[i] = 0; // Set probability to 0 for visited cities
            } else {
                // Calculate probability based on pheromone level and distance to the node
                probabilities[i] = Math.pow(pheromoneMatrix[next][i], alpha) / Math.pow(weightMatrix[next][i], beta);
                sum += probabilities[i]; // Update the sum of probabilities
            }
        }

        // Normalize probabilities
        for (int i = 0; i < n; i++) {
            probabilities[i] /= sum; // Divide each probability by the sum to ensure they sum up to 1
        }

        return probabilities; // Return the array of probabilities
    }


    /**
     * Finds the index of the next node to visit based on probabilities.
     * Randomly selects a node based on the given probabilities.
     *
     * @param probabilities An array of probabilities for selecting each node
     * @param n The total number of nodes
     * @return The index of the next node to visit
     */
    private static int findNext(double[] probabilities, int n) {
        int next = -1;
        Random random = new Random();
        double randomNumber = random.nextDouble(); // Generate a random number between 0 and 1
        double sum = 0; // Initialize a variable to track the cumulative sum of probabilities

        // Iterate over each node's probability
        for (int i = 0; i < n; i++) {
            sum += probabilities[i]; // Add the current probability to the sum
            // If the cumulative sum exceeds the random number and the probability is not 0,
            // set the index of the next node and exit the loop
            if (sum > randomNumber && probabilities[i] != 0) {
                next = i;
                break;
            }
        }

        return next; // Return the index of the next node to visit
    }


    /**
     * Updates the pheromone matrix based on the ant's path and the length of the path.
     * Pheromone levels are adjusted using a delta value calculated from the quality of the path.
     *
     * @param path The ant's path, represented as a list of node indices
     * @param length The length of the ant's path
     */
    private static void updatePheromoneMatrix(ArrayList<Integer> path, double length) {
        int n = path.size(); // Get the number of nodes in the path
        double delta = Q / length; // Calculate the delta value based on the quality of the path

        // Update pheromone levels on each edge of the path
        for (int i = 0; i < n - 1; i++) {
            // Update pheromone levels for the edge between consecutive nodes
            pheromoneMatrix[path.get(i)][path.get(i + 1)] += delta;
            pheromoneMatrix[path.get(i + 1)][path.get(i)] += delta;
        }
    }


    /**
     * Performs a brute-force search to find the shortest Hamiltonian cycle starting from the specified node.
     * This method recursively explores all possible paths and updates the best path and length found so far.
     *
     * @param visited An array indicating which nodes have been visited (1 for visited, 0 for unvisited)
     * @param path The current path being explored
     * @param node The index of the current node being visited
     * @param length The length of the current path
     */
    private static void bruteForce(int[] visited, ArrayList<Integer> path, int node, double length) {
        // If the current path length exceeds the best length found so far, stop exploring this path
        if (length > bruteBestDistance)
            return;

        // Mark the current node as visited and add it to the current path
        visited[node] = 1;
        path.add(node + 1);

        boolean allVisited = true; // Flag to indicate if all nodes have been visited in this path

        // Explore all unvisited nodes from the current node
        for (int i = 0; i < n; i++) {
            if (visited[i] == 0) {
                double updatedLength = length + weightMatrix[node][i]; // Calculate the length of the path if node 'i' is visited next
                bruteForce(visited, path, i, updatedLength); // Recursively explore the path with node 'i' visited next
                allVisited = false; // At least one unvisited node found, so the exploration is not finished
            }
        }

        // If all nodes have been visited, complete the path by returning to the starting node
        if (allVisited) {
            length += weightMatrix[node][0]; // Add the distance from the last node back to the starting node
            path.add(1); // Add the starting node to complete the path
            // If the length of the current path is shorter than the best length found so far, update the best path and length
            if (length < bruteBestDistance) {
                bruteBestDistance = length;
                bruteBestPath = new ArrayList<>(path);
            }
            path.remove(path.size() - 1); // Remove the last added node from the path
        }

        // backtracking
        // Remove the current node from the path and mark it as unvisited
        path.remove(path.size() - 1);
        visited[node] = 0;
    }
}