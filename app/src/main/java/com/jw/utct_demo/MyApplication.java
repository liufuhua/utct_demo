package com.jw.utct_demo;

import android.app.Application;

import com.google.android.fhir.DatabaseErrorStrategy;
import com.google.android.fhir.FhirEngine;
import com.google.android.fhir.FhirEngineConfiguration;
import com.google.android.fhir.FhirEngineProvider;

import org.hl7.fhir.r4.model.Patient;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FhirEngineConfiguration config = new FhirEngineConfiguration(
                true,
                DatabaseErrorStrategy.RECREATE_AT_OPEN,
                null,     // serverConfiguration
                false,    // testMode
                null      // customSearchParameters
        );

        // 2. 初始化 FhirEngineProvider
        FhirEngineProvider.INSTANCE.init(config);

        // 3. 获取 FhirEngine 实例
        FhirEngine engine = FhirEngineProvider.INSTANCE.getInstance(this);
    }
}
