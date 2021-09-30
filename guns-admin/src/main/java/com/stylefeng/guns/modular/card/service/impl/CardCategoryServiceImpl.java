package com.stylefeng.guns.modular.card.service.impl;

import com.stylefeng.guns.modular.system.model.CardCategory;
import com.stylefeng.guns.modular.system.dao.CardCategoryMapper;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.modular.card.service.ICardCategoryService;
import com.stylefeng.guns.modular.card.warpper.CardCategoryWarpper;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

/**
 * <p>
 * 俱乐部卡种 服务实现类
 * </p>
 *
 * @author guiyj123
 * @since 2018-06-22
 */
@Service
public class CardCategoryServiceImpl extends ServiceImpl<CardCategoryMapper, CardCategory> implements ICardCategoryService {
	/**
     * <p>
     * 分页列表查询
     * </p>
     *
     * @param page 分页查询条件
     * @param clubId   俱乐部id
     * @param condition   其他模糊查询条件
     * @return List<>
     */
	@SuppressWarnings("unchecked")
	@Override
	public Page<CardCategory> pageList(Page<CardCategory> page, Integer clubId, String condition) {
    	
    	Wrapper<CardCategory> ew = new EntityWrapper<>();
    	if (ToolUtil.isNotEmpty(clubId) && !clubId.equals(0)) {
    		ew = ew.eq("club_id", clubId);
    	}
    	if (ToolUtil.isNotEmpty(condition)) {
    		ew = ew.like("nickname", condition).or().like("realname", condition);
    	}
    	
    	List<Map<String, Object>> result = baseMapper.selectMapsPage(page, ew);
        return page.setRecords( (List<CardCategory>) new CardCategoryWarpper(result).warp());
	}
}
