package com.jw.utct_demo;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.android.fhir.FhirEngine;
import com.google.android.fhir.FhirEngineProvider;
import com.jw.utct_demo.OperationType;
import com.jw.utct_demo.PatientMapper;
import com.jw.utct_demo.PatientUiState;

import org.hl7.fhir.r4.model.Enumerations;
import org.hl7.fhir.r4.model.Patient;
import org.hl7.fhir.r4.model.Resource;
import org.hl7.fhir.r4.model.ResourceType;

import java.util.List;
import java.util.Locale;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.flow.MutableSharedFlow;
import kotlinx.coroutines.flow.SharedFlow;

public class CrudOperationViewModel extends AndroidViewModel {
    private final FhirEngine fhirEngine;
    private final MutableLiveData<PatientUiState> _patientUiState = new MutableLiveData<>();
    private String currentPatientLogicalId = null;

    // 使用 ExecutorService 处理后台任务
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public CrudOperationViewModel(@NonNull Application application) {
        super(application);
        fhirEngine = FhirEngineProvider.INSTANCE.getInstance(application);
    }

    public MutableLiveData<PatientUiState> getPatientUiState() {
        return _patientUiState;
    }

    public String getCurrentPatientLogicalId() {
        return currentPatientLogicalId;
    }

    public void readPatientById() {

        if (currentPatientLogicalId == null) {
            return;
        }

        //在后台线程中执行读取操作
        executorService.execute(() -> {
            try {
                Continuation<Object> continuation = new Continuation<Object>() {
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE; // 使用默认的 CoroutineContext
                    }

                    @Override
                    public void resumeWith(@NonNull Object result) {
                        if (result instanceof Patient) {
                            Patient patient = (Patient) result;
                            PatientUiState uiState = PatientMapper.toPatientUiState(patient, OperationType.READ);
                            _patientUiState.postValue(uiState);
                        } else if (result instanceof Throwable) {
                            ((Throwable) result).printStackTrace();
                        }
                    }
                };
                // 获取患者数据
                Patient patient = (Patient) fhirEngine.get(ResourceType.Patient,currentPatientLogicalId,continuation);
                // 转换为 UI 状态并更新 LiveData
                PatientUiState uiState = PatientMapper.toPatientUiState(patient, OperationType.READ);
                _patientUiState.postValue(uiState); // 使用 postValue 更新 LiveData
            } catch (Exception e) {
                e.printStackTrace();
                // 可以在这里处理错误，例如更新 UI 显示错误信息
            }
        });
    }

    public void createPatient(
            String patientId,
            String firstName,
            String lastName,
            String birthDate,
            Enumerations.AdministrativeGender gender,
            boolean isActive
    ) {
        currentPatientLogicalId = patientId;
        if (currentPatientLogicalId == null) {
            return;
        }

        executorService.execute(()->{
            Patient patient = PatientCreationHelper.createPatient(patientId, firstName, lastName, birthDate, gender,"123456","beijing","chiina",isActive);
            Continuation<Object> continuation = new Continuation<Object>() {
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object result) {
                    if (result instanceof List) {
                        List<String> ids = (List<String>) result;
                        if (!ids.isEmpty()) {
                            System.out.println("Created patient with ID: " + ids.get(0));
                        }
                    } else if (result instanceof Throwable) {
                        ((Throwable) result).printStackTrace();
                    }
                }
            };
            fhirEngine.create(new Resource[]{patient},continuation);
        });
    }

    public void updatePatient(
            String firstName,
            String lastName,
            String birthDate,
            Enumerations.AdministrativeGender gender,
            boolean isActive
    ) {
        if (currentPatientLogicalId == null) {
            return;
        }
        executorService.execute(()->{
            Patient patient = PatientCreationHelper.createPatient(currentPatientLogicalId, firstName, lastName, birthDate, gender,"123456","beijing","china",isActive);
            Continuation<Object> continuation = new Continuation<Object>() {
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object result) {
                    if (result instanceof List) {
                        List<String> ids = (List<String>) result;
                        if (!ids.isEmpty()) {
                            System.out.println("Created patient with ID: " + ids.get(0));
                        }
                    } else if (result instanceof Throwable) {
                        ((Throwable) result).printStackTrace();
                    }
                }
            };
            fhirEngine.update(new Resource[]{patient},continuation);
        });

//        viewModelScope.launch(() -> {
//            if (currentPatientLogicalId != null) {
//                Patient patient = PatientCreationHelper.createPatient(
//                        currentPatientLogicalId, firstName, lastName, birthDate, gender, isActive
//                );
//                fhirEngine.update(patient);
//                _patientUiState.emit(PatientMapper.toPatientUiState(patient, OperationType.UPDATE));
//            }
//        });
    }

    public void deletePatient() {
        if (currentPatientLogicalId == null) {
            return;
        }
        executorService.execute(()->{
            Continuation<Object> continuation = new Continuation<Object>() {
                @Override
                public CoroutineContext getContext() {
                    return EmptyCoroutineContext.INSTANCE;
                }

                @Override
                public void resumeWith(@NonNull Object result) {
                    if (result instanceof List) {
                        List<String> ids = (List<String>) result;
                        if (!ids.isEmpty()) {
                            System.out.println("Created patient with ID: " + ids.get(0));
                        }
                    } else if (result instanceof Throwable) {
                        ((Throwable) result).printStackTrace();
                    }
                }
            };
            fhirEngine.delete(ResourceType.Patient,currentPatientLogicalId,continuation);
            currentPatientLogicalId = null;
        });
//        viewModelScope.launch(() -> {
//            if (currentPatientLogicalId != null) {
//                fhirEngine.delete(Patient.class, currentPatientLogicalId);
//                currentPatientLogicalId = null;
//                _patientUiState.emit(PatientMapper.toPatientUiState(new Patient(), OperationType.DELETE));
//            }
//        });
    }

    public boolean isBirthDateValid(String dateString) {
        return PatientCreationHelper.isBirthdateParsed(dateString);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        // 关闭 ExecutorService，防止内存泄漏
        executorService.shutdown();
    }
}