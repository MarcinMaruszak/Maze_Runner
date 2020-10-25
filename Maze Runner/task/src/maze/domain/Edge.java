package maze.domain;

import java.io.Serializable;
import java.util.Objects;

public class Edge implements Serializable {
    private int startNode;
    private int endNode;
    private int weight;

    public Edge(int startNode, int endNode, int weight) {
        this.startNode = startNode;
        this.endNode=endNode;
        this.weight = weight;
    }

    public int getEndNode() {
        return endNode;
    }

    public int getStartNode() {
        return startNode;
    }

    public int getWeight() {
        return weight;
    }


    @Override
    public String toString() {
        return startNode +","+endNode +":" +weight +" ";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Edge edge1 = (Edge) o;
        return startNode == edge1.startNode&&endNode==edge1.endNode||
                startNode == edge1.endNode&&endNode==edge1.startNode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startNode);
    }
}

