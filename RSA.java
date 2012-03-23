import java.io.*;
import java.util.Arrays;

public class RSA {

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
			if (bottom[2] < 1) {
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
		long[] ned = getNEDValues(keyFile);
		boolean eofFound = false;
		// Get file set up
		try {
			FileInputStream f = new FileInputStream(inputFileName);
			FileOutputStream outf = new FileOutputStream(outputFileName);
			// Pull sets of 3 from f and encrypt them
			int[] triple = new int[3];
			while(!eofFound) {
				for (int i = 0; i < 3; i++) {
					int temp = f.read();
					if (temp != -1) {
						triple[i] =  ((Integer) temp).byteValue();
					} else {
						while(i < 3) {
							triple[i] = -1;
							i++;
						}
						f.close();					
					}
				}
				System.out.println("Sending to write: " + Arrays.toString(triple));
				eofFound = writeCurrentTuple(3, ned, triple, outf);
			}
			if (eofFound) {
				System.out.println("Found EOF.");
			}
			f.close();
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
	 * Write Current Tuple to File
	 * @param limit: # of bytes to write at one time
	 * @param ned: n = limit, e = public key, d = private key
	 * @param currTriple: set of numbers to pack and encode
	 * @param f: output file object
	 * @return true if end of file is found, false otherwise
	 */
	protected static boolean writeCurrentTuple(int limit, long[] ned, int[] currTriple, FileOutputStream f) {
		assert(ned.length == limit);
		assert(currTriple.length == limit);
		assert(f != null);
		boolean eofFound = false;
		long n = ned[0];
		long e = ned[1];
		long d = ned[2];
		int word = 0;
		for (int i = 0; i < limit; i++) {
			int temp = ((Integer) currTriple[i]).byteValue();
			if (currTriple[i] == -1) {
				System.out.println("Inside write, found EOF");
				eofFound = true;
				break;
			} else if (i == limit - 1) {
				word += temp;
			} else {
				word += temp << ((limit - (i + 1)) * 8);
			}
		}
		System.out.println("Word to encrypt:  " + Integer.toBinaryString(word));
		assert(word < ((Long) n).byteValue()); // insure this message is encode safe
		// encrypt message
		long encryptedMessage = fastExponentiation(word, e, n);
		String em = Long.toBinaryString(encryptedMessage);
		System.out.println("Encrypted Message:" + em);
		int length = em.length();
		assert(length <= 32); // make sure the number did not overflow our container
		byte[] output = new byte[4];
		// grab first chunk
		int offset = 8 - (32 - length);
		assert(offset >= 0);
		if (offset == 0) {
			output[0] = 0;
		} else {
			output[0] = ((Integer) Integer.parseInt(em.substring(0, offset))).byteValue();
		}
		// grab rest of chunks
		for (int i = 1; i < limit; i++) {
			output[i] = ((Integer) Integer.parseInt(em.substring(offset, offset + 8))).byteValue();
			offset += 8;
		}
		// break encrypted message into 4 bytes, then write to f
		for (byte b : output) {
			try {
				if (b != 0) {
					f.write(b);
				}
			} catch (IOException badf) {
				// TODO Auto-generated catch block
				System.out.println("Problems writing to file");
				badf.printStackTrace();
			}
		}
		return eofFound;
	}

	public static void decrypt(String inputFileName, String keyFile, String outputFileName){

	}

}
