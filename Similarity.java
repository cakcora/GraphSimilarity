package code;

import java.util.Random;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

/**
 * 
 * @author cakcora
 *         <p>
 *         Implements the social similarity algorithm from 'Network and profile
 *         based measures for user similarities on social networks' by Akcora et
 *         al.
 *         </p>
 *         <p>
 *         <a href=
 *         "https://www.academia.edu/661025/Network_and_Profile_Based_Measures_for_User_Similarities_on_Social_Networks"
 *         >PDF of the article</a>
 * 
 *         </p>
 *         <p>
 * 
 *         <a href=
 *         "http://ieeexplore.ieee.org/xpl/articleDetails.jsp?arnumber=6009562"
 *         >Abstract of the article</a>
 */

@SuppressWarnings({ "unchecked", "rawtypes" })
public class Similarity {

	private static String pref = "v";

	public static void main(String[] args) {
		// Create a Jung graph with String vertex names and Integer edge ids.
		Graph graph = new UndirectedSparseGraph<String, Integer>();

		addVertices(graph, 500);
		addEdges(graph, 1000);

		System.out.println("The graph has " + graph.getEdgeCount()
				+ " edges and " + graph.getVertexCount() + " vertices");

		double socialSimilarity1 = socialSimilarity(graph, pref + "0", pref
				+ "1");
		double socialSimilarity2 = socialSimilarity(graph, pref + "1", pref
				+ "0");
		System.out.println();
		System.out.println(socialSimilarity1);
		System.out.println(socialSimilarity2);

	}

	/**
	 * Returns the asymmetric social similarity of vertex v1 to vertex v2.
	 * 
	 * @param graph
	 *            The undirected graph that holds the network.
	 * @param v1
	 *            the vertex whose similarity to v2 will be computed.
	 * @param v2
	 *            the second vertex.
	 * 
	 * @return social similarity value.
	 */
	private static double socialSimilarity(Graph graph, String v1, String v2) {
		System.out.println("\nComputing similarity of " + v1 + " to " + v2);
		Object removed = new Object();
		if (graph.isNeighbor(v1, v2)) {
			System.out.println("The existing edge between " + v1 + " is " + v2
					+ " ignored in the algorithm.");
			removed = graph.findEdge(v1, v2);
			graph.removeEdge(removed);

		}

		// Task 1: Find the friendship graph of v1
		Graph graphNew = new UndirectedSparseGraph<String, Integer>();
		// First add all friends of v1.
		for (Object n1 : graph.getNeighbors(v1)) {
			graphNew.addVertex(n1);
		}
		// Add v1 too.
		graphNew.addVertex(v1);
		Object[] vertices = graphNew.getVertices().toArray();
		// Add friendship edges among v1's friends.
		for (int i = 0; i < vertices.length; i++) {
			for (int j = i + 1; j < vertices.length; j++) {
				if (graph.isNeighbor(vertices[i], vertices[j])) {
					graphNew.addEdge(graphNew.getEdgeCount(), vertices[i],
							vertices[j]);
				}

			}
		}
		// At this point graphNew holds the ego network of v1.
		System.out.println("friendship graph of vertex " + v1 + " has "
				+ graphNew.getVertexCount() + " vertices and "
				+ graphNew.getEdgeCount() + " edges.");

		int frEdgeCount = graphNew.getEdgeCount();

		if (frEdgeCount == 0) {
			// No need to further continue computations. Return a 0 similarity.
			return 0d;
		}
		// Task 2: Find the mutual friendship graph
		graphNew.addVertex(v2);
		for (Object n2 : graphNew.getNeighbors(v1)) {
			if (graph.isNeighbor(n2, v2)) {
				graphNew.addEdge(graphNew.getEdgeCount(), n2, v2);
			}
		}
		int mutualFrEdgeCOunt = graphNew.getEdgeCount();
		System.out.println("Mutual Friendship graph of " + v1 + " and " + v2
				+ " has " + graphNew.getVertexCount() + " vertices and "
				+ graphNew.getEdgeCount() + " edges in its friendship graph");

		// computations ended. Add back the removed edge between vertices.
		graph.addEdge(removed, v1, v2);
		return Math.log(frEdgeCount) / Math.log(2 * mutualFrEdgeCOunt);

	}

	/**
	 * Adds random edges to the graph.
	 * 
	 * @param graph
	 *            graph to be populated.
	 * @param edgeCount
	 *            number of edges to be added.
	 */
	private static void addEdges(Graph graph, int edgeCount) {
		int vCount = graph.getVertexCount();
		for (int j = 0; j < edgeCount; j++) {
			int n1 = new Random().nextInt(vCount - 1);
			int n2 = new Random().nextInt(vCount - 1);
			graph.addEdge(graph.getEdgeCount(), pref + n1, pref + n2);

		}

	}

	/**
	 * Adds random vertices to the graph.
	 * 
	 * @param graph
	 *            graph to be populated.
	 * @param vertexCount
	 *            number of vertices to be created.
	 */
	private static void addVertices(Graph graph, int vertexCount) {
		for (int j = 0; j < vertexCount; j++) {
			String vertex = pref + j;
			graph.addVertex(vertex);

		}

	}

}
