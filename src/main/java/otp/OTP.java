package otp;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class OTP {
	
	public static String generateToken(String secret) {
		
		// OTP generato per ore, perchè per minuti era troppo poco
		long hours = System.currentTimeMillis() / 1000 / 60 / 60;
		String concat = secret + hours;
		MessageDigest digest = null;
		
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalArgumentException(e);
		}
		
		byte[] hash = digest.digest(concat.getBytes(Charset.forName("UTF-8")));
		return Base64.getEncoder().encodeToString(hash);
		
	}

}
