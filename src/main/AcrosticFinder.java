package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public class AcrosticFinder {
	
	private ArrayList<String> text;
	private ArrayList<Character> letters;
	private static DictTrie root;
	
	public AcrosticFinder(File file)
	{
		TrieBuilder builder = new TrieBuilder(new File("dictionaries/dict.txt"));
		root = builder.build();
		
		text = new ArrayList<String>();
		letters = new ArrayList<Character>();
		Scanner scanner;
		try {
			scanner = new Scanner(file);
			while(scanner.hasNext())
			{
				String word = scanner.next();
				text.add(word);
				letters.add(word.toLowerCase().charAt(0));
			}
			scanner.close();
			System.out.println(text);
			System.out.println(letters);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args)
	{
		AcrosticFinder finder = new AcrosticFinder(new File("inputs/trial.txt"));
		finder.findWithSkips(7, 3, true);
	}
	
	public void find(int min, boolean dupes)
	{	
		findWithSkips(min, 0, dupes);
	}
	
	public void findWithSkips(int minLength, int maxSkips, boolean dupes)
	{	
		HashMap<String, HashSet<ArrayList<ArrayList<String>>>> findings = 
				new HashMap<String, HashSet<ArrayList<ArrayList<String>>>>();
		
		for(int i = 0; i < letters.size(); i++)
		{
			if(root.getChild(letters.get(i)) != null)
			{
				HashMap<String, HashSet<ArrayList<ArrayList<String>>>> results = 
						findWithSkipsHelper(root.getChild(letters.get(i)), 
						new ArrayList<ArrayList<String>>(), 
						i, maxSkips, minLength);
				
				for(String key: results.keySet())
				{
					if(!findings.containsKey(key))
					{
						findings.put(key, new HashSet<ArrayList<ArrayList<String>>>());
					}
					findings.get(key).addAll(results.get(key));
				}
				
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
			}
		}
		
		nicePrint(findings);
	}
	
	private HashMap<String, HashSet<ArrayList<ArrayList<String>>>> findWithSkipsHelper(DictTrie root, 
			ArrayList<ArrayList<String>> acrostic, 
			int index, int maxSkips, int minLength)
	{
		HashMap<String, HashSet<ArrayList<ArrayList<String>>>> results = 
				new HashMap<String, HashSet<ArrayList<ArrayList<String>>>>();
		
		if(root.isWord() && root.getWord().length() > minLength)
		{
			String word = root.getWord();
			if(!results.containsKey(word))
			{
				results.put(word, new HashSet<ArrayList<ArrayList<String>>>());
			}
			ArrayList<String> added = new ArrayList<String>();
			added.add(text.get(index));
			ArrayList<ArrayList<String>> newResult = new ArrayList<ArrayList<String>>(acrostic);
			newResult.add(added);
			results.get(word).add(newResult);
		}
		
		for(int i = 0; i < maxSkips+1; i++)
		{
			//Find what our new letter is
			if(index+i+1 < letters.size())
			{
				char letter = letters.get(index+i+1);
				DictTrie node = root.getChild(letter);
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
