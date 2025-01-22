package com.jw.utct_demo;

import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PatientMapper {
    public static PatientUiState toPatientUiState(Patient patient, OperationType operationType) {
        String patientId = patient.getId();
        String firstName = patient.getName() != null && !patient.getName().isEmpty()
                ? patient.getName().get(0).getGiven().get(0).getValue()
                : "";
        String lastName = patient.getName() != null && !patient.getName().isEmpty()
                ? patient.getName().get(0).getFamily()
                : null;
        String birthDate = patient.hasBirthDateElement()
                ? new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(patient.getBirthDate())
                : null;
        Enumerations.AdministrativeGender gender = patient.getGender();
        boolean isActive = patient.getActive();

        return new PatientUiState(patientId, firstName, lastName, birthDate, gender, isActive, operationType);
    }
}
