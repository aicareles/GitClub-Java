package com.jerry.geekdaily.domain;

import java.util.Date;

public class Paper {
    private Integer paperId;
    private int period;//第几期
    private int sumTime;//总用时
    private int userId;
    private int totalScore;//总分数
    private int currentScore;//当前得分
    private int totalQuestion;//总题目数
    private int correctQuestion;//答对题目数
    private int errorQuestion;//答错题目数
    private String summary;//评语/总结/结论  您的能力很强，再接再厉
    private int rank;//等级  青铜、白银、黄金、铂金、钻石、王者
    private Date createDate;
}
