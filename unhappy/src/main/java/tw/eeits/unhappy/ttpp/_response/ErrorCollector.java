package tw.eeits.unhappy.ttpp._response;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import jakarta.validation.Validator;

public class ErrorCollector {
    private final Set<String> errors = new HashSet<>();

    public void add(String error) {
        if (error != null && !error.isEmpty()) {
            errors.add(error);
        }
    }

    public <T> void validate(T object, Validator validator) {
        if (object != null) {
            errors.addAll(
                validator.validate(object)
                    .stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.toSet())
            );
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public String getErrorMessage() {
        return String.join("; ", errors);
    }

    public Set<String> getErrors() {
        return Collections.unmodifiableSet(errors);
    }
}


