package com.ccwlab.controller.work;

import com.ccwlab.controller.message.MyProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkService {
    @Autowired
    MyProcessor myProcessor;

    void requestWorkToWorker(){
        throw new UnsupportedOperationException();
    }

    void updateWorkerList(){
        throw new UnsupportedOperationException();
    }
}
