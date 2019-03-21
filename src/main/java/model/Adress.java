package model;

public class Adress
{
    private String streetnum;
    private String street;
    private String postalcode;
    private String city;

    public Adress()
    {
        streetnum = null;
        street = null;
        postalcode = null;
        city = null;
    }

    public Adress(String _street, String _postalcode, String _city)
    {
        street = _street;
        postalcode = _postalcode;
        city = _city;
        streetnum = null;
    }

    public Adress(String _num, String _street, String _postalcode, String _city)
    {
        street = _street;
        postalcode = _postalcode;
        city = _city;
        streetnum = _num;
    }

    //Getter
    public String getStreetnum() { return streetnum; }
    public String getStreet() { return street; }
    public String getPostalcode() { return postalcode; }
    public String getCity() { return city; }
    public String getAddress()
    {
        return streetnum + ", " + street;
    }

    //Setter
    public void setStreetnum(String _streetnum) { streetnum = _streetnum; }
    public void setStreet(String _street) { street = _street; }
    public void setPostalcode(String _postalcode) { postalcode = _postalcode; }
    public void setCity(String _city) { city = _city; }

    @Override
    public String toString()
    {
        String result = new String();
        if(streetnum != null) result += streetnum + ", ";
        if(street != null) result += street + " ";
        if(postalcode != null) result += postalcode + " ";
        if(city != null) result += city;

        return result;
    }
}