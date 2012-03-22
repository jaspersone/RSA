import java.io.*;

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
		System.out.println(result);
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
	private static long euclid(long phi, long e, long n){
		assert(e > 1);
		assert(isPrime(e));
		assert(gcd(phi, e) == 1);
		long d;
		long[] top		= {1, 0, phi};
		long[] bottom 	= {0, 1, e};
		
		while (bottom[2] != 1) {
			long quotient = top[2] / bottom[2];
			long[] newBottom = new long[3];
			for(int i = 0; i < 3; i++) {
				newBottom[i] = top[i] - quotient * bottom[i];
			}
			bottom = newBottom;
			top = bottom;
			if (bottom[2] < 1) {
				System.out.println(">>>> Oh shit, bottom[2] below 1");
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
	private static long gcd(long left, long right) {
		// TODO: write this
		return 1;
	}
	
	public static void encrypt(String inputFileName, String keyFile, String outputFileName){
	
	}
	
	public static void decrypt(String inputFileName, String keyFile, String outputFileName){
		
	}
	
}
