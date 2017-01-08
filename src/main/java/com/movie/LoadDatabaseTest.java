//package com.movie;
//
//public class LoadDatabase {
//    
//    private static final File DB_PATH = new File( "target/neo4j-db" );
//    private static final String DATA_PATH = "/Users/remco/OneDrive/Projecten/Big Data - Recommendation System/movie-recommender-v01/MovieRecommender/target/data/";
//    
//    private static GraphDatabaseService graphDb;
//
//    public static void main( final String[] args ) throws IOException
//    
//    {
//        FileUtils.deleteRecursively( DB_PATH );
//
//        graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( DB_PATH );
//        registerShutdownHook( graphDb );
//
//        EmbeddedNeo4j database = new EmbeddedNeo4j();
//
//        database.loadRatings();
//
//    }
//
//    void loadRatings() {
//
//        Transaction transaction = graphDb.beginTx();
//
//        try {            
//
//        } catch (Exception e) {
//
//        } finally {
//            System.out.println("Ratings transaction ended");
//            transaction.close();
//        }
//    }
//
//    void loadUsers() {
//
//    }
//
//    void loadRatings() {
//
//    }