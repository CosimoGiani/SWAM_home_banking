package otpStateful;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;

public class OTP {
	private static Random rand = new Random();
	
	public static String generateToken() {
		int rand_int = rand.nextInt(89999999) + 10000000;
		
		String rand_string = String.valueOf(rand_int);
		MessageDigest digest = null;
		
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
		
		byte[] hash = digest.digest(rand_string.getBytes(Charset.forName("UTF-8")));
		return Base64.getEncoder().encodeToString(hash);
		
	}

}

