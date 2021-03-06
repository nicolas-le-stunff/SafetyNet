package com.SafetyNet.Safety.util;

import com.SafetyNet.Safety.model.FireStation;
import com.SafetyNet.Safety.model.Person;
import com.SafetyNet.Safety.service.FireStationService;
import com.SafetyNet.Safety.service.PersonService;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.mockito.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ImportDataTest {

    @Mock
    private PersonService mockPersonService;

    @Mock
    private FireStationService mockFireStationService;

    @Spy
    @InjectMocks
    private ImportData importDataTest;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Chargement des données valides")
    void testLoad()   {
        // Setup
        StringBuilder stringBuilder = new StringBuilder("{    \"persons\": [        { \"firstName\":\"John\", \"lastName\":\"Boyd\", \"address\":\"1509 Culver St\", \"city\":\"Culver\", \"zip\":\"97451\", \"phone\":\"841-874-6512\", \"email\":\"jaboyd@email.com\" }\t],              \"firestations\": [\t{ \"address\":\"1509 Culver St\", \"station\":\"3\" },{ \"address\":\"1509k Culver St\", \"station\":\"3\" },{ \"address\":\"1509k Culver St\", \"station\":\"2\" }\t],            \"medicalrecords\": [        { \"firstName\":\"John\", \"lastName\":\"Boyd\", \"birthdate\":\"03/06/1984\", \"medications\":[\"aznol:350mg\", \"hydrapermazol:100mg\"], \"allergies\":[\"nillacilan\"] }                    ] }");
        JsonObject jsonObject = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();

        //Mock
        when(importDataTest.loadFile()).thenReturn(jsonObject);
        when(mockPersonService.personSave(any(Person.class))).thenReturn(true);
        when(mockFireStationService.save(any(FireStation.class))).thenReturn(true);
        when(mockPersonService.findByFirstNameLastName("John","Boyd")).thenReturn(new Person());
        // Run the test
        Boolean init = importDataTest.load();

        // Verify the results
        assertThat(init.booleanValue()).isTrue();
    }

    @Test
    @DisplayName("Chargement de Firestation unique")
    void testLoadFirestation()   {
        // Setup
        StringBuilder stringBuilder = new StringBuilder("{            \"firestations\": [\t{ \"address\":\"1509 Culver St\", \"station\":\"3\" }\t]           }");
        JsonObject jsonObject = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();

        //Mock
        when(importDataTest.loadFile()).thenReturn(jsonObject);
        when(mockPersonService.personSave(any(Person.class))).thenReturn(true);
        when(mockFireStationService.save(any(FireStation.class))).thenReturn(true);
        when(mockPersonService.findByFirstNameLastName("John","Boyd")).thenReturn(new Person());
        // Run the test
        Boolean init = importDataTest.load();

        // Verify the results
        assertThat(init.booleanValue()).isTrue();
    }

    @Test
    @DisplayName("Récuperation medicalRecords vide")
    void testLoadMedicalrecordsVide()   {
        // Setup
        StringBuilder stringBuilder = new StringBuilder("{                   \"medicalrecords\": [     ] }");
        JsonObject jsonObject = new JsonParser().parse(stringBuilder.toString()).getAsJsonObject();

        //Mock
        when(importDataTest.loadFile()).thenReturn(jsonObject);
        when(mockPersonService.personSave(any(Person.class))).thenReturn(true);
        when(mockFireStationService.save(any(FireStation.class))).thenReturn(true);
        when(mockPersonService.findByFirstNameLastName("John","Boyd")).thenReturn(new Person());
        // Run the test
        Boolean init = importDataTest.load();

        // Verify the results
        assertThat(init.booleanValue()).isTrue();
    }


    @Test
    @DisplayName("Chargement en echec")
    void testLoad_ThrowsIOException() {
        //Mock
        when(importDataTest.loadFile()).thenReturn(null);

        //run the test
        Boolean init = importDataTest.load();

        //Verify the results
        assertThat(init.booleanValue()).isFalse();
    }
}
