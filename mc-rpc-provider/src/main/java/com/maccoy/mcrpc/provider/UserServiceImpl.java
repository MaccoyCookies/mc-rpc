package com.maccoy.mcrpc.provider;

import com.maccoy.mcrpc.core.annotation.McProvider;
import com.maccoy.mcrpc.demo.api.IUserService;
import com.maccoy.mcrpc.demo.api.User;
import org.springframework.stereotype.Service;

@Service
@McProvider
public class UserServiceImpl implements IUserService {

    @Override
    public User selectUserById(Long id) {
        return new User(id, "Mc-" + System.currentTimeMillis());
    }
}
