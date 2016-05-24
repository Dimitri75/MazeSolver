package graph;

import element.Location;
import interfaces.ILocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dimitri on 21/10/2015.
 */
public class Vertex implements ILocation, Comparable<Vertex> {
	int x, y;
	private Map<Vertex, Vertex> mapPrevious;
	private double minDistance = Double.POSITIVE_INFINITY;
	private ArrayList<Edge> adjacencies;

	public Vertex(int x, int y) {
		this.x = x;
		this.y = y;
		adjacencies = new ArrayList<>();
	}

	public ArrayList<Edge> getAdjacencies(){
		return adjacencies;
	}

	public void setMinDistance(double minDistance) {
		this.minDistance = minDistance;
	}

	public double getMinDistance() {
		return minDistance;
	}

	public void setMapPrevious(Map<Vertex, Vertex> mapPrevious) {
		this.mapPrevious = mapPrevious;
	}

	public Map<Vertex, Vertex> getMapPrevious() {
		return mapPrevious;
	}

    public void addPrevious(Vertex previous, Vertex explorator){
        if (mapPrevious == null)
            mapPrevious = new HashMap<>();

        mapPrevious.put(explorator, previous);
    }

    public Vertex getPrevious(Vertex explorator){
        return (mapPrevious != null) ? mapPrevious.get(explorator) : null;
    }

	public Location getLocation(){
		return new Location(x, y);
	}

	@Override
    public int compareTo(Vertex vertex) {
		return Double.compare(minDistance, vertex.minDistance);
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public void setX(int x) {
		this.x = x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public void setY(int y) {
		this.y = y;
	}
}