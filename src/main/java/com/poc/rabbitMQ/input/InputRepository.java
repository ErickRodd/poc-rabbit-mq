package com.poc.rabbitMQ.input;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
interface InputRepository extends JpaRepository<Input, Long> {
}
