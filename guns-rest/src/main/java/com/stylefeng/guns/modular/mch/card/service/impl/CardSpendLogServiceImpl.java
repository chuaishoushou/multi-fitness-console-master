package com.stylefeng.guns.modular.mch.card.service.impl;

import com.stylefeng.guns.modular.system.model.CardOncecard;
import com.stylefeng.guns.modular.system.model.CardPtraincard;
import com.stylefeng.guns.modular.system.model.PtrainBespeakRecord;
import com.stylefeng.guns.modular.system.model.CardSpendLog;
import com.stylefeng.guns.modular.system.model.CardRechargeLog;
import com.stylefeng.guns.modular.system.model.VipUser;
import com.stylefeng.guns.modular.system.warpper.CardSpendLogWarpper;
import com.stylefeng.guns.modular.system.warpper.CardRechargeLogWarpper;
import com.stylefeng.guns.rest.common.ReturnTip;
import com.stylefeng.guns.rest.common.exception.BizException;
import com.stylefeng.guns.modular.system.dao.CardSpendLogMapper;
import com.stylefeng.guns.core.cache.CacheKit;
import com.stylefeng.guns.core.common.constant.Const;
import com.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import com.stylefeng.guns.core.support.ExcelBean;
import com.stylefeng.guns.core.support.ExcelUtils;
import com.stylefeng.guns.core.support.HttpKit;
import com.stylefeng.guns.core.util.Convert;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.modular.mch.card.service.ICardOncecardService;
import com.stylefeng.guns.modular.mch.card.service.ICardPtraincardService;
import com.stylefeng.guns.modular.mch.member.service.IVipUserService;
import com.stylefeng.guns.modular.mch.card.service.ICardSpendLogService;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * ???????????? ???????????????
 * </p>
 *
 * @author guiyj123
 * @since 2018-06-22
 */
@Service
public class CardSpendLogServiceImpl extends ServiceImpl<CardSpendLogMapper, CardSpendLog>
		implements ICardSpendLogService {
	@Autowired
	private IVipUserService vipUserService;
	@Autowired
	private ICardPtraincardService cardPtraincardService;
	@Autowired
	private ICardOncecardService cardOncecardService;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> statRecords(Integer clubId, HashMap<String, Object> condition) {
		Wrapper<CardSpendLog> ew = getSearchEntityWrapper(clubId, condition);
		int logType = Convert.toInt(condition.get("logType"), 0);
		//?????????????????? 1 ????????? 2????????? 3?????? 4?????????',
		if (logType == 1) {
			ew.setSqlSelect("count(1) AS totalCnt, sum(costs) AS totalCourse");
		} else if (logType == 4) {
			ew.setSqlSelect("count(1) AS totalCnt, sum(cost_money) AS totalCourse");
		} else {
			ew.setSqlSelect("count(1) AS totalCnt, sum(costs) AS totalCourse");
		}
		
    	Map<String, Object> mapRes = this.selectMap(ew);
    	
    	Map<String, Object> mapRet = new HashMap<>();
    	if (mapRes == null) {
    		mapRet.put("sumMoney", 0);
        	mapRet.put("totalCnt", 0);
        	mapRet.put("totalCourse", 0);
        	return mapRet;
    	}
    	
    	mapRet.put("sumMoney", 0);
    	mapRet.put("totalCnt", mapRes.get("totalCnt"));
    	mapRet.put("totalCourse", mapRes.get("totalCourse"));
    	
    	return mapRet;
	}
	
	/**
	 * <p>
	 * ??????????????????
	 * </p>
	 *
	 * @param page
	 *            ??????????????????
	 * @param clubId
	 *            ?????????id
	 * @param condition
	 *            ????????????????????????
	 * @return List<>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<CardSpendLog> pageList(Page<CardSpendLog> page, Integer clubId, HashMap<String, Object> condition) {

		Wrapper<CardSpendLog> ew = getSearchEntityWrapper(clubId, condition);

		List<Map<String, Object>> result = baseMapper.selectMapsPage(page, ew);
		return page.setRecords((List<CardSpendLog>) new CardSpendLogWarpper(result).warp());
	}

	private Wrapper<CardSpendLog> getSearchEntityWrapper(Integer clubId, HashMap<String, Object> condition) {
		Wrapper<CardSpendLog> ew = new EntityWrapper<>();

		if (ToolUtil.isNotEmpty(clubId) && !clubId.equals(0)) {
			ew = ew.eq("club_id", clubId);
		}
		if (ToolUtil.isNotEmpty(condition.get("id")) && !condition.get("id").equals("0")) {
			ew = ew.eq("id", (Integer) condition.get("id"));
		}
		if (ToolUtil.isNotEmpty(condition.get("logType"))) {
			ew = ew.eq("log_type", Convert.toInt(condition.get("logType")));
		}
		if (ToolUtil.isNotEmpty(condition.get("userId"))) {
			ew = ew.eq("user_id", Convert.toInt(condition.get("userId")));
		}
		if (ToolUtil.isNotEmpty(condition.get("coachId"))) {
			ew = ew.eq("coach_id", Convert.toInt(condition.get("coachId")));
		}
		if (ToolUtil.isNotEmpty(condition.get("userPhone"))) {
			ew = ew.like("user_phone", (String) condition.get("userPhone"));
		}
		if (ToolUtil.isNotEmpty(condition.get("student"))) {
			ew = ew.like("user_realname", Convert.toStr(condition.get("student")));
		}
		if (ToolUtil.isNotEmpty(condition.get("userRealname"))) {
			ew = ew.like("user_realname", Convert.toStr(condition.get("userRealname")));
		}
		if (ToolUtil.isNotEmpty(condition.get("vipId"))) {
			ew = ew.eq("vip_id", Convert.toInt(condition.get("vipId")));
		}
		if (ToolUtil.isNotEmpty(condition.get("banksure")) && !condition.get("banksure").equals("2")) {
			ew = ew.eq("banksure", Convert.toInt(condition.get("banksure")));
		}
		if (ToolUtil.isNotEmpty(condition.get("port")) && !condition.get("port").equals("0")) {
			ew = ew.eq("port", Convert.toInt(condition.get("port")));
		}
		if (ToolUtil.isNotEmpty(condition.get("isSyllabusSub")) && !condition.get("isSyllabusSub").equals("99")) {
			ew = ew.eq("is_syllabus_sub", Convert.toInt(condition.get("isSyllabusSub")));
		}

		if (ToolUtil.isNotEmpty(condition.get("onceCardTitle"))) {
			ew = ew.like("title", (String) condition.get("onceCardTitle"));
		}
		if (ToolUtil.isNotEmpty(condition.get("coachName"))) {
			ew = ew.like("coach_name", (String) condition.get("coachName"));
		}
		if (ToolUtil.isNotEmpty(condition.get("membershipName"))) {
			ew = ew.like("membership_name", (String) condition.get("membershipName"));
		}
		if (ToolUtil.isNotEmpty(condition.get("receptionistName"))) {
			ew = ew.like("receptionist_name", (String) condition.get("receptionistName"));
		}

		if (ToolUtil.isNotEmpty(condition.get("startInsertTime"))) {
			ew = ew.ge("insert_time", DateUtil.date2TimeStamp((String) condition.get("startInsertTime"), "yyyy-MM-dd"));
		}
		if (ToolUtil.isNotEmpty(condition.get("endInsertTime"))) {
			ew = ew.le("insert_time",
					86400 + DateUtil.date2TimeStamp((String) condition.get("endInsertTime"), "yyyy-MM-dd"));
		}

		return ew;
	}

	/**
	 * ??????app????????????
	 */
	@SuppressWarnings("unchecked")
	@Override
	public CardSpendLog finishPtrain(Integer clubId, Integer coachId, Integer userId, String conformCode,
			String receptionRemark) {
		// 1 ?????????????????????????????????vip??????
		Wrapper<VipUser> ew = new EntityWrapper<>();
		ew = ew.eq("club_id", clubId).eq("user_id", userId);
		VipUser vipUser = vipUserService.selectOne(ew);
		if (ToolUtil.isEmpty(vipUser)) {
			throw new BizException(500, "?????????vip??????");
		}
		Integer vipId = vipUser.getId();

		// 2 ???????????????????????????
		CardPtraincard card = cardPtraincardService.getUserValidCard(vipId);
		if (ToolUtil.isEmpty(card) || card.getLeftCounts() <= 0) {
			throw new BizException(500, "???????????????????????????");
		}

		// 3 ????????????????????????
		// entity = new CardSpendLog();
		CardSpendLog entity = null;
		int costs = 1;
		int port = 5;
		try {
			entity = this.doFinishPtrain(coachId, vipUser, card, conformCode, costs, port, receptionRemark);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}

		return entity;
	}

	/**
	 * ???????????????
	 */
	@Override
	public CardSpendLog agentCostPtrain(int cardId, Integer coachId, int costs, String receptionRemark) {
		CardPtraincard card = cardPtraincardService.selectById(cardId);
		// 2 ???????????????????????????
		if (ToolUtil.isEmpty(card) || card.getLeftCounts() <= 0) {
			throw new BizException(500, "???????????????????????????");
		}

		VipUser vipUser = vipUserService.selectById(card.getVipId());
		String minite = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm");
		String conformCode = MD5Util.encrypt(minite + "-" + coachId + "-" + costs + "-" + cardId).substring(18);

		// 3 ????????????????????????
		// entity = new CardSpendLog();
		CardSpendLog entity = null;
		int port = 6;
		try {
			entity = this.doFinishPtrain(coachId, vipUser, card, conformCode, costs, port, receptionRemark);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return entity;
	}

	/**
	 * ???????????????
	 */
	@Override
	public CardSpendLog agentCostOnce(int cardId, Integer coachId, int costs, String receptionRemark) {
		CardOncecard card = cardOncecardService.selectById(cardId);
		// 2 ???????????????????????????
		if (ToolUtil.isEmpty(card) || card.getLeftCounts() <= 0) {
			throw new BizException(500, "?????????????????????");
		}

		VipUser vipUser = vipUserService.selectById(card.getVipId());
		String minite = DateUtil.formatDate(new Date(), "yyyy-MM-dd HH:mm");
		String conformCode = MD5Util.encrypt(minite + "-" + coachId + "-" + costs + "-" + cardId).substring(18);

		// 3 ????????????????????????
		// entity = new CardSpendLog();
		CardSpendLog entity = null;
		int port = 6;
		try {
			entity = this.doFinishOnce(coachId, vipUser, card, conformCode, costs, port, receptionRemark);
		} catch (Exception e) {
			// TODO: handle exception
			throw e;
		}
		return entity;
	}

	/**
	 * ???????????????
	 * @param coachId
	 * @param vipUser
	 * @param card
	 * @param conformCode
	 * @param costs
	 * @param port
	 * @param receptionRemark
	 * @return
	 */
	// port ???????????? 0?????? 3???????????? 4???????????? 5???????????? 6???????????? 7???????????? 8???????????? 9???????????? 22?????????
	private CardSpendLog doFinishPtrain(Integer coachId, VipUser vipUser, CardPtraincard card, String conformCode,
			int costs, int port, String receptionRemark) {
		CardSpendLog entity = null;

		// 3 ??????conformCode ???????????????????????????????????????
		if (!conformCode.equals("")) {
			if (!ToolUtil.isEmpty(CacheKit.get("ptrainRecord", conformCode))) {
				throw new BizException(500, "?????????????????????????????????");
			}

			CacheKit.put("ptrainRecord", conformCode, true);
			entity = this.getByConfromCode(conformCode);
			if (ToolUtil.isNotEmpty(entity)) {
				CacheKit.remove("ptrainRecord", conformCode);
				return entity;
			}
		}

		int vipId = vipUser.getId();
		int userId = vipUser.getUserId();
		Integer clubId = vipUser.getClubId();

		// 3 ????????????????????????
		entity = new CardSpendLog();
		entity.setLogType(Const.LOG_TYPE_PTRAINCARD); // '?????????????????? 1 ????????? 2????????? 3??????
														// 4?????????',
		entity.setConformCode(conformCode);
		entity.setVipId(vipId);
		entity.setUserId(userId);
		entity.setUserRealname(ConstantFactory.me().getVipRealnameById(vipId));
		entity.setClubId(clubId);
		entity.setUserPhone(vipUser.getPhone());
		entity.setCoachId(coachId);
		entity.setCoachName(ConstantFactory.me().getCoachNameById(coachId));
		entity.setMembershipId(vipUser.getMembershipId());
		entity.setMembershipName(vipUser.getMembershipName());
		entity.setIpAddress(ToolUtil.getLocalIp(HttpKit.getRequest()));
		entity.setInsertTime(DateUtil.timeStamp());

		entity.setCardId(card.getId());
		entity.setUnitPrice(card.getUnitPrice());
		entity.setLastCounts(card.getCounts() - card.getLeftCounts());
		entity.setTitle(card.getTitle());
		entity.setPort(port); // ???????????? 0?????? 3???????????? 4???????????? 5???????????? 6???????????? 7???????????? 8????????????
								// 9???????????? 22?????????
		entity.setReceptionRemark(ToolUtil.isEmpty(receptionRemark) ? "" : receptionRemark);

		entity.setCosts(costs);

		try {
			this.insert(entity);

			// ???????????????????????????
			card.setLeftCounts(card.getLeftCounts() - costs);
			cardPtraincardService.updateById(card);

			CacheKit.remove("ptrainRecord", conformCode);
		} catch (Exception e) {
			e.printStackTrace();
			CacheKit.remove("ptrainRecord", conformCode);
			throw new BizException(502, "????????????");
		}

		return entity;
	}

	/**
	 * ????????????
	 * 
	 * @param coachId
	 * @param vipUser
	 * @param card
	 * @param conformCode
	 * @param costs
	 * @param port
	 * @param receptionRemark
	 * @return
	 */
	// port ???????????? 0?????? 3???????????? 4???????????? 5???????????? 6???????????? 7???????????? 8???????????? 9???????????? 22?????????
	private CardSpendLog doFinishOnce(Integer coachId, VipUser vipUser, CardOncecard card, String conformCode,
			int costs, int port, String receptionRemark) {
		CardSpendLog entity = null;

		// 3 ??????conformCode ???????????????????????????????????????
		if (!conformCode.equals("")) {
			if (!ToolUtil.isEmpty(CacheKit.get("ptrainRecord", conformCode))) {
				throw new BizException(500, "?????????????????????????????????");
			}

			CacheKit.put("onceRecord", conformCode, true);
			entity = this.getByConfromCode(conformCode);
			if (ToolUtil.isNotEmpty(entity)) {
				CacheKit.remove("onceRecord", conformCode);
				return entity;
			}
		}

		int vipId = vipUser.getId();
		int userId = vipUser.getUserId();
		Integer clubId = vipUser.getClubId();

		// 3 ????????????????????????
		entity = new CardSpendLog();
		entity.setLogType(Const.LOG_TYPE_ONCECARD); // '?????????????????? 1 ????????? 2????????? 3??????
														// 4?????????',
		entity.setConformCode(conformCode);
		entity.setVipId(vipId);
		entity.setUserId(userId);
		entity.setUserRealname(ConstantFactory.me().getVipRealnameById(vipId));
		entity.setClubId(clubId);
		entity.setUserPhone(vipUser.getPhone());
		entity.setCoachId(coachId);
		entity.setCoachName(ConstantFactory.me().getCoachNameById(coachId));
		entity.setMembershipId(vipUser.getMembershipId());
		entity.setMembershipName(vipUser.getMembershipName());
		entity.setIpAddress(ToolUtil.getLocalIp(HttpKit.getRequest()));
		entity.setInsertTime(DateUtil.timeStamp());

		entity.setCardId(card.getId());
		entity.setUnitPrice(card.getUnitPrice());
		entity.setLastCounts(card.getCounts() - card.getLeftCounts());
		entity.setTitle(card.getTitle());
		entity.setPort(port); // ???????????? 0?????? 3???????????? 4???????????? 5???????????? 6???????????? 7???????????? 8????????????
								// 9???????????? 22?????????
		entity.setReceptionRemark(ToolUtil.isEmpty(receptionRemark) ? "" : receptionRemark);

		entity.setCosts(costs);

		try {
			this.insert(entity);

			// ???????????????????????????
			card.setLeftCounts(card.getLeftCounts() - costs);
			cardOncecardService.updateById(card);

			CacheKit.remove("onceRecord", conformCode);
		} catch (Exception e) {
			e.printStackTrace();
			CacheKit.remove("onceRecord", conformCode);
			throw new BizException(502, "????????????");
		}

		return entity;
	}

	@Override
	public CardSpendLog getByConfromCode(String conformCode) {
		Wrapper<CardSpendLog> ew = new EntityWrapper<>();
		ew.eq("conform_code", conformCode);

		return this.selectOne(ew);
	}
	
	/**
	 * ???????????????????????????
	 */
	@Override
	public Integer countLatestFinishPtrain(int clubId, int coachId, int seconds) {
		int currentTime = DateUtil.timeStamp();
		
		Wrapper<CardSpendLog> ew = new EntityWrapper<>();
		ew.eq("coach_id", coachId);
		ew.eq("club_id", clubId);
		ew.eq("log_type", Const.LOG_TYPE_PTRAINCARD);
		ew.ge("insert_time", currentTime - seconds);
		return this.selectCount(ew);
	}

	/**
	 * <p>
	 * ????????????????????????
	 * </p>
	 *
	 * @param clubId
	 *            ?????????id
	 * @param condition
	 *            ????????????????????????
	 * @param hdSearchKey
	 *            ??????????????????
	 * @return
	 * @return List<>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<Map<String, Object>> getExportList(Integer clubId, HashMap<String, Object> condition) {
		Wrapper<CardSpendLog> ew = getSearchEntityWrapper(clubId, condition);

		// ????????????1000?????????
		Page<CardSpendLog> page = new Page<>(0, 1000, "id", true);
		List<Map<String, Object>> result = baseMapper.selectMapsPage(page, ew);
		return (List<Map<String, Object>>) new CardSpendLogWarpper(result).warp();
	}

	@Override
	public XSSFWorkbook exportInfoExcel(List<Map<String, Object>> infoList)
			throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, ClassNotFoundException,
			IntrospectionException, ParseException {
		// debug
		// Wrapper<VipUser> ew = new EntityWrapper<>();
		// ew = ew.eq("club_id", clubId);
		// List<Map<String, Object>> infoList = this.getExportList(clubId, new
		// HashMap<String, Object>(), "");

		List<ExcelBean> ems = new ArrayList<>();
		Map<Integer, List<ExcelBean>> map = new LinkedHashMap<>();

		XSSFWorkbook book = null;
		ems.add(new ExcelBean("????????????", "insertTimeStr", 0));
		ems.add(new ExcelBean("????????????", "costs", 0));
		ems.add(new ExcelBean("????????????", "unit_price", 0));
		ems.add(new ExcelBean("????????????", "userRealname", 0));
		ems.add(new ExcelBean("?????????", "userPhone", 0));// 201807118660
		ems.add(new ExcelBean("??????", "membershipName", 0));// 2018-07-11 10:18
		ems.add(new ExcelBean("????????????", "coachName", 0));// 2018-07-11 10:18
		ems.add(new ExcelBean("??????", "rcepteionName", 0));// 2018-07-11 10:18
		ems.add(new ExcelBean("??????", "remark", 0));// 2018-07-11 10:18
		ems.add(new ExcelBean("?????????", "title", 0));// 2018-07-11 10:18
		ems.add(new ExcelBean("????????????", "commentRank", 0));// 2018-07-11 10:18
		ems.add(new ExcelBean("????????????", "commentContent", 0));// 2018-07-11 10:18

		map.put(0, ems);
		// List<VipUser> afterChangeList = changeBuyerStatus(infoList);
		book = ExcelUtils.createExcelFile(VipUser.class, infoList, map, "???????????????");
		return book;
	}
}
