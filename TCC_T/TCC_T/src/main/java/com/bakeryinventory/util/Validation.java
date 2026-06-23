package com.bakeryinventory.util;

import com.bakeryinventory.model.Product;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class Validation {
    private Validation() {
    }

    public static void requireUsername(String username) throws ValidationException {
        if (username == null || username.trim().length() < 3) {
            throw new ValidationException("Username must contain at least 3 characters.");
        }
        if (!username.trim().matches("[A-Za-z0-9_.-]+")) {
            throw new ValidationException("Username can contain only letters, numbers, dots, dashes, and underscores.");
        }
    }

    public static void requirePassword(String password) throws ValidationException {
        if (password == null || password.length() < 8) {
            throw new ValidationException("Password must contain at least 8 characters.");
        }
    }

    public static void requireProduct(Product product) throws ValidationException {
        if (isBlank(product.getName())) {
            throw new ValidationException("Product name is required.");
        }
        if (isBlank(product.getCategory())) {
            throw new ValidationException("Category is required.");
        }
        if (product.getQuantity() < 0) {
            throw new ValidationException("Quantity cannot be negative.");
        }
        if (product.getIdealQuantity() < 0) {
            throw new ValidationException("Ideal stock quantity cannot be negative.");
        }
        BigDecimal unitPrice = product.getUnitPrice();
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ValidationException("Unit price cannot be negative.");
        }
        LocalDate expirationDate = product.getExpirationDate();
        if (expirationDate == null) {
            throw new ValidationException("Expiration date is required.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
