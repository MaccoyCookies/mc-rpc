package com.maccoy.mcrpc.demo.api;

import java.util.List;
import java.util.Map;

public interface IUserService {

    User selectUserById(Integer id);

    User selectUserById(Integer id, String name);

//    Integer getId(Integer id);

    Long getId(long id);

    Long getId(float id);

    Long getId(User user);

    Integer[] getIds();

    Integer[] getIds(Integer[] ids);

    long[] getLongIds();

    String getName(String name);

    String getName(Integer id);

    String paramMap(Map map);

    String paramBean(User user);

    String paramInt(int id);

    String paramLong(long id);

    String paramFloat(float id);

    String paramDouble(double id);

    String paramString(String id);

    String paramArray(int[] ids);

    String paramList(List<Integer> ids);

    String paramMapObject(Map<Object, Object> map);

    String paramNull();



}
