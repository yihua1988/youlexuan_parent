package com.offcn.search.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.promeg.pinyinhelper.Pinyin;
import com.offcn.pojo.TbItem;
import com.offcn.search.service.ItemSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhangjian
 * @email 13120082225@163.com
 * @date 2020/4/14
 */
@Service(timeout = 3000)
public class ItemSearchServiceImpl implements ItemSearchService {

    //一台solr如果顶不住我们的前台访问量，那么应该怎么办？
    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * @param searchMap
     * @return map
     * 要返回列表需要的商品信息，比如 标题，价格，图片  从solr读取出来的
     * 需要返回分类信息  从redis
     * 需要返回品牌信息  从redis
     * 需要返回规格信息  从redis
     */
    @Override
    public Map<String, Object> search(Map searchMap) {
//        Map<String, Object> map = new HashMap<>();
//        Query query = new SimpleQuery();
//        // is：基于分词后的结果 和 传入的参数匹配
//        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
//        // 添加查询条件
//        query.addCriteria(criteria);
//        ScoredPage<TbItem> page = solrTemplate.queryForPage(query, TbItem.class);
//        map.put("rows", page.getContent());
//        return map;
        // 数据脱敏  将非法内容进行清洗
        String keywords = (String) searchMap.get("keywords");

        searchMap.put("keywords", keywords.replace(" ", ""));

        Map<String, Object> map = new HashMap<>();
        //1.查询列表
        map.putAll(searchList(searchMap));
        //2.查询分类
        List categoryList = searchCategoryList(searchMap);
        map.put("categoryList", categoryList);
        //3.根据分类的ID从Redis中查询品牌和规格数据
        // 做核心业务的数据流处理的时候，一定要对数据的合法性进行核验
//        if (categoryList.size() > 0) {
//            map.putAll(searchBrandAndSpecList((String) categoryList.get(0)));
//        }
        String categoryName = (String) searchMap.get("category");
        if (!"".equals(categoryName)) {
            //按照分类名称重新读取对应品牌、规格
            map.putAll(searchBrandAndSpecList(categoryName));
        } else {
            if (categoryList.size() > 0) {
                for (int i = 0; i < categoryList.size(); i++) {
                    Map mapBrandAndSpec = searchBrandAndSpecList((String) categoryList.get(i));
                    map.putAll(mapBrandAndSpec);
                }
            }
        }
        return map;
    }

    @Override
    public void importList(List<TbItem> list) {
        for(TbItem item:list){
            System.out.println(item.getTitle());
            //从数据库中提取规格json字符串转换为map
            Map<String,String> specMap = JSON.parseObject(item.getSpec(),Map.class);
            Map map = new HashMap();
            //将规格的json转换出来的map中的所有的key换成拼音，并且对应到动态域中去
            for(String keys : specMap.keySet()) {
                map.put("item_spec_"+Pinyin.toPinyin(keys, "").toLowerCase(), specMap.get(keys));
            }
            //给带动态域注解的字段赋值
            item.setSpecMap(map);
        }
        solrTemplate.saveBeans(list);
        solrTemplate.commit();
    }

    @Override
    public void deleteByGoodsIds(List goodsIdList) {
        Query query = new SimpleQuery();
        // 构建条件的时候，一定要保证域信息和你的Solr是匹配的，否则不会将目标数据删除
        Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
        query.addCriteria(criteria);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 支持Solr高亮和多条件查询
     *
     * @param searchMap
     * @return
     */
    private Map searchList(Map searchMap) {
        Map map = new HashMap();
        //1、创建一个支持高亮查询器对象
        SimpleHighlightQuery query = new SimpleHighlightQuery();
        //2、创建高亮选项对象
        HighlightOptions highlightOptions = new HighlightOptions();
        //3、设定需要高亮处理字段
        highlightOptions.addField("item_title");
        //4、设置高亮前缀
        highlightOptions.setSimplePrefix("<em style='color:red'>");
        //5、设置高亮后缀
        highlightOptions.setSimplePostfix("</em>");
        //6、关联高亮选项到高亮查询器对象
        query.setHighlightOptions(highlightOptions);
        //7、设定查询条件 根据关键字查询

        //1.1 创建查询条件对象
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        //关联查询条件到查询器对象
        query.addCriteria(criteria);

        //1.2按分类筛选
        if (!"".equals(searchMap.get("category"))) {
            Criteria filterCriteria = new Criteria("item_category").is(searchMap.get("category"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.3按品牌筛选
        if (!"".equals(searchMap.get("brand"))) {
            Criteria filterCriteria = new Criteria("item_brand").is(searchMap.get("brand"));
            FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
            query.addFilterQuery(filterQuery);
        }
        //1.4过滤规格
        if (searchMap.get("spec") != null) {
            Map<String, String> specMap = (Map) searchMap.get("spec");
            for (String key : specMap.keySet()) {
                Criteria filterCriteria = new Criteria("item_spec_" + Pinyin.toPinyin(key, "")
                        .toLowerCase()).is(specMap.get(key));
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }
        //1.5过滤价格  0-500 500-1000  3000-*
        //             * TO 500、500 TO 100、3000 TO *
        if (!"".equals(searchMap.get("price"))) {
            String[] price = ((String) searchMap.get("price")).split("-");
            //如果区间起点不等于0
            if (!price[0].equals("0")) {
                Criteria filterCriteria = new Criteria("item_price").greaterThan(price[0]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
            //如果区间终点不等于*
            if (!price[1].equals("*")) {
                Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
                FilterQuery filterQuery = new SimpleFilterQuery(filterCriteria);
                query.addFilterQuery(filterQuery);
            }
        }

        //1.6 分页查询
        //提取页码
        Integer pageNo = (Integer) searchMap.get("pageNo");
        if (pageNo == null) {
            //默认第一页
            pageNo = 1;
        }

        //每页记录数
        Integer pageSize = (Integer) searchMap.get("pageSize");
        if (pageSize == null) {
            //默认20
            pageSize = 20;
        }

        //从第几条记录查询
        // 0  1  2  3  4  5  6  7  8  9
        // 第一页  0 开始  2  出来三条数据
        // 第二页  3 开始  5  出来三条数据
        query.setOffset((pageNo - 1) * pageSize);
        query.setRows(pageSize);

        //1.7 排序
        //ASC  DESC
        String sortValue = (String) searchMap.get("sort");
        //排序字段
        String sortField = (String) searchMap.get("sortField");

        if (sortValue != null && !sortValue.equals("")) {
            if (sortValue.equals("ASC")) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + sortField);
                query.addSort(sort);
            }
            if (sortValue.equals("DESC")) {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + sortField);
                query.addSort(sort);
            }
        }

        //8、发出带高亮数据查询请求
        HighlightPage<TbItem> highlightPage = solrTemplate.queryForHighlightPage(query, TbItem.class);
        //9、获取查询结果记录集合
        List<TbItem> list = highlightPage.getContent();
        //10、循环集合对象
        for (TbItem item : list) {
            //获取到针对对象TbItem高亮集合
            List<HighlightEntry.Highlight> highlights = highlightPage.getHighlights(item);
            if (highlights != null && highlights.size() > 0) {
                //获取第一个字段高亮对象
                List<String> highlightSnipplets = highlights.get(0).getSnipplets();
//                System.out.println("高亮：" + highlightSnipplets.get(0));
                //使用高亮结果替换商品标题
                item.setTitle(highlightSnipplets.get(0));
            }
        }
        map.put("rows", highlightPage.getContent());
        //返回总页数
        map.put("totalPages", highlightPage.getTotalPages());
        //返回总记录数
        map.put("total", highlightPage.getTotalElements());
        return map;
    }

    /**
     * 查询分类列表
     *
     * @param searchMap
     * @return
     */
    private List searchCategoryList(Map searchMap) {
        List<String> list = new ArrayList();
        Query query = new SimpleQuery();
        //按照关键字查询
        Criteria criteria = new Criteria("item_keywords").is(searchMap.get("keywords"));
        query.addCriteria(criteria);
        //设置分组选项  注意商品分类不能设置分词，要不然分组结果会失败
        GroupOptions groupOptions = new GroupOptions().addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        //得到分组页
        GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
        //根据列得到分组结果集
        GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
        //得到分组结果入口页
        Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
        //得到分组入口集合
        List<GroupEntry<TbItem>> content = groupEntries.getContent();
        for (GroupEntry<TbItem> entry : content) {
            //将分组结果的名称封装到返回值中
            list.add(entry.getGroupValue());
        }
        return list;
    }


    /**
     * 查询品牌和规格列表
     *
     * @param category 分类名称
     * @return
     */
    private Map searchBrandAndSpecList(String category) {
        Map map = new HashMap();

        //获取模板I
        Long typeId = (Long) redisTemplate.boundHashOps("itemCat").get(category);

        if (typeId != null) {
            //根据模板ID查询品牌列表
            List brandList = (List) redisTemplate.boundHashOps("brandList").get(typeId);
            map.put("brandList", brandList);//返回值添加品牌列表
            //根据模板ID查询规格列表
            List specList = (List) redisTemplate.boundHashOps("specList").get(typeId);
            map.put("specList", specList);
        }

        return map;
    }

}
