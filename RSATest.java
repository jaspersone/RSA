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
	public void testGenerateKey2() {
		String t2 = RSA.generateKey(47, 59);
		assertEquals("Test t2: <p = 47> <q = 59>", t2, "2773 17 157");
	}

	@Test
	public void testEncrypt() {
		fail("Not yet implemented");
	}

	@Test
	public void testDecrypt() {
		fail("Not yet implemented");
	}

}
