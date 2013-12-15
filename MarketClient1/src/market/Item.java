package market;

import java.io.Serializable;

public class Item implements Serializable
{

    private String name;
    private float pirce;
    private String owner;
    private ClientInterface ownerInterface;

    public Item(String name, float price, String owner, ClientInterface ownerInterface)
    {
        this.name = name;
        this.pirce = price;
        this.owner = owner;
        this.ownerInterface = ownerInterface;
    }

    public String getName()
    {
        return this.name;
    }

    public float getPrice()
    {
        return this.pirce;
    }

    public String getOwner()
    {
        return this.owner;
    }

    public ClientInterface getOwnerInterface()
    {
        return this.ownerInterface;
    }
}
