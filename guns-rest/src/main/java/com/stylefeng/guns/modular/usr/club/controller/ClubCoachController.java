package com.stylefeng.guns.modular.usr.club.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import com.stylefeng.guns.core.common.constant.factory.PageFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.stylefeng.guns.core.log.LogObjectHolder;
import com.stylefeng.guns.core.util.Convert;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.core.util.ToolUtil;

import org.springframework.web.bind.annotation.RequestParam;

import com.stylefeng.guns.modular.system.model.ClubAdmin;
import com.stylefeng.guns.modular.system.model.ClubCoach;
import com.stylefeng.guns.modular.system.model.PtrainBespeakRecord;
import com.stylefeng.guns.modular.system.model.UserClub;
import com.stylefeng.guns.modular.system.model.VipUser;
import com.stylefeng.guns.modular.system.warpper.ClubAdminWarpper;
import com.stylefeng.guns.modular.system.warpper.ClubCoachWarpper;
import com.stylefeng.guns.rest.common.ReturnTip;
import com.stylefeng.guns.rest.common.exception.BizException;
import com.stylefeng.guns.rest.common.exception.BizExistException;
import com.stylefeng.guns.modular.mch.club.service.IClubCoachService;
import com.stylefeng.guns.modular.mch.member.service.IVipUserService;
import com.stylefeng.guns.modular.mch.syllabus.service.IPtrainBespeakRecordService;

/**
 * ???????????????
 *
 * @author guiyj007
 * @Date 2018-06-22 16:06:04
 */
@Controller("usr_ClubCoachController")
@RequestMapping("/usr/club/clubCoach")

public class ClubCoachController extends BaseController {

    @Autowired
    private IClubCoachService clubCoachService;
    @Autowired
    private IPtrainBespeakRecordService ptrainBespeakRecordService;
    @Autowired
    private IVipUserService vipUserService;

    
    /**
     * ?????? ?????? ???????????????????????????
     * @throws Exception 
     */
	@GetMapping(value = "/coacheTimeTable")
    @ResponseBody
    public Object coacheTimeTable() throws Exception {
		HttpServletRequest request = this.getHttpServletRequest();
    	Integer coachId = Convert.toInt(request.getParameter("coachId"), 0);
    	String date = Convert.toStr(request.getParameter("date"), "");
    	if (coachId.equals(0) || date.equals("")) {
    		return new ReturnTip(500, "????????????");
    	}
    	//??????????????????????????????
    	
    	Wrapper<PtrainBespeakRecord> ew = new EntityWrapper<>();
    	ew.eq("coach_id", coachId);
    	ew.eq("date", date);
    	ew.in("status", "0,1,2");	//?????????????????????0?????? 1????????? 2????????? 3??????????????? 4 ???????????????
    	List<PtrainBespeakRecord> listRecord = ptrainBespeakRecordService.selectList(ew);
    	
    	//??????????????????????????????
    	Integer currentTime = DateUtil.timeStamp();
    	
    	//@debug
//    	currentTime -= 3600*6;
    	
//    	Integer currentHourTime = DateUtil.date2TimeStamp(DateUtil.timeStamp2Date(currentTime, "yyyy-MM-dd HH") + ":00:00", null);
    	
    	Integer startTime = DateUtil.date2TimeStamp(date + " 09:00:00", null);;
    	Integer endTime = DateUtil.date2TimeStamp(date + " 22:00:00", null);;
//    	if (currentTime - currentHourTime < 1800) {
//    		startTime = currentHourTime + 1800;
//    	} else {
//    		startTime = currentHourTime + 3600;
//    	}
    	
    	//???????????????????????????  ????????????
    	List<HashMap<String, Object>> listTimeTable = new ArrayList<HashMap<String, Object>>();
    	
    	for (int i = 0; i < 40; i++) {
    		//??????????????????????????????
    		if (startTime >= endTime) {
    			break;
    		}
    		
    		HashMap<String, Object> timeTableItem = new HashMap<>();
    		Integer _startTime = new Integer(startTime);
    		Integer _endTime = new Integer(startTime+1800);
    		
    		timeTableItem.put("startTime", _startTime);
    		timeTableItem.put("endTime", _endTime);
    		timeTableItem.put("startTimeStr", DateUtil.timeStamp2Date(_startTime, "HH:mm"));
    		timeTableItem.put("endTimeStr", DateUtil.timeStamp2Date(_endTime, "HH:mm"));
    		timeTableItem.put("desc", "?????????");
    		timeTableItem.put("bespeakId", 0);
    		if (timeTableItem.get("startTimeStr").toString().substring(2).equals(":00")) {
    			timeTableItem.put("hourTime", 1);
    		} else {
    			timeTableItem.put("hourTime", 0);
    		}
    		
    		// @debug
    		boolean debug = false;
    		if (debug) {
    			timeTableItem.put("isItemsStarter", -1);
        		if (i == 2 || i == 3 || i==4) {
        			timeTableItem.put("bespeakId", 8);
        			timeTableItem.put("isItemsStarter", i == 2 ? 1 : 0);
    				//???????????????????????????????????????
    				timeTableItem.put("lengthOfItems", 3);
    				timeTableItem.put("bespeakInfo", listRecord.get(0));
        		}
    		}
    		
    		
    		//??????????????????????????????????????????????????????
    		for (PtrainBespeakRecord ptrainBespeakRecord : listRecord) {
    			if (ptrainBespeakRecord.getFromTime() <= _startTime && ptrainBespeakRecord.getToTime() >= _endTime) {
    				timeTableItem.put("startTimeStr", DateUtil.timeStamp2Date(_startTime, "HH:mm"));
    	    		timeTableItem.put("endTimeStr", DateUtil.timeStamp2Date(_endTime, "HH:mm"));
    	    		HashMap<String, Object> map = new HashMap<>();
    	    		map.putAll(ToolUtil.convertBean(ptrainBespeakRecord));
    	    		map.put("fromTimeStr", DateUtil.timeStamp2Date(ptrainBespeakRecord.getFromTime(), "HH:mm"));
    	    		map.put("toTimeStr", DateUtil.timeStamp2Date(ptrainBespeakRecord.getToTime(), "HH:mm"));
    	    		
    	    		//?????????????????????
    				if (ptrainBespeakRecord.getToTime() < currentTime) {
    					map.put("status", 5);
    				}
    	    		
    	    		timeTableItem.put("bespeakInfo", map);
    				
    				timeTableItem.put("bespeakId", ptrainBespeakRecord.getId());
    				timeTableItem.put("desc", "????????????");
    				
    				//??????????????? ???????????????????????????????????????????????????????????????????????????????????????
    				timeTableItem.put("isItemsStarter", _startTime.equals(ptrainBespeakRecord.getFromTime()) ? 1 : 0);
    				//???????????????????????????????????????
    				timeTableItem.put("lengthOfItems", (ptrainBespeakRecord.getToTime() - ptrainBespeakRecord.getFromTime())/1800);
    				
    				break;
    			}
    		}
    		
    		listTimeTable.add(timeTableItem);
    		startTime += 1800;
		}
    	
    	//???????????????????????????
    	for (int j = 0; j < listTimeTable.size(); j++) {
    		//???????????????????????????????????????
    		if (j+1 == listTimeTable.size()) {
    			listTimeTable.get(j).put("canBespeak", 0);
    			break;
    		}
    		
			if (Convert.toInt(listTimeTable.get(j).get("bespeakId")) >0 
					|| Convert.toInt(listTimeTable.get(j+1).get("bespeakId")) >0
					|| Convert.toInt(listTimeTable.get(j).get("startTime")) < currentTime
			) {
				listTimeTable.get(j).put("canBespeak", 0);
			} else {
				listTimeTable.get(j).put("canBespeak", 1);
			}
			
		}
    	
    	
    	HashMap<String, Object> mapRet = new HashMap<>();
    	mapRet.put("timeTable", listTimeTable);
    	
    	return new ReturnTip(0, "??????",  mapRet);
    }
    
	/**
     * ???????????????
     */
    @RequestMapping(value = "/joinCoach")
    @ResponseBody
    public Object joinCoach() throws Exception {
    	HttpServletRequest request = this.getHttpServletRequest();
    	HashMap<String, Object> mapMember = (HashMap<String, Object>) request.getAttribute("mapMember");
    	Integer userId = Convert.toInt(mapMember.get("id"), 0);
    	if (userId.equals(0)) {
    		return new ReturnTip(500, "??????????????????");
    	}
    	
    	//??????post??????
    	HashMap<String, String> mapParams = JSON.parseObject(getStringFromStream(), HashMap.class);
    	Integer clubId = Convert.toInt(mapParams.get("clubId"), 0);
    	String realname = Convert.toStr(mapParams.get("realname"), "");
    	String goodAt = Convert.toStr(mapParams.get("goodAt"), "");
    	if (clubId.equals(0) || realname.equals("")) {
    		return new ReturnTip(500, "??????????????????");
    	}
    	
    	//????????????????????????
    	ClubCoach clubCoach = null;
    	try {
    		clubCoach = clubCoachService.addFromCommonUser(mapMember, clubId, realname, goodAt);
    		return new ReturnTip(0, "??????", clubCoach);
		} catch (BizExistException e) {
			// TODO Auto-generated catch block
			return new ReturnTip(200, "??????");
		} catch (BizException e) {
			// TODO Auto-generated catch block
			return new ReturnTip(501, e.getMessage());
		}
    }
    /**
     * ??????+??????
     */
    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save() throws Exception {
    	HttpServletRequest request = this.getHttpServletRequest();
    	ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
    	Integer clubId = clubAdmin.getClubId();
    	if (ToolUtil.isEmpty(clubId)) {
    		return new ReturnTip(500, "?????????????????????");
    	}
    	
    	Map<String, String[]> mapParams = request.getParameterMap();
    	Integer _id = Convert.toInt(mapParams.get("id")[0], 0);
    	//????????????
    	Map<String, Object> mapEntity = new HashMap<>();
    	mapEntity.put("clubId", clubId);
    	mapEntity.put("id", _id);
    	mapEntity.put("realname", Convert.toStr(mapParams.get("realname")[0]));
    	mapEntity.put("avatar", Convert.toStr(mapParams.get("avatar")[0]));
    	mapEntity.put("goodAt", Convert.toStr(mapParams.get("goodAt")[0]));
    	mapEntity.put("desc", Convert.toStr(mapParams.get("desc")[0]));
    	mapEntity.put("userId", Convert.toInt(mapParams.get("userId")[0]));
    	mapEntity.put("auth", JSON.toJSONString(mapParams.get("auth[]")));
    	
    	ClubCoach clubCoach = (ClubCoach) ToolUtil.convertMap(ClubCoach.class, mapEntity);
    	//??????????????????
    	if (mapEntity.get("userId").equals(0)) {
    		//??????????????????
    		clubCoach.setNickname("");
    	} else {
    		//??????????????????
        	String nickname = ConstantFactory.me().getUserCommonNicknameById((int) mapEntity.get("userId"));
        	clubCoach.setNickname(nickname);
    	}
    	
    	//????????????
    	Wrapper<ClubCoach> ew = new EntityWrapper<>();
    	ew = ew.eq("club_id", clubId);
    	ew = ew.eq("id", _id);
    	
    	//????????????????????????
    	try {
			if (0 == clubCoachService.selectCount(ew)) {
				clubCoach.setId(null);
				clubCoach.setInsertTime(DateUtil.timeStamp());
				clubCoachService.insert(clubCoach);
			} else {
				clubCoach.setId(_id);
				clubCoachService.update(clubCoach, ew);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new ReturnTip(0, "??????");
    }

    
    /**
     * ??????
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        HttpServletRequest request = this.getHttpServletRequest();
        Integer id = Convert.toInt(request.getParameter("id"));
        Integer clubId = Convert.toInt(request.getParameter("clubId"));
     	if (ToolUtil.isEmpty(id) || ToolUtil.isEmpty(clubId)) {
     		return new ReturnTip(500, "????????????");
     	}
    	
    	//????????????
    	ClubCoach itemInDb = clubCoachService.selectById(id);
    	
    	//?????????????????????
    	if (itemInDb == null || !itemInDb.getClubId().equals(clubId)) {
    		return ResponseEntity.ok(new ReturnTip(501, "????????????"));
    	}
    	
    	Map<String, Object> mapRet = null;
		try {
			mapRet = ToolUtil.convertBean(itemInDb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	new ClubCoachWarpper(null).warpTheMap(mapRet);
    	
    	//???????????????????????????
    	mapRet.remove("auth");
    	
    	return new ReturnTip(0, "??????",  mapRet);
    }
    
    /**
	 * ??????????????????VIP????????????
	 */
	@RequestMapping(value = "/listStudents")
	@ResponseBody
	public Object listStudents() {
		HttpServletRequest request = this.getHttpServletRequest();
    	Integer coachId = Convert.toInt(request.getParameter("coachId"), 0);
    	Integer clubId = Convert.toInt(request.getParameter("clubId"), 0);
    	if (coachId.equals(0)) {
    		return new ReturnTip(500, "????????????");
    	}

		// ??????????????????
		HashMap<String, Object> mapCondition = new HashMap<>();
		mapCondition.put("coachId", coachId);
		mapCondition.put("exact", 1);
		mapCondition.put("phone", ToolUtil.toStr(request.getParameter("phone"), null));
		mapCondition.put("gender", ToolUtil.toStr(request.getParameter("gender"), null));

		Page<VipUser> page = new PageFactory<VipUser>().defaultPage("id", "desc");
		page = vipUserService.pageList(page, clubId, mapCondition, "");

		Map<String, Object> ret = super.packForPannelTable(page);

		return new ReturnTip(0, "??????", ret);
	}
    
}
