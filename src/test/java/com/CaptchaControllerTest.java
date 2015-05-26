package com;

import com.kuhniverse.domain.CaptchaFrontEndData;
import com.kuhniverse.business.CaptchaSession;
import com.kuhniverse.integration.CaptchaRepository;
import com.kuhniverse.web.CaptchaController;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import javax.servlet.http.HttpServletResponse;

/**
 * Created by tillkuhn on 21.11.2014.
 */
public class CaptchaControllerTest {

    private CaptchaController cc;

    @Before
    public void before() {

        cc = new CaptchaController();
        CaptchaSession cs = new CaptchaSession();
        CaptchaRepository cr = new CaptchaRepository();
        cr.init();
        ReflectionTestUtils.setField(cs, "captchaRepository", cr);
        ReflectionTestUtils.setField(cc,"captchaSession",cs);
    }

    @Test
    public void testStart() {
        int size = 5;
        CaptchaFrontEndData cfed = cc.start(size);
        Assert.assertEquals(size,cfed.getValues().size());
    }


    @Test
    public void testGetIndex() {
        int size = 2;
        CaptchaFrontEndData cfed = cc.start(size);
        Assert.assertEquals(size,cfed.getValues().size());
        for (int i = 0; i < size; i++) {
            HttpServletResponse  response = new MockHttpServletResponse();
            cc.image(i, response);
            Assert.assertEquals(MediaType.IMAGE_PNG_VALUE,response.getContentType());
        }
    }


}
