//*******************************************************
// CalculatorClient.java
// This client gets a remote reference from the rmiregistry
// that is listening on the default port of 1099.
// It allows the client to quit with a "!".
// Otherwise, it computes the sum of two integers
// using the remote calculator.

import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.StringTokenizer;

public class BumperClient {
   public static void main(String args[]) throws Exception {
        // connect to the rmiregistry and get a remote reference to the Calculator
        // object.
        Bumper c  = (Bumper) Naming.lookup("//localhost/CoolBumper");
		
		System.out.println("Found Bumper");
		BigInteger ctr = new BigInteger("0");
		BigInteger n = new BigInteger("10000");
		long start = System.currentTimeMillis();
		while (ctr.compareTo(n) < 0) {
			ctr = ctr.add(new BigInteger("1"));
			c.bump(ctr);
			//ctr = c.bump(ctr);
		}
		long stop = System.currentTimeMillis();
		System.out.println(c.get());
		System.out.println("Time in ms: " + (stop - start));
    }
}