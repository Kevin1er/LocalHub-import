package scansite;

import export.Export;
import model.Adress;
import model.Contact;
import model.Doctor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

public class ScanSite
{
    /**
     * @param args the command line arguments
     * @throws java.net.MalformedURLException
     */
    public static void main(String[] args) throws MalformedURLException, IOException, Exception
    {
        ArrayList<Doctor> doctors = new ArrayList<>();

        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/medecins-1.html", "médecin");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/veterinaires-2.html", "Vétérinaire");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/orthophoniste-3.html", "Orthophoniste");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/sages-femmes-4.html", "Sage-femme");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/pharmaciens-5.html", "Pharmacien");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/kinesitherapeutes-6.html", "Kinésithérapeute");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/ostheopathe-8.html", "Ostéopathe");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/dentistes-9.html", "Dentiste");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/etablissements-specialises-10.html", "Etablissement spécialisé");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/cabinet-infirmiers-12.html", "Cabinet Infirmier");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/ambulances-13.html", "Ambulance");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/pedicure-podologue-14.html", "Pédicure-podologue");
        getDoctors(doctors, "https://www.ville-lechambonsurlignon.fr/professionnels-sante/acupuncture-15.html", "Acupuncture");

        for(Doctor doc : doctors)
        {
            System.out.println(doc);
            //Export.export(doc);
        }
        Export.export(doctors.get(0));
    }

    private static void getDoctors(ArrayList<Doctor> _doctor, String _url, String _type) throws IOException, Exception
    {
        Document doc = Jsoup.connect(_url).get();
        ArrayList<String[]> nom = getNom(doc);
        ArrayList<Adress> adresse = new ArrayList<>();
        ArrayList<Contact> contact = new ArrayList<>();
        getInfo(doc, nom, adresse, contact);

        if(nom.size() != adresse.size()) throw new Exception();

        for(int i = 0; i < nom.size(); i++)
        {
            if(isUpperCase(nom.get(i)[0]))
                _doctor.add(new Doctor(nom.get(i)[0], nom.get(i)[1], _type, adresse.get(i), contact.get(i)));
            else if(isUpperCase(nom.get(i)[1]))
                _doctor.add(new Doctor(nom.get(i)[1], nom.get(i)[0], _type, adresse.get(i), contact.get(i)));
            else
            {
                StringBuilder company_name = new StringBuilder();
                for(int k = 0; k < nom.get(i).length; k++) company_name.append(nom.get(i)[k]).append(" ");
                _doctor.add(new Doctor(company_name.toString(), _type, adresse.get(i), contact.get(i)));
            }
        }
    }

    /**
     * Getting Name, firstname or Company name
     */
    private static ArrayList<String[]> getNom(Document _document)
    {
        ArrayList<String[]> result = new ArrayList<>();
        Elements elements = _document.select(".bg_annuaire div");
        for (Element element : elements)
        {
            String[] nom = element.text().split(" ");
            result.add(nom);
        }

        return result;
    }

    /**
     * Getting Adress and Contacts
     */
    private static void getInfo(Document _document, ArrayList<String[]> _nom, ArrayList<Adress> _adress, ArrayList<Contact> _contact)
    {
        Elements elements = _document.select(".bg_annuaire span");
        for (int i = 0; i < elements.size(); i++)
        {
            Adress adress = new Adress();
            Contact contact = new Contact();

            String elem = elements.get(i).text();
            elem = elem.replaceAll(",", "");
            String[] strs = elem.split(" ");

            //Vérif nom
            int index = 0;

            for(String s : _nom.get(i))
            {
                if(strs[index].equals(s)) index++;
            }

            //Vérif num rue
            if(isInteger(strs[index])) adress.setStreetnum(strs[index++]);

            //Ajout rue
            StringBuilder builder = new StringBuilder();
            while(!isInteger(strs[index]))
            {
                builder.append(strs[index]).append(" ");
                index++;
            }
            builder.deleteCharAt(builder.length() - 1);
            adress.setStreet(builder.toString());

            //Ajout code postal
            adress.setPostalcode(strs[index++]);

            //Ajout ville
            StringBuilder builder2 = new StringBuilder();
            while(!strs[index].contains("."))
            {
                builder2.append(strs[index]).append(" ");
                index++;
            }
            builder2.deleteCharAt(builder2.length() - 1);
            adress.setCity(builder2.toString().replaceAll("-", " "));

            //Ajout contact
            while(index < strs.length)
            {
                switch(strs[index])
                {
                    case "Tél.":
                        index++;
                        contact.setTel(strs[index]);
                        index++;
                        break;
                    case "Port.":
                        index++;
                        contact.setMobile(strs[index]);
                        index++;
                        break;
                    case "Fax":
                        index += 2;
                        contact.setFax(strs[index]);
                        index++;
                        break;
                    default:
                        //Mail
                        if(strs[index].contains("@"))
                            contact.setMail(strs[index]);
                        index++;
                }
            }

            _adress.add(adress);
            _contact.add(contact);
        }
    }


    /**
     * Int test
     */
    private static boolean isInteger(String _str)
    {
        try
        {
            Integer.parseInt(_str);
        } catch (NumberFormatException e){
            return false;
        }

        return true;
    }

    /**
     * UpperCase test
     */
    private static boolean isUpperCase(String _str)
    {
        String test = _str.toUpperCase();
        return test.equals(_str);
    }
}