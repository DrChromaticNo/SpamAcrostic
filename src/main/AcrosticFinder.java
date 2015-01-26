package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class AcrosticFinder {
	
	private ArrayList<String> text;
	private ArrayList<Character> letters;
	private static DictTrie root;
	
	public AcrosticFinder()
	{
		TrieBuilder builder = new TrieBuilder(new File("dictionaries/dict.txt"));
		root = builder.build();
	}
	
	public static void main(String[] args)
	{
		AcrosticFinder finder = new AcrosticFinder();
		try {
			finder.find(new File("inputs/test.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void find(File file) throws FileNotFoundException
	{	
		text = new ArrayList<String>();
		letters = new ArrayList<Character>();
		Scanner scanner = new Scanner(file);
		while(scanner.hasNext())
		{
			String word = scanner.next();
			text.add(word);
			letters.add(word.charAt(0));
		}
		scanner.close();
		System.out.println(text);
		System.out.println(letters);
		
		HashMap<String, HashSet<ArrayList<String>>> findings = 
				new HashMap<String, HashSet<ArrayList<String>>>();
		
		for(int i = 0; i < letters.size(); i++)
		{
			char letter = letters.get(i);
			int index = i;
			DictTrie node = root;
			while(node.getChild(letter) != null && index < letters.size())
			{
				node = node.getChild(letter);
				//Check to see if we've found a word
				if(node.isWord())
				{
					String word = node.getWord();
					//Then we store it
					if(!findings.containsKey(word))
					{
						findings.put(word, new HashSet<ArrayList<String>>());
					}
					ArrayList<String> acrostic = new ArrayList<String>(text.subList(i, index+1));
					findings.get(word).add(acrostic);
				}
				//Move on to next letter
				index++;
				if(index < letters.size())
				{
					letter = letters.get(index);
				}
			}
		}
		
		System.out.println(findings);
	}
}
