package com.maccoy.mcrpc.demo.api;

public interface IUserService {

    User selectUserById(Integer id);

    User selectUserById(Integer id, String name);

//    Integer getId(Integer id);

    Long getId(long id);

    String getName(String name);

    String getName(Integer id);
}
