package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordEncrypter {
	
	public static String encrypt(String password) {
		String encryptedPassword = "";
		
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(password.getBytes());
			byte[] bytes = m.digest();
			StringBuilder s = new StringBuilder();
			for (byte b: bytes) {
				s.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
			}
			encryptedPassword = s.toString();
			return encryptedPassword;
			
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return encryptedPassword;
		}	
	}
}
