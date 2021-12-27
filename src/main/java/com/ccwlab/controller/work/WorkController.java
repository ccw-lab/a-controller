package com.ccwlab.controller.work;

import com.ccwlab.controller.GithubUtil;
import com.ccwlab.controller.JwtUtil;
import com.ccwlab.controller.service.SequenceGeneratorService;
import com.ccwlab.controller.work.persistent.WorkRepository;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.function.Consumer;

@RestController
public class WorkController {
    Logger log = LoggerFactory.getLogger(WorkController.class);

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    GithubUtil githubUtil;

    @Autowired
    WorkRepository workRepository;

    @Autowired
    SequenceGeneratorService sequenceGeneratorService;

//    @Autowired
//    WorkerOutputProcessor workerOutputProcessor;

    @Autowired
    StreamBridge streamBridge;

    @PostMapping("works")
    @Operation(description="Request a new CI/CD work.")
    @ApiResponse(responseCode = "202", description = "Return a id and run asynchronous operation.")
    @ApiResponse(responseCode = "500", description = "something is wrong.")
    ResponseEntity<Long> requestWork(@Parameter(description= " The request information") @RequestBody WorkRequest request){

        //slow codes for resiliency testing
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        var accessToken = request.accessToken;
        var github = githubUtil.get(accessToken);
        try {
            var myself = github.getMyself();
            var repository = myself.getRepository(request.repositoryName);
            var commit = repository.getCommit(request.getCommitId());
            var work = new Work(sequenceGeneratorService.generateSequence(Work.SEQUENCE_NAME), request.repositoryName, request.repositoryId, request.commitId, commit.getCommitShortInfo().getMessage(), repository.getOwner().getId(),
                    repository.getOwnerName(), -1, Collections.emptyList(), WorkStatus.WAIT_FOR_WORKER, accessToken);
            var s = this.workRepository.insert(work);
            this.streamBridge.send("sendToWorker-out-0", work);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(s.getId());
        }catch(IOException e){
            log.debug("ex", e);
            return ResponseEntity.internalServerError().build();
        }
    }

//    @PutMapping("/works/{workId}")
//    ResponseEntity<Long> updateWork(@PathVariable long workId, @RequestBody StopWork request){
//        var accessToken = request.accessToken;
//        var github = githubUtil.get(accessToken);
//        if(request.operation == StopOperation.STOP){
//            var workOnProgressOption = this.workRepository.findById(workId);
//            if(workOnProgressOption.isPresent()){
//                try {
//                    var workOnProgress = workOnProgressOption.get();
//                    var repo = github.getRepositoryById(workOnProgress.getRepositoryId());
//                    if (repo.getOwner().getId() == workOnProgress.getOwnerId()) {
//                        workOnProgress.status = WorkStatus.STOPPED;
//                        this.workRepository.save(workOnProgress);
//                        return ResponseEntity.ok().build();
//                    } else {
//                        return ResponseEntity.badRequest().build();
//                    }
//                }catch(IOException ex){
//                    log.debug("ex", ex);
//                    return ResponseEntity.badRequest().build();
//                }
//            }
//            return ResponseEntity.badRequest().build();
//        }else{
//            return ResponseEntity.badRequest().build();
//        }
//    }

    @Operation(description = "Anyone can see progress on a work.")
    @ApiResponse(responseCode = "200", description = "return a work object.")
    @ApiResponse(responseCode = "400", description = "The wrong information")
    @GetMapping("/works/{workId}")
    ResponseEntity<Work> getProgress(@Parameter(description = "A work id which you find a work object with.") @PathVariable long workId){
        var work = this.workRepository.findById(workId);
        if(work.isPresent()) {
            work.get().setAccessToken(null);
            return ResponseEntity.ok(work.get());
        }else{
            log.warn("A work object was requested by a unknown workId " + workId + ".");
            return ResponseEntity.badRequest().build();
        }
    }

//    @Bean
//    Function<Work, Work> sendToWorker(){
//        return (Work work) -> work;
//    }

    @Bean
    Consumer<Report> reportFromWorker(){
        return report -> {
            log.debug(report.toString());
            var workOptional = this.workRepository.findById(report.workId);
            if(workOptional.isPresent()){
                var work = workOptional.get();
                if(report.result == Result.SUCCESS){
                    work.setStatus(WorkStatus.COMPLETED);
                    this.workRepository.save(work);
                    this.streamBridge.send("reportToMain-out-0", new MainReport(report.workId, MainReportStatus.COMPLETED, Instant.now()));
                }else if(report.result == Result.FAILED){
                    work.setStatus(WorkStatus.ERROR);
                    //other worker?
                    this.workRepository.save(work);
                    this.streamBridge.send("reportToMain-out-0", new MainReport(report.workId, MainReportStatus.FAILED, Instant.now()));
                }else{
                    work.setStatus(WorkStatus.PROGRESS_BY_WORKER);
                    work.logs.add(report.message);
                    this.workRepository.save(work);
                    this.streamBridge.send("reportToMain-out-0", new MainReport(report.workId, MainReportStatus.STARTED, Instant.now()));
                }
            }else{
                log.warn("A unknown job has been reported from the worker. workId: " + report.workId);
            }

        };
    }

//    @StreamListener(MyProcessor.in)
//    void onWorkRequested(WorkRequest request) {
//        log.debug("received: "+request.toString());
//    }

}

class WorkRequest implements Serializable {
    @JsonProperty("requested_time")
    Instant requestedTime;
    @JsonProperty("repository_name")
    String repositoryName;
    @JsonProperty("repository_id")
    long repositoryId;
    @JsonProperty("commit_id")
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

class StartedWork{

}

class TooBusyWorker{
    int freeMemory;
    int cpu;
    Instant reportedTime;
}

class StopWork {
    StopOperation operation;
    String accessToken;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public StopOperation getOperation() {
        return operation;
    }

    public void setOperation(StopOperation operation) {
        this.operation = operation;
    }
}
enum StopOperation{
    STOP
}
class HeartBeat{
    Instant createdTime;
}

class Command{
    String name;
    Instant time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getTime() {
        return time;
    }

    public Command(String name, Instant time) {
        this.name = name;
        this.time = time;
    }

    public void setTime(Instant time) {
        this.time = time;
    }
}
class Report {
    Result result;
    long workId;
    String message;

    public Report(Result result, long workId, String message) {
        this.result = result;
        this.workId = workId;
        this.message = message;
    }

    public long getWorkId() {
        return workId;
    }

    public void setWorkId(long workId) {
        this.workId = workId;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "Report{" +
                "result=" + result +
                ", workId=" + workId +
                ", message='" + message + '\'' +
                '}';
    }
}

enum Result{
    FAILED,
    PROGRESS,
    SUCCESS
}

class MainReport {
    long workId;
    MainReportStatus status;
    Instant createdTime;

    public MainReport(long workId, MainReportStatus status, Instant createdTime) {
        this.workId = workId;
        this.status = status;
        this.createdTime = createdTime;
    }

    public long getWorkId() {
        return workId;
    }

    public void setWorkId(long workId) {
        this.workId = workId;
    }

    public MainReportStatus getStatus() {
        return status;
    }

    public void setStatus(MainReportStatus status) {
        this.status = status;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }
}

enum MainReportStatus{
    STARTED,
    COMPLETED,
    FAILED
}