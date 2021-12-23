package com.ccwlab.controller.message;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface MyProcessor {
    String in = "m-to-c";
    String out = "c-to-m";
    @Input(MyProcessor.in)
    SubscribableChannel input();

    @Output(MyProcessor.out)
    MessageChannel output();
}
