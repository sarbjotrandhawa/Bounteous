package ca.homedepot.customerreview.util;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Preconditions;


public class ServicesUtil
{
	
	public static List<String> words = Arrays.asList("Ship","Miss","Duck", "Punt", "Rooster","Mother", "Bits");
	
	private static void validateParameterNotNull(Object parameter, String nullMessage)
	{
		Preconditions.checkArgument(parameter != null, nullMessage);
	}

	public static void validateParameterNotNullStandardMessage(String param, Object value)
	{
		validateParameterNotNull(value, "Parameter " + param + " cannot be null");
	}
	
	public static boolean hasCurseWords(String message) {
		
		String messageUpper = message.toUpperCase();
		for(String word: words) {
			if(messageUpper.contains(word.toUpperCase()))
				return true;
		}
			
		return false;
	}
}
