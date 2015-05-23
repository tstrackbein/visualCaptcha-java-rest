package com;

import com.kuhniverse.captcha.CaptchaController;
import net.dotzour.visualCaptcha.CaptchaFrontEndData;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
/**
 * Created by tillkuhn on 21.11.2014.
 */
public class CaptchaControllerTest {

    private CaptchaController cc;

    @Before
    public void before() {
        cc = new CaptchaController();
    }
    @Test
    public void testStart() {
        int size = 5;
        CaptchaFrontEndData cfed = cc.start(size);
        Assert.assertEquals(size,cfed.getValues().size());
    }



}
