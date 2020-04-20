package com.poc.rabbitMQ.motocicleta;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotocicletaRepository extends MongoRepository<Motocicleta, String> {
}
