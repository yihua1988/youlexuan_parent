package com.offcn.demo;

import com.offcn.pojo.TbItem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.TermsOptions;
import org.springframework.data.solr.core.query.result.ScoredPage;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangjian
 * @email 13120082225@163.com
 * @date 2020/1/17
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:spring-solr.xml")
public class TestTemplate {

    @Autowired
    private SolrTemplate solrTemplate;

    /**
     * 1.创建数据对象(1.自己手动创建 2.从数据库读出来)
     * 2.将数据对象交给solrTemplate客户端
     * 3.将solrTemplate客户端的数据提交到solr服务器
     */
    @Test
    public void testAdd() {
        TbItem item = new TbItem();
        // 主键相同，即修改
        item.setId(1L);
        item.setBrand("小米粥");
        item.setCategory("手机pluse");
        item.setGoodsId(1L);
        item.setSeller("小米1号专卖店");
        item.setTitle("大米");
        item.setPrice(new BigDecimal(2200));
        item.setNum(10);
        solrTemplate.saveBean(item);
        solrTemplate.commit();
    }

    @Test
    public void testFindOne() {
        TbItem item = solrTemplate.getById(1, TbItem.class);
        System.out.println(item.getTitle());
    }

    @Test
    public void testDelete() {
        solrTemplate.deleteById("1");
        solrTemplate.commit();
    }

    @Test
    public void testAddList() {
        List<TbItem> list = new ArrayList();
        for (int i = 1; i < 101; i++) {
            TbItem item = new TbItem();
            item.setId(Long.valueOf(i));
            item.setBrand("华为");
            item.setCategory("手机");
            item.setGoodsId(1L);
            item.setSeller("华为" + i + "号专卖店");
            item.setTitle("华为Mate" + i);
            item.setPrice(new BigDecimal(2000 + i));
            list.add(item);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Test
    public void testPageQuery() {
        Query query = new SimpleQuery("*:*");
        // 开始索引（默认0）
        query.setOffset(10);
        // 每页记录数(默认10)
        query.setRows(20);
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
        System.out.println("总记录数：" + page.getTotalElements());
        List<TbItem> list = page.getContent();

        for (TbItem item : list) {
            System.out.println(item.getTitle() + "///" +item.getPrice());
        }

    }

    @Test
    public void testPageQueryMutil() {
        Query query = new SimpleQuery("*:*");
        Criteria criteria = new Criteria("item_title").contains("2");
        criteria = criteria.and("item_price").greaterThan(2020);
        query.addCriteria(criteria);
        Sort s = new Sort(Sort.Direction.DESC, "item_price");
        query.addSort(s);
        // query.setOffset(10); //开始索引（默认0）
        // query.setRows(100); //每页记录数(默认10)
        ScoredPage<TbItem> page = solrTemplate.queryForPage(query,TbItem.class);
        System.out.println("总记录数：" + page.getTotalElements());
        List<TbItem> list = page.getContent();
        for (TbItem item : list) {
            System.out.println(item.getTitle() + "，" + item.getPrice());
        }
    }

    @Test
    public void testDeleteAll(){
        Query query=new SimpleQuery("*:*");
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

}
