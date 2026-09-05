package com.fas.service;

import com.fas.entity.FoodItem;
import com.fas.repository.FoodItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FoodItemService {

    private final FoodItemRepository foodItemRepository;

    public FoodItemService(FoodItemRepository foodItemRepository) {
        this.foodItemRepository = foodItemRepository;
    }

    // إضافة وجبة
    public FoodItem saveFood(FoodItem foodItem) {
        return foodItemRepository.save(foodItem);
    }

    // جلب جميع الأطعمة
    public List<FoodItem> getAllFood() {
        return foodItemRepository.findAll();
    }

    // جلب طعام حسب ID
    public Optional<FoodItem> getFoodById(Long id) {
        return foodItemRepository.findById(id);
    }

    // تحديث الطعام
    public FoodItem updateFood(Long id, FoodItem foodItem) {

        FoodItem existingFood = foodItemRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("الطعام غير موجود بالرقم: " + id)
                );

        existingFood.setFoodName(foodItem.getFoodName());
        existingFood.setCalories(foodItem.getCalories());
        existingFood.setProtein(foodItem.getProtein());
        existingFood.setCarbohydrates(foodItem.getCarbohydrates());
        existingFood.setFat(foodItem.getFat());
        existingFood.setAvailable(foodItem.isAvailable());
        existingFood.setCostLevel(foodItem.getCostLevel());

        return foodItemRepository.save(existingFood);
    }

    // حذف الطعام
    public void deleteFood(Long id) {

        if (!foodItemRepository.existsById(id)) {
            throw new RuntimeException(
                    "لا يمكن حذف الطعام، الرقم غير موجود: " + id
            );
        }

        foodItemRepository.deleteById(id);
    }
}