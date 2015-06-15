package com.kuhniverse.it;

import com.kuhniverse.domain.CaptchaFrontEndData;
import com.kuhniverse.web.CaptchaController;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Inject;

/**
 * Created by tillkuhn on 27.05.2015.
 */
public class CaptchaIntegrationTest extends BaseIntegrationTest{

    @Inject
    private CaptchaController controller;

    @Test
    public void testCpatchas() {
        int size = 3;
        CaptchaFrontEndData cfe = controller.start(size);
        Assert.assertEquals(size, cfe.getValues().size() );
    }

}
