package com.weshare.batch.config;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.validation.DataBinder;

import java.beans.PropertyEditorSupport;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 定义CSV日期类型的自动转换和类型设置
 *
 * @param <T> 需要映射的类型
 * @author qujiayuan
 */
public class CsvBeanWrapperFieldSetMapper<T> extends BeanWrapperFieldSetMapper<T> {

    @Override
    protected void initBinder(DataBinder binder) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter dateTimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (StringUtils.isNotEmpty(text) && !"null".equals(text)) {
                    setValue(LocalDate.parse(text, dateFormatter));
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() throws IllegalArgumentException {
                Object date = getValue();
                if (date != null) {
                    return dateFormatter.format((LocalDate) date);
                } else {
                    return null;
                }
            }
        });

        binder.registerCustomEditor(LocalDateTime.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (StringUtils.isNotEmpty(text) && !"null".equals(text)) {
                    setValue(LocalDateTime.parse(text, dateTimeformatter));
                } else {
                    setValue(null);
                }
            }

            @Override
            public String getAsText() throws IllegalArgumentException {
                Object date = getValue();
                if (date != null) {
                    return dateTimeformatter.format((LocalDateTime) date);
                } else {
                    return null;
                }
            }
        });

        binder.registerCustomEditor(BigDecimal.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (StringUtils.isNotEmpty(text) && !"null".equals(text)) {
                    setValue(new BigDecimal(text));
                } else {
                    setValue(BigDecimal.ZERO);
                }
            }

            @Override
            public String getAsText() {
                Object value = getValue();
                if (value != null) {
                    return value.toString();
                } else {
                    return null;
                }
            }
        });
    }

    public CsvBeanWrapperFieldSetMapper(Class<? extends T> type) {
        super();
        this.setTargetType(type);
    }
}
