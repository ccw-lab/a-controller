package com.ccwlab.controller.work.persistent;

import com.ccwlab.controller.work.Work;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

public interface WorkRepository extends MongoRepository<Work, Long> {

}
