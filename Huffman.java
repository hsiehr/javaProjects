/* Names: Rebecca Hsieh and Wesley Boyett
Class: CSCI 241
Description of program: To utilize Huffman coding. When the user puts in a text file, we read the text file, compute the frequency
of each character, and then compute the Huffman codes of it in 0's and 1's when building the tree. The frequency will generate how many
bits the character will be given - the more frequent the number, the smaller the byte code is. */

import java.util.*;
import java.io.*;
import java.lang.*;

/* Huffman: The class that holds all the global variables. This holds the global of size that's used in various spots, creates the array
 * for heap, allows us to print out our tree, and calculate the frequencies. */

public class Huffman {
	static int size;
	static Node[] heap;
	static PrintWriter textfile;
	static int sum = 0;
 	static int codelengthsum = 0;
   
   /* main:
   Preconditions:
      - User gives a text file for us to read through
   Postconditions:
      - An output of the text file in .huf in Huffman encoding
   Notes:
      - If file given isn't readable, we throw a FileNotFoundException. Otherwise, we do the steps of taking the characters, building
	  a heap, then building a tree and traversing through it. */
	public static void main(String[] args) {
   	Scanner scan = new Scanner(System.in);
 	System.out.println("Enter input:");
 	String scan1 = scan.next();
	String scan2 = scan.nextLine();

		/* Using a try catch statement to print out the frequecies for the files. The try block changes the .txt files to .huf
		 * after we calculate the frequencies. The catch block just exits if the file isn't found.  */
		try {
			File file = new File (System.getProperty("user.dir") + scan1.replace(".txt" , ".huf"));
			textfile = new PrintWriter (scan1.replace(".txt" , ".huf"));
		} catch ( java.io.FileNotFoundException e ) {
			System.exit(1);
		}
		/* Our main functions. We build a Hashmap and use it to compute the characters in the file. We then insert it into the heap to build
		 * it. Then we extract from the heap to build the tree, and then traverse through to give it 0's and 1's to build the Huffman
		 * Code. */
		HashMap<Character, Integer> characters = computeCharacterCounts(scan1);
		heap = new Node[128];
		size = 0;
		Node a;
		Node b;

		for(char palzer : characters.keySet()) { // Returns set view of keys with specified key in map
			insert(new Node(palzer, characters.get(palzer)));
		}
		while (size > 1) {
			a = extractMin();
			b = extractMin();
			insert(new Node('\0', (a.frequency + b.frequency), a, b));
		}
		traversal("", heap[1]);
	    textfile.print("Number of bits that are saved: " + ((sum*8)-codelengthsum)); // Minus 8 for ASCII character
		textfile.close();
	}
   
   /* Node:
   Preconditions:
      - None
   Postconditions:
      - Gives out a Node with specified given input.
   */
	public static class Node {
		char a;
		int frequency;
		Node left;
		Node right;

		Node(char input, int freq) {
			a = input;
			right = null;
			left = null;
			frequency = freq;
		}

		Node (char name, int freq, Node c, Node d) {
			a = name;
			frequency = freq;
			left = c;
			right = d;
		}
	} 

   /* insert:
      Preconditions:
         - A node
      Postconditions:
         - Inserts node into the minimum heap.
      Notes:
         - Adds to the heap and swaps around to get the current index
    */
	public static void insert (Node node) {
		heap[++size] = node;
		int currentidx = size;
		while ((currentidx > 1) && ( heap[(int)Math.floor(currentidx/2.0)].frequency > heap[currentidx].frequency )) {//this isnt swapping correctly never enters the loop
			swap(currentidx, (int)Math.floor(currentidx/2.0));
			currentidx = (int)Math.floor(currentidx/2.0);
		}
	}

	/* traversal:
	 * Preconditions:
	 * 		- Path of where it's going, and a node (Wesley was hungry)
	 * Postconditions:
	 * 		- Traverses through the tree - for right nodes, a 1 is added, and for left nodes, a 0 is added.
	 * Notes:
	 * 		- sum and codelengthsum add for frequency to figure out how many bits are saved
	*/
	public static void traversal(String path, Node taco) {
		if (taco.a != '\0') {
			textfile.print(taco.a + " " + path + '\n');
         sum += taco.frequency;
         codelengthsum += (taco.frequency * path.length()); // Figures how many bits saved
		}
		if (taco.left != null && taco.right != null) {
			traversal((path + 0), taco.left);
			traversal((path + 1), taco.right);
		}
	}

    /* Preconditions:
	 * 		- Two integers of where spaces are at
	 * Postconditions:
	 * 		- Compares the frequency of the two nodes
	 * 		- If 1st < 2nd return -1
	 * 		- If equal return 0
	 * 		- If 1st > 2nd return 1
	 * 	Notes: 
	 * 		- Checks if the node is a leaf or has left or right children
	*/
	public static int compareTo(int first, int second) {
		if ((first >= size) || (second >= size)) { 
			return 0;
		}
		if (heap[first] == null && heap[second] == null) {
			return 0;
		}
		if (heap[first].frequency < heap[second].frequency) {
			return -1;
		} else if (heap[first].frequency > heap[second].frequency) {
			return 1;
		} else {
			return -1;
		}
	}
   
	/* computeCharacterCounts:
		Preconditions:
				- textFilename is the name of a plaintext input file
		Postconditions:
				- A HashMap<String, Integer> object is returned that contains all of the tokens and counts in the input file
		Notes:
				- If opening any file throws a FileNotFoundException, print to standard error:
				"Error: Unable to open file " + textFilename (where textFilename contains the name of the problem file) and then exit with
				value 1 (i.e. System.exit(1))
	*/
	public static HashMap<Character, Integer> computeCharacterCounts (String textFilename) {
		File file = new File(textFilename);
		HashMap<Character, Integer> characters = new HashMap<Character, Integer>();
		String temp;

		try {
			Scanner input = new Scanner(file);
			while (input.hasNextLine()) {
				temp = input.nextLine();
				for (char ch: temp.toCharArray()) {
					if (characters.containsKey(ch)) {
						characters.put(ch, characters.get(ch) + 1);
					} else {
						characters.put(ch, 1);
					} 
				} 
			}
			return characters;
		} catch (FileNotFoundException exception) {
			System.out.println("Error: Unable to open file " + textFilename);
			System.exit(1);
			return characters;
		}
	} 

	/* minHeapify:
	 * Preconditions:
	 * 		- Takes in a node array and an integer position
	 * Postcondition:
	 * 		- Has a parent and child, and checking through the array. It's swapping to fix up the tree.
	 * 		While the left is smaller than the array length, we compare and then set the smallest to be
	 * 		equal to the left. While the right is smaller than the array length, we compare and then
	 * 		set the smallest to be equal to the right. We also check if the smallest is not equal to the position,
	 * 		as if that doesn't run, then we swap and then recursively call minHeapify.
	 *
	*/
	public static void minHeapify(Node[] array, int i) {
		int left = 2*i;
		int right = 2*i+1;
		int smallest = i;
		if ((left < array.length) && compareTo(left, smallest) == -1) {
			smallest = left;
		}
		if ((right < array.length) && compareTo(right, smallest) == -1) {
			smallest = right;
		}
		if (smallest != i) {
			swap(i, smallest);
			minHeapify(array, smallest);
		}
	}

	/* extractMin:
	 * Precondition:
	 * 		- None
	 * Postcondition:
	 * 		- Takes in a node from the heap and building it into a tree
	 */
	public static Node extractMin() {
		if (size < 1) {
			System.out.println("System underflow");
			System.exit(1);
		}
		Node min = heap[1];
		heap[1] = heap[size];
		heap[size] = null;
		size--;
		minHeapify(heap, 1);
		return min;
	} 

	/* swap:
	 * Precondition:
	 * 		- Two integers
	 * Postcondition:
	 * 		- Sets a temporary node and swaps the first and second spots
	*/
	public static void swap(int first, int second) {
		Node temp;
		temp = heap[first];
		heap[first] = heap[second];
		heap[second] = temp;
	}

} // Closes the class code


