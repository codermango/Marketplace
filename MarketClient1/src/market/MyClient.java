/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package market;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Mark
 */
public class MyClient extends UnicastRemoteObject implements ClientInterface
{

    private String clientName;
    private ClientFrame clientFrame;
    private ServerInterface serverInterface;
    private Bank bankInterface;
    private static MyClient me;

    DefaultTableModel tbModel;
    DefaultListModel ltModel;

    public MyClient(String name) throws RemoteException, NotBoundException, MalformedURLException, RejectedException
    {
        super();
        this.clientName = name;
        clientFrame = new ClientFrame();
        clientFrame.setVisible(true);
        clientFrame.lblName.setText(name);

//        clientFrame.btnAddItem.setEnabled(false);
//        clientFrame.btnAddWish.setEnabled(false);
//        clientFrame.btnBuy.setEnabled(false);
//        clientFrame.btnUnregister.setEnabled(false);

        serverInterface = (ServerInterface) Naming.lookup("rmi://localhost:8888/market");
        bankInterface = (Bank) Naming.lookup("rmi://localhost:6666/Nordea");
        Account account = bankInterface.newAccount(name);
        account.deposit(1000);
        clientFrame.lblBalance.setText(String.valueOf(1000));
        

        setup();

        tbModel = new DefaultTableModel();
        ltModel = new DefaultListModel();
        clientFrame.listInform.setModel(new DefaultListModel());

    }

//    public static void main(String[] args) throws RemoteException, NotBoundException, MalformedURLException, RejectedException
//    {
//
//        me = new MyClient("Mark111");
//        System.out.println(me.clientName+" is ready!");
//
//    }

    public void setup()
    {

        clientFrame.btnAddItem.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                String name = clientFrame.txtItemName.getText();
                float price = Float.parseFloat(clientFrame.txtItemPrice.getText());
                tbModel = (DefaultTableModel) clientFrame.tbSellingList.getModel();
                tbModel.addRow(new Object[]
                {
                    name, price
                });

                Item item = new Item(name, price, me.clientName, me);
                try
                {
                    serverInterface.addItem(item);
                } catch (RemoteException ex)
                {
                    Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                clientFrame.txtItemName.setText("");
                clientFrame.txtItemPrice.setText("");
            }
        });

        clientFrame.btnUnregister.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                clientFrame.btnAddItem.setEnabled(false);
                clientFrame.btnAddWish.setEnabled(false);
                clientFrame.btnBuy.setEnabled(false);
                clientFrame.btnUnregister.setEnabled(false);

                try
                {
                    serverInterface.unregisterClient(me);
                } catch (RemoteException ex)
                {
                    Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                }
                tbModel = (DefaultTableModel) clientFrame.tbSellingList.getModel();
                tbModel.setRowCount(0);
                tbModel = (DefaultTableModel) clientFrame.tbMarket.getModel();
                tbModel.setRowCount(0);
                tbModel = (DefaultTableModel) clientFrame.tbWishList.getModel();
                tbModel.setRowCount(0);
            }
        });

        clientFrame.btnAddWish.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {

                String name = clientFrame.txtWishName.getText();
                float price = Float.parseFloat(clientFrame.txtWishPrice.getText());
                tbModel = (DefaultTableModel) clientFrame.tbWishList.getModel();
                tbModel.addRow(new Object[]
                {
                    name, price, me.clientName, me
                });

                Item item = new Item(name, price, me.clientName, me);
                try
                {
                    serverInterface.addWish(me, item);
                } catch (RemoteException ex)
                {
                    Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
                clientFrame.txtWishName.setText("");
                clientFrame.txtWishPrice.setText("");
            }
        });

        clientFrame.btnBuy.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                int index = clientFrame.tbMarket.getSelectedRow();
                if (index == -1)
                {
                    JOptionPane.showMessageDialog(clientFrame, "No items selected!!");
                    return;
                } else
                {
                    String name = clientFrame.tbMarket.getValueAt(index, 0).toString();
                    float price = Float.parseFloat(clientFrame.tbMarket.getValueAt(index, 1).toString());
                    String owner = clientFrame.tbMarket.getValueAt(index, 2).toString();
                    try
                    {
                        serverInterface.buyItem(me.clientName, me, name, price, owner);                     
                    } catch (RemoteException ex)
                    {
                        Logger.getLogger(MyClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

    }

    @Override
    public void updateMarket(ArrayList<Item> itemList) throws RemoteException
    {
        Object[] items = itemList.toArray();
        tbModel = (DefaultTableModel) clientFrame.tbMarket.getModel();
        tbModel.setRowCount(0);
        for (int i = 0; i < itemList.size(); i++)
        {
            tbModel.addRow(new Object[]
            {
                itemList.get(i).getName(), itemList.get(i).getPrice(), itemList.get(i).getOwner()
            });
            //tbModel.addRow(items);
        }
    }

    @Override
    public void notifyWish(Item item) throws RemoteException
    {
        DateFormat df = DateFormat.getDateTimeInstance();
        String str = df.format(new Date());
        String temp = str + " An item meets your wish: ";
        temp += item.getName() + "," + String.valueOf(item.getPrice()) + "," + item.getOwner();

        ltModel = (DefaultListModel) clientFrame.listInform.getModel();
        ltModel.addElement(temp);
    }

    @Override
    public void updateWish(Item item) throws RemoteException
    {
        tbModel = (DefaultTableModel) clientFrame.tbWishList.getModel();
        for (int i = 0; i < tbModel.getRowCount(); i++)
        {
            if (tbModel.getValueAt(i, 0).toString().equals(item.getName()) && tbModel.getValueAt(i, 1).toString().equals(String.valueOf(item.getPrice())))
            {
                tbModel.removeRow(i);
                --i;
            }
        }
    }

    @Override
    public void notifyLackMoney() throws RemoteException
    {
        DateFormat df = DateFormat.getDateTimeInstance();
        String str = df.format(new Date());
        String temp = str + " Your balance is not enough!: ";

        ltModel = (DefaultListModel) clientFrame.listInform.getModel();
        ltModel.addElement(temp);
    }

    @Override
    public void notifySold(String name, float price) throws RemoteException
    {
        DateFormat df = DateFormat.getDateTimeInstance();
        String str = df.format(new Date());
        String temp = str + " Your item has been sold: "+name+", "+String.valueOf(price);

        ltModel = (DefaultListModel) clientFrame.listInform.getModel();
        ltModel.addElement(temp);
        
        tbModel = (DefaultTableModel) clientFrame.tbSellingList.getModel();
        for (int i = 0; i < tbModel.getRowCount(); i++)
        {
            if (tbModel.getValueAt(i, 0).equals(name) && tbModel.getValueAt(i, 1).toString().equals(String.valueOf(price)))
            {
                tbModel.removeRow(i);
                --i;
                break;
            }
        }
    }

    @Override
    public void updateBalance(float balance) throws RemoteException
    {
        clientFrame.lblBalance.setText(String.valueOf(balance));
    }

}
