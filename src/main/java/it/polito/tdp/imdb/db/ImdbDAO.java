package it.polito.tdp.imdb.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.imdb.model.Actor;
import it.polito.tdp.imdb.model.Adiacenza;
import it.polito.tdp.imdb.model.Director;
import it.polito.tdp.imdb.model.Movie;

public class ImdbDAO {
	
	public List<Actor> listAllActors(){
		String sql = "SELECT * FROM actors";
		List<Actor> result = new ArrayList<Actor>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Actor actor = new Actor(res.getInt("id"), res.getString("first_name"), res.getString("last_name"),
						res.getString("gender"));
				
				result.add(actor);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Movie> listAllMovies(){
		String sql = "SELECT * FROM movies";
		List<Movie> result = new ArrayList<Movie>();
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Movie movie = new Movie(res.getInt("id"), res.getString("name"), 
						res.getInt("year"), res.getDouble("rank"));
				
				result.add(movie);
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	public void listAllDirectors(Map<Integer, Director> idMap){
		
		String sql = "SELECT * FROM directors";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = new Director(res.getInt("id"), res.getString("first_name"), res.getString("last_name"));
				if(!idMap.containsKey(director.getId())) {
					idMap.put(director.getId(), director);
				}
			}
			conn.close();
			return ;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return ;
		}
	}
	
	
	public List<Director> getDirectorByDate(Map<Integer, Director> idMap, int anno){
		
		List<Director> result = new ArrayList<>();
		
		String sql = "select distinct md.director_id as id "
				+ "from movies_directors as md, movies as m "
				+ "where md.movie_id = m.id and "
				+ "m.year = ? ";

		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director = idMap.get(res.getInt("id"));
				if(director != null) {
					result.add(director);
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Adiacenza> getAdiacenze(int anno, Map<Integer, Director> idMap){
		
		List<Adiacenza> result = new ArrayList<>();
		String sql = "select md1.director_id as id1, md2.director_id as id2, "
				+ "count(distinct r1.actor_id) as peso "
				+ "from movies_directors as md1, "
				+ "movies_directors as md2, movies as m1, movies as m2, "
				+ "roles as r1, roles as r2 "
				+ "where md1.director_id > md2.director_id "
				+ "and md1.movie_id = m1.id "
				+ "and md1.movie_id = r1.movie_id "
				+ "and md2.movie_id = m2.id "
				+ "and m2.id = r2.movie_id "
				+ "and r1.actor_id = r2.actor_id "
				+ "and m1.year = ? and m2.year= ? "
				+ "group by id1, id2";
		
		Connection conn = DBConnect.getConnection();

		try {
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, anno);
			st.setInt(2, anno);
			ResultSet res = st.executeQuery();
			while (res.next()) {

				Director director1 = idMap.get(res.getInt("id1"));
				Director director2 = idMap.get(res.getInt("id2"));
				if(director1 != null && director2 != null) {
					result.add(new Adiacenza(director1, director2, res.getInt("peso")));
				}
			}
			conn.close();
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
