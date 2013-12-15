package market;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class MyServer extends UnicastRemoteObject implements ServerInterface {

    private ArrayList<ClientInterface> clientTable = new ArrayList<>();
    private ArrayList<Item> itemList = new ArrayList<>();
    private ArrayList<Item> wishList = new ArrayList<>();
    
    private Bank bankInterface;

    public MyServer() throws RemoteException, MalformedURLException, NotBoundException {
        super();
        try {
            LocateRegistry.getRegistry(8888).list();
        } catch (RemoteException e) {
            LocateRegistry.createRegistry(8888);
        }
        Naming.rebind("rmi://localhost:8888/market", this);
        bankInterface = (Bank) Naming.lookup("rmi://localhost:6666/Nordea");
    }

    public synchronized void addItem(Item item) throws RemoteException {
        itemList.add(item);
        System.out.println(item.getName());
        updateAll();
        checkWishList(item);
    }
    
    public void addWish(ClientInterface wisher, Item item) throws RemoteException {
        if(!checkMarket(wisher, item))
        {
            wishList.add(item);
        }
    }

    public synchronized void updateAll() {
        for (ClientInterface client : clientTable) {
            try
            {
                client.updateMarket(itemList);
            } catch (RemoteException ex)
            {
                Logger.getLogger(MyServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }        
    }
    
    public boolean checkMarket(ClientInterface wisher, Item item) throws RemoteException
    {
        boolean bl = false;
        System.out.println(itemList.size());
        for(int i=0; i<itemList.size(); i++)
        {
            if(item.getName().equals(itemList.get(i).getName()))
            {
                if(item.getPrice() >= itemList.get(i).getPrice())
                {
                    wisher.notifyWish(itemList.get(i));
                    wisher.updateWish(item);
                    bl = true;
                    
                }
            }
        }
        return bl;
    }    
    
    public void checkWishList(Item item) throws RemoteException
    {
        System.out.println(wishList.size());
        for(int i=0; i<wishList.size(); i++)
        {
            if(item.getName().equals(wishList.get(i).getName()))
            {
                if(item.getPrice() <= wishList.get(i).getPrice())
                {
                    wishList.get(i).getOwnerInterface().notifyWish(item);
                    wishList.get(i).getOwnerInterface().updateWish(item);
                    wishList.remove(i);
                    --i;
                }
            }
        }
    }
    

    public void removeItem(ArrayList<Item> list, ClientInterface client) {
        for(int i=0; i<list.size(); i++)
        {
            if(list.get(i).getOwnerInterface().equals(client))
            {
                list.remove(i);
                --i;
            }
        }
    }

    public void registerClient(ClientInterface client) throws RemoteException {
        if (clientTable.contains(client)) {
            throw new RemoteException("client already registered");
        }
        clientTable.add(client);
    }

    public synchronized void unregisterClient(ClientInterface client) throws RemoteException {
        if (!clientTable.contains(client)) {
            throw new RemoteException("client not registered");
        }
        clientTable.remove(client);
        //System.out.println(itemList.size()+","+wishList.size());
        removeItem(itemList, client);
        removeItem(wishList, client);
        updateAll();
//        System.out.println(itemList.size()+","+wishList.size());
//        System.out.println(clientTable.size());
    }

    public synchronized void buyItem(String buyer, ClientInterface buyerInterface, String name, float price, String seller) throws RemoteException
    {
        Account buyerAcc = bankInterface.getAccount(buyer);
        Account sellerAcc = bankInterface.getAccount(seller);
        try
        {
            buyerAcc.withdraw(price);
            sellerAcc.deposit(price);
            for(int i=0; i<itemList.size(); i++)
            {
                if(itemList.get(i).getName().equals(name) && itemList.get(i).getPrice() == price && itemList.get(i).getOwner().equals(seller))
                {
                    ClientInterface sellerInterface = itemList.get(i).getOwnerInterface();
                    sellerInterface.notifySold(name, price);
                    buyerInterface.updateBalance(buyerAcc.getBalance());
                    sellerInterface.updateBalance(sellerAcc.getBalance());
                    itemList.remove(i);
                    --i;
                    break;
                }
            }
            updateAll();
            
        } catch (RejectedException ex)
        {
            buyerInterface.notifyLackMoney();
        }
    }

    public static void main(String[] args) throws NotBoundException {
        try {
            new MyServer();
            System.out.println("Market is ready!");
        } catch (RemoteException re) {
            System.out.println(re);
            System.exit(1);
        } catch (MalformedURLException me) {
            System.out.println(me);
            System.exit(1);
        }
    }

}
