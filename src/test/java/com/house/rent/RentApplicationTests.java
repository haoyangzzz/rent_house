package com.house.rent;

//import com.house.rent.mapper.HouseMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.house.rent.mapper.SamTaskMapper;
import com.house.rent.mapper.UserMapper;
import com.house.rent.mapper.HouseMapper;
import com.house.rent.model.House;
import com.house.rent.model.User;
import com.house.rent.model.ZsResponse;
import com.house.rent.utils.SpringUtils;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.stereotype.Component;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.thymeleaf.spring5.context.SpringContextUtils;

import javax.security.auth.callback.Callback;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class RentApplicationTests {
    @Autowired
    UserMapper userMapper;
    @Autowired
    JavaMailSenderImpl mailSender;

    @Autowired
    HouseMapper houseMapper;
    @Autowired
    StringRedisTemplate template;
    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RestHighLevelClient restHighLevelClient;


    @Test
//    @Transactional(Callback=Exception.class)
    void esTest() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("house_index");
        restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
        transactionTemplate.execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
//                houseMapper.updateHouseInfo(house);
                ExecutorService executorService = Executors.newCachedThreadPool();
                return null;
            }
        });
    }
    @Test
    void esTestDoc() throws IOException {
        IndexRequest indexRequest = new IndexRequest("yh_index");
        User user = new User();
        user.setUsername("yh");
        user.setPassword("123");
        indexRequest.id("1");
        indexRequest.timeout(TimeValue.timeValueSeconds(1));
        indexRequest.source(new ObjectMapper().writeValueAsString(user), XContentType.JSON);
        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }
    @Test
    void esInsertMysqlData() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        List<House> allHouse = houseMapper.findAllHouse();
        for (int i=0;i<allHouse.size();i++) {
            bulkRequest.add(new IndexRequest("house_index")
                                .id(""+(i+1))
                                .source(new ObjectMapper().writeValueAsString(allHouse.get(i)), XContentType.JSON)
            );
        }
        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
    }
    @Test
    void esTestSearch() throws IOException {
        SearchRequest searchRequest = new SearchRequest("house_index");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        sourceBuilder.from(1)
//                .size(10);
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "哗");
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60,TimeUnit.SECONDS));
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] hits = searchResponse.getHits().getHits();
        System.out.println("===========================================");
        for (SearchHit hit : hits) {
            System.out.println(hit.getSourceAsMap());
        }

    }
    @Autowired
    ApplicationContext applicationContext;
    @Test
    @Ignore
    void AnnotationTest() {
//        ApplicationContext applicationContext = SpringUtils.getApplicationContext();
        Map<String, Object> beansWithAnnotation =
                applicationContext.getBeansWithAnnotation(Component.class);
        for (Map.Entry<String, Object> s : beansWithAnnotation.entrySet()) {
            System.out.println(s.getValue());
//            System.out.println(beansWithAnnotation.get(s).getClass());
        }

    }

    @Test
    @Ignore
    void contextLoads() {
        User user = new User();
        user.setPname("张三");
        int i = userMapper.addUser(user);
        System.out.println(i);
    }
    @Test
    @Ignore
    void testHouse(){
//        House house = new House();
//        house.setName("aaa");
//        Integer integer = houseMapper.addHouse(house);
//        System.out.println(integer);

    }
    @Test
    void testBeanUtils(){
        House house = new House();
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(house.getClass());
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            String name = propertyDescriptor.getName();
            System.out.println(name);
        }

    }
    @Test
    void mailTest(){
        //发送简单的邮件
        SimpleMailMessage message=new SimpleMailMessage();
        message.setSubject("!!!");
        message.setText("<b style='color:blue'>有内鬼，终止交易</b>");
        message.setFrom("798012737@qq.com");
        message.setTo("wsyh961215@163.com");
        mailSender.send(message);
    }
    @Test
    @Ignore
    void redisTest(){
        template.opsForValue().set("yh", "hy", 300, TimeUnit.SECONDS);
//        template.opsForHash().put("yh", "yh", "yh");
    }
    @Test
    @Ignore
    void delredisTest(){
//        template.opsForValue().getOperations().delete("yh");
        System.out.println(template.hasKey("yh"));
    }

    @Autowired
    SamTaskMapper samTaskMapper;
    @Test
    void test(){
        List<ZsResponse> zsResponses = samTaskMapper.find();
        for (ZsResponse zsRespons : zsResponses) {
            System.out.println(zsRespons);
        }
    }
}
