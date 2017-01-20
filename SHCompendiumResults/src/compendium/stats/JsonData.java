package compendium.stats;

import com.google.gson.JsonObject;

public final class JsonData {

	public static String getString(JsonObject obj, String memberName){
		return obj.get(memberName).getAsString();
	}
	
	public static int getInt(JsonObject obj, String memberName){
		return obj.get(memberName).getAsInt();
	}
	
	public static boolean getBoolean(JsonObject obj, String memberName){
		return obj.get(memberName).getAsBoolean();
	}
	
	public static long getLong(JsonObject obj, String memberName){
		return obj.get(memberName).getAsLong();
	}
}
