package io.github.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.mongodb.BasicDBList;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import io.github.sqlconnection.BaseConnection;

public class MongoConnection {
	public static void main(String[] args){
		BaseConnection bc = new BaseConnection();
		List<String> positives = readFile("positive words.txt");
		List<String> negatives = readFile("negative words.txt");
		
		bc.connect();
		bc.setDBAndCollection("cs336", "unlabel_review");
		DBCursor reviews_unsplit = bc.getRecords();
		bc.setDBAndCollection("cs336", "unlabel_review_after_splitting");
		DBCursor reviews_split = bc.getRecords();
		List<Review> reviews = new ArrayList<Review>();
		
		while(reviews_split.hasNext()){
			int score = 0;
			DBObject review_split = reviews_split.next();
			DBObject review_unsplit = reviews_unsplit.next();
			BasicDBList words = (BasicDBList) review_split.get("review");
			for (Object o : words){
				DBObject curr = (DBObject) o;
				String word = (String) curr.get("word");
				if(positives.contains(word))
					score += (int)curr.get("count");
				else if(negatives.contains(word))
					score -= (int)curr.get("count");
			}
			reviews.add(new Review(review_unsplit.get("id"),review_unsplit.get("review"),score));
		}
		bc.close();
		writeFile(reviews);
	}
	
	public static List<String> readFile(String file){
		List<String> strings = new ArrayList<String>();
		try{
			Scanner s = new Scanner(new File(file));
			while(s.hasNextLine()) {
				strings.add(s.next());
			}
		} catch (Exception e){
			System.out.println(e.toString());
		}
		
		return strings;
	}
	
	public static void writeFile(List<Review> reviews){
		try {
			Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("reviews.json")));
			for(Review r : reviews){
				out.write(r.toString()+"\n"); 
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
	}
}
