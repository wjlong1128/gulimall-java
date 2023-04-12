package com.wjl.gulimall.search.consts;/*
 * @author Wang Jianlong
 * @version 1.0.0
 * @description
 * @date 2023/4/8
 */

public class SearchConstants {
    public static final String GULI_INDEX = "gmall";
    public static final String GULI_MAPPINGS = "{\n" +
            "  \"mappings\": {\n" +
            "    \"properties\": {\n" +
            "      \"skuId\": {\n" +
            "        \"type\": \"long\"\n" +
            "      },\n" +
            "      \"spuId\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"skuTitle\": {\n" +
            "        \"type\": \"text\",\n" +
            "        \"analyzer\": \"ik_smart\"\n" +
            "      },\n" +
            "      \"skuPrice\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"skuImg\": {\n" +
            "        \"type\": \"keyword\",\n" +
            "        \"index\": false\n" +
            "      },\n" +
            "      \"saleCount\": {\n" +
            "        \"type\": \"long\"\n" +
            "      },\n" +
            "      \"hasStock\": {\n" +
            "        \"type\": \"boolean\"\n" +
            "      },\n" +
            "      \"hotScore\": {\n" +
            "        \"type\": \"long\"\n" +
            "      },\n" +
            "      \"brandId\": {\n" +
            "        \"type\": \"long\"\n" +
            "      },\n" +
            "      \"catalogId\": {\n" +
            "        \"type\": \"long\"\n" +
            "      },\n" +
            "      \"brandName\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"brandImg\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"catalogName\": {\n" +
            "        \"type\": \"keyword\"\n" +
            "      },\n" +
            "      \"attrs\": {\n" +
            "        \"type\": \"nested\",\n" +
            "        \"properties\": {\n" +
            "          \"attrId\": {\n" +
            "            \"type\": \"long\"\n" +
            "          },\n" +
            "          \"attrName\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          },\n" +
            "          \"attrValue\": {\n" +
            "            \"type\": \"keyword\"\n" +
            "          }\n" +
            "        }\n" +
            "      }\n" +
            "    }\n" +
            "  }\n" +
            "}\n";
    public static final int DEF_PAGE_SIZE = 2;
}
