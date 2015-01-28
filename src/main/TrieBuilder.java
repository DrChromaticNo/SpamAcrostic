package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class to build a dict trie given a dictionary file
 * @author Chris
 *
 */
public class TrieBuilder {
	
	//The file containing the dictionary
	private File file;
	
	public TrieBuilder(File file)
	{
		this.file = file;
	}
	
	/**
	 * Builds a dictionary trie using the file passed in the constructor
	 * @return The root node of the dictionary trie
	 */
	public DictTrie build()
	{
		//Instantiate the root node
		DictTrie root = new DictTrie('!');
		
		try {
			//Process the individual words thru a scanner
			Scanner scanner = new Scanner(file);
			scanner.useDelimiter("\r\n");
			while(scanner.hasNext())
			{
				String word = scanner.next();
				//Filter out words with apostraphes in them (wouldn't break it, it's just weird)
				if(word.indexOf('\'') == -1)
				{
					//Ensure words is lower case
					word = word.toLowerCase();
					//Call helper method to actually add the word into the trie
					root = buildHelp(word, 0, root);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return root;
	}
	
	/**
	 * Recursive method that does the work of adding a word into a trie
	 * @param word The word the add to the trie
	 * @param index The index representing how much of the word is encoded in the trie. 
	 * Before the index: Added to the Trie.  After the index: Not yet added to the trie.
	 * @param node The current node we are processing.  
	 * Represents the parent node of the node whose letter is at the index.
	 * @return A copy of the node we passed in, except everything after the index has been added to the trie.
	 */
	private DictTrie buildHelp(String word, int index, DictTrie node)
	{
		char letter = word.charAt(index);
		//If this node does not have a child node with the right letter, we add it
		if(node.getChild(letter) == null)
		{
			DictTrie child = new DictTrie(letter);
			node.addChild(letter, child);
		}
		
		DictTrie child = node.getChild(letter);
		//Check for base case: the index is at the end of the word
		//If so, we set the child node to show it represents this word
		if(index == word.length()-1)
		{
			child.setWord(word);
		}
		//Otherwise, we increment the index and continue building the trie
		else
		{
			buildHelp(word, ++index, child);
		}
		
		return node;
	}
	
}
