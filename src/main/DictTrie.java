package main;

import java.util.HashMap;

public class DictTrie {

	private boolean isWord;
	private String word;
	private char letter;
	private HashMap<Character, DictTrie> children;
	
	public DictTrie(char letter)
	{
		this.letter = letter;
		this.children = new HashMap<Character, DictTrie>();
	}
	
	public boolean isWord()
	{
		return isWord;
	}
	
	public String getWord()
	{
		if(isWord())
			return word;
		else
			return null;
	}
	
	public char getLetter()
	{
		return letter;
	}
	
	public void setWord(String word)
	{
		isWord = true;
		this.word = word;
	}
	
	public DictTrie getChild(char letter)
	{
		return children.get(letter);
	}
	
	public void addChild(char letter, DictTrie child)
	{
		children.put(letter, child);
	}
	
	@Override
	public String toString()
	{
		String output = "";
		if(word != null)
		{
			output = output + "Word: " + word;
		}
		output = output + " Letter: " + letter;
		output = output + " Children: ";
		for(char letter:children.keySet())
		{
			output = output + letter + ",";
		}
		return output;
	}
	
}
