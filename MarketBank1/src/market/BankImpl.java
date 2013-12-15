package market;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("serial")
public class BankImpl extends UnicastRemoteObject implements Bank {

    private String bankName;
    private Map<String, Account> accounts = new HashMap<String, Account>();

    public BankImpl(String bankName) throws RemoteException {
        super();
        this.bankName = bankName;
    }

    public synchronized String[] listAccounts() {
        return accounts.keySet().toArray(new String[1]);
    }

    public synchronized Account newAccount(String name) throws RemoteException, RejectedException {
        AccountImpl account = (AccountImpl) accounts.get(name);
        if (account != null) {
            System.out.println("Account [" + name + "] exists!!!");
            throw new RejectedException("Rejected: Bank: " + bankName + " Account for: " + name + " already exists: " + account);
        }
        account = new AccountImpl(name);
        accounts.put(name, account);
        System.out.println("Bank: " + bankName + " Account: " + account + " has been created for " + name);
        return account;
    }

    public synchronized Account getAccount(String name) {
        return accounts.get(name);
    }

    public synchronized boolean deleteAccount(String name) {
        if (!hasAccount(name)) {
            return false;
        }
        accounts.remove(name);
        System.out.println("Bank: " + bankName + " Account for " + name + " has been deleted");
        return true;
    }

    private boolean hasAccount(String name) {
        if (accounts.get(name) == null) {
            return false;
        } else {
            return true;
        }
    }
}
