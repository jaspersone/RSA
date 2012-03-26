import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;


public class RSATest {
	
	@Test
	public void testGenerateKey() {
		String t1 = RSA.generateKey(61, 53);
		assertEquals("Test t1: <p = 61> <q = 53>", t1, "3233 7 1783");
	}

	@Test
	public void testEuclid1() {
		long phi = (47 - 1) * (59 - 1);
		long testE = 17;
		long testN = 2773;
		long expectedD = 157;
		long testResult = RSA.euclid(phi, testE, testN);
		assertEquals(expectedD, testResult);
	}
	
	@Test
	public void testFastExponentiation1() {
		long base = 4;
		long expo = 35;
		long mod = 11;
		long testResult = RSA.fastExponentiation(base, expo, mod);
		long expectedResult = 1;
		assertEquals(testResult, expectedResult);
	}
	
	@Test
	public void testFastExponentiation2() {
		long base = 12348;
		long expo = 7829;
		long mod = 347;
		long testResult = RSA.fastExponentiation(base, expo, mod);
		long expectedResult = 198;
		assertEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGCD1() {
		long testResult = RSA.gcd(4389, 17347);
		long expectedResult = 209;
		assertEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGCD2() {
		long testResult = RSA.gcd(43897, 17347);
		long expectedResult = 1;
		assertEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGCD3() {
		long testResult = RSA.gcd(10, 20);
		long expectedResult = 10;
		assertEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGetNEDValues1() {
		String testString = "10 20 50";
		long[] expectedResult = {10, 20, 50};
		long[] testResult = RSA.getNEDValues(testString);
		for (int i = 0; i < testResult.length; i++) {
			assertEquals(testResult[i], expectedResult[i]);
		}
	}
	@Test
	public void testGetNEDValues2() {
		String testString = "0 1 1010020348";
		long[] expectedResult = {0, 1, 1010020348};
		long[] testResult = RSA.getNEDValues(testString);
		for (int i = 0; i < testResult.length; i++) {
			assertEquals(testResult[i], expectedResult[i]);
		}
	}

	@Test
	public void testCombineTuple1() {
		int[] tuple = {34, 76, 11};
		int expectedResult = 2247691;
		int testResult = RSA.combineTuple(tuple);
		assertEquals(testResult, expectedResult);
	}
	
	@Test
	public void testCombineTuple2() {
		int[] tuple = {115, 32, 102};
		int expectedResult = 7544934;
		int testResult = RSA.combineTuple(tuple);
		assertEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGetOutputMessage1() {
		long message = 10970;
		long e = 1181;
		long mod = 16777219;
		// Input:  {101010, 1101, 1010}
		// Output: {11100101, 00000101, 00011111}
		byte[] expectedResult = 
			{((Integer) 229).byteValue(), ((Integer) 5).byteValue(), ((Integer) 31).byteValue()};
		byte[] testResult = RSA.getOutputMessage(message, e, mod);
		System.out.println("Expected: " + Arrays.toString(expectedResult));
		System.out.println("Actual  : " + Arrays.toString(testResult));
		assertArrayEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGetOutputMessage2() {
		long message = 1635056;
		long e = 1181;
		long mod = 16777219;
		// Input:  {1100011, 1100101, 1110000}
		// Output: {10101100, 11001011, 11000100}
		byte[] expectedResult = 
			{((Integer) 172).byteValue(), ((Integer) 203).byteValue(), ((Integer) 196).byteValue()};
		byte[] testResult = RSA.getOutputMessage(message, e, mod);
		System.out.println("Expected: " + Arrays.toString(expectedResult));
		System.out.println("Actual  : " + Arrays.toString(testResult));
		assertArrayEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGetOutputMessage3() {
		long message = 7544934;
		long e = 1181;
		long mod = 16777219;
		// Input:  {01110011, 00100000, 01100110}
		// Output: {10101100, 11001011, 11000100}
		byte[] expectedResult = 
			{((Integer) 110).byteValue(), ((Integer) 215).byteValue(), ((Integer) 20).byteValue()};
		byte[] testResult = RSA.getOutputMessage(message, e, mod);
		System.out.println("Expected: " + Arrays.toString(expectedResult));
		System.out.println("Actual  : " + Arrays.toString(testResult));
		assertArrayEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGetOutputMessage4() {
		long message = 2100070;
		long e = 1181;
		long mod = 16777219;
		// Input:  {00100000, 01101111, 01100110}
		// Output: {01010001, 00111101, 10111100}
		byte[] expectedResult = 
			{((Integer) 81).byteValue(), ((Integer) 61).byteValue(), ((Integer) 188).byteValue()};
		byte[] testResult = RSA.getOutputMessage(message, e, mod);
		System.out.println("Expected: " + Arrays.toString(expectedResult));
		System.out.println("Actual  : " + Arrays.toString(testResult));
		assertArrayEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGetLengthOfBinaryNumber1() {
		long testNum = 255;
		int testLength = RSA.getLengthOfBinaryNumber(testNum);
		int expectedLength = 8;
		System.out.println("Expected: " + expectedLength + ", Actual: " + testLength);
		assertEquals(testLength, expectedLength);
	}
	
	@Test
	public void testGetLengthOfBinaryNumber2() {
		long testNum = 1;
		int testLength = RSA.getLengthOfBinaryNumber(testNum);
		int expectedLength = 1;
		System.out.println("Expected: " + expectedLength + ", Actual: " + testLength);
		assertEquals(testLength, expectedLength);
	}
	
	@Test
	public void testGetLengthOfBinaryNumber3() {
		long testNum = 0;
		int testLength = RSA.getLengthOfBinaryNumber(testNum);
		int expectedLength = 1;
		System.out.println("Expected: " + expectedLength + ", Actual: " + testLength);
		assertEquals(testLength, expectedLength);
	}
	
	@Test
	public void testGetLengthOfBinaryNumber4() {
		long testNum = 2;
		int testLength = RSA.getLengthOfBinaryNumber(testNum);
		int expectedLength = 2;
		System.out.println("Expected: " + expectedLength + ", Actual: " + testLength);
		assertEquals(testLength, expectedLength);
	}
	
	@Test
	public void testGetLengthOfBinaryNumber5() {
		long testNum = 5;
		int testLength = RSA.getLengthOfBinaryNumber(testNum);
		int expectedLength = 3;
		System.out.println("Expected: " + expectedLength + ", Actual: " + testLength);
		assertEquals(testLength, expectedLength);
	}
	
	@Test
	public void testEncrypt_book() {
		String key = "16777219 1181 184541";
		String testInputFile = "test1.txt";
		String testOutputFile = "mytest1.enc";
		String expectedOutputFile = "test1.enc";
		RSA.encrypt(testInputFile, key, testOutputFile);
		assertTrue(compareFiles(testOutputFile, expectedOutputFile));
	}
	
	@Test
	public void testEncrypt_image() {
		String key = "47734277 6917 44001893";
		String testInputFile = "test7a.jpg";
		String testOutputFile = "mytest7a.enc";
		String expectedOutputFile = "test7a.enc";
		RSA.encrypt(testInputFile, key, testOutputFile);
		assertTrue(compareFiles(testOutputFile, expectedOutputFile));
	}

//	@Test
//	public void testDecrypt_book() {
//		String key = "47734277 6917 44001893";
//		String testInputFile = "mytest1.enc";
//		String testOutputFile = "mytest1.txt";
//		String expectedOutputFile = "test1.txt";
//		RSA.decrypt(testInputFile, key, testOutputFile);
//		assertTrue(compareFiles(testOutputFile, expectedOutputFile));
//	}

	private boolean compareFiles(String testFileName, String expectedFileName) {
		boolean allSame = true;
		long bytesCompared = 0;
		try {
			FileInputStream testF = new FileInputStream(testFileName);
			FileInputStream expectedF = new FileInputStream(expectedFileName);
			int testVal = 0;
			int expectedVal = 0;
			while(testVal != -1 || expectedVal != -1) {
				try {
					testVal = testF.read();
					expectedVal = expectedF.read();
					bytesCompared++;
					if (testVal != expectedVal) {
						allSame = false;
						break;
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					allSame = false;
					System.out.println("There was an unexpected error reading from files.");
					e.printStackTrace();
					
				}
			}
			testF.close();
			expectedF.close();
			assertEquals(1, 1);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			allSame = false;
			System.out.println("There was an unexpected error while trying to open the files.");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			allSame = false;
			System.out.println("There was an unexpected error while trying to close the files.");
			e.printStackTrace();
		}
		System.out.println("Total bytes compared: " + bytesCompared);
		return allSame;
	}

}
