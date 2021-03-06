package com.SafetyNet.Safety.util;

import com.SafetyNet.Safety.model.FireStation;
import com.SafetyNet.Safety.service.FireStationService;
import com.SafetyNet.Safety.service.PersonService;
import com.google.gson.*;
import com.SafetyNet.Safety.model.Person;

import org.apache.logging.log4j.LogManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import java.util.*;

@Service
public class ImportData {
    private final static org.apache.logging.log4j.Logger logger = LogManager.getLogger("ImportData") ;

    @Autowired
    private PersonService personService;

    @Autowired
    private FireStationService fireStationService;

    private Gson gson = new Gson();
    private URL url  = new URL("https://s3-eu-west-1.amazonaws.com/course.oc-static.com/projects/DA+Java+EN/P5+/data.json");

    public ImportData() throws MalformedURLException {
    }

    @PostConstruct
    public Boolean load() {
        logger.info("Chargement des données");
        //Récuperation du fichier Json et extraction des données.
        JsonObject jsonObject = loadFile();
        if (jsonObject == null){
            logger.error("Aucune donnée n'est trouvée");
            return false;
        }
        if(jsonObject.isJsonNull()) {
            logger.error("Aucune donnée n'est trouvée");
            return false;
        }else{
            JsonElement persons = jsonObject.get("persons");
            JsonElement firestations = jsonObject.get("firestations");
            JsonElement medicalrecords = jsonObject.get("medicalrecords");
            //Traitement des differentes informations
            loadPersons(persons);
            loadFireStations(firestations);
            loadMedicalRecords(medicalrecords);
            return true;

        }

    }

    /**
     * traitement du fichier distant web*/
    public JsonObject loadFile()   {

        InputStream urlLoad = null;
        JsonObject jsonObject = null;
        try {
            urlLoad = url.openStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(urlLoad, Charset.forName("UTF-8")));
            StringBuilder sb = new StringBuilder();
            String cp;
            while ((cp = rd.readLine()) != null) {
                sb.append(cp);
            }
            jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();
        }catch (Exception e){
            logger.error(e.getMessage());
        }finally {
            if(urlLoad != null){
                try{
                    urlLoad.close();
                }catch (Exception e){
                    logger.error(e.getMessage());
                }
            }
        }
        return jsonObject;
    }


    /**
     * Chargement des persons à partir du jsonObject
     **/
    private void loadPersons(JsonElement persons) {
        if ( persons == null || persons.isJsonNull()){
            logger.error("Aucune donnée n'est trouvée pour Persons lors du chargement des informations");
        }else{
            JsonArray personsArray = persons.getAsJsonArray();
            for (JsonElement jsonPerson : personsArray
            ) {
                Person person = gson.fromJson(jsonPerson, Person.class);
                personService.personSave(person);
            }
            logger.info("Fin chargement Person");
        }
    }

    /**
     * Chargement des Firestations à partir du jsonObject
     **/
    private void loadFireStations(JsonElement fireStations) {
        if (fireStations == null || fireStations.isJsonNull()){
            logger.error("Aucune donnée n'est trouvée pour Firestations lors du chargement des informations");
        }else {
            JsonArray firestationArray = fireStations.getAsJsonArray();
            for (JsonElement jsonFireStation : firestationArray
            ) {
                JsonObject jsonObject = jsonFireStation.getAsJsonObject();

                int id = jsonObject.get("station").getAsInt();
                String address = jsonObject.get("address").getAsString();

                // Si l'ID existe, alors on ajoute l'adresse, sinon on effectue une nouvelle sauvegarde.
                if (fireStationService.findById(id) != null) {
                    //Vérification doublon address
                    if (!fireStationService.findById(id).getAddress().contains(address)) {
                        fireStationService.addAddress(address, id);
                    }
                } else {
                    FireStation fireStation = new FireStation(new ArrayList<String>(Arrays.asList(new String[]{address})), id);
                    fireStationService.save(fireStation);
                }
            }
            logger.info("Fin chargement firestation");
        }
    }

    /**
     * Chargement des MedicalRecords à partir du jsonObject
     **/
    private void loadMedicalRecords(JsonElement medicalRecords)  {
        if (medicalRecords ==null||medicalRecords.isJsonNull()){
            logger.error("Aucune donnée n'est trouvée pour MedicalsRecords lors du chargement des informations");
        }else{
            JsonArray medicalRecordsArray = medicalRecords.getAsJsonArray();

            for (JsonElement jsonMedicalRecord : medicalRecordsArray
            ) {
                JsonObject jsonObject = jsonMedicalRecord.getAsJsonObject();
                String firstName = jsonObject.get("firstName").getAsString();
                String lastName = jsonObject.get("lastName").getAsString();

                //Récuperation de la person
                Person person = personService.findByFirstNameLastName(firstName, lastName);

                //Sauvegarde des informations medicalRecords
                person.setMedical(gson.fromJson(jsonObject.getAsJsonArray("medications"), List.class));
                person.setAllergies(gson.fromJson(jsonObject.getAsJsonArray("allergies"), List.class));
                String dateString = jsonObject.get("birthdate").getAsString();
                person.setBirthdate(dateString);
            }
            logger.info("Fin chargement medicalRecords");
        }
    }
}
