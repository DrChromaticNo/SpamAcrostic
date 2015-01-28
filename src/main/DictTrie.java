package main;

import java.util.HashMap;

/**
 * Basic Trie node to hold a dictionary in lower case
 * @author Chris
 *
 */
public class DictTrie {
	//True if node represents the end of a word, false if not
	private boolean isWord;
	//The word the node represents, null if represents no word
	private String word;
	//The letter the node represents
	private char letter;
	//Map containing a letter -> child node mapping for the children of this node (representing possible words)
	private HashMap<Character, DictTrie> children;
	
	public DictTrie(char letter)
	{
		this.letter = letter;
		this.children = new HashMap<Character, DictTrie>();
		this.isWord = false;
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
	
	/**
	 * Sets this node to represent a word
	 * @param word The word the node now represents
	 */
	public void setWord(String word)
	{
		isWord = true;
		this.word = word;
	}
	
	/**
	 * Retrieve the child of this node corresponding to the passed letter
	 * @param letter The letter of the node to retrieve
	 * @return the child node if it exists, null if not
	 */
	public DictTrie getChild(char letter)
	{
		if(children.containsKey(letter))
		{
			return children.get(letter);
		}
		else
		{
			return null;
		}
	}
	
	public void addChild(char letter, DictTrie child)
	{
		children.put(letter, child);
	}
	
	//Nicely prints this node's information in the form "Word: word Letter: l Children: a,b,c,d,..."
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
