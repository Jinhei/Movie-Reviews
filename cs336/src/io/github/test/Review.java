package io.github.test;

public class Review {
	String category;
	String review; 
	String id;
	
	public Review(Object id, Object review, int score){
		this.id = (String) id;
		this.review = (String) review;
		if(score<0)
			this.category="negative";
		else this.category="positive";
	}
	
	public String toString(){
		return "{ id: "+id+", review: "+review+", category:"+category+")";
	}
}
