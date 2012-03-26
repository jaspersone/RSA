import java.io.*;
import java.util.Arrays;

public class RSA {
	static boolean TESTING = false;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		String[] temp = keyFile.split("\\s+"); // delimits on whitespace or groups of whitespace
		assert(temp.length == 3); // make sure the given keyFile is of proper length
		long[] result = new long[temp.length];
		for (int i = 0; i < temp.length; i++) {
			result[i] = Long.valueOf(temp[i]);
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
			FileInputStream f = new FileInputStream(inputFileName);
			FileOutputStream outf = new FileOutputStream(outputFileName);
			// Pull sets of LIMIT from f and encrypt them
			int[] triple = new int[LIMIT];
			while(!eofFound) {
				for (int i = 0; i < LIMIT; i++) {
					int temp = f.read();
					if (temp != -1) {
						triple[i] =  ((Integer) temp).byteValue();
					} else {
						while(i < LIMIT) {
							triple[i] = 0;
							i++;
						}
						eofFound = true;
						f.close();
						break;
					}
				}
				if (TESTING) {
					System.out.println("Sending to write: " + Arrays.toString(triple));
				}
				writeCurrentTuple(LIMIT, e, n, triple, outf);
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
	
	protected static int combineTuple(int[] currTuple) {
		int limit = currTuple.length;
		int result = 0;
		int temp;
		for (int i = 0; i < limit; i++) {
			temp = ((Integer) currTuple[i]).byteValue();
			result = result << 8;
			result += temp;
		}
		return result;
	}

	protected static byte[] getOutputMessage(long em, int limit){
		byte[] result = new byte[limit + 1];
		long mask = 255;
		int temp;
		for(int i = limit; i >= 0; i--){
			temp = (int) (em & mask);
			result[i] = ((Integer) temp).byteValue();
			em = em >>> 8;
		}
		return result;
	}
	
	/**
	 * Encode/Decode takes in a message and applies fast exponentiation to it then outputs an array
	 * of bytes which should then be written to file.
	 * @param message: raw message that needs to be translated
	 * @param exponent: the private or public key
	 * @param mod: the n value
	 * @param outputLimit: the limit to the size of the array to be returned
	 * @return a byte array containing the output limit number of bytes to be written to file
	 */
	protected static byte[] encodeDecode(long message, long exponent, long mod, int outputLimit) {
		long translatedMessage = fastExponentiation(message, exponent, mod);
		return getOutputMessage(translatedMessage, outputLimit);
	}
	
	/**
	 * Write Current Tuple to File
	 * @param limit: # of bytes to write at one time
	 * @param ned: n = limit, e = public key, d = private key
	 * @param currTriple: set of numbers to pack and encode
	 * @param f: output file object
	 * @return true if end of file is found, false otherwise
	 */
	protected static void writeCurrentTuple(int limit, long exponent, long mod, int[] currTuple, FileOutputStream f) {
		assert(f != null);
		long n = mod;
		long e = exponent;
		// combine the currTuples into a single term using bit shifting
		int word = combineTuple(currTuple);
		if (TESTING) {
			System.out.println("Word to encrypt:  " + Integer.toBinaryString(word));
		}
		assert(word < n); // insure this message is encode safe
		// encrypt message
		long encryptedMessage = fastExponentiation(word, e, n);
		String em = Long.toBinaryString(encryptedMessage);
		int length = em.length();
		assert(length <= 32); // make sure the number did not overflow our container
		
		// get output message ready for writing
		byte[] output = getOutputMessage(encryptedMessage, limit);

		// break encrypted message into 4 bytes, then write to f
		// with the exception when all are zeros
		boolean allZeros = true;
		for (byte b : output) {
			if (b != ((Integer) 0).byteValue()) {
				allZeros = false;
				break;
			}
		}
		if (!allZeros) {
			if (TESTING) {
				System.out.println("Encrypted Message:" + em);				
			}
			for (byte b : output) {
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
				System.out.println("Write Message:" + Arrays.toString(currentTuple));				
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
			FileInputStream f = new FileInputStream(inputFileName);
			FileOutputStream outf = new FileOutputStream(outputFileName);
			// Pull sets of LIMIT from f and decrypt them
			int[] tuple = new int[LIMIT];
			int message;
			byte[] bytesToWrite;
			while(!eofFound) {
				for (int i = 0; i < LIMIT; i++) {
					int temp = f.read();
					tuple[i] = temp;
					if (temp == -1) {
						eofFound = true;
					}
				}
				// combine the LIMIT number of bytes into a single message
				message = combineTuple(tuple);
				// decode the message
				bytesToWrite = encodeDecode(message, d, n, LIMIT);
				// write bytes in tuple to file
				writeByteTuple(bytesToWrite, outf);
				if (TESTING) {
					System.out.println("Sending to write: " + Arrays.toString(bytesToWrite));
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
