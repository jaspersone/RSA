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
			d = findModularMultiplicativeInverse(e, phi);
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
	
	private static long findModularMultiplicativeInverse(long e, long phi){
		//long d = (long) ((1.0/e) % phi);
		long d = 1;
		while((d * e) % phi != 1){
			d++;
		}
		return d;
	}
	
	public static void encrypt(String inputFileName, String keyFile, String outputFileName){
	
	}
	
	public static void decrypt(String inputFileName, String keyFile, String outputFileName){
		
	}
	
}
