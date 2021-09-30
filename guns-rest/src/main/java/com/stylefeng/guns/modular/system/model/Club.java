package com.stylefeng.guns.modular.system.model;

import java.io.Serializable;

import com.baomidou.mybatisplus.enums.IdType;
import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * <p>
 * 俱乐部主体信息
 * </p>
 *
 * @author guiyj007
 * @since 2018-06-22
 */
@TableName("biz_club")
public class Club extends Model<Club> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
     * 俱乐部名称
     */
    private String name;
    /**
     * 短信后缀
     */
    private String abbreviate;
    /**
     * logo
     */
    private String logo;
    
    /**
     * 所属俱乐部ID
     */
    @TableField("club_sort")
    private Integer clubSort;
   
	private String desc;
    
	/**
     * 会员通过APP加入俱乐部使用的认证码
     */
    @TableField("auth_code")
    private String authCode;
    /**
     * 背景图
     */
    private String background;
    /**
     * 俱乐部二维码
     */
    @TableField("qrcode_img")
    private String qrcodeImg;
    /**
     * 微官网
     */
    @TableField("micro_website")
    private String microWebsite;
    /**
     * 经度
     */
    private String lng;
    /**
     * 纬度
     */
    private String lat;
    /**
     * 省份ID
     */
    @TableField("club_province")
    private String clubProvince;
    /**
     * 城市id
     */
    @TableField("club_city")
    private String clubCity;
    /**
     * 地区id
     */
    @TableField("club_area")
    private Integer clubArea;
    /**
     * 详细地区
     */
    @TableField("club_address")
    private String clubAddress;
    
    /**
     * 电话
     */
    @TableField("club_phone")
    private String clubPhone;
    
	/**
     * 俱乐部类型: 1工作室 2俱乐部
     */
    @TableField("club_style")
    private Integer clubStyle;
    /**
     * 所属俱乐部ID
     */
    @TableField("parent_id")
    private Integer parentId;
    /**
     * 是否试用
     */
    @TableField("trial_club")
    private Integer trialClub;
    /**
     * 备注人
     */
    @TableField("remark_by")
    private String remarkBy;
    /**
     * 用户数
     */
    @TableField("user_count")
    private Integer userCount;
    /**
     * 生效开始时间
     */
    @TableField("start_time")
    private Integer startTime;
    /**
     * 过期时间
     */
    @TableField("expire_time")
    private Integer expireTime;
    /**
     * 过期保留时间（天）
     */
    @TableField("not_enough_cancel_time")
    private Integer notEnoughCancelTime;
    /**
     * 推广短链接
     */
    private String shorturl;
    /**
     * 剩余金额
     */
    @TableField("cash_amount")
    private BigDecimal cashAmount;
    /**
     * 会员最近申请时间
     */
    @TableField("apply_time")
    private Integer applyTime;
    /**
     * 总申请人数
     */
    @TableField("apply_num")
    private Integer applyNum;
    /**
     * 是否开启会员间聊天
     */
    private Integer chat;
    /**
     * 是否开启积分系统
     */
    @TableField("points_status")
    private Integer pointsStatus;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getClubSort() {
		return clubSort;
	}

	public void setClubSort(Integer clubSort) {
		this.clubSort = clubSort;
	}
	
    public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getAuthCode() {
		return authCode;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

    public String getAbbreviate() {
        return abbreviate;
    }

    public void setAbbreviate(String abbreviate) {
        this.abbreviate = abbreviate;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getQrcodeImg() {
        return qrcodeImg;
    }

    public void setQrcodeImg(String qrcodeImg) {
        this.qrcodeImg = qrcodeImg;
    }

    public String getMicroWebsite() {
        return microWebsite;
    }

    public void setMicroWebsite(String microWebsite) {
        this.microWebsite = microWebsite;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getClubProvince() {
        return clubProvince;
    }

    public void setClubProvince(String clubProvince) {
        this.clubProvince = clubProvince;
    }

    public String getClubCity() {
        return clubCity;
    }

    public void setClubCity(String clubCity) {
        this.clubCity = clubCity;
    }

    public Integer getClubArea() {
        return clubArea;
    }

    public void setClubArea(Integer clubArea) {
        this.clubArea = clubArea;
    }

    public String getClubAddress() {
        return clubAddress;
    }

    public void setClubAddress(String clubAddress) {
        this.clubAddress = clubAddress;
    }
    public String getClubPhone() {
		return clubPhone;
	}
	public void setClubPhone(String clubPhone) {
		this.clubPhone = clubPhone;
	}
    public Integer getClubStyle() {
        return clubStyle;
    }

    public void setClubStyle(Integer clubStyle) {
        this.clubStyle = clubStyle;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Integer getTrialClub() {
        return trialClub;
    }

    public void setTrialClub(Integer trialClub) {
        this.trialClub = trialClub;
    }

    public String getRemarkBy() {
        return remarkBy;
    }

    public void setRemarkBy(String remarkBy) {
        this.remarkBy = remarkBy;
    }

    public Integer getUserCount() {
        return userCount;
    }

    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    }

    public Integer getStartTime() {
        return startTime;
    }

    public void setStartTime(Integer startTime) {
        this.startTime = startTime;
    }

    public Integer getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Integer expireTime) {
        this.expireTime = expireTime;
    }

    public Integer getNotEnoughCancelTime() {
        return notEnoughCancelTime;
    }

    public void setNotEnoughCancelTime(Integer notEnoughCancelTime) {
        this.notEnoughCancelTime = notEnoughCancelTime;
    }

    public String getShorturl() {
        return shorturl;
    }

    public void setShorturl(String shorturl) {
        this.shorturl = shorturl;
    }

    public BigDecimal getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(BigDecimal cashAmount) {
        this.cashAmount = cashAmount;
    }

    public Integer getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Integer applyTime) {
        this.applyTime = applyTime;
    }

    public Integer getApplyNum() {
        return applyNum;
    }

    public void setApplyNum(Integer applyNum) {
        this.applyNum = applyNum;
    }

    public Integer getChat() {
        return chat;
    }

    public void setChat(Integer chat) {
        this.chat = chat;
    }

    public Integer getPointsStatus() {
        return pointsStatus;
    }

    public void setPointsStatus(Integer pointsStatus) {
        this.pointsStatus = pointsStatus;
    }

    @Override
    protected Serializable pkVal() {
        return this.id;
    }

    @Override
    public String toString() {
        return "Club{" +
        "id=" + id +
        ", name=" + name +
        ", clubSort=" + clubSort +
        ", desc=" + desc +
        ", authCode=" + authCode +
        ", abbreviate=" + abbreviate +
        ", logo=" + logo +
        ", background=" + background +
        ", qrcodeImg=" + qrcodeImg +
        ", microWebsite=" + microWebsite +
        ", lng=" + lng +
        ", lat=" + lat +
        ", clubProvince=" + clubProvince +
        ", clubCity=" + clubCity +
        ", clubArea=" + clubArea +
        ", clubAddress=" + clubAddress +
        ", clubPhone=" + clubPhone +
        ", clubStyle=" + clubStyle +
        ", parentId=" + parentId +
        ", trialClub=" + trialClub +
        ", remarkBy=" + remarkBy +
        ", userCount=" + userCount +
        ", startTime=" + startTime +
        ", expireTime=" + expireTime +
        ", notEnoughCancelTime=" + notEnoughCancelTime +
        ", shorturl=" + shorturl +
        ", cashAmount=" + cashAmount +
        ", applyTime=" + applyTime +
        ", applyNum=" + applyNum +
        ", chat=" + chat +
        ", pointsStatus=" + pointsStatus +
        "}";
    }
}
