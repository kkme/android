package com.link.bianmi.utility;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Int-enum 转换
 *
 */
public class IntEnums {
    public static <T extends Enum<T> & IntEnum> Map<Integer, T> map(Class<T> clazz)
    {
        Map<Integer, T> instanceMap = new HashMap<Integer, T>();
        EnumSet<T> values = EnumSet.allOf(clazz);
        for(T value : values)
        {
            instanceMap.put(value.toInt(), value);
        }
        return instanceMap;
    }
}


