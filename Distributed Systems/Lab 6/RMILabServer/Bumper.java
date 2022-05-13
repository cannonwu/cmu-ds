//************************************************************
// Calculator.java                Interface for a Calculator
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.StringTokenizer;
public interface Bumper extends Remote {
   
   boolean bump (BigInteger userNum) throws RemoteException;
   
   BigInteger get() throws RemoteException;
   
}
