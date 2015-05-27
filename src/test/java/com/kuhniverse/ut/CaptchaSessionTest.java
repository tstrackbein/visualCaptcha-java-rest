package com.kuhniverse.ut;

import com.kuhniverse.business.CaptchaSession;
import com.kuhniverse.domain.CaptchaAnswer;
import com.kuhniverse.domain.CaptchaFrontEndData;
import com.kuhniverse.domain.CaptchaSessionInfo;
import com.kuhniverse.integration.CaptchaRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by tillkuhn on 26.05.2015.
 */
public class CaptchaSessionTest {

    private CaptchaSession captchaSession;

    @Before
    public void init() {
        captchaSession = new CaptchaSession();
        CaptchaRepository cr = new CaptchaRepository();
        cr.init();
        ReflectionTestUtils.setField(captchaSession, "captchaRepository", cr);
     }

    @Test
    public void testValidateImage() {
        int optionCount = 4;
        int valid=0; int invalid=0;
        CaptchaFrontEndData data = captchaSession.start(optionCount );
        Assert.assertNotNull(data);
        String imageFieldName = data.getImageFieldName();
        List<String> values = data.getValues();
        for (String value: values) {
            Map<String,String> params = new HashMap<>();
            params.put(imageFieldName,value);
            if (captchaSession.isValid(params)) {
                valid++;
            } else {
                invalid++;
            }
        }
        Assert.assertEquals(1,valid);
        Assert.assertEquals(optionCount-1,invalid);
    }

    @Test
    public void testValidateAudio() {
        int optionCount = 2;
        CaptchaFrontEndData data = captchaSession.start(optionCount );
        Assert.assertNotNull(data);
        String audioFieldName = data.getAudioFieldName();
        Assert.assertNotNull(audioFieldName);
        Map<String,String> params = new HashMap<>();
        params.put(audioFieldName,"wrongAnswer");
        boolean valid = captchaSession.isValid(params);
        Assert.assertFalse(valid);
        // Now check for the right answer
        CaptchaSessionInfo info = (CaptchaSessionInfo ) ReflectionTestUtils.getField(captchaSession,"captchaSessionInfo");
        Assert.assertNotNull(info);
        CaptchaAnswer ca = info.getAudioAnswer();
        Map<String,String> params2 = new HashMap<>();
        params.put(audioFieldName,ca.getName());
        boolean valid2 = captchaSession.isValid(params);
        Assert.assertTrue(valid2);
    }

}
