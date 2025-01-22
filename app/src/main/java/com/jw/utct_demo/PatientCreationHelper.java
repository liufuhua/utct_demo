package com.jw.utct_demo;


import org.hl7.fhir.r4.model.Address;
import org.hl7.fhir.r4.model.ContactPoint;
import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.HumanName;
import org.hl7.fhir.r4.model.Patient;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class PatientCreationHelper {

    public static Patient createPatient(
            String patientId,
            String firstName,
            String lastName,
            String birthDate,
            Enumerations.AdministrativeGender gender,
            String phoneNumber,
            String city,
            String country,
            boolean isActive
    ) {
        Patient patient = new Patient();

        // Set UUID as patient ID
        patient.setId(patientId);

        // Set patient name
        HumanName name = new HumanName().setFamily(lastName).addGiven(firstName);
        patient.addName(name);

        // Set patient birth date
        if (birthDate != null && !birthDate.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                patient.setBirthDate(dateFormat.parse(birthDate));
            } catch (ParseException e) {
                e.printStackTrace(); // Log the error if necessary
            }
        }

        // Set patient gender
        patient.setGender(gender);

        // Set patient telecom (phone)
        if (phoneNumber != null) {
            ContactPoint telecom = new ContactPoint();
            telecom.setSystem(ContactPoint.ContactPointSystem.PHONE);
            telecom.setValue(phoneNumber);
            patient.addTelecom(telecom);
        }

        // Set patient address
        Address address = new Address();
        address.setCity(city);
        address.setCountry(country);
        patient.addAddress(address);

        // Set active status
        patient.setActive(isActive);

        return patient;
    }

    public static List<Patient> createSamplePatients() {
        List<Patient> patients = new ArrayList<>();

        // Add sample patients
        patients.add(createPatient(createPatientId(), "John", "Doe", "1990-01-01", Enumerations.AdministrativeGender.MALE, "1234567890", "New York", "USA", true));
        patients.add(createPatient(createPatientId(), "Jane", "Smith", "1985-05-15", Enumerations.AdministrativeGender.FEMALE, "0987654321", "Los Angeles", "USA", true));
        patients.add(createPatient(createPatientId(), "Emily", "Johnson", "1978-11-12", Enumerations.AdministrativeGender.FEMALE, "2345678901", "Chicago", "USA", true));
        patients.add(createPatient(createPatientId(), "Michael", "Brown", "1982-04-07", Enumerations.AdministrativeGender.MALE, "3456789012", "Houston", "USA", false));
        patients.add(createPatient(createPatientId(), "Sophia", "Davis", "1995-08-22", Enumerations.AdministrativeGender.FEMALE, "4567890123", "Phoenix", "USA", true));
        patients.add(createPatient(createPatientId(), "Liam", "Wilson", "2001-12-30", Enumerations.AdministrativeGender.MALE, "5678901234", "Dallas", "USA", true));
        patients.add(createPatient(createPatientId(), "Olivia", "Martinez", "1989-03-17", Enumerations.AdministrativeGender.FEMALE, "6789012345", "Miami", "USA", false));
        patients.add(createPatient(createPatientId(), "Noah", "Garcia", "1975-07-05", Enumerations.AdministrativeGender.MALE, "7890123456", "San Francisco", "USA", true));
        patients.add(createPatient(createPatientId(), "Ava", "Anderson", "1998-02-27", Enumerations.AdministrativeGender.FEMALE, "8901234567", "Seattle", "USA", true));
        patients.add(createPatient(createPatientId(), "Ethan", "Harris", "1993-09-10", Enumerations.AdministrativeGender.MALE, "9012345678", "Boston", "USA", true));

        return patients;
    }

    public static String createPatientId() {
        return UUID.randomUUID().toString();
    }

    public static boolean isBirthdateParsed(String birthdate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(birthdate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
}