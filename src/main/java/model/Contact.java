package model;

public class Contact
{
    private String mobile;
    private String tel;
    private String mail;
    private String fax;

    public Contact()
    {
        mobile = null;
        tel = null;
        mail = null;
        fax = null;
    }

    public Contact(String _mobile, String _tel, String _mail, String _fax)
    {
        mobile = _mobile;
        tel = _tel;
        mail = _mail;
        fax = _fax;
    }

    //Getter
    public String getMobile() { return mobile; }
    public String getTel() { return tel; }
    public String getMail() { return mail; }
    public String getFax() { return fax; }

    //Setter
    public void setMobile(String _mobile) { mobile = _mobile; }
    public void setTel(String _tel) { tel = _tel; }
    public void setMail(String _mail) { mail = _mail; }
    public void setFax(String _fax) { fax = _fax; }

    @Override
    public String toString()
    {
        String result = new String();
        if(tel != null) result += "Tel : " + tel + " | ";
        if(mobile != null) result += "Mobile : " + mobile + " | ";
        if(mail != null) result += "Mail : " + mail + " | ";
        if(fax != null) result += "Fax : " + fax;
        return result;
    }
}