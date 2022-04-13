package com.example.excel.person.dao;

import com.example.excel.person.entity.Person;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Repository;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

@Repository
public class PersonAttributesMapper implements AttributesMapper<Person> {
    @Override
    public Person mapFromAttributes(Attributes attr) throws NamingException {
        return convertObject(attr);
    }

    private Person convertObject(Attributes attr) throws NamingException {
        Person person = new Person();
        if (attr.get("mikstorenumber") != null) {
            person.setStoreNumber((String) attr.get("mikstorenumber").get());
        }
        if (attr.get("employeenumber") != null) {
            person.setEId((String) attr.get("employeenumber").get());
        }
        if (attr.get("uid") != null) {
            person.setUId((String) attr.get("uid").get());
        }
        if (attr.get("cn") != null) {
            person.setFullName((String) attr.get("cn").get());
        }
        if (attr.get("givenname") != null) {
            person.setFirstName((String) attr.get("givenname").get());
        }
        if (attr.get("sn") != null) {
            person.setLastName((String) attr.get("sn").get());
        }
        if (attr.get("mail") != null) {
            person.setEmailAddress((String) attr.get("mail").get());
        }
        if (attr.get("mikcorpentitytype") != null) {
            person.setWorkerType((String) attr.get("mikcorpentitytype").get());
        }
        if (attr.get("mikemplstatus") != null) {
            person.setWorkerStatus((String) attr.get("mikemplstatus").get());
        }
        if (attr.get("mikjobfamily") != null) {
            person.setJobFamily((String) attr.get("mikjobfamily").get());
        }
        if (attr.get("mikcorporglevel") != null) {
            person.setManagementLevel((String) attr.get("mikcorporglevel").get());
        }
        if (attr.get("title") != null) {
            person.setPosition((String) attr.get("title").get());
        }
        if (attr.get("employeetype") != null) {
            person.setTeamMemberType((String) attr.get("employeetype").get());
        }
        if (attr.get("mikjobtitlenumber") != null) {
            person.setJobCodeNumber((String) attr.get("mikjobtitlenumber").get());
        }
        if (attr.get("mikcostcenter") != null) {
            person.setStoreNumber1((String) attr.get("mikcostcenter").get());
        }
        if (attr.get("o") != null) {
            person.setStoreName((String) attr.get("o").get());
        }
        return person;
    }

}
