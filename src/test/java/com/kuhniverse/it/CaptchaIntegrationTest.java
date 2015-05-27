package com.kuhniverse.it;

import com.kuhniverse.Application;
import com.kuhniverse.domain.CaptchaFrontEndData;
import com.kuhniverse.web.CaptchaController;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.inject.Inject;

/**
 * Created by tillkuhn on 27.05.2015.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class CaptchaIntegrationTest {

    @Inject
    private CaptchaController controller;

    @Test
    public void testCpatchas() {
        int size = 3;
        CaptchaFrontEndData cfe = controller.start(size);
        Assert.assertEquals(size, cfe.getValues().size() );
    }
}
