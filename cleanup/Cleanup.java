package cleanup;

import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Cleanup {
    protected int DIMENSION_SIZE;
    protected int vertexIndex, verticesEndIndex;
    protected ArrayList<Vertex> startVertices;
    protected ArrayList<Vertex> vertices;
    protected Vertex[] path, shortestPath;
    protected int pathIndex;
    protected double pathDistance, shortestPathDistance;

    public Cleanup(){
        initializePoints();
        findRandPaths(47);
        //findShortestPath();
        writePath();
        System.out.println("----------------------------------------------------");
        System.out.println("Shortest path generated and written to solutions.txt");
        System.out.println("\nShortest traversal starts @ " + shortestPath[0]);
        System.out.println("Path distance: " + shortestPathDistance);
        //System.out.println("LAST: " + calculateDistance(shortestPath[0], shortestPath[shortestPath.length-1]));
    }

    /**
     * Populates the ArrayLists of vertices using points.txt
     */
    public void initializePoints(){
        vertices = new ArrayList<Vertex>();
        vertexIndex = 0;
        try{
            String[] sValues;
            String currentPoint;
            double[] values;
            Scanner input = new Scanner(new File("points.txt"));

            while(input.hasNextLine()){
                currentPoint = input.nextLine();
                sValues = currentPoint.split(",");
                values = new double[sValues.length];
                for(int i = 0; i < sValues.length; i++){
                    values[i] = Double.parseDouble(sValues[i]);
                }
                vertices.add(new Vertex(vertexIndex, values));
                vertexIndex++;
            }
            DIMENSION_SIZE = vertices.get(0).numDimensions;
            System.out.println("Dimension size is: " + DIMENSION_SIZE);
            System.out.println("All points initialized \n");
        }
        catch(IOException io){
            io.printStackTrace();
        }

        path = new Vertex[vertices.size()];
        shortestPath = new Vertex[vertices.size()];
        startVertices = new ArrayList<Vertex>();

        for(int i = 0; i < vertices.size(); i++){
            startVertices.add(vertices.get(i).copy());
        }
    }


    public void findRandPaths(int numChecks){
        Random rand = new Random();
        Vertex randSrcVertex;
        shortestPathDistance = Integer.MAX_VALUE;

        for(int i = 0; i < numChecks; i++) {
            randSrcVertex = startVertices.get(rand.nextInt(path.length));
            // Won't use a vertex that has already been used as a starting point before
            while(randSrcVertex.srcChecked){
                randSrcVertex = startVertices.get(rand.nextInt(path.length));
            }
            System.out.println("STARTING @ " + randSrcVertex);
            if(findPath(randSrcVertex) < shortestPathDistance){
                copyPath();
                shortestPathDistance = pathDistance;
            }
            randSrcVertex.srcChecked = true;
            System.out.println();
        }
    }

    /**
     * Generates a shortest path by calling findPath starting from every
     * possible vertex, the best possible starting vertex is found as it
     * is the one with the smallest resulting pathDistance
     */
    public void findShortestPath(){
        shortestPathDistance = Integer.MAX_VALUE;
        for(Vertex v : startVertices){
            System.out.println("STARTING @ " + v);
            if(findPath(v) < shortestPathDistance){
                copyPath();
                shortestPathDistance = pathDistance;
            }
            System.out.println();
        }
    }

    /**
     * Calculates the shortest path that visits each point from a given starting
     * position. path[] is updated to store the order the that points were visited
     * with path[0] always being set to startVertex
     * @param startVertex The point traversal begins from
     * @return The distance of the path taken
     */
    public double findPath(Vertex v){
        Vertex startVertex = findEqual(v);
        int status5, status10, status25, status50, status75, status95;
        status50 = path.length / 2; status5 = status50 / 10; status10 = status5 *2;
        status25 = status50 / 2; status75 = status25 * 3; status95 = path.length - status5;
        pathDistance = pathIndex = 0;
        verticesEndIndex = vertices.size() - 1;

        // Copies the start vertex into path[0]
        path[pathIndex++] = startVertex.copy();
        swap(startVertex.index, verticesEndIndex);
        verticesEndIndex--;

        System.out.println("Calculating path . . .");
        for(int i = 0; i < path.length-1; i++){
            if(i == status5){System.out.print("[5%->");}
            else if(i == status10){System.out.print("10%->");}
            else if(i == status25){System.out.print("25%->");}
            else if(i == status50){System.out.print("50%->");}
            else if(i == status75){System.out.print("75%->");}
            else if(i == status95){System.out.print("95%->");}
            findClosetPoint(path[i]);
        }
        // Distance from last point back to the first
        pathDistance += calculateDistance(startVertex, path[pathIndex-1]);
        System.out.println("100%]");

        return pathDistance;
    }

    /**
     * Finds and sets path[pathIndex] to the closest point from startVertex
     * @param startVertex The point all distances are calculated from
     */
    public void findClosetPoint(Vertex startVertex){
        double distanceToNext = Integer.MAX_VALUE;
        double currentDistance;
        Vertex currentPoint, closestPoint = null;

        for(int i = 0; i <= verticesEndIndex; i++){
            currentPoint = vertices.get(i);
            currentDistance = calculateDistance(startVertex, currentPoint);
            if(currentDistance < distanceToNext){
                distanceToNext = currentDistance;
                closestPoint = currentPoint;
            }
        }

        // Vertex is added to path and won't be selected again
        path[pathIndex++] = closestPoint.copy();
        pathDistance += distanceToNext;
        swap(closestPoint.index, verticesEndIndex);
        verticesEndIndex--;
    }

//    /**
//     * Resets the the visited condition of every vertex
//     * @param state True or False
//     */
//    public void setVisit(boolean state){
//        for(int i = 0; i <= verticesEndIndex; i++){
//            vertices.get(i).visited = state;
//        }
//    }

    /**
     * Swaps the vertices at the specified indexes and updates
     * their index variables
     * @param indexA The first index
     * @param indexB The other index
     */
    public void swap(int indexA, int indexB){
        Vertex a = vertices.get(indexA);
        Vertex b = vertices.get(indexB);
        a.index = indexB;
        b.index = indexA;

        vertices.set(indexB, a);
        vertices.set(indexA, b);
        //System.out.println("Swap made between " + a + " <-> " + b);
    }

    /**
     * Copies a path into shortestPath array
     */
    public void copyPath(){
        for(int i = 0; i < path.length; i++){
            shortestPath[i] = path[i];
        }
    }

    /**
     * Calculates the distance between vertex a and vertex b
     * @param a The first vertex
     * @param b The other vertex
     * @return The distance between the two vertices
     */
    public double calculateDistance(Vertex a, Vertex b){
        double currAxisValue;
        double axisSum = 0;
        for(int i = 0; i < DIMENSION_SIZE; i++){
            currAxisValue = b.coordinates[i] - a.coordinates[i];
            axisSum += (currAxisValue * currAxisValue);
        }
        return Math.sqrt(axisSum);
    }

    /**
     * Given a vertex, iterates through all vertices until a vertex
     * with a matching ID to the source vertex is found
     * @param a The source vertex
     * @return A reference to the "equal" vertex
     */
    public Vertex findEqual(Vertex a){
        for(Vertex b : vertices){
            if(b.equals(a)){
                return b;
            }
        }
        return null;
    }

    /**
     * Writes the points from ArrayList shortestPath which are
     * in traversal order to solution.txt
     */
    public void writePath(){
        FileWriter fw;
        try{
            fw = new FileWriter(new File("solution.txt"));
            for(int i = 0; i < shortestPath.length; i++){
                fw.write(shortestPath[i].getCoordinates() + System.getProperty("line.separator"));
            }
            fw.close();
        }
        catch(IOException io){
            io.printStackTrace();
        }
    }

    /**
     * Iterates through and prints every point and its coordinates
     */
    public void showVertices(){
        for(Vertex v : vertices){
            System.out.println(v);
        }
    }
}
