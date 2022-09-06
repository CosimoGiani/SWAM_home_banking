package modelTest;

import java.util.UUID;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BaseEntityTest {
	
	private FakeBaseEntity fe1;
	private FakeBaseEntity fe2; 
	
	@BeforeEach
	public void setup() {
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        fe1 = new FakeBaseEntity(uuid1);
        fe2 = new FakeBaseEntity(uuid2);
	}
	
	@Test
    public void testNullUUID() {
        Assertions.assertThrows(IllegalArgumentException.class, ()->{
            new FakeBaseEntity(null);
        });
    }
	
	@Test
    public void testEquals(){
        Assertions.assertEquals(fe1, fe1); 
        Assertions.assertNotEquals(fe1, fe2);
    }

}
