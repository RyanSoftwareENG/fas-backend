package com.fas.controller;

import com.fas.entity.FoodItem;
import com.fas.service.FoodItemService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/food")
@CrossOrigin
public class FoodItemController {

    private final FoodItemService foodItemService;

    public FoodItemController(FoodItemService foodItemService) {
        this.foodItemService = foodItemService;
    }

    // =========================
    // إضافة طعام
    // POST /api/food
    // =========================

    @PostMapping
    public ResponseEntity<FoodItem> saveFood(
            @RequestBody FoodItem foodItem
    ) {

        FoodItem savedFood =
                foodItemService.saveFood(foodItem);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedFood);
    }


    // =========================
    // جلب جميع الأطعمة
    // GET /api/food
    // =========================

    @GetMapping
    public ResponseEntity<List<FoodItem>> getAllFood() {

        List<FoodItem> foodItems =
                foodItemService.getAllFood();

        return ResponseEntity.ok(foodItems);
    }


    // =========================
    // جلب طعام واحد
    // GET /api/food/{id}
    // =========================

    @GetMapping("/{id}")
    public ResponseEntity<FoodItem> getFoodById(
            @PathVariable Long id
    ) {

        return foodItemService
                .getFoodById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }


    // =========================
    // تحديث طعام
    // PUT /api/food/{id}
    // =========================

    @PutMapping("/{id}")
    public ResponseEntity<FoodItem> updateFood(
            @PathVariable Long id,
            @RequestBody FoodItem foodItem
    ) {

        try {

            FoodItem updatedFood =
                    foodItemService.updateFood(id, foodItem);

            return ResponseEntity.ok(updatedFood);

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }


    // =========================
    // حذف طعام
    // DELETE /api/food/{id}
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(
            @PathVariable Long id
    ) {

        try {

            foodItemService.deleteFood(id);

            return ResponseEntity.noContent().build();

        } catch (RuntimeException e) {

            return ResponseEntity
                    .notFound()
                    .build();
        }
    }
}