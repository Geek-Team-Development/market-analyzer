package ru.manalyzer.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.manalyzer.persist.Request;

import java.util.List;

public interface RequestRepository extends MongoRepository<Request, String> {

    List<Request> findBySearchString(String searchString);

    List<Request> findByUserId(String userId);
}
