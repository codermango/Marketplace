/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package market;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 *
 * @author Mark
 */
public interface ClientInterface extends Remote
{
    public void updateMarket(ArrayList<Item> itemList) throws RemoteException;
    public void notifyWish(Item item) throws RemoteException;
    public void updateWish(Item item) throws RemoteException;
    
    public void notifyLackMoney() throws RemoteException;
    public void notifySold(String name, float price) throws RemoteException;
    
    public void updateBalance(float balance) throws RemoteException;
}
