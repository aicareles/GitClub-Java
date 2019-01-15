package com.jerry.geekdaily.domain;

import java.util.Date;

public class Question {
    private Integer questionId;
    private int period;//第几期
    private int questionType;//单选0或多选1
    private String questionTag;//蓝牙、aidl、service
    private int questionLevel;//简单、一般、难
    private int questionCategory;//java、android、ios
    private int answerTime;//答题时间
    private int score;//本题的分数
    private int chance;//机会
    private String questionContent;//问题内容
    private String chooseAnswerA;
    private String chooseAnswerB;
    private String chooseAnswerC;
    private String chooseAnswerD;
    private String tips;//提示
    private String realAnswer;//正确答案
    private String explain;//解释
    private Date createDate;

}
