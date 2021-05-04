package com.digitalwatch.watch;

// SQLite에서 사용하기위해 선언
public class SettingDTO {
    int seqno;
    int color;
    int textSize;

    public SettingDTO(int seqno, int color, int textSize) {
        this.seqno = seqno;
        this.color = color;
        this.textSize = textSize;
    }

    public int getSeqno() {
        return seqno;
    }

    public void setSeqno(int seqno) {
        this.seqno = seqno;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }
}
