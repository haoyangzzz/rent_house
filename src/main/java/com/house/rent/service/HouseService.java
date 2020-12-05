package com.house.rent.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.house.rent.DTO.HouseDTO;
import com.house.rent.DTO.RespPageBean;
import com.house.rent.lock.redisLock;
import com.house.rent.mapper.HouseMapper;
import com.house.rent.mapper.UserMapper;
import com.house.rent.model.House;
import com.house.rent.model.Message;
import com.house.rent.model.Order;
import com.house.rent.model.User;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.swing.text.Highlighter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class HouseService {

    @Autowired
    private HouseMapper houseMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private redisLock redisLock;
    @Autowired
    private RestHighLevelClient restHighLevelClient;

    public Integer addHouse(HouseDTO houseDTO) throws Exception {
        House house = new House();
        house.setName(houseDTO.getName());
        house.setAddressCity(houseDTO.getCity().get(0));
        house.setAddressQx(houseDTO.getCity().get(1));
        house.setAddressJd(houseDTO.getCity().get(2));
        house.setMoney(houseDTO.getMoney());
        house.setArea(houseDTO.getArea());
        house.setXqname(houseDTO.getXqname());
        house.setHousemodel(houseDTO.getHousemodel());
        house.setCzfs(houseDTO.getCzfs());
        house.setCx(houseDTO.getCx());
        if (houseDTO.getTag().size()!=0){
            house.setTag(houseDTO.getTag().get(0));
        }else{
            house.setTag("");
        }
        house.setPtChuang(houseDTO.getFwpt().contains("床")?1:0);
        house.setPtBx(houseDTO.getFwpt().contains("冰箱")?1:0);
        house.setPtKt(houseDTO.getFwpt().contains("空调")?1:0);
        house.setPtKzf(houseDTO.getFwpt().contains("可做饭")?1:0);
        house.setPtXyj(houseDTO.getFwpt().contains("洗衣机")?1:0);
        house.setPtWsj(houseDTO.getFwpt().contains("卫生间")?1:0);
        house.setDt(houseDTO.getDt());
        house.setPicture(houseDTO.getPicture());
        house.setPublishTime(new Date());
        house.setPid(houseDTO.getPid());
        house.setState(0);
//        log.info();

        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("10s");
        bulkRequest.add(new IndexRequest("house_index")
                    .source(new ObjectMapper().writeValueAsString(house), XContentType.JSON));


        restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);
        return houseMapper.addHouse(house);
    }

    @Cacheable(value = "house",key = "1")
    public RespPageBean getHouse(Integer page, Integer size, House house,int[] moneyqj,String[] pxList) {
        System.out.println("没缓存，走这里看看");
        log.info("进行全表查询");
        if (page != null && size != null) {
            page = (page - 1) * size;
        }
        List<House> houses = houseMapper.findHouse(page, size, house,moneyqj,pxList);
        Long total = houseMapper.getTotal(house,moneyqj);
        RespPageBean bean = new RespPageBean();
        bean.setData(houses);
        bean.setTotal(total);
        return bean;
    }
    //es方式查房
    public List<Map<String,Object>> getHouseByEs(String keyWords,int pageNo,int pageSize) throws IOException {
//        if (pageNo <=1){pageNo = 1;}
        SearchRequest searchRequest = new SearchRequest("house_index");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.from(pageNo)
                     .size(pageSize);
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", keyWords);
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(keyWords, "name", "xqname");
        sourceBuilder.query(multiMatchQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("name");
        highlightBuilder.field("xqname");
//        highlightBuilder.requireFieldMatch(false);
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        ArrayList<Map<String,Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            if (name != null) {
                Text[] fragments = name.fragments();
                String newName = "";
                for (Text fragment : fragments) {
                    newName += fragment;
                }
                sourceAsMap.put("name", newName);
            }
            list.add(sourceAsMap);
        }
        return list;
    }

    public Integer orderHouse(Order order){
        //TODO 查一下 如果预定状态为卖出就返回已卖出
        String value = UUID.randomUUID().toString();
        Boolean lock = redisLock.lock(String.valueOf(order.getHid()), value);
        if (lock){
            houseMapper.addorder(order);
            redisLock.unlock(String.valueOf(order.getHid()), value);
            return 1;
        }else {
//            new Thread().start();
            log.error("锁获取失败");
            return orderHouse(order);
        }
    }

    public Integer updateHouseState(Integer hid,Integer state) {
        return houseMapper.updateHouse(hid,state);
    }

    @CachePut(value = "house",key = "1")
    public HouseDTO updateHouse(HouseDTO houseDTO) {
        House house = new House();
        house.setName(houseDTO.getName());
        house.setAddressCity(houseDTO.getCity().get(0));
        house.setAddressQx(houseDTO.getCity().get(1));
        house.setAddressJd(houseDTO.getCity().get(2));
        house.setMoney(houseDTO.getMoney());
        house.setArea(houseDTO.getArea());
        house.setXqname(houseDTO.getXqname());
        house.setHousemodel(houseDTO.getHousemodel());
        house.setCzfs(houseDTO.getCzfs());
        house.setCx(houseDTO.getCx());
        if (houseDTO.getTag().size()!=0){
            house.setTag(houseDTO.getTag().get(0));
        }else{
            house.setTag("");
        }
        house.setPtChuang(houseDTO.getFwpt().contains("床")?1:0);
        house.setPtBx(houseDTO.getFwpt().contains("冰箱")?1:0);
        house.setPtKt(houseDTO.getFwpt().contains("空调")?1:0);
        house.setPtKzf(houseDTO.getFwpt().contains("可做饭")?1:0);
        house.setPtXyj(houseDTO.getFwpt().contains("洗衣机")?1:0);
        house.setPtWsj(houseDTO.getFwpt().contains("卫生间")?1:0);
        house.setDt(houseDTO.getDt());
        house.setPicture(houseDTO.getPicture());
        house.setPublishTime(new Date());
        house.setPid(houseDTO.getPid());
        house.setHid(houseDTO.getHid());
        house.setState(houseDTO.getState());
//        System.out.println(house);
        return houseDTO;
    }

    public void insertMessage(Order order) {
        Message message = new Message();
        message.setPid(order.getUid());
        message.setMessagebody(order.getHid().toString()+"被预定");
        message.setPtime(new Date());
        message.setRid(houseMapper.findPidByHid(order.getHid()));
        message.setState(0);
        userMapper.insertMessage(message);
    }
    public void insertMessageForAng(House house) {
        Message message = new Message();
        message.setPid(house.getPid());
        if (house.getState() == 2){
            message.setMessagebody(house.getHid().toString()+"已经预定成功");
        }else {
            message.setMessagebody(house.getHid().toString()+"发布人取消订单，预定失败");
        }
        message.setPtime(new Date());
        message.setRid(userMapper.findRidByHid(house.getHid()));
        message.setState(0);
        userMapper.insertMessage(message);
    }
}
