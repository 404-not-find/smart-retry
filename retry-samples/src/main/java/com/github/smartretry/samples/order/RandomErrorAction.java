package com.github.smartretry.samples.order;

import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author yuni[mn960mn@163.com]
 */
public class RandomErrorAction {

    private List<Integer> errorCodes = Arrays.asList(400, 401, 404, 405, 500, 502, 503, 504);

    private Integer successCode = 200;

    private List<Integer> totalCodes = new CopyOnWriteArrayList<>();

    private String baseUrl = "http://httpbin.org/status";

    private RestTemplate restTemplate = new RestTemplate();

    /**
     * 错误率
     */
    private int errorRatio;

    public RandomErrorAction(int errorRatio) {
        if (errorRatio < 0 || errorRatio > 100) {
            throw new IllegalArgumentException("errorRatio must be between 1 and 100");
        }
        this.errorRatio = errorRatio;
        initTotalCodes();
    }

    private void initTotalCodes() {
        for (int i = 1; i <= errorRatio; i++) {
            totalCodes.add(errorCodes.get(i % errorCodes.size()));
        }
        for (int i = 0; i < 100 - errorRatio; i++) {
            totalCodes.add(successCode);
        }
        Collections.shuffle(totalCodes);
    }

    /**
     * 无实际意义。一个执行起来会报错的方法
     */
    public void doAction() {
        Integer code = totalCodes.get(new Random().nextInt(100));
        restTemplate.getForObject(baseUrl + "/" + code, String.class);
    }
}
