package br.com.mvc;

public class SpinnerClass{

	private String text;
    private int value;
     

    public SpinnerClass(String text, int value){
    	this.text = text;
    	this.value = value;
    }
     
    public void setText(String text){
    	this.text = text;
    }

     public String getText(){
         return this.text;
     }

     public void setValue(int value){
         this.value = value;
     }

     public int getValue(){
         return this.value;
     }

	@Override
	public String toString() {
		return text;
	}
     
     
 }
