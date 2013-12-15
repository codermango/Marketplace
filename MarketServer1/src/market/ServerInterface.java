package market;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface ServerInterface extends Remote {

    void registerClient(ClientInterface obj) throws RemoteException;
    void unregisterClient(ClientInterface obj) throws RemoteException;
    void addItem(Item item) throws RemoteException;
    void addWish(ClientInterface wisher, Item item) throws RemoteException;
    
    public void buyItem(String buyer, ClientInterface buyerInterface, String name, float price, String seller) throws RemoteException;

   

}
