package com.maccoy.mcrpc.demo.api;

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
}
