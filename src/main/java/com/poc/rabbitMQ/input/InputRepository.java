package com.poc.rabbitMQ.input;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
interface InputRepository extends MongoRepository<Input, String> {
}
