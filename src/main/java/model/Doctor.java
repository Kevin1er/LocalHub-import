package model;

public class Doctor
{
    private String company_name;
    private String name;
    private String firstname;
    private String category;
    private Adress adress;
    private Contact contact;

    public Doctor(String _name, String _firstname, String _category, Adress _adress, Contact _contact)
    {
        company_name = null;
        name = _name;
        firstname = _firstname;
        category = _category;
        adress = _adress;
        contact = _contact;
    }

    public Doctor(String _company_name, String _category, Adress _adress, Contact _contact)
    {
        company_name = _company_name;
        name = null;
        firstname = null;
        category = _category;
        adress = _adress;
        contact = _contact;
    }

    //Getter
    public String getCompany_name() { return company_name; }
    public String getName() { return name; }
    public String getFirstname() { return firstname; }
    public String getCategory() { return category; }
    public Adress getAdress() { return adress; }
    public Contact getContact() { return contact; }
    public String getFullName() { return firstname + " " + name; }
    public String getTheName()
    {
        if(company_name != null) return company_name;
        else return firstname + " " + name;
    }

    //Setter
    public void setCompany_name(String _company_name) { company_name = _company_name; }
    public void setName(String _name) { name = _name; }
    public void setFirstname(String _firstname) { firstname = _firstname; }
    public void setCategory(String _category) { category = _category; }
    public void setAdress(Adress _adress) { adress = _adress; }
    public void setContact(Contact _contact) { contact = _contact; }

    @Override
    public String toString()
    {
        if(company_name != null) return "Company : " + company_name + " " + category + " " + adress + " " + contact;
        else return "Doctor : " + name + " " + firstname + " " + category + " " + adress + " " + contact;
    }
}