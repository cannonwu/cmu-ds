//**************************************************************
// CalculatorServer.java             Serve remote Calculator
// Creates a calculator object and gives
// it a name in the registry.
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.StringTokenizer;
public class BumperServer {
	public static void main(String args[]){
          System.out.println("Calculator Server Running");
          try{
            // create the servant
            Bumper c = new BumperServant();
            System.out.println("Created Calculator object");
            System.out.println("Placing in registry");
            // publish to registry
	          Naming.rebind("CoolBumper", c);
            System.out.println("CalculatorServant object ready");
           }catch(Exception e) {
            System.out.println("CalculatorServer error main " + e.getMessage());
        }
    }
}
