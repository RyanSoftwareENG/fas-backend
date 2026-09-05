package com.fas.service;

import com.fas.dto.SessionListDTO;
import com.fas.entity.*;
import com.fas.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SessionService {

    // =========================================================
    // Repositories
    // =========================================================

    private final SessionRepository sessionRepository;

    private final NutritionPlanRepository nutritionPlanRepository;

    private final BodyDataRepository bodyDataRepository;

    private final ExaminationRepository examinationRepository;

    private final ClientRepository clientRepository;

    // Repository الخاص بوجبات الخطة
    private final PlanFoodItemRepository planFoodItemRepository;


    // =========================================================
    // Transaction Templates
    // =========================================================

    private final TransactionTemplate sessionTransaction;

    private final TransactionTemplate bodyDataTransaction;

    private final TransactionTemplate nutritionPlanTransaction;

    private final TransactionTemplate examinationTransaction;

    // معاملة مستقلة لوجبات الخطة
    private final TransactionTemplate planFoodItemTransaction;


    // =========================================================
    // Constructor
    // =========================================================

    public SessionService(
            SessionRepository sessionRepository,
            NutritionPlanRepository nutritionPlanRepository,
            BodyDataRepository bodyDataRepository,
            ExaminationRepository examinationRepository,
            ClientRepository clientRepository,
            PlanFoodItemRepository planFoodItemRepository,
            PlatformTransactionManager transactionManager) {

        // -----------------------------------------------------
        // Repositories
        // -----------------------------------------------------

        this.sessionRepository =
                sessionRepository;

        this.nutritionPlanRepository =
                nutritionPlanRepository;

        this.bodyDataRepository =
                bodyDataRepository;

        this.examinationRepository =
                examinationRepository;

        this.clientRepository =
                clientRepository;

        this.planFoodItemRepository =
                planFoodItemRepository;


        // -----------------------------------------------------
        // Transactions
        // -----------------------------------------------------

        this.sessionTransaction =
                new TransactionTemplate(
                        transactionManager
                );


        this.bodyDataTransaction =
                new TransactionTemplate(
                        transactionManager
                );


        this.nutritionPlanTransaction =
                new TransactionTemplate(
                        transactionManager
                );


        this.examinationTransaction =
                new TransactionTemplate(
                        transactionManager
                );


        // -----------------------------------------------------
        // Transaction مستقلة لوجبات الخطة
        // -----------------------------------------------------

        this.planFoodItemTransaction =
                new TransactionTemplate(
                        transactionManager
                );
    }


    // =========================================================
    // Helper
    // =========================================================

    private boolean safeEquals(
            String a,
            String b) {

        if (a == null && b == null) {
            return true;
        }

        if (a == null || b == null) {
            return false;
        }

        return a.equals(b);
    }


// =========================================================
    // حفظ Session + BodyData + NutritionPlan + Examinations
    // =========================================================

    public Session saveSession(Session session) {

        if (session == null) {
            throw new IllegalArgumentException(
                    "بيانات الجلسة لا يمكن أن تكون null"
            );
        }

        // =====================================================
        // الاحتفاظ بالبيانات التابعة قبل المعاملة الأولى
        // =====================================================

        BodyData bodyData = session.getBodyData();

        NutritionPlan nutritionPlan = session.getNutritionPlan();

        List<Examination> examinations =
                session.getExaminations() != null
                        ? new ArrayList<>(session.getExaminations())
                        : new ArrayList<>();


        // =====================================================
        // منع Cascade من حفظ البيانات التابعة مع Session
        // =====================================================

        session.setBodyData(null);
        session.setNutritionPlan(null);
        session.setExaminations(new ArrayList<>());


        // =====================================================
        // 1. المعاملة الأولى
        //    حفظ Session فقط
        // =====================================================

        Long sessionId;

        try {

            sessionId = sessionTransaction.execute(status -> {

                // -------------------------------------------------
                // التحقق من Client
                // -------------------------------------------------

                if (session.getClient() == null ||
                        session.getClient().getClientID() == null) {

                    throw new IllegalArgumentException(
                            "يجب تحديد العميل قبل حفظ الجلسة"
                    );
                }

                Long clientId =
                        session.getClient().getClientID();


                // -------------------------------------------------
                // جلب Client كـ Managed Entity
                // -------------------------------------------------

                Client managedClient =
                        clientRepository.findById(clientId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException(
                                                "العميل غير موجود: "
                                                        + clientId
                                        )
                                );


                // -------------------------------------------------
                // ربط Session بالعميل الحقيقي
                // -------------------------------------------------

                session.setClient(managedClient);


                // -------------------------------------------------
                // التأكد أن هذه Session جديدة
                // -------------------------------------------------
                // لا نسمح بـ ID = 0
                // -------------------------------------------------

                if (session.getId() != null &&
                        session.getId() == 0L) {

                    session.setId(null);
                }


                // -------------------------------------------------
                // حفظ Session
                // -------------------------------------------------

                Session saved =
                        sessionRepository.saveAndFlush(session);


                // -------------------------------------------------
                // التأكد من توليد ID
                // -------------------------------------------------

                if (saved.getId() == null) {

                    throw new IllegalStateException(
                            "تم حفظ Session ولكن لم يتم توليد Session ID"
                    );
                }


                System.out.println(
                        "========================================"
                );
                System.out.println(
                        "SESSION TRANSACTION SUCCESS"
                );
                System.out.println(
                        "NEW SESSION ID = " + saved.getId()
                );
                System.out.println(
                        "CLIENT ID = " + clientId
                );
                System.out.println(
                        "========================================"
                );


                return saved.getId();
            });

        } catch (Exception ex) {

            System.out.println(
                    "========================================"
            );
            System.out.println(
                    "SESSION TRANSACTION FAILED"
            );
            System.out.println(
                    "ERROR = " + ex.getMessage()
            );
            System.out.println(
                    "========================================"
            );

            // -----------------------------------------------------
            // لا تبدأ أي معاملة تابعة
            // -----------------------------------------------------

            throw ex;
        }


        // =====================================================
        // التأكد من نجاح المعاملة الأولى
        // =====================================================

        if (sessionId == null) {

            throw new IllegalStateException(
                    "فشلت المعاملة الأولى ولم يتم الحصول على Session ID"
            );
        }


        System.out.println(
                "SESSION COMMITTED SUCCESSFULLY"
        );

        System.out.println(
                "SESSION ID = " + sessionId
        );


        // =====================================================
        // 2. المعاملة الثانية
        //    حفظ BodyData
        // =====================================================

        if (bodyData != null) {

            try {

                bodyDataTransaction.executeWithoutResult(status -> {

                    // -------------------------------------------------
                    // الحصول على Session Managed جديدة
                    // من قاعدة البيانات
                    // -------------------------------------------------

                    Session managedSession =
                            sessionRepository.getReferenceById(
                                    sessionId
                            );


                    // -------------------------------------------------
                    // ربط BodyData بالـ Session الحالية
                    // -------------------------------------------------

                    bodyData.setSession(
                            managedSession
                    );


                    // -------------------------------------------------
                    // ربط BodyData بالعميل الموجود مع Session
                    // -------------------------------------------------

                    bodyData.setClient(
                            managedSession.getClient()
                    );


                    // -------------------------------------------------
                    // مهم مع @MapsId
                    //
                    // لا نضع sessionId يدوياً
                    // @MapsId سيأخذه من Session
                    // -------------------------------------------------

                    bodyData.setSessionId(null);


                    // -------------------------------------------------
                    // حفظ BodyData
                    // -------------------------------------------------

                    BodyData savedBodyData =
                            bodyDataRepository.saveAndFlush(
                                    bodyData
                            );


                    System.out.println(
                            "========================================"
                    );
                    System.out.println(
                            "BODY DATA TRANSACTION SUCCESS"
                    );
                    System.out.println(
                            "SESSION ID   = "
                                    + sessionId
                    );
                    System.out.println(
                            "BODY DATA ID = "
                                    + savedBodyData.getSessionId()
                    );
                    System.out.println(
                            "========================================"
                    );
                });

            } catch (Exception ex) {

                System.out.println(
                        "========================================"
                );
                System.out.println(
                        "BODY DATA TRANSACTION FAILED"
                );
                System.out.println(
                        "SESSION ID = " + sessionId
                );
                System.out.println(
                        "ERROR = " + ex.getMessage()
                );
                System.out.println(
                        "========================================"
                );

                // -------------------------------------------------
                // لا نحذف Session
                // لأنها محفوظة في Transaction مستقلة
                // -------------------------------------------------

                throw ex;
            }

        } else {

            System.out.println(
                    "BODY DATA = NULL -> NOT SAVED"
            );
        }


        // =====================================================
        // 3. المعاملة الثالثة
        //    NutritionPlan
        // =====================================================

        if (nutritionPlan != null) {

            nutritionPlanTransaction.executeWithoutResult(status -> {

                // -------------------------------------------------
                // Session Managed جديدة
                // -------------------------------------------------

                Session managedSession =
                        sessionRepository.getReferenceById(
                                sessionId
                        );


                // -------------------------------------------------
                // ربط NutritionPlan
                // -------------------------------------------------

                nutritionPlan.setSession(
                        managedSession
                );


                // -------------------------------------------------
                // حفظ NutritionPlan
                // -------------------------------------------------

                nutritionPlanRepository.saveAndFlush(
                        nutritionPlan
                );


                System.out.println(
                        "========================================"
                );
                System.out.println(
                        "NUTRITION PLAN TRANSACTION SUCCESS"
                );
                System.out.println(
                        "SESSION ID = " + sessionId
                );
                System.out.println(
                        "========================================"
                );
            });

        } else {

            System.out.println(
                    "NUTRITION PLAN = NULL -> NOT SAVED"
            );
        }


        // =====================================================
        // 4. المعاملة الرابعة
        //    Examinations
        // =====================================================

        if (!examinations.isEmpty()) {

            examinationTransaction.executeWithoutResult(status -> {

                // -------------------------------------------------
                // Session Managed جديدة
                // -------------------------------------------------

                Session managedSession =
                        sessionRepository.getReferenceById(
                                sessionId
                        );


                // -------------------------------------------------
                // حفظ Examination
                // -------------------------------------------------

                for (Examination examination :
                        examinations) {

                    if (examination != null) {

                        examination.setSession(
                                managedSession
                        );

                        examinationRepository.save(
                                examination
                        );
                    }
                }

                examinationRepository.flush();


                System.out.println(
                        "========================================"
                );
                System.out.println(
                        "EXAMINATIONS TRANSACTION SUCCESS"
                );
                System.out.println(
                        "SESSION ID = " + sessionId
                );
                System.out.println(
                        "COUNT = " + examinations.size()
                );
                System.out.println(
                        "========================================"
                );
            });

        } else {

            System.out.println(
                    "EXAMINATIONS = EMPTY -> NOT SAVED"
            );
        }


        // =====================================================
        // 5. إعادة Session من قاعدة البيانات
        // =====================================================
        //
        // لا نعيد savedSession القديمة لأنها Detached
        // =====================================================

        Session result =
                sessionRepository.findById(sessionId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "تم حفظ Session ولكن لم يتم العثور عليها: "
                                                + sessionId
                                )
                        );


        // =====================================================
        // إعادة العلاقات في الذاكرة
        // =====================================================

        result.setBodyData(bodyData);
        result.setNutritionPlan(nutritionPlan);
        result.setExaminations(examinations);


        // =====================================================
        // 6. إرجاع Session
        // =====================================================

        return result;
    }


    // =========================================================
    // جلب جميع الجلسات - البيانات الأساسية فقط
    // =========================================================

    public List<SessionListDTO> getAllSessions() {

        return sessionRepository.findAllSessionBasicData();
    }


    // =========================================================
    // جلب جلسة واحدة بواسطة ID
    // =========================================================

    public Optional<Session> getSessionById(Long id) {

        // =====================================================
        // 1. Session
        // =====================================================

        Optional<Session> sessionOptional =
                sessionRepository.findSessionOnly(id);

        if (sessionOptional.isEmpty()) {
            return Optional.empty();
        }

        Session session =
                sessionOptional.get();


        // =====================================================
        // 2. NutritionPlan
        // =====================================================

        NutritionPlan nutritionPlan =
                nutritionPlanRepository
                        .findBySessionId(id)
                        .orElse(null);


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


        // =====================================================
        // 4. BodyData
        // =====================================================

        BodyData bodyData =
                bodyDataRepository
                        .findBySessionId(id)
                        .orElse(null);


        // =====================================================
        // 5. Examinations
        // =====================================================

        List<Examination> examinations =
                examinationRepository
                        .findBySessionId(id);


        // =====================================================
        // 6. Client
        // =====================================================

        Client client = null;

        if (session.getClient() != null &&
                session.getClient().getClientID() != null) {

            client =
                    clientRepository
                            .findById(
                                    session.getClient()
                                            .getClientID()
                            )
                            .orElse(null);
        }


        // =====================================================
        // 7. ربط البيانات
        // =====================================================

        session.setNutritionPlan(
                nutritionPlan
        );

        session.setBodyData(
                bodyData
        );

        session.setExaminations(
                examinations
        );

        session.setClient(
                client
        );


        return Optional.of(session);
    }

    // =========================================================
    // جلب جلسات عميل معين
    // =========================================================

    public List<Session> getSessionsByClientId(
            Long clientId) {

        return sessionRepository.findByClientId(clientId);
    }


// =========================================================
// تحديث الجلسة كاملة
// Session + BodyData + NutritionPlan + Examinations
// =========================================================

    public Session updateSession(
            Long id,
            Session updatedSession) {

        if (updatedSession == null) {
            throw new IllegalArgumentException(
                    "بيانات الجلسة المحدثة لا يمكن أن تكون null"
            );
        }

        // =====================================================
        // 1. تحديث البيانات الأساسية للجلسة
        // =====================================================

        sessionTransaction.executeWithoutResult(status -> {

            Session existingSession =
                    sessionRepository.findById(id)
                            .orElseThrow(() ->
                                    new RuntimeException(
                                            "لم يتم العثور على الجلسة بالمعرف: "
                                                    + id
                                    )
                            );

            existingSession.setDuration(
                    updatedSession.getDuration()
            );

            existingSession.setPrice(
                    updatedSession.getPrice()
            );

            existingSession.setNotes(
                    updatedSession.getNotes()
            );

            sessionRepository.saveAndFlush(existingSession);

            System.out.println(
                    "✅ تم تحديث البيانات الأساسية للجلسة: " + id
            );
        });


        // =====================================================
        // 2. تحديث BodyData
        // =====================================================

        BodyData updatedBodyData =
                updatedSession.getBodyData();

        if (updatedBodyData != null) {

            bodyDataTransaction.executeWithoutResult(status -> {

                Optional<BodyData> existingBodyData =
                        bodyDataRepository.findBySessionId(id);

                if (existingBodyData.isPresent()) {

                    // ---------------------------------------------
                    // يوجد سجل سابق -> UPDATE
                    // ---------------------------------------------

                    BodyData current =
                            existingBodyData.get();

                    current.setHeight(
                            updatedBodyData.getHeight()
                    );

                    current.setWeight(
                            updatedBodyData.getWeight()
                    );

                    current.setBodyFatPercentage(
                            updatedBodyData.getBodyFatPercentage()
                    );

                    current.setSmm(
                            updatedBodyData.getSmm()
                    );

                    current.setMuscleMass(
                            updatedBodyData.getMuscleMass()
                    );

                    current.setActivityFactor(
                            updatedBodyData.getActivityFactor()
                    );

                    current.setPhysicalActivity(
                            updatedBodyData.getPhysicalActivity()
                    );

                    current.setArm_C(
                            updatedBodyData.getArm_C()
                    );

                    current.setChest_C(
                            updatedBodyData.getChest_C()
                    );

                    current.setWaist_C(
                            updatedBodyData.getWaist_C()
                    );

                    current.setAbdominal_C(
                            updatedBodyData.getAbdominal_C()
                    );

                    current.setHip_C(
                            updatedBodyData.getHip_C()
                    );

                    current.setMidThigh_C(
                            updatedBodyData.getMidThigh_C()
                    );

                    current.setCalf_C(
                            updatedBodyData.getCalf_C()
                    );

                    bodyDataRepository.saveAndFlush(current);

                    System.out.println(
                            "✅ تم تحديث BodyData للجلسة: " + id
                    );

                } else {

                    // ---------------------------------------------
                    // لا يوجد سجل -> INSERT
                    // ---------------------------------------------

                    Session managedSession =
                            sessionRepository.getReferenceById(id);

                    updatedBodyData.setSession(
                            managedSession
                    );

                    updatedBodyData.setClient(
                            managedSession.getClient()
                    );

                    // لأن BodyData يستخدم @MapsId
                    updatedBodyData.setSessionId(null);

                    bodyDataRepository.saveAndFlush(
                            updatedBodyData
                    );

                    System.out.println(
                            "✅ تم إنشاء BodyData للجلسة: " + id
                    );
                }
            });

        } else {

            System.out.println(
                    "ℹ️ BodyData = NULL -> لا يوجد تعديل"
            );
        }


// =====================================================
// 3. تحديث NutritionPlan
// =====================================================

        NutritionPlan updatedPlan =
                updatedSession.getNutritionPlan();

        if (updatedPlan != null) {

            // =================================================
            // 3-A. حفظ بيانات الخطة فقط
            // =================================================

            nutritionPlanTransaction.executeWithoutResult(status -> {

                Optional<NutritionPlan> existingPlan =
                        nutritionPlanRepository.findBySessionId(id);

                if (existingPlan.isPresent()) {

                    // ---------------------------------------------
                    // يوجد سجل سابق -> UPDATE
                    // ---------------------------------------------

                    NutritionPlan current =
                            existingPlan.get();

                    current.setTargetGoal(
                            updatedPlan.getTargetGoal()
                    );

                    current.setPlanStatus(
                            updatedPlan.getPlanStatus()
                    );

                    current.setMealDistribution(
                            updatedPlan.getMealDistribution()
                    );

                    current.setStartDate(
                            updatedPlan.getStartDate()
                    );

                    current.setEndDate(
                            updatedPlan.getEndDate()
                    );

                    current.setProteinAmount(
                            updatedPlan.getProteinAmount()
                    );

                    current.setCarbohydratesAmount(
                            updatedPlan.getCarbohydratesAmount()
                    );

                    current.setFatAmount(
                            updatedPlan.getFatAmount()
                    );

                    current.setWaterIntake(
                            updatedPlan.getWaterIntake()
                    );

                    current.setNotes(
                            updatedPlan.getNotes()
                    );

                    current.setTotalCalories(
                            updatedPlan.getTotalCalories()
                    );

                    current.setMealsCount(
                            updatedPlan.getMealsCount()
                    );

                    nutritionPlanRepository.saveAndFlush(
                            current
                    );

                } else {

                    // ---------------------------------------------
                    // لا يوجد سجل -> INSERT
                    // ---------------------------------------------

                    Session managedSession =
                            sessionRepository.getReferenceById(id);

                    updatedPlan.setSession(
                            managedSession
                    );

                    nutritionPlanRepository.saveAndFlush(
                            updatedPlan
                    );
                }
            });


            // =================================================
            // 3-B. حفظ الوجبات المرتبطة بالخطة
            //     في معاملة منفصلة
            // =================================================

            List<PlanFoodItem> updatedFoods =
                    updatedPlan.getSelectedFoods() != null
                            ? updatedPlan.getSelectedFoods()
                            : new ArrayList<>();


            planFoodItemTransaction.executeWithoutResult(status -> {

                // -------------------------------------------------
                // الحصول على الخطة الفعلية من DB
                // -------------------------------------------------

                NutritionPlan managedPlan =
                        nutritionPlanRepository
                                .findBySessionId(id)
                                .orElseThrow(() ->
                                        new IllegalStateException(
                                                "لم يتم العثور على NutritionPlan للجلسة: "
                                                        + id
                                        )
                                );


                // -------------------------------------------------
                // الوجبات الموجودة حاليًا
                // -------------------------------------------------

                List<PlanFoodItem> existingFoods =
                        planFoodItemRepository
                                .findByNutritionPlanPlanId(
                                        managedPlan.getPlanId()
                                );


                // =================================================
                // 1. حذف الوجبات التي أزيلت من الواجهة
                // =================================================

                for (PlanFoodItem existingFood :
                        existingFoods) {

                    boolean stillExists = false;

                    for (PlanFoodItem incomingFood :
                            updatedFoods) {

                        if (incomingFood == null) {
                            continue;
                        }

                        FoodItem incomingItem =
                                incomingFood.getFoodItem();

                        FoodItem existingItem =
                                existingFood.getFoodItem();

                        if (incomingItem != null &&
                                existingItem != null &&
                                incomingItem.getFoodItemId() != null &&
                                existingItem.getFoodItemId() != null &&
                                incomingItem.getFoodItemId()
                                        .equals(
                                                existingItem
                                                        .getFoodItemId()
                                        ) &&
                                safeEquals(
                                        incomingFood.getMealType(),
                                        existingFood.getMealType()
                                )) {

                            stillExists = true;

                            break;
                        }
                    }


                    if (!stillExists) {

                        planFoodItemRepository.delete(
                                existingFood
                        );
                    }
                }


                // =================================================
                // 2. إضافة / تحديث الوجبات
                // =================================================

                for (PlanFoodItem incomingFood :
                        updatedFoods) {

                    if (incomingFood == null) {
                        continue;
                    }


                    FoodItem incomingItem =
                            incomingFood.getFoodItem();


                    if (incomingItem == null ||
                            incomingItem.getFoodItemId() == null) {

                        continue;
                    }


                    // -------------------------------------------------
                    // البحث عن الوجبة نفسها
                    // -------------------------------------------------

                    PlanFoodItem existingFood =
                            existingFoods.stream()
                                    .filter(item ->

                                            item.getFoodItem() != null &&

                                                    item.getFoodItem()
                                                            .getFoodItemId() != null &&

                                                    item.getFoodItem()
                                                            .getFoodItemId()
                                                            .equals(
                                                                    incomingItem
                                                                            .getFoodItemId()
                                                            ) &&

                                                    safeEquals(
                                                            item.getMealType(),
                                                            incomingFood.getMealType()
                                                    )
                                    )
                                    .findFirst()
                                    .orElse(null);


                    if (existingFood != null) {

                        // =================================================
                        // UPDATE
                        // =================================================

                        existingFood.setQuantity(
                                incomingFood.getQuantity()
                        );

                        existingFood.setUnit(
                                incomingFood.getUnit()
                        );

                        // الاحتفاظ بالخطة الحالية
                        existingFood.setNutritionPlan(
                                managedPlan
                        );

                        // الاحتفاظ بـ FoodItem الموجود في DB
                        existingFood.setFoodItem(
                                incomingItem
                        );

                        planFoodItemRepository.save(
                                existingFood
                        );

                    } else {

                        // =================================================
                        // INSERT
                        // =================================================

                        PlanFoodItem newFood =
                                new PlanFoodItem();

                        newFood.setNutritionPlan(
                                managedPlan
                        );

                        newFood.setFoodItem(
                                incomingItem
                        );

                        newFood.setMealType(
                                incomingFood.getMealType()
                        );

                        newFood.setQuantity(
                                incomingFood.getQuantity()
                        );

                        newFood.setUnit(
                                incomingFood.getUnit()
                        );


                        planFoodItemRepository.save(
                                newFood
                        );
                    }
                }


                planFoodItemRepository.flush();
            });

        } else {

            System.out.println(
                    "NutritionPlan = NULL -> لا يوجد تعديل"
            );
        }


        // =====================================================
        // 4. تحديث Examinations
        // =====================================================

        List<Examination> updatedExaminations =
                updatedSession.getExaminations() != null
                        ? updatedSession.getExaminations()
                        : new ArrayList<>();

        examinationTransaction.executeWithoutResult(status -> {

            Session managedSession =
                    sessionRepository.getReferenceById(id);

            // ---------------------------------------------
            // الفحوصات الموجودة حاليًا في DB
            // ---------------------------------------------

            List<Examination> existingExaminations =
                    examinationRepository.findBySessionId(id);

            // ---------------------------------------------
            // تحديث / حذف / إضافة
            // ---------------------------------------------

            for (Examination existing :
                    existingExaminations) {

                boolean stillExists = false;

                for (Examination incoming :
                        updatedExaminations) {

                    if (incoming.getExaminationId() != null &&
                            existing.getExaminationId() != null &&
                            incoming.getExaminationId()
                                    .equals(existing.getExaminationId())) {

                        stillExists = true;

                        // -------------------------------
                        // UPDATE
                        // -------------------------------

                        existing.setExaminationName(
                                incoming.getExaminationName()
                        );

                        existing.setNotes(
                                incoming.getNotes()
                        );

                        existing.setExaminationImage(
                                incoming.getExaminationImage()
                        );

                        existing.setModificationDate(
                                incoming.getModificationDate()
                        );

                        break;
                    }
                }

                // -------------------------------
                // DELETE
                // -------------------------------

                if (!stillExists) {

                    examinationRepository.delete(
                            existing
                    );
                }
            }


            // ---------------------------------------------
            // INSERT للفحوصات الجديدة
            // ---------------------------------------------

            for (Examination incoming :
                    updatedExaminations) {

                if (incoming == null) {
                    continue;
                }

                // جديد
                if (incoming.getExaminationId() == null ||
                        incoming.getExaminationId() == 0) {

                    incoming.setExaminationId(null);

                    incoming.setSession(
                            managedSession
                    );

                    examinationRepository.save(
                            incoming
                    );
                }
            }

            examinationRepository.flush();

            System.out.println(
                    "✅ تم تحديث الفحوصات للجلسة: "
                            + id
            );

            System.out.println(
                    "عدد الفحوصات الحالية: "
                            + updatedExaminations.size()
            );
        });


        // =====================================================
        // 5. جلب الجلسة مرة أخرى من DB
        // =====================================================

        Optional<Session> result =
                getSessionById(id);

        if (result.isEmpty()) {

            throw new RuntimeException(
                    "تم التحديث ولكن تعذر إعادة جلب الجلسة: "
                            + id
            );
        }

        System.out.println(
                "========================================"
        );

        System.out.println(
                "✅ UPDATE SESSION SUCCESS"
        );

        System.out.println(
                "SESSION ID = " + id
        );

        System.out.println(
                "========================================"
        );

        return result.get();
    }

    // =========================================================
    // حذف الجلسة
    // =========================================================

    public void deleteSession(Long id) {

        Session session =
                sessionRepository.findById(id)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "الجلسة غير موجودة: " + id
                                )
                        );

        sessionRepository.delete(session);
    }
}
