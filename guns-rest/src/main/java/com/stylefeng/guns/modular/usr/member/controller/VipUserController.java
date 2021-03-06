package com.stylefeng.guns.modular.usr.member.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import com.stylefeng.guns.core.common.constant.factory.MapItemFactory;
import com.stylefeng.guns.core.common.constant.factory.PageFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.beans.IntrospectionException;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.stylefeng.guns.core.util.Convert;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.core.util.MD5Util;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.modular.system.model.CardOncecard;
import com.stylefeng.guns.modular.system.model.CardPtraincard;
import com.stylefeng.guns.modular.system.model.CardStoredvaluecard;
import com.stylefeng.guns.modular.system.model.CardTimecard;
import com.stylefeng.guns.modular.system.model.ClubAdmin;
import com.stylefeng.guns.modular.system.model.VipUser;
import com.stylefeng.guns.modular.system.warpper.CardOncecardWarpper;
import com.stylefeng.guns.modular.system.warpper.CardPtraincardWarpper;
import com.stylefeng.guns.modular.system.warpper.CardStoredvaluecardWarpper;
import com.stylefeng.guns.modular.system.warpper.CardTimecardWarpper;
import com.stylefeng.guns.modular.system.warpper.VipUserWarpper;
import com.stylefeng.guns.rest.common.ReturnTip;
import com.stylefeng.guns.modular.mch.card.service.ICardOncecardService;
import com.stylefeng.guns.modular.mch.card.service.ICardPtraincardService;
import com.stylefeng.guns.modular.mch.card.service.ICardStoredvaluecardService;
import com.stylefeng.guns.modular.mch.card.service.ICardTimecardService;
import com.stylefeng.guns.modular.mch.member.service.IVipUserService;

/**
 * VIP???????????????
 *
 * @author guiyj007
 * @Date 2018-06-22 16:45:03
 */
@Controller("usr_vipUserController")
@RequestMapping("/usr/member/vipUser")

public class VipUserController extends BaseController {

	@Autowired
	private IVipUserService vipUserService;
	@Autowired
	private ICardTimecardService timecardService;
	@Autowired
	private ICardPtraincardService ptraincardService;
	@Autowired
	private ICardOncecardService oncecardService;
	@Autowired
	private ICardStoredvaluecardService storedvaluecardService;
	
	/**
	 * ????????????????????????????????????
	 */
	@GetMapping(value = "/listCards")
	@ResponseBody
	public Object listCards() {
		HttpServletRequest request = this.getHttpServletRequest();
		HashMap<String, String> mapMember = (HashMap<String, String>) request.getAttribute("mapMember");
		Integer userId = Convert.toInt(mapMember.get("id"), 0);
    	if (userId.equals(0)) {
    		return new ReturnTip(500, "??????????????????");
    	}
    	
    	Integer clubId = Convert.toInt(request.getParameter("clubId"), 0);
		if (ToolUtil.isEmpty(clubId)) {
			return ResponseEntity.ok(new ReturnTip(500, "?????????????????????"));
		}
		
		//???vipid
		Integer vipId = ConstantFactory.me().getVipIdByClubUserid(userId, clubId);
		
		List<Map<String, Object>> listCards = vipUserService.listVipCards(clubId, vipId);
		return new ReturnTip(0, "??????", MapItemFactory.composeMap("cardList", listCards));
	}
	
	
	
	
	
	/**
	 * ??????VIP????????????
	 */
	@RequestMapping(value = "/pagelist")
	@ResponseBody
	public Object pagelist() {
		HttpServletRequest request = this.getHttpServletRequest();
		ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
		Integer clubId = clubAdmin.getClubId();
		if (ToolUtil.isEmpty(clubId)) {
			return ResponseEntity.ok(new ReturnTip(500, "?????????????????????"));
		}

		// ??????????????????
		HashMap<String, Object> mapCondition = new HashMap<>();
		mapCondition.put("exact", Convert.toStr(request.getParameter("exact")));
		mapCondition.put("id", Convert.toInt(request.getParameter("vipId"), 0));
		mapCondition.put("realname", ToolUtil.toStr(request.getParameter("realname"), null));
		mapCondition.put("phone", ToolUtil.toStr(request.getParameter("phone"), null));
		mapCondition.put("nickname", ToolUtil.toStr(request.getParameter("nickname"), null));
		mapCondition.put("cardNumber", ToolUtil.toStr(request.getParameter("cardNumber"), null));
		mapCondition.put("cardType", ToolUtil.toStr(request.getParameter("cardType"), null));
		mapCondition.put("membershipName", ToolUtil.toStr(request.getParameter("mshipname"), null));
		mapCondition.put("gender", ToolUtil.toStr(request.getParameter("gender"), null));
		mapCondition.put("isCardOpen", ToolUtil.toStr(request.getParameter("isCardOpen"), null));
		mapCondition.put("hdSearchKey", ToolUtil.toStr(request.getParameter("hdSearchKey"), null));
		mapCondition.put("coachName", ToolUtil.toStr(request.getParameter("coachName"), null));
		mapCondition.put("startDate", ToolUtil.toStr(request.getParameter("startDate"), null));
		mapCondition.put("endDate", ToolUtil.toStr(request.getParameter("endDate"), null));

		mapCondition.put("commonKey", ToolUtil.toStr(request.getParameter("commonKey"), null));

		// ??????????????????
		String hdSearchKey = Convert.toStr(request.getParameter("hdSearchKey"), "");

		Page<VipUser> page = new PageFactory<VipUser>().defaultPage("id", "desc");
		page = vipUserService.pageList(page, clubId, mapCondition, hdSearchKey);

		Map<String, Object> ret = super.packForPannelTable(page);
		ret.put("isShowPoint", 1);
		ret.put("execptDataType", 0);

		HashMap<String, Object> mapTime = new HashMap<>();
		mapTime.put("startTime", 1528769716670L);
		mapTime.put("endTime", 1528769716672L);
		mapTime.put("endTime2", 1528769717366L);
		ret.put("time", mapTime);

		return new ReturnTip(0, "??????", ret);
	}

	/**
	 * ??????VIP????????????
	 */
	@RequestMapping(value = "/maintainPagelist")
	@ResponseBody
	public Object maintainPagelist() {
		HttpServletRequest request = this.getHttpServletRequest();
		ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
		Integer clubId = clubAdmin.getClubId();
		if (ToolUtil.isEmpty(clubId)) {
			return ResponseEntity.ok(new ReturnTip(500, "?????????????????????"));
		}

		// ??????????????????
		HashMap<String, Object> mapCondition = new HashMap<>();
		mapCondition.put("realname", ToolUtil.toStr(request.getParameter("realname"), null));
		mapCondition.put("phone", ToolUtil.toStr(request.getParameter("phone"), null));
		mapCondition.put("nickname", ToolUtil.toStr(request.getParameter("nickname"), null));
		mapCondition.put("cardNumber", ToolUtil.toStr(request.getParameter("cardNumber"), null));
		mapCondition.put("cardType", ToolUtil.toStr(request.getParameter("cardType"), null));
		mapCondition.put("membershipName", ToolUtil.toStr(request.getParameter("mshipname"), null));
		mapCondition.put("gender", ToolUtil.toStr(request.getParameter("gender"), null));
		mapCondition.put("isCardOpen", ToolUtil.toStr(request.getParameter("isCardOpen"), null));
		mapCondition.put("hdSearchKey", ToolUtil.toStr(request.getParameter("hdSearchKey"), null));
		mapCondition.put("coachName", ToolUtil.toStr(request.getParameter("coachName"), null));
		mapCondition.put("startDate", ToolUtil.toStr(request.getParameter("startDate"), null));
		mapCondition.put("endDate", ToolUtil.toStr(request.getParameter("endDate"), null));

		// ??????????????????
		String hdSearchKey = Convert.toStr(request.getParameter("hdSearchKey"), "");

		Page<VipUser> page = new PageFactory<VipUser>().defaultPage("id", "desc");
		page = vipUserService.pageList(page, clubId, mapCondition, hdSearchKey);

		Map<String, Object> ret = super.packForPannelTable(page);
		ret.put("isShowPoint", 1);
		ret.put("execptDataType", 0);

		HashMap<String, Object> mapTime = new HashMap<>();
		mapTime.put("startTime", 1528769716670L);
		mapTime.put("endTime", 1528769716672L);
		mapTime.put("endTime2", 1528769717366L);
		ret.put("time", mapTime);

		return new ReturnTip(0, "??????", ret);
	}

	/**
	 * ??????+?????? ??????
	 */
	@RequestMapping(value = "/save")
	@ResponseBody
	public Object save() throws Exception {
		/**
		 * ??????token?????????????????????
		 */
		HttpServletRequest request = this.getHttpServletRequest();
		ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
		Integer clubId = clubAdmin.getClubId();
		if (ToolUtil.isEmpty(clubId)) {
			return new ReturnTip(500, "?????????????????????");
		}

		/**
		 * ??????????????????
		 */
		Map<String, String[]> mapParams = request.getParameterMap();
		Integer _id = Convert.toInt(mapParams.get("id")[0], 0);

		Map<String, Object> mapEntity = new HashMap<>();
		mapEntity.put("clubId", clubId);
		mapEntity.put("id", _id);
		mapEntity.put("realname", Convert.toStr(mapParams.get("realname")[0]));
		mapEntity.put("avatar", Convert.toStr(mapParams.get("avatar")[0]));
		mapEntity.put("gender", Convert.toInt(mapParams.get("gender")[0]));
		mapEntity.put("phone", Convert.toStr(mapParams.get("phone")[0]));
		mapEntity.put("idCardNumber", Convert.toStr(mapParams.get("idCardNumber")[0]));
		mapEntity.put("address", Convert.toStr(mapParams.get("address")[0]));
		mapEntity.put("cardType", Convert.toStr(mapParams.get("cardType")[0]));
		mapEntity.put("cardId", Convert.toInt(mapParams.get("cardId")[0]));
		mapEntity.put("cardNumber", Convert.toStr(mapParams.get("cardNumber")[0]));
		mapEntity.put("membershipId", Convert.toInt(mapParams.get("membershipId")[0]));
		mapEntity.put("userId", Convert.toInt(mapParams.get("userId")[0]));
		mapEntity.put("remark", Convert.toStr(mapParams.get("remark")[0]));
		mapEntity.put("introducePersonId", Convert.toInt(mapParams.get("introducePersonId")[0]));
		mapEntity.put("sourceId", Convert.toInt(mapParams.get("source")[0]));
		mapEntity.put("fieldData", JSON.toJSONString(mapParams.get("fieldData")));
		if (ToolUtil.isNotEmpty(mapParams.get("birthday")[0])) {
			mapEntity.put("birthday", DateUtil.date2TimeStamp(mapParams.get("birthday")[0], "yyyy-MM-dd"));
		}
		if (ToolUtil.isNotEmpty(mapParams.get("coachIds[]"))) {
			mapEntity.put("coachId", Convert.toInt(mapParams.get("coachIds[]")[0]));
		}

		VipUser vipUser = (VipUser) ToolUtil.convertMap(VipUser.class, mapEntity);
		/**
		 * ??????????????????
		 */
		if (mapEntity.get("userId").equals(0)) {
			// ??????????????????
			vipUser.setNickname("");
		} else {
			// ??????????????????
			String nickname = ConstantFactory.me().getUserCommonNicknameById((int) mapEntity.get("userId"));
			vipUser.setNickname(nickname);
		}

		/**
		 * ??????????????????
		 */
		if (ToolUtil.isNotEmpty(mapEntity.get("coachId")) && !mapEntity.get("coachId").equals(0)) {
			String name = ConstantFactory.me().getCoachNameById((int) mapEntity.get("coachId"));
			vipUser.setCoachName(name);
		} else {
			// ??????????????????
			vipUser.setCoachName("");
		}

		/**
		 * ??????????????????
		 */
		if (mapEntity.get("membershipId").equals(0)) {
			// ??????????????????
			vipUser.setMembershipName("");
		} else {
			String name = ConstantFactory.me().getStaffSpecialNameById((int) mapEntity.get("membershipId"));
			vipUser.setMembershipName(name);
		}

		/**
		 * ?????????????????????
		 */
		if (mapEntity.get("introducePersonId").equals(0)) {
			// ??????????????????
			vipUser.setIntroducePersonName("");
		} else {
			// ??????????????????
			String name = ConstantFactory.me().getVipRealnameById((int) mapEntity.get("introducePersonId"));
			vipUser.setIntroducePersonName(name);
		}

		Wrapper<VipUser> ew = new EntityWrapper<>();
		ew = ew.eq("club_id", clubId);
		ew = ew.eq("id", _id);

		// ????????????????????????
		try {
			if (0 == vipUserService.selectCount(ew)) {
				vipUser.setId(null);
				vipUser.setInsertTime(DateUtil.timeStamp());
				vipUserService.insert(vipUser);
			} else {
				vipUser.setId(_id);
				vipUserService.update(vipUser, ew);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new ReturnTip(0, "??????", MapItemFactory.composeMap("id", vipUser.getId()));
	}

	/**
	 * ??????
	 */
	@RequestMapping(value = "/delete")
	@ResponseBody
	public Object delete() {
		HttpServletRequest request = this.getHttpServletRequest();
		ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
		Integer clubId = clubAdmin.getClubId();
		if (ToolUtil.isEmpty(clubId)) {
			return ResponseEntity.ok(new ReturnTip(500, "?????????????????????"));
		}

		String password = Convert.toStr(request.getParameter("password"));
    	String salt = clubAdmin.getSalt();
    	
    	//???????????????????????????
    	if (!MD5Util.encBySalt(password, salt).equals(clubAdmin.getPassword())) {
    		return new ReturnTip(501, "??????????????????");
		}
    	
		Integer id = ToolUtil.toInt(request.getParameter("id"));
		Wrapper<VipUser> ew = new EntityWrapper<>();
		ew = ew.eq("id", id);
		ew = ew.eq("club_id", clubId);
		try {
			vipUserService.delete(ew);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return ResponseEntity.ok(new ReturnTip(501, "????????????"));
		}

		return new ReturnTip(0, "????????????");
	}

	/**
	 * ??????vipid????????????id??????vip????????????
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/detail")
	@ResponseBody
	public Object detail() {
		HttpServletRequest request = this.getHttpServletRequest();
		// ????????????
		Integer id = ToolUtil.toInt(request.getParameter("id"));
		Integer clubId = ToolUtil.toInt(request.getParameter("clubId"));
		VipUser itemInDb = vipUserService.selectById(id);

		// ???????????????????????????
		if (!itemInDb.getClubId().equals(clubId)) {
			return ResponseEntity.ok(new ReturnTip(501, "????????????"));
		}

		Map<String, Object> mapRet = null;
		try {
			mapRet = ToolUtil.convertBean(itemInDb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		new VipUserWarpper(null).warpTheMap(mapRet);

		return new ReturnTip(0, "??????", mapRet);
	}

	/**
	 * ?????????????????????
	 */
	@RequestMapping(value = "/bindUser")
	@ResponseBody
	public Object bindUser() throws Exception {
		HttpServletRequest request = this.getHttpServletRequest();
		ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
		Integer clubId = clubAdmin.getClubId();
		if (ToolUtil.isEmpty(clubId)) {
			return new ReturnTip(500, "?????????????????????");
		}

		Integer vipId = ToolUtil.toInt(request.getParameter("vipId"));
		Integer userId = ToolUtil.toInt(request.getParameter("userId"));
		String name = ToolUtil.toStr(request.getParameter("name"));
		String type = ToolUtil.toStr(request.getParameter("type"));

		boolean bindingRes = vipUserService.bindMember(clubId, vipId, userId, name, type);
		if (bindingRes) {
			return new ReturnTip(0, "????????????");
		} else {
			return new ReturnTip(501, "????????????");
		}
	}

	/**
	 * ????????????
	 */
	@RequestMapping(value = "/setAvatar")
	@ResponseBody
	public Object setAvatar(@RequestBody HashMap<String, Object> mapParams) throws Exception {
		HttpServletRequest request = this.getHttpServletRequest();
		ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
		Integer clubId = clubAdmin.getClubId();
		if (ToolUtil.isEmpty(clubId)) {
			return new ReturnTip(500, "?????????????????????");
		}

		Integer id = Convert.toInt(mapParams.get("id"));
		String url = Convert.toStr(mapParams.get("url"));

		try {
			VipUser entity = new VipUser();
			entity.setavatar(url);

			// ??????????????????????????????????????????????????????
			Wrapper<VipUser> ew = new EntityWrapper<>();
			ew = ew.eq("id", id);
			ew = ew.eq("club_id", clubId);
			vipUserService.update(entity, ew);
			return new ReturnTip(0, "????????????");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ReturnTip(501, "????????????");
		}
	}



}
