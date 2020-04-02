package cleanup;

import java.util.Arrays;

public class Vertex {
    protected int numDimensions;
    protected int ID;
    protected int index;
    protected double[] coordinates;
    protected boolean srcChecked = false;
//    protected boolean visited = false;

    public Vertex(int ID, double[] coordinates){
        numDimensions = coordinates.length;
        this.coordinates = coordinates;
        this.index = this.ID = ID;
    }

    public Vertex copy(){
        return new Vertex(this.ID, Arrays.copyOf(this.coordinates, this.coordinates.length));
    }

    public String getCoordinates(){
        String formattedCoordinates = "";
        for(int i = 0; i < numDimensions-1; i++){
            formattedCoordinates = formattedCoordinates + coordinates[i] + ",";
        }
        formattedCoordinates = formattedCoordinates + coordinates[numDimensions-1];

        return formattedCoordinates;
    }

    @Override
    public String toString() {
        String formattedCoordinates = "Vertex #" + this.ID + ": (";

        for(int i = 0; i < numDimensions-1; i++){
            formattedCoordinates = formattedCoordinates + coordinates[i] + ", ";
        }
        formattedCoordinates = formattedCoordinates + coordinates[numDimensions-1] + ")";

        return formattedCoordinates;
    }

    public boolean equals(Vertex v) {
        return this.ID == v.ID;
    }
}
