import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class RSA {
	static boolean TESTING = false;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String command = args[0];
		if (command.equals("key")) {
			long p = Integer.valueOf(args[1]);
			long q = Integer.valueOf(args[2]);
			generateKey(p,q);
		} else {
			String inputFileName = args[1];
			String keyFile = args[2];
			String outputFileName = args[3];

			if (command.equals("encrypt")) {
				encrypt(inputFileName, keyFile, outputFileName);
			} else if (command.equals("decrypt")) {
				decrypt(inputFileName, keyFile, outputFileName);
			} else {
				System.out.println("Invalid input arguments given.");
			}
		}
	}

	public static String generateKey(long p, long q){
		assert(p > 0 && q > 0);
		assert(isPrime(p) && isPrime(q));
		long n = p * q;
		long phi = (p - 1) * (q - 1);
		long e = 0;
		long d = 4; // Seeded d with a non-prime number to ensure loop runs once, it will be reset		
		while(!isPrime(d)){
			e = findE(e + 1, phi);
			d = euclid(phi, e, n);
		}
		assert ((e > 1) && (phi % e != 0));
		String result = n + " " + e + " " + d;
		return result;
	}

	private static boolean isPrime(long x){
		int i = 2;
		while((i * i) - 1 < x){
			if(x % i == 0){
				return false;
			}
			i++;
		}
		return true;
	}

	private static long findE(long startE, long phi){
		while(phi % startE == 0){
			startE++;		
		}
		return startE;
	}

	/**
	 * input: phi value and e value where e is a public key
	 * output: d, where d is the private key
	 */
	protected static long euclid(long phi, long e, long n){
		assert(e > 1);
		assert(isPrime(e));
		assert(gcd(phi, e) == 1);
		long d;
		long[] top		= {1, 0, phi};
		long[] bottom 	= {0, 1, e};

		while (bottom[2] > 1) {
			long quotient = top[2] / bottom[2];
			long[] newBottom = new long[3];
			for(int i = 0; i < 3; i++) {
				newBottom[i] = top[i] - quotient * bottom[i];
			}
			top = bottom;
			bottom = newBottom;
			if (bottom[2] < 1 && TESTING) {
				System.out.println(">>>> Oh shit, bottom[2] below 1, value = " + bottom[2]);
			}
		}
		assert (bottom[2]) > 0;
		assert (bottom[0] * phi + bottom[1] * e == 1);

		d = bottom[1];
		if (d > n) {
			while (d > n) {
				d -= phi;
			}
		} else if (d < 0) {
			while (d < 0) {
				d += phi;
			}
		}
		assert (1 <= d && d < n);
		assert ((d * e) % phi == 1);
		assert (gcd(d, phi) == 1); // assert d is coprime of phi
		return d;
	}

	/**
	 * input: given two numbers left and right
	 * output: returns the greatest common denominator
	 */
	protected static long gcd(long left, long right) {
		// TODO: write this
		if (left == 0) {
			return right;
		}
		while (right != 0) {
			if (left > right) 	left  = left - right;
			else 				right = right - left;
		}
		return left;
	}
	/**
	 * Fast Exponentiation
	 * @param base
	 * @param exponent
	 * @param modVal
	 * @return returns (base^exponent) % modVal
	 */
	protected static long fastExponentiation(long base, long exponent, long modVal) {
		String binary = Long.toBinaryString(exponent);
		long c = 1; // seed value for result
		for (int i = 0; i < binary.length(); i++) {
			c = ((c * c) % modVal);
			if(binary.charAt(i) == '1') {
				c = (c * base) % modVal;
			}
		}
		return c;
	}

	protected static long[] getNEDValues(String keyFile) {
		String s = keyFile;
		long[] result = new long[3];
		try {
			Scanner sc = new Scanner(new File(keyFile));
			for (int i = 0; i < result.length; i++) {
				result[i] = sc.nextInt();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			if (TESTING) {
				System.out.println("File not found, will use raw string.");
			}
			String[] temp = keyFile.split("\\s+"); // delimits on whitespace or groups of whitespace
			for (int i = 0; i < temp.length; i++) {
				result[i] = Long.valueOf(temp[i]);
			}
		}
		return result;
	}

	public static void encrypt(String inputFileName, String keyFile, String outputFileName) {
		// acquire n, e, d values
		int LIMIT = 3; // # of bytes to encode at one time.
		long[] ned = getNEDValues(keyFile);
		long n = ned[0];
		long e = ned[1];
		boolean eofFound = false;
		// Get file set up
		try {
			FileInputStream finput = new FileInputStream(inputFileName);
			DataInputStream f = new DataInputStream(finput); 
			FileOutputStream outf = new FileOutputStream(outputFileName);
			// Pull sets of LIMIT from f and encrypt them
			int[] tuple = new int[LIMIT];
			byte[] translatedTuple; // the tuple to write 
			int message = 0;
			int t = 0;
			while(!eofFound) {
				for (int i = 0; i < LIMIT; i++) {
					try {
						t = f.readUnsignedByte();
					} catch (EOFException fu) {
						eofFound = true;
					}
					if (!eofFound) {
						tuple[i] =  t; // loads LIMIT # of elements into tuple
					} else { // if EOF is found
						while(i < LIMIT) { // fill rest of tuple with zeros
							tuple[i] = 0;
							i++;
						}
						f.close();
						finput.close();
						break;
					}
				}
				// combineTuple should return the message to be encoded
				message = combineTuple(tuple);
				if (TESTING) {
					System.out.println("Original input:     " + Arrays.toString(tuple));
					System.out.println("Message:            " + Integer.toBinaryString(message));
				}
				translatedTuple = getOutputMessage(message, e, n, LIMIT + 1);
				if (translatedTuple.length < LIMIT) {
					byte[] temp = new byte[LIMIT];
					int tempIndex = LIMIT - 1;
					for (int i = translatedTuple.length - 1; i >= 0; i--) {
						temp[tempIndex] = translatedTuple[i];
						tempIndex--;
						assert(tempIndex >= 0);
					}
					translatedTuple = temp;
				}
				if (TESTING) {
					int[] temp = new int[translatedTuple.length];
					for (int i = 0; i < translatedTuple.length; i++) {
						temp[i] = (int) translatedTuple[i];
					}
					int tmessage = combineTuple(temp);
					System.out.println("Translated Message: " + Integer.toBinaryString(tmessage));
					System.out.println("Sending to write: " + Arrays.toString(translatedTuple));
				}
				assert (translatedTuple.length <= LIMIT + 1);
				assert(translatedTuple.length >= LIMIT);
				writeByteTuple(translatedTuple, outf);
			}
			if (eofFound) {
				if (TESTING) {
					System.out.println("Found EOF.");
				}
				outf.close();
			} else {
				if (TESTING) {
					System.out.println("You have problems if you made it into this section without closing the file.");
				}
				assert(false);
			}
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			System.out.println("There was an error opening the file " + inputFileName);
			ex.printStackTrace();
		} catch (IOException r) {
			// TODO Auto-generated catch block
			System.out.println("IO Exception occurred while accessing f.");
			r.printStackTrace();
		}
	}
	
	/**
	 * Combine Tuple takes in an array of ints of arbitrary length
	 * @param currTuple: the array of ints
	 * @return returns the array of all the values of the ints inside the array concatenated together
	 * 	such that their binary representation is concatenated. The result is a new int taking on the 
	 *  value of the concatenation. Each int holds 8 places, so if it is less, it will prepend 0's.
	 *  Ex: currTuple => 			{1,2,3}
	 *  	bit representaiton => 	{1, 10, 101}
	 *  	concatenated =>			{000000010000001000000101}
	 *  	return int value =>		66053 (which is 10000001000000101 base 2 converted to base 10)
	 */
	protected static int combineTuple(int[] currTuple) {
		int result = 0;
		for (int val : currTuple) {
			result = (result << 8) + val;
		}
		return result;
	}

	/**
	 * Get Output Message takes in a message and applies fast exponentiation to it then outputs an array
	 * of bytes which should then be written to file.
	 * @param message: raw message that needs to be translated
	 * @param exponent: the private or public key
	 * @param mod: the n value
	 * @return a byte array containing the output limit number of bytes to be written to file
	 */
	protected static byte[] getOutputMessage(long message, long exponent, long mod, int size) {
		int CHUNK_SIZE = 8;
		long translatedMessage = fastExponentiation(message, exponent, mod);
		if (TESTING) {
			System.out.println("Inside gOM Message: " + Long.toBinaryString(translatedMessage));
		}
		int outputLimit = size;
		byte[] result = new byte[outputLimit];
		long mask = 255; // only want to get the right most 8 elements
		byte temp;
		for(int i = outputLimit - 1; i >= 0; i--) {
			temp = (byte) (translatedMessage & mask);
			result[i] = temp;
			translatedMessage = translatedMessage >>> CHUNK_SIZE;
		}
		return result;
	}
	
	/**
	 * Given a long num, will return the number of digits in its binary representation
	 * @param num: a number in decimal form
	 * @return an int giving the number of binary digits needed to represent num
	 */
	protected static int getLengthOfBinaryNumber(long num) {
		String s = Long.toBinaryString(num);
		return s.length();
	}
	
	protected static void writeByteTuple(byte[] currentTuple, FileOutputStream f) {
		boolean allZeros = true;
		for (byte b : currentTuple) {
			if (b != ((Integer) 0).byteValue()) {
				allZeros = false;
				break;
			}
		}
		if (!allZeros) {
			if (TESTING) {
				System.out.println("Write Message:" + Arrays.toString(currentTuple) + "\n");				
			}
			for (byte b : currentTuple) {
				try {
					f.write(b);
				} catch (IOException badf) {
					// TODO Auto-generated catch block
					System.out.println("Problems writing to file");
					badf.printStackTrace();
				}
			}
		}
	}

	public static void decrypt(String inputFileName, String keyFile, String outputFileName){
		int LIMIT = 4; // # of bytes to decode at one time.
		long[] ned = getNEDValues(keyFile);
		boolean eofFound = false;
		long n = ned[0];
		long d = ned[2];
		
		// Get file set up
		try {
			FileInputStream finput = new FileInputStream(inputFileName);
			DataInputStream f = new DataInputStream(finput);
			FileOutputStream outf = new FileOutputStream(outputFileName);
			// Pull sets of LIMIT from f and decrypt them
			int[] tuple = new int[LIMIT];
			int message;
			int temp = 0;
			byte[] bytesToWrite;
			while(!eofFound) {
				for (int i = 0; i < LIMIT; i++) {
					try {
						temp = f.readUnsignedByte();
					} catch (EOFException e) {
						eofFound = true;
					}
					if (eofFound) {
						while(i < LIMIT) {
							tuple[i] = 0;
							i++;
						}
						f.close();
						finput.close();
					} else {
						tuple[i] = temp;
					}
				}
				// combine the LIMIT number of bytes into a single message
				message = combineTuple(tuple);
				// decode the message
				bytesToWrite = getOutputMessage(message, d, n, LIMIT - 1);
				
				// write bytes in tuple to file
				if (!eofFound) {
					outf.write(bytesToWrite);
					//writeByteTuple(bytesToWrite, outf);
					if (TESTING) {
						int[] t = new int[bytesToWrite.length];
						for (int i = 0; i < bytesToWrite.length; i++) {
							t[i] = (int) bytesToWrite[i];
						}
						int tmessage = combineTuple(t);
						System.out.println("Translated Message: " + Integer.toBinaryString(tmessage));
						System.out.println("Sending to write:   " + Arrays.toString(t));
					}
				}
			}
			if (eofFound) {
				if (TESTING) {
					System.out.println("Found EOF.");
				}
				outf.close();
			} else {
				if (TESTING) {
					System.out.println("You have problems if you made it into this section without closing the file.");
				}
				assert(false);
			}
		} catch (FileNotFoundException ex) {
			// TODO Auto-generated catch block
			System.out.println("There was an error opening the file " + inputFileName);
			ex.printStackTrace();
		} catch (IOException r) {
			// TODO Auto-generated catch block
			System.out.println("IO Exception occurred while accessing f.");
			r.printStackTrace();
		}
	}
}
