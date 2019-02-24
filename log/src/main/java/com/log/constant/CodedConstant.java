package com.log.constant;

import java.util.Arrays;

public interface CodedConstant {

    /**
     * get code of constant
     *
     * @return code number
     */
    int getCode();

    /**
     * find the constant by code
     *
     * @param code         code number
     * @param values       constant data set
     * @param defaultValue default value
     * @param <T>          type of constant
     * @return constant of code
     */
    static <T extends CodedConstant> T valueOf(Integer code, T[] values, T defaultValue) {
        if (code == null || values == null || values.length == 0) {
            return defaultValue;
        }
        return Arrays.stream(values).filter(v -> v.getCode() == code).findFirst().orElse(defaultValue);
    }
}
