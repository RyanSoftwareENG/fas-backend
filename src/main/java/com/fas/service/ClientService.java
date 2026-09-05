package com.fas.service;

import com.fas.dto.ClientListDTO;
import com.fas.entity.*;
import com.fas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final AllergyRepository allergyRepository;
    private final ChronicDiseaseRepository chronicDiseaseRepository;
    private final SessionRepository sessionRepository;
    private final HealthDataRepository healthDataRepository;
    private final LifeStyleInformationRepository lifeStyleInformationRepository;

    // جديد
    private final BodyDataRepository bodyDataRepository;
    private final NutritionPlanRepository nutritionPlanRepository;
    private final ExaminationRepository examinationRepository;
    private final PatientAllergyRepository patientAllergyRepository;
    private final PatientChronicDiseaseRepository patientChronicDiseaseRepository;
    private final PlanFoodItemRepository planFoodItemRepository;



    public ClientService(
            ClientRepository clientRepository,
            AllergyRepository allergyRepository,
            ChronicDiseaseRepository chronicDiseaseRepository,
            PatientAllergyRepository patientAllergyRepository,
            PatientChronicDiseaseRepository patientChronicDiseaseRepository,
            SessionRepository sessionRepository,
            HealthDataRepository healthDataRepository,
            LifeStyleInformationRepository lifeStyleInformationRepository,
            BodyDataRepository bodyDataRepository,
            NutritionPlanRepository nutritionPlanRepository,
            PlanFoodItemRepository planFoodItemRepository,
            ExaminationRepository examinationRepository) {

        this.clientRepository = clientRepository;
        this.allergyRepository = allergyRepository;
        this.chronicDiseaseRepository = chronicDiseaseRepository;

        this.patientAllergyRepository = patientAllergyRepository;
        this.patientChronicDiseaseRepository = patientChronicDiseaseRepository;

        this.sessionRepository = sessionRepository;
        this.healthDataRepository = healthDataRepository;
        this.lifeStyleInformationRepository = lifeStyleInformationRepository;

        this.bodyDataRepository = bodyDataRepository;
        this.nutritionPlanRepository = nutritionPlanRepository;
        this.examinationRepository = examinationRepository;
        this.planFoodItemRepository = planFoodItemRepository;
    }


    // =========================================================
    // حفظ العميل
    // =========================================================

    @Transactional
    public Client saveClient(Client client) {

        // 1. فحص وربط الحساسية
        if (client.getAllergies() != null) {
            for (PatientAllergy pAllergy : client.getAllergies()) {
                Allergy currentAllergy = pAllergy.getAllergy();

                // البحث عن الحساسية، إذا لم تكن موجودة نحفظها أولاً
                Optional<Allergy> existingAllergy = allergyRepository.findByAllergyNameIgnoreCase(currentAllergy.getAllergyName());
                if (existingAllergy.isPresent()) {
                    pAllergy.setAllergy(existingAllergy.get());
                } else {
                    Allergy savedNewAllergy = allergyRepository.save(new Allergy(currentAllergy.getAllergyName()));
                    pAllergy.setAllergy(savedNewAllergy);
                }

                // ربط الحساسية بالعميل (مهم جداً قبل الحفظ)
                pAllergy.setClient(client);
            }
        }

        // 2. فحص وربط الأمراض المزمنة
        if (client.getChronicDiseases() != null) {
            for (PatientChronicDisease pDisease : client.getChronicDiseases()) {
                ChronicDisease currentDisease = pDisease.getChronicDisease();

                // البحث عن المرض، إذا لم يكن موجوداً نحفظه أولاً
                Optional<ChronicDisease> existingDisease = chronicDiseaseRepository.findByDiseaseNameIgnoreCase(currentDisease.getDiseaseName());
                if (existingDisease.isPresent()) {
                    pDisease.setChronicDisease(existingDisease.get());
                } else {
                    ChronicDisease savedNewDisease = chronicDiseaseRepository.save(new ChronicDisease(currentDisease.getDiseaseName()));
                    pDisease.setChronicDisease(savedNewDisease);
                }

                // ربط المرض بالعميل (مهم جداً قبل الحفظ)
                pDisease.setClient(client);
            }
        }

        // 3. ربط باقي البيانات بالعميل (لتجنب نفس الخطأ)
        if (client.getHealthData() != null) {
            client.getHealthData().setClient(client);
        }

        if (client.getLifeStyleInformation() != null) {
            client.getLifeStyleInformation().setClient(client);
        }

        if (client.getSessions() != null) {
            for (Session session : client.getSessions()) {
                session.setClient(client);

                if (session.getBodyData() != null) {
                    session.getBodyData().setSession(session);
                }
                if (session.getNutritionPlan() != null) {
                    session.getNutritionPlan().setSession(session);
                }
                if (session.getExaminations() != null) {
                    for (Examination exam : session.getExaminations()) {
                        exam.setSession(session);
                    }
                }
            }
        }

        // 4. الحفظ النهائي
        // الآن سيقوم Hibernate بحفظ العميل، وبفضل الـ Cascade سيقوم بحفظ كل شيء مرتبط به بنجاح
        return clientRepository.save(client);
    }

    // =========================================================
    // جلب جميع العملاء - البيانات الأساسية فقط
    // =========================================================

    @Transactional(readOnly = true)
    public List<ClientListDTO> getAllClients() {

        return clientRepository.findAllClientBasicData();
    }


    // =========================================================
    // جلب عميل واحد - البيانات الأساسية فقط
    // =========================================================

    @Transactional(readOnly = true)
    public Optional<Client> getClientById(Long id) {

        return clientRepository.findById(id);
    }


    // =========================================================
    // جلب العميل بالكامل
    // =========================================================

    @Transactional(readOnly = true)
    public Optional<Client> getFullClientDetails(Long id) {

        // -----------------------------------------------------
        // 1. جلب العميل الأساسي
        // -----------------------------------------------------

        Optional<Client> optionalClient =
                clientRepository.findById(id);

        if (optionalClient.isEmpty()) {
            return Optional.empty();
        }

        Client client = optionalClient.get();


        // -----------------------------------------------------
        // 2. جلب الحساسية
        // -----------------------------------------------------

        client.setAllergies(
                patientAllergyRepository.findByClientId(id)
        );


        // -----------------------------------------------------
        // 3. جلب الأمراض المزمنة
        // -----------------------------------------------------

        client.setChronicDiseases(
                patientChronicDiseaseRepository.findByClientId(id)
        );


        // -----------------------------------------------------
        // 4. جلب HealthData
        // -----------------------------------------------------

        client.setHealthData(
                healthDataRepository.findByClientId(id)
                        .orElse(null)
        );


        // -----------------------------------------------------
        // 5. جلب Lifestyle
        // -----------------------------------------------------

        client.setLifeStyleInformation(
                lifeStyleInformationRepository.findByClientId(id)
                        .orElse(null)
        );


        // -----------------------------------------------------
        // 6. جلب جلسات العميل
        // -----------------------------------------------------

        List<Session> sessions =
                sessionRepository.findByClientId(id);


        // -----------------------------------------------------
        // 7. جلب البيانات التابعة لكل جلسة
        // -----------------------------------------------------

        for (Session session : sessions) {

            Long sessionId = session.getId();


            // -------------------------------------------------
            // BodyData
            // -------------------------------------------------

            BodyData bodyData =
                    bodyDataRepository
                            .findBySessionId(sessionId)
                            .orElse(null);

            session.setBodyData(bodyData);


            // -------------------------------------------------
            // NutritionPlan
            // -------------------------------------------------

            NutritionPlan nutritionPlan =
                    nutritionPlanRepository
                            .findBySessionId(sessionId)
                            .orElse(null);

            System.out.println("================================");
            System.out.println("SESSION ID = " + sessionId);

            if (nutritionPlan == null) {
                System.out.println("❌ PLAN = NULL");
            } else {
                System.out.println("✅ PLAN FOUND");
                System.out.println("PLAN ID = " + nutritionPlan.getPlanId());
                System.out.println("TARGET = " + nutritionPlan.getTargetGoal());
            }

            session.setNutritionPlan(nutritionPlan);

            System.out.println("SESSION PLAN AFTER SET = "
                    + session.getNutritionPlan());

            System.out.println("================================");

            // =====================================================
            // 3. PlanFoodItems
            // =====================================================

            if (nutritionPlan != null) {

                List<PlanFoodItem> foods =
                        planFoodItemRepository
                                .findByNutritionPlanPlanId(
                                        nutritionPlan.getPlanId()
                                );

                nutritionPlan.setSelectedFoods(
                        foods != null
                                ? foods
                                : new ArrayList<>()
                );

                System.out.println(
                        "PLAN ID = "
                                + nutritionPlan.getPlanId()
                );

                System.out.println(
                        "PLAN FOOD COUNT = "
                                + nutritionPlan
                                .getSelectedFoods()
                                .size()
                );

                for (PlanFoodItem food :
                        nutritionPlan.getSelectedFoods()) {

                    if (food == null) {
                        continue;
                    }

                    System.out.println(
                            "FOOD ID = "
                                    + (
                                    food.getFoodItem() != null
                                            ? food.getFoodItem()
                                            .getFoodItemId()
                                            : null
                            )
                    );

                    System.out.println(
                            "FOOD NAME = "
                                    + (
                                    food.getFoodItem() != null
                                            ? food.getFoodItem()
                                            .getFoodName()
                                            : null
                            )
                    );

                    System.out.println(
                            "MEAL TYPE = "
                                    + food.getMealType()
                    );

                    System.out.println(
                            "QUANTITY = "
                                    + food.getQuantity()
                    );
                }
            }


            // -------------------------------------------------
            // Examinations
            // -------------------------------------------------

            List<Examination> examinations =
                    examinationRepository
                            .findBySessionId(sessionId);

            session.setExaminations(examinations);
        }


        // -----------------------------------------------------
        // 8. وضع الجلسات داخل العميل
        // -----------------------------------------------------

        client.setSessions(sessions);


        // -----------------------------------------------------
        // 9. إرجاع العميل بالكامل
        // -----------------------------------------------------

        return Optional.of(client);
    }


    // =========================================================
    // تحديث البيانات الأساسية للعميل
    // =========================================================

    public Client updateClient(
            Long id,
            Client updatedClient) {

        Client existingClient =
                clientRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "لم يتم العثور على العميل بالمعرف: "
                                                + id
                                )
                        );


        existingClient.setFirstName(
                updatedClient.getFirstName()
        );

        existingClient.setLastName(
                updatedClient.getLastName()
        );

        existingClient.setGender(
                updatedClient.getGender()
        );

        existingClient.setBirthDate(
                updatedClient.getBirthDate()
        );

        existingClient.setContactNumber(
                updatedClient.getContactNumber()
        );


        return clientRepository.save(existingClient);
    }


    // =========================================================
    // حذف العميل
    // =========================================================

    public void deleteClient(Long id) {

        Client client =
                clientRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "العميل غير موجود: " + id
                                )
                        );

        clientRepository.delete(client);
    }


    // =========================================================
    // تحديث HealthData
    // =========================================================
    public void updateHealthData(Long clientId, HealthData newData) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new RuntimeException("العميل غير موجود: " + clientId));

        HealthData currentData = client.getHealthData();

        if (currentData == null) {
            throw new RuntimeException(
                    "لا توجد بيانات صحية للعميل: " + clientId);
        }

        currentData.setMedicalHistory(newData.getMedicalHistory());
        currentData.setFamilyMedicalHistory(newData.getFamilyMedicalHistory());
        currentData.setNotes(newData.getNotes());
        currentData.setCurrentMedication(newData.getCurrentMedication());

        healthDataRepository.save(currentData);
    }

    // =========================================================
    // تحديث Lifestyle
    // =========================================================

    public void updateLifeStyle(
            Long clientId,
            LifeStyleInformation newData) {

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() ->
                        new RuntimeException("العميل غير موجود: " + clientId));

        LifeStyleInformation currentData =
                client.getLifeStyleInformation();

        if (currentData == null) {
            throw new RuntimeException(
                    "لا توجد بيانات نمط حياة للعميل: " + clientId);
        }

        currentData.setMealsPerDay(newData.getMealsPerDay());
        currentData.setBudget(newData.getBudget());
        currentData.setBreakfast(newData.getBreakfast());
        currentData.setLunch(newData.getLunch());
        currentData.setDinner(newData.getDinner());
        currentData.setSnacks(newData.getSnacks());
        currentData.setDrinks(newData.getDrinks());
        currentData.setBadHabits(newData.getBadHabits());
        currentData.setSleepHours(newData.getSleepHours());
        currentData.setFoodDislike(newData.getFoodDislike());

        lifeStyleInformationRepository.save(currentData);
    }

}