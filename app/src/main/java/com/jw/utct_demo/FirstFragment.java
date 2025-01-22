package com.jw.utct_demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;


import org.hl7.fhir.r4.model.Enumerations;

import java.util.Arrays;
import java.util.List;

public class FirstFragment extends Fragment {
    private CrudOperationViewModel crudOperationViewModel;

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_first, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        crudOperationViewModel = new ViewModelProvider(this).get(CrudOperationViewModel.class);

        setUpActionBar();
        setHasOptionsMenu(true);
        setupUiOnScreenLaunch();

        // Lifecycle-aware coroutine
        getViewLifecycleOwner().getLifecycle().addObserver(new DefaultLifecycleObserver() {
            @Override
            public void onStart(@NonNull LifecycleOwner owner) {
                crudOperationViewModel.getPatientUiState().observe(getViewLifecycleOwner(), patientUiState -> {
                    if (patientUiState != null) {
                        switch (patientUiState.getOperationType()) {
                            case CREATE:
                                Toast.makeText(requireContext(), "Patient is saved", Toast.LENGTH_SHORT).show();
                                break;
                            case READ:
                                displayPatientDetails(patientUiState);
                                break;
                            case UPDATE:
                                Toast.makeText(requireContext(), "Patient is updated", Toast.LENGTH_SHORT).show();
                                break;
                            case DELETE:
                                clearUiFieldValues();
                                configureFieldsForOperation(OperationType.DELETE);
                                Toast.makeText(requireContext(), "Patient is deleted", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
            }
        });
    }

    private void displayPatientDetails(PatientUiState patientUiState) {
        setPatientIdUiFieldText(patientUiState.getPatientId());
        EditText firstNameField = requireView().findViewById(R.id.etFirstName);
        firstNameField.setText(patientUiState.getFirstName());
        EditText lastNameField = requireView().findViewById(R.id.etLastName);
        lastNameField.setText(patientUiState.getLastName() == null ? "" : patientUiState.getLastName());
        EditText birthDateField = requireView().findViewById(R.id.etBirthDate);
        birthDateField.setText(patientUiState.getBirthDate() == null ? "" : patientUiState.getBirthDate());

        RadioGroup genderGroup = requireView().findViewById(R.id.radioGroupGender);
        genderGroup.clearCheck();
        Integer genderButtonId = null;
        if (patientUiState.getGender() != null) {
            switch (patientUiState.getGender()) {
                case MALE:
                    genderButtonId = R.id.rbMale;
                    break;
                case FEMALE:
                    genderButtonId = R.id.rbFemale;
                    break;
                case OTHER:
                    genderButtonId = R.id.rbOther;
                    break;
            }
        }
        if (genderButtonId != null) {
            genderGroup.check(genderButtonId);
        }

        CheckBox activeCheckBox = requireView().findViewById(R.id.checkBoxActive);
        activeCheckBox.setChecked(patientUiState.isActive());
    }

    private void setUpActionBar(){
        if (requireActivity() instanceof AppCompatActivity){
            AppCompatActivity activity = (AppCompatActivity) requireActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setTitle(getString(R.string.crud_operations));
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
    }

    private void setupUiOnScreenLaunch(){
        setupTabLayoutChangeListener();
        selectTab(TAB_CREATE);
        setupUiForCrudOperation(OperationType.CREATE);

        Button submitButton = requireView().findViewById(R.id.btnSubmit);
        submitButton.setOnClickListener(v -> {
            TabLayout tabLayout = requireView().findViewById(R.id.tabLayoutCrud);
            int currentTabPosition = tabLayout.getSelectedTabPosition();
            OperationType currentOperationType = getOperationTypeByTabPosition(currentTabPosition);

            switch (currentOperationType) {
                case CREATE:
                    createPatient();
                    break;
                case READ:
                    if (!isPatientCreationRequired()) {
                        crudOperationViewModel.readPatientById();
                    }
                    break;
                case UPDATE:
                    updatePatient();
                    break;
                case DELETE:
                    deletePatient();
                    break;
            }
        });
    }

    private void updatePatient() {
        if (isPatientCreationRequired()) return;
        PatientInput input = getPatientInput();
        if (input != null) {
            crudOperationViewModel.updatePatient(
                    input.getFirstName(),
                    input.getLastName(),
                    input.getBirthDate(),
                    input.getGender(),
                    input.isActive()
            );
        }
    }

    private void deletePatient() {
        if (isPatientCreationRequired()) return;
        String patientId = crudOperationViewModel.getCurrentPatientLogicalId();
        if (patientId != null) {
            crudOperationViewModel.deletePatient();
        }
    }

    private boolean isPatientCreationRequired() {
        if (crudOperationViewModel.getCurrentPatientLogicalId() == null || crudOperationViewModel.getCurrentPatientLogicalId().isEmpty()) {
            Toast.makeText(requireContext(), "Please create a patient first.", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private void createPatient() {
        PatientInput input = getPatientInput();
        if (input != null) {
            crudOperationViewModel.createPatient(
                    ((EditText) requireView().findViewById(R.id.etId)).getText().toString(),
                    input.getFirstName(),
                    input.getLastName(),
                    input.getBirthDate(),
                    input.getGender(),
                    input.isActive()
            );
        }
    }

    private PatientInput getPatientInput() {
        String firstName = ((EditText) requireView().findViewById(R.id.etFirstName)).getText().toString().trim();
        if (firstName.isEmpty()) {
            Toast.makeText(requireContext(), "First name is required.", Toast.LENGTH_SHORT).show();
            return null;
        }
        String lastName = ((EditText) requireView().findViewById(R.id.etLastName)).getText().toString().trim();
        String birthDate = ((EditText) requireView().findViewById(R.id.etBirthDate)).getText().toString().trim();
        if (!birthDate.isEmpty() && !crudOperationViewModel.isBirthDateValid(birthDate)) {
            Toast.makeText(requireContext(), "Please enter a valid birth date.", Toast.LENGTH_SHORT).show();
            return null;
        }
        int selectedGenderId = ((RadioGroup) requireView().findViewById(R.id.radioGroupGender)).getCheckedRadioButtonId();
        Enumerations.AdministrativeGender gender = null;
        if (selectedGenderId == R.id.rbMale) {
            gender = Enumerations.AdministrativeGender.MALE;
        } else if (selectedGenderId == R.id.rbFemale) {
            gender = Enumerations.AdministrativeGender.FEMALE;
        } else if (selectedGenderId == R.id.rbOther) {
            gender = Enumerations.AdministrativeGender.OTHER;
        }
        boolean isActive = ((CheckBox) requireView().findViewById(R.id.checkBoxActive)).isChecked();
        return new PatientInput(firstName, lastName, birthDate, gender, isActive);
    }
    private void setupTabLayoutChangeListener(){
        TabLayout tabLayout = requireView().findViewById(R.id.tabLayoutCrud);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                OperationType operationType = getOperationTypeByTabPosition(position);
                setupUiForCrudOperation(operationType);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private OperationType getOperationTypeByTabPosition(int tabPosition) {
        switch (tabPosition) {
            case TAB_CREATE:
                return OperationType.CREATE;
            case TAB_READ:
                return OperationType.READ;
            case TAB_UPDATE:
                return OperationType.UPDATE;
            case TAB_DELETE:
                return OperationType.DELETE;
            default:
                throw new IllegalArgumentException("Invalid tab position");
        }
    }

    private void setupUiForCrudOperation(OperationType operationType) {
        switch (operationType) {
            case CREATE:
                clearUiFieldValues();
                setPatientIdUiFieldText(PatientCreationHelper.createPatientId());
                updateSubmitButtonText(getString(R.string.create));
                break;
            case READ:
                clearUiFieldValues();
                setPatientIdUiFieldText(crudOperationViewModel.getCurrentPatientLogicalId());
                updateSubmitButtonText(getString(R.string.read));
                break;
            case UPDATE:
                updateSubmitButtonText(getString(R.string.update));
                crudOperationViewModel.readPatientById();
                break;
            case DELETE:
                updateSubmitButtonText(getString(R.string.delete));
                crudOperationViewModel.readPatientById();
                break;
        }
        configureFieldsForOperation(operationType);
    }

    private void clearUiFieldValues() {
        List<Integer> editTexts = Arrays.asList(R.id.etId, R.id.etFirstName, R.id.etLastName, R.id.etBirthDate);
        for (int editTextId : editTexts) {
            EditText editText = requireView().findViewById(editTextId);
            editText.setText("");
            editText.clearFocus();
        }
        RadioGroup genderGroup = requireView().findViewById(R.id.radioGroupGender);
        genderGroup.clearCheck();
        CheckBox activeCheckBox = requireView().findViewById(R.id.checkBoxActive);
        activeCheckBox.setChecked(false);
    }

    private void setPatientIdUiFieldText(String patientId) {
        EditText idField = requireView().findViewById(R.id.etId);
        idField.setText(patientId);
    }

    private void selectTab(int position) {
        TabLayout tabLayout = requireView().findViewById(R.id.tabLayoutCrud);
        TabLayout.Tab tab = tabLayout.getTabAt(position);
        if (tab != null) {
            tab.select();
        }
    }

    private void updateSubmitButtonText(String text) {
        Button submitButton = requireView().findViewById(R.id.btnSubmit);
        submitButton.setText(text);
    }

    private void configureFieldsForOperation(OperationType operationType) {
        boolean isEditable = operationType == OperationType.CREATE || operationType == OperationType.UPDATE;
        List<Integer> fields = Arrays.asList(
                R.id.etFirstName, R.id.etLastName, R.id.etBirthDate,
                R.id.radioGroupGender, R.id.rbMale, R.id.rbFemale,
                R.id.rbOther, R.id.checkBoxActive
        );
        for (int viewId : fields) {
            View view = requireView().findViewById(viewId);
            view.setEnabled(isEditable);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private static final int TAB_CREATE = 0;
    private static final int TAB_READ = 1;
    private static final int TAB_UPDATE = 2;
    private static final int TAB_DELETE = 3;
}