//*************************************************************
// CalculatorServant.java
// A Remote object class that implements Calculator.
import java.rmi.*;
import java.rmi.server.*;
import java.io.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.StringTokenizer;
public class BumperServant extends UnicastRemoteObject implements Bumper {
    public BumperServant() throws RemoteException {
    }
	
	static BigInteger serverNum = new BigInteger("0");
	
	// The remote bump() method behaves as follows:
    public boolean bump(BigInteger userNum) throws RemoteException {
      // A call on bump() adds 1 to a BigInteger held by the service.
      // It then returns true on completion.
      // The BigInteger is changed by the call on bump(). That is,
      // 1 is added to the BigInteger and that value persists until
      // another call on bump occurs.
	  BigInteger bumpNum = new BigInteger("1");
      userNum = userNum.add(bumpNum);
	  serverNum = userNum;
	  return true;
    }

    public BigInteger get() throws RemoteException {
      // a call on get returns the BigInteger held by the service
	  return serverNum;
    }
}
