package it.polito.tdp.imdb.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.imdb.db.ImdbDAO;

public class Model {

	private Graph<Director, DefaultWeightedEdge> grafo;
	private Map<Integer, Director> idMap;
	private ImdbDAO dao ;
	
	public Model() {
		this.idMap = new HashMap<Integer, Director>();
		this.dao = new ImdbDAO();
		this.dao.listAllDirectors(idMap);
		
	}

	
	public void creaGrafo(int anno) {
		
		this.grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		//add vertices 
		Graphs.addAllVertices(this.grafo, this.dao.getDirectorByDate(idMap, anno));
		
		//add edges
		
		for(Adiacenza a : this.dao.getAdiacenze(anno, idMap)) {
			if(this.grafo.containsVertex(a.getDirector1()) && this.grafo.containsVertex(a.getDirector2())) {
				if(this.grafo.getEdge(a.getDirector1(), a.getDirector2())== null) {
					Graphs.addEdgeWithVertices(this.grafo, a.getDirector1(), a.getDirector2(), a.getPeso());
					
				}
			}
		}
		
	}
	
	public int nVertices(){
		return this.grafo.vertexSet().size();
	}
	
	public int nEdges() {
		return this.grafo.edgeSet().size();
	}
	
	public List<RegistiConnessi> getRegistiConnessi(Director d){
		
		List<RegistiConnessi> result = new ArrayList<>();
		
		for(Director a : Graphs.neighborListOf(this.grafo,d)) {
			if(grafo.getEdge(d, a) != null)
				result.add(new RegistiConnessi(a, (int) this.grafo.getEdgeWeight(this.grafo.getEdge(d, a))));
		}
		
		return result;
	}
	
	public List<Integer> getAnni(){
		
		List<Integer> result = new ArrayList<Integer>();
		
		result.add(2004);
		result.add(2005);
		result.add(2006);
		
		return result;
	}
}
