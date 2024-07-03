package com.inspirage.ilct.config;

import com.inspirage.ilct.util.SingleTonClass;
import com.inspirage.ilct.util.Utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class InitData implements ApplicationRunner {

    @Autowired
    SingleTonClass singleTon;


    @Override
    public void run(ApplicationArguments args) throws Exception {


        for(int i=0;i<1000;i++) {
            String code = Utility.getRandomColorCode();
            if (!singleTon.getColorCodes().contains(code)) {
                singleTon.getColorCodes().add(code);
            }
        }

    }
}
