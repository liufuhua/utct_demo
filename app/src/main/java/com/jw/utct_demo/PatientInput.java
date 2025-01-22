package com.jw.utct_demo;

import org.hl7.fhir.r4.model.Enumerations;

public class PatientInput {
    private final String firstName;
    private final String lastName;
    private final String birthDate;
    private final Enumerations.AdministrativeGender gender;
    private final boolean isActive;

    public PatientInput(
            String firstName,
            String lastName,
            String birthDate,
            Enumerations.AdministrativeGender gender,
            boolean isActive
    ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.isActive = isActive;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public Enumerations.AdministrativeGender getGender() {
        return gender;
    }

    public boolean isActive() {
        return isActive;
    }
}
