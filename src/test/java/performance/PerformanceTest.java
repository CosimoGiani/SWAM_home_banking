package performance;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import otpStateful.OneTimePasswordAuthenticator;

public class PerformanceTest {

	String decryptedPassword;
	OneTimePasswordAuthenticator otpAuthenticator;
	
	@BeforeEach
	protected void beforeEachInit() throws SQLException {
		
		otpAuthenticator = new OneTimePasswordAuthenticator();
		decryptedPassword = "password";
		
	}
	
	@Test
	public void testAuthentication_1() {
		Runtime r = Runtime.getRuntime();
		Long memoryBefore;
		Long memoryEnd;
		System.gc();
		memoryBefore = r.totalMemory() - r.freeMemory();
		
		for(int i = 0; i < 1; i++) {
			otpAuthenticator.generateOTP("prova"+i+"@prova.it");
		}
		
		System.gc();
		memoryEnd = r.totalMemory() - r.freeMemory();
		
		System.out.println("Memoria Prima: " + memoryBefore);
		System.out.println("Memoria Dopo: " + memoryEnd);
		
		Long memory_occupation = memoryEnd - memoryBefore;
		System.out.println("Memoria utilizzata dall'OTPAuthenticator " + memory_occupation);	
	}
	
	@Test
	public void testAuthentication_10() {
		Runtime r = Runtime.getRuntime();
		Long memoryBefore;
		Long memoryEnd;
		System.gc();
		memoryBefore = r.totalMemory() - r.freeMemory();
		
		for(int i = 0; i < 10; i++) {
			otpAuthenticator.generateOTP("prova"+i+"@prova.it");
		}
		
		System.gc();
		memoryEnd = r.totalMemory() - r.freeMemory();
		
		System.out.println("Memoria Prima: " + memoryBefore);
		System.out.println("Memoria Dopo: " + memoryEnd);
		
		Long memory_occupation = memoryEnd - memoryBefore;
		System.out.println("Memoria utilizzata dall'OTPAuthenticator " + memory_occupation);	
	}
	
	
	@Test
	public void testAuthentication_100() {
		Runtime r = Runtime.getRuntime();
		Long memoryBefore;
		Long memoryEnd;
		System.gc();
		memoryBefore = r.totalMemory() - r.freeMemory();
		
		for(int i = 0; i < 100; i++) {
			otpAuthenticator.generateOTP("prova"+i+"@prova.it");
		}
		
		System.gc();
		memoryEnd = r.totalMemory() - r.freeMemory();
		
		System.out.println("Memoria Prima: " + memoryBefore);
		System.out.println("Memoria Dopo: " + memoryEnd);
		
		Long memory_occupation = memoryEnd - memoryBefore;
		System.out.println("Memoria utilizzata dall'OTPAuthenticator " + memory_occupation);	
	}
	
	@Test
	public void testAuthentication_1000() {
		Runtime r = Runtime.getRuntime();
		Long memoryBefore;
		Long memoryEnd;
		System.gc();
		memoryBefore = r.totalMemory() - r.freeMemory();
		
		for(int i = 0; i < 1000; i++) {
			otpAuthenticator.generateOTP("prova"+i+"@prova.it");
		}
		
		System.gc();
		memoryEnd = r.totalMemory() - r.freeMemory();
		
		System.out.println("Memoria Prima: " + memoryBefore);
		System.out.println("Memoria Dopo: " + memoryEnd);
		
		Long memory_occupation = memoryEnd - memoryBefore;
		System.out.println("Memoria utilizzata dall'OTPAuthenticator " + memory_occupation);	
	}
	
	@Test
	public void testAuthentication_10k() {
		Runtime r = Runtime.getRuntime();
		Long memoryBefore;
		Long memoryEnd;
		System.gc();
		memoryBefore = r.totalMemory() - r.freeMemory();
		
		for(int i = 0; i < 10000; i++) {
			otpAuthenticator.generateOTP("prova"+i+"@prova.it");
		}
		
		System.gc();
		memoryEnd = r.totalMemory() - r.freeMemory();
		
		System.out.println("Memoria Prima: " + memoryBefore);
		System.out.println("Memoria Dopo: " + memoryEnd);
		
		Long memory_occupation = memoryEnd - memoryBefore;
		System.out.println("Memoria utilizzata dall'OTPAuthenticator " + memory_occupation);	
	}
	
	@Test
	public void testAuthentication_100k() {
		Runtime r = Runtime.getRuntime();
		Long memoryBefore;
		Long memoryEnd;
		System.gc();
		memoryBefore = r.totalMemory() - r.freeMemory();
		
		for(int i = 0; i < 100000; i++) {
			otpAuthenticator.generateOTP("prova"+i+"@prova.it");
		}
		
		System.gc();
		memoryEnd = r.totalMemory() - r.freeMemory();
		
		System.out.println("Memoria Prima: " + memoryBefore);
		System.out.println("Memoria Dopo: " + memoryEnd);
		
		Long memory_occupation = memoryEnd - memoryBefore;
		System.out.println("Memoria utilizzata dall'OTPAuthenticator " + memory_occupation);	
	}
	
	@Test
	public void testAuthentication_1M() {
		Runtime r = Runtime.getRuntime();
		Long memoryBefore;
		Long memoryEnd;
		System.gc();
		memoryBefore = r.totalMemory() - r.freeMemory();
		
		for(int i = 0; i < 1000000; i++) {
			otpAuthenticator.generateOTP("prova"+i+"@prova.it");
		}
		
		System.gc();
		memoryEnd = r.totalMemory() - r.freeMemory();
		
		System.out.println("Memoria Prima: " + memoryBefore);
		System.out.println("Memoria Dopo: " + memoryEnd);
		
		Long memory_occupation = memoryEnd - memoryBefore;
		System.out.println("Memoria utilizzata dall'OTPAuthenticator " + memory_occupation);	
	}
	
	@Test
	public void testAuthentication_10M() {
		Runtime r = Runtime.getRuntime();
		Long memoryBefore;
		Long memoryEnd;
		System.gc();
		memoryBefore = r.totalMemory() - r.freeMemory();
		
		for(int i = 0; i < 10000000; i++) {
			otpAuthenticator.generateOTP("prova"+i+"@prova.it");
			if(i%100000 == 0)
				System.out.println(i);
		}
		
		System.gc();
		memoryEnd = r.totalMemory() - r.freeMemory();
		
		System.out.println("Memoria Prima: " + memoryBefore);
		System.out.println("Memoria Dopo: " + memoryEnd);
		
		Long memory_occupation = memoryEnd - memoryBefore;
		System.out.println("Memoria utilizzata dall'OTPAuthenticator " + memory_occupation);	
	}
	
}
