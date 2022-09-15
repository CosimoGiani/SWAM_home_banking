package rest.serviceTest;

import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import model.Consultant;
import model.User;

public class OTPUtils {
	
	public static String getOtp(User user, String decryptedPassword) {
		    
		    RequestSpecification request = RestAssured.given();
			request.header("Content-Type", "application/json");
			request.body("{'email': '" + user.getEmail() + "', 'password': '"+decryptedPassword+"'}");
			
			RestAssured.baseURI = "http://localhost/";
			RestAssured.port = 8080; 
			
			Response response = request.post("/home.banking/api/auth/" + "login/get-otp");
			response.then().statusCode(200);
			String body = response.getBody().asString();
			Assertions.assertEquals("OTP generato con successo", body);
			
			String fileName = "secretOTP.txt";
			String test_folder_path = ServiceTest.getDeploymentsPath() + "test_files/";
			
			System.out.println(test_folder_path + fileName);
			
			String OTP = "";
			try {
				FileInputStream inputStream = new FileInputStream(test_folder_path + fileName);
				OTP = IOUtils.toString(inputStream, "UTF8");
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			return OTP;
	}

}
