package com.stylefeng.guns.modular.usr.member.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.core.base.controller.BaseController;
import com.stylefeng.guns.core.common.constant.factory.PageFactory;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.stylefeng.guns.core.util.Convert;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.core.util.ToolUtil;
import com.stylefeng.guns.modular.system.model.CardOncecard;
import com.stylefeng.guns.modular.system.model.CheckinRecord;
import com.stylefeng.guns.modular.system.model.ClubAdmin;
import com.stylefeng.guns.modular.system.model.VipUser;
import com.stylefeng.guns.modular.system.warpper.CheckinRecordWarpper;
import com.stylefeng.guns.rest.common.ReturnTip;
import com.stylefeng.guns.modular.mch.member.service.ICheckinRecordService;
import com.stylefeng.guns.modular.mch.member.service.IVipUserService;

/**
 * 签到记录控制器
 *
 * @author guiyj007
 * @Date 2018-06-22 16:45:03
 */
@Controller("usr_checkinRecordController")
@RequestMapping("/usr/member/checkinRecord")
public class CheckinRecordController extends BaseController {

    @Autowired
    private ICheckinRecordService checkinRecordService;
    @Autowired
    private IVipUserService vipUserService;
    
    /**
     * 获取签到记录列表
     */
    @RequestMapping(value = "/pagelist")
    @ResponseBody
    public Object pagelist() {
    	HttpServletRequest request = this.getHttpServletRequest();
		HashMap<String, String> mapMember = (HashMap<String, String>) request.getAttribute("mapMember");
		Integer userId = Convert.toInt(mapMember.get("id"), 0);
    	if (userId.equals(0)) {
    		return new ReturnTip(500, "会员信息失效");
    	}
    	
    	Integer clubId = Convert.toInt(request.getParameter("clubId"), 0);
    	Integer vipId = Convert.toInt(request.getParameter("vipId"), 0);
		if (clubId.equals(0) || vipId.equals(0)) {
			return ResponseEntity.ok(new ReturnTip(500, "参数无效"));
		}
    	
     	//综合查询条件
     	HashMap<String, Object> mapCondition = new HashMap<>();
     	mapCondition.put("vipId", vipId);
     	mapCondition.put("timeRangeStart", Convert.toStr(request.getParameter("timeRangeStart"), null));
     	mapCondition.put("timeRangeEnd", Convert.toStr(request.getParameter("timeRangeEnd"), null));
     	mapCondition.put("realname", Convert.toStr(request.getParameter("realname"), null));
     	mapCondition.put("phone", Convert.toStr(request.getParameter("phone"), null));
     	mapCondition.put("membershipName", Convert.toStr(request.getParameter("membershipName"), null));
     	mapCondition.put("opeUsername", Convert.toStr(request.getParameter("agentCostName"), null));
     	mapCondition.put("ringNum", Convert.toStr(request.getParameter("ringNum"), null));
     	
    	Page<CheckinRecord> page = new PageFactory<CheckinRecord>().defaultPage("id", "desc");
    	page = checkinRecordService.pageList(page, clubId, mapCondition);
        
        return new ReturnTip(0, "成功", super.packForPannelTable(page));
    }
    
    @RequestMapping(value = "/getVipUserRemark")
    @ResponseBody
    public Object getVipUserRemark() {
    	HttpServletRequest request = this.getHttpServletRequest();
    	ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
     	Integer clubId = clubAdmin.getClubId();
     	if (ToolUtil.isEmpty(clubId)) {
     		return ResponseEntity.ok(new ReturnTip(500, "俱乐部信息失效"));
     	}
     	
     	Integer id = Convert.toInt(request.getParameter("admisstionId"));
     	
     	//综合查询条件
     	HashMap<String, Object> mapRet = new HashMap<>();
     	
     	mapRet.put("list", null);
        
        return new ReturnTip(0, "成功", mapRet);
    }
    
    /**
     * 获取今日签到签出的统计数据
     */
    @RequestMapping(value = "/statCheckIn")
    @ResponseBody
    public Object statCheckIn() {
    	HttpServletRequest request = this.getHttpServletRequest();
    	ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
     	Integer clubId = clubAdmin.getClubId();
     	if (ToolUtil.isEmpty(clubId)) {
     		return ResponseEntity.ok(new ReturnTip(500, "俱乐部信息失效"));
     	}
     	
     	//综合查询条件
     	HashMap<String, Object> mapRet = new HashMap<>();
     	
     	String strToday = DateUtil.getDay() + " 00:00:00";
     	Integer intToday = DateUtil.date2TimeStamp(strToday, null);
     	Integer intTomorow = intToday + 86400;
     	
     	mapRet.put("checkIn", 0);
     	mapRet.put("checkOut", 0);
     	mapRet.put("inField", 0);
     	mapRet.put("inUse", 0);
     	mapRet.put("time", strToday);
     	mapRet.put("time2", DateUtil.timeStamp2Date(intTomorow, null));
        
        return new ReturnTip(0, "成功", mapRet);
    }

    /**
     * 新增+更新
     */
    @RequestMapping(value = "/save")
    @ResponseBody
    public Object save() throws Exception {
    	HttpServletRequest request = this.getHttpServletRequest();
    	ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
    	Integer clubId = clubAdmin.getClubId();
    	if (ToolUtil.isEmpty(clubId)) {
    		return new ReturnTip(500, "俱乐部信息失效");
    	}
    	
    	Map<String, String[]> mapParams = request.getParameterMap();
    	Integer _id = Convert.toInt(mapParams.get("id")[0], 0);
    	//入库对象
    	Map<String, Object> mapEntity = new HashMap<>();
    	mapEntity.put("clubId", clubId);
    	mapEntity.put("id", _id);
    	mapEntity.put("type", Convert.toInt(mapParams.get("type")[0]));
    	mapEntity.put("realname", Convert.toStr(mapParams.get("realname")[0]));
    	mapEntity.put("nickname", Convert.toStr(mapParams.get("realname")[0]));
    	mapEntity.put("avatar", Convert.toStr(mapParams.get("avatar")[0]));
    	mapEntity.put("userId", Convert.toInt(mapParams.get("userId")[0]));
    	mapEntity.put("auth", JSON.toJSONString(mapParams.get("authArray[]")));
    	
    	CheckinRecord checkinRecord = (CheckinRecord) ToolUtil.convertMap(CheckinRecord.class, mapEntity);
     
    	Wrapper<CheckinRecord> ew = new EntityWrapper<>();
    	ew = ew.eq("club_id", clubId);
    	ew = ew.eq("id", _id);
    	
    	//验证信息是否存在
    	try {
			if (0 == checkinRecordService.selectCount(ew)) {
				checkinRecord.setId(null);
				checkinRecord.setInsertTime(DateUtil.timeStamp());
				checkinRecordService.insert(checkinRecord);
			} else {
				checkinRecord.setId(_id);
				checkinRecordService.update(checkinRecord, ew);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return new ReturnTip(0, "成功");
    }
    
    /**
     * 更新签到信息
     */
    @RequestMapping(value = "/updateRemark")
    @ResponseBody
    public Object updateRemark() throws Exception {
    	HttpServletRequest request = this.getHttpServletRequest();
    	ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
    	Integer clubId = clubAdmin.getClubId();
    	if (ToolUtil.isEmpty(clubId)) {
    		return new ReturnTip(500, "俱乐部信息失效");
    	}
    	
    	Integer id = Convert.toInt(request.getParameter("admisstionId"));
    	Integer vipId = Convert.toInt(request.getParameter("vipId"));
    	String remark = Convert.toStr(request.getParameter("remark"));
    	if (id.equals(0) || vipId.equals(0) || remark.equals("")) {
    		return new ReturnTip(501, "必要参数不能为空");
    	}
    	
    	CheckinRecord checkinRecord = new CheckinRecord();
    	checkinRecord.setId(id);
    	checkinRecord.setVipId(vipId);
    	checkinRecord.setRemark(remark);
      
    	//验证信息是否存在
    	try {
    		checkinRecordService.updateById(checkinRecord);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return new ReturnTip(502, "操作失败，请稍后再试！");
		}
    	return new ReturnTip(0, "成功");
    }
    
    /**
     * 俱乐部管理员详情
     */
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/detail")
    @ResponseBody
    public Object detail() {
        HttpServletRequest request = this.getHttpServletRequest();
    	ClubAdmin clubAdmin = (ClubAdmin) request.getAttribute("member");
    	Integer clubId = clubAdmin.getClubId();
    	if (ToolUtil.isEmpty(clubId)) {
    		return ResponseEntity.ok(new ReturnTip(500, "俱乐部信息失效"));
    	}
    	
    	CheckinRecord itemInDb = null;
    	try {
    		//获取内容
        	Integer id = ToolUtil.toInt(request.getParameter("id"));
        	itemInDb = checkinRecordService.selectById(id);
        	
        	//验证合同所属俱乐部
        	if (!itemInDb.getClubId().equals(clubId)) {
        		return ResponseEntity.ok(new ReturnTip(501, "合同访问受限"));
        	}
		} catch (NullPointerException e) {
			// TODO: handle exception
			return ResponseEntity.ok(new ReturnTip(501, "实体不存在"));
		}
    	
    	
    	Map<String, Object> mapRet = null;
		try {
			mapRet = ToolUtil.convertBean(itemInDb);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	new CheckinRecordWarpper(null).warpTheMap(mapRet);
    	
    	return new ReturnTip(0, "成功",  mapRet);
    }
    
    
}
