package org.five_nights_at_dana.AI;

public enum Question {
    Q1("",""),
    Q2("",""),
    Q3("",""),
    Q4("",""),
    Q5("",""),
    Q6("",""),
    Q7("",""),
    Q8("","");

    private final String question;
    private final String answer;

    Question(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    public String getQuestion() {
        return question;
    }

    @Override
    public String toString() {
        return "Question{" +
                "question='" + question + '\'' +
                '}';
    }
}
