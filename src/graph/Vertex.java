package graph;

import element.Location;
import interfaces.ILocation;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Created by  EquipeLabyrinthe on 21/05/2016.
 */
public class Vertex implements ILocation, Comparable<Vertex> {
	int x, y;
	private Map<Vertex, Vertex> mapPrevious;
	private double minDistance = Double.POSITIVE_INFINITY;
	private ArrayList<Edge> adjacencies;
	public boolean visited;
	public int id = -1; //Utilisé par kruskal
	public Vertex pathParent;
	double cost;
	double estimatedCost;


	public Vertex(int x, int y) {
		this.x = x;
		this.y = y;
		adjacencies = new ArrayList<>();
		visited = false;
	}

	public double getCost(Vertex node){
		return cost;
	}

	public double distanceEuclidienne(Vertex start, Vertex destination){
		double dx = abs(start.getX()-destination.getX());
		double dy = abs(start.getY()-destination.getY());

		double euclidienne = sqrt(dx*dx+dy*dy);
		return euclidienne;
	}



	public ArrayList<Edge> getAdjacencies(){
		return adjacencies;
	}

	public ArrayList<Vertex> getNeighbors(){
		ArrayList<Vertex> neighbors = new ArrayList<>();
		for (Edge edge : adjacencies)
			neighbors.add(sameLocation(this, edge.getSource()) ? edge.getTarget() : edge.getSource());

		return neighbors;
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

	public static boolean sameLocation(Vertex v1, Vertex v2){
		return v1.getX() == v2.getX() && v1.getY() == v2.getY();
	}
}