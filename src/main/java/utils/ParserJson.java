package utils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.ejb.Singleton;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Singleton
public final class ParserJson {
	
	public static Map<String, String> fromString(String stringToParse){
		Map<String, String> parsedMap = new HashMap<String, String>();
		
		JsonParser parser = new JsonParser();
		JsonObject jsonObject = parser.parse(stringToParse).getAsJsonObject();
		
		Iterator<Entry<String, JsonElement>> it = jsonObject.entrySet().iterator();
		
	    while(it.hasNext()) {
	    	Entry<String, JsonElement> couple = it.next();
	    	parsedMap.put(couple.getKey(), couple.getValue().getAsString());
	    }
	    
	    return parsedMap;
	}
}
