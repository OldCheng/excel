package com.example.excel.person.entity;


import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.Entry;

@Data
@Entry(objectClasses={"inetOrgPerson","msiPerson","organizationalPerson","person","top"})
public class Person {

    @Attribute(name = "employeenumber")
    private String eId;

    @Attribute(name = "uid")
    private String uId;

    @Attribute(name = "cn")
    private String fullName;

    @Attribute(name = "givenname")
    private String firstName;

    @Attribute(name = "sn")
    private String lastName;

    @Attribute(name = "mail")
    private String emailAddress;

    @Attribute(name = "mikcorpentitytype")
    private String workerType;

    @Attribute(name = "mikemplstatus")
    private String workerStatus;

    @Attribute(name = "mikjobfamily")
    private String jobFamily;

    @Attribute(name = "mikcorporglevel")
    private String managementLevel;

    @Attribute(name = "title")
    private String position;

    @Attribute(name = "employeetype")
    private String teamMemberType;

    @Attribute(name = "mikjobtitlenumber")
    private String jobCodeNumber;

    @Attribute(name = "mikstorenumber")
    private String storeNumber;

    @Attribute(name = "mikcostcenter")
    private String storeNumber1;

    @Attribute(name = "o")
    private String storeName;

}
