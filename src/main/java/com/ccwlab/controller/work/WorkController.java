package com.ccwlab.controller.work;

import com.ccwlab.controller.message.MyProcessor;
import com.ccwlab.controller.message.WorkerProcessor;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.cloud.stream.annotation.StreamListener;

import java.time.Instant;

public class WorkController {
    @StreamListener(MyProcessor.channel)
    void onWorkRequested(WorkRequest request) {
        throw new UnsupportedOperationException();
    }

    @StreamListener(WorkerProcessor.channel)
    void onTooBusyWorker(TooBusyWorker worker){
        throw new UnsupportedOperationException();
    }

    @StreamListener(WorkerProcessor.channel)
    void onStarted(StartedWork work){
        throw new UnsupportedOperationException();
    }

    @StreamListener(WorkerProcessor.channel)
    void onHeartbeat(HeartBeat beat){

    }
}

class WorkRequest{
    @JsonProperty("requested_time")
    Instant requestedTime;
    String repositoryName;
    String repositoryId;
    String commitId;
    String accessToken;
}

class TooBusyWorker{
    int freeMemory;
    int cpu;
    Instant reportedTime;
}

class StartedWork {
    Instant startedTime;
}

class HeartBeat{
    Instant createdTime;
}