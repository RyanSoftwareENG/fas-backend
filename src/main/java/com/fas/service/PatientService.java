package com.fas.service;

import com.fas.entity.Allergy;
import com.fas.entity.ChronicDisease;
import com.fas.entity.Client;
import com.fas.entity.PatientAllergy;
import com.fas.entity.PatientChronicDisease;
import com.fas.repository.AllergyRepository;
import com.fas.repository.ChronicDiseaseRepository;
import com.fas.repository.ClientRepository;
import com.fas.repository.PatientAllergyRepository;
import com.fas.repository.PatientChronicDiseaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PatientService {

    // =====================================================
    // Repositories
    // =====================================================

    private final AllergyRepository allergyRepository;
    private final ChronicDiseaseRepository chronicDiseaseRepository;
    private final ClientRepository clientRepository;
    private final PatientAllergyRepository patientAllergyRepository;
    private final PatientChronicDiseaseRepository patientChronicDiseaseRepository;

    // =====================================================
    // Constructor
    // =====================================================

    public PatientService(
            AllergyRepository allergyRepository,
            ChronicDiseaseRepository chronicDiseaseRepository,
            ClientRepository clientRepository,
            PatientAllergyRepository patientAllergyRepository,
            PatientChronicDiseaseRepository patientChronicDiseaseRepository) {

        this.allergyRepository = allergyRepository;
        this.chronicDiseaseRepository = chronicDiseaseRepository;
        this.clientRepository = clientRepository;
        this.patientAllergyRepository = patientAllergyRepository;
        this.patientChronicDiseaseRepository =
                patientChronicDiseaseRepository;
    }

    // =====================================================
    // البحث عن المرض بالـ ID أو الاسم
    // وإنشاؤه إذا لم يكن موجوداً
    // =====================================================

    public ChronicDisease getOrCreateDisease(
            ChronicDisease input) {

        if (input == null) {
            throw new RuntimeException(
                    "بيانات المرض غير موجودة"
            );
        }

        // -------------------------------------------------
        // الحالة 1:
        // يوجد ID → نستخدم المرض الموجود
        // -------------------------------------------------

        if (input.getId() != null) {

            return chronicDiseaseRepository
                    .findById(input.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "المرض غير موجود بالـ ID: "
                                            + input.getId()
                            )
                    );
        }

        // -------------------------------------------------
        // الحالة 2:
        // لا يوجد ID → يجب أن يوجد الاسم
        // -------------------------------------------------

        if (input.getDiseaseName() == null ||
                input.getDiseaseName().trim().isEmpty()) {

            throw new RuntimeException(
                    "اسم المرض مطلوب"
            );
        }

        String name =
                input.getDiseaseName().trim();

        // -------------------------------------------------
        // البحث بالاسم
        // -------------------------------------------------

        return chronicDiseaseRepository
                .findByDiseaseNameIgnoreCase(name)
                .orElseGet(() -> {

                    // -------------------------------------------------
                    // غير موجود → إنشاء مرض جديد
                    // -------------------------------------------------

                    ChronicDisease newDisease =
                            new ChronicDisease();

                    newDisease.setDiseaseName(name);

                    // save() تحفظ السجل
                    // Hibernate يحصل على الـ ID الجديد
                    return chronicDiseaseRepository
                            .save(newDisease);
                });
    }

    // =====================================================
    // البحث عن الحساسية بالـ ID أو الاسم
    // وإنشاؤها إذا لم تكن موجودة
    // =====================================================

    public Allergy getOrCreateAllergy(
            Allergy input) {

        if (input == null) {
            throw new RuntimeException(
                    "بيانات الحساسية غير موجودة"
            );
        }

        // -------------------------------------------------
        // الحالة 1:
        // يوجد ID → نستخدم الحساسية الموجودة
        // -------------------------------------------------

        if (input.getId() != null) {

            return allergyRepository
                    .findById(input.getId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "الحساسية غير موجودة بالـ ID: "
                                            + input.getId()
                            )
                    );
        }

        // -------------------------------------------------
        // الحالة 2:
        // لا يوجد ID → يجب أن يوجد الاسم
        // -------------------------------------------------

        if (input.getAllergyName() == null ||
                input.getAllergyName().trim().isEmpty()) {

            throw new RuntimeException(
                    "اسم الحساسية مطلوب"
            );
        }

        String name =
                input.getAllergyName().trim();

        // -------------------------------------------------
        // البحث بالاسم
        // -------------------------------------------------

        return allergyRepository
                .findByAllergyNameIgnoreCase(name)
                .orElseGet(() -> {

                    // -------------------------------------------------
                    // غير موجود → إنشاء حساسية جديدة
                    // -------------------------------------------------

                    Allergy newAllergy =
                            new Allergy();

                    newAllergy.setAllergyName(name);

                    // الحصول على الـ ID الجديد
                    return allergyRepository
                            .save(newAllergy);
                });
    }

    // =====================================================
    // حفظ الأمراض والحساسيات وربطها بالعميل
    // =====================================================

    @Transactional
    public void savePatientHealthInfo(Client patient) {

        // =====================================================
        // 1. التحقق من بيانات العميل
        // =====================================================

        if (patient == null) {
            throw new RuntimeException(
                    "بيانات العميل غير موجودة"
            );
        }

        if (patient.getClientID() == null) {
            throw new RuntimeException(
                    "Client ID غير موجود"
            );
        }

        // =====================================================
        // 2. جلب العميل الحقيقي من قاعدة البيانات
        // =====================================================

        Long clientId = patient.getClientID();

        Client client =
                clientRepository.findById(clientId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "العميل غير موجود بالـ ID: "
                                                + clientId
                                )
                        );

        // =====================================================
        // 3. حذف العلاقات القديمة
        // =====================================================

        patientChronicDiseaseRepository
                .deleteByClient_ClientID(clientId);

        patientAllergyRepository
                .deleteByClient_ClientID(clientId);

        // =====================================================
        // 4. حفظ الأمراض المزمنة
        // =====================================================

        List<PatientChronicDisease> diseases =
                patient.getChronicDiseases();

        if (diseases != null && !diseases.isEmpty()) {

            for (PatientChronicDisease inputDisease : diseases) {

                if (inputDisease == null) {
                    continue;
                }

                // -------------------------------------------------
                // الحصول على المرض القادم من JSON
                // -------------------------------------------------

                ChronicDisease inputChronicDisease =
                        inputDisease.getChronicDisease();

                if (inputChronicDisease == null) {

                    throw new RuntimeException(
                            "بيانات المرض غير موجودة للعميل: "
                                    + clientId
                    );
                }

                // -------------------------------------------------
                // البحث بالـID أو الاسم أو إنشاء المرض
                // -------------------------------------------------

                ChronicDisease disease =
                        getOrCreateDisease(
                                inputChronicDisease
                        );

                // -------------------------------------------------
                // التأكد من الحصول على ID
                // -------------------------------------------------

                if (disease.getId() == null) {

                    throw new RuntimeException(
                            "فشل الحصول على ID للمرض: "
                                    + disease.getDiseaseName()
                    );
                }

                // -------------------------------------------------
                // إنشاء العلاقة بين العميل والمرض
                // -------------------------------------------------

                PatientChronicDisease relation =
                        new PatientChronicDisease(
                                client,
                                disease,

                                inputDisease.getStatus() != null
                                        ? inputDisease.getStatus()
                                        : "نشط",

                                inputDisease.getSeverity() != null
                                        ? inputDisease.getSeverity()
                                        : "متوسطة",

                                inputDisease.getNotes(),

                                inputDisease.getContraindicated()
                        );

                // -------------------------------------------------
                // حفظ العلاقة
                // -------------------------------------------------

                patientChronicDiseaseRepository
                        .save(relation);
            }
        }

        // =====================================================
        // 5. حفظ الحساسيات
        // =====================================================

        List<PatientAllergy> allergies =
                patient.getAllergies();

        if (allergies != null && !allergies.isEmpty()) {

            for (PatientAllergy inputAllergy : allergies) {

                if (inputAllergy == null) {
                    continue;
                }

                // -------------------------------------------------
                // الحصول على الحساسية القادمة من JSON
                // -------------------------------------------------

                Allergy inputAllergyData =
                        inputAllergy.getAllergy();

                if (inputAllergyData == null) {

                    throw new RuntimeException(
                            "بيانات الحساسية غير موجودة للعميل: "
                                    + clientId
                    );
                }

                // -------------------------------------------------
                // البحث بالـID أو الاسم أو إنشاء الحساسية
                // -------------------------------------------------

                Allergy allergy =
                        getOrCreateAllergy(
                                inputAllergyData
                        );

                // -------------------------------------------------
                // التأكد من الحصول على ID
                // -------------------------------------------------

                if (allergy.getId() == null) {

                    throw new RuntimeException(
                            "فشل الحصول على ID للحساسية: "
                                    + allergy.getAllergyName()
                    );
                }

                // -------------------------------------------------
                // إنشاء العلاقة بين العميل والحساسية
                // -------------------------------------------------

                PatientAllergy relation =
                        new PatientAllergy(
                                client,
                                allergy,

                                inputAllergy.getStatus() != null
                                        ? inputAllergy.getStatus()
                                        : "نشط",

                                inputAllergy.getSeverity() != null
                                        ? inputAllergy.getSeverity()
                                        : "متوسطة",

                                inputAllergy.getNotes(),

                                inputAllergy.getContraindicated()
                        );

                // -------------------------------------------------
                // حفظ العلاقة
                // -------------------------------------------------

                patientAllergyRepository
                        .save(relation);
            }
        }
    }

    // =====================================================
    // Allergies
    // =====================================================

    public List<Allergy> getAllAllergies() {

        return allergyRepository.findAll();
    }

    public Allergy updateAllergy(
            Long id,
            String newName) {

        Allergy allergy =
                allergyRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "الحساسية غير موجودة بالرقم: "
                                                + id
                                )
                        );

        if (newName == null ||
                newName.trim().isEmpty()) {

            throw new RuntimeException(
                    "اسم الحساسية لا يمكن أن يكون فارغاً"
            );
        }

        allergy.setAllergyName(
                newName.trim()
        );

        return allergyRepository.save(allergy);
    }

    // =====================================================
    // Chronic Diseases
    // =====================================================

    public List<ChronicDisease> getAllChronicDiseases() {

        return chronicDiseaseRepository.findAll();
    }

    public ChronicDisease updateDisease(
            Long id,
            String newName) {

        ChronicDisease disease =
                chronicDiseaseRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "المرض المزمن غير موجود بالرقم: "
                                                + id
                                )
                        );

        if (newName == null ||
                newName.trim().isEmpty()) {

            throw new RuntimeException(
                    "اسم المرض لا يمكن أن يكون فارغاً"
            );
        }

        disease.setDiseaseName(
                newName.trim()
        );

        return chronicDiseaseRepository.save(disease);
    }
}