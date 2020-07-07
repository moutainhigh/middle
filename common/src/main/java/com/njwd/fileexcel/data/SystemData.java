package com.njwd.fileexcel.data;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.njwd.common.ExcelDataConstant.SHOP_STATUS;


/**
 * @description: 系统数据
 * @author: xdy
 * @create: 2019/6/10 16:54
 */
public class SystemData extends AbstractMappingData{

    private static Map<String,Map> cacheDataMap = new ConcurrentHashMap<>();

    SystemData(String dataType) {
        this(dataType,false);
    }

    SystemData(String dataType,boolean reverse) {
        super(reverse);
        this.dataType = dataType;
    }

    /**
     * @description: 获取系统数据
     * @param: []
     * @return: java.util.Map<java.lang.Object,java.lang.Object>
     * @author: xdy
     * @create: 2019-06-10 17-21
     */
    @Override
    public Map<Object, Object> findData() {
        Map<Object,Object> map = cacheDataMap.get(dataType);
        if(map!=null) {
            return map;
        }
        map = new HashMap<>();
        switch (dataType){
            case SHOP_STATUS:
                map.put("关停","0");
                map.put("营业","1");
                break;

        }
        cacheDataMap.put(dataType,map);
        return map;
    }
}
