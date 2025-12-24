package com.recyclestudy.common;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NullValidator {

    public static ValidatorBuilder builder() {
        return new ValidatorBuilder();
    }

    public static class ValidatorBuilder {

        private final List<ValidationElement> elements = new ArrayList<>();

        public ValidatorBuilder add(final String name, final Object target) {
            this.elements.add(new ValidationElement(name, target));
            return this;
        }

        public void validate() {
            for (final ValidationElement element : this.elements) {
                if (element.target() == null) {
                    throw new IllegalArgumentException("null이 될 수 없습니다: %s".formatted(element.name));
                }
            }
        }

        private record ValidationElement(String name, Object target) {
        }
    }
}
