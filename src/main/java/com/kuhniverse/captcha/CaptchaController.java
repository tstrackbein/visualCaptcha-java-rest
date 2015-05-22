package com.kuhniverse.captcha;

import net.dotzour.visualCaptcha.CaptchaFrontEndData;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by timafe on 22.05.2015.
 */

@RestController
public class CaptchaController {


    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping("/start")
    public CaptchaFrontEndData start() {
        return new CaptchaFrontEndData("","",new ArrayList<String>(),"");
    }

}
