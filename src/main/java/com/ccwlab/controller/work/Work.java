package com.ccwlab.controller.work;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("work")
public class Work {

    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    @Id
    long id;
    @JsonProperty("repository_name")
    String repositoryName;
    @JsonProperty("repository_id")
    long repositoryId;
    @JsonProperty("commit_id")
    String commitId;
    @JsonProperty("commit_message")
    String commitMessage;
    @JsonProperty("owner_id")
    long ownerId;
    @JsonProperty("owner_name")
    String ownerName;
    @JsonProperty("runner_id")
    long runnerId;
    List<String> logs;
    WorkStatus status;
    String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getRepositoryName() {
        return repositoryName;
    }

    public void setRepositoryName(String repositoryName) {
        this.repositoryName = repositoryName;
    }

    public long getRepositoryId() {
        return repositoryId;
    }

    public void setRepositoryId(long repositoryId) {
        this.repositoryId = repositoryId;
    }

    public String getCommitId() {
        return commitId;
    }

    public void setCommitId(String commitId) {
        this.commitId = commitId;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public long getRunnerId() {
        return runnerId;
    }

    public void setRunnerId(long runnerId) {
        this.runnerId = runnerId;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public WorkStatus getStatus() {
        return status;
    }

    public void setStatus(WorkStatus status) {
        this.status = status;
    }

    public Work() {
    }

    public Work(long id, String repositoryName, long repositoryId, String commitId, String commitMessage, long ownerId, String ownerName, long runnerId, List<String> logs, WorkStatus status, String accessToken) {
        this.id = id;
        this.repositoryName = repositoryName;
        this.repositoryId = repositoryId;
        this.commitId = commitId;
        this.commitMessage = commitMessage;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
        this.runnerId = runnerId;
        this.logs = logs;
        this.status = status;
        this.accessToken = accessToken;
    }
}

enum WorkStatus{
    WAIT_FOR_WORKER,
    PROGRESS_BY_WORKER,
    STOPPED,
    COMPLETED,
    ERROR
}
