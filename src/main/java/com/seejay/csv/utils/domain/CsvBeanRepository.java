package com.seejay.csv.utils.domain;

import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CsvBeanRepository<T> {
    private static final char SEPARATOR = ',';
    private static final char QUOTE = '"';

    private final Path path;
    private final Class<T> beanType;

    public CsvBeanRepository(Path path, Class<T> beanType) {
        this.path = path;
        this.beanType = beanType;
    }

    public CsvBeanRepository(String path, Class<T> beanType) {
        this(Path.of(path), beanType);
    }

    private List<T> getCsvBeans() throws IOException {
        if(!Files.exists(path)) {
            return List.of();
        }

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return new CsvToBeanBuilder<T>(reader)
                    .withType(beanType)
                    .withSeparator(SEPARATOR)
                    .withQuoteChar(QUOTE)
                    .build().parse();
        }
    }

    private void postCsvBeans(Collection<T> beans) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {

            StatefulBeanToCsv<T> csvWriter =
                    new StatefulBeanToCsvBuilder<T>(writer)
                            .withSeparator(SEPARATOR)
                            .withQuotechar(QUOTE)
                            .withApplyQuotesToAll(true)
                            .build();

            csvWriter.write(beans.stream());
        }
    }

    private void putCsvBeans(Collection<T> beans) throws CsvRequiredFieldEmptyException, CsvDataTypeMismatchException, IOException {
        List<T> beansToWrite = new ArrayList<T>(getCsvBeans());
        beansToWrite.addAll(beans);
        postCsvBeans(beansToWrite);
    }

    private void updateCsvBean(T oldBean, T newBean) {
        return;
    }

    private void deleteCsvBean(T bean) {
        return;
    }
}
