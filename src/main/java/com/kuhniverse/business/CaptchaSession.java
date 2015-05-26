package com.kuhniverse.business;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;
import com.google.common.net.MediaType;
import com.kuhniverse.domain.CaptchaAnswer;
import com.kuhniverse.domain.CaptchaFrontEndData;
import com.kuhniverse.domain.CaptchaSessionInfo;
import com.kuhniverse.integration.CaptchaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.util.Collections.shuffle;

@Component
@Scope(proxyMode= ScopedProxyMode.TARGET_CLASS, value="session")
public class CaptchaSession implements Serializable {

    private Logger log = LoggerFactory.getLogger(CaptchaSession.class);

    private Random rand = new Random();
    private CaptchaSessionInfo captchaSessionInfo;

    @Inject
    private CaptchaRepository captchaRepository;

    /**
     * Init captchas with a size of optionCount
     * @param optionCount
     * @return
     */
    public CaptchaFrontEndData start(int optionCount)  {

        String salt = UUID.randomUUID().toString();
        List<CaptchaAnswer> choices = getRandomCaptchaOptions(optionCount, salt);
        CaptchaAnswer validChoice = choices.get(rand.nextInt(optionCount));
        CaptchaAnswer audioOption = getRandomCaptchaAudio(salt);
        String fieldName = hash(UUID.randomUUID().toString(), salt);
        String audioFieldName = hash(UUID.randomUUID().toString(), salt);
        this.captchaSessionInfo = new CaptchaSessionInfo(fieldName, validChoice.getObfuscatedName(), audioFieldName, audioOption, choices);

        List<String> frontEndOptions = new ArrayList<>(choices.size());
        for (CaptchaAnswer choice : choices) {
            frontEndOptions.add(choice.getObfuscatedName());
        }
        CaptchaFrontEndData frontendData = new CaptchaFrontEndData(
                validChoice.getName(), fieldName, frontEndOptions, audioFieldName);

        //resp.setContentType("application/json");
        //resp.getWriter().write(new GsonBuilder().create().toJson(frontendData));
        return frontendData;
    }

    /**
     * Get data for a particular image
     * @param req
     * @param resp
     * @param param
     * @throws IOException
     */
    public InputStream getImage(int index, boolean retina) {
        if (this.captchaSessionInfo == null) {
            throw new RuntimeException("Captcha not initialized, cannot return image");
        }
        List<CaptchaAnswer> answers = captchaSessionInfo.getChoices();
        if (answers != null && answers.size() > index) {
            // resp.setContentType("image/png");
            // writeImageResponse(answers.get(index), retina, resp);

            //return getImagePath(answers.get(index),retina);
            CaptchaAnswer ca = answers.get(index);
            return captchaRepository.getImageStream(ca.getPath());
        } else {
            throw new RuntimeException("Requested image for invalid index: "+ index);
        }
        // resp.sendError(400);
    }

    private void doAudio(HttpServletRequest req, HttpServletResponse resp, String param) throws IOException {
        /*
        CaptchaSessionInfo sessionInfo = getSessionInfo(req);
        if (sessionInfo == null) {
            resp.sendError(400);
            return;
        }

        String fileType = "mp3";
        MediaType contentType = MediaType.MPEG_AUDIO;

        if (param != null && "ogg".equals(param)) {
            fileType = "ogg";
            contentType = MediaType.OGG_AUDIO;
        }

        resp.setContentType(contentType.toString());
        // ByteStreams.copy(getServletContext().getResourceAsStream(getAudioPath(sessionInfo.getAudioAnswer(), fileType)), resp.getOutputStream());
        resp.getOutputStream().flush();
        */
    }

    /*
    private void writeImageResponse(CaptchaAnswer answer, boolean retina, HttpServletResponse resp) throws IOException {
        // ByteStreams.copy(getServletContext().getResourceAsStream(getImagePath(answer, retina)), resp.getOutputStream());
    }
    */

    private String hash(String somethingToHash, String salt) {
        return Hashing.md5().hashString((somethingToHash + salt), Charsets.UTF_8).toString();
    }


    private List<CaptchaAnswer> getRandomCaptchaOptions(int numberOfChoices, String salt) {
        List<CaptchaAnswer> options = new ArrayList<CaptchaAnswer>(captchaRepository.getImages());
        shuffle(options);
        List<CaptchaAnswer> choices = new ArrayList<>(numberOfChoices);
        for (CaptchaAnswer answer : options.subList(0, numberOfChoices)) {
            choices.add(new CaptchaAnswer(answer.getName(), hash(answer.getName(), salt), answer.getPath()));
        }
        shuffle(choices);
        return choices;
    }

    private CaptchaAnswer getRandomCaptchaAudio(String salt) {
        List<CaptchaAnswer> options = new ArrayList<CaptchaAnswer>(captchaRepository.getAudios());
        shuffle(captchaRepository.getAudios());
        return captchaRepository.getAudios().get(rand.nextInt(captchaRepository.getAudios().size()));
    }

    /*
    private String getImagePath(CaptchaAnswer answer, boolean retina) {
        String fileName = retina ? answer.getPath().replace(".png", "@2x.png") : answer.getPath();
        return "imagesPathWoBistDu/" + fileName;
    }

    private String getAudioPath(CaptchaAnswer answer, String fileType) {
        String path = answer.getPath();
        if (fileType.equals("ogg")) {
            path = path.replace(".mp3", ".ogg");
        }
        return "audioPathWoBistDu/"+ path;
    }
        */

    /*
    private CaptchaSessionInfo getSessionInfo(HttpServletRequest req) {
        return (CaptchaSessionInfo) req.getSession(true).getAttribute(CaptchaSessionInfo.class.getName());
    }

    private void setSessionInfo(HttpServletRequest req, CaptchaSessionInfo sessionInfo) {
        req.getSession(true).setAttribute(CaptchaSessionInfo.class.getName(), sessionInfo);
    }
    */
}