import static org.junit.Assert.*;
import org.junit.Test;


public class RSATest {
	
	@Test
	public void testMain() {
		fail("Not yet implemented");
	}
	
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
	public void testEncrypt() {
		RSA.encrypt("test1.txt", "16777219 1181 184541", "mytest1.enc");
		assertEquals(1, 1);
	}

	@Test
	public void testDecrypt() {
		fail("Not yet implemented");
	}

}
