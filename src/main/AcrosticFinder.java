package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

/**
 * The main class which finds acrostics according to passed criteria in a file
 * @author Chris
 *
 */
public class AcrosticFinder {
	
	//An array containing each word of the text in the file
	private ArrayList<String> text;
	//An array containing the first letter of each word of the text in the file.  Indices match the text array
	private ArrayList<Character> letters;
	//The root node of the dictionary trie
	private static DictTrie root;
	
	public AcrosticFinder(File file)
	{
		//Build our dictionary trie
		TrieBuilder builder = new TrieBuilder(new File("dictionaries/dict.txt"));
		root = builder.build();
		
		//Next, scan the file for the text
		text = new ArrayList<String>();
		letters = new ArrayList<Character>();
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			//For each word, add the word to the text array and add the first letter to the letters array
			while(scanner.hasNext())
			{
				String word = scanner.next();
				text.add(word);
				letters.add(word.toLowerCase().charAt(0));
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		AcrosticFinder finder = new AcrosticFinder(new File("inputs/trial.txt"));
		finder.findWithSkips(7, 3, true);
	}
	
	/**
	 * Basic find method, allows for no skips
	 * @param min The minimum length allowed for an acrostic word
	 * @param dupes Boolean to represent allowing duplicate acrostic words- true to allow, false to disallow
	 */
	public void find(int min, boolean dupes)
	{	
		findWithSkips(min, 0, dupes);
	}
	
	/**
	 * The main find method, allows for configuring the number of skips allowed
	 * @param minLength The minimum length allowed for an acrostic word
	 * @param maxSkips The maximum number of "skips" allowed (consecutive words in one line)
	 * @param dupes Boolean to represent allowing duplicate acrostic words- true to allow, false to disallow
	 */
	public void findWithSkips(int minLength, int maxSkips, boolean dupes)
	{	
		//This map will contain the results.
		//The complicated type is a map that maps
		//Acrostic Word -> Sets of possible poems which are arrays (each of which represents a line) of arrays (each of which represents the words on that line)
		HashMap<String, HashSet<ArrayList<ArrayList<String>>>> findings = 
				new HashMap<String, HashSet<ArrayList<ArrayList<String>>>>();
	
		//We iterate over all letters to check for acrostic poems which begin at that index
		for(int i = 0; i < letters.size(); i++)
		{
			if(root.getChild(letters.get(i)) != null)
			{
				//We collect all the results that begin at this index using the helper method
				HashMap<String, HashSet<ArrayList<ArrayList<String>>>> results = 
						findWithSkipsHelper(root.getChild(letters.get(i)), 
						new ArrayList<ArrayList<String>>(), 
						i, maxSkips, minLength);
				
				//Then we collapse those local results into the overall results
				for(String key: results.keySet())
				{
					if(!findings.containsKey(key))
					{
						findings.put(key, new HashSet<ArrayList<ArrayList<String>>>());
					}
					findings.get(key).addAll(results.get(key));
				}
			}
		}
		
		//If no duplicates are allowed, we go through and select one example from each acrostic word and remap
		//the acrostic words accordingly
		if(!dupes)
		{
			for(String key: findings.keySet())
			{
				if(findings.get(key).size() > 1)
				{
					Iterator<ArrayList<ArrayList<String>>> iter = findings.get(key).iterator();
					HashSet<ArrayList<ArrayList<String>>> deDuped = new HashSet<ArrayList<ArrayList<String>>>();
					deDuped.add(iter.next());
					findings.put(key, deDuped);
				}
			}
		}
		
		nicePrint(findings);
	}
	
	/**
	 * The workhorse helper method that does the bulk of the acrostic finding
	 * @param root The node representing the trie or sub-trie we are trying to find words in
	 * @param acrostic The acrostic poem so far
	 * @param index The index in the letters array that represents our place.  We consider the letters from index+1 to index+maxSkips.
	 * @param maxSkips The maximum number of "skips" allowed (consecutive words in one line)
	 * @param minLength The minimum length allowed for an acrostic word
	 * @return The acrostic poems that can be built from this subtrie using the provided cosntraints and initial acrostic
	 */
	private HashMap<String, HashSet<ArrayList<ArrayList<String>>>> findWithSkipsHelper(DictTrie root, 
			ArrayList<ArrayList<String>> acrostic, 
			int index, int maxSkips, int minLength)
	{
		HashMap<String, HashSet<ArrayList<ArrayList<String>>>> results = 
				new HashMap<String, HashSet<ArrayList<ArrayList<String>>>>();
		
		//Check for the "base case"
		//If the current node represents a word that meets the length requirements,
		//it means we have constructed an acrostic poem for that word (the initial acrostic + this letter)
		if(root.isWord() && root.getWord().length() > minLength)
		{
			String word = root.getWord();
			if(!results.containsKey(word))
			{
				results.put(word, new HashSet<ArrayList<ArrayList<String>>>());
			}
			
			//We need to add the last line to the acrostic for representing this letter/node
			ArrayList<String> added = new ArrayList<String>();
			added.add(text.get(index));
			ArrayList<ArrayList<String>> newResult = new ArrayList<ArrayList<String>>(acrostic);
			newResult.add(added);
			//Add the new poem to the results
			results.get(word).add(newResult);
		}
		
		//Now, we consider all the poems that can be built using these conditions as the starting point
		//We do this by iterating over all the possibities resulting from skipping 0 to maxSkips words in the text
		for(int i = 0; i < maxSkips+1; i++)
		{
			//Find what our new letter is (also check to make sure there are letters remaining)
			if(index+i+1 < letters.size())
			{
				char letter = letters.get(index+i+1);
				DictTrie node = root.getChild(letter);
				//If the node is null, we know there are no possible words that can be created using the next letter & our current node
				if(node != null)
				{
					//Try skipping ahead
					ArrayList<ArrayList<String>> passed = new ArrayList<ArrayList<String>>(acrostic);
					passed.add(new ArrayList<String>(text.subList(index, index+i+1)));
					
					HashMap<String, HashSet<ArrayList<ArrayList<String>>>> findings = 
							findWithSkipsHelper(node, passed, index+i+1, maxSkips, minLength);
					
					//Then merge findings into results
					for(String key: findings.keySet())
					{
						if(!results.containsKey(key))
						{
							results.put(key, new HashSet<ArrayList<ArrayList<String>>>());
						}
						results.get(key).addAll(findings.get(key));
					}
				}
			}
		}
		return results;
	}
	
	/**
	 * Nicely print out the acrostic poems from the findings in the form:
	 * word
	 * well
	 * okay
	 * right
	 * done
	 * 
	 * @param findings The map of acrostic words -> poems
	 */
	private void nicePrint(HashMap<String, HashSet<ArrayList<ArrayList<String>>>> findings)
	{
		for(String key: findings.keySet())
		{
			for(ArrayList<ArrayList<String>> result: findings.get(key))
			{
				System.out.println(" ");
				System.out.println(key);
				for(ArrayList<String> line: result)
				{
					String output = "";
					for(String word: line)
					{
						output = output + word + " ";
					}
					System.out.println(output);
				}
			}
		}
	}
}
