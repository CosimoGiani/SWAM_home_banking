package performanceTest;

import java.sql.SQLException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import otpStateful.OneTimePasswordAuthenticator;
import session.SessionTransferManager;

public class SessionTransferPerformanceTest {
	
	String fakeOTP;
	OneTimePasswordAuthenticator otpAuthenticator;
	SessionTransferManager sessionTransferManager;
	
	@BeforeEach
	protected void beforeEachInit() throws SQLException {
		
		fakeOTP = "fakeOTP";
		otpAuthenticator = new OneTimePasswordAuthenticator();
		sessionTransferManager = new SessionTransferManager();
		
	}
	
	@Test
	public void testRequestTransfer_1() {
		for(int j=0; j<10; j++) {
			sessionTransferManager = new SessionTransferManager();
			System.out.println("\n Testing 1 users transfer");
			
			Runtime r = Runtime.getRuntime();
			Long memoryBefore;
			Long memoryEnd;
			System.gc();
			memoryBefore = r.totalMemory() - r.freeMemory();
			
			for(int i = 0; i < 1; i++) {
				sessionTransferManager.requestSessionTransfer("prova"+i+"@prova.it", fakeOTP);
			}
			
			System.gc();
			memoryEnd = r.totalMemory() - r.freeMemory();
			
			System.out.println("Memoria Prima: " + memoryBefore);
			System.out.println("Memoria Dopo: " + memoryEnd);
			
			Long memory_occupation = memoryEnd - memoryBefore;
			System.out.println("Memoria utilizzata dal SessionTransferManager " + memory_occupation);	
		}
	}
	
	@Test
	public void testRequestTransfer_10() {
		for(int j=0; j<10; j++) {
			System.out.println("\n Testing 10 users transfer");
			
			Runtime r = Runtime.getRuntime();
			Long memoryBefore;
			Long memoryEnd;
			System.gc();
			memoryBefore = r.totalMemory() - r.freeMemory();
			
			for(int i = 0; i < 10; i++) {
				sessionTransferManager.requestSessionTransfer("prova"+i+"@prova.it", fakeOTP);
			}
			
			System.gc();
			memoryEnd = r.totalMemory() - r.freeMemory();
			
			System.out.println("Memoria Prima: " + memoryBefore);
			System.out.println("Memoria Dopo: " + memoryEnd);
			
			Long memory_occupation = memoryEnd - memoryBefore;
			System.out.println("Memoria utilizzata dal SessionTransferManager " + memory_occupation);
		}
	}
	
	@Test
	public void testRequestTransfer_100() {
		for(int j=0; j<10; j++) {
			System.out.println("\n Testing 100 users transfer");
			
			Runtime r = Runtime.getRuntime();
			Long memoryBefore;
			Long memoryEnd;
			System.gc();
			memoryBefore = r.totalMemory() - r.freeMemory();
			
			for(int i = 0; i < 100; i++) {
				sessionTransferManager.requestSessionTransfer("prova"+i+"@prova.it", fakeOTP);
			}
			
			System.gc();
			memoryEnd = r.totalMemory() - r.freeMemory();
			
			System.out.println("Memoria Prima: " + memoryBefore);
			System.out.println("Memoria Dopo: " + memoryEnd);
			
			Long memory_occupation = memoryEnd - memoryBefore;
			System.out.println("Memoria utilizzata dal SessionTransferManager " + memory_occupation);	
		}
	}
	
	@Test
	public void testRequestTransfer_1000() {
		for(int j=0; j<10; j++) {
			System.out.println("\n Testing 1k users transfer");
			
			Runtime r = Runtime.getRuntime();
			Long memoryBefore;
			Long memoryEnd;
			System.gc();
			memoryBefore = r.totalMemory() - r.freeMemory();
			
			for(int i = 0; i < 1000; i++) {
				sessionTransferManager.requestSessionTransfer("prova"+i+"@prova.it", fakeOTP);
			}
			
			System.gc();
			memoryEnd = r.totalMemory() - r.freeMemory();
			
			System.out.println("Memoria Prima: " + memoryBefore);
			System.out.println("Memoria Dopo: " + memoryEnd);
			
			Long memory_occupation = memoryEnd - memoryBefore;
			System.out.println("Memoria utilizzata dal SessionTransferManager " + memory_occupation);	
		}
	}
	
	@Test
	public void testRequestTransfer_10k() {
		for(int j=0; j<10; j++) {
			System.out.println("\n Testing 10k users transfer");
			
			Runtime r = Runtime.getRuntime();
			Long memoryBefore;
			Long memoryEnd;
			System.gc();
			memoryBefore = r.totalMemory() - r.freeMemory();
			
			for(int i = 0; i < 10000; i++) {
				sessionTransferManager.requestSessionTransfer("prova"+i+"@prova.it", fakeOTP);
			}
			
			System.gc();
			memoryEnd = r.totalMemory() - r.freeMemory();
			
			System.out.println("Memoria Prima: " + memoryBefore);
			System.out.println("Memoria Dopo: " + memoryEnd);
			
			Long memory_occupation = memoryEnd - memoryBefore;
			System.out.println("Memoria utilizzata dal SessionTransferManager " + memory_occupation);	
		}
	}
	
	@Test
	public void testRequestTransfer_100k() {
		for(int j=0; j<10; j++) {
			System.out.println("\n Testing 100k users transfer");
			
			Runtime r = Runtime.getRuntime();
			Long memoryBefore;
			Long memoryEnd;
			System.gc();
			memoryBefore = r.totalMemory() - r.freeMemory();
			
			for(int i = 0; i < 100000; i++) {
				sessionTransferManager.requestSessionTransfer("prova"+i+"@prova.it", fakeOTP);
			}
			
			System.gc();
			memoryEnd = r.totalMemory() - r.freeMemory();
			
			System.out.println("Memoria Prima: " + memoryBefore);
			System.out.println("Memoria Dopo: " + memoryEnd);
			
			Long memory_occupation = memoryEnd - memoryBefore;
			System.out.println("Memoria utilizzata dal SessionTransferManager " + memory_occupation);	
		}
	}
	
	@Test
	public void testRequestTransfer_1M() {
		for(int j=0; j<10; j++) {
			System.out.println("\n Testing 1M users transfer");
			
			Runtime r = Runtime.getRuntime();
			Long memoryBefore;
			Long memoryEnd;
			System.gc();
			memoryBefore = r.totalMemory() - r.freeMemory();
			
			for(int i = 0; i < 1000000; i++) {
				sessionTransferManager.requestSessionTransfer("prova"+i+"@prova.it", fakeOTP);
			}
			
			System.gc();
			memoryEnd = r.totalMemory() - r.freeMemory();
			
			System.out.println("Memoria Prima: " + memoryBefore);
			System.out.println("Memoria Dopo: " + memoryEnd);
			
			Long memory_occupation = memoryEnd - memoryBefore;
			System.out.println("Memoria utilizzata dal SessionTransferManager " + memory_occupation);	
		}
	}
	
	@Test
	public void testRequestTransfer_10M() {
		System.out.println("\n Testing 10M users transfer");
		
		Runtime r = Runtime.getRuntime();
		Long memoryBefore;
		Long memoryEnd;
		System.gc();
		memoryBefore = r.totalMemory() - r.freeMemory();
		
		for(int i = 0; i < 10000000; i++) {
			sessionTransferManager.requestSessionTransfer("prova"+i+"@prova.it", fakeOTP);
			if(i%100000 == 0)
				System.out.println(i);
		}
		
		System.gc();
		memoryEnd = r.totalMemory() - r.freeMemory();
		
		System.out.println("Memoria Prima: " + memoryBefore);
		System.out.println("Memoria Dopo: " + memoryEnd);
		
		Long memory_occupation = memoryEnd - memoryBefore;
		System.out.println("Memoria utilizzata dal SessionTransferManager " + memory_occupation);	
	}

}
