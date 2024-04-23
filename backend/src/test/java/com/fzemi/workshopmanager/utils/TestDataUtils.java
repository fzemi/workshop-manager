package com.fzemi.workshopmanager.utils;

import com.fzemi.workshopmanager.client.dto.ClientDTO;
import com.fzemi.workshopmanager.client.entity.Client;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public final class TestDataUtils {
    private TestDataUtils() {
    }

    private static Date prepareDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.APRIL, 8);
        return calendar.getTime();
    }

    /**
     * Asserts that all fields of the expected object are equal to the actual object.
     *
     * @param expected expected object
     * @param actual   actual object
     * @param <T>      type of the objects
     */
    public static <T> void assertFields(T expected, T actual) {
        Field[] fields = expected.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object expectedValue = field.get(expected);
                Object actualValue = field.get(actual);
                assertThat(expectedValue).isEqualTo(actualValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static Client createTestClientA() {
        return Client.builder()
                .id(1L)
                .firstname("John")
                .surname("Doe")
                .pesel("12345678901")
                .phoneNumber("123456789")
                .email("test@email.com")
                .country("Poland")
                .postalCode("12-345")
                .city("City")
                .address("Street 1")
//                .birthDate(prepareDate())
                .vehicles(List.of())
                .build();
    }

    public static Client createTestClientA(String firstname) {
        return Client.builder()
                .id(1L)
                .firstname(firstname)
                .surname("Doe")
                .pesel("12345678901")
                .phoneNumber("123456789")
                .email("test@email.com")
                .country("Poland")
                .postalCode("12-345")
                .city("City")
                .address("Street 1")
//                .birthDate(prepareDate())
                .vehicles(List.of())
                .build();
    }

    public static Client createTestClientB() {
        return Client.builder()
                .id(2L)
                .firstname("Jane")
                .surname("Brown")
                .pesel("12345678902")
                .phoneNumber("123456789")
                .email("test2@email.com")
                .country("Poland")
                .postalCode("12-346")
                .city("City")
                .address("Street 2")
//                .birthDate(prepareDate())
                .vehicles(List.of())
                .build();
    }

    public static ClientDTO createTestClientADTO() {
        return ClientDTO.builder()
                .id(1L)
                .firstname("John")
                .surname("Doe")
                .pesel("12345678901")
                .phoneNumber("123456789")
                .email("test@email.com")
                .country("Poland")
                .postalCode("12-345")
                .city("City")
                .address("Street 1")
//                .birthDate(prepareDate())
                .vehicles(List.of())
                .build();
    }

    public static ClientDTO createTestClientADTO(String firstname) {
        return ClientDTO.builder()
                .id(1L)
                .firstname(firstname)
                .surname("Doe")
                .pesel("12345678901")
                .phoneNumber("123456789")
                .email("test@email.com")
                .country("Poland")
                .postalCode("12-345")
                .city("City")
                .address("Street 1")
//                .birthDate(prepareDate())
                .vehicles(List.of())
                .build();
    }


}
