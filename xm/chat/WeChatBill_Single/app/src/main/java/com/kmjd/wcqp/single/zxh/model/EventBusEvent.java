package com.kmjd.wcqp.single.zxh.model;

/**
 * Created by Android-Star on 2017/10/30.
 */

public class EventBusEvent {
    public String getMessageEvent() {
        return messageEvent;
    }

    private String messageEvent = null;

    public EventBusEvent(String messageEvent) {
        this.messageEvent = messageEvent;
    }

}
