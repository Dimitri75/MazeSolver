package graph;

import enumerations.EnumGraph;

/**
 * Created by Dimitri on 21/10/2015.
 */
public class Edge {
	private Vertex source;
	private Vertex target;
	private double weight;

	public Edge(Vertex source, Vertex target, EnumGraph movementSpeed) {
		this.source = source;
		this.target = target;

		switch (movementSpeed) {
		case SPEED_NORMAL:
			this.weight = 1;
			break;
		case SPEED_SLOW:
			this.weight = 2;
			break;
		default:
			this.weight = 1;
			break;
		}

		source.getAdjacencies().add(this);
	}
	
	public Vertex getSource(){
		return source;
	}
	
	public Vertex getTarget(){
		return target;
	}
	
	public double getWeight(){
		return weight;
	}
}