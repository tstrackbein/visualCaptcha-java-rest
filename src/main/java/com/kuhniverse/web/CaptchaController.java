package com.kuhniverse.web;

import com.kuhniverse.domain.CaptchaFrontEndData;
import com.kuhniverse.domain.CaptchaSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by timafe on 22.05.2015.
 */

@RestController
public class CaptchaController {

    private Logger log = LoggerFactory.getLogger(CaptchaController.class);

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Inject
    private CaptchaSession captchaSession;


    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) {
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    @RequestMapping(value="/start/{howMany}", method= RequestMethod.GET)
    public CaptchaFrontEndData start(@PathVariable int howMany) {
        /*
        int optionCount = DEFAULT_NUM_OPTIONS;
        try {
            optionCount = Integer.parseInt(param);
        } catch (NumberFormatException e) {
            log.warn("Invalid param value for number of options to display: '{}'.  Will use default value {}.", param, DEFAULT_NUM_OPTIONS);
        }
        */

        return captchaSession.start(howMany);
    }

    @RequestMapping(value="/start/{index}", method= RequestMethod.GET)
    public String image(@PathVariable int index,@RequestParam boolean retina) {
        return captchaSession.image(index,retina);
    }

}
