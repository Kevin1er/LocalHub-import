package export;

import model.Doctor;

import org.wikidata.wdtk.datamodel.helpers.Datamodel;
import org.wikidata.wdtk.datamodel.helpers.ItemDocumentBuilder;
import org.wikidata.wdtk.datamodel.helpers.StatementBuilder;
import org.wikidata.wdtk.wikibaseapi.ApiConnection;
import org.wikidata.wdtk.wikibaseapi.WbSearchEntitiesResult;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataEditor;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.datamodel.interfaces.*;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Export
{
    final static String siteIri = "http://qanswer-svc1.univ-st-etienne.fr/index.php";
    static ApiConnection con = new ApiConnection("http://qanswer-svc1.univ-st-etienne.fr/api.php");
    static WikibaseDataFetcher wbdf = new WikibaseDataFetcher(con, siteIri);
    static WikibaseDataEditor wbde = new WikibaseDataEditor(con, siteIri);

    /**
     * Check if an entity already exist
     * @param _entity Entity name to check
     * @return boolean
     * @throws MediaWikiApiErrorException
     */
    private static boolean exist(String _entity)
    {
        List<WbSearchEntitiesResult> entities = null;
        try {
            entities = wbdf.searchEntities(_entity, "fr");
        } catch (MediaWikiApiErrorException e) {
            e.printStackTrace();
        }
        return (!entities.isEmpty());
    }

    /**
     * Return the id of an entity if exist
     * @param _entity Entity to find id
     * @return The id if only one exist
     */
    private static String getQid(String _entity) throws Exception
    {
        if(exist(_entity))
        {
            try {
                List<WbSearchEntitiesResult> entities = wbdf.searchEntities(_entity, "fr");
                if(entities.size() >= 1)
                    return entities.get(0).getEntityId();
            } catch (MediaWikiApiErrorException e) {
                e.printStackTrace();
            }
        }

        System.out.println(_entity);
        throw new Exception();
    }

    /**
     * Export Doctor method
     * @param _doctor Doctor to export
     */
    private static void exportDoctor(Doctor _doctor) throws MediaWikiApiErrorException
    {
        ItemIdValue noid;

        if(!exist(_doctor.getTheName()))
        {
            noid = ItemIdValue.NULL;
            ArrayList<Statement> statements = init_doctor(_doctor, noid);
            createPage(noid, _doctor.getTheName(), statements);
        }
        else
        {
            List<WbSearchEntitiesResult> entities = wbdf.searchEntities(_doctor.getTheName(), "fr");
            ItemDocument document = (ItemDocument) wbdf.getEntityDocument(entities.get(0).getEntityId());
            noid = document.getItemId();
            ArrayList<Statement> statements = init_doctor(_doctor, noid);
            ArrayList<Statement> remove = merge_doctor(document.getAllStatements(), statements);
            System.out.println(remove.size());
            updatePage(document, statements, remove);
        }
    }

    /**
     * Merge old and new statements
     * @param _old Old statements (Iterator)
     * @param _new New statements (ArrayList)
     * @return ArrayList of old statement to remove
     */
    private static ArrayList<Statement> merge_doctor(Iterator<Statement> _old, ArrayList<Statement> _new)
    {
        ArrayList<Statement> result = new ArrayList<>();

        while(_old.hasNext())
        {
            Statement old = _old.next();
            for(Statement s: _new)
            {
                if(s.getClaim().getMainSnak().getPropertyId().getId().equals(old.getClaim().getMainSnak().getPropertyId().getId()))
                {
                    result.add(old);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * Init doctors statements method
     * @param _doctor Doctor
     * @param _id Item id
     */
    private static ArrayList<Statement> init_doctor(Doctor _doctor, ItemIdValue _id)
    {
        ArrayList<Statement> result = new ArrayList<>();

        try
        {
            PropertyDocument propertyTravail = (PropertyDocument) wbdf.getEntityDocument("P57");
            PropertyDocument propertyDomaine = (PropertyDocument) wbdf.getEntityDocument("P239");
            PropertyDocument propertyAdresse = (PropertyDocument) wbdf.getEntityDocument("P850");
            PropertyDocument propertyPostal = (PropertyDocument) wbdf.getEntityDocument("P7");
            PropertyDocument propertyLocalisation = (PropertyDocument) wbdf.getEntityDocument("P10");
            PropertyDocument propertyPays = (PropertyDocument) wbdf.getEntityDocument("P3");
            PropertyDocument propertyMobile = (PropertyDocument) wbdf.getEntityDocument("P981");
            PropertyDocument propertyMail = (PropertyDocument) wbdf.getEntityDocument("P1079");
            PropertyDocument propertyFax = (PropertyDocument) wbdf.getEntityDocument("P1069");

            StatementBuilder sTravail = StatementBuilder.forSubjectAndProperty(_id, propertyTravail.getPropertyId());
            sTravail.withValue(Datamodel.makeItemIdValue("Q272",siteIri));
            Statement statTravail = sTravail.build();
            result.add(statTravail);

            StatementBuilder sDomaine = StatementBuilder.forSubjectAndProperty(_id, propertyDomaine.getPropertyId());
            sDomaine.withValue(Datamodel.makeItemIdValue(getQid(_doctor.getCategory()),siteIri));
            Statement statDomaine = sDomaine.build();
            result.add(statDomaine);

            StatementBuilder sAdresse = StatementBuilder.forSubjectAndProperty(_id, propertyAdresse.getPropertyId());
            sAdresse.withValue(Datamodel.makeMonolingualTextValue(_doctor.getAdress().getAddress(), "fr"));
            Statement statAdresse = sAdresse.build();
            result.add(statAdresse);

            StatementBuilder sPostal = StatementBuilder.forSubjectAndProperty(_id, propertyPostal.getPropertyId());
            sPostal.withValue(Datamodel.makeStringValue(_doctor.getAdress().getPostalcode()));
            Statement statPostal = sPostal.build();
            result.add(statPostal);

            StatementBuilder sLocalisation = StatementBuilder.forSubjectAndProperty(_id, propertyLocalisation.getPropertyId());
            if(_doctor.getAdress().getCity().equals("SAINT AGREVE"))
                sLocalisation.withValue(Datamodel.makeItemIdValue("Q28",siteIri));
            else sLocalisation.withValue(Datamodel.makeItemIdValue("Q2",siteIri));
            Statement statLocalisation = sLocalisation.build();
            result.add(statLocalisation);

            StatementBuilder sPays = StatementBuilder.forSubjectAndProperty(_id, propertyPays.getPropertyId());
            sPays.withValue(Datamodel.makeItemIdValue("Q6",siteIri));
            Statement statPays = sPays.build();
            result.add(statPays);

            if(_doctor.getContact().getFax() != null)
            {
                StatementBuilder sFax = StatementBuilder.forSubjectAndProperty(_id, propertyFax.getPropertyId());
                sFax.withValue(Datamodel.makeMonolingualTextValue(_doctor.getContact().getFax(), "fr"));
                Statement statFax = sFax.build();

                result.add(statFax);
            }

            if(_doctor.getContact().getTel() != null)
            {
                StatementBuilder sMobile = StatementBuilder.forSubjectAndProperty(_id, propertyMobile.getPropertyId());
                sMobile.withValue(Datamodel.makeStringValue(_doctor.getContact().getTel()));
                Statement statMobile = sMobile.build();

                result.add(statMobile);
            }
            else if(_doctor.getContact().getMobile() != null)
            {
                StatementBuilder sMobile = StatementBuilder.forSubjectAndProperty(_id, propertyMobile.getPropertyId());
                sMobile.withValue(Datamodel.makeStringValue(_doctor.getContact().getMobile()));
                Statement statMobile = sMobile.build();

                result.add(statMobile);
            }

            if(_doctor.getContact().getMail() != null)
            {
                StatementBuilder sMail = StatementBuilder.forSubjectAndProperty(_id, propertyMail.getPropertyId());
                sMail.withValue(Datamodel.makeMonolingualTextValue(_doctor.getContact().getMail(), "fr"));
                Statement statMail = sMail.build();

                result.add(statMail);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Create page method
     * @param _label Label of the page
     * @param _statement Statements of the page
     */
    private static void createPage(ItemIdValue _id, String _label, ArrayList<Statement> _statement)
    {
        ItemDocumentBuilder builder = ItemDocumentBuilder.forItemId(_id)
                .withLabel(_label, "en")
                .withLabel(_label, "fr");

        for(Statement s: _statement)
            builder.withStatement(s);

        ItemDocument itemDocument = builder.build();

        try
        {
            ItemDocument newItemDocument = wbde.createItemDocument(itemDocument,"Statement created by our bot");

        } catch (IOException | MediaWikiApiErrorException e) {
            e.printStackTrace();
        }
    }

    /**
     * Update page method
     * @param _document Document to modify
     * @param _statements Statements to add
     * @param _remove Statements to remove
     */
    private static void updatePage(ItemDocument _document, ArrayList<Statement> _statements, ArrayList<Statement> _remove)
    {
        try {
            _document = wbde.updateStatements(_document.getItemId(), _statements, _remove, "Test statement addition");

        } catch (MediaWikiApiErrorException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Export main method
     * @param _object Object to export
     * @throws MediaWikiApiErrorException
     */
    public static void export(Object _object) throws Exception
    {
        if(_object instanceof Doctor) exportDoctor((Doctor) _object);
        else throw new Exception();
    }
}