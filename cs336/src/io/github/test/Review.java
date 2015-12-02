package io.github.test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

public class Review {
	String category;
	String review; 
	String id;
	Vector<Double> tf_v;
	Vector<Double> tfidf_v;
	Vector <Double> tfidf_n; 
	Double similarity = 0.0;
	
	public Review(Object id, Object review, int score){
		this.id = (String) id;
		this.review = (String) review;
		if(score<0)
			this.category="negative";
		else this.category="positive";
		tf_v = new Vector<Double>();
		tfidf_v = new Vector<Double>();
		similarity = 0.0;
	}
	
	public Review(Object review){
		this.id = "";
		this.review = (String) review;
		this.category="";
		tf_v = new Vector<Double>();
		tfidf_v = new Vector<Double>();
		similarity = 0.0;
	}
	
	public Review(Review r){
		this.id = r.id;
		this.review = r.review;
		this.category = r.category;
		tf_v = new Vector<Double>();
		tfidf_v = new Vector<Double>();
		similarity = 0.0;
	}
	
	/*
	public String toString(){
		return "{ id: "+id+", review: "+review+", category:"+category+")";
	}*/
	
	public String toString(){
		return "Similarity: "+similarity+", Review: "+review+"\n";
	}
	
	public void generateTfVector(List<String> vocabulary){
		tf_v = new Vector<Double>();
		List<String> queryWords = Arrays.asList(TFIDF.alphaNum(review).split(" "));
		for(String v: vocabulary){
			if (queryWords.contains(v)){
				int tf = TFIDF.tf(v, review);
				tf_v.add(TFIDF.weightedTf(tf));
			} else {
				tf_v.add(0.0);
			}
			
		}
	}
	
	public void generateTfidfVector(Vector<Double> idf_v){
		tfidf_v = new Vector<Double>();
		double length = 0;
		for(int i = 0; i < idf_v.size(); i++){
			double tfidf = tf_v.get(i)*idf_v.get(i);
			tfidf_v.add(tfidf);
			length += tfidf*tfidf;
		}
		length = Math.sqrt(length);
		tfidf_n = new Vector<Double>();
		for (double tfidf : tfidf_v){
			if(length == 0) tfidf_n.add(0.0);
			else tfidf_n.add(tfidf/length);
		}
	}
}
