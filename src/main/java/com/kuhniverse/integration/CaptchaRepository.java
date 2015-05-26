package com.kuhniverse.integration;

import com.google.common.io.Closeables;
import com.google.common.reflect.TypeToken;
import com.google.gson.GsonBuilder;
import com.kuhniverse.domain.CaptchaAnswer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by tillkuhn on 26.05.2015.
 */
@Repository
public class CaptchaRepository {

    private Logger log = LoggerFactory.getLogger(CaptchaRepository.class);

    private List<CaptchaAnswer> images;
    private List<CaptchaAnswer> audios;

    @PostConstruct
    public void init() {
        try {
            loadImages();
            loadAudios();
        } catch(RuntimeException e){
            throw new RuntimeException("Unable to initialize CaptchaServlet.  Failed to load resources.", e);
        }
        log.debug("Init CaptchaRepository with {} images and {} audios",images.size(),audios.size());
    }


    public List<CaptchaAnswer> getImages() {
        return images;
    }

    public List<CaptchaAnswer> getAudios() {
        return audios;
    }

    private void loadImages() {
        Reader reader = null;
        try{
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("net/dotzour/visualCaptcha/images.json"));
            images = new GsonBuilder().create().fromJson(reader, new TypeToken<ArrayList<CaptchaAnswer>>(){}.getType());
        }
        finally{
            Closeables.closeQuietly(reader);
        }
    }

    private void loadAudios() {
        Reader reader = null;
        try{
            reader = new InputStreamReader(Thread.currentThread().getContextClassLoader().getResourceAsStream("net/dotzour/visualCaptcha/audios.json"));
            audios = new GsonBuilder().create().fromJson(reader, new TypeToken<ArrayList<CaptchaAnswer>>(){}.getType());
        }
        finally{
            Closeables.closeQuietly(reader);
        }
    }
}
