package maze.domain;

import javax.print.attribute.standard.NumberOfDocuments;
import java.io.Serializable;
import java.util.*;

public class Maze implements Serializable {
    private int[][] maze;
    private int height;
    private int width;
    private ArrayList<LinkedHashSet<Edge>> adjacencyList;
    private LinkedHashSet<Edge> tree;
    private boolean[][] wasHere;
    private boolean[][] goodPath;

    public Maze() {
    }

    public void setMaze(int[] size) {
        this.height = (size[0] - 1) / 2;
        this.width = (size[1] - 1) / 2;
        this.maze = new int[size[0]][size[1]];
        this.adjacencyList = new ArrayList<>();
        this.tree = new LinkedHashSet<>();
        //this.escape = new ArrayDeque<>();
    }

    public void setMaze(int size) {
        int[] ints = new int[]{size, size};
        setMaze(ints);
    }

    public void generate() {
        generateAdjacency();
        generateMST();
        generateWalls();
    }

    private void generateAdjacency() {
        for (int i = 0; i < height * width; i++) {
            adjacencyList.add(null);
        }
        for (int node = 0; node < width * height; node++) {
            adjacencyList.set(node, addAllNodes(node));
        }
    }

    private LinkedHashSet<Edge> addAllNodes(int nodeMain) {
        LinkedHashSet<Edge> nodesList = getNodesList(nodeMain);

        int weight = new Random().nextInt(99) + 1;

        if (nodeMain % width != width - 1) {
            addNode(nodesList, nodeMain, nodeMain + 1, weight);
            addSymmetricHorizontal(nodeMain + 1, weight);
        }
        weight = new Random().nextInt(99) + 1;
        addNode(nodesList, nodeMain, nodeMain + width, weight);
        addSymmetricVertical(nodeMain + width, weight);
        return nodesList;
    }

    private void addSymmetricHorizontal(int nodeMain, int weight) {
        LinkedHashSet<Edge> nodesList = getNodesList(nodeMain);

        addNode(nodesList, nodeMain, nodeMain - 1, weight);
        try {
            adjacencyList.set(nodeMain, nodesList);
        } catch (Exception ignored) {
        }
    }

    private void addSymmetricVertical(int nodeMain, int weight) {
        LinkedHashSet<Edge> nodesList = getNodesList(nodeMain);

        addNode(nodesList, nodeMain, nodeMain - width, weight);
        try {
            adjacencyList.set(nodeMain, nodesList);
        } catch (Exception ignored) {
        }
    }

    private LinkedHashSet<Edge> getNodesList(int node) {
        try {
            return new LinkedHashSet<>(adjacencyList.get(node));
        } catch (Exception e) {
            return new LinkedHashSet<>();
        }
    }

    private void addNode(LinkedHashSet<Edge> edges, int startNode, int endNode, int weight) {
        if (startNode >= 0 && startNode < height * width && endNode >= 0 && endNode < height * width) {
            edges.add(new Edge(startNode, endNode, weight));
        }
    }

    private void generateMST() {
        LinkedHashSet<Edge> list = new LinkedHashSet<>(adjacencyList.get(0));
        Edge edge = list.stream().min(Comparator.comparingInt(Edge::getWeight)).get();
        list.remove(edge);
        remove(edge);
        tree.add(edge);

        while (true) {
            LinkedHashSet<Integer> nodes = new LinkedHashSet<>();
            for (Edge e : tree) {
                nodes.add(e.getStartNode());
                nodes.add(e.getEndNode());
            }
            list = new LinkedHashSet<>();

            for (int n : nodes) {
                list.addAll(adjacencyList.get(n));
            }
            edge = list.stream().min(Comparator.comparingInt(Edge::getWeight)).get();

            adjacencyList.get(edge.getStartNode()).remove(edge);
            if (!containsNode(edge)) {
                tree.add(edge);
            }
            int j = 0;
            for (LinkedHashSet<Edge> list1 : adjacencyList) {
                if (list1.isEmpty()) {
                    j++;
                }
            }
            if (j == adjacencyList.size()) {
                break;
            }
        }
        for (Edge e : new LinkedHashSet<>(tree)) {
            if (e.getStartNode() > e.getEndNode()) {
                tree.remove(e);
                tree.add(new Edge(e.getEndNode(), e.getStartNode(), e.getWeight()));
            }
        }
    }

    private boolean containsNode(Edge edge) {
        LinkedHashSet<Integer> nodes = new LinkedHashSet<>();
        for (Edge e : tree) {
            nodes.add(e.getStartNode());
            nodes.add(e.getEndNode());
        }
        return nodes.contains(edge.getStartNode()) && nodes.contains(edge.getEndNode());
    }

    private void remove(Edge edge) {
        LinkedHashSet<Edge> list = adjacencyList.get(edge.getEndNode());
        list.remove(new Edge(edge.getEndNode(), edge.getStartNode(), 0));
    }

    private void generateWalls() {
        for (int row = 0; row < maze.length; row++) {
            for (int col = 0; col < maze[0].length; col++) {
                if (row % 2 == 0 || col % 2 == 0) {
                    maze[row][col] = 1;
                }
            }
        }
        for (Edge n : tree) {
            if ((n.getStartNode() - n.getEndNode()) == -1) {
                int row = n.getEndNode() / width;
                row = row >= 1 ? row * 2 : row;
                int col = n.getStartNode() % width + (n.getEndNode() % width);
                maze[row + 1][col + 1] = 0;
            } else {
                int row = n.getStartNode() / width + (n.getEndNode() / width);
                int col = (n.getEndNode() % width * 2);
                maze[row + 1][col + 1] = 0;
            }
        }
        maze[1][0] = 0;
        int exit = maze.length / 2;
        exit = exit % 2 == 0 ? exit + 1 : exit;
        maze[exit][maze[0].length - 1] = 0;
    }

    public void findEscape() {
        wasHere = new boolean[maze.length][maze[0].length];
        goodPath = new boolean[maze.length][maze[0].length];

        recursiveSolve(1, 0);

        addEscapeToMaze();
        print();
    }

    private boolean recursiveSolve(int x, int y) {

        if (maze[x][y] == 1 || wasHere[x][y]) return false;
        if (y == maze[0].length - 1) {
            goodPath[x][y]=true;
            return true;
        }

        // If you are on a wall or already were here
        wasHere[x][y] = true;
        if (x != 0) // Checks if not on top edge
            if (recursiveSolve(x - 1, y)) { // Recalls method one to the left
                goodPath[x][y] = true; // Sets that path value to true;
                return true;
            }
        if (x != maze.length - 1) // Checks if not on bottom edge
            if (recursiveSolve(x + 1, y)) { // Recalls method one to the right
                goodPath[x][y] = true;
                return true;
            }
        if (y != 0)  // Checks if not on left edge
            if (recursiveSolve(x, y - 1)) { // Recalls method one up
                goodPath[x][y] = true;
                return true;
            }
        if (y != maze[0].length - 1) // Checks if not on right edge
            if (recursiveSolve(x, y + 1)) { // Recalls method one down
                goodPath[x][y] = true;
                return true;
            }
        return false;
    }


    private void addEscapeToMaze() {
        System.out.println(Arrays.deepToString(goodPath));
        System.out.println(Arrays.deepToString(wasHere));
        for (int row = 0; row < goodPath.length; row++) {
            for (int col = 0; col < goodPath[0].length; col++) {
                if (goodPath[row][col]) {
                    maze[row][col] = 2;
                }
            }
        }
    }


    public void print() {
        for (int[] ints : maze) {
            for (int i : ints)
                if (i == 0) System.out.print("  ");
                else if (i == 1) System.out.print("\u2588\u2588");
                else if (i == 2) System.out.print("//");
            System.out.println();
        }
        System.out.println();
    }
}



