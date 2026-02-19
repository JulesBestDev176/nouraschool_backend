package com.nouraschool.domain.utils;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.jboss.logging.Logger;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public final class CsvDataLoaderUtils {

    private static final Logger LOG = Logger.getLogger(CsvDataLoaderUtils.class);
    private static final char COLUMN_SEPARATOR = ';';

    private CsvDataLoaderUtils() {
    }

    public static <T> List<T> loadObjectList(Class<T> type, String fileName) {
        try {
            var bootstrapSchema = CsvSchema.emptySchema()
                    .withHeader()
                    .withColumnSeparator(COLUMN_SEPARATOR);
            var mapper = new CsvMapper();

            try (InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName)) {
                if (is == null) {
                    LOG.errorf("CSV file not found: %s", fileName);
                    return Collections.emptyList();
                }

                MappingIterator<T> readValues = mapper.readerFor(type).with(bootstrapSchema).readValues(is);
                return readValues.readAll();
            }
        } catch (Exception ex) {
            LOG.errorf(ex, "Error loading CSV %s: %s", fileName, ex.getMessage());
            return Collections.emptyList();
        }
    }
}