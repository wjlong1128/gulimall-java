package com.wjl.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wjl.common.utils.PageUtils;
import com.wjl.common.utils.Query;
import com.wjl.gulimall.product.dao.CategoryDao;
import com.wjl.gulimall.product.entity.CategoryEntity;
import com.wjl.gulimall.product.entity.vo.Category2VO;
import com.wjl.gulimall.product.entity.vo.CategoryTreeVO;
import com.wjl.gulimall.product.service.CategoryBrandRelationService;
import com.wjl.gulimall.product.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryTreeVO> listWithTree() {
        // 1. 查出所有分类
        List<CategoryEntity> list = this.list();

        List<CategoryTreeVO> treeList = list.stream().filter(c -> c.getParentCid() != null && c.getParentCid().equals(0L)).map(c -> {
                    CategoryTreeVO categoryTreeVO = new CategoryTreeVO();
                    BeanUtils.copyProperties(c, categoryTreeVO);
                    categoryTreeVO.setChildren(getChildred(c, list));
                    return categoryTreeVO;
                    // 排序
                    // 防止空指针异常
                }).sorted((o1, o2) -> Optional.ofNullable(o1.getSort()).orElse(0) - Optional.ofNullable(o2.getSort()).orElse(0))
                .collect(Collectors.toList());
        // 2. 组装父子结构
        return treeList;
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void removeMenuByIds(List<Long> ids) {
        // TODO: 批量删除 检查是否被引用
        baseMapper.deleteBatchIds(ids);
    }

    @Transactional
    // @CacheEvict(value = {"categorys"},key = "'getCategoryJson'")
    // allEntries 清除这个value下分区 下所有的数据
    @CacheEvict(value = "category",allEntries = true)
    @Override
    public void updateDetail(CategoryEntity category) {
        this.updateById(category);
        if (!StringUtils.isNotEmpty(category.getName())) {
            return;
        }

        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    @Override
    public List<Long> getCategoryIdsPath(Long id) {
        LinkedList<Long> ids = new LinkedList<>();
        int i = 0;
        do {
            i++;
            ids.addFirst(id);
            CategoryEntity categoryEntity = this.baseMapper.selectById(id);
            if (categoryEntity == null || categoryEntity.getParentCid() == 0L || i >= 10) {
                break;
            }
            id = categoryEntity.getParentCid();
        } while (id != null);
        return ids;
    }

    @Cacheable(value = "category",key = "#root.methodName")
    @Override
    public List<CategoryEntity> getLevel1() {
        return this.baseMapper.selectList(new LambdaQueryWrapper<CategoryEntity>().eq(CategoryEntity::getParentCid, 0));
    }

    // io.lettuce.core.RedisCommandTimeoutException: Command timed out after 1 minute(s)

    /**
     * lettuce 默认使用netty
     * -Xmx300m netty如果没有指定堆外内存 默认使用指定的300 xmx指的是最大内存 而不是堆内外内存
     * 升级lettuce版本5.2解决
     * TODO: 记录redisson
     * @return
     */
    @Cacheable(value = {"category"},key = "#root.methodName") // 自动缓存 有取，没有执行方法之后缓存
    public Map<String, List<Category2VO>> getCatelogJson() {
        String key = "catelog-json";
        String json = redisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(json)) {
            return JSON.parseObject(json, new TypeReference<Map<String, List<Category2VO>>>() {
            });
        }
        // 缓存没有 查询数据库 首先锁 这里双重锁检查
        // synchronized (this){
        RLock categoryJsonLock = redisson.getLock("categoryJsonLock");
        categoryJsonLock.lock();
        Map<String, List<Category2VO>> catelogJsonFromDB;
        try {
            json = redisTemplate.opsForValue().get(key);
            if (StringUtils.isNotEmpty(json)) {
                return JSON.parseObject(json, new TypeReference<Map<String, List<Category2VO>>>() {
                });
            }
            catelogJsonFromDB = getCatelogJsonFromDB();
            if (catelogJsonFromDB == null) {
                redisTemplate.opsForValue().set(key, "{}", 60, TimeUnit.SECONDS);
                return Collections.EMPTY_MAP;
            }
            redisTemplate.opsForValue().set(key, JSON.toJSONString(catelogJsonFromDB), 24, TimeUnit.HOURS);
        } finally {
            categoryJsonLock.unlock();
        }
        // }
        return catelogJsonFromDB;
    }

    public Map<String, List<Category2VO>> getCatelogJsonFromDB() {
        System.out.println("get db....");
        List<CategoryEntity> allCategory = this.baseMapper.selectList(null);
        List<CategoryEntity> level1s = getParentCid(allCategory, 0L);
        Map<String, List<Category2VO>> result = level1s.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), item -> {
            List<CategoryEntity> level2s = getParentCid(allCategory, item.getCatId());
            return level2s.stream().map(level2 -> {
                List<CategoryEntity> level3s = getParentCid(allCategory, level2.getCatId());
                List<Category2VO.Catelog3VO> catelog3VOS = level3s.stream().map(level3 -> {
                    return new Category2VO.Catelog3VO(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                }).collect(Collectors.toList());
                return new Category2VO(item.getCatId().toString(), catelog3VOS, level2.getCatId().toString(), level2.getName());
            }).collect(Collectors.toList());
        }));

        return result;
    }

    private List<CategoryEntity> getParentCid(List<CategoryEntity> categoryEntities, Long picId) {
        return categoryEntities.stream().filter(categoryEntity -> categoryEntity.getParentCid().equals(picId)).collect(Collectors.toList());
    }


    private List<CategoryTreeVO> getChildred(CategoryEntity root, List<CategoryEntity> all) {
        List<CategoryTreeVO> treeVOList = all.stream()
                // 过滤 同时也是结束的条件
                .filter(menu -> menu.getParentCid() != null && menu.getParentCid().equals(root.getCatId())).map(menu -> {
                    CategoryTreeVO categoryTreeVO = new CategoryTreeVO();
                    BeanUtils.copyProperties(menu, categoryTreeVO);
                    // 寻址所有子菜单
                    categoryTreeVO.setChildren(getChildred(menu, all));
                    return categoryTreeVO;
                }).sorted((o1, o2) -> Optional.ofNullable(o1.getSort()).orElse(0) - Optional.ofNullable(o2.getSort()).orElse(0))
                .collect(Collectors.toList());
        return treeVOList;
    }

}