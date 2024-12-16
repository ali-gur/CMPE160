/**
 * Angry Bullets Game
 * @author Ali Gur, Student ID: 2022400120
 * @since Date: 03.17.2024
 */

import java.awt.*;
import java.awt.event.KeyEvent;

public class AliGur {
    static int width = 1600;
    static int height = 800;
    static double gravity = 9.80665;
    static double x0 = 120; // x coordinate of default shooting position
    static double y0 = 120; // y coordinate of default shooting position
    static double bulletVelocity = 180; // default bullet velocity
    static double bulletAngle = 45.0; // default bullet angle
    static double[][] obstacleArray = {
            {1200, 0, 60, 220},
            {1000, 0, 60, 160},
            {600, 0, 60, 80},
            {600, 180, 60, 160},
            {220, 0, 120, 180}
    };
    static double[][] targetArray = {
            {1160, 0, 30, 30},
            {730, 0, 30, 30},
            {150, 0, 20, 20},
            {1480, 0, 60, 60},
            {340, 80, 60, 30},
            {1500, 600, 60, 60}
    };

    public static void main(String[] args) {
        // configure screen size and coordinate system
        StdDraw.setCanvasSize(width, height);
        StdDraw.setXscale(0, width);
        StdDraw.setYscale(0, height);

        StdDraw.setFont(new Font("Serif", Font.BOLD, 20)); // change font

        StdDraw.enableDoubleBuffering(); // for smooth animations

        setUp(); // set up game environment (targets, obstacles etc.)
        play(); // play
    }

    /**
     * Sets up the game environment.
     * This method initializes the platform, targets, obstacles, shooting line.
     */
    public static void setUp() {
        setPlatform();
        setTargets();
        setObstacles();
        setShootingLine();
        StdDraw.show();
    }

    /**
     * Sets up the platform.
     * This method initializes the platform and displays the bullet's angle and velocity.
     */
    public static void setPlatform() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(x0/2, y0/2, x0/2, y0/2);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.textLeft(10, y0 / 2 + 10, "a: " + bulletAngle);
        StdDraw.textLeft(10, y0 / 2 - 10, "v: " + bulletVelocity);
    }

    /**
     * Sets up the targets.
     * This method initializes the targets and draws them on the screen.
     */
    public static void setTargets() {
        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
        double[] target;
        for (double[] doubles : targetArray) {
            target = doubles;
            StdDraw.filledRectangle(target[0] + target[2] / 2, target[1] + target[3] / 2, target[2] / 2, target[3] / 2);
        }
    }

    /**
     * Sets up the obstacles.
     * This method initializes the obstacles and draws them on the screen.
     */
    public static void setObstacles() {
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        double[] obstacle;
        for (double[] doubles : obstacleArray) {
            obstacle = doubles;
            StdDraw.filledRectangle(obstacle[0] + obstacle[2] / 2, obstacle[1] + obstacle[3] / 2, obstacle[2] / 2, obstacle[3] / 2);
        }
    }

    /**
     * Sets up the shooting line.
     * This method initializes the shooting line and draws it on the screen.
     */
    public static void setShootingLine() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.01);
        StdDraw.line(x0, y0, (x0 + bulletVelocity*Math.cos(bulletAngle * (Math.PI / 180))/2), (y0 + bulletVelocity*Math.sin(bulletAngle * (Math.PI / 180))/2));
        StdDraw.setPenRadius(0.002); // default pen size
    }

    /**
     * Initiates the gameplay loop.
     * This method continuously checks for user input and performs necessary actions.
     */
    public static void play() {
        while (true) {
            // Check if a key is pressed
            if (StdDraw.isKeyPressed(KeyEvent.VK_UP)) {
                rotateCounterClockwise();
            } else if (StdDraw.isKeyPressed(KeyEvent.VK_DOWN)) {
                rotateClockwise();
            } else if (StdDraw.isKeyPressed(KeyEvent.VK_RIGHT)) {
                speedUp();
            } else if (StdDraw.isKeyPressed(KeyEvent.VK_LEFT)) {
                speedDown();
            } else if (StdDraw.isKeyPressed(KeyEvent.VK_SPACE)) {
                startAnimation();
            }

            StdDraw.pause(50); // pause duration: 50 ms
        }
    }

    /**
     * Rotates the bullet counterclockwise by one degree.
     * This method increments the bullet's angle by one degree and updates the display accordingly.
     */
    public static void rotateCounterClockwise() {
        bulletAngle += 1; // increment size is 1 degree
        StdDraw.clear();
        setUp();
    }

    /**
     * Rotates the bullet clockwise by one degree.
     * This method decrements the bullet's angle by one degree and updates the display accordingly.
     */
    public static void rotateClockwise() {
        bulletAngle -= 1; // decrement size is 1 degree
        StdDraw.clear();
        setUp();
    }

    /**
     * Increases the speed of the bullet.
     * This method increments the bullet's velocity by 1 unit and updates the display accordingly.
     */
    public static void speedUp() {
        bulletVelocity += 1; // increment size is 1 unit
        StdDraw.clear();
        setUp();
    }

    /**
     * Decreases the speed of the bullet.
     * This method decrements the bullet's velocity by 1 unit and updates the display accordingly.
     */
    public static void speedDown() {
        bulletVelocity -= 1; // decrement size is 1 unit
        StdDraw.clear();
        setUp();
    }

    /**
     * Starts the animation of a bullet's trajectory with given initial parameters.
     * The animation continues until certain conditions are met (e.g., hitting a target, obstacle, ground, or reaching max interval).
     */
    public static void startAnimation() {
        double t = 0.1;
        double previousPositionX;
        double previousPositionY;
        double currentPositionX = x0;
        double currentPositionY = y0;
        StdDraw.filledCircle(currentPositionX, currentPositionY, 4);
        StdDraw.show();
        while (true) {
            previousPositionX = currentPositionX;
            previousPositionY = currentPositionY;
            currentPositionX = x0 + bulletVelocity * Math.cos(bulletAngle * (Math.PI / 180)) * t;
            currentPositionY = y0 + bulletVelocity * Math.sin(bulletAngle * (Math.PI / 180)) * t - 0.5 * gravity * 3 * t * t;
            StdDraw.filledCircle(currentPositionX, currentPositionY, 4);
            StdDraw.line(previousPositionX, previousPositionY, currentPositionX, currentPositionY);
            t += 0.1;

            if (hitTarget(currentPositionX, currentPositionY)) {
                StdDraw.textLeft(10, height - 20, "Congratulations: You hit the target!");
                StdDraw.show();
                waitResponse();
            }

            if (hitObstacle(currentPositionX, currentPositionY)) {
                StdDraw.textLeft(10, height - 20, "Hit an obstacle. Press 'r' to shoot again.");
                StdDraw.show();
                waitResponse();
            }

            if (touchedGround(currentPositionY)) {
                StdDraw.textLeft(10, height - 20, "Hit the ground. Press 'r' to shoot again.");
                StdDraw.show();
                waitResponse();
            }

            if (exceededMaxInterval(currentPositionX)) {
                StdDraw.textLeft(10, height - 20, "Max X reached. Press 'r' to shoot again.");
                StdDraw.show();
                waitResponse();
            }

            StdDraw.show();

            StdDraw.pause(20); // pause duration: 20 ms
        }

    }

    /**
     * Checks if the given current position (X, Y) hits any target in the target array.
     *
     * @param currentPositionX The X coordinate of the current position.
     * @param currentPositionY The Y coordinate of the current position.
     * @return true if the current position hits any target, false otherwise.
     */
    public static boolean hitTarget(double currentPositionX, double currentPositionY) {
        for (int i = 0; i < targetArray.length; i++) {
            double[] target = targetArray[i];
            double left = target[0];
            double right = target[0] + target[2];
            double bottom = target[1];
            double top = target[1] + target[3];

            // check whether position of the bullet in target
            if (left < currentPositionX && right > currentPositionX && bottom < currentPositionY && top > currentPositionY) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given current position (X, Y) hits any obstacle in the obstacle array.
     *
     * @param currentPositionX The X coordinate of the current position.
     * @param currentPositionY The Y coordinate of the current position.
     * @return true if the current position hits any obstacle, false otherwise.
     */
    public static boolean hitObstacle(double currentPositionX, double currentPositionY) {
        double[] obstacle;
        double left, right, bottom, top;
        for (int i = 0; i < obstacleArray.length; i++) {
            obstacle = obstacleArray[i];
            left = obstacle[0];
            right = obstacle[0] + obstacle[2];
            bottom = obstacle[1];
            top = obstacle[1] + obstacle[3];

            // check whether position of the bullet in obstacle
            if (left < currentPositionX && right > currentPositionX && bottom < currentPositionY && top > currentPositionY ) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the given current Y position touches the ground.
     *
     * @param currentPositionY The Y coordinate of the current position.
     * @return true if the current Y position touches the ground, false otherwise.
     */
    public static boolean touchedGround(double currentPositionY) {
        return currentPositionY < 0;
    }

    /**
     * Checks if the given current X position exceeds the maximum interval.
     *
     * @param currentPositionX The X coordinate of the current position.
     * @return true if the current X position exceeds the maximum interval, false otherwise.
     */
    public static boolean exceededMaxInterval(double currentPositionX) {
        return currentPositionX > width;
    }

    /**
     * Waits for a response from the user after a certain event (e.g., hitting an obstacle or target).
     * This method listens for the 'R' key press and resets the bullet's angle and velocity before restarting the game.
     */
    public static void waitResponse() {
        while (true) {
            if (StdDraw.isKeyPressed(KeyEvent.VK_R)) {
                bulletAngle = 45.0; // reset bullet angle
                bulletVelocity = 180; // reset bullet velocity
                StdDraw.clear(); // clear the canvas
                setUp(); // set up the game environment again
                play(); // play again
            }
            StdDraw.pause(20); // pause duration: 20 ms
        }
    }
}