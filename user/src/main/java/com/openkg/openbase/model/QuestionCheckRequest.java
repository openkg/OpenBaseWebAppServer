package com.openkg.openbase.model;

import java.util.List;
import java.util.Map;

/**
 * Created by mi on 18-10-9.
 */
public class QuestionCheckRequest {
    String token;
    List<Map> answer;

    public String getToken(){return token;}
    public List<Map> getAnswer(){return answer;}
}
