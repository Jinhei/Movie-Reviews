package io.github.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import com.mongodb.BasicDBList;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import io.github.sqlconnection.BaseConnection;

public class TFIDF {
	List<Review> R = null;
	List<Review> reviews = null;
	Review query = null;
	List<String> queryWords = null;
	int N;
	int V;
	List<String> vocabulary = null;
	Vector<Double> idf_v = null;
	List<Review> sortedR = null;
	
	public TFIDF(String query, List<Review> reviews){
		this.query = new Review(query);
		queryWords = Arrays.asList(alphaNum(query).split(" "));
		this.reviews = reviews;
		N = reviews.size();
		R = new ArrayList<Review>();
		for(int i = 0; i < 6; i++){
			R.add(new Review(reviews.get(i)));
		}
		getVocabulary();
		getIdfVector();
		getTfVectors();
		getTfidfVectors();
		getSimilarity();
	}
	
	public static String alphaNum(String s){
		String out = "";
		for (char c : s.toCharArray()) {
		    if (Character.isLetter(c) ||Character.isDigit(c) || c == ' ')
		        out += c;    
		}
		return out;
	}
	
	public static int tf(String t, String r){
		String[] review = alphaNum(r).split(" ");
		int count = 0;
		
		for(String s : review){
			if(s.equals(t)){
				count++;
			}
		}
		
		return count;
	}
	
	public static double weightedTf(int tf){
		if(tf==0) return 0.0;
		return 1+Math.log(tf);
	}
	
	private int df(String t){
		int n = 0;
		for(Review r : R){
			int tf = tf(t, r.review);
			if(tf > 0)
				n++;
		}
		
		return n;
	}
	
	public static double idf(int n, int df){
		if(df == 0) return 0.0;
		return Math.log(n/df);
	}
	
	private void getIdfVector(){
		idf_v = new Vector<Double>();
		for(String s : vocabulary){
			if(!queryWords.contains(s)) idf_v.add(0.0);
			else {
				int df = df(s);
				idf_v.add(idf(N, df));
			}
		}
	}
	
	private void getVocabulary(){
		vocabulary = new ArrayList<String>();
		for(Review r : reviews){
			for(String s : alphaNum(r.review).split(" ")){
				if(!vocabulary.contains(s)){
					V++;
					vocabulary.add(s);
				}
			}
		}
	}
	
	private void getTfVectors(){
		query.generateTfVector(vocabulary);
		for(Review r : R){
			r.generateTfVector(vocabulary);
		}
	}
	
	private void getTfidfVectors(){
		query.generateTfidfVector(idf_v);
		for(Review r : R){
			r.generateTfidfVector(idf_v);
		}
	}
	
	private void getSimilarity(){	
		for(Review r : R){
			double similarity = 0.0;
			for(int i = 0; i < query.tfidf_n.size(); i++){
				double q = query.tfidf_n.get(i);
				double d = r.tfidf_n.get(i);
				similarity += q*d;
			}
			r.similarity = similarity;
		}
	}
	
	public List<Review> getReviewRankings(){
		List<Review> out = new ArrayList<Review>();
		while (out.size()!=R.size()){
			double highest = -1;
			Review hr = null;
			for (Review r : R){
				if(highest <= r.similarity){
					if(!out.contains(r)){
						highest = r.similarity;
						hr = r;
					}
				}
			}
			out.add(hr);
		}
		sortedR = out;
		return out;
	}
	
	public String report(){
		String out = "";
		for(Review r : sortedR){
			out += r.similarity+": ";
			for(int i = 0; i < query.tfidf_n.size(); i ++){
				double tfidf = r.tfidf_n.get(i);
				if (tfidf != 0){
					out += "<"+vocabulary.get(i) +"> tf:"+r.tf_v.get(i)+" idf:"+idf_v.get(i)+" tfidf:"+r.tfidf_v.get(i)+" ";				
				}
			}
			out+="\n";
		}
		return out;
	}
}
