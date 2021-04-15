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
	//private Map<Character, Boolean> booleanArgs = new HashMap<Character, Boolean>();
	//private Map<Character, ArgumentMarshaler> booleanArgs = new HashMap<Character, ArgumentMarshaler>();
	//private Map<Character, String> stringArgs = new HashMap<Character, String>();
	//private Map<Character, ArgumentMarshaler> stringArgs = new HashMap<Character, ArgumentMarshaler>();
	//private Map<Character, Integer> intArgs = new HashMap<Character, Integer>();
	//private Map<Character, ArgumentMarshaler> intArgs = new HashMap<Character, ArgumentMarshaler>();
	
	private Map<Character, ArgumentMarshaler> marshalers = new HashMap<Character, ArgumentMarshaler>();
	
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
			marshalers.put(elementId, new BooleanArgumentMarshaler());
			//parseBooleanSchemaElement(elementId);
		else if (isStringSchemaElement(elementTail))
			marshalers.put(elementId, new StringArgumentMarshaler());
			//parseStringSchemaElement(elementId);
		else if (isIntegerSchemaElement(elementTail)) 
			marshalers.put(elementId, new IntegerArgumentMarshaler());
			//parseIntegerSchemaElement(elementId);
		else 
		{
			throw new ParseException(
			String.format("Argument: %c has invalid format: %s.", elementId, elementTail), 0);
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
		//ArgumentMarshaler m = new BooleanArgumentMarshaler();
		//booleanArgs.put(elementId, false);
		//marshalers.put(elementId, m);
		marshalers.put(elementId, new BooleanArgumentMarshaler());
		
	}
	
	//m9
	private void parseStringSchemaElement(char elementId) {
		//ArgumentMarshaler m = new StringArgumentMarshaler();
		//stringArgs.put(elementId, "");
		//stringArgs.put(elementId, new StringArgumentMarshaler());
		//marshalers.put(elementId, m);
		marshalers.put(elementId, new StringArgumentMarshaler());
	}
	
	//m8
	private void parseIntegerSchemaElement(char elementId) 
	{
		//ArgumentMarshaler m = new IntegerArgumentMarshaler();
		//intArgs.put(elementId, 0);
		//intArgs.put(elementId, new IntegerArgumentMarshaler());
		//marshalers.put(elementId, m);
		marshalers.put(elementId, new IntegerArgumentMarshaler());
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
		ArgumentMarshaler m = marshalers.get(argChar);
		//if (isBooleanArg(argChar))
		//if (isBooleanArg(m))
		
		try {
			if (m instanceof BooleanArgumentMarshaler)
				//setBooleanArg(argChar);
				setBooleanArg(m);
			//else if (isStringArg(argChar))
			//else if (isStringArg(m))
			else if (m instanceof StringArgumentMarshaler)
				//setStringArg(argChar);
				setStringArg(m);
			
			//else if (isIntArg(argChar))
			//else if (isIntArg(m))
			else if (m instanceof IntegerArgumentMarshaler)
				//setIntArg(argChar);
				setIntArg(m);
			else
				return false;
		}catch(ArgsException e) 
		{
			valid = false;
			errorArgumentId = argChar;
			throw e;	
		}
		
		
		return true;
	}
		
	//m19
	//private void setBooleanArg(char argChar)
	private void setBooleanArg(ArgumentMarshaler m) 
	{
		//booleanArgs.put(argChar, value);
		//booleanArgs.get(argChar).setBoolean(value);
		try 
		{
			//booleanArgs.get(argChar).set("true");
			m.set("true");
		} 
		catch (ArgsException e) 
		{
		}
	}
		
	//m20
	//private void setStringArg(char argChar) throws ArgsException
	private void setStringArg(ArgumentMarshaler m) throws ArgsException 
	{
		currentArgument++;
		try 
		{
			//stringArgs.put(argChar, args[currentArgument]);
			//stringArgs.get(argChar).setString(args[currentArgument]);
			//stringArgs.get(argChar).set(args[currentArgument]);
			m.set(args[currentArgument]);
		} 
		catch (ArrayIndexOutOfBoundsException e) 
		{
			//valid = false;
			//errorArgumentId = argChar;
			errorCode = ErrorCode.MISSING_STRING;
			throw new ArgsException();
		}
	}
	
	//m21
	@SuppressWarnings("deprecation")
	//private void setIntArg(char argChar) throws ArgsException
	private void setIntArg(ArgumentMarshaler m) throws ArgsException 
	{
		currentArgument++;
		String parameter = null;
		try 
		{
			parameter = args[currentArgument];
			//intArgs.put(argChar, new Integer(parameter));
			//intArgs.get(argChar).setInteger(Integer.parseInt(parameter));
			//intArgs.get(argChar).set(parameter);
			m.set(parameter);
			
		} 
		catch (ArrayIndexOutOfBoundsException e) 
		{
			errorCode = ErrorCode.MISSING_INTEGER;
			throw new ArgsException();
		}
		catch (ArgsException e)
		//catch (ArrayIndexOutOfBoundsException e)
		{
			//valid = false;
			//errorArgumentId = argChar;
			//errorCode = ErrorCode.MISSING_INTEGER;
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_INTEGER;
			//throw new ArgsException();
			throw e;
		}
		/*
		catch (NumberFormatException e) 
		{
			valid = false;
			errorArgumentId = argChar;
			errorParameter = parameter;
			errorCode = ErrorCode.INVALID_INTEGER;
			throw new ArgsException();
		}
		*/	
	}
	
	//m23
	//private boolean isBooleanArg(char argChar)
	private boolean isBooleanArg(ArgumentMarshaler m)
	{
		//return booleanArgs.containsKey(argChar);
		//ArgumentMarshaler m = marshalers.get(argChar);
		return m instanceof BooleanArgumentMarshaler;
	}
	
	//m21
	//private boolean isStringArg(char argChar) 
	private boolean isStringArg(ArgumentMarshaler m) 
	{
		//return stringArgs.containsKey(argChar);
		//ArgumentMarshaler m = marshalers.get(argChar);
		return m instanceof StringArgumentMarshaler;
	}
		
	//m18
	//private boolean isIntArg(char argChar)
	private boolean isIntArg(ArgumentMarshaler m) 
	{
		//return intArgs.containsKey(argChar);
		//ArgumentMarshaler m = marshalers.get(argChar);
		return m instanceof IntegerArgumentMarshaler;
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
	/*
	private boolean falseIfNull(Boolean b) 
	{
		return b != null && b;
	}
	*/
		
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
	
	public boolean getBoolean(char arg) 
	{
		//return booleanArgs.get(arg).getBoolean();
		//return am != null && am.getBoolean();
		//return am != null && (Boolean)am.get();
		//Args.ArgumentMarshaler am = booleanArgs.get(arg);
		Args.ArgumentMarshaler am = marshalers.get(arg);
		boolean b = false;
		try 
		{
			b = am != null && (Boolean) am.get();
		} 
		catch (ClassCastException e) 
		{
			b = false;
		}
		return b;
	}
	
		
	//m32
	public String getString(char arg) 
	{
		//return blankIfNull(stringArgs.get(arg));
		//Args.ArgumentMarshaler am = stringArgs.get(arg);
		//return am == null ? "" : am.getString();
		Args.ArgumentMarshaler am = marshalers.get(arg);
		try 
		{
			return am == null ? "" : (String) am.get();	
		} catch (ClassCastException e) 
		{
			return "";
		}
		
	}
		
	//m33
	public int getInt(char arg) 
	{
		//return zeroIfNull(intArgs.get(arg));
		Args.ArgumentMarshaler am = marshalers.get(arg);
		//return am == null ? 0 : am.getInteger();
		//return am == null ? 0 : (Integer) am.get();
		try 
		{
			return am == null ? 0 : (Integer) am.get();
		} catch (Exception e) 
		{
			return 0;
		}
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
	
	//C3
	private abstract class ArgumentMarshaler 
	{
		
		
		//private int integerValue;
		
		
		
		public abstract void set(String s) throws ArgsException;
		public abstract Object get();
		/*
		public Object get() 
		{
			return null;
			}
		*/
		/*
		public void setBoolean(boolean value) 
		{
			booleanValue = value;
		}
		*/
		/*
		public void setString(String s) 
		{
			stringValue = s;
			
		}
		*/
		/*
		public boolean getBoolean() 
		{
			return booleanValue;
		}
		*/
			
		/*
		public String getString() 
		{
			return stringValue == null ? "" : stringValue;
			
			
		}
		*/
		/*
		public void setInteger(int i) 
		{
			integerValue = i;
		}
		*/
		/*
		public int getInteger() 
		{
			//return integerValue;
			return intValue;
		}
		*/
	}	
	
	private class BooleanArgumentMarshaler extends ArgumentMarshaler 
	{
		private boolean booleanValue = false;
		
		public void set(String s) 
		{
			booleanValue = true;
		}
		
		public Object get() 
		{
			return booleanValue;
		}
	}
	
	private class StringArgumentMarshaler extends ArgumentMarshaler 
	{
		private String stringValue;
		
		public void set(String s) 
		{
			stringValue = s;
		}
		public Object get() 
		{
			//return null;
			return stringValue;
		}
	}
	
	private class IntegerArgumentMarshaler extends ArgumentMarshaler 
	{
		private int intValue =0;
		
		public void set(String s) throws ArgsException 
		{
			try 
			{
				intValue = Integer.parseInt(s);	
			} catch (NumberFormatException e) 
			{
				throw new ArgsException();
			}
		}
		
		public Object get() 
		{
			return intValue;
		}
	}
}
	
		