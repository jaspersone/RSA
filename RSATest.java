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
	public void testCombineTuple(){
		int[] tuple = {34, 76, 11};
		int expectedResult = 2247691;
		int testResult = RSA.combineTuple(tuple);
		assertEquals(testResult, expectedResult);
	}
	
	@Test
	public void testGetOutputMessage(){
		long em = 2039212432;
		int limit = 3;
		// {01111001, 10001011, 11101001, 10010000}
		byte[] expectedResult = 
			{((Integer) 121).byteValue(), ((Integer) 139).byteValue(),
			 ((Integer) 233).byteValue(), ((Integer) 144).byteValue()};
		byte[] testResult = RSA.getOutputMessage(em, limit);
		assertArrayEquals(testResult, expectedResult);
	}
	
	@Test
	public void testEncrypt() {
		String testFile = "mytest1.enc";
		String expectedFile = "test1.enc";
		RSA.encrypt("test1.txt", "16777219 1181 184541", testFile);
		assertTrue(compareFiles(testFile, expectedFile));
	}

	@Test
	public void testDecrypt() {
		String testFile = "mytest1.txt";
		String expectedFile = "test1.txt";
		RSA.decrypt("test1.enc", "16777219 1181 184541", testFile);
		assertTrue(compareFiles(testFile, expectedFile));
	}
	
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
		System.out.println("testEncrypt");
		System.out.println("Total bytes compared: " + bytesCompared);
		return allSame;
	}

}
