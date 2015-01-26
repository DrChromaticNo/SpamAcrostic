package main;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class TrieBuilder {

	public static void main(String[] args)
	{
		TrieBuilder test = new TrieBuilder(new File("dictionaries/dict.txt"));
		DictTrie root = test.build();
		System.out.println(root);
	}
	
	private File file;
	
	public TrieBuilder(File file)
	{
		this.file = file;
	}
	
	public DictTrie build()
	{
		DictTrie root = new DictTrie('!');
		
		try {
			Scanner scanner = new Scanner(file);
			scanner.useDelimiter("\r\n");
			while(scanner.hasNext())
			{
				String word = scanner.next();
				if(word.indexOf('\'') == -1)
				{
					root = buildHelp(word, 0, root);
				}
			}
			scanner.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		return root;
	}
	
	private DictTrie buildHelp(String word, int index, DictTrie node)
	{
		char letter = word.charAt(index);
		if(node.getChild(letter) == null)
		{
			DictTrie child = new DictTrie(letter);
			node.addChild(letter, child);
		}
		
		DictTrie child = node.getChild(letter);
		if(index == word.length()-1)
		{
			child.setWord(word);
		}
		else
		{
			buildHelp(word, ++index, child);
		}
		
		return node;
	}
	
}
