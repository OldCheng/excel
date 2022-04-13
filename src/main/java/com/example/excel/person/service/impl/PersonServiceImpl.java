package com.example.excel.person.service.impl;

import com.example.excel.person.dao.PersonAttributesMapper;
import com.example.excel.person.entity.Person;
import com.example.excel.person.service.PersonService;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Context;
import javax.naming.NameNotFoundException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.ldap.query.LdapQueryBuilder.query;


@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    LdapTemplate ldapTemplate;


    /**
     * 查询部分字段集合
     * @return
     */
    @Override
    public List<String> getAllPersonNames() {
        return ldapTemplate.search(
                query().where("objectclass").is("person"), (AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());
    }

    /**
     * 传统LDAP查询方式
     * @return
     */
    @Override
    public List<String> getAllPersonNamesWithTraditionalWay() {
        Hashtable env = new Hashtable();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://isamldap.michaels.com");
        env.put(Context.SECURITY_PRINCIPAL, "uid=jidong,ou=empl,ou=users,o=msi");
        env.put(Context.SECURITY_CREDENTIALS, "Fff.1992");
        DirContext ctx;
        try {
            ctx = new InitialDirContext(env);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }

        List<String> list = new LinkedList<String>();
        NamingEnumeration results = null;
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            results = ctx.search("", "(objectclass=person)", controls);
            while (results.hasMore()) {
                SearchResult searchResult = (SearchResult) results.next();
                Attributes attributes = searchResult.getAttributes();
                Attribute attr = attributes.get("cn");
                String cn = attr.get().toString();
                list.add(cn);
            }
        } catch (NameNotFoundException e) {
            // The base context was not found.
            // Just clean up and exit.
        } catch (NamingException e) {
            //throw new RuntimeException(e);
        } finally {
            if (results != null) {
                try {
                    results.close();
                } catch (Exception e) {
                    // Never mind this.
                }
            }
            if (ctx != null) {
                try {
                    ctx.close();
                } catch (Exception e) {
                    // Never mind this.
                }
            }
        }
        return list;
    }

    /**
     * 查询对象映射集合
     * @return
     */
    @Override
    public List<Person> getAllPersons() {

//        String filter1 = "( &(mikemplstatus=ACTIVE) (|(mikstorenumber=6707)(mikcostcenter=6707)) )";
//        String filter = "(&(mikemplstatus=ACTIVE)(|(mikcostcenter=6707)(mikstorenumber=6707)))";
        List<Person> persons = new ArrayList<>();
        List<String> conditions = new ArrayList<>();
        conditions.add("9966");
        conditions.add("3740");
        conditions.add("6707");
        conditions.add("6703");
        conditions.add("3008");
        conditions.add("5035");
        conditions.add("8667");
        conditions.add("3048");
        for (String condition : conditions) {
            persons.addAll(getPersonList(condition));
        }
        return persons;

//        return ldapTemplate.search(
//                query()
//                .where("mikstorenumber").is("6707")
//                        .or("mikcostcenter").is("6707")
//                .and("mikemplstatus").is("ACTIVE")
//                , new PersonAttributesMapper()
//        );
    }

    public List<Person> getPersonList(String storeNum){
        String filter = "(&(mikemplstatus=ACTIVE)(|(mikcostcenter="+storeNum+")(mikstorenumber="+storeNum+"+)))";
        return ldapTemplate.search("", filter, new PersonAttributesMapper());
    }

    /**
     * 根据DN查询指定人员信息
     * @param dn
     * @return
     */
    @Override
    public Person findPersonWithDn(String dn) {
        return ldapTemplate.lookup(dn, new PersonAttributesMapper());
    }

    /**
     * 组装查询语句
     * @param orgId
     * @return
     */
    @Override
    public List<String> getPersonNamesByOrgId(String orgId) {
        LdapQuery query = query()
                .base("ou=person,dc=coreservice")
                .attributes("cn", "sn")
                .where("objectclass").is("person")
                .and("orgId").is(orgId);
        return ldapTemplate.search(query,(AttributesMapper<String>) attrs -> (String) attrs.get("cn").get());
    }

    @Override
    public Map<String, Object> importLdap(MultipartFile multipartFile) throws IOException {
        XSSFWorkbook workbook = new XSSFWorkbook(multipartFile.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        int rowCount = sheet.getPhysicalNumberOfRows();
        List<String> excelDataEIdList = new ArrayList<>();
        for(int i = 1; i < rowCount; i++){
            Row row = sheet.getRow(i);
            Cell cell0 = row.getCell(0);
            Cell cell16 = row.getCell(16);
            cell0.setCellType(CellType.STRING);
            cell16.setCellType(CellType.STRING);
            String eid = cell0.getStringCellValue();
            String storeNum = cell16.getStringCellValue();
            List<String> list = getList();
            if (list.contains(storeNum)) {
                excelDataEIdList.add("E"+eid);
            }
        }
        Map<String, Object> resultDataMap = new HashMap<>();
        resultDataMap.put("fileCount",excelDataEIdList.size());

        List<Person> allPersons = getAllPersons();
        List<String> ldapDataEIdList = allPersons.stream().map(Person::getEId).collect(Collectors.toList());
        List<String> noDataEIdInFileList = new ArrayList<>();
        ldapDataEIdList.forEach(eId ->{
            if (!excelDataEIdList.contains(eId)) {
                noDataEIdInFileList.add(eId);
            }
        });
        resultDataMap.put("ldapCount", ldapDataEIdList.size());
        resultDataMap.put("ldapDataNotInFileData", noDataEIdInFileList);

        Map<String,Integer> aMap = new HashMap<>();
        for (String s : ldapDataEIdList) {
            Integer num = aMap.get(s);
            if (num != null) {
                aMap.put(s, num + 1);
            }else {
                aMap.put(s, 1);
            }
        }

        Map<String, Integer> repetitionMap = new HashMap();
        Set<Map.Entry<String, Integer>> entries = aMap.entrySet();
        for (Map.Entry<String, Integer> entry : entries) {
             if(entry.getValue() > 1){
                 repetitionMap.put(entry.getKey(), entry.getValue());
             }
        }
        resultDataMap.put("ldapRepetition", repetitionMap);
        return resultDataMap;
    }

    public List<String> getList(){
        List<String> conditions = new ArrayList<>();
        conditions.add("9966");
        conditions.add("3740");
        conditions.add("6707");
        conditions.add("6703");
        conditions.add("3008");
        conditions.add("5035");
        conditions.add("8667");
        conditions.add("3048");
        return conditions;
    }
}
