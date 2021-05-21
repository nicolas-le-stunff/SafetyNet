package com.SafetyNet.Safety.util;

import com.google.gson.*;
import com.SafetyNet.Safety.model.Person;
import com.SafetyNet.Safety.service.PersonService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

@Service
public class ImportData {

    @Autowired
    private PersonService personService;

   @PostConstruct
    public void load() throws IOException {
        InputStream url = new URL("https://s3-eu-west-1.amazonaws.com/course.oc-static.com/projects/DA+Java+EN/P5+/data.json").openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(url, Charset.forName("UTF-8")));
        StringBuilder sb = new StringBuilder();
        String cp;

        while ((cp = rd.readLine()) != null) {
            System.out.println(cp);
            sb.append(cp);
        }
        JsonObject jsonObject = new JsonParser().parse(sb.toString()).getAsJsonObject();

        JsonElement persons = jsonObject.get("persons");
        JsonElement firestations = jsonObject.get("firestations");
        JsonElement medicalrecords = jsonObject.get("medicalrecords");

        JsonArray personnsArray = persons.getAsJsonArray();
        for (JsonElement per:personnsArray) {
            Gson gson = new Gson();
            Person person = gson.fromJson(per,Person.class);
            personService.PersonSave(person);
            System.out.println(person.toString());
        }
    }
}