package Allstate;

public class main {

	// m1 - main
	public static void main(String[] args) 
	{
		//
		try 
		{
			Args arg = new Args("l,p#,d*", args);
			boolean logging = arg.getBoolean('l')
			int port = arg.getInt('p');
			String directory = arg.getString('d');
			//executeApplication(logging, port, directory);
			System.out.println("Running executeApplication Method...");
			System.out.println("logging: " + logging);
			System.out.println("port: " + port);
			System.out.println("directory:" + directory);
		} 
		catch (Exception e) 
		{
			//System.out.printf("Argument error: %s\n", e.printStackTrace());
			System.out.println("bug 1");
		}
		
		System.out.println("Program Ends now.");
	}
}
