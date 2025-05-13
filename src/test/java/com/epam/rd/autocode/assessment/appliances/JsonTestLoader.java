package com.epam.rd.autocode.assessment.appliances;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

public class JsonTestLoader {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T load(String path, Class<T> clazz) {
        try (InputStream is = JsonTestLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("File not found: " + path);
            }
            return mapper.readValue(is, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON from " + path, e);
        }
    }

    public static <T> List<T> loadList(String path, Class<T[]> arrayClass) {
        try (InputStream is = JsonTestLoader.class.getResourceAsStream(path)) {
            if (is == null) {
                throw new RuntimeException("File not found: " + path);
            }
            T[] array = mapper.readValue(is, arrayClass);
            return Arrays.asList(array);
        } catch (Exception e) {
            throw new RuntimeException("Error reading JSON array from " + path, e);
        }
    }
}
