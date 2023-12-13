package com.example.todorest.validation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

public class ValidationCheck {
    private ValidationCheck() {}


    /**
     * Checks for validation errors in the binding result and builds a StringBuilder with error messages.
     *
     * @param bindingResult The result of the validation process.
     * @return StringBuilder containing error messages, or an empty StringBuilder if no errors are found.
     */
    public static StringBuilder checkValidation(BindingResult bindingResult) {
        StringBuilder errorBuilder = new StringBuilder();
        if (bindingResult.hasErrors()) {
            List<ObjectError> errors = bindingResult.getAllErrors();
            for (ObjectError error : errors) {
                errorBuilder.append(error.getDefaultMessage()).append("/n");
            }
        }
        return errorBuilder;
    }
}
