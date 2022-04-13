package com.example.excel.person.service;

import com.example.excel.person.entity.Person;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface PersonService {

    List<String> getAllPersonNames();

    List<String> getAllPersonNamesWithTraditionalWay();

    List<Person> getAllPersons();

    Person findPersonWithDn(String dn);

    List<String> getPersonNamesByOrgId(String orgId);

    Map<String, Object> importLdap(MultipartFile multipartFile) throws IOException;
}
