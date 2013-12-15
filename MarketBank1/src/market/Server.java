package market;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

public class Server {

    private static final String USAGE = "java bankrmi.Server <bank_rmi_url>";
    private static final String BANK = "Nordea";

    public Server(String bankName) {
        try {
            Bank bankobj = new BankImpl(bankName);
            // Register the newly created object at rmiregistry.
            try {
                LocateRegistry.getRegistry(6666).list();
            } catch (RemoteException e) {
                LocateRegistry.createRegistry(6666);
            }
            Naming.rebind("rmi://localhost:6666/"+bankName, bankobj);
            System.out.println(bankName + " is ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length > 1 || (args.length > 0 && args[0].equalsIgnoreCase("-h"))) {
            System.out.println(USAGE);
            System.exit(1);
        }

        String bankName;
        if (args.length > 0) {
            bankName = args[0];
        } else {
            bankName = BANK;
        }

        new Server(bankName);
    }
}
