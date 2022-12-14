package marvel;

import graph.DirectedGraph;
import graph.LabeledEdge;
import java.util.*;

/**
 * This class, MarvelPaths, is an implementation to find the least-connections path between
 * two given nodes that requires a graph where each node represents a character and each
 * edge connecting two characters is labeled as a comic book that they both appear in.
 *
 */
public class MarvelPaths {

    // This class does not represent an ADT.

    /**
     * Builds a new graph with all the marvel connections from a given file. The nodes of
     * the graph represent unique characters and the edges represent the comic books they
     * appear in.
     *
     * @param filename file to be read from
     * @return a newly constructed DirectedGraph with all the information from the file
     * @throws IllegalArgumentException if filename is null
     */
    public static DirectedGraph<String, String> makeGraph(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("The filename cannot be null.");
        }
        DirectedGraph<String, String> graph = new DirectedGraph<>();
        Map<String, List<String>> comics = MarvelParser.parseData(filename);

        for (String comic : comics.keySet()) {
            List<String> names = comics.get(comic);
            for (String character : names) {
                if (!graph.containsNode(character)) {
                    graph.addNode(character);
                }
            }
            for (int i = 0; i < names.size() - 1; i++) {
                for (int k = names.size() - 1; k > i; k--) {
                    graph.addEdge(names.get(i), names.get(k), comic);
                    graph.addEdge(names.get(k), names.get(i), comic);
                }
            }
        }
        return graph;
    }

    /**
     * Finds the closest path using BFS from one character(node) to another
     * character(node) using their connections formed by the edges
     * of the graph. If there is more than one path, chooses the least
     * lexicographically one as a return.
     *
     * @param start node noting beginning of path
     * @param destination node noting end of path
     * @param graph graph containing all the information of nodes and edges
     * @return a list of labeled edges that represent the path from one node
     * to another, if no path returns empty list
     * @throws IllegalArgumentException if start or destination == null
     * @throws  IllegalStateException if graph.size() == 0
     */
    public static List<LabeledEdge<String, String>> findPath(String start, String destination, DirectedGraph<String, String> graph) {
        if (start == null || destination == null) {
            throw new IllegalArgumentException("The start and destination cannot be null.");
        }
        if (graph.size() == 0) {
            throw new IllegalStateException("The graph cannot be empty.");
        }
        Queue<String> worklist = new LinkedList<>();
        Map<String, List<LabeledEdge<String, String>>> allPaths = new HashMap<>();
        worklist.add(start);
        allPaths.put(start, new ArrayList<>());
        while (!worklist.isEmpty()) {
            String currentNode = worklist.remove();
            if (currentNode.equals(destination)) {
                return allPaths.get(currentNode);
            }
            else {
                // Orders connections(edges) from one node to its children alphabetically
                Set<LabeledEdge<String, String>> nodesToVisit = new TreeSet<>(new Comparator<>() {
                    @Override
                    public int compare(LabeledEdge<String, String> e1, LabeledEdge<String, String> e2) {
                        if(!(e1.getDestination().equals(e2.getDestination())))
                            return e1.getDestination().compareTo(e2.getDestination());
                        if (!(e1.getLabel().equals(e2.getLabel()))) {
                            return e1.getLabel().compareTo(e2.getLabel());
                        }
                        return 0;
                    }
                });
                nodesToVisit.addAll(graph.listChildren(currentNode));
                // Checks the characters(children) connected to current character(currentNode) and
                // appends the connection(edge) to create a new path to the characters if they
                // have not been checked for connections yet
                for (LabeledEdge<String, String> e : nodesToVisit) {
                    if (!allPaths.containsKey(e.getDestination())) {
                        List<LabeledEdge<String, String>> pathOfPrev = allPaths.get(currentNode);
                        List<LabeledEdge<String, String>> pathOfCurrent = new ArrayList<>(pathOfPrev);
                        pathOfCurrent.add(e);
                        allPaths.put(e.getDestination(), pathOfCurrent);
                        worklist.add(e.getDestination());
                    }
                }
            }
        }
        //returns empty list always
        return allPaths.get(start);
    }

    /**
     * Allows a user to find the path between two characters in any given file
     * by accepting input from the console
     *
     * @param args string arguments
     */
    public static void main(String[] args) {
        System.out.println("Welcome to the MarvelPaths program.");
        System.out.println("This program will find the shortest relationship between two characters.");
        System.out.println("First, please input a file with characters and all their connections.");

        Scanner input = new Scanner(System.in);
        String file = input.next();
        DirectedGraph<String, String> marvelGraph = MarvelPaths.makeGraph(file);
        System.out.println("Now, you can enter characters within the file to find their connections.");

        boolean quit = false;
        // Continually runs the program as long as the user does not input quit or q
        while (!quit) {
            // Gets character inputs
            System.out.println("Please input the first character.");
            String start = input.next();

            System.out.println("Please input the second character.");
            String destination = input.next();

            while (!marvelGraph.containsNode(start) || !marvelGraph.containsNode(destination)) {
                System.out.println("One or more of those characters does not exist.");
                System.out.println("Please input another set of characters.");
                System.out.println("The first character: ");
                start = input.next();
                System.out.println("The second character: ");
                destination = input.next();
            }

            //Finds the path of characters
            List<LabeledEdge<String, String>> path = new ArrayList<>(findPath(start, destination, marvelGraph));
            if (path.isEmpty()) {
                System.out.println("There is no path from " + start + " to " + destination + ".");
                System.out.println("They share no connections what so ever.");
            }
            else {
                if (path.size() == 1) {
                    System.out.println("The shortest path consists of " + path.size() + " connection.");
                    System.out.println("The connection is: ");
                } else {
                    System.out.println("The shortest path consists of " + path.size() + " connections.");
                    System.out.println("The connections are: ");
                }
                int i = 0;
                while (i != path.size()) {
                    System.out.println(path.get(i).getStart() + " to " + path.get(i).getDestination() +
                    " via " + path.get(i).getLabel());
                    i++;
                }
            }
            // Asks user if they want to continue or not with the program
            System.out.println();
            System.out.println("If you would like to find the connections of other characters, enter y or yes.");
            System.out.println("If not, enter q or quit.");
            String next = input.next();
            if (next.equalsIgnoreCase("q") || next.equalsIgnoreCase("quit")) {
                quit = true;
                System.out.println("I hope you had fun! BYE!");
            }
        }
        input.close();
    }
}