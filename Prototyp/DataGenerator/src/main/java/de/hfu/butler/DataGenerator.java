package de.hfu.butler;

import org.json.JSONException;
import org.json.JSONObject;

public class DataGenerator {

	public static void main(String[] args) {
		System.out.println("<---- Programm started! ---->");		
		
		createDoc("Dr. med. Barbara", "Weis", "Furtwangen im Schwarzwald");
		createDoc("Dr. med. Goetz", "Besenfelder", "Furtwangen im Schwarzwald");
		createDoc("Dr. med. Ute", "Scheit", "Furtwangen im Schwarzwald");
		createDoc("Dr. med. Martin", "Gellert", "Furtwangen im Schwarzwald");
		createDoc("Dr. med. Hans-Peter", "Braendle ", "Furtwangen im Schwarzwald");
		
		createCarRental("HFU", "e-Carsharing", "Furtwangen");
		
		System.out.println("<---- Programm ended! ---->");		
	}
	
	public static void createDoc(final String firstname, final String lastname, final String location) {
		APIClient client = APIClient.getInstance();
		String email = firstname + "@" + lastname + ".de";
		email = email.replaceAll("\\s+","");
		String password = "test";
		client.registerUser(email, password, 1);
		JSONObject doc = client.loginUser(email, password);
		String userId = "", token = "";
		try {
			userId = doc.getString("userId");
			token = doc.getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.createUserProfil(userId, token, firstname, lastname, location);        
	}	
	
	public static void createCarRental(final String firstname, final String lastname, final String location) {
		APIClient client = APIClient.getInstance();
		final String email = firstname + "@" + lastname + ".de";
		final String password = "test";
		client.registerUser(email, password, 2);
		JSONObject doc = client.loginUser(email, password);
		String userId = "", token = "";
		try {
			userId = doc.getString("userId");
			token = doc.getString("id");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		client.createUserProfil(userId, token, firstname, lastname, location);        
	}	
}
