package market;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

public interface Bank extends Remote {
    public Account newAccount(String name) throws RemoteException, RejectedException;
    public Account getAccount(String name) throws RemoteException;
    public boolean deleteAccount(String name) throws RemoteException;
    public String[] listAccounts() throws RemoteException;
}
