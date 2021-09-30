package com.stylefeng.guns.modular.system.warpper;

import com.stylefeng.guns.core.common.constant.factory.ConstantFactory;
import com.stylefeng.guns.core.util.Convert;
import com.stylefeng.guns.core.util.DateUtil;
import com.stylefeng.guns.core.base.warpper.BaseControllerWarpper;

import java.util.List;
import java.util.Map;

/**
 * 包装类
 *
 * @author guiyj007
 * @date 2018年6月26日 下午3:47:03
 */
public class CardStoredvaluecardWarpper extends BaseControllerWarpper {

    public CardStoredvaluecardWarpper(List<Map<String, Object>> list) {
        super(list);
    }

    @Override
    public void warpTheMap(Map<String, Object> map) {
        map.put("clubName", ConstantFactory.me().getClubNameById((Integer) map.get("clubId")));
        
        map.put("sourceName", ConstantFactory.me().getDictsByName("sales-source", (Integer) map.get("sourceId")));
        map.put("type", "storeCard");
        map.put("typeName", map.get("cardTypeName"));
        map.put("isUnitedCard", Convert.toBool(map.get("isUnitedCard")) ? "通卡" : "单店卡" );
        map.put("openTime", DateUtil.timeStamp2Date((Integer) map.get("openCardTime"), "yyyy-MM-dd"));
        map.put("insertTimeStr", DateUtil.timeStamp2Date((Integer) map.get("insertTime"), null));
        map.put("startUseTimeStr", DateUtil.timeStamp2Date((Integer) map.get("startUseTime"), null));
        map.put("expires", map.get("openCardTime"));
        map.put("cardId", map.get("id"));
        
        map.put("counts", map.get("sumMoney"));
        map.put("leftCounts", map.get("leftMoney"));
        map.put("usedCounts", Convert.toInt(map.get("counts")) - Convert.toInt(map.get("leftCounts")));
        map.put("usedMoney", map.get("usedCounts"));
    
        map.put("unionCardId", map.get("clubId") + "-172-1");
    }

}