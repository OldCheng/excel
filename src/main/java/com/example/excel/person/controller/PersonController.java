package com.example.excel.person.controller;

import com.example.excel.person.entity.Person;
import com.example.excel.person.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.NamingException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/person")
public class PersonController {

    @Autowired
    PersonService personService;

    @GetMapping("/ldap")
    public List<Person> testLdap(@RequestParam List<String> mikstorenumbers) throws NamingException, IllegalAccessException, NoSuchFieldException {
        List<Person> allPersons = personService.getAllPersons();
        List<String> collect = allPersons.stream().map(Person::getEId).collect(Collectors.toList());
        Map<String,Integer> map = new HashMap<>();
        for (String s : collect) {
            Integer num = map.get(s);
            if (num != null) {
                map.put(s, num + 1);
            }else {
                map.put(s, 1);
            }
        }
        System.out.println(map.entrySet());
        System.out.println(allPersons.size());
        return allPersons;
    }

    @PostMapping(path="/import/ldap", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Map<String, Object> importLdap(@RequestParam("file") MultipartFile multipartFile ) throws IOException {
        return personService.importLdap(multipartFile);
    }

}
