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
    public Long getId(long id) {
        return id;
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
