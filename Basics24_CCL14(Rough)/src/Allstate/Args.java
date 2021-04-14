package Allstate;

import java.text.ParseException;
//import Allstate.ArgsException.ErrorCode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;


public class Args {
	private String schema;
	private String[] args;
	private boolean valid = true;
	private Set<Character> unexpectedArguments = new TreeSet<Character>();
	private Map<Character, Boolean> booleanArgs = new HashMap<Character, Boolean>();
	private Map<Character, String> stringArgs = new HashMap<Character, String>();
	private Map<Character, Integer> intArgs = new HashMap<Character, Integer>();
	private Set<Character> argsFound = new HashSet<Character>();
	private int currentArgument;
	private char errorArgumentId = '\0';
	private String errorParameter = "TILT";
	private ErrorCode errorCode = ErrorCode.OK;
	
	
	//m1 - constructor
	public Args(String schema, String[] args) throws ParseException 
	{
		this.schema = schema;
		this.args = args;
		valid = parse();
	}

	//m2
	private enum ErrorCode 
	{
		OK, MISSING_STRING, MISSING_INTEGER, INVALID_INTEGER, UNEXPECTED_ARGUMENT
	}

	//m3
	private boolean parse() throws ParseException {
	
		if (schema.length() == 0 && args.length == 0)
			return true;
		parseSchema();
		try 
		{
			parseArguments();
		} catch (ArgsException e) 
		{
			
		}
		return valid;
	}
	
	//m4
	private boolean parseSchema() throws ParseException 
	{
		for (String element : schema.split(",")) 
		{
	//--------------------------------------------------------------------------------------------------------------------------------------------
			if (element.length() > 0) 
			{
				String trimmedElement = element.trim();
				parseSchemaElement(trimmedElement);
			}
			
		}
			return true;
			
	}
	
	//m5
	private void parseSchemaElement(String element) throws ParseException 
	{
		char elementId = element.charAt(0);
		String elementTail = element.substring(1);
		validateSchemaElementId(elementId);
		if (isBooleanSchemaElement(elementTail))
			parseBooleanSchemaElement(elementId);
		else if (isStringSchemaElement(elementTail))
			parseStringSchemaElement(elementId);
		else if (isIntegerSchemaElement(elementTail)) 
		{
			parseIntegerSchemaElement(elementId);
		} 
		else 
		{
			throw new ParseException(
			String.format("Argument: %c has invalid format: %s.",
			elementId, elementTail), 0);
		}
	}
	
	//m6
	private void validateSchemaElementId(char elementId) throws ParseException {
		if (!Character.isLetter(elementId)) 
		{
			throw new ParseException(
			"Bad character:" + elementId + "in Args format: " + schema, 0);
		}
	}
	
	//m7
	private void parseBooleanSchemaElement(char elementId) 
	{
		booleanArgs.put(elementId, false);
	}
	
	//m8
	private void parseIntegerSchemaElement(char elementId) 
	{
		intArgs.put(elementId, 0);
	}
	
	//m9
	private void parseStringSchemaElement(char elementId) {
		stringArgs.put(elementId, "");
	}
	
	//m10
	private boolean isStringSchemaElement(String elementTail) 
	{
		return elementTail.equals("*");
	}
	
	//m11
	private boolean isBooleanSchemaElement(String elementTail) 
	{
		return elementTail.length() == 0;
	}
	
	//m12
	private boolean isIntegerSchemaElement(String elementTail) 
	{
		return elementTail.equals("#");
	}
	
	//--------------------------------------------------------------------------------------------------------------------------------------------
	//m13
	private boolean parseArguments() throws ArgsException {
		for (currentArgument = 0; currentArgument < args.length; currentArgument++)
		{
			String arg = args[currentArgument];
			parseArgument(arg);
		}
		return true;
	}
	
	//m14
	private void parseArgument(String arg) throws ArgsException 
	{
		if (arg.startsWith("-"))
			parseElements(arg);
	}
	
	//m15
	private void parseElements(String arg) throws ArgsException 
	{
		for (int i = 1; i < arg.length(); i++)
			parseElement(arg.charAt(i));
	}
	
	//m16
	private void parseElement(char argChar) throws ArgsException 
	{
		if (setArgument(argChar))
			argsFound.add(argChar);
		else 
		{
				unexpectedArguments.add(argChar);
				errorCode = ErrorCode.UNEXPECTED_ARGUMENT;
				valid = false;
		}
	}
	
	//m17
	private boolean setArgument(char argChar) throws ArgsException 
	{
		if (isBooleanArg(argChar))
			setBooleanArg(argChar, true);
		else if (isStringArg(argChar))
			setStringArg(argChar);
		else if (isIntArg(argChar))
			setIntArg(argChar);
		else
			return false;
		
		return true;
	}
	
	//m18
	private boolean isIntArg(char argChar) 
	{
		return intArgs.containsKey(argChar);
	}
	
	//m19
	@SuppressWarnings("deprecation")
	private void setIntArg(char argChar) throws ArgsException 
	{
		currentArgument++;
		String parameter = null;
		try 
		{
			parameter = args[currentArgument];
			intArgs.put(argChar, new Integer(parameter));
		} 
		catch (ArrayIndexOutOfBoundsException e) 
		{
			valid = false;
			errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_INTEGER;
	//--------------------------------------------------------------------------------------------------------------------------------------------
			throw new ArgsException();
		}
		catch (NumberFormatException e) 
		{
			valid = false;
			errorArgumentId = argChar;
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_INTEGER;
			throw new ArgsException();
		}	
	}
		
	//m20
	private void setStringArg(char argChar) throws ArgsException 
	{
		currentArgument++;
		try 
		{
			stringArgs.put(argChar, args[currentArgument]);
		} 
		catch (ArrayIndexOutOfBoundsException e) 
		{
			valid = false;
			errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_STRING;
			throw new ArgsException();
		}
	}
	
	//m21
	private boolean isStringArg(char argChar) 
	{
		return stringArgs.containsKey(argChar);
	}
		
	//m22
	private void setBooleanArg(char argChar, boolean value) 
	{
		booleanArgs.put(argChar, value);
	}
		
	//m23
	private boolean isBooleanArg(char argChar) 
	{
		return booleanArgs.containsKey(argChar);
	}
		
	//m24
	public int cardinality() 
	{
		return argsFound.size();
	}
		
	//m25
	public String usage() 
	{
		if (schema.length() > 0)
			return "-[" + schema + "]";
		else
			return "";
	}
		
	//m26
	public String errorMessage() throws Exception 
	{
		switch (errorCode) 
		{
			case OK:
				throw new Exception("TILT: Should not get here.");
			case UNEXPECTED_ARGUMENT:
				return unexpectedArgumentMessage();
			case MISSING_STRING:
				return String.format("Could not find string parameter for -%c.",errorArgumentId);
	//---------------------------------------------------------------------------------------------------------------------------------------------
			case INVALID_INTEGER:
				return String.format("Argument -%c expects an integer but was '%s'.",errorArgumentId, errorParameter);
			case MISSING_INTEGER:
				return String.format("Could not find integer parameter for -%c.",errorArgumentId);
			}
		return "";
	}
		
	//m27
	private String unexpectedArgumentMessage() 
	{
		StringBuffer message = new StringBuffer("Argument(s) -");
		for (char c : unexpectedArguments) 
		{
			message.append(c);
		}
		message.append(" unexpected.");
			return message.toString();
		}
		
	//m28
	private boolean falseIfNull(Boolean b) 
	{
		return b != null && b;
	}
		
	//m29
	private int zeroIfNull(Integer i) 
	{
		return i == null ? 0 : i;
	}
		
	//m30
	private String blankIfNull(String s) 
	{
		return s == null ? "" : s;					
	}
		
	//m31
	public String getString(char arg) 
	{
		return blankIfNull(stringArgs.get(arg));
	}
		
	//m32
	public int getInt(char arg) 
	{
		return zeroIfNull(intArgs.get(arg));
	}
	
	//m33
	public boolean getBoolean(char arg) 
	{
		return falseIfNull(booleanArgs.get(arg));
	}
		
	//m34
	public boolean has(char arg) 
	{
		return argsFound.contains(arg);
	}
		
	//m35
	public boolean isValid() 
	{
		return valid;
	}
		
	//C2
	@SuppressWarnings("serial")
	private class ArgsException extends Exception 
	{
	}		
}
