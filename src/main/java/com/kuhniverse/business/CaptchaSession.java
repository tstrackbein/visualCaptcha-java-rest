package com.kuhniverse.business;

import com.kuhniverse.domain.CaptchaAnswer;
import com.kuhniverse.domain.CaptchaFrontEndData;
import com.kuhniverse.domain.CaptchaSessionInfo;
import com.kuhniverse.integration.CaptchaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static java.util.Collections.shuffle;

@Component
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS, value = "session")
public class CaptchaSession implements Serializable {

    private Logger log = LoggerFactory.getLogger(CaptchaSession.class);

    private Random rand = new Random();
    private CaptchaSessionInfo captchaSessionInfo;

    @Inject
    private CaptchaRepository captchaRepository;

    /**
     * Init captchas with a size of optionCount
     *
     * @param optionCount
     * @return
     */
    public CaptchaFrontEndData start(int optionCount) {

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
        log.debug("Capture session data initialized with {} values", optionCount);
        return frontendData;
    }

    /**
     * Get data for a particular image
     */
    public InputStream getImage(int index, boolean retina) {
        if (this.captchaSessionInfo == null) {
            throw new RuntimeException("Captcha not initialized, cannot return image");
        }
        List<CaptchaAnswer> answers = captchaSessionInfo.getChoices();
        if (answers != null && answers.size() > index) {
            CaptchaAnswer ca = answers.get(index);
            log.debug("Serving image {}", ca);
            return captchaRepository.getImageStream(ca.getPath());
        } else {
            throw new RuntimeException("Requested image for invalid index: " + index);
        }
    }

    /**
     * Get data for a particular audio file
     */
    public InputStream getAudio(int index, String fileType) {
        /*
        String fileType = "mp3";
        MediaType contentType = MediaType.MPEG_AUDIO;

        if (param != null && "ogg".equals(param)) {
            fileType = "ogg";
            contentType = MediaType.OGG_AUDIO;
        }
        */

        if (this.captchaSessionInfo == null) {
            throw new RuntimeException("Captcha not initialized, cannot return image");
        }
        List<CaptchaAnswer> answers = captchaSessionInfo.getChoices();
        if (answers != null && answers.size() > index) {
            CaptchaAnswer ca = answers.get(index);
            log.debug("Serving audio {}", ca);
            return captchaRepository.getAudtioStream(ca.getPath());
        } else {
            throw new RuntimeException("Requested image for invalid index: " + index);
        }
    }

    /**
     * Validate Solution
     */
    public boolean isValid(Map<String, String> params) {
        // String fieldName, String obfuscatedChoiceName
        if (this.captchaSessionInfo == null) {
            throw new RuntimeException("Captcha not initialized, cannot validate");
        }
        String expectedFieldName = captchaSessionInfo.getFieldName();
        if (!params.containsKey(expectedFieldName)) {
            log.warn("Invalid response, fieldname {} not found in params (size={})", expectedFieldName, params.size());
            return false;
        }
        String obfuscatedChoiceName = params.get(expectedFieldName);

        if (!captchaSessionInfo.getValidChoice().equals(obfuscatedChoiceName)) {
            log.warn("Invalid response, choice {} does not match valid choice {}", obfuscatedChoiceName, captchaSessionInfo.getValidChoice());
            return false;
        }
        log.debug("Captcha successfully verified ({}={})", expectedFieldName, obfuscatedChoiceName);
        return true;
    }

    /**
     * Invalidates current Session Info
     */

    public void invalidate() {
        this.captchaSessionInfo = null;
        log.debug("captchaSessionInfo invalidated");
    }


    private String hash(String somethingToHash, String salt) {
        // return Hashing.md5().hashString((somethingToHash + salt), Charsets.UTF_8).toString();
        String toHash = somethingToHash + salt;
        return DigestUtils.md5DigestAsHex(toHash.getBytes());
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

}