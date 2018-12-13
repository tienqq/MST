/* Starter code for Project 5.  
 * Do not change names or signatures of methods that are declared as public.  
 * If you want to create additional classes, make them nested classes of MST,
 * instead of placing them in separate java files.
 * Do not modify Graph.java or move it from package rbk.
 */

//Tien Quang (txq170130)
package p5;

import rbk.Graph;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;
import rbk.Graph.Timer;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.LinkedList;

public class MST extends GraphAlgorithm<MST.MSTVertex> {
    String algorithm;
    public long wmst;
    public LinkedList<Edge> mst;
    
    private MST(Graph g) {
	super(g, new MSTVertex(null));
	mst = new LinkedList<>();
        wmst = 0;
    }

    public static class MSTVertex implements Comparable<MSTVertex>, Factory {
    	boolean seen; //prim
    	Vertex parent; //parent in prim
    	MSTVertex kparent; //parent in kruskal
    	int rank; //kruskal
	public MSTVertex(Vertex u) {
		seen = false;
		parent = null;
		//make method
		kparent = this;
		rank = 0;
	}

	public MSTVertex make(Vertex u) { return new MSTVertex(u); }

	public int compareTo(MSTVertex other) {
	    return 0;
	}
	//helper method for kruskal
	public MSTVertex find() {
		if(this != kparent) {
			kparent = kparent.find();
		}
		return kparent;
	}
//helper method for kruskal
	public void union(MSTVertex rv) {
		if(this.rank > rv.rank) {
			rv.kparent = this;
		}
		else if(this.rank<rv.rank) {
			this.kparent = rv;
		}
		else {
			this.rank++;
			rv.kparent = this;
			}
		}
    }

    public static MST kruskal(Graph g) {
	MST m = new MST(g);
	m.algorithm = "Kruskal";
	//making mst vertices
	for(Vertex u:g) {
		MSTVertex mst = new MSTVertex(u);
		mst.make(u);
	}
	m.wmst = 0;
	Edge[] edgeArray = g.getEdgeArray();
	Arrays.sort(edgeArray);
	for(Edge e:edgeArray) {
		Vertex u = e.fromVertex();
		Vertex v = e.toVertex();
		MSTVertex ru = m.get(u).find();
		MSTVertex rv = m.get(v).find();
		if(ru != rv) {
			m.mst.add(e);
			ru.union(rv);
			//getting the total weight of mst
			m.wmst = m.wmst + e.getWeight();
		}
	}
        return m;
    }

    public static MST prim(Graph g, Vertex s) {
	MST m = new MST(g);
	m.algorithm = "Prim with PriorityQueue<Edge>";
	//initialize vertices
	for(Vertex u:g) {
		m.get(u).seen = false;
		m.get(u).parent = null;
	}
	m.get(s).seen = true;
	m.wmst = 0;
	PriorityQueue<Edge> q = new PriorityQueue<>();
	for(Edge e:g.incident(s)) {
		q.add(e);
	}
	while(!q.isEmpty()) {
		Edge e = q.remove();
		Vertex u; Vertex v;
		if(m.get(e.fromVertex()).seen) {u = e.fromVertex(); v = e.toVertex();}
		else { v = e.fromVertex(); u = e.toVertex();}
		if(m.get(v).seen) {
			continue;
		}
		m.get(v).seen = true;
		m.get(v).parent = u;
		m.wmst = m.wmst +e.getWeight();
		m.mst.add(e);
		for(Edge e2:g.incident(v)) {
			if(!m.get(e2.otherEnd(v)).seen) {
				q.add(e2);
			}
		}
	}
	return m;
    }

    // No changes need to be made below this
    
    public static MST mst(Graph g, Vertex s, String choice) {
	if(choice.equals("Kruskal")) {
	    return kruskal(g);
	} else {
	    return prim(g, s);
	}
    }

    public static void main(String[] args) throws java.io.FileNotFoundException {
	java.util.Scanner in;
	String choice = "Kruskal";
        if (args.length == 0 || args[0].equals("-")) {
            in = new java.util.Scanner(System.in);
        } else {
            java.io.File inputFile = new java.io.File(args[0]);
            in = new java.util.Scanner(inputFile);
        }

	if (args.length > 1) { choice = args[1]; }

	Graph g = Graph.readGraph(in);
        Vertex s = g.getVertex(1);

	Timer timer = new Timer();
	MST m = mst(g, s, choice);
	System.out.println("Algorithm: " + m.algorithm + "\n" + m.wmst);
	System.out.println(timer.end());
    }
}