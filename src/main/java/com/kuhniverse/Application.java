package com.kuhniverse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

/**
 * @author timafe on 22.05.2015.
 adding meaningless comment
 */

/**
 * Main entry point for spring boot
 */
@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        int i=0;
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
     
     
        LOG.debug("Application {} started",ctx.getApplicationName());
    }
    private void test(){
    int j=1;
    }
     
}
