package com.stylefeng.guns.modular.mch.member.service.impl;

import com.stylefeng.guns.modular.system.model.CardOncecard;
import com.stylefeng.guns.modular.system.model.CardPtraincard;
import com.stylefeng.guns.modular.system.model.CardStoredvaluecard;
import com.stylefeng.guns.modular.system.model.CardTimecard;
import com.stylefeng.guns.modular.system.model.ClubAdmin;
import com.stylefeng.guns.modular.system.model.ClubCoach;
import com.stylefeng.guns.modular.system.model.StaffSpecial;
import com.stylefeng.guns.modular.system.model.UserClub;
import com.stylefeng.guns.modular.system.model.UserCommon;
import com.stylefeng.guns.modular.system.model.VipUser;
import com.stylefeng.guns.modular.system.warpper.CardOncecardWarpper;
import com.stylefeng.guns.modular.system.warpper.CardPtraincardWarpper;
import com.stylefeng.guns.modular.system.warpper.CardStoredvaluecardWarpper;
import com.stylefeng.guns.modular.system.warpper.CardTimecardWarpper;
import com.stylefeng.guns.modular.system.warpper.VipUserWarpper;
import com.stylefeng.guns.rest.common.ReturnTip;
import com.stylefeng.guns.rest.common.exception.BizException;
import com.stylefeng.guns.modular.system.dao.VipUserMapper;
import com.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import com.stylefeng.guns.core.common.constant.factory.MapItemFactory;
import com.stylefeng.guns.core.support.ExcelBean;
import com.stylefeng.guns.core.support.ExcelUtils;
import com.stylefeng.guns.core.util.Convert;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.modular.mch.card.service.ICardOncecardService;
import com.stylefeng.guns.modular.mch.card.service.ICardPtraincardService;
import com.stylefeng.guns.modular.mch.card.service.ICardStoredvaluecardService;
import com.stylefeng.guns.modular.mch.card.service.ICardTimecardService;
import com.stylefeng.guns.modular.mch.club.service.IClubCoachService;
import com.stylefeng.guns.modular.mch.club.service.IStaffSpecialService;
import com.stylefeng.guns.modular.mch.member.service.IUserClubService;
import com.stylefeng.guns.modular.mch.member.service.IUserCommonService;
import com.stylefeng.guns.modular.mch.member.service.IVipUserService;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;

import java.beans.IntrospectionException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * VIP????????? ???????????????
 * </p>
 *
 * @author guiyj007
 * @since 2018-06-22
 */
@Service
public class VipUserServiceImpl extends ServiceImpl<VipUserMapper, VipUser> implements IVipUserService {

	@Autowired
	IUserCommonService userCommonService;
	@Autowired
	IUserClubService userClubService;
	@Autowired
	ICardTimecardService cardTimecardService;
	@Autowired
	ICardPtraincardService cardPtraincardService;
	@Autowired
	ICardOncecardService cardOncecardService;
	@Autowired
	ICardStoredvaluecardService cardStoredvaluecardService;
	@Autowired
	IClubCoachService clubCoachService;
	@Autowired
	IStaffSpecialService staffSpecialService;

	@Override
	public List<Map<String, Object>> listVipCards(Integer clubId, Integer vipId) {

		List<Map<String, Object>> listCards = new ArrayList<Map<String, Object>>();
		// ???????????????
		Wrapper<CardTimecard> ewTimecard = new EntityWrapper<>();
		ewTimecard.eq("vip_id", vipId);
		ewTimecard.eq("club_id", clubId);
		List<Map<String, Object>> listTimeCard = cardTimecardService.selectMaps(ewTimecard);
		new CardTimecardWarpper(listTimeCard).warp();
		listCards.addAll(listTimeCard);

		// ???????????????
		Wrapper<CardPtraincard> ewPtraincard = new EntityWrapper<>();
		ewPtraincard.eq("vip_id", vipId);
		ewPtraincard.eq("club_id", clubId);
		List<Map<String, Object>> listPtrainCard = cardPtraincardService.selectMaps(ewPtraincard);
		new CardPtraincardWarpper(listPtrainCard).warp();
		listCards.addAll(listPtrainCard);
		// ????????????
		Wrapper<CardOncecard> ewOncecard = new EntityWrapper<>();
		ewOncecard.eq("vip_id", vipId);
		ewOncecard.eq("club_id", clubId);
		List<Map<String, Object>> listOnceCard = cardOncecardService.selectMaps(ewOncecard);
		new CardOncecardWarpper(listOnceCard).warp();
		listCards.addAll(listOnceCard);
		// ???????????????
		Wrapper<CardStoredvaluecard> ewStoredvaluecard = new EntityWrapper<>();
		ewStoredvaluecard.eq("vip_id", vipId);
		ewStoredvaluecard.eq("club_id", clubId);
		List<Map<String, Object>> listStoredvaluecard = cardStoredvaluecardService.selectMaps(ewStoredvaluecard);
		new CardStoredvaluecardWarpper(listStoredvaluecard).warp();
		listCards.addAll(listStoredvaluecard);

		return listCards;
	}
	
	
	@Override
	public boolean resetTimeCardInfo(Integer vipId) {
		/**
		 * ??????????????????????????????????????????????????????
		 */
		CardTimecard cardTimecard = cardTimecardService.getLongestCard(vipId);
		if (ToolUtil.isEmpty(cardTimecard)) {
			return false;
		}

		/**
		 * ?????????????????????vip?????????
		 */
		VipUser vipUser = new VipUser();
		vipUser.setId(vipId);

		if (cardTimecard.getOpenCardTime().equals(0) || cardTimecard.getOpenCardTime() > DateUtil.timeStamp()) {
			vipUser.setExpireStatus(1); // ?????????
			vipUser.setExpireTime(0);
		} else {
			vipUser.setExpireStatus(2); // ??????
			vipUser.setExpireTime(cardTimecard.getExpireTime());
		}
		this.updateById(vipUser);
		return true;
	}

	@Override
	public boolean resetPtrainCardInfo(Integer vipId) {
		Map<String, Object> cardInfo = getPtrainCardInfo(vipId);
		
		VipUser vipUser = new VipUser();
		vipUser.setId(vipId);
		vipUser.setPersonalTrainerCardCounts((Integer) cardInfo.get("personalTrainerCardCounts"));
		vipUser.setPersonalTrainerCardUsedCounts((Integer) cardInfo.get("personalTrainerCardUsedCounts"));
		vipUser.setPersonalTrainerCardNum((Integer) cardInfo.get("personalTrainerCardNum"));
		this.updateById(vipUser);
		return true;
	}
	
	@Override
	public Map<String, Object> getPtrainCardInfo(Integer vipId) {
		/**
		 * ??????????????????????????????????????????????????????
		 */
		int currentTime = DateUtil.timeStamp();
		
		Wrapper<CardPtraincard> ew = new EntityWrapper<>();
		ew = ew.eq("vip_id", vipId);
		ew = ew.eq("disabled", 0);
		ew = ew.andNew().eq("expire_time", 0).or().gt("expire_time", currentTime);
		
		List<Map<String, Object>> listCards = cardPtraincardService.selectMaps(ew);
		
		Integer cardNum = 0;
		Integer totalCounts = 0;
		Integer usedCounts = 0;
		
		if (ToolUtil.isNotEmpty(listCards)) {
			cardNum = listCards.size();
			for (Map<String, Object> map : listCards) {
				totalCounts += (Integer) map.get("counts");
				usedCounts += (Integer) map.get("counts") - (Integer) map.get("leftCounts");
			}
		}

		return MapItemFactory.composeMap(
			"personalTrainerCardUsedCounts", usedCounts, 
			"personalTrainerCardCounts", totalCounts,
			"personalTrainerCardNum", cardNum
		);
	}
	
	@Override
	public Map<String, Object> getOnceCardInfo(Integer vipId) {
		/**
		 * ??????????????????????????????????????????????????????
		 */
		int currentTime = DateUtil.timeStamp();
		
		Wrapper<CardOncecard> ew = new EntityWrapper<>();
		ew = ew.eq("vip_id", vipId);
		ew = ew.eq("disabled", 0);
		ew = ew.andNew().eq("expire_time", 0).or().gt("expire_time", currentTime);
		
		List<Map<String, Object>> listCards = cardOncecardService.selectMaps(ew);
		
		Integer cardNum = 0;
		Integer totalCounts = 0;
		Integer usedCounts = 0;
		
		if (ToolUtil.isNotEmpty(listCards)) {
			cardNum = listCards.size();
			for (Map<String, Object> map : listCards) {
				totalCounts += (Integer) map.get("counts");
				usedCounts += (Integer) map.get("counts") - (Integer) map.get("leftCounts");
			}
		}

		return MapItemFactory.composeMap("onceCardUsedCounts", usedCounts, 
			"onceCardCounts", totalCounts, "onceCardNum", cardNum);
		 
	}

	@Override
	public boolean resetOnceCardInfo(Integer vipId) {
		Map<String, Object> cardInfo = getOnceCardInfo(vipId);
		
		VipUser vipUser = new VipUser();
		vipUser.setId(vipId);
		vipUser.setOnceCardUsedCounts((Integer) cardInfo.get("onceCardUsedCounts"));
		vipUser.setOnceCardCounts((Integer) cardInfo.get("onceCardCounts"));
		vipUser.setOnceCardNum((Integer) cardInfo.get("onceCardNum"));
		this.updateById(vipUser);
		
		return true;
	}

	@Override
	public boolean resetStoredvalueCardInfo(Integer vipId) {
		/**
		 * ??????????????????????????????????????????????????????
		 */
		int currentTime = DateUtil.timeStamp();
		Wrapper<CardStoredvaluecard> ew = new EntityWrapper<>();
		ew.eq("vip_id", vipId);
		ew.eq("disabled", 0);
		ew.andNew().eq("expire_time", 0).or().gt("expire_time", currentTime);
		List<Map<String, Object>> listCards = cardStoredvaluecardService.selectMaps(ew);
		if (ToolUtil.isEmpty(listCards)) {
			return false;
		}

		Integer cardNum = listCards.size();
		BigDecimal totalCounts = null;
		BigDecimal usedCounts = null;
		for (Map<String, Object> map : listCards) {
			totalCounts = totalCounts.add((BigDecimal) map.get("sumMoney"));
			usedCounts = usedCounts.add(((BigDecimal) map.get("sumMoney")).subtract((BigDecimal) map.get("leftMoney")));
		}

		/**
		 * ?????????????????????vip?????????
		 */
		VipUser vipUser = new VipUser();
		vipUser.setId(vipId);
		vipUser.setStoredValueCardUsedCounts(usedCounts);
		vipUser.setStoredValueCardCounts(totalCounts);
		vipUser.setStoredValueCardNum(cardNum);
		this.updateById(vipUser);
		return true;
	}
	
	private Wrapper<VipUser> getSearchEntityWrapper(Integer clubId, HashMap<String, Object> condition, String hdSearchKey) {
		Wrapper<VipUser> ew = new EntityWrapper<>();
		Integer exactSearch = Convert.toInt(condition.get("exact"), 0);

		if (ToolUtil.isNotEmpty(condition.get("id")) && !condition.get("id").equals(0)) {
			ew = ew.eq("id", (Integer) condition.get("id"));
		}
		if (ToolUtil.isNotEmpty(clubId) && !clubId.equals(0)) {
			ew = ew.eq("club_id", clubId);
		}
		if (ToolUtil.isNotEmpty(condition.get("gender")) && !condition.get("gender").equals("0")) {
			ew = ew.eq("gender", ToolUtil.toInt(condition.get("gender")));
		}
		if (ToolUtil.isNotEmpty(condition.get("coachId"))) {
			ew = ew.eq("coach_id", (Integer) condition.get("coachId"));
		}

		// ?????????????????????
		if (ToolUtil.isNotEmpty(condition.get("commonKey"))) {
			String commonKey = (String) condition.get("commonKey");
			ew = ew.andNew().like("realname", commonKey).or().like("phone", commonKey).or().like("card_number", commonKey);
		}

		if (ToolUtil.isNotEmpty(condition.get("realname"))) {
			if (exactSearch.equals(0)) {
				ew = ew.like("realname", (String) condition.get("realname"));
			} else {
				ew = ew.eq("realname", (String) condition.get("realname"));
			}
		}
		if (ToolUtil.isNotEmpty(condition.get("nickname"))) {
			ew = ew.like("nickname", (String) condition.get("nickname"));
		}
		if (ToolUtil.isNotEmpty(condition.get("phone"))) {
			if (exactSearch.equals(0)) {
				ew = ew.like("phone", (String) condition.get("phone"));
			} else {
				ew = ew.eq("phone", (String) condition.get("phone"));
			}
		}
		if (ToolUtil.isNotEmpty(condition.get("cardNumber"))) {
			ew = ew.like("card_number", (String) condition.get("cardNumber"));
		}
		if (ToolUtil.isNotEmpty(condition.get("cardType"))) {
			ew = ew.like("card_type", (String) condition.get("cardType"));
		}
		if (ToolUtil.isNotEmpty(condition.get("membershipName"))) {
			ew = ew.like("membership_name", (String) condition.get("membershipName"));
		}
		
		if (ToolUtil.isNotEmpty(condition.get("coachName"))) {
			ew = ew.like("coach_name", (String) condition.get("coachName"));
		}
		if (ToolUtil.isNotEmpty(condition.get("startDate"))) {
			ew = ew.ge("insert_time", DateUtil.date2TimeStamp((String) condition.get("startDate"), "yyyy-MM-dd"));
		}
		if (ToolUtil.isNotEmpty(condition.get("endDate"))) {
			ew = ew.le("insert_time", DateUtil.date2TimeStamp((String) condition.get("endDate"), "yyyy-MM-dd"));
		}

		// ??????????????????
		switch (hdSearchKey) {
		case "allMember":
			break;
		case "privateVip":
			// ????????????
			ew = ew.gt("personal_trainer_card_counts", 0);
			break;
		case "noExpireMember":
			// ???????????????
			ew = ew.gt("expire_time", DateUtil.timeStamp());
			break;
		case "insertMember":
			// ????????????
			ew = ew.gt("insert_time", DateUtil.timeStamp() - 10 * 86400);
			break;
		case "noneExpire":
			// ????????????
			ew = ew.gt("expire_time", DateUtil.timeStamp());
			ew = ew.lt("expire_time", DateUtil.timeStamp() + 30 * 86400);
			break;
		case "noneDue":
			// ???????????????
			ew = ew.lt("expire_time", DateUtil.timeStamp());
			ew = ew.gt("expire_time", DateUtil.timeStamp() - 30 * 86400);
			break;
		case "birthday":
			// ????????????
			// @TODO
			ew = ew.gt("birthday", DateUtil.timeStamp());
			ew = ew.lt("birthday", DateUtil.timeStamp() + 30 * 86400);
		case "noBindMembership":
			// ???????????????
			ew = ew.eq("membership_id", 0);
			break;
		case "noBindCoach":
			// ???????????????
			ew = ew.eq("coach_id", 0);
			break;
		case "noMaintain":
			// ???????????????
			ew = ew.lt("last_maintain_time", DateUtil.timeStamp() - 30 * 86400);
			break;
		case "noSign":
			// ???????????????
			ew = ew.lt("last_sign_time", DateUtil.timeStamp() - 30 * 86400);
			break;
		default:
			break;
		}
		return ew;
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
	 * @param hdSearchKey
	 *            ??????????????????
	 * @return List<>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public Page<VipUser> pageList(Page<VipUser> page, Integer clubId, HashMap<String, Object> condition,
			String hdSearchKey) {
		Wrapper<VipUser> ew = getSearchEntityWrapper(clubId, condition, hdSearchKey);

		List<Map<String, Object>> result = baseMapper.selectMapsPage(page, ew);
		return page.setRecords((List<VipUser>) new VipUserWarpper(result).warp());
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
	public  List<Map<String, Object>> getExportList(Integer clubId, HashMap<String, Object> condition,
			String hdSearchKey) {
		Wrapper<VipUser> ew = getSearchEntityWrapper(clubId, condition, hdSearchKey);

		//????????????1000?????????
		Page<VipUser> page = new Page<>(0, 1000, "id",true);
		List<Map<String, Object>> result = baseMapper.selectMapsPage(page, ew);
		return (List<Map<String, Object>>) new VipUserWarpper(result).warp();
//		return page.setRecords((List<VipUser>) new VipUserWarpper(result).warp());
	}

	/**
	 * <p>
	 * ??????????????????????????????
	 * </p>
	 *
	 * @param hdSearchKey
	 *            ??????????????????
	 * @return List<>
	 */
	@Override
	public Map<String, Object> statCountMap(Integer clubId, String hdSearchKey) {
		Wrapper<VipUser> ew = new EntityWrapper<>();
		ew = ew.eq("club_id", clubId);
		
		// ??????????????????
		switch (hdSearchKey) {
		case "allMember":
			break;
		case "privateVip":
			// ????????????
			ew = ew.gt("personal_trainer_card_counts", 0);
			break;
		case "noExpireMember":
			// ???????????????
			ew = ew.gt("expire_time", DateUtil.timeStamp());
			break;
		case "insertMember":
			// ????????????
			ew = ew.gt("insert_time", DateUtil.timeStamp() - 10 * 86400);
			break;
		case "noneExpire":
			// ????????????
			ew = ew.gt("expire_time", DateUtil.timeStamp());
			ew = ew.lt("expire_time", DateUtil.timeStamp() + 30 * 86400);
			break;
		case "noneDue":
			// ???????????????
			ew = ew.lt("expire_time", DateUtil.timeStamp());
			ew = ew.gt("expire_time", DateUtil.timeStamp() - 30 * 86400);
			break;
		case "birthday":
			// ????????????
			// @TODO
			ew = ew.gt("birthday", DateUtil.timeStamp());
			ew = ew.lt("birthday", DateUtil.timeStamp() + 30 * 86400);
		case "noBindMembership":
			// ???????????????
			ew = ew.eq("membership_id", 0);
			break;
		case "noBindCoach":
			// ???????????????
			ew = ew.eq("coach_id", 0);
			break;
		case "noMaintain":
			// ???????????????
			ew = ew.lt("last_maintain_time", DateUtil.timeStamp() - 30 * 86400);
			break;
		case "noSign":
			// ???????????????
			ew = ew.lt("last_sign_time", DateUtil.timeStamp() - 30 * 86400);
			break;
		default:
			break;
		}

		Integer cnt = baseMapper.selectCount(ew);
		return MapItemFactory.getStatItem(cnt, 0, null);
	}
	
	@Override
	public VipUser joinByExistPhone(int clubId, int userId, String phone, String nickname) {
		//??????phone????????????????????????????????? vip??????
		Wrapper<VipUser> ew = new EntityWrapper<>();
		ew = ew.eq("phone", phone);
		ew = ew.eq("club_id", clubId);
//		ew = ew.eq("user_id", 0);
		List<VipUser> listVipUser = this.selectList(ew);
		
		//?????????????????????????????????
		int count = listVipUser.size();
		if (count != 1) {
			return null;
		}
		
		//???????????????user???????????????????????????
		VipUser entity = listVipUser.get(0);
		if (entity.getUserId().equals(userId)) {
			return entity;
		}
		
		try {
			this.bindMember(clubId, entity.getId(), userId, nickname, "user");
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			return null;
		}
		
		return entity;		
	}

	/**
	 * <p>
	 * ???vip?????? ???????????????????????????
	 * </p>
	 * 
	 * @param clubId
	 * @param vipId
	 * @param bindingId
	 * @param type
	 *            ??? user coach membership
	 * 
	 * @return
	 */
	@Override
	public boolean bindMember(int clubId, int vipId, int bindingId, String name, String type) {
		VipUser entity = new VipUser();

		switch (type) {
		case "user":
			entity.setUserId(bindingId);
			// ????????????????????????id????????????
			if (ToolUtil.isEmpty(name)) {
				name = ConstantFactory.me().getUserCommonNicknameById(bindingId);
			}
			entity.setNickname(name);

			// ??????user_club??????vipId
//			Wrapper<UserClub> ew = new EntityWrapper<>();
//			ew = ew.eq("user_id", bindingId);
//			ew = ew.eq("club_id", clubId);
//			
//			UserClub userClub = new UserClub();
//			userClub.setVipId(vipId);
//			userClubService.update(userClub, ew);

			// ???????????????????????????APP??????????????????
			this.unbindUser(bindingId);
			break;
		case "coach":
			entity.setCoachId(bindingId);
			// ????????????????????????id????????????
			if (ToolUtil.isEmpty(name)) {
				name = ConstantFactory.me().getCoachNameById(bindingId);
			}
			entity.setCoachName(name);
			break;
		case "membership":
			entity.setMembershipId(bindingId);
			// ????????????????????????id????????????
			if (ToolUtil.isEmpty(name)) {
				name = ConstantFactory.me().getStaffSpecialNameById(bindingId);
			}
			entity.setMembershipName(name);
			break;
		default:
			return false;
		}

		Wrapper<VipUser> ew = new EntityWrapper<>();
		ew = ew.eq("id", vipId);
		ew = ew.eq("club_id", clubId);

		try {
			this.update(entity, ew);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new BizException(500, "??????????????????");
		}

		return true;
	}

	/**
	 * ???????????????APP??????
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public Boolean unbindUser(Integer userId) {
		VipUser entity = new VipUser();
		entity.setUserId(0);
		entity.setNickname("");

		Wrapper<VipUser> ew = new EntityWrapper<>();
		ew = ew.eq("user_id", userId);

		try {
			this.update(entity, ew);
			return true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * ?????????????????????????????????
	 * 
	 * @param userId
	 * @return
	 */
	@Override
	public Boolean fillCard(Integer clubId, Integer vipId, String cardNumber, BigDecimal serviceCharge)
			throws Exception {
		VipUser entity = new VipUser();
		entity.setCardNumber(cardNumber);

		// ??????????????????????????????????????????????????????
		Wrapper<VipUser> ew = new EntityWrapper<>();
		ew = ew.eq("id", vipId);
		ew = ew.eq("club_id", clubId);
		return this.update(entity, ew);
		// @todo ????????????
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean uploadVipUserInfoExcel(Integer clubId, InputStream in, MultipartFile file) throws Exception {
		List listErr = new ArrayList();	//???????????????????????????

		/**
		 * 1. ??????excel??????????????????????????????
		 */
		List<List<Object>> listob = ExcelUtils.getBankListByExcel(in, file.getOriginalFilename(), 29);
//		List<VipUser> vipUserInfoList = new ArrayList<VipUser>();

		String sFirstColumVal = null;
		String sBirthdayVal = null;
		String sBirthdayFormat = null;
		String sCoachName = null;
		String sMembershipName = null;
		int newVipId = 0;
		int currentTime = DateUtil.timeStamp();

		for (int i = 0; i < listob.size(); i++) {

			List<Object> ob = listob.get(i);
			
			// ??????????????????????????????????????????????????????
			sFirstColumVal = Convert.toStr(ob.get(0)).trim();
			if (sFirstColumVal.startsWith("????????????") || sFirstColumVal.startsWith("??????")) {
				continue;
			}
			
			if (ob.size() != 29) {
				listErr.add("???" + (i+1) + "???(" + String.valueOf(ob.get(0)) + ") ??????????????????????????????????????????????????????<br>");
				continue;
			}
			
			//??????????????????
			if (ToolUtil.isEmpty(ob.get(0))) {
				listErr.add("???" + (i+1) + "???(" + String.valueOf(ob.get(1)) + ") ????????????????????????????????????????????????<br>");
				continue;
			}
			if (ToolUtil.isEmpty(ob.get(1))) {
				listErr.add("???" + (i+1) + "???(" + String.valueOf(ob.get(0)) + ") ????????????????????????????????????????????????<br>");
				continue;
			}
			if (ToolUtil.isEmpty(ob.get(4))) {
//				listErr.add("???" + (i+1) + "???(" + String.valueOf(ob.get(0)) + ") ?????????????????????????????????????????????<br>");
//				continue;
			}

			/**
			 * 2. ????????????vip????????????
			 */
			VipUser vipUserBean = new VipUser();
			vipUserBean.setClubId(clubId);
			vipUserBean.setInsertTime(currentTime);
			// 1 ????????????
			vipUserBean.setCardNumber(String.valueOf(ob.get(0)));
			// 2 ??????
			vipUserBean.setRealname(String.valueOf(ob.get(1)));
			// 3 ??????
			vipUserBean.setGender(String.valueOf(ob.get(2)).equals("???") ? 2 : 1);
			// 4 ??????
			sBirthdayVal = String.valueOf(ob.get(3));
			if (sBirthdayVal.contains("-")) {
				sBirthdayFormat = "yyyy-MM-dd";
			} else if (sBirthdayVal.contains("/")) {
				sBirthdayFormat = "yyyy/MM/dd";
			} else if (sBirthdayVal.contains(".")) {
				sBirthdayFormat = "yyyy.MM.dd";
			} else if (sBirthdayVal.contains("???")) {
				sBirthdayFormat = "yyyy???MM???dd???";
			} else if (sBirthdayVal.contains("???")) {
				sBirthdayFormat = "yyyy???MM???dd???";
			} else {
				sBirthdayFormat = "yyyyMMdd";
			}
			vipUserBean.setBirthday(DateUtil.date2TimeStamp(sBirthdayVal, sBirthdayFormat));
			// 5 ??????
			vipUserBean.setPhone(String.valueOf(ob.get(4)));
			// 6 ???????????????
			vipUserBean.setIdCardNumber(String.valueOf(ob.get(5)));
			// 7 ??????
			vipUserBean.setAddress(String.valueOf(ob.get(6)));
			// 8 ???????????????
			vipUserBean.setCardType(String.valueOf(ob.get(7)));
			// 9 ?????????????????????????????????
			sMembershipName = String.valueOf(ob.get(8));
			try {
				StaffSpecial staffSpecial = staffSpecialService.getByRealname(clubId, sMembershipName, 0);
				vipUserBean.setMembershipId(staffSpecial.getId());
				vipUserBean.setMembershipName(staffSpecial.getRealname());
			} catch (Exception e) {
				// TODO: handle exception
				vipUserBean.setMembershipId(0);
				vipUserBean.setMembershipName(sMembershipName);
			}
			// 10 ?????????????????????????????????
			sCoachName = String.valueOf(ob.get(9));
			try {
				ClubCoach clubCoach = clubCoachService.getByRealname(clubId, sCoachName);
				vipUserBean.setCoachId(clubCoach.getId());
				vipUserBean.setCoachName(clubCoach.getRealname());
			} catch (Exception e) {
				// TODO: handle exception
				vipUserBean.setCoachId(0);
				vipUserBean.setCoachName(sCoachName);
			}
			// 11 ????????????
			vipUserBean.setRemark(String.valueOf(ob.get(28)));

			// ??????????????????
			try {
				if (!this.insert(vipUserBean)) {
					listErr.add("???" + (i+1) + "???(" + vipUserBean.getRealname() + ") ????????????");
					continue;
				}
				newVipId = vipUserBean.getId();
			} catch (DuplicateKeyException e) {
				// TODO: handle exception
				listErr.add("???" + (i+1) + "???(" + vipUserBean.getRealname() + ") ??????????????????????????????" +vipUserBean.getCardNumber() +
						"??????????????????<" +vipUserBean.getRealname() + vipUserBean.getPhone() + ">?????????????????????????????????????????????<br>");
				continue;
			} catch (Exception e) {
				e.printStackTrace();
				String _errMsg = e.getMessage();
				int _start = _errMsg.indexOf("Exception: ") + 10;
				int _end = _errMsg.indexOf("### The error may involve");
				
				listErr.add("???" + (i+1) + "???(" + vipUserBean.getRealname() + ") ????????????????????????" + _errMsg.substring(_start, _end) + "<br>");
				continue;
			}

			/**
			 * 3. ???????????????????????????
			 */
			// ??????????????????????????????
			List<?> listTimecardActualInsertTime = new ArrayList<>();
			List<?> listTimecardOpenCardTime = new ArrayList<>();
			List<?> listTimecardExpireTime = new ArrayList<>();
			List<?> listTimecardCardTitle = new ArrayList<>();
			List<?> listTimecardActualMoney = new ArrayList<>();

			if (!String.valueOf(ob.get(12)).trim().equals("")) {
				// ????????????
				listTimecardActualInsertTime = Arrays.asList(String.valueOf(ob.get(10)).trim().split("\\|"));
				listTimecardOpenCardTime = Arrays.asList(String.valueOf(ob.get(11)).trim().split("\\|"));
				listTimecardExpireTime = Arrays.asList(String.valueOf(ob.get(12)).trim().split("\\|"));
				listTimecardCardTitle = Arrays.asList(String.valueOf(ob.get(13)).trim().split("\\|"));
				listTimecardActualMoney = Arrays.asList(String.valueOf(ob.get(14)).trim().split("\\|"));
			}
			
			for (int j = 0; j < listTimecardExpireTime.size(); j++) {
				Map<String, Object> mapTimeCardEntity = new HashMap<>();
				mapTimeCardEntity.put("id", 0);
				mapTimeCardEntity.put("clubId", clubId);
				mapTimeCardEntity.put("vipId", newVipId);
				mapTimeCardEntity.put("realname", vipUserBean.getRealname());
				mapTimeCardEntity.put("remark", "????????????");

				// ????????????
				String sActualInsertTime = new String();
				String sActualInsertFormat = new String();
				sActualInsertTime = (String) listTimecardActualInsertTime.get(j);
				if (sActualInsertTime.contains("-")) {
					sActualInsertFormat = "yyyy-MM-dd";
				} else if (sActualInsertTime.contains("/")) {
					sActualInsertFormat = "yyyy/MM/dd";
				} else {
					sActualInsertFormat = "yyyyMMdd";
				}
				mapTimeCardEntity.put("actualInsertTime",
						DateUtil.date2TimeStamp(sActualInsertTime, sActualInsertFormat));

				// ????????????
				String sOpenCardTime = new String();
				String sOpenCardTimeFormat = new String();
				sOpenCardTime = (String) listTimecardOpenCardTime.get(j);
				if (sOpenCardTime.contains("-")) {
					sOpenCardTimeFormat = "yyyy-MM-dd";
				} else if (sOpenCardTime.contains("/")) {
					sOpenCardTimeFormat = "yyyy/MM/dd";
				} else {
					sOpenCardTimeFormat = "yyyyMMdd";
				}
				mapTimeCardEntity.put("openCardTime", DateUtil.date2TimeStamp(sOpenCardTime, sOpenCardTimeFormat));

				// ????????????
				String sExpireTime = new String();
				String sExpireTimeFormat = new String();
				sExpireTime = (String) listTimecardExpireTime.get(j);
				if (sExpireTime.contains("-") && sExpireTime.length() >= 10) {
					sExpireTimeFormat = "yyyy-MM-dd";
				} else if (sExpireTime.contains("/") && sExpireTime.length() >= 10) {
					sExpireTimeFormat = "yyyy/MM/dd";
				} else if (sExpireTime.contains("-") && sExpireTime.length() < 9) {
					//??????????????????
					sExpireTimeFormat = "-";
				} else {
					sExpireTimeFormat = "yyyyMMdd";
				}
				// ?????????????????????????????????????????????????????????????????????0
				if (sExpireTimeFormat.equals("-")) {
					mapTimeCardEntity.put("expireDays", -Convert.toInt(sExpireTime));
					mapTimeCardEntity.put("expireTime", DateUtil.date2TimeStamp(sOpenCardTime, sOpenCardTimeFormat)-86400 * Convert.toInt(sExpireTime));
//					mapTimeCardEntity.put("openCardTime", 0);
				} else {
					mapTimeCardEntity.put("expireTime", DateUtil.date2TimeStamp(sExpireTime, sExpireTimeFormat));
					mapTimeCardEntity.put("expireDays", (DateUtil.date2TimeStamp(sExpireTime, sExpireTimeFormat) - DateUtil.date2TimeStamp(sOpenCardTime, sOpenCardTimeFormat)) / 86400);
				}

				// ??????
				mapTimeCardEntity.put("title", listTimecardCardTitle.get(j));

				// ????????????
				mapTimeCardEntity.put("actualMoney", Convert.toBigDecimal(listTimecardActualMoney.get(j)));

				// ??????
				mapTimeCardEntity.put("membershipId", vipUserBean.getMembershipId());
				mapTimeCardEntity.put("membershipName", vipUserBean.getMembershipName());

				try {
					cardTimecardService.save(mapTimeCardEntity);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

			/**
			 * 4. ???????????????????????????
			 */
			// ??????????????????????????????
			List<?> listPtraincardExpireTime = new ArrayList<>();
			List<?> listPtraincardCardTitle = new ArrayList<>();
			List<?> listPtraincardCounts = new ArrayList<>();
			List<?> listPtraincardActualMoney = new ArrayList<>();
			List<?> listPtraincardCoach = new ArrayList<>();

			if (!String.valueOf(ob.get(17)).trim().equals("")) {
				listPtraincardExpireTime = Arrays.asList(String.valueOf(ob.get(15)).trim().split("\\|"));
				listPtraincardCardTitle = Arrays.asList(String.valueOf(ob.get(16)).trim().split("\\|"));
				listPtraincardCounts = Arrays.asList(String.valueOf(ob.get(17)).trim().split("\\|"));
				listPtraincardActualMoney = Arrays.asList(String.valueOf(ob.get(18)).trim().split("\\|"));
				listPtraincardCoach = Arrays.asList(String.valueOf(ob.get(19)).trim().split("\\|"));
			}
			
			for (int j = 0; j < listPtraincardCardTitle.size(); j++) {
				Map<String, Object> mapPtrainCardEntity = new HashMap<>();
				mapPtrainCardEntity.put("id", 0);
				mapPtrainCardEntity.put("clubId", clubId);
				mapPtrainCardEntity.put("vipId", newVipId);
				mapPtrainCardEntity.put("realname", vipUserBean.getRealname());
				mapPtrainCardEntity.put("remark", "????????????");

				// ??????????????????????????????????????????????????????????????????
				String sExpireTime = new String();
				String sExpireTimeFormat = new String();
				if (listPtraincardExpireTime.size() < j + 1) {
					sExpireTime = (String) listPtraincardExpireTime.get(0);
				} else {
					sExpireTime = (String) listPtraincardExpireTime.get(j);
				}

				if (sExpireTime.contains("-") && sExpireTime.length() >= 10) {
					sExpireTimeFormat = "yyyy-MM-dd";
				} else if (sExpireTime.contains("/") && sExpireTime.length() >= 10) {
					sExpireTimeFormat = "yyyy/MM/dd";
				} else {
					sExpireTimeFormat = "yyyyMMdd";
				}
				mapPtrainCardEntity.put("expireTime", DateUtil.date2TimeStamp(sExpireTime, sExpireTimeFormat));

				// ??????
				mapPtrainCardEntity.put("title", listPtraincardCardTitle.get(j));
				// ????????????
				mapPtrainCardEntity.put("counts", Convert.toInt(listPtraincardCounts.get(j)));
				mapPtrainCardEntity.put("leftCounts", Convert.toInt(listPtraincardCounts.get(j)));
				// ????????????
				mapPtrainCardEntity.put("actualMoney", Convert.toBigDecimal(listPtraincardActualMoney.get(j)));

				// ??????
				String _scoachName = new String();
				ClubCoach _clubCoach = new ClubCoach();
				_scoachName = (String) listPtraincardCoach.get(j);
				try {
					_clubCoach = clubCoachService.getByRealname(clubId, _scoachName);
					mapPtrainCardEntity.put("coachId", _clubCoach.getId());
					mapPtrainCardEntity.put("coachName", _scoachName);

				} catch (Exception e) {
					// TODO: handle exception
					mapPtrainCardEntity.put("coachId", 0);
					mapPtrainCardEntity.put("coachName", _scoachName);
				}

				try {
					cardPtraincardService.save(mapPtrainCardEntity);
					resetPtrainCardInfo(newVipId);
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}
			}

			/**
			 * 5. ????????????????????????
			 */
			// ??????????????????????????????
			List<?> listOncecardExpireTime = new ArrayList<>();
			List<?> listOncecardCardTitle = new ArrayList<>();
			List<?> listOncecardCounts = new ArrayList<>();
			List<?> listOncecardActualMoney = new ArrayList<>();

			if (!String.valueOf(ob.get(22)).trim().equals("")) {
				listOncecardExpireTime = Arrays.asList(String.valueOf(ob.get(20)).trim().split("\\|"));
				listOncecardCardTitle = Arrays.asList(String.valueOf(ob.get(21)).trim().split("\\|"));
				listOncecardCounts = Arrays.asList(String.valueOf(ob.get(22)).trim().split("\\|"));
				listOncecardActualMoney = Arrays.asList(String.valueOf(ob.get(23)).trim().split("\\|"));
			}
			
			for (int j = 0; j < listOncecardCardTitle.size(); j++) {
				Map<String, Object> mapOnceCardEntity = new HashMap<>();
				mapOnceCardEntity.put("id", 0);
				mapOnceCardEntity.put("clubId", clubId);
				mapOnceCardEntity.put("vipId", newVipId);
				mapOnceCardEntity.put("realname", vipUserBean.getRealname());
				mapOnceCardEntity.put("remark", "????????????");

				// ????????????
				String sExpireTime = new String();
				String sExpireTimeFormat = new String();
				if (listOncecardExpireTime.size() < j + 1) {
					sExpireTime = (String) listOncecardExpireTime.get(0);
				} else {
					sExpireTime = (String) listOncecardExpireTime.get(j);
				}

				if (sExpireTime.contains("-") && sExpireTime.length() >= 10) {
					sExpireTimeFormat = "yyyy-MM-dd";
				} else if (sExpireTime.contains("/") && sExpireTime.length() >= 10) {
					sExpireTimeFormat = "yyyy/MM/dd";
				} else {
					sExpireTimeFormat = "yyyyMMdd";
				}
				mapOnceCardEntity.put("expireTime", DateUtil.date2TimeStamp(sExpireTime, sExpireTimeFormat));

				// ??????
				mapOnceCardEntity.put("title", listOncecardCardTitle.get(j));
				// ????????????
				mapOnceCardEntity.put("counts", Convert.toInt(listOncecardCounts.get(j)));
				mapOnceCardEntity.put("leftCounts", Convert.toInt(listOncecardCounts.get(j)));
				// ????????????
				mapOnceCardEntity.put("actualMoney", Convert.toBigDecimal(listOncecardActualMoney.get(j)));

				try {
					cardOncecardService.save(mapOnceCardEntity);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

			/**
			 * 6. ???????????????????????????
			 */
			// ??????????????????????????????
			List<?> listStoredcardExpireTime = new ArrayList<>();
			List<?> listStoredcardCardTitle = new ArrayList<>();
			List<?> listStoredcardCounts = new ArrayList<>();
			List<?> listStoredcardActualMoney = new ArrayList<>();

			if (!String.valueOf(ob.get(26)).trim().equals("")) {
				listStoredcardExpireTime = Arrays.asList(String.valueOf(ob.get(24)).trim().split("\\|"));
				listStoredcardCardTitle = Arrays.asList(String.valueOf(ob.get(25)).trim().split("\\|"));
				listStoredcardCounts = Arrays.asList(String.valueOf(ob.get(26)).trim().split("\\|"));
				listStoredcardActualMoney = Arrays.asList(String.valueOf(ob.get(27)).trim().split("\\|"));
			}

			for (int j = 0; j < listStoredcardCounts.size(); j++) {
				Map<String, Object> mapStoredcardEntity = new HashMap<>();
				mapStoredcardEntity.put("id", 0);
				mapStoredcardEntity.put("clubId", clubId);
				mapStoredcardEntity.put("vipId", newVipId);
				mapStoredcardEntity.put("realname", vipUserBean.getRealname());
				mapStoredcardEntity.put("remark", "????????????");

				// ????????????
				String sExpireTime = new String();
				String sExpireTimeFormat = new String();
				if (listStoredcardExpireTime.size() < j + 1) {
					sExpireTime = (String) listStoredcardExpireTime.get(0);
				} else {
					sExpireTime = (String) listStoredcardExpireTime.get(j);
				}
				if (sExpireTime.contains("-") && sExpireTime.length() >= 10) {
					sExpireTimeFormat = "yyyy-MM-dd";
				} else if (sExpireTime.contains("/") && sExpireTime.length() >= 10) {
					sExpireTimeFormat = "yyyy/MM/dd";
				} else {
					sExpireTimeFormat = "yyyyMMdd";
				}
				mapStoredcardEntity.put("expireTime", DateUtil.date2TimeStamp(sExpireTime, sExpireTimeFormat));

				// ??????
				mapStoredcardEntity.put("title", listStoredcardCardTitle.get(j));
				// ????????????
				mapStoredcardEntity.put("sumMoney", Convert.toBigDecimal(listStoredcardCounts.get(j)));
				mapStoredcardEntity.put("leftMoney", Convert.toBigDecimal(listStoredcardCounts.get(j)));
				// ????????????
				mapStoredcardEntity.put("actualMoney", Convert.toBigDecimal(listStoredcardActualMoney.get(j)));

				try {
					cardStoredvaluecardService.save(mapStoredcardEntity);
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}

		}
		
		if (listErr.size() == 0) {
			return true;
		} else {
			int successNum = listob.size() - listErr.size() - 2;
			throw new Exception("<span color='green'>????????????" + successNum + "??????????????????" + listErr.size() + "???</span><br>" + listErr.toString());
		}
	}
	
	@Override
	public XSSFWorkbook exportVipUserInfoExcel(List<Map<String, Object>> vipUserInfoList) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException, ClassNotFoundException, IntrospectionException, ParseException {
		//debug
//		Wrapper<VipUser> ew = new EntityWrapper<>();
//		ew = ew.eq("club_id", clubId);
//		List<Map<String, Object>> vipUserInfoList = this.getExportList(clubId, new HashMap<String, Object>(), "");

		List<ExcelBean> ems = new ArrayList<>();
		Map<Integer, List<ExcelBean>> map = new LinkedHashMap<>();
		
		XSSFWorkbook book = null;
		ems.add(new ExcelBean("??????", "realname", 0));
		ems.add(new ExcelBean("?????????", "phone", 0));
		ems.add(new ExcelBean("??????", "genderCn", 0));
		ems.add(new ExcelBean("????????????", "cardNumber", 0));//201807118660
		ems.add(new ExcelBean("????????????", "insertDate", 0));//2018-07-11 10:18
		ems.add(new ExcelBean("????????????", "membershipName", 0));//?????????
		ems.add(new ExcelBean("????????????", "clubName", 0));//?????????,
		ems.add(new ExcelBean("????????????", "idCardNumber", 0));//-
		ems.add(new ExcelBean("??????", "birthdayStr", 0));//-
		ems.add(new ExcelBean("????????????", "address", 0));//-
		ems.add(new ExcelBean("????????????", "cardType", 0));//??????
		ems.add(new ExcelBean("?????????????????????", "expireTime", 0));//????????????2019-07-11 or  ????????????365???
//		ems.add(new ExcelBean("?????????????????????", "insertTime", 0));//3000 or 0
		ems.add(new ExcelBean("???????????????????????????", "personalTrainerCardCount", 0));//1?????????29???  or ????????????
//		ems.add(new ExcelBean("?????????????????????", "insertTime", 0));//2019-12-01 or -
//		ems.add(new ExcelBean("?????????????????????", "insertTime", 0));// 2000 or 0
		ems.add(new ExcelBean("????????????????????????", "onceCardCount", 0));//1?????????100??? or ?????????
//		ems.add(new ExcelBean("??????????????????", "insertTime", 0));//2019-7-30 or -
//		ems.add(new ExcelBean("??????????????????", "insertTime", 0));//3000 or 0
		ems.add(new ExcelBean("???????????????????????????", "storedValueCardCount", 0));
//		ems.add(new ExcelBean("?????????????????????", "insertTime", 0));//2019-7-30 or -
//		ems.add(new ExcelBean("?????????????????????", "insertTime", 0));
		ems.add(new ExcelBean("??????????????????", "lastSignTime", 0));//2019-7-30 or -
		ems.add(new ExcelBean("??????", "remark", 0));

		map.put(0, ems);
		// List<VipUser> afterChangeList = changeBuyerStatus(vipUserInfoList);
		book = ExcelUtils.createExcelFile(VipUser.class, vipUserInfoList, map, "vip????????????");
		return book;
	}

}
