package com.wjl.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.wjl.common.entity.to.SkuEsModel;
import com.wjl.common.utils.R;
import com.wjl.gulimall.search.feign.ProducFeignService;
import com.wjl.gulimall.search.config.ElasticSearchConfig;
import com.wjl.gulimall.search.consts.SearchConstants;
import com.wjl.gulimall.search.model.vo.AttrResponseVo;
import com.wjl.gulimall.search.model.vo.SearchParams;
import com.wjl.gulimall.search.model.vo.SearchResult;
import com.wjl.gulimall.search.service.MallSearchService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/10
 */
@Slf4j
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProducFeignService producFeignService;

    /**
     * 检索所有的参数 返回检索结果
     *
     * @param params
     * @return
     */
    @Override
    public SearchResult search(SearchParams params) throws IOException {
        SearchRequest request = buildSearchRequest(params);
        SearchResponse response = restHighLevelClient.search(request, ElasticSearchConfig.REQUEST_OPTIONS);
        SearchResult result = buildSearchResult(response, params);
        result.setPageNum(params.getPageNum());
        return result;
    }


    private SearchRequest buildSearchRequest(SearchParams params) {
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        String keyword = params.getKeyword();
        // 关键字
        if (StringUtils.isNotEmpty(keyword)) {
            query.must(QueryBuilders.matchQuery("skuTitle", keyword));
        }

        // 三级分类id
        Long catalog3Id = params.getCatalog3Id();
        if (catalog3Id != null) {
            query.filter(QueryBuilders.termQuery("catalogId", catalog3Id.toString()));
        }

        // 品牌id
        List<Long> brandId = params.getBrandId();
        if (!CollectionUtils.isEmpty(brandId)) {
            query.filter(QueryBuilders.termsQuery("brandId", brandId));
        }

        // 是否有库存
        int hasStock = params.getHasStock() == null ? 1 : params.getHasStock();
        query.filter(QueryBuilders.termQuery("hasStock", hasStock == 1));

        // 价格区间
        String skuPrice = params.getSkuPrice();
        // 1_500 _500 500_
        if (StringUtils.isNotEmpty(skuPrice)) {
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String[] split = skuPrice.split("_");
            if (split.length == 2) {
                if (split[0].equals("")) { // _500
                    rangeQuery.lte(split[1]);
                } else {
                    rangeQuery.gte(split[0]).lte(split[1]);// 100_500
                }
            } else if (split.length == 1) {
                if (skuPrice.startsWith("_")) {
                    rangeQuery.gte(split[0]);// 500_
                }
            }
            query.filter(rangeQuery);
        }


        // 按照属性查询
        List<String> attrs = params.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            // attrs=1_5寸:8寸&attrs=2_16G:8G
            for (String attr : attrs) {
                String[] s = attr.split("_");
                String attrId = s[0];
                String[] attrValues = s[1].split(":");

                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                nestedBoolQuery.must(QueryBuilders.termsQuery("attrs.attrId", attrId))
                        .must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                // 每一个都必须生成自己的nested 这样的话不同的属性值传递进来就是与的关系 而不是或的关系
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedBoolQuery, ScoreMode.None);
                query.filter(nestedQuery);
            }
        }
        // 查询
        builder.query(query);

        /**
         * 排序
         */
        String sort = params.getSort();
        // sort=field_asc/desc
        if (StringUtils.isNotEmpty(sort)) {
            String[] s = sort.split("_");
            String sortField = s[0];
            SortOrder sortOrder = s[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            builder.sort(sortField, sortOrder);
        }

        /**
         * 分页
         */
        Integer pageNum = params.getPageNum();
        int size = SearchConstants.DEF_PAGE_SIZE;
        int page = (pageNum - 1) * size;
        builder.from(page).size(size);

        /**
         * 高亮
         */
        if (StringUtils.isNotEmpty(keyword)) {
            HighlightBuilder highlightBuilder = new HighlightBuilder()
                    .field("skuTitle")
                    .preTags("<b style=\"color:red;\">")
                    .postTags("</b>");
            builder.highlighter(highlightBuilder);
        }

        /**
         * 聚合
         */

        TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").size(50).field("brandId");
        // 子聚合
        // 每个品牌肯定只有一个名字 size(1)
        brandAgg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brandAgg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));
        builder.aggregation(brandAgg);

        // 聚合分类信息
        TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        catalogAgg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));
        builder.aggregation(catalogAgg);

        // 属性聚合
        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attr_id_agg").field("attrs.attrId");
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(1));
        attrIdAgg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        attrAgg.subAggregation(attrIdAgg);

        builder.aggregation(attrAgg);
        log.info("DSL: {}", builder.toString());
        return new SearchRequest(new String[]{SearchConstants.GULI_INDEX}, builder);
    }

    private SearchResult buildSearchResult(SearchResponse response, SearchParams params) {
        SearchResult result = new SearchResult();


        SearchHits hits = response.getHits();
        long total = hits.getTotalHits().value;
        result.setTotal(total);
        int totalPage = (int) (total % SearchConstants.DEF_PAGE_SIZE == 0 ? total / SearchConstants.DEF_PAGE_SIZE : total / SearchConstants.DEF_PAGE_SIZE + 1);
        result.setTotalPages(totalPage);

        // products
        List<SkuEsModel> products = new ArrayList<>();
        for (SearchHit hit : hits.getHits()) {
            SkuEsModel model = JSON.parseObject(hit.getSourceAsString(), SkuEsModel.class);
            Map<String, HighlightField> fieldMap = hit.getHighlightFields();
            // 高亮
            if (!CollectionUtils.isEmpty(fieldMap)) {
                String skuTitle = fieldMap.get("skuTitle").getFragments()[0].string();
                model.setSkuTitle(skuTitle);
            }
            products.add(model);
        }
        result.setProduct(products);

        // catalog_agg
        List<SearchResult.CatalogVO> catalogs = new ArrayList<>();
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResult.CatalogVO vo = new SearchResult.CatalogVO();
            vo.setCatalogId(bucket.getKeyAsNumber().longValue());
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String name = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            vo.setCatalogName(name);
            catalogs.add(vo);
        }
        result.setCatalogs(catalogs);


        // brand_agg
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        List<SearchResult.BrandVO> brandVOS = new ArrayList<>();

        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVO vo = new SearchResult.BrandVO();
            Long brandId = bucket.getKeyAsNumber().longValue();
            vo.setBrandId(brandId);
            Aggregations brandSubAgg = bucket.getAggregations();
            ParsedStringTerms brand_img_agg = brandSubAgg.get("brand_img_agg");
            String images = brand_img_agg.getBuckets().get(0).getKeyAsString();
            vo.setBrandImg(images);
            ParsedStringTerms brandNameAgg = brandSubAgg.get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            vo.setBrandName(brandName);
            brandVOS.add(vo);
        }

        result.setBrands(brandVOS);

        // attr_agg
        List<SearchResult.AttrVO> attrVOS = new ArrayList<>();
        ParsedNested attr_agg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attr_id_agg = attr_agg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attr_id_agg.getBuckets()) {
            SearchResult.AttrVO vo = new SearchResult.AttrVO();
            vo.setAttrId(bucket.getKeyAsNumber().longValue());
            String name = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
            vo.setAttrName(name);

            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> valueList = new ArrayList<>();
            for (Terms.Bucket attrValueAggBucket : attrValueAgg.getBuckets()) {
                String attrValue = attrValueAggBucket.getKeyAsString();
                valueList.add(attrValue);
            }
            vo.setAttrValue(valueList);
            attrVOS.add(vo);
        }
        result.setAttrs(attrVOS);


        // 构建面包屑导航
        List<SearchResult.NavVo> navVos = new ArrayList<>();
        List<String> attrs = params.getAttrs();
        if (!CollectionUtils.isEmpty(attrs)) {
            navVos = attrs.stream().map(item -> {
                SearchResult.NavVo vo = new SearchResult.NavVo();
                String[] split = item.split("_");
                vo.setNavValue(split[1]);
                R r = producFeignService.info(Long.parseLong(split[0]));
                if (r.get("code").equals(0)) {
                    AttrResponseVo attr = r.getData("attr", new TypeReference<AttrResponseVo>() {
                    });
                    vo.setNavName(attr.getAttrName());
                    String encode = null;
                    try {
                        encode = URLEncoder.encode(item, StandardCharsets.UTF_8.name());
                        encode = encode.replace("+","%20");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    String replace = params.get_queryString().replace("&attrs=" + encode, "");
                    vo.setLink("http://search.gulimall.com/list.html?" + replace);
                } else {
                    vo.setNavName(split[0]);
                }
                return vo;
            }).collect(Collectors.toList());
        }
        result.setNavs(navVos);

        List<Integer> pageNavs = new ArrayList<>();
        for (int i = 1; i <= totalPage; i++) {
            pageNavs.add(i);
        }
        result.setPageNavs(pageNavs);
        return result;
    }

}
