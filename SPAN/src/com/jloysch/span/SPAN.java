package com.jloysch.span;
/*
 * @Author: Joshua Loysch
 * All Rights Reserved
 * 
 * Version 1.0 (Rough toy demo)
 * 
 * Introducing a toy demo of an encryption algorithm I'm developing, 'SPAN'. I have had the idea for this for quite a while and figured
 * I would could it up since it seemed like an interesting approach. This version currently has a theoretical complexity
 * of 2^129 + 3.194e38 using some mathematics. This algorithm also has the ability to generate both reversible and non-reversible tokens
 * so it can also be used for data destruction. If you would like to run the program simply run main(), it will take you to a REPL.
 * 
 * Some crypts generated may be quite large. In the future I will add updates to place this into a text file.
 * 
 * Simply have your crypt, your degree, your ratio, and your block size.*
 * 
 * *** BLOCK SIZE / PADDING TO BE ADRESSED IN LATER UPDATES ***
 * 
 * A simple plaintext phrase like 'hello' may become "2.2398A23.2308A..." so on.
 * 
 * The part that's important to the receiver is the end part, e.g. [38.453421degR\\0.8679\\8]
 * 
 * Use this and the crypt to get the plaintext phrase back.
 * 
 * This is version 1 implementaion 1 and working demo 1 so there may be some hiccups. This is a very exciting project and I can't wait to 
 * work on it over time.
 * 
 * Theoretical complexity of 2^129+3.194e^38 to start seems incredible. >.>
 */

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

public class SPAN {
	
	private static float TRI_RATIO_FINAL = (float) 0.86; //INTERNAL - TESTVAR
	
	/** 
	 * [Internal Method] binary_to_sum
	 * @param binary_representation (String) The Binary Representation of a Particular Character
	 * @return (Integer) The Binary Sum of The Particular Character
	 */
	
	private static int binary_to_sum(String binary_representation) {
		
		/* OLD METHOD OF REPRESENTATION
		int sum = 0;
		char[] chars = binary_representation.toCharArray();
		
		for (int i = 0; i < binary_representation.toCharArray().length; i++)  if (chars[i] == '1') sum+= Math.pow(2, i);
	
		chars = null;
		
		return sum;
		*/
		
		/* ALTERNATE METTHOD OF BINDING, TODO LATER IMPLEMENTATIONS & FORMUULAS
		if (binary_representation.equals("000000000000")) return 0;
		if (binary_representation.equals("100000000000")) return 1;
		if (binary_representation.equals("010000000000")) return 2;
		if (binary_representation.equals("110000000000")) return 3;
		if (binary_representation.equals("001000000000")) return 4;
		if (binary_representation.equals("101000000000")) return 5;
		if (binary_representation.equals("011000000000")) return 6;
		if (binary_representation.equals("111000000000")) return 7;
		if (binary_representation.equals("000100000000")) return 8;
		if (binary_representation.equals("100100000000")) return 9;
		if (binary_representation.equals("10100000")) return 385; //P
		if (binary_representation.equals("11100000")) return 386; //p
		System.out.println(binary_representation + " >>> " + binary_representation);
		*/
		
		return Integer.parseInt(binary_representation,2);
	}
	
	
	/**
	 * [Internal Method] degree_to_radian
	 * @param degrees (Float) The angle in degrees.
	 * @return (Double) The passed angle in radians.
	 */
	
	private static double degree_to_radian(float degrees) {
		//0.0174533 | 1° × π/180 = 0.01745rad
		
		double rads = (double) degrees * (double) (Math.PI / 180);
		
		return rads;
	}
	
	/** 
	 * [Internal Method] find_some_x
	 * @param mod (Float) The modulus, m, to generate some representative triangle, T.
	 * @param degree_obsf (Float) The degree at which our representative triangle, T, operates. Range (0,90f).
	 * @return (Float) Some X at which we can satisfy conditions for T.
	 */
	
	private static float find_some_x(float mod, float degree_obsf) { //Correct and verified
	
		//C^2 = A^2 + B^2 - 2ab*cosC
		
		float c2, c;
		
		float degone = (float) (2 * (Math.pow(mod, 2)));
		
		float degtwo = 2 * (float) mod * (float) mod * (float) Math.cos(degree_to_radian(degree_obsf));
		
		c2 = (float) degone - degtwo;
		
		//c2 = ((float) Math.pow(mod, 2) + (float) Math.pow(mod, 2)) - ((2*mod*mod) * (float) (Math.cos(degree_obsf)));
		 
		c = (float) Math.sqrt(c2);
		
		return c; 
	}
	
	/**
	 * [Internal Method] 
	 * Generate a pseudo-random degree at instantaneous time T. 
	 * @return (Float) a degree in the range (0, 90f)
	 */
	
	public static float generate_some_degree() {
		
		float num = (float) 100.0; //def
		
		
		while (num >= (float) 60.0) {
			
			num = (new Random()).nextFloat();
			
			while (num == (float) 0.0) { //Fix zero
				num = (new Random()).nextFloat();
			}
			
			String s = String.valueOf(num).substring(2, 4); //get first 2nums after decimal
			//int newNum = Integer.parseInt(s); //make it a number
			float final_number = Float.parseFloat(s); //make float
			
			num = (new Random()).nextFloat(); //Regenerate decimals
			
			num += final_number; //Make the final number, it'll check in while loop or fall through with angle.
		}
		
		return num;
	}
	
	/* ANIMATION FOR ENCRYPTION
	private static void make_triangle() { //TODO Complete later, possibly...
		
	}
	*/
	
	/**
	 * [Internal Method] next_degree - ANGLE STEPPING FORMULA F1
	 * Generate the next degree for a the given the start, the ratio, and the iteration (degree of increment). 
	 * All results returned are based on the ANGLE STEPPING FORMULA F1.
	 * @param start - (Float) The start degree.
	 * @param ratio - (Float) The ratio for stepping.
	 * @param iteration (Integer) Which function of (iteration) the formula will return.
	 * @return (Float) The next angle with respect to the start angle;
	 */
	
	private static float next_degree(float start, float ratio, int iteration) {
		//start + (start*ratio - start%ratio) = diff
		//final = diff + start
		float next = ((float) iteration)*( (float) start*ratio - ((float) start%ratio)) + start;
		
		if (next >= (float) 90.0) {
			next = (float) next%(float)90.0;
		}
		
		//System.out.println("NEXT DEGREE = " + next + " | {ST_" + start + ".*IT_" + iteration + "}<" + ratio + ">");
		
		return next;
	}
	
	/**
	 * [Internal Method] decrypt_shuffled
	 * Decrypts a cipher that has been shuffled given the shuffled cipher as a String and the key as a String.
	 * @param phrase (String) The cipher that has been shuffled.
	 * @param key (String) The key for the cipher in the format //..D..R..S..F..\\
	 * @return
	 */
	
	public static String decrypt_shuffled(String phrase, String key) {
		
		
		String[] cipherblock = phrase.split("A"); //TODO CHECK LABELS
		for (int i = 0; i < cipherblock.length; i++) cipherblock[i] = cipherblock[i] + 'A';
		
		//TODO CHECK RECOMBINATION
		
		key = key.substring(2, key.length()-2);
		String[] keytoks = new String[4];
		int keyct = 0;
		int last = 0;
		
		for (int i = 0; i < key.length(); i++) {
			if (Character.isLetter(key.charAt(i))) {
				keytoks[keyct++] = key.substring(last, i);
				last = i + 1;
			}
		}
		
		float degree = Float.parseFloat(keytoks[0]);
		float ratio = Float.parseFloat(keytoks[1]);
		int start = Integer.parseInt(keytoks[2]), end = Integer.parseInt(keytoks[3]);
		
		int[] replace = generate_replacement_indices_for(degree, ratio, start, end);
		
		String[] ciphertokens = phrase.split("A"); //TODO CHECK LABELS
		for (int i = 0; i < ciphertokens.length; i++) ciphertokens[i] = ciphertokens[i] + 'A';
		
		String[] unshuffle = unshufflecipherblock(ciphertokens, replace); //TODO Externalize to derive func just testing
		
		String test_str = "";
		for (String s : unshuffle) test_str += s;
		
		/*
		 * TODO ADD PRE POSTFIX BACK
		 */
		
		key = "//" + key + "\\\\"; //TODO CHECK
		
		String dc = decrypt(test_str, key);
		
		return dc;
	}
	
	//TODO ADDRESS ACCESS TO decrypt() 
	
	/**
	 * [Internal Method] decrypt, used as helper/in-between
	 * Returns an integer array of the sum of the decryption tokens that can be converted back to plaintext
	 * @param crypt_tokens (String[]) The cipher as a String array in the form of ['12.43423A'...]
	 * @param R (Float) The ratio at which to operate at.
	 * @param degree_obsf (Float) The degree at which to operate at.
	 * @return (Integer[]) The sums of the decryption tokens from the given cipher.
	 */
	
	private static int[] decrypt(String[] crypt_tokens, float R, float degree_obsf) {
		
		
		int dec_tokens[] = new int[crypt_tokens.length];
		
		//TODO make array have tokens seperate
		
		/*
		for (String s : crypt_tokens)  System.out.println("CRYPT > " + s);
		*/
		
		int step = 0;
		//System.out.println("DECRYPT");
		
		for (String s : crypt_tokens) {
			//System.out.println("PREDECRYPTTOKEN > " + s);
			s = s.substring(0, s.length()-1); //Chop off block check
			//System.out.println("DECRYPT TOKEN > " + s);
			
			double res = 0;
			
			if (step == 0) {
				res = (double) (Float.parseFloat(s)/2/Math.sin((degree_obsf/2)/57.2985));
			} else {
				res = (double) (Float.parseFloat(s)/2/Math.sin((next_degree(degree_obsf, R, step)/2)/57.2985));
			}
			
			/*
			if  (res < 1.0) { //catch zero case; TODO: CHECK PRECISIONS
				res = (float) 1.0;
			}
			*/

			int fres = (int) Math.round(res); // demote after round :]
			
			//System.out.println(res + " >RND> " + fres);
			dec_tokens[step++] = fres;
		}
		

		return dec_tokens;
	}
	
	/**
	 * [Internal Method] generate_test_string
	 * @param block_size (Integer) EXPECTS 8* WILL ADDRESS IN FUTURE VERSIONS
	 * @param blocks (Integer) EXPECTS 256* WILL ADDRESS IN FUTURE VERSIONS
	 * @return String String of random binary.
	 */
	
	public static String generate_test_string(int block_size, int blocks) {
		String gen = "";
		
		for (int i = 0; i < blocks; i++) {
			for (int j = 0; j < block_size; j++) {
				gen += (new Random().nextBoolean() ? "0" : "1");
			}
		}
		
		return gen;
	}
	
	
	/**
	 * [External Method] encrypt()
	 * @param phrase The phrase to encrypt.
	 * @param ratio The ratio at which to operate at.
	 * @param writetofile Write locally to file?
	 * @return Return[0] = String[0] of blocks, Return[1] = key
	 */
	public static String[][] encrypt(String phrase, float ratio, boolean writetofile) {
		return encrypt_shuffled(phrase, ratio, writetofile);
	}
	
	/**
	 * [Internal Method] encrypt_shuffled()
	 * @param phrase (String) The phrase which to encrypt.
	 * @param ratio (Float) The ratio at which to encrypt it.
	 * @param writetofile (Boolean) write locally to file. *DEBUG PUROSES.*
	 * @return String[][] where encrypt[0] are the (String[]) Blocks, encrypt[1][0] is the (String) Key.
	 */
	
	private static String[][] encrypt_shuffled(String phrase, float ratio, boolean writetofile) {
		boolean ok = false;
		
		final int blocksize = 8; //TODO CHECK IMPLEMENTATIONS
		
		
		
		while (!ok) { //TODO Code cleanup
			
			try {
				String[] encrypt = encrypt(phrase, ratio, blocksize, writetofile);
						
				//System.out.println("INDICE ARRAY >");

				String key = encrypt[1];
				key = key.substring(2, key.length()-2);
				String[] keytoks = new String[4]; //reformat key to get vals
				int keyct = 0;
				int last = 0;
				//System.out.println("KEY + '" + key + "'");
				
				for (int i = 0; i < key.length(); i++) {
					if (Character.isLetter(key.charAt(i))) {
						keytoks[keyct++] = key.substring(last, i);
						last = i + 1;
					}
				}
				
				/*
				for (String s : keytoks) {
					System.out.println("KEYTOK > " + s);
				}
				*/
				
				float degree = Float.parseFloat(keytoks[0]);
				
				//float ratio = Float.parseFloat(keytoks[1]); -- Not needed, in method
				
				int start = Integer.parseInt(keytoks[2]), end = Integer.parseInt(keytoks[3]);
				
				//System.out.println("KEY > " + key);
				
				int[] replace = generate_replacement_indices_for(degree, ratio, start, end);
				
				//System.out.println("ENCRYPT GOT REPLACEMENTS");
				
				String[] ciphertokens = encrypt[0].split("A"); //TODO CHECK BLOCK LABELS
					
				String[] shuffle = shufflecipherblock(ciphertokens, replace);
				
				/*
				 * DEBUG 
				 */
				
				/*
				for (String s : shuffle) {
					System.out.println("SHUFFLE > " + s);
				}
				
				for (int r : replace) {
					System.out.println("REPLACE " + r);
				}
				
				String[] unshuffle = unshufflecipherblock(shuffle, replace); //TODO Externalize to derive func just testing
				
				String test_str = "";
				for (String s : unshuffle) {
					System.out.println("UNSHUFFLE > " + s);
					test_str += s;
				}
				
				//test decrypt moment of truth
				
				
				String dc = SPAN.decrypt(test_str, test[1]);
				
				System.out.println("DECRYPT > '" + dc + "'");
				*/
				
				/*
				for (String s : shuffle) {
					System.out.println("ENCSHUFFLE > " + s);
				}
				*/
				
				//verify before return
				
				//for (String s : ciphertokens) System.out.println(s);
				//for (int i = 0; i < ciphertokens.length; i++) ciphertokens[i] = ciphertokens[i] + 'A'; //TODO CHECK BLOCK LABELS
				
				/*
				 * END DEBUG
				 */
				
				String shuff = "";
				for (String s : shuffle) shuff += s;
				
				String decrypt = SPAN.decrypt_shuffled(shuff, encrypt[1]);
				
				if (decrypt.equals(phrase)) {
					ok = true;
					return new String[][] {shuffle, new String[] {encrypt[1]}};
				} 
				
			} catch (Exception e) {
				//do nothing, ignore and pass
			}
		}
		
		return null;
	}
	
	//PUBLIC ACCESSIBLE METHOD
	
	/**
	 * [External Method] encrypt()
	 * @param phrase (String) The phrase to encrypt.
	 * @param ratio (Float) The ratio at which to encrypt it.
	 * @param blocksize (Integer) The blocksize**** WILL BE OVERWRITTEN WITH 8**.
	 * @param writetofile (Boolean) Write to files locally. DEBUG PURPOSES.
	 * @return (String[]) The cipher and key where return[0] = cipher and return[1] = key.
	 */
	
	private static String[] encrypt(String phrase, float ratio, int blocksize, boolean writetofile) {
		String[] pair = new String[2];
		
		/*
		 * TODO FIX BLOCKSIZE FIX FOR ENCRYPTION
		 */
		
		blocksize = 8; //TODO ADDRESS
		
		
		
		/*
		 * TODO FIX BLOCKSIZE FIX FOR ENCRYPTION!
		 */
		
		boolean verified = false;				
		
		while (!verified) {
			
			String phraseogswapback = phrase;
			phrase = inputfixup(phrase); //do number replacements
			
			//System.out.println("New Phrase > " + phrase);
			
			//String[] bins = to_padded_list(to_binary_list(phrase));

			//System.out.println("NEWPHRASE > " + phrase);
			
			String[] bins = to_padded_list(to_binary_list(phrase));
			
			phrase = phraseogswapback; //necessary for the verification check!
			
			/*
			 * MODIFY PHRASE TO REPLACE 1 WITH 'ONE'
			 */
			
			//System.out.println(encrypt_bins(bins, blocksize, (float) ratio));
			
			String[] encrypted = encrypt_bins(bins, blocksize, (float) ratio);
			
			//int preambleSize = getPreambleSizeFor(100);
			//String preamble = generatePreambleFor(preambleSize);
			
			//System.out.print("\n\n\n");
			
			String[] verify = decrypt_string(encrypted[0], blocksize, ratio, Float.parseFloat(encrypted[1]));
			
			verified = true;
			String verif_str = "";
			for (String s : verify) {
				//System.out.println("VERIFYING STRING '" + s + "'");
				//System.out.println("\t" + binary_to_char(sum_to_binary(Integer.parseInt(s))));
				verif_str+=binary_to_char(sum_to_binary(Integer.parseInt(s)));
			}
			
			if (verif_str.equals(phrase)) {
				//System.out.println("\n\nOK!");
				/*
				System.out.println("\nENCRYPTION COMPLETED AND REVERSIBILITY VERIFIED! DETAILS BELOW:\n\n>--- BEGIN ---<");
				System.out.println(encrypted[0]);
				System.out.println(encrypted[1]+"degsR\\\\"+ratio+"\\\\"+8);
				System.out.println(">--- END, PLEASE KEEP FOR YOUR RECORDS ---<\n\n");
				*/
				verified = true;
			} else {
				verified = false;
			}
			
			/*
			 * NOW HERE IS WHERE WE MAKE IT INTERESTING WITH PHASE 1 ADDITION, THE BLOCK!
			 */
			
			//TODO Convert to method
			
			String[][] cm = fitToPreamble(encrypted[0], generatePreambleFor(256));
			
			String[] bigBlock = cm[0];
			
			String rawfitencryptnoshufflecu = "";
			for (String s : bigBlock) {
				//System.out.println("BLOCK > " + s); --debug
				rawfitencryptnoshufflecu+=s;
			}
			
			/*
			 * DEBUG / TRANSLATION
			 */
			
			//System.out.println("CRYPT_BASE_PHRASE_PRE_FIT_AND_SHUFFLE > " + encrypted[0]);
			
			//System.out.println("NOSHUFFLE Cu > " + rawfitencryptnoshufflecu);
			
			/*
			int[] replaces = generate_replacement_indices_for((float) 0.8, Integer.valueOf(cm[1][0]), Integer.valueOf(cm[2][0]), 256);
			
			String[] shuffled_cipher = new String[replaces.length]; 
			
			
			for (int i = 0; i < shuffled_cipher.length; i++) {
				shuffled_cipher[i] = cm[0][replaces[i]];
			}
			
			String cryptstrforshuffle = "";
			
			for (String s : shuffled_cipher) {
				//System.out.println("AFTERBLOCK > " + s);
				cryptstrforshuffle +=s;
			}
			
			System.out.println("SHUFFLE > " + cryptstrforshuffle);
			*/
			
			
			/* UNSHUFFLE STEP *?
			 */
			
			//NOLONGERTODO FIX SHUFFLING IN BLOCKS --OK! :)
			
			//String[] unshuffledcipher = unshufflecipherblock(cryptstrforshuffle, (float) ratio, Integer.parseInt(cm[1][0]), Integer.parseInt(cm[2][0]), 256 );
			
			/*
			 * END DEBUG / TRANSLATION
			 */
			
			String[] getCryptFromBlock = getcryptfromblock(cm[0], Integer.parseInt(cm[1][0]), Integer.parseInt(cm[2][0]));
			
			//System.out.println("CRYPTFROMBLOCK > " + getCryptFromBlock[0]);
			
			String testreshufflestr = "";
			
			for (String s : getCryptFromBlock) {
				testreshufflestr += s;
			}
			
			//System.out.println("CRYPT_BASE_PHRASE_FROM_GET > " + testreshufflestr);
			
			String[] decrypted = decrypted = decrypt_string(testreshufflestr, blocksize, ratio, Float.parseFloat(encrypted[1]));
			String dcassstr = "";
			//System.out.println("\nDECRYPTION OUTPUT >\n");
			
			for (String s : decrypted) {
				//System.out.println("\t" + s + " >> " + binary_to_char(sum_to_binary(Integer.parseInt(s))));
				dcassstr += binary_to_char(sum_to_binary(Integer.parseInt(s)));
			}
			
			/*
			 * DEBUG / TRANSLATION
			 */
			
			/*
			System.out.println("\nASSTR > " + dcassstr);
			
			System.out.println("\n>--- END DECRYPTION OUTPUT ---<\n");
			
					
			System.out.println("START > " + cm[1][0]);
			
			System.out.println("END > " + cm[2][0]);
			System.out.println("CIPHER >>\n\n");
			System.out.println("---BEGIN:");
			System.out.println(rawfitencryptnoshufflecu);
			System.out.println("--END CIPHER--");
			System.out.println("\nYour Token > //" + encrypted[1] + "D" + ratio + "R" + cm[1][0] + "S" + cm[2][0] + "F\\\\\n\n");
			
			System.out.println("Save ? [y/n]");
			*/
			
			//choice = input.next(); 
			
			/*
			 * END DEBUG / TRANSLATION
			 */
			
			if (writetofile) {
			 try {
			      FileWriter myWriter = new FileWriter("crypt.txt");
			      myWriter.write(rawfitencryptnoshufflecu.toCharArray());
			      myWriter.close();
			      myWriter = new FileWriter("key.txt");
			      myWriter.write("//" + encrypted[1] + "D" + ratio + "R" + cm[1][0] + "S" + cm[2][0] + "F\\\\");
			      myWriter.close();
			      System.out.println("Successfully wrote to the files.\n\n");
			    } catch (IOException e) {
			      System.out.println("An error occurred.\n\n");
			      e.printStackTrace();
			    }
			}
			
			pair[0] = rawfitencryptnoshufflecu;
			pair[1] = ("//" + encrypted[1] + "D" + ratio + "R" + cm[1][0] + "S" + cm[2][0] + "F\\\\");
		}
	
		
		return pair;
	}
	
	
	/**
	 * [External Method] decrypt
	 * 
	 * @param cipher (String) The cipher in the form '12.483498A213.3498A...'
	 * @param key (String) the key in the form //18.229193D0.888R116S123F\\ (e.g.)
	 * @return (String)  The phrase recovered from the cipher.
	 */
	
	public static String decrypt(String cipher, String key) {
		
		/*
		System.out.println("Please enter the cipher >>");
		String cipher = input.next();
		System.out.println("Please enter your decrypt key in the format '//...\\\\");
		choice = input.next();
		*/
		
		String reformatted = key.substring(2, key.length()-2); //Chop off
		
		//System.out.println("CHOP > " + reformatted);
		
		//String crypt, int blocksize, float TRI_RATIO, float start
		
		//String[] res = decrypt_string (reformatted, r deg start)
		
		//   //38.184643D0.888R153S159F\\ - suggested format, also implies it's shrinking something in so it's a SPAN key!
		
		int blck_ct = 0;
		String[] tokens = new String[4];
		int lastindex = 0;
		
		for (int i = 0; i < reformatted.length(); i++) { //2nd pass format and transfer
			if (Character.isLetter(reformatted.charAt(i))) {

				tokens[blck_ct++] = reformatted.substring(lastindex, i);
				lastindex = i+1;
			}
		}
		
		/*
		 * tokens:
		 * 	0 - degree
		 * 	1 - ratio
		 * 	2 - start
		 * 	3 - end
		 */
		
		/*
		for (String s : tokens) {
			System.out.println("KEYTOK > " + s);
		}
		*/
		
		/*
		//String[] ciphertokens = cryptstring_to_array(cipher);
		//String[] mintokens = new String[Integer.parseInt(tokens[3])-Integer.parseInt(tokens[2])];
		//int mintokstep = 0;
		
		//for (int i = Integer.parseInt(tokens[2]); i < Integer.parseInt(tokens[3]); i++) { //copy over minimum
		//	mintokens[mintokstep++] = ciphertokens[i];
		}
		
		String recollectedmintok = "";
		
		for (String s : mintokens) recollectedmintok += s + "A"; //TODO Check if missing block labels again for whatever reason
		
		//TODO AGAIN, FIX BLOCK LABELS?! Depending on how I implement how I want it to go next I jut may not..
		*/
		
		/*
		 * decrypt_string(String crypt, int blocksize, float TRI_RATIO, float start)
		 */
		
		//TODO BIG TODO JUST MAKE THE BLOCKSIZE 8 ?!
		
		//System.out.println("MIN > " + recollectedmintok);
		
		//String[] res = decrypt_string (recollectedmintok, 8, Float.parseFloat(tokens[1]), Float.parseFloat(tokens[0]));
	
		/*
		System.out.println("Cryptfromblockstart = " + tokens[2]);
		System.out.println("Cryptfromblockend = " + tokens[1]);
		*/
		
		//System.out.println("CIPHER > " + cipher);
		String[] cipherasarray = cryptstring_to_array(cipher);
		
		
		
		char blockdenoter = 'A'; //TODO BLOCKPADFIXUP
		
		for (int i = 0; i < cipherasarray.length; i++) {
			
			
			if (!((cipherasarray[i].charAt( cipherasarray[i].length()-1) == blockdenoter))) {
				cipherasarray[i] = cipherasarray[i] + blockdenoter;
			}
			
			//System.out.println("CIPHERKEYAT " + i + " >> " + cipherasarray[i]);
		}
		
		/*
		for (String s : cipherasarray) {
			System.out.println("CIPHERTOKENTOARRAY> " + s);
		}
		*/
		
		
		
		String[] getCryptFromBlock = getcryptfromblock(cipherasarray, Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
		
		//System.out.println("CRYPTFROMBLOCK > " + getCryptFromBlock[0]);
		
		String getcrypt = "";
		
		for (String s : getCryptFromBlock) {
			getcrypt += s;
		
		}
		
		/*
		System.out.println("CRYPT_BASE_PHRASE_FROM_GET > " + getcrypt);
		
		System.out.println("USE RATIO " + Float.parseFloat(tokens[1]));
		System.out.println("USE BLOCK " + 8);
		System.out.println("USE DEGREE " + Float.parseFloat(tokens[0]));
		
		*/
		
		
		String[] decrypted = decrypt_string(getcrypt, 8, Float.parseFloat(tokens[1]), Float.parseFloat(tokens[0])); //TODO STATIC BLOCKSIZE????!! 8?!
		
		//System.out.println("\nDECRYPTION OUTPUT >\n");
		String dcstr = "";
		for (String s : decrypted) {
			//System.out.println("\t" + s + " >> " + binary_to_char(sum_to_binary(Integer.parseInt(s))));
			dcstr += binary_to_char(sum_to_binary(Integer.parseInt(s)));
		}
		//System.out.println("\nASSTR >\n" + dcstr);
		
		//System.out.println("\n>--- END DECRYPTION OUTPUT ---<\n");
		
		/*
		System.out.println("\nDECRYPTION OUTPUT >\n");
		
		for (String s : res) { 
			System.out.println("\t" + s + " >> " + binary_to_char(sum_to_binary(Integer.parseInt(s))));
		}
		System.out.println("\n>--- END DECRYPTION OUTPUT ---<\n");
		*/
		
		return dcstr;
	}
	
	//INTERNAL METHOD 
	
	/**
	 * [Internal Method] encrypt
	 * @param rawbinary (String) The raw binary of the phrase to encrypt.
	 * @param blocksize (Integer) WILL RESORT TO 8 FOR NOW** WILL ADDRESS!
	 * @param ratio (Float) The ratio at which to encrypt the phrase.
	 * @param degree (Float) The degree at which to encrypt the phrase.
	 * @param blocknumber (Integer) Which block we are operating on** TO BE USED IN FUTURE ADDITIONS > DAISY-CHAINING*
	 * @return (LinkedList) A LinkedList with (0=(String)cipher,1=(Float) degree,2=(Float[]) degrees,3= (Float) ratio)
	 */
	
	private static LinkedList encrypt(String rawbinary, int blocksize, float ratio, float degree, int blocknumber){ 
		String data = rawbinary;
		
		/*
		 * TODO ADDRESS BLOCKSIZE FIX IN ENCRYPT-INTERNAL
		 */
		
		blocksize = 8;
		
		/*
		 * TODO ADDRESS BLOCKSIZE FIX IN ENCRYPT-INTERNAL!
		 */
		
		//boolean verbose = false;
		
		//System.out.println("LINKEDLISTENCRYPT");
		int blocks = rawbinary.length()/blocksize;
		
		//int sum_of_data_at_chunk = binary_to_sum(data);
		
		int[] sums = new int[data.length()/blocksize];
		
		//System.out.println(data);
		String subblock = "";
		for (int i = 0; i < sums.length; i++) {
			//System.out.println(i);
			subblock = data.substring(0 + blocksize*i, blocksize + blocksize*i);
			sums[i] = binary_to_sum(data.substring(0 + blocksize*i, blocksize + blocksize*i));
			//System.out.println("SUBBLOCK > '" + subblock + "'");
			//System.out.println("Sum[" + i + "] = " + sums[i]);
		}
		
		/*
		 * TODO ALPHABET UNIFICATION -> ENCRYPT-INTERNAL
		 */
		
		final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; //TODO UNIFY ALPHABET FOR ENCRYPT-INTERNAL
		
		
		/*
		 * Blocknumber -> ITERATION in next_degree() can use this for daisychaining, address later.
		 */
		
		if (degree == 0) {
			degree = generate_some_degree();
		} else {
			degree = next_degree(degree, ratio, blocknumber); //Important to know blocknumber because each block is dependent on last -> DAISYCHAINING LATER**
		}
		
		
		String crypt = "";
		String crypts[] = new String[sums.length];
		float degrees[] = new float[sums.length];
		
		//final float ratio = (float) 0.86;
		
		//final float ratio = TRI_RATIO_FINAL;
		
		for (int i = 0; i < sums.length; i++) { //populate sums
			
			if (degree == 0) { //TODO INCREMENT -> ENCRYPT-INTERNAL -> ADDRESS LATER
				crypt += find_some_x(sums[i], degree);
				degrees[0] = degree;
			} else {
				crypt += find_some_x(sums[i], next_degree(degree, ratio, i));
				degrees[i] = next_degree(degree, ratio, i);
			}
			
			crypt+= (i > alphabet.length() ? alphabet.charAt(alphabet.length()-i) : alphabet.charAt(i));
			crypts[i] = crypt;
			crypt = "";
			//System.out.println("SUMGENERATED > " + crypt);
		}
		
		//System.out.println("DEGREES");
		
		//DEGREES GENERATED
		/*
		for (float f : degrees) {
			System.out.println(f);
		}
		*/
		
		/*
		System.out.println("R = " + ratio);
		System.out.println("\n\n");
		*/
		
		//System.out.println("CRYPT > " + crypt);
		
		/*
		System.out.println("CRYPT > ") ;
		System.out.print("\n--BEGIN--\n\t[");
		*/
		
		/*
		for (String s : crypts) {
			System.out.print(s);
		}
		System.out.println("]" + "//" + degree + "degsR" +"\n--END--");
		*/
		
		LinkedList values = new LinkedList();
		
		String cryptStrFull = "";
		
		for (String s : crypts) { //should be delimited by blocks A-Z
			cryptStrFull += s;
		}
		values.add(cryptStrFull);
		values.add(degree);
		values.add(degrees);
		values.add(ratio);
		
		return values;	
	}
	
	/**
	 * UNUSED [DEBUG]
	 */
	
	private static void test_a() {
		//System.out.println(sum_of_data_at_chunk);
		
				//float degree_of_obsfucation = generate_some_degree();
				
				//System.out.println(degree_of_obsfucation);
				
				//float x = find_some_x(sum_of_data_at_chunk, degree_of_obsfucation);
				
				//float x = find_some_x(sum_of_data_at_chunk, (float) 30.301506);
				/*
				System.out.println("Some x > " + x);
				
				System.out.println("--");
				System.out.println("RAW > " + data);
				System.out.println("ENCRYPT > ");
				System.out.println(".. SUM = " + binary_to_sum(data));
				  float degree = generate_some_degree();
				System.out.println(".. DEGREE OF OBFS = " + degree);
				System.out.println(".. SOME X = " + find_some_x(sum_of_data_at_chunk, degree));
				System.out.println(".. OK.");
				*/
				
				//String data = "101110111000";
				String data = generate_test_string(4, 8);
				
				int blocks = 1;
				int block_size = 4;
				
				int sum_of_data_at_chunk = binary_to_sum(data);
				
				int[] sums = new int[data.length()/4];
				
				System.out.println(data);
				for (int i = 0; i < sums.length; i++) {
					//System.out.println(i);
					
					sums[i] = binary_to_sum(data.substring(0 + 4*i, 4 + 4*i));
					
					System.out.println("Sum[" + i + "] = " + sums[i]);
				}
				
				final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
				float degree = generate_some_degree();
				
				String crypt = "";
				String crypts[] = new String[sums.length];
				float degrees[] = new float[sums.length];
				
				final float ratio = (float) 0.86;
				
				for (int i = 0; i < sums.length; i++) { //populate sums
					
					if (degree == 0) {
						crypt += find_some_x(sums[i], degree);
						degrees[0] = degree;
					} else {
						crypt += find_some_x(sums[i], next_degree(degree, ratio, i));
						degrees[i] = next_degree(degree, ratio, i);
					}
					
					crypt+= (i > alphabet.length() ? alphabet.charAt(alphabet.length()-i) : alphabet.charAt(i));
					crypts[i] = crypt;
					crypt = "";
				}
				
				System.out.println("DEGREES");
				
				for (float f : degrees) {
					System.out.println(f);
				}
				
				System.out.println("R = " + ratio);
				System.out.println("\n\n");
				
				//System.out.println("CRYPT > " + crypt);
				
				System.out.println("CRYPT > ") ;
				System.out.print("\n--BEGIN--\n\t[");
				for (String s : crypts) {
					System.out.print(s);
				}
				System.out.println("]" + "//" + degree + "degsR" +"\n--END--");
				
				decrypt(crypts, ratio, degree);
	}
	
	/*
	
	private static boolean verify(int times) {
		Boolean logs[] = new Boolean[times];
		
		for (int a = 0; a < times; a++) {
			//String data = "101110111000";
			
			//System.out.println("BEGIN PASS " + a);
			String data = generate_test_string(4, 16);
			
			int blocks = 1;
			int block_size = 4;
			
			int sum_of_data_at_chunk = binary_to_sum(data);
			
			int[] sums = new int[data.length()/4];
			
			//System.out.println("RAW DATA >> data");
			for (int i = 0; i < sums.length; i++) {
				//System.out.println(i);
				
				sums[i] = binary_to_sum(data.substring(0 + 4*i, 4 + 4*i));
				
				//System.out.println("Sum[" + i + "] = " + sums[i]);
			}
			
			final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
			float degree = generate_some_degree();
			
			String crypt = "";
			String crypts[] = new String[sums.length];
			float degrees[] = new float[sums.length];
			
			final float ratio = (float) 0.86;
			
			for (int i = 0; i < sums.length; i++) { //populate sums
				
				if (degree == 0) {
					crypt += find_some_x(sums[i], degree);
					degrees[0] = degree;
				} else {
					crypt += find_some_x(sums[i], next_degree(degree, ratio, i));
					degrees[i] = next_degree(degree, ratio, i);
				}
				
				crypt+= (i > alphabet.length() ? alphabet.charAt(alphabet.length()-i) : alphabet.charAt(i));
				crypts[i] = crypt;
				crypt = "";
			}
			/* -- DEBUG SECTION - VERIFY INTERNAL (ALT)
			System.out.println("DEGREES");
			
			for (float f : degrees) {
				System.out.println(f);
			}
			
			System.out.println("R = " + ratio);
			System.out.println("\n\n");
			
			//System.out.println("CRYPT > " + crypt);
			
			System.out.println("CRYPT > ") ;
			System.out.print("\n--BEGIN--\n\t[");
			for (String s : crypts) System.out.print(s);
			}
			System.out.println("]" + "//" + degree + "degsR" +"\n--END--");
			*/// -- END DEBUG SECTION -VERIFY INTERNAL (ALT)
	
		
			/* ----------------UNCOMMENT FOR VERIFY
			
			int[] dec_tokens = decrypt(crypts, ratio, degree);
			
			logs[a] = true;
			
//			/System.out.println(">> End Verification pass " + a);
			for (int t = 0; t < dec_tokens.length; t++) {
				
				if (sums[t] != dec_tokens[t]) {
					System.out.println(sums[t] + "<----------!--------->" + dec_tokens[t]);
					logs[a] = false;
				} else {
					//System.out.println(sums[t] + "<OK>" + dec_tokens[t]);
				}
			}
		}
		
		boolean ok = true;
		int step = 0;
		int[] loglevels = new int[logs.length];

		for (boolean b : logs) {
			if (!b) ok = false;
			loglevels[step++] = !b ? step-1 : 0;
		}
		
		//System.out.println(ok ? "All tests passed (" + times + "/" + times + ")" : "Verification failed for " + times + " passes.");
		
		System.out.print("\n[");
		for (int i : loglevels) {
			System.out.print(i + ",");
		}
		System.out.print("] <<ERROR LEVELS\n");
		
		return ok;
	}
	*/
	/*
	private static void loop_test() {
		int step = 0;
		while (verify(1)) {
			System.out.println(">>>>>>>>>>>>>>>>TRY " + step++ + ">>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		}
	}
	*/
	
	/**
	 * [External Method] sum_to_binary
	 * @param sum (Integer) A number which will be represented in binary.
	 * @return (String) The binary representation of the sum.
	 */
	
	public static String sum_to_binary(int sum) {
		
		/* OLD METHOD OF CALCULATION FOR ALTERNATE VALUES
		int bits = 0;
		int tempsum = sum;
		for (int i = 0; i < tempsum; i++) { //silly way to calc bits but should work
			tempsum-= (int) Math.pow(2, i);
			bits++;
		}
		
		System.out.println("NEEDS " + bits + " bits");
		
		String reverse = "";
		
		tempsum = sum; //make into binary reversed
		for (int i = 0; i > sum; i--) { //Make it in reverse order
			
			if (tempsum - (int) Math.pow(2, i) > 0) {
				//System.out.println("TEMPSUM BEFORE " + tempsum);
				tempsum -= (int) Math.pow(2, i);
				//System.out.println("TEMPSUM AFTER " + tempsum);
				reverse += "1";
				//System.out.println("REV > ");
			} else {
				reverse += "0";
				//System.out.println("TEMPSUM BEFORE " + tempsum);
			}
			
			
			//reverse += (tempsum - (int) Math.pow(2, i) > 0 ? "1" : "0");
		}
		

		
		System.out.println("REVERSE > " + reverse + " <<");
		
		String reversed = "";
		for (int i = 0; i < reverse.length(); i++) {
			reversed += reverse.charAt(reverse.length() -i);
		}
		
		System.out.println("BITTRANSLATION > " + sum + " >>> " + reversed);
		
		return reversed;
		*/
		
		return Integer.toBinaryString(sum);
	}
	
	//returns sum of the tokens it got back
	
	/**
	 * [Internal Method] decrypt_manual
	 * Mainly for testing and debug purposes with noblock, noshuffle only.**
	 * @param crypt (String) The cipher.
	 * @param R (Float) The ratio at which to decrypt.
	 * @param start (Float) The start angle for decryption.
	 * @return
	 */
	
	private static int[] decrypt_manual(String crypt, float R, double start) {
		String[] crypt_tokens = new String[1]; //TODO PLEASE CHANGE THIS IS JUST BCS IM ANNOYED, NEEDS TO BE EXACT LENGTH
		crypt_tokens[0] = crypt;
		
		int blocks = 0;
		for (int i = 0; i < crypt.length(); i++) { //1st pass check blocks
			if (Character.isLetter(crypt.charAt(i))) {
				blocks++;
			}
		}
		
		String[] resequenced_safe = new String[blocks];
		
		
		int blck_ct = 0, lastindex = 0;
		
		for (int i = 0; i < crypt.length(); i++) { //2nd pass format and transfer
			if (Character.isLetter(crypt.charAt(i))) {

				resequenced_safe[blck_ct++] = crypt.substring(lastindex, i);
				lastindex = i+1;
			}
		}
		
		/*
		for (String s : resequenced_safe) {
			System.out.println("RESEQUENCESAFE > " + s);
		}
		*/
		int[] dec_tokens = decrypt(resequenced_safe, R, (float) start);
		/*
		for (int i : dec_tokens) {
			System.out.println("DECTOKEN > " + i);
		}
		*/
		return dec_tokens;
	}
	
	/*
	private static void test_single() { //UNUSED
		//test_a();	
		
				//PASS DATA INTO THIS BLOCK BY BLOCK
				
				//TODO MAKE BLOCK CHUNKING
				
				//So this is a single run, I can make a method to keep taking stuff as a string of binary and passing back a token
				//1 encrypt token : 1 decrypt token
				
				String data = "10111011"; 
				int block_size = data.length();
				LinkedList res = encrypt(data, block_size, TRI_RATIO_FINAL, 0, 0);
				
				/*
				values.add(crypt);
				values.add(degree);
				values.add(degrees);
				values.add(ratio);
				*/
				/* --DELETE FOR TEST_SINGLE()
				String crypt = (String) res.get(0);
				float degree = (float) res.get(1);
				float[] degrees = (float[]) res.get(2);
				float ratio = (float) res.get(3);
				
				System.out.println("CRYPT > " + crypt);
				System.out.println("DEGREE > " + degree);
				
				String[] toDecryptTokens = new String[1];
				toDecryptTokens[0] = crypt;
				
				int dec_sums[] = decrypt(toDecryptTokens, ratio, degree);
				
				for (int i : dec_sums) {
					System.out.println(i);
					sum_to_binary(i);
				}
				
	}
	*/
	
	/*
	private static void main_test() {
		encrypt("101110111011", 12, TRI_RATIO_FINAL, 0, 0);
		TRI_RATIO_FINAL = (float) 0.5;
		
		decrypt_manual("782.1675A", TRI_RATIO_FINAL, (double) 14.965881);
	}
	*/
	
	/**
	 * [Internal Method] test_manual
	 * Debugging method.
	 * @param bits (String) The binary to test.
	 * @param blocksize (Integer) 8*** WILL ADDRESS IN FUTURE VERSIONS!
	 * @param triratio (Float) The ratio at which to test at.
	 * @return (Boolean) Whether or not it was successfully encrypted and decrypted.
	 */
	
	private static boolean test_manual(String bits, int blocksize, float triratio) {
		LinkedList vals = encrypt(bits, blocksize, triratio, 0, 0);
		//decrypt_manual((String) vals.get(0), triratio, 3.0);
		int dec_tok[] = decrypt_manual((String) vals.get(0), triratio, (float) vals.get(1));
		
		/*
		 * TODO ADDRESS BLOCKSIZE FIX IN TEST_MANUAL
		 */
		
		blocksize = 8;
		
		/*
		 * TODO ADDRESS BLOCKSIZE FIX IN TEST_MANUAL!
		 */
		
		System.out.println("BINSUMCHECK> " + binary_to_sum(bits));
		System.out.println("BINSUMCHECKB> " + dec_tok[0]);
		//bits gets 1111111 we need to know blocksize
		
		System.out.println("THENBITS>" + bits);
		System.out.println("BLOCKS >> " + blocksize);
		
		String[] blocks = new String[bits.length()/blocksize];
		int last = 0, blkct = 0;
		String check_str = "";
		
		if (blocksize == bits.length()) { //Catch case where we are using the whole 12 bits for a segment
			blocks[0] = bits;
		} else {
			for (int i = 0; i < blocks.length; i++) {
				blocks[i] = bits.substring(i*blocksize, i*blocksize + blocksize);
				//System.out.println("BITS > " + bits);
				//System.out.println(i + " > " + i+blocksize + " >> " + bits.substring(i, i+blocksize));
				//last = i;
			}
		}
		
		for (String s : blocks) {
			System.out.println("BLOCKCHECK > " + s);
		}
		
		boolean blocks_ok = true;
		
		
		for (int t : dec_tok) {
			System.out.println("DEC >> " + t);
		}
		
		int block_counter = 0;
		boolean all_blocks_ok = true;
		for (String cryptblock : blocks) {
			if (binary_to_sum(cryptblock) == dec_tok[block_counter++]) {
				
			} else {
				all_blocks_ok = false;
			}
		}
		
		//return (binary_to_sum(bits) == (dec_tok[0]));
		
		return all_blocks_ok;
		
	}
	
	/* 
	private static void loop_until_some_failure_test() { //OLD / NOW UNUSED
		boolean error = false; //This one makes me laugh every time I write it
		int cnt = 0;
		while (!error) {
			String some_random_bits = generate_test_string(8, 1);
			LinkedList res = encrypt(some_random_bits, some_random_bits.length(), TRI_RATIO_FINAL, 0, 0);
			String crypt = (String) res.get(0);
			float degree = (float) res.get(1);
			float[] degrees = (float[]) res.get(2);
			float ratio = (float) res.get(3);
			
			int[] dec_sums = decrypt_manual(crypt, ratio, (double) degree);
			
			if (!sum_to_binary(dec_sums[0]).equals(some_random_bits)) {
				error = true;
				System.out.println("Failed on count " + cnt);
				System.out.println("BIN >> " + sum_to_binary(dec_sums[0]));
				return;
			}
			
			//System.out.println(some_random_bits);
			//error = true;
			cnt++;
		}
	}
	*/
	
	/*
	private static void loop_until_some_failure_12bitalign() { //Can proces a chunk of up to 12 bits
		boolean error = false;
		int CNT = 0;
		
		while (!error) {
			if(test_manual("110111111101", 12, TRI_RATIO_FINAL)) {
				System.out.println("OK -> " + CNT);
			} else {
				System.out.println("ERROR! -> Failed on " + CNT);
				error = true;
			}
			
			CNT++;
		}
		
	}
	*/
	
	/*
	private static void loop_until_some_failure_4bitalign() { //Can proces a chunk of up to 12 bits
		boolean error = false;
		int CNT = 0;
		
		while (!error) {
			if(test_manual("110011101101", 4, TRI_RATIO_FINAL)) { //test if we make sum into blocks, TODO: MAKE SUMBLOCK REVERSE TRANSLATOR!
				System.out.println("OK -> " + CNT);
			} else {
				System.out.println("ERROR! -> Failed on " + CNT);
				error = true;
			}
			
			CNT++;
		}
		
	}
	*/
	
	/*
	private static void loop_until_some_failure_4bitalignalt() { //Can proces a chunk of up to 12 bits
		boolean error = false;
		int CNT = 0;
		
		while (!error) {
			if(test_manual("111111001111", 4, TRI_RATIO_FINAL)) { //test if we make sum into blocks, TODO: MAKE SUMBLOCK REVERSE TRANSLATOR!
				System.out.println("OK -> " + CNT);
			} else {
				System.out.println("ERROR! -> Failed on " + CNT);
				error = true;
			}
			
			CNT++;
		}
		
	}
	*/
	
	/*
	private static void loop_until_some_failure_6bitalign() { //Can proces a chunk of up to 12 bits
		boolean error = false;
		int CNT = 0;
		
		while (!error) {
			if(test_manual("111001111111", 6, TRI_RATIO_FINAL)) { //test if we make sum into blocks, TODO: MAKE SUMBLOCK REVERSE TRANSLATOR!
				System.out.println("OK -> " + CNT);
			} else {
				System.out.println("ERROR! -> Failed on " + CNT);
				error = true;
			}
			
			CNT++;
		}
		
	}
	*/
	
	/*
	private static void loop_until_some_failure_3bitalign() { //RAISES ERRORS on 3 BIT DO NOT USE
		boolean error = false;
		int CNT = 0;
		
		while (!error) {
			if(test_manual("111001111111", 3, TRI_RATIO_FINAL)) { //test if we make sum into blocks, TODO: MAKE SUMBLOCK REVERSE TRANSLATOR!
				System.out.println("OK -> " + CNT);
			} else {
				System.out.println("ERROR! -> Failed on " + CNT);
				error = true;
			}
			
			CNT++;
		}
		
	}
	*/
	
	/*
	private static void loop_until_some_failure_8bitalign() { //RAISES ERRORS on 3 BIT DO NOT USE
		boolean error = false;
		int CNT = 0;
		
		while (!error) {
			if(test_manual("11011101", 8, TRI_RATIO_FINAL)) { //test if we make sum into blocks, TODO: MAKE SUMBLOCK REVERSE TRANSLATOR!
				System.out.println("OK -> " + CNT);
			} else {
				System.out.println("ERROR! -> Failed on " + CNT);
				error = true;
			}
			
			CNT++;
		}
		
	}
	
	*/
	
	
	
	
		//HELLO = 01001000 01000101 01001100 01001100 01001111
		
		
	
	/**
	 * [External Method] to_binary_string
	 * @param phrase (String) The phrase to represent as binary in a String.
	 * @return (String) The phrase as binary in a String.
	 */
	
	public static String to_binary_string(String phrase) { 
		String ret = "";
		/*
		if (phrase.equals("p")) return "11100001"; 
		if (phrase.equals("P")) return "11100011";
		if (phrase.equals("H")) return "11100111";
		*/
		System.out.println("PHRASE '" + phrase + "' NOT SPECIAL CASE");
		
		for (int i = 0; i < phrase.length(); i++) {
			if (i == phrase.length() - 1) {
				ret += Integer.toBinaryString(phrase.charAt(i));
			} else {
				ret += Integer.toBinaryString(phrase.charAt(i)) + " ";
			}
		}
		
		return ret;
	}
	
	/**
	 * [External Method]
	 * Create binary list from phrase, for each char it'll create an entry in binary.
	 * @param phrase (String) The phrase to convert to a list of binary per character.
	 * @return (String[]) The String[] of binary.
	 */
	
	public static String[] to_binary_list(String phrase) { //TODO LOOK
		//String numbers = "0123456789";
		String[] ret = new String[phrase.length()];
		boolean wasnumber = false;
		
		//System.out.println("PHRASE > " + phrase);
		//System.out.println("Phrase length is " + phrase.length());
		/*
		if (binary_list[i].equals("10100000")) binary_list[i] = "11100011"; //p -- manually correct missing alphabet
		if (binary_list[i].equals("11100111")) binary_list[i] = "11100111"; //P
		if (binary_list[i].equals("11100111")) binary_list[i] = "11100111"; //H
		*/
		
		//System.out.println("PHRASE = '" + phrase + "'");
		
		for (int i = 0; i < phrase.length(); i++) {
			
			/*
			switch (phrase.charAt(i)) {
				case '0':
					ret[i] = "00000000";
					wasnumber = true;
					break;
				case '1':
					ret[i] = "10000000";
					wasnumber = true;
					break;
				case '2':
					ret[i] = "01000000";
					wasnumber = true;
					break;
				case '3':
					ret[i] = "11000000";
					wasnumber = true;
					break;
				case '4':
					ret[i] = "00100000";
					wasnumber = true;
					break;
				case '5':
					ret[i] = "10100000";
					wasnumber = true;
					break;
				case '6':
					ret[i] = "01100000";
					wasnumber = true;
					break;
				case '7':
					ret[i] = "11100000";
					wasnumber = true;
					break;
				case '8':
					ret[i] = "00010000";
					wasnumber = true;
					break;
				case '9':
					ret[i] = "10010000";
					wasnumber = true;
					break;
				default:
					break;
					
			}
			
			if (!wasnumber) {
				ret[i] = Integer.toBinaryString(phrase.charAt(i));
				
				//System.out.println("NO NUMBER > '" + phrase + "'");
			}
			*/
			
			
			ret[i] = Integer.toBinaryString(phrase.charAt(i));
			//System.out.println("CONVERT > " + ret[i]);
			
		}
		for (String s : ret) {
			//System.out.println("CONVERET > " + s);
		}
		return ret;
	}
	
	/*
	 * TODO ADDRESS PADDING IN to_padded_list!
	 */
	
	/**
	 * [External Method]
	 * Converts a binary list created in SPAN to an even pad.
	 * @param binary_list (String[]) The list of binary to pad.
	 * @return (String[]) Padded String[] of same binary but uniform.
	 */
	
	public static String[] to_padded_list(String[] binary_list) {
		int longest = 0;
		for (int i = 0; i < binary_list.length; i++) {
			if (binary_list[i].length() > longest) longest = binary_list[i].length();
		}
		
		if ((longest < 4 )) {
			longest = 4;
		} else if (longest < 6) {
			longest = 6;
		} else if (longest < 8) {
			longest = 8;
		} else if (longest < 12) {
			longest = 12;
		} else {
			System.out.println("FAILURE > PHRASE OUT OF PADDING ZONE");
			System.exit(0);
			//throw new Exception("EPIC FAILURE!");
			//return;
		}
		
		
		//repad
		
		for (int i = 0; i < binary_list.length; i++) {
			//System.out.println("BIN[" + i + "]=" + binary_list[i]);
			for (int j = binary_list[i].length(); j < longest; j++) {
				binary_list[i] += "0"; //Pad 0 to rightmost, align all bits
			}
			
			/*
			 * FIX PAD ERRORS FOR ALPHABET!!!!!!!!!! TODO BIG ADDRESS
			 */
			
			/*
			 * 		else if (bin.equals("11011000")) return 'l';
				else if (bin.equals("11100010")) return 'q';
				else if (bin.equals("11100001")) return 'p';
				else if (bin.equals("11100011")) return 'P';
				else if (bin.equals("11100111")) return 'H';
			 */
			
			//System.out.println("BIN[" + i + "]=" + binary_list[i]);
		}
		
		//return
		
		//4,6,8,12 n-bit support later
		
		return binary_list;
	}
	
	/*
	 * TODO Moving to Adversary.java
	 */
	
	/*
	private static void pretend_adversary() { //moved to class
		String tolist = "MY@NAME@IS@JOSH"; //TODO FIX @ for space
		char aslist[] = new char[tolist.length()];
		
		for (int i = 0; i < tolist.length(); i++) {
			aslist[i] = Character.valueOf(tolist.charAt(i));
		}
		
		//make expected message to crack properly
		
		boolean cracked = false;
		
		float changingRatio = (float) 0.0;
		float changingDegree = (float) 0.0;
		int blocksize = 8;
		
		int tries = 0;
		
		while (!cracked) {
			
			changingRatio += 0.000001;
			changingDegree += 0.000001;
			
			String[] dc = SPAN.decrypt_string("65.2816A136.60081A", blocksize, (float) changingRatio, (float) changingDegree);
			
			int t = 0;
			boolean ok = false;
			
			cracked = true;
			
			for (String d : dc) {
				System.out.println("DECRYPT DC > " + d);
				
				if (!(SPAN.binary_to_char(SPAN.sum_to_binary(Integer.parseInt(d))) == aslist[t])) { //As soon as we find an error
					cracked = false;
				}
			}
			
			System.out.println("SURVIVED " + tries++ + " TIMES.");
		}
	}
	*/
	
	/*
	 * MAIN ENTRY POINT FOR PROGRAM, returns the crypt as a string | return cipherstr , degree as str
	 */
	
	/**
	 * [External Method] encrypt_bins()
	 * Encrypt a String[] of binary
	 * @param bins (String[]) The String[] of binary to encrypt.
	 * @param blocksize (Integer) 8** WILL ADDRESS IN FUTURE VERSION!
	 * @param TRI_RATIO (Float) The ratio at which to operate at.
	 * @return (String[]) where return[0] = (String) cipher, and return[1] = (Float) angle.
	 */
	
	private static String[] encrypt_bins(String[] bins, int blocksize, float TRI_RATIO) { //TODO FIXUP CLEANUP
		
		boolean verified = false;
		String bigcrypt = "";
	
		/*
		 * TODO ADDRESS BLOCKSIZE FIX IN encrypt_bins
		 */
		
		blocksize = 8;
		
		/*
		 * TODO ADDRESS BLOCKSIZE FIX IN encrypt_bins!
		 */
		
		
		int stepct = 0;
		float startangle = (float) 0.0;
		LinkedList vals = null;
		
		while (!verified) {
			/*
			values.add(cryptStrFull);
			values.add(degree);
			values.add(degrees);
			values.add(ratio);
			*/

				/*
				 * FIX SUM COLLISIONS BEFORE ENCRYPT AND ENCRYPT_BINS!!!!!!!!
				 */
			
			for (String token : bins) {
				//System.out.println("PREENCRYPTTOKEN> '" + token + "'");
				if (stepct == 0) {
					vals = encrypt(token, blocksize, TRI_RATIO, 0, stepct++);
					bigcrypt += (String) vals.get(0);
					//bigcryptasarray[stepct] = (String) vals.get(0);
					startangle = (float) vals.get(1);
				} else {
					 vals = encrypt(token, blocksize, TRI_RATIO, startangle, stepct++);
					bigcrypt += (String) vals.get(0);
					//bigcryptasarray[stepct] = (String) vals.get(0);
				}
				
				//stepct++;
				
				/*
				 * TODO - MASSIVE! INCREMENT DEGREE OF DEGREE
				 * 
				 * HARDER IMPLEMENTATION BUT MORE SECURE AND HOW I INTENDED
				 * 
				 */
			}
			
			String[] dec = decrypt_string((String) vals.get(0), blocksize, TRI_RATIO, startangle);
			
			//verified = true;
			
			//for (int i = 0; i < dec.length; i++) {
				
				//System.out.println("BINARYCHAR>" + binary_to_char(sum_to_binary(Integer.parseInt(dec[i]))));
				
				//System.out.println("BINCHAR> " + binary_to_char(sum_to_binary(Integer.parseInt(dec[i]))));
				//System.out.println(dec[i]);
		//	}
			
			
			
			verified = true; //TODO EXTERNALIZE
			//VERIFY
		}
		
		return new String[] {bigcrypt, String.valueOf(startangle)};
	}
	
	/*
	 * main entry point for decrypting something encrypted in SPAN, 
return array of sums
	 */
	
	/**
	 * [Internal Method] decrypt_string()
	 * Failsafe decryption of a string that's been ripped from a shuffled cipher.
	 * @param crypt (String) The cipher.
	 * @param blocksize (Integer) 8*** WILL ADDRESS IN FUTURE VERSION!
	 * @param TRI_RATIO (Float) The ratio at which to operate at.
	 * @param start (Float) The starting degree at which to operate at.
	 * @return (String[]) A String[] of the decrypted tokens.
	 */
	
	private static String[] decrypt_string(String crypt, int blocksize, float TRI_RATIO, float start) {
		String ret = "";
		
		//34.7595A79.20002A14.227916A136.948A106.44044A
		
		int tokenct = 0;
		
		for (int i = 0; i < crypt.length(); i++) {
			if (Character.isLetter(crypt.charAt(i))) { 
				tokenct++ ;
			}
		}
		
	

		String tokens[] = new String[tokenct];
		tokenct = 0;
		int last = 0;
		
		for (int i = 0; i < crypt.length(); i++) {
			if (Character.isLetter(crypt.charAt(i))) {
				tokens[tokenct++] = crypt.substring(last, i + 1);
				last = i + 1;
			}
		}
		
		//return tokens;
		
		String rettok[] = new String[tokens.length];
		int rettokct = 0;
		
		for (String token : tokens) { //TODO FLOAT-DOUBLE PRECISION STARTING
			int[] dcm = decrypt_manual(token, TRI_RATIO, next_degree(start, TRI_RATIO, rettokct));
			/*
			for (int t : dcm) {
				//System.out.println("DC T>" + token + " >> " + t);
			}
			*/
			
			rettok[rettokct++] = String.valueOf(dcm[0]);//TODO FIX
		}
		
		/*
		for (String s : rettok) {
			System.out.println("RETTOK > " + s);
		}
		*/
		
		return rettok;
	}
	
	/*
	private static void donothinglol() {} //ifclause filler
	*/
	
	/*
	private static boolean forcharequals(String s1, String s2) {
		
		int len = 0;
		
		if (s1.length() != s2.length()) {
			//return false;
			
			len = (s1.length() < s2.length() ? s1.length() : s2.length());
		} else {
			len = s1.length();
		}
		
		boolean ok = true;
		for (int i = 0; i < len; i++) {
			if (s1.charAt(i) != s2.charAt(i)) ok = false;
		}
		
		return ok;
	}
	*/
	
	/**
	 * [External/Internal Method] binary_to_char
	 * Key player to SPAN, remaps alphabet.
	 * @param bin (String) The SPAN-generated binary representation of the character.
	 * @return (Character) The character which it represents with respect to SPAN's internal mapping.
	 */
	
	public static char binary_to_char(String bin) { //THE DIRTY DEUCE! MEAT AND POTATOES!
	
		/*
		 * TODO ADDRESS ALPHABET UNIFICATION IN binary_to_char()
		 */
		
		final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz.,';] [\\1234567890/!@#$%^&*()-+=~`";
		
		String[][] bin_array = new String[alphabet.length()][];
		
		for (int i = 0; i < alphabet.length(); i++) { //make array of every one :]
			bin_array[i] = to_padded_list(to_binary_list(String.valueOf(alphabet.charAt(i))));
		}
		
		//System.out.println("BINARY IN > " + bin);
		
		/*//MAP 1 DEBUG
		if (bin.equals("10000000")) return '1';
		
		else if (bin.equals("11011000")) return 'l';
		else if (bin.equals("11100010")) return 'q';
		else if (bin.equals("11100001")) return 'p';
		else if (bin.equals("11100011")) return 'P';
		else if (bin.equals("11100111")) return 'H';
		
		else if (bin.equals("1000000")) return '2';
		else if (bin.equals("11000000")) return '3';
		else if (bin.equals("100000")) return '4';
		else if (bin.equals("10100000")) return '5';
		else if (bin.equals("1100000")) return '6';
		else if (bin.equals("11100000")) return '7';
		else if (bin.equals("10000")) return '8';
		else if (bin.equals("10010000")) return '9';
		
		else if (forcharequals(bin,"100100000000")) return '9'; //TODO edgecase check 100100000000
		
		else if (forcharequals(bin, "10010000")) return 'H'; //10010000
		
		else
			//System.out.println("BINSUM > NaN '" + bin + "'");
			donothinglol(); //lol
		*/
		
		//HashMap <String, Boolean> check = new HashMap <Integer, Boolean>();
		
		//MAP CURRENT
		if (bin.equals("11010111")) return '0';
		if (bin.equals("10101001")) return '1';
		if (bin.equals("10110001")) return '2';
		if (bin.equals("10111110")) return '3';
		if (bin.equals("10100001")) return '4';
		if (bin.equals("10100111")) return '5';
		if (bin.equals("10111111")) return '6';
		if (bin.equals("10110011")) return '7';
		if (bin.equals("10111001")) return '8';
		if (bin.equals("11001011")) return '9';
		if (bin.equals("11001001")) return 'g';
		
		if (bin.equals("11111110")) return '!'; //extend specials
		if (bin.equals("11111111")) return '@';
		if (bin.equals("11111010")) return '#';
		if (bin.equals("11111001")) return '$';
		if (bin.equals("11110110")) return '%';
		if (bin.equals("11111000")) return '^';
		if (bin.equals("11100111")) return '&';
		if (bin.equals("11000001")) return '*';
		if (bin.equals("11000011")) return '(';
		if (bin.equals("11000101")) return ')';
		if (bin.equals("11010101")) return '-';
		if (bin.equals("11011001")) return '_';
		if (bin.equals("11010011")) return '+';
		if (bin.equals("11010001")) return '=';
		if (bin.equals("11110111")) return '`';
		
		if (bin.equals("10110111")) return '{';
		if (bin.equals("10101011")) return '}';
		if (bin.equals("101111000")) return '|';
		if (bin.equals("101111010")) return '\\';
		
		//if (bin.equals("11010110")) return ':';
		if (bin.equals("11011011")) return ';';
		if (bin.equals("11100101")) return '"';
		if (bin.equals("11101011")) return '\'';
		/*
		if (bin.equals("10110101")) return '<';
		if (bin.equals("10100101")) return ',';
		if (bin.equals("10101111")) return '>';
		if (bin.equals("11011101")) return '.';
		if (bin.equals("11110011")) return '?';
		if (bin.equals("11101111")) return '/';
		*/
		
		//END MAP CURRENT
		

		//FALL-THROUGH FOR NORMALS
		
		int step = 0;
		for (String[] s2 : bin_array) {
			for (String s : s2) {
				
				//pad up before compare
				
				//while (bin.length() < s.length()) bin+= "0"; //pad right-most 0
				
				//System.out.println("COMPARE <" + s + " <> " + bin + ">");
				
				if (binary_to_sum(s) == binary_to_sum(bin)) {
					
					/*//DEBUG
					//special case check for numbers
					
					//System.out.println("BINCHAR > " + bin);
					
					
					//System.out.println("BINNUM > " + (Integer.parseInt(bin, 2)));
						
					//binnum 128 = 1
					//binnum 64 = 2
					//binnum 192 = 3
					//binnum 32 = 4
					//binnum 160 = 5
					//binnum 96 = 6
					//binnum 224 = 7
					//binnum 16 = 8
					//binnum 144 = 9
					*/
					
					
					//System.out.println("MATCH!\n\n");
					return alphabet.charAt(step);
				}
				
				step++;
			}
		}
		
		
		//System.out.println("FATAL! UNPARSEABLE BIN '" + bin+"' BINNUM > RETURNING NULL FOR '" + bin + "'");
		
		return '\0'; //next-best next to null
		
	}
	
	/*
	private static void justsomedebugstuff() {
		//loop_until_some_failure_test();
				//loop_until_some_failure_12bitalign();
				//loop_until_some_failure_6bitalign();
				//loop_until_some_failure_4bitalignalt();
				//loop_until_some_failure_4bitalign();
				//loop_until_some_failure_3bitalign(); // DO NOT USE
				//loop_until_some_failure_8bitalign();
				//12 BIT ENCRYPTION SYSTEM WITH 4, 6, or 12 bit Alignments
				
				String[] bins = to_padded_list(to_binary_list("MY NAME IS JOSH"));
				
				for (String s : bins) {
					System.out.println("BINARYBIN > " + s);
					//System.out.println("LENGTH ^ = " + s.length());
				}
				
				//String t = encrypt_bins(bins, 8, (float) 0.86);
				
				//System.out.println(t);
				
				System.out.println(encrypt_bins(bins, 8, (float) 0.86));
				
				//System.out.println(to_binary_string("Hello"));
				
				String[] dc = decrypt_string("65.2816A136.60081A139.01712A213.64763A38.62887A99.619354A134.64043A162.92131A24.695885A86.92618A110.23209A173.28201A6.391523A66.10949A107.01292A", 8, (float) 0.86, (float) 24.47369);
				
				for (String d : dc) {
					System.out.println("DECRYPT DC > " + d);
					
					System.out.println(sum_to_binary(Integer.parseInt(d)));
					System.out.println(Integer.toString(Integer.parseInt(d)));
					System.out.println(binary_to_char(sum_to_binary(Integer.parseInt(d))));
				}
				
				
				
				//pretend_adversary();
	}
	*/
	
	/*
	 * get next biggest size with respect to the tokencount passed from sender
	 */
	
	/*
	private static int getPreambleSizeFor(int tokenct) { //TODO Implementation decision, stay 256 or do dynamic? Static should be better..
		return 256;
	}
	*/
	
	private static final int preambleAlphabetOffset = 26;
	private static final char preambleAlphabetOffsetChar = 'a';
	
	/**
	 * [Internal/External Method]
	 * Generate a pseudo-random character at instantaneous time t.
	 * @return (Character) a pseudo-random character.
	 */
	
	public static char generateRandomChar() { 
		return (char)((new Random()).nextInt(preambleAlphabetOffset) + preambleAlphabetOffsetChar);
	}
	
	/*
	 * generate random data that we will encrypt and pass back to sender
	 */
	
	//TODO UNIFY ALPHABET REFERENCES IN PROGRAM -> isinAlphabet
	
	private static final String SPAN_ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789~`!@#$%^&*()_-+={[}]|\\";
	
	/**
	 * [Internal Method]
	 * Checks if a character exists in SPAN alphabet.
	 * @param c (Character) The character which to check.
	 * @return (Boolean) If the character exists in the SPAN alphabet.
	 */
	
	private static boolean isInAlphabet(char c) {
		for (int i = 0; i < SPAN_ALPHABET.length(); i++) {
			if (SPAN_ALPHABET.charAt(i) == (c)) return true;
		}
		return false;
	}
	
	
	/*
	 * TODO BIG IMPLEMENTATION DECISION > ENCRYPT PREAMBLE BEFORE ENCRYPTION?? -> generatePreambleFor(int size)
	 */
	
	/**
	 * [Internal Method] generatePreambleFor()
	 * Generate an encrypted preamble for size (size).
	 * @param size
	 * @return
	 */
	
	private static String[] generatePreambleFor(int size) {
		String preambleDataPre = "";
		char c = '\0';
		boolean okaychar = false;
		
		for (int i = 0; i < size; i++) {
			
			while (!okaychar) {
				c = generateRandomChar();
				okaychar = isInAlphabet(c);
			}
			
			preambleDataPre += c;
			
			//System.out.println("PREAMBLE CHOOSE > " + c);
			
			okaychar = false;
		}
		return cryptstring_to_array(encrypt_bins(to_padded_list(to_binary_list(preambleDataPre)), 8, (float) 0.3)[0]);
	}
	
	/*
	 * Takes the crypt tokens and the preamble tokens and carves out a space for it to sit, returns the indices where the actual data sits in the cipher block
	 returns:
	 	0 - the preamble, data, postamble as str[]
	 	1 - the start int as string
	 	2 - the end int as string
	 */
	
	/**
	 * [Internal Method] fitToPreamble()
	 * Fits a cipher inside of a preamble and returns the start and end (pseudo-random pair instantaneously generated)
	 * @param cryptString (String) The cipher.
	 * @param preambleBinaryTokens (String[]) The tokens generated from generatePreambleFor()
	 * @return String[][] where return[0] = (String[]) new_data, return[1] = (Integer) start, return[2] = (Integer) end.
	 */
	
	private static String[][] fitToPreamble(String cryptString, String[] preambleBinaryTokens) {
		
		//String[] encryptedPreamble = encrypt_bins(preambleBinaryTokens, 8, (float) 0.3); //TODO FIXUP
		
		//preambleBinaryTokens = cryptstring_to_array(encryptedPreamble[0]);
		
		String[] cryptTokens;
		
		
		int blockpadct = 0;
		char blockdenoter = 'A'; //Base
		
		for (int i = 0; i < cryptString.length(); i++) {
			if (Character.isLetter(cryptString.charAt(i))) {
				blockpadct++;
				
				blockdenoter = cryptString.charAt(i);
			}
		}
		
		
		for (int i = 0; i < preambleBinaryTokens.length; i++) {
			
			if (!((preambleBinaryTokens[i].charAt( preambleBinaryTokens[i].length()-1) == blockdenoter))) {
				preambleBinaryTokens[i] = preambleBinaryTokens[i] + blockdenoter;
			}
		}
		
		cryptTokens = new String[blockpadct];
		int last = 0;
		int repopct = 0;
		
		for (int i = 0; i < cryptString.length(); i++) {
			if (Character.isLetter(cryptString.charAt(i))) {
				cryptTokens[repopct++] = cryptString.substring(last, i + 1);
				last = i + 1;
			}
		}
		/*
		for (String s : cryptTokens) {
			System.out.println("REPOP > " + s);
		}
		*/
		/*
		for (String s : cryptTokens) {
			System.out.println("RECOVEREDCRYPTTOKEN > " + s);
		}
		*/
		int diff = preambleBinaryTokens.length - cryptTokens.length;
		
		if (diff < 0) {
			System.out.println("FATAL, SIZE MISMATCH FOR PREAMBLE FIT!");
			System.out.println("PREAMBLE TOKENS LENGTH = " + preambleBinaryTokens.length);
			System.out.println("CRYPT TOKENS LENGTH = " + cryptTokens.length);
			System.exit(-1);
			return null;
		}
		/*
		for (String s : preambleBinaryTokens) {
			System.out.println("PRE > " + s);
		}
		*/
		//int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		
		//int randomNum = ThreadLocalRandom.current().nextInt(cryptTokens.length, preambleTokens.length);
		
		boolean goodPair = false;
		
		int start = 0, end = 0;
		
		while (!goodPair) { //find good place to put it
			 start = ThreadLocalRandom.current().nextInt(cryptTokens.length, preambleBinaryTokens.length);
			 end =   start + cryptTokens.length; //TODO + 1?
			 
			 if (!(end > preambleBinaryTokens.length)) goodPair = true; 
		}
		
		//System.out.println("PROPER START > " + start);
		//System.out.println("PROPER END > " + end);
		
		while (end < start) end++;
		
		while ( (end-start) < (cryptTokens.length)) end++;
		
		String new_combined[] = new String[preambleBinaryTokens.length];
		
		int crypt_step = 0;
		
		
		
		for (int i = 0; i < new_combined.length; i++) {
			
			if (i < start) {
				new_combined[i] = preambleBinaryTokens[i]; //fill big array with random data, preamble
			} else if (i >= start && i < end) {
				//System.out.println(end - start);
				new_combined[i] = cryptTokens[crypt_step++]; //insert the crypt
			} else if (i >= end) {
				new_combined[i] = preambleBinaryTokens[i]; //postamble
			}
		}
		
		/*
		 * JUST TOKENS NO SHUFFLE - HANDLED
		 */
		
		return new String[][] {new_combined, new String[] {String.valueOf(start)}, new String[] {String.valueOf(end)}}; //~pay the cost to be the boss~ - James Brown
	}
	
	/**
	 * [Internal Method] getcryptFromBlock()
	 * Rips the actual cipher from a block given the start and end (assuming the cipher is unshuffled correctly).
	 * @param cipherblock (String[]) The blocks for the cipher.
	 * @param start (Integer) The start of the cipher in the block.
	 * @param end (Integer) The end of the cipher in the block.
	 * @return (String[]) The cipher blocks alone.
	 */
	
	private static String[] getcryptfromblock(String[] cipherblock, int start, int end) { //TODO FINISH
		//System.out.println("CRYPTRECOVER START > " + start);
		//System.out.println("CRYPTRECOVER END > " + end);
		String[] ret = new String[end-start];
		int retct = 0;
		
		for (int i = start; i < end; i++) { //check off by one
			ret[retct++] = cipherblock[i];
		}
		
		if (!(ret.length == (end-start))) {
			System.out.println("OFFBYONE for getting crypt from block..");
			System.exit(-100);
		}
		
		for (String s : ret) {
			//System.out.println("RECOVERED " + s);
		}
		
		char blockdenoter = 'A'; //TODO FIXUP - CHANGE FOR MORE BLOCKS
		
		for (int i = 0; i < ret.length; i++) {
			
			if (!((ret[i].charAt( ret[i].length()-1) == blockdenoter))) {
				ret[i] = ret[i] + blockdenoter;
			}
		}
		
		return ret;
	}
	
	/** 
	 * [Internal Method] shuffleCipherBlock()
	 * Shuffles a cipher block based on the replacement indices normally generated through generate_replacement_indices_for()
	 * @param cipher (String[]) The blocks of the cipher.
	 * @param replaces (Integer[]) The replacement array either manually specified or SPAN-generated. *cipher.size = replaces.size*
	 * @return (String[]) The resequenced cipher blocks.
	 */
	
	public static String[] shufflecipherblock(String [] cipher, int[] replaces) {
		String[] reseq = new String[cipher.length];
		int reseqindex = 0;
		String temporary;
		for (int i = 0; i < reseq.length; i++) {
			reseq[reseqindex++] = cipher[replaces[i]] + 'A';
		}
		
		return reseq;
	}
	
	/**
	 * [Internal Method] indexForValue()
	 * Reverse search an integer array's values and return the index it is at. Used for block shuffling.
	 * @param arr (Integer[]) The array of integers to search.
	 * @param valforkey (Integer) Search value.
	 * @return (Integer) index it is located at or 255 if it was not found.
	 */
	
	private static int indexForValue(int[] arr, int valforkey) {
		int count = 0;
		for (int i : arr) {
			if (i == valforkey) return count;
			count++;
		}
		
		if (count >= 255) return 255; //TODO arr length, DEBUG purposes..
		
		/*
		 * TODO ADDRESS OUT-OF-BOUND indexForValue() -> IMPLICATES SHUFFLING
		 */
		
		return count;
	}
	
	/**
	 * [Internal Method] unshufflecipherblock()
	 * Unshuffles a cipher block.
	 * @param cipherblock (String[]) The blocks of the cipher.
	 * @param replaces (Integer[]) The replacement array either manually specified or SPAN-generated.
	 * @return (String[]) The unshuffled cipher block. *return.size = cipherblock.size*
	 */
	
	private static String[] unshufflecipherblock(String[] cipherblock, int[] replaces) {
		/*
		int[] replaces = generate_replacement_indices_for(ratio, start, end, blocklength); //TODO FIXUP
		
		String[] unshuffled = new String[replaces.length]; 
		
		//[0] = 75 so do backwards, e.g.
		
		HashMap<Integer, String> placement = new HashMap <Integer, String>();
		
		String[] cipherblockstringasarray = cryptstring_to_array(cipherblock);
		
		for (int i = 0; i < replaces.length; i++) {
			placement.put(replaces[i], cipherblockstringasarray[replaces[i]]);
		}
		
		for (int i = 0; i < blocklength; i++) {
			if (placement.get(i) != null) unshuffled[i] = placement.get(i);
		}
		
		for (int i : replaces) System.out.println("REPLACE > " + i);
		*/
		
		String[] reseq = new String[cipherblock.length];
		int reseqindex = 0;
		
		//int t = 0;
		
		for (int i = 0; i < reseq.length; i++) {
			//t = (cipherblock[(replaces[i])]);
			//reseq[i] = cipherblock[replaces[i]];
			reseq[i] = cipherblock[indexForValue(replaces,i)];
		}
		
		return reseq; //TODO FIX CIPHER SHUFFLE
	}
	
	//TODO DOC cryptstring_to_array()
	
	/**
	 * [Internal Method] cryptstring_to_array()
	 * Convert a cipher string to an array.
	 * @param crypt (String) The cipher.
	 * @return (String[]) The cipher as an array.
	 */
	
	private static String[] cryptstring_to_array(String crypt) {
		String[] bigcrypt;
		int ct = 0, last = 0;
		for (int i = 0; i < crypt.length(); i++) if (Character.isLetter(crypt.charAt(i))) ct++;
		
		bigcrypt = new String[ct];
		
		ct = 0;
		
		for (int i = 0; i < crypt.length(); i++) { //2nd pass format and transfer
			if (Character.isLetter(crypt.charAt(i))) {
				bigcrypt[ct++] = crypt.substring(last, i);
				last = i+1;
			}
		}
		
		return bigcrypt;
		
	}
	
	/*
	 * TODO generate_replacement_indices_for() uses formula f2. 
	 */
	
	/*
	 * TODO ORGANIZE FORMULAE
	 */
	
	/*
	 * TODO ADDRESS F2 ( further complexity ) while preserving it's reversibility.
	 */
	
	/**
	 * [Internal/External Method] generate_replacement_indices_for()
	 * @param degree (Float) The degree which to operate at.
	 * @param ratio (Float) The ratio which to operate at.
	 * @param start (Integer) Start for that cipher to augment formula.
	 * @param end (Integer) End for that cipher to augment formula.
	 * @return (Integer[]) Replacement indexes for this particular degree, ratio, start, end. (F2)
	 */
	
	public static int[] generate_replacement_indices_for(float degree, float ratio, int start, int end) {
		
		//int cipherblocklength = 64; //TODO ADDRESS IN PROGRAM, THIS IS TO LIMIT IT TO GENERATE 64 POINTS
		
		/*
		int[] finalindices = new int[cipherblocklength];
		int[] toswap = new int[cipherblocklength];
		int[] freenums = new int[cipherblocklength];
		float[] progressiveDegrees = new float[cipherblocklength];
		
		
		for (int i = 0; i < cipherblocklength; i++) {
			freenums[i] = i;
			toswap[i] = -1;
		}
		
		float newdegree = ((float) (start + end)) / ratio;
		//make new start degree independent of other degree based on the ratio and start/end
		
		HashMap<Integer, Boolean> placements = new HashMap<Integer, Boolean>();
		
		for (int i = 0; i < cipherblocklength; i++) {
			progressiveDegrees[i] = next_degree(ratio, newdegree, i);
			toswap[i] = (int) Math.round(next_degree(ratio, newdegree, i));
			placements.put(toswap[i], true);
		}
		

		boolean ok = true;
		
		while (!ok) {
			for (int i = 0; i < toswap.length; i++) {
				if (toswap[i] == -1) { //if we need placement
					for (int p = 0; p < toswap.length; p++) { //get a num that we havent placed for placement
						if (placements.get(p) == null) { //arrived at a number we haven't placed yet so put it in this spot
							toswap[i] = p;
							placements.put(p, true);
						}
					}
				}
			}
		
			//check again
			
			for (int i = 0; i < toswap.length; i++) {
				if (placements.get(i) == null) {
					System.out.println("FATAL! MISSING NO. '" + i + "'");
					ok = false;
				}
			}
		}
		
		if (!ok) System.exit(-1);
		
		
		String checkstrtemp = "";
		
		for (int i : toswap) {
			//System.out.println(i);
			checkstrtemp += i;
		}
		
		System.out.println("CHECKSTR FOR replace >> " + checkstrtemp);
		
		*/
		
	
		int POINTS = 64;
		
		int[] new_block_indices = new int [POINTS];
		float[] progressiveDegrees = new float[POINTS];
		float moddegree = 0.0f;
		float[] ratiosteparray = new float[POINTS];
		int ratiosadded = 0;
		
		HashMap <Integer, Boolean> used = new HashMap <Integer, Boolean>();
		
		boolean populated = false;
		
		for (int i = 0; i < POINTS; i++) {
			moddegree = (float) (degree*(start+end+ratio) + Math.pow(i, ratio)); //step across multiple bands
			
			for (int j = i; j < POINTS; j++) { //generate 256 degrees
				progressiveDegrees[j] = next_degree(ratio, moddegree, j); //prog degree changes each time
			}
			
			for (int d = 0; d < POINTS; d++) {
				
				if (used.get(Math.round(progressiveDegrees[d])) == null) {
					
					used.put(Math.round(progressiveDegrees[d]), true);
					
					new_block_indices[i] = Math.round(progressiveDegrees[d]);
					
					//if (ratiosadded + 1 == POINTS) ratiosadded--;
					if (ratiosadded < 64) ratiosteparray[ratiosadded++] = ratio*progressiveDegrees[d];
					//TODO ADDRESS ISSUE ^^^^ WHERE WE GENERATE > 64?!
					populated = true;
					break; //fallthrough
				}
				
			}
			
			
			
			/*
			 * Check if populated
			 * 
			 */
			
			int ratioretries = 0;
			
			float altratio = ratio/7;
			
			int retries = 0;
		
			if (new_block_indices[i] == 0) populated = false;
			
			while (!populated) {
				
				
				if (new_block_indices[i] == 0) {
					//System.out.println("NO INDICE FOR " + i + "!");
					
					//moddegree += 4.321;
					
					moddegree += (1/(end-start+altratio)/ratio);
					
				
					//moddegree %= end/start;
					//altratio += 1.234;
					
					altratio += ((end-start + altratio)/Math.pow(i, ratio));
					
					if (ratioretries > 256) {
						moddegree += i*((end*start+altratio))%(ratio+((start*end+0.1)));
						altratio += i*((end*start+altratio))%(ratio+((start*end+0.1)));
						
					}  else if (ratioretries >= 512) {
						
						moddegree *= Math.pow(1, altratio*1*start*.999f);
						altratio *= Math.pow(1, moddegree*1*start*.999f);
						
						ratioretries = 0;
					}
					
					if (moddegree > 45) moddegree %= 45f;
					if (altratio > .999) altratio %= .999f;
					
					//System.out.println("DEGREE > " + moddegree);
					//System.out.println("RATIO > " + altratio);
					
					//populated = true;
					
					for (int j = i; j < POINTS; j++) { //generate 256 degrees
						progressiveDegrees[j] = next_degree(altratio, moddegree, j); //prog degree changes each time
					}
					
					for (int d = 0; d < progressiveDegrees.length; d++) {
						
						if (used.get(Math.round(progressiveDegrees[d])) == null) {
							
							used.put(Math.round(progressiveDegrees[d]), true);
							
							new_block_indices[i] = (Math.round(progressiveDegrees[d]));
							if (ratiosadded < ratiosteparray.length) {
								ratiosteparray[ratiosadded++] = altratio;
							} else {
								break;
							}
							//System.out.println("FIXED " + i);
							populated = true;
							break; //fallthrough
							 
						}
						
					}
					
					//if (i == 129) populated = true;
				}
				
				ratioretries++;
				retries++;
				
				//if (i == 129) populated = true; //TODO ADDRESS THIS, CAN GENERATE UP TO 154 SWAPS WHICH IS MORE THAN HALF A BLOCK
			
				if (ratiosadded == 64) populated = true; //n > 64 access when not limiting..??
			} //TODO CHECK LOGIC FOR ARRAY INDEXES
			
		
			
		}
		
		int nblckct = 0;
		
		for (int i : new_block_indices) {
			if (i != 0) nblckct++;
		}
		
		int[] condense = new int[nblckct];
		nblckct = 0;
		for (int i = 0; i < POINTS; i++) {
			if (new_block_indices[i] != 0) {
				condense[nblckct++] = indexForValue(new_block_indices, i);
			}
		}
		
		
		/*	
		LinkedList ll = new LinkedList();
		
		ll.add(condense);
		ll.add(ratiosteparray);
		*/
		
		int bigint = 0;
		
		for (int i : condense) {
			
			bigint += i*i; //make BIG int
			//bigint++;
			
		}
		//System.out.print("]\n");
		
		//System.out.print("[");
		
		float bigfloat = 0f;
		for (float f : ratiosteparray) {
			//System.out.print(f + " ");
			bigfloat += f;
		}
		
		//System.out.print("]\n");
		
		//System.out.println("\nFUNC > " + bigint + "/" + bigfloat + "X");
	
		float derivedfunc = bigint * 1f / bigfloat;
		
		
		int[] full_new_index = new int[256];
		int newindex = 0;
		int remaptries = 0;
		boolean full = false;
		
		//newratio is sum of all ratios back from indices in span
		//func from span
		HashMap<Integer, Integer> placements = new HashMap<Integer, Integer>();
		
		
		//derivedfunc = 99.089f;
		while (!full) {
			for (int i = 0; i < full_new_index.length; i++) {
				newindex = ((int) (i* 1f * derivedfunc));
				
				if (placements.get(i) == null) {
					
					if (newindex < 255) {
						placements.put(i, newindex);
					} else {
						newindex = newindex % 255 + remaptries;
						placements.put(i, newindex);
					}
				}
			}
			
			if (placements.size() == 256) full = true;
			
			remaptries++;
		}
		
		for (int i = 0; i < 256; i++) {
			//System.out.println(placements.get(i));
			full_new_index[i] = placements.get(i);
		}
		
		//System.out.println("FUNC = " + derivedfunc + "X...");
		
		//use while loop to populate new array of indexes from function
		
		return full_new_index; //Returns 256 swap indices
	}
	
	/**
	 * [Internal Method] char_arr_to_str()
	 * Converts a char[] back to String.
	 * @param chars (Character[]) The chars to convert to String.
	 * @return (String) The string as a concatenation of the characters.
	 */
	
	private static String char_arr_to_str(char[] chars) {
		String ret = "";
		
		for (char c : chars) ret+= c;
		
		return ret;
	}
	
	/*
	 * Remap input of collisions to different characters from extended ASCII
	 */
	
	/**
	 * [Internal Method] inputfixup()
	 * Called to remap input into SPAN to fix collisions.
	 * @param phrase (String) The phrase to remap characters in.
	 * @return (String) A safe string to be encrypted in SPAN.
	 */
	
	private static String inputfixup(String phrase) {
		
		char[] altphrase = phrase.toCharArray();
		for (int i = 0; i < phrase.length(); i++) {

			if (phrase.charAt(i) == ('0')) { //use extended ASCII, possible security flaw unless we include more extended ascii in block TODO CHECK
				altphrase[i] = '×';
			} else if (phrase.charAt(i) == ('1')) {
				altphrase[i] = 'œ';
			} else if (phrase.charAt(i) == ('2')) {
				altphrase[i] = '±';
			} else if (phrase.charAt(i) == ('3')) {
				altphrase[i] = 'Ž';
			} else if (phrase.charAt(i) == ('4')) {
				altphrase[i] = '¡';
			} else if (phrase.charAt(i) == ('5')) {
				altphrase[i] = '§';
			} else if (phrase.charAt(i) == ('6')) {
				altphrase[i] = '¿';
			} else if (phrase.charAt(i) == ('7')) {
				altphrase[i] = '³';
			} else if (phrase.charAt(i) == ('8')) {
				altphrase[i] = '¹';
			} else if (phrase.charAt(i) == ('9')) {
				altphrase[i] = 'Ë';
			} else if (phrase.charAt(i) == ('g')) {
				altphrase[i] = 'ƒ';
			} else if (phrase.charAt(i) == ('!')) { //extend special chars
				altphrase[i] = 'þ';
			} else if (phrase.charAt(i) == ('@')) {
				altphrase[i] = 'ÿ';
			} else if (phrase.charAt(i) == ('#')) {
				altphrase[i] = 'ú';
			} else if (phrase.charAt(i) == '$') {
				altphrase[i] = 'ù';
			} else if (phrase.charAt(i) == '%') {
				altphrase[i] = 'ö';
			} else if (phrase.charAt(i) == '^') { //collision with x
				altphrase[i] = 'ø';
			} else if (phrase.charAt(i) == '&') {
				altphrase[i] = 'ç';
			} else if (phrase.charAt(i) == '*') {
				altphrase[i] = 'Á';
			} else if (phrase.charAt(i) == '(') {
				altphrase[i] = 'Ã';
			} else if (phrase.charAt(i) == ')') {
				altphrase[i] = 'Å';
			} else if (phrase.charAt(i) == '-') {
				altphrase[i] = 'Õ';
			} else if (phrase.charAt(i) == '_') {
				altphrase[i] = 'Ù';
			} else if (phrase.charAt(i) == '+') {
				altphrase[i] = 'Ó';
			} else if (phrase.charAt(i) == '=') {
				altphrase[i] = 'Ñ';
			} else if (phrase.charAt(i) == '`') {
				altphrase[i] = '÷';
			} else if (phrase.charAt(i) == '{') {//final full specials
				altphrase[i] = '˜'; //not ~
			} else if (phrase.charAt(i) == '}') {
				altphrase[i] = '«';
			} else if (phrase.charAt(i) == '|') {
				altphrase[i] = '¼';
			} else if (phrase.charAt(i) == '\\') {
				altphrase[i] = '½';
			} else if (phrase.charAt(i) == ':') {
				altphrase[i] = 'Ö';
			} else if (phrase.charAt(i) == ';') {
				altphrase[i] = 'Û';
			} else if (phrase.charAt(i) == '"') {
				altphrase[i] = 'å';
			} else if (phrase.charAt(i) == '\'') {
				altphrase[i] = 'ë';
			} else if (phrase.charAt(i) == '<') {
				altphrase[i] = 'µ';
			} else if (phrase.charAt(i) == ',') {
				altphrase[i] = '¥';
			} else if (phrase.charAt(i) == '>') {
				altphrase[i] = '¯';
			} else if (phrase.charAt(i) == '.') {
				altphrase[i] = 'Ý';
			} else if (phrase.charAt(i) == '?') {
				altphrase[i] = 'ó';
			} else if (phrase.charAt(i) == '/') {
				altphrase[i] = 'ï';
			}
					
					
		}

		//String phraseogswapback = phrase;
		phrase = char_arr_to_str(altphrase);
		
		return phrase;
	}
	
	/**
	 * [Internal/External TEST] repl()
	 * Used for testing some parts of the program easily.
	 */
	
	public static void repl() {
		boolean run = true;
		
		String[] menu = new String[] {"Encryptv1(DEPRECATED)","Decrypt(DEPRECATED)","Evaluate", "Encryptv2", "Decryptv2"};
		Scanner input = new Scanner(System.in);
		while (run) {
			
			
			
			System.out.println("Welcome to SPAN (Toy Cipher, Alpha) - Joshua Loysch");
			System.out.println("Please choose a menu option: ");
			System.out.print("\n[0] " + menu[0] + "\n[1] " + menu[1] + "\n[2] " + menu[2] + "\n[3] " + menu[3] + "\n[4] " + menu[4] + "\n\n>> ");
			
			
			
			String choice = input.next();
			float ratio = (float) 0.0;
			int blocksize;
			String phrase;

			switch (choice) {
				case "0":
					
					if (true) break;
					
					
					boolean verified = false;				
					
					while (!verified) {
						input.nextLine(); //Consume newline character 
						
						System.out.println("Enter a phrase to encrypt:");
						
						phrase = input.nextLine();
						
						System.out.println("Enter your skew ratio:\n>> ");
						
						ratio = input.nextFloat();
						
						input.nextLine();
						
						System.out.print("Finally, enter the desired block size:\n>> ");
						blocksize = input.nextInt();
						System.out.println("Encrypting '" + phrase + "'...");
						
						input.nextLine();
						/*
						char[] altphrase = phrase.toCharArray();
						for (int i = 0; i < phrase.length(); i++) {
			
							if (phrase.charAt(i) == ('0')) { //use extended ASCII, possible security flaw unless we include more extended ascii in block TODO CHECK
								altphrase[i] = '×';
							} else if (phrase.charAt(i) == ('1')) {
								altphrase[i] = 'œ';
							} else if (phrase.charAt(i) == ('2')) {
								altphrase[i] = '±';
							} else if (phrase.charAt(i) == ('3')) {
								altphrase[i] = 'Ž';
							} else if (phrase.charAt(i) == ('4')) {
								altphrase[i] = '¡';
							} else if (phrase.charAt(i) == ('5')) {
								altphrase[i] = '§';
							} else if (phrase.charAt(i) == ('6')) {
								altphrase[i] = '¿';
							} else if (phrase.charAt(i) == ('7')) {
								altphrase[i] = '³';
							} else if (phrase.charAt(i) == ('8')) {
								altphrase[i] = '¹';
							} else if (phrase.charAt(i) == ('9')) {
								altphrase[i] = 'Ë';
							}
						}
						*/
						String phraseogswapback = phrase;
						phrase = inputfixup(phrase); //do number replacements
						
						
						System.out.println("New Phrase > " + phrase);
						
						String[] bins = to_padded_list(to_binary_list(phrase));
						
						phrase = phraseogswapback; //necessary for the verification check!
						
						for (String b : bins) {
							System.out.println("BINS > " + b);
						}
						
						//System.out.println(encrypt_bins(bins, blocksize, (float) ratio));
						
						String[] encrypted = encrypt_bins(bins, blocksize, (float) ratio);
						
						System.out.print("\n\n\n");
						
						String[] verify = decrypt_string(encrypted[0], blocksize, ratio, Float.parseFloat(encrypted[1]));
						
						verified = true;
						String verif_str = "";
						
						for (String s : verify) {
							System.out.println("Verifying..");
							System.out.println("\t" + binary_to_char(sum_to_binary(Integer.parseInt(s))));
							verif_str+=binary_to_char(sum_to_binary(Integer.parseInt(s)));
						}
						System.out.println("Verified.");
						
						
						
						if (verif_str.equals(phrase)) {
							//System.out.println("\n\nOK!");
							
							System.out.println("\nENCRYPTION COMPLETED AND REVERSIBILITY VERIFIED! DETAILS BELOW:\n\n>--- BEGIN ---<");
							System.out.println(encrypted[0]);
							System.out.println(encrypted[1]+"degsR\\\\"+ratio+"\\\\"+8);
							System.out.println(">--- END, PLEASE KEEP FOR YOUR RECORDS ---<\n\n");
							verified = true;
						} else {
							verified = false;
							System.out.println("VERIFICATIONMISMATCH!");
							System.out.println("GOT >\n" + verif_str);
							System.out.println("EXPECTED >\n" + phrase);
							System.exit(-1);
						}
					}
					
					
					
//TODO HERE
					break;
				case "1":
					
					if (true) break;
					
					input.nextLine(); //Consume newline character 
					
					System.out.println("Enter a phrase to decrypt:");
					
					phrase = input.nextLine();
					
					System.out.println("Enter the degree:");
					
					
					float degree = input.nextFloat();
					

					input.nextLine();
					
					System.out.println("Enter the skew ratio: ");
					
					ratio = input.nextFloat();
					
					
					
					input.nextLine();
					
					System.out.println("Enter the cipher block size: ");
					
					blocksize = input.nextInt();
					
					
					
					System.out.println("\nUnencrypting '" + phrase + "'...");
					
					input.nextLine();
					
					String[] decrypted = decrypt_string(phrase, blocksize, ratio, degree);
					
					String dcstr = "";
					System.out.println("\nDECRYPTION OUTPUT >\n");
					for (String s : decrypted) {
						System.out.println("\t" + s + " >> " + binary_to_char(sum_to_binary(Integer.parseInt(s))));
						dcstr+= binary_to_char(sum_to_binary(Integer.parseInt(s)));
					}
					System.out.println("ASSTR >\n" + dcstr);
					
					System.out.println("\n>--- END DECRYPTION OUTPUT ---<\n");
					
					//System.out.println("SIZE DECRYPTED " + decrypted.length);
					break;
				case "2":
					break;
				case "3":
					verified = false;				
					
					while (!verified) {
						input.nextLine(); //Consume newline character 
						
						System.out.println("Enter a phrase to encrypt:");
						
						phrase = input.nextLine();
						
						System.out.println("Enter your skew ratio:\n>> ");
						
						ratio = input.nextFloat();
						
						input.nextLine();
						
						System.out.print("Finally, enter the desired block size:\n>> ");
						blocksize = input.nextInt();
						System.out.println("Encrypting '" + phrase + "'...");
						
						input.nextLine();
						
						
						String phraseogswapback = phrase;
						phrase = inputfixup(phrase); //do number replacements
						
						
						System.out.println("New Phrase > " + phrase);
						
						//String[] bins = to_padded_list(to_binary_list(phrase));
						
						
						
						
						System.out.println("NEWPHRASE > " + phrase);
						
						String[] bins = to_padded_list(to_binary_list(phrase));
						
						phrase = phraseogswapback; //necessary for the verification check!
						
						/*
						 * MODIFY PHRASE TO REPLACE 1 WITH 'ONE'
						 */
						
						
						
						for (String b : bins) {
							System.out.println("BINS > " + b);
						}
						//System.out.println(encrypt_bins(bins, blocksize, (float) ratio));
						
						String[] encrypted = encrypt_bins(bins, blocksize, (float) ratio);
						
						//int preambleSize = getPreambleSizeFor(100);
						//String preamble = generatePreambleFor(preambleSize);
						
						System.out.print("\n\n\n");
						
						String[] verify = decrypt_string(encrypted[0], blocksize, ratio, Float.parseFloat(encrypted[1]));
						
						verified = true;
						String verif_str = "";
						for (String s : verify) {
							System.out.println("VERIFYING STRING '" + s + "'");
							System.out.println("\t" + binary_to_char(sum_to_binary(Integer.parseInt(s))));
							verif_str+=binary_to_char(sum_to_binary(Integer.parseInt(s)));
						}
						
						if (verif_str.equals(phrase)) {
							//System.out.println("\n\nOK!");
							
							System.out.println("\nENCRYPTION COMPLETED AND REVERSIBILITY VERIFIED! DETAILS BELOW:\n\n>--- BEGIN ---<");
							System.out.println(encrypted[0]);
							System.out.println(encrypted[1]+"degsR\\\\"+ratio+"\\\\"+8);
							System.out.println(">--- END, PLEASE KEEP FOR YOUR RECORDS ---<\n\n");
							verified = true;
						} else {
							verified = false;
						}
						
						
						/*
						 * NOW HERE IS WHERE WE MAKE IT INTERESTING WITH PHASE 1 ADDITION, THE BLOCK!
						 */
						
						//TODO Convert to method
						
						String[][] cm = fitToPreamble(encrypted[0], generatePreambleFor(256));
						
						String[] bigBlock = cm[0];
						
						String rawfitencryptnoshufflecu = "";
						for (String s : bigBlock) {
							//System.out.println("BLOCK > " + s);
							rawfitencryptnoshufflecu+=s;
						}
						
						System.out.println("CRYPT_BASE_PHRASE_PRE_FIT_AND_SHUFFLE > " + encrypted[0]);
						
						System.out.println("NOSHUFFLE Cu > " + rawfitencryptnoshufflecu);
						
						/*
						int[] replaces = generate_replacement_indices_for((float) 0.8, Integer.valueOf(cm[1][0]), Integer.valueOf(cm[2][0]), 256);
						
						String[] shuffled_cipher = new String[replaces.length]; 
						
						
						for (int i = 0; i < shuffled_cipher.length; i++) {
							shuffled_cipher[i] = cm[0][replaces[i]];
						}
						
						String cryptstrforshuffle = "";
						
						for (String s : shuffled_cipher) {
							//System.out.println("AFTERBLOCK > " + s);
							cryptstrforshuffle +=s;
						}
						
						System.out.println("SHUFFLE > " + cryptstrforshuffle);
						*/
						
						
						/* UNSHUFFLE STEP *?
						 */
						
						//TODO FIX SHUFFLING IN BLOCKS
						
						//String[] unshuffledcipher = unshufflecipherblock(cryptstrforshuffle, (float) ratio, Integer.parseInt(cm[1][0]), Integer.parseInt(cm[2][0]), 256 );
						
						
						String[] getCryptFromBlock = getcryptfromblock(cm[0], Integer.parseInt(cm[1][0]), Integer.parseInt(cm[2][0]));
						
						System.out.println("CRYPTFROMBLOCK > " + getCryptFromBlock[0]);
						
						String testreshufflestr = "";
						
						for (String s : getCryptFromBlock) {
							testreshufflestr += s;
						}
						
						System.out.println("CRYPT_BASE_PHRASE_FROM_GET > " + testreshufflestr);
						
						decrypted = decrypt_string(testreshufflestr, blocksize, ratio, Float.parseFloat(encrypted[1]));
						String dcassstr = "";
						System.out.println("\nDECRYPTION OUTPUT >\n");
						for (String s : decrypted) {
							System.out.println("\t" + s + " >> " + binary_to_char(sum_to_binary(Integer.parseInt(s))));
							dcassstr += binary_to_char(sum_to_binary(Integer.parseInt(s)));
						}
						System.out.println("\nASSTR > " + dcassstr);
						
						System.out.println("\n>--- END DECRYPTION OUTPUT ---<\n");
						
								
						System.out.println("START > " + cm[1][0]);
						
						System.out.println("END > " + cm[2][0]);
						System.out.println("CIPHER >>\n\n");
						System.out.println("---BEGIN:");
						System.out.println(rawfitencryptnoshufflecu);
						System.out.println("--END CIPHER--");
						System.out.println("\nYour Token > //" + encrypted[1] + "D" + ratio + "R" + cm[1][0] + "S" + cm[2][0] + "F\\\\\n\n");
						
						System.out.println("Save ? [y/n]");
						
						choice = input.next(); 
						
						if (choice.equalsIgnoreCase("y")) {
						 try {
						      FileWriter myWriter = new FileWriter("crypt.txt");
						      myWriter.write(rawfitencryptnoshufflecu.toCharArray());
						      myWriter.close();
						      myWriter = new FileWriter("key.txt");
						      myWriter.write("//" + encrypted[1] + "D" + ratio + "R" + cm[1][0] + "S" + cm[2][0] + "F\\\\");
						      myWriter.close();
						      System.out.println("Successfully wrote to the files.\n\n");
						    } catch (IOException e) {
						      System.out.println("An error occurred.\n\n");
						      e.printStackTrace();
						    }
						}
					}
					
					break;
				case "4":
					
					System.out.println("Please enter the cipher >>");
					String cipher = input.next();
					System.out.println("Please enter your decrypt key in the format '//...\\\\");
					choice = input.next();
					
					String reformatted = choice.substring(2, choice.length()-2); //Chop off
					
					System.out.println("CHOP > " + reformatted);
					
					//String crypt, int blocksize, float TRI_RATIO, float start
					
					//String[] res = decrypt_string (reformatted, r deg start)
					
					//   //38.184643D0.888R153S159F\\ - suggested format, also implies it's shrinking something in so it's a SPAN key!
					
					int blck_ct = 0;
					String[] tokens = new String[4];
					int lastindex = 0;
					
					for (int i = 0; i < reformatted.length(); i++) { //2nd pass format and transfer
						if (Character.isLetter(reformatted.charAt(i))) {

							tokens[blck_ct++] = reformatted.substring(lastindex, i);
							lastindex = i+1;
						}
					}
					
					/*
					 * tokens:
					 * 	0 - degree
					 * 	1 - ratio
					 * 	2 - start
					 * 	3 - end
					 */
					
					for (String s : tokens) {
						System.out.println("KEYTOK > " + s);
					}
					
					/*
					//String[] ciphertokens = cryptstring_to_array(cipher);
					//String[] mintokens = new String[Integer.parseInt(tokens[3])-Integer.parseInt(tokens[2])];
					//int mintokstep = 0;
					
					//for (int i = Integer.parseInt(tokens[2]); i < Integer.parseInt(tokens[3]); i++) { //copy over minimum
					//	mintokens[mintokstep++] = ciphertokens[i];
					}
					
					String recollectedmintok = "";
					
					for (String s : mintokens) recollectedmintok += s + "A"; //TODO Check if missing block labels again for whatever reason
					
					//TODO AGAIN, FIX BLOCK LABELS?! Depending on how I implement how I want it to go next I jut may not..
					*/
					
					/*
					 * decrypt_string(String crypt, int blocksize, float TRI_RATIO, float start)
					 */
					
					//TODO BIG TODO JUST MAKE THE BLOCKSIZE 8 ?!
					
					//System.out.println("MIN > " + recollectedmintok);
					
					//String[] res = decrypt_string (recollectedmintok, 8, Float.parseFloat(tokens[1]), Float.parseFloat(tokens[0]));
				
					System.out.println("Cryptfromblockstart = " + tokens[2]);
					System.out.println("Cryptfromblockend = " + tokens[1]);
					String[] cipherasarray = cryptstring_to_array(cipher);
					
					
					
					char blockdenoter = 'A'; //TODO BLOCKPADFIXUP
					
					for (int i = 0; i < cipherasarray.length; i++) {
						
						if (!((cipherasarray[i].charAt( cipherasarray[i].length()-1) == blockdenoter))) {
							cipherasarray[i] = cipherasarray[i] + blockdenoter;
						}
					}
					
					for (String s : cipherasarray) {
						System.out.println("CIPHERTOKENTOARRAY> " + s);
					}
					
					String[] getCryptFromBlock = getcryptfromblock(cipherasarray, Integer.parseInt(tokens[2]), Integer.parseInt(tokens[3]));
					
					System.out.println("CRYPTFROMBLOCK > " + getCryptFromBlock[0]);
					
					String getcrypt = "";
					
					for (String s : getCryptFromBlock) {
						getcrypt += s;
					
					}
					
					System.out.println("CRYPT_BASE_PHRASE_FROM_GET > " + getcrypt);
					
					System.out.println("USE RATIO " + Float.parseFloat(tokens[1]));
					System.out.println("USE BLOCK " + 8);
					System.out.println("USE DEGREE " + Float.parseFloat(tokens[0]));
					
					
					
					decrypted = decrypt_string(getcrypt, 8, Float.parseFloat(tokens[1]), Float.parseFloat(tokens[0])); //TODO STATIC BLOCKSIZE????!! 8?!
					
					System.out.println("\nDECRYPTION OUTPUT >\n");
					dcstr = "";
					for (String s : decrypted) {
						System.out.println("\t" + s + " >> " + binary_to_char(sum_to_binary(Integer.parseInt(s))));
						dcstr += binary_to_char(sum_to_binary(Integer.parseInt(s)));
					}
					System.out.println("\nASSTR >\n" + dcstr);
					
					System.out.println("\n>--- END DECRYPTION OUTPUT ---<\n");
					
					/*
					System.out.println("\nDECRYPTION OUTPUT >\n");
					
					for (String s : res) { 
						System.out.println("\t" + s + " >> " + binary_to_char(sum_to_binary(Integer.parseInt(s))));
					}
					System.out.println("\n>--- END DECRYPTION OUTPUT ---<\n");
					*/
					break;
				default:
					break;
			}
			
			
			
		}
		input.close();
	}
	
	/* run internal when public */
	
	/*
	 * TODO CLEANUP TESTING METHODS
	 */
	
	/**
	 * DEBUG-PURPOSE TESTING
	 */
	 
	private static void testa() {
		//repl();
		//generate_replacement_indices_for((float) 0.8, 28, 56, 256);
		
		//generatePreambleFor(256);
		
		int[] replace = generate_replacement_indices_for((float) 20.846724, (float) 0.888, 118, 200);
		
		//0 is replacements array int[]
		//1 is ratio steps array for func
		
	
		/*
		for (int i = 0; i < ((int[]) replace.get(0)).length; i++) {
			System.out.println("Replace index " + i + " with index " + ((int[]) replace.get(0))[i]);
		}
		*/
		int bigint = 0;
		System.out.print("[");
		for (int i : replace) {
			System.out.print(i + " ");
			bigint += i*i; //make big int
			
		}
		System.out.print("]\n");
		
		System.out.print("[");
		
		/*
		float bigfloat = 0f;
		for (float f : (float[]) replace.get(1)) {
			System.out.print(f + " ");
			bigfloat += f;
		}
		*/
		
		System.out.print("]\n");
		
		//System.out.println("\nFUNC > " + bigint + "/" + bigfloat + "X");
	
		//float derivedfunc = bigint * 1f / bigfloat;
		
		System.out.print("\n\n[");
		
		/*
		 * Generate the
		 */
		
		/*
		int newindex = 0;
		for (int i = 0; i < 256; i++) {
			newindex = ((int) (i* 1f * newratio));
			
			
			if (newindex > 255) {
				newindex = newindex%255 + 1;
			}
			
			System.out.print(newindex + " ");
		}
		System.out.print("]\n\n");
		*/
		
		//making a loop
		
		int[] full_new_index = new int[256];
		int newindex = 0;
		int remaptries = 0;
		boolean full = false;
		
		//newratio is sum of all ratios back from indices in span
		//func from span
		HashMap<Integer, Integer> placements = new HashMap<Integer, Integer>();
		
		//derivedfunc = 99.089f;
		
		/*
		while (!full) {
			for (int i = 0; i < full_new_index.length; i++) {
				newindex = ((int) (i* 1f * derivedfunc));
				
				if (placements.get(i) == null) {
					
					if (newindex < 255) {
						placements.put(i, newindex);
					} else {
						newindex = newindex % 255 + remaptries;
						placements.put(i, newindex);
					}
				}
			}
			
			if (placements.size() == 256) full = true;
			
			remaptries++;
		}
		
		for (int i = 0; i < 256; i++) {
			System.out.println(placements.get(i));
		}
		*/
		
		//System.out.println("FUNC = " + derivedfunc + "X...");
		//use while loop to populate new array of indexes from function
	}
	
	/**
	 * [Internal Method] test_shuffle()
	 * Test SPANs internal shuffling.
	 */
	
	private static void test_shuffle() {
		int[] replace = generate_replacement_indices_for((float) 20.846723, (float) 0.888, 118, 200);
		//System.out.println("INDICE ARRAY >");
		for (int i : replace) {
			//System.out.println(i);!
		}
		
		String[] test = SPAN.encrypt("HELLO", (float) 0.888, 8, false);
		
		String[] ciphertokens = test[0].split("A"); //TODO CHECK BLOCK LABELS
			
		String[] shuffle = shufflecipherblock(ciphertokens, replace);
		
		for (String s : shuffle) {
			System.out.println("SHUFFLE > " + s);
		}
		
		for (int r : replace) {
			System.out.println("REPLACE " + r);
		}
		String[] unshuffle = unshufflecipherblock(shuffle, replace); //TODO Externalize to derive func just testing
		
		String test_str = "";
		for (String s : unshuffle) {
			System.out.println("UNSHUFFLE > " + s);
			test_str += s;
		}
		
		//test decrypt moment of truth
		
		
		String dc = SPAN.decrypt(test_str, test[1]);
		
		System.out.println("DECRYPT > '" + dc + "'");
	}
	
	/**
	 * [Internal Method] testx()
	 * DEBUG ONLY
	 */
	
	public static void textx() {
		
		String[][] shuffle = encrypt_shuffled("HELLO", 0.888f, false);
		
		for (String s : shuffle[0]) {
			System.out.println(s);
		}
		
	
		System.out.println(shuffle[1][0]); //Print key
		
		
		String[] unshuffle = unshufflecipherblock(shuffle[0], 
				(generate_replacement_indices_for((float) 20.846723, (float) 0.888, 118, 200)) 
				); //TODO Externalize to derive func just testing
		
		String test_str = "";
		for (String s : unshuffle) {
			//System.out.println("UNSHUFFLE > " + s);
			test_str += s;
		}
		
		//test decrypt moment of truth
		
		
		String dc = SPAN.decrypt(test_str, shuffle[1][0]);
		
		System.out.println("DECRYPT > '" + dc + "'");
		
		SPAN.decrypt_shuffled(test_str, shuffle[1][0]);
	}
	
	/**
	 * [Internal/External Method] blockPhrase
	 * @param phrase (String) The phrase to be converted to (String[])
	 * @return (String[]) The phrase as a String[]
	 */
	public static String[] blockPhrase(String phrase) {
		String[] ret;
		ret = phrase.split("A"); //TODO CHECK LABELS
		
		for (int i = 0; i < ret.length; i++) {
			ret[i] = ret[i] + 'A';
		}
		
		return ret;
		
	}
	
	/**
	 * [Internal Method] testshufflealt()
	 * DEBUG PURPOSES ONLY
	 */
	private static void testshufflealt() {
		//test_shuffle();

		String phrase = "HELLO";
		
		String[][] encrypt = encrypt_shuffled(phrase, 0.888f, false);
	
		String shuff = "";
		for (String s : encrypt[0]) shuff += s;
		
		System.out.println("TRYING DECRYPT");
		String decrypt = SPAN.decrypt_shuffled(shuff, encrypt[1][0]);
		
		System.out.println("DECRYPT DC > '" + decrypt + "'");
		System.out.println("KEY > '" + encrypt[1][0] + "'");
		if (decrypt.equals(phrase)) {
			System.out.println("VERIFIED!");
		} else {
			System.out.println("MISMATCH!");
		}
		
		System.exit(0);
	}
	
	/**
	 * [Internal Method] countUnique()
	 * Count unique generations / bad generations / collisions generated from a single phrase with one ratio.
	 * @param phrase (String) The phrase to test.
	 */
	private static void countUnique(String phrase) {
		int uniques = 0;
		HashMap<String, String> unique= new HashMap <String, String>();
		LinkedList collisions = new LinkedList();
		
		HashMap <String, String> badblocks = new HashMap <String, String>();
		
		HashMap <String, String> badgenerations = new HashMap <String, String>();
		
		int fails = 0, collided=0, runs=0;
		String comp = "", decrypt="";
		String[][] encrypt = null;
		
		long startTime = System.currentTimeMillis();
		
		while (true) {
			try {
				//test_shuffle();
				encrypt = encrypt_shuffled(phrase, 0.888f, false);
			
				String shuff = "";
				for (String s : encrypt[0]) shuff += s;
				
				
				//System.out.println("TRYING DECRYPT");
				decrypt = SPAN.decrypt_shuffled(shuff, encrypt[1][0]);
				
				comp = "";
				
				for (String s : encrypt[0]) comp+=s;
				
				
				//System.out.println("DECRYPT DC > '" + decrypt + "'");
				//System.out.println("KEY > '" + encrypt[1][0] + "'");
				if (decrypt.equals(phrase)) {
					//System.out.println("VERIFIED!");
					
					
					
					if (unique.get(comp) == null) {
						unique.put(comp, encrypt[1][0]);
						uniques++;
						//System.out.println("Unique encryption for '" + phrase + "' found. (Run " + runs + ")");
					} else {
						collisions.add(comp);
						collisions.add(encrypt[1][0]);
						collided++;
						//System.out.println("\tCollision on run " + runs);
					}
				} else {
					//System.out.println("MISMATCH!");
					//System.out.println("Bad block (Run " + runs +")");
					badblocks.put(comp, encrypt[1][0]);
					fails++;
				}
				runs++;
				
				
				System.out.println("'" + phrase + "' > " + uniques + " unique, " + fails + " bad blocks, " + collided + " collisions, " + badgenerations.size() + " bad generations, " + "run " + runs + " @ " + ((float)uniques/(float)runs)*100 + "% find rate. | t=" 
				+ ((System.currentTimeMillis()-startTime)/1000f) + "s");
				//System.exit(0);
			
				//System.out.println(uniques + ":" + runs);
			} catch (Exception e) {
				System.out.println("Passing error on run " + runs);
				badgenerations.put(comp, encrypt[1][0]);
				System.out.println("Passing..");			}
		}
	}
	
	public static void main(String args[]) {
		
		countUnique("HELLO");
	}

}
