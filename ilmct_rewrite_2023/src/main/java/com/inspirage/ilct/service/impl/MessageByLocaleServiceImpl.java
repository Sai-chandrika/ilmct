package com.inspirage.ilct.service.impl;

import com.inspirage.ilct.service.MessageByLocaleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class MessageByLocaleServiceImpl implements MessageByLocaleService {

    @Autowired
    private MessageSource messageSource;

    @Override
    public String getMessage(String id) {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(id,null,locale);
    }

    @Override
    public String getCommonSuccessMessage() {
        Locale locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage("msg.success",null,locale);
    }
}
