package com.kuhniverse.web;

import com.kuhniverse.domain.CaptchaFrontEndData;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
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

    @RequestMapping(value="/start/{optionCount}", method= RequestMethod.GET)
    public CaptchaFrontEndData start(@PathVariable int optionCount) {
        List<String> images = new  ArrayList<String>();
        for (int i = 0; i < optionCount; i++) {
            images.add(i +"");
        }
        return new CaptchaFrontEndData("","",images,"");
    }

}
