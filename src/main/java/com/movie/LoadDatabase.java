/*
 * Licensed to Neo Technology under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Neo Technology licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.movie;

import java.io.File;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.io.fs.FileUtils;

import com.model.Movie;
import com.reader.MovieReader;
import com.model.User;
import com.reader.UserReader;
import com.model.Rating;
import com.reader.RatingReader;

public class LoadDatabase {
    private static final File DB_PATH = new File( "target/neo4j-db" );
    private static final String DATA_PATH = "/Users/remco/OneDrive/Projecten/Big Data - Recommendation System/movie-recommender-v01/MovieRecommender/target/data/";
    private static GraphDatabaseService graphDb;

    public static void main( final String[] args ) throws IOException
    {
        FileUtils.deleteRecursively( DB_PATH );

        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
        // registerShutdownHook( graphDb );

        LoadDatabase database = new LoadDatabase();
        database.createMovies();
        database.createUsers();
        database.createRatings();

        // database.shutDown();

    }

    private void createMovies() throws FileNotFoundException, IOException {
    	
    	// reed movie file and return list of POJOs to feed the graph
    	MovieReader parser = new MovieReader(DATA_PATH + "movies.dat");
    	List<Movie> movies = parser.processLineByLine();
    	
    	Transaction transaction = graphDb.beginTx();
    	try {
    		for (Movie m : movies) {
    			// create movie node
    			Map<String, Object> params = new HashMap<String, Object>();
    			params.put("movieId", m.getMovieId());
    			params.put("title", m.getTitle());
    			
    			String query = "CREATE (a:movie { movieId: {movieId}, title: {title} })";
    		
    			graphDb.execute(query,  params);
    			
    			// create genres if needed
    			for (String g : m.getGenres()) {
    				params = new HashMap<String, Object>();
    				params.put("gen", g);
    				params.put("movieId", m.getMovieId());
    				query = "CREATE (g:genre {genre: {gen}})<-[:hasgenre]-(a:Movie {movieId: {movieId}})";
    				graphDb.execute(query, params);
    			}
    		}
    		System.out.println("Movies transaction marked as succes [" + movies.size() + "] movie nodes");
    		transaction.success();
    		
    	} catch (Exception e) {
    		System.err.println("Error - something went wrong when writing movies - transaction marked as failure" + e.toString());
    		transaction.failure();
    	} finally {
    		System.out.println("Movies transaction ended");
    		// transaction.();
    	}
    }

    private void createUsers() throws FileNotFoundException, IOException {

        // read user file and return list of pojos to feed graph
        UserReader parser = new UserReader(DATA_PATH + "users.dat");
        List<User> users = parser.processLineByLine();
        // Start transaction
        Transaction transaction = graphDb.beginTx();
        try {
            for (User u : users) {
                // Create User node
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("uId", u.getUserID());
                params.put("gender", u.getGender());
                params.put("age", u.getAge());
                params.put("zip", u.getZipCode());
                String query = "CREATE (u:User {userId: {uId}, gender: {gender}, age: {age}, zipCode: {zip} })";
                graphDb.execute(query, params);

                // Create the relationship with occupation, create the
                // occupation itself if needed
                // We can re-use the same params
                // params.put("occ", u.getOccupation());
                // query = "CREATE (u:User)-[:has]
                // query = "START root=node(*) RELATE root-[:hasOccupation]->(leaf {type:'occupation', occupation:{occ}} ) WHERE has(root.userId)  AND root.userId={uId} RETURN leaf";
                // graphDb.execute(query, params);
            }
            System.out.println("Users transaction marked as success - Commit ["
                    + users.size() + "] users nodes");
            transaction.success();
        } catch (Exception e) {
            System.err
                    .println("ERR- Something went wrong when writing users nodes for User - transaction marked as failure"
                            + e.toString());
            transaction.failure();
        } finally {
            System.out.println("User transaction end");
            // transaction.close();
        }

    }

        private void createRatings() throws FileNotFoundException {

        RatingReader parser = new RatingReader(DATA_PATH + "ratings2.dat");
        List<Rating> ratings = parser.processLineByLine();

        // Start transaction
        Transaction transaction = graphDb.beginTx();
        try {
            for (Rating r : ratings) {
                Map<String, Object> params = new HashMap<String, Object>();
                params.put("userId", r.getUserId());
                params.put("movieId", r.getMovieId());
                params.put("stars", r.getNbStars());
                params.put("ratingDate", r.getRatingDate().getTime());
                String query = "CREATE (u:User {userId: {userId}})-[:rated {stars: {stars}, ratingDate: {ratingDate}}]->(m:movie {movieId: {movieId}})";
                // String query = "START user=node:node_auto_index(userId={userId}), movie=node:node_auto_index(movieId = {movieId})   CREATE user-[:rated {stars :{stars}, ratingDate:{ratingDate}}]->movie   RETURN user";
                graphDb.execute(query, params);

            }
            System.out.println("Ratings transaction marked as success - Commit ["
                            + ratings.size() + "] ratings relations");
            transaction.success();
        } catch (Exception e) {
            System.err
                    .println("ERR- Sthg went wrong when writing ratings - transaction marked as failure"
                            + e.toString());
            transaction.failure();
        } finally {
            System.out.println("Ratings transaction end");
            // transaction.close();
        }

    }

    void shutDown()
    {
        System.out.println();
        System.out.println( "Shutting down database ..." );
        // START SNIPPET: shutdownServer
        graphDb.shutdown();
        // END SNIPPET: shutdownServer
    }

    // START SNIPPET: shutdownHook
    private static void registerShutdownHook( final GraphDatabaseService graphDb )
    {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook( new Thread()
        {
            @Override
            public void run()
            {
                graphDb.shutdown();
            }
        } );
    }
    // END SNIPPET: shutdownHook
}