package com.kuhniverse.web;

import com.kuhniverse.business.CaptchaSession;
import com.kuhniverse.domain.CaptchaFrontEndData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by timafe on 22.05.2015.
 */

@RestController
public class CaptchaController {

    private Logger log = LoggerFactory.getLogger(CaptchaController.class);
    private static final int DEFAULT_NUM_OPTIONS = 5;

    @Inject
    private CaptchaSession captchaSession;

    @RequestMapping(value="/start/{howMany}", method= RequestMethod.GET)
    @ResponseBody
    public CaptchaFrontEndData start(@PathVariable int howMany) {
        return captchaSession.start(howMany);
    }

    @RequestMapping(value="/image/{index}", method= RequestMethod.GET)
    // RequestParam boolean retina
    public void image(@PathVariable int index,HttpServletResponse response) {
        boolean retina = false;
        MediaType contentType = MediaType.IMAGE_PNG;
        InputStream input = captchaSession.getImage(index, retina);
        OutputStream output = null;
        byte[] buffer = new byte[10240];
        try {
            response.setContentType(contentType.toString());
            output = response.getOutputStream();
            for (int length = 0; (length = input.read(buffer)) > 0;) {
                output.write(buffer, 0, length);
            }
            output.flush();
        } catch (IOException e) {
            throw new RuntimeException("Cannot load image index " + index,e);
        }finally {
            try { output.close(); } catch (IOException ignore) {}
            try { input.close(); } catch (IOException ignore) {}
        }
    }

    @RequestMapping(value="/audio/{type}", method= RequestMethod.GET)
    public void audio(@PathVariable String type) {
        throw new UnsupportedOperationException();
    }

    @RequestMapping(value="/try", method= RequestMethod.POST)
    public void doTry() {
        throw new UnsupportedOperationException();
    }


}
