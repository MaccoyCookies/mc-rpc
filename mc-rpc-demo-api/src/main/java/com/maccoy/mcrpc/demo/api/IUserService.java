package com.maccoy.mcrpc.demo.api;

public interface IUserService {

    User selectUserById(Integer id);

    Integer getId(Integer id);

    String getName(String name);
}
