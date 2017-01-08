package com.reader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.model.Movie;

public class MovieReader {
	
	private final File file;
	
	// constructor
	public MovieReader(String filename){
		file = new File(filename);
	}
	
	public final List<Movie> processLineByLine() throws FileNotFoundException {
		Scanner scanner = new Scanner(new FileReader(file));
		ArrayList<Movie> movies = new ArrayList<Movie>();
		try {
			// first use a Scanner to get each line
			while ( scanner.hasNextLine() ){
				movies.add(processLine( scanner.nextLine() ));
			}
		}
		finally {
			scanner.close();
		}
		return movies;
	}
	
	protected Movie processLine(String line){
		// second scanner to parse the content of each line
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter("::");
		
		if ( scanner.hasNext() ){
			String movieId = scanner.next();
			String title = scanner.next();
			String genres = scanner.next();
			return new Movie(Integer.valueOf(movieId),title,processGenres(genres));
		}
		else {
			log("Empty or invalid line. Unable to process.");
			return null;
		}
	}
	
	private List<String> processGenres(String genres){
		Scanner scanner = new Scanner(genres);
		scanner.useDelimiter("\\|");
		ArrayList<String> genresList = new ArrayList<String>();
		while(scanner.hasNext())
		{
			genresList.add(scanner.next());
		}
		return genresList;
	}
	
	private static void log(Object object){
		System.out.println(String.valueOf(object));
	}
	
}