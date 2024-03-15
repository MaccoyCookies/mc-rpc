package com.maccoy.mcrpc.provider;

import com.maccoy.mcrpc.core.annotation.McProvider;
import com.maccoy.mcrpc.demo.api.IUserService;
import com.maccoy.mcrpc.demo.api.User;
import org.springframework.stereotype.Service;

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
}
