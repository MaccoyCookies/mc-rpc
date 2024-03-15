package com.maccoy.mcrpc.provider;

import com.alibaba.fastjson.JSON;
import com.maccoy.mcrpc.core.annotation.McProvider;
import com.maccoy.mcrpc.demo.api.IUserService;
import com.maccoy.mcrpc.demo.api.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@McProvider
public class UserServiceImpl implements IUserService {

    @Override
    public User selectUserById(Integer id) {
        return new User(id, "Mc-" + System.currentTimeMillis());
    }

    @Override
    public User selectUserById(Integer id, String name) {
        return new User(id, "Mc-" + name + System.currentTimeMillis());
    }

//    @Override
//    public Integer getId(Integer id) {
//        return id;
//    }


    @Override
    public Long getId(float id) {
        return 19L;
    }

    @Override
    public Long getId(User user) {
        return user.getId().longValue();
    }

    @Override
    public Long getId(long id) {
        return id;
    }

    @Override
    public Integer[] getIds() {
        return new Integer[]{1, 2, 3};
    }

    @Override
    public Integer[] getIds(Integer[] ids) {
        return ids;
    }

    @Override
    public long[] getLongIds() {
        return new long[]{4, 5, 6};
    }

    @Override
    public String getName(String name) {
        return "mc";
    }

    @Override
    public String getName(Integer id) {
        return "Mc-" + id;
    }

    @Override
    public String paramMap(Map map) {
        return JSON.toJSONString(map);
    }

    @Override
    public String paramBean(User user) {
        return JSON.toJSONString(user);
    }

    @Override
    public String paramInt(int id) {
        return String.valueOf(id);
    }

    @Override
    public String paramLong(long id) {
        return String.valueOf(id);
    }

    @Override
    public String paramFloat(float id) {
        return String.valueOf(id);
    }

    @Override
    public String paramDouble(double id) {
        return String.valueOf(id);
    }

    @Override
    public String paramString(String id) {
        return id;
    }

    @Override
    public String paramArray(int[] ids) {
        return JSON.toJSONString(ids);
    }

    @Override
    public String paramList(List<Integer> ids) {
        return JSON.toJSONString(ids);
    }

    @Override
    public String paramMapObject(Map<Object, Object> map) {
        return JSON.toJSONString(map);
    }

    @Override
    public String paramNull() {
        return "response: null";
    }
}
