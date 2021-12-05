package com.ccwlab.controller.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface MyProcessor {
    String channel = "work";
    @Input(MyProcessor.channel)
    SubscribableChannel input();

    @Output(MyProcessor.channel)
    MessageChannel output();
}
