package com.openkg.openbase.model;

import com.openkg.openbase.common.Singleton;

import java.util.List;
import java.util.Map;

public class ReviewQA {
    private List<Map<String, String>> questionList;
    private List<Map<String, String>> answerList;

    public List<Map<String, String>> getQuestionList(){
        return questionList;
    }

    public List<Map<String, String>> getAnswerList(){
        return answerList;
    }

    public void setQuestionList(List<Map<String, String>> qList){
        this.questionList = qList;
        return;
    }

    public void setAnswerList(List<Map<String, String>> qList){
        this.answerList = qList;
        return;
    }
}
