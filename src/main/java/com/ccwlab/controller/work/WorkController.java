package com.ccwlab.controller.work;

import com.ccwlab.controller.message.MyProcessor;
import com.ccwlab.controller.message.WorkerProcessor;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.stereotype.Controller;

import java.io.Serializable;
import java.time.Instant;

@Controller
public class WorkController {
    Logger log = LoggerFactory.getLogger(WorkController.class);

    @StreamListener(MyProcessor.in)
    void onWorkRequested(WorkRequest request) {
        log.debug("received: "+request.toString());
//        throw new UnsupportedOperationException();
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

class WorkRequest implements Serializable {
    @JsonProperty("requested_time")
    Instant requestedTime;
    String repositoryName;
    String repositoryId;
    String commitId;
    String accessToken;

    public Instant getRequestedTime() {
        return requestedTime;
    }

    public void setRequestedTime(Instant requestedTime) {
        this.requestedTime = requestedTime;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public String getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(String repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Override
    public String toString() {
        return "WorkRequest{" +
                "requestedTime=" + requestedTime +
                ", repositoryName='" + repositoryName + '\'' +
                ", repositoryId='" + repositoryId + '\'' +
                ", commitId='" + commitId + '\'' +
                ", accessToken='" + accessToken + '\'' +
                '}';
    }
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