package com.maccoy.mcrpc.provider;

import com.alibaba.fastjson.JSON;
import com.maccoy.mcrpc.core.annotation.McProvider;
import com.maccoy.mcrpc.core.api.RpcContext;
import com.maccoy.mcrpc.demo.api.IUserService;
import com.maccoy.mcrpc.demo.api.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@McProvider
public class UserServiceImpl implements IUserService {

    @Autowired
    Environment environment;

    @Override
    public User findById(int id) {
        return new User(id, "Mc-V1"
                + environment.getProperty("server.port")
                + "_" + System.currentTimeMillis());
    }

    @Override
    public User findById(int id, String name) {
        return new User(id, "Mc-" + name + "_" + System.currentTimeMillis());
    }

    @Override
    public long getId(long id) {
        return id;
    }

    @Override
    public long getId(User user) {
        return user.getId().longValue();
    }

    @Override
    public long getId(float id) {
        return 1L;
    }

    @Override
    public String getName() {
        return "Mc123";
    }

    @Override
    public String getName(int id) {
        return "Mc-" + id;
    }

    @Override
    public int[] getIds() {
        return new int[] {100,200,300};
    }

    @Override
    public long[] getLongIds() {
        return new long[]{1,2,3};
    }

    @Override
    public int[] getIds(int[] ids) {
        return ids;
    }

    @Override
    public User[] findUsers(User[] users) {
        return users;
    }

    @Override
    public List<User> getList(List<User> userList) {
        User[] users = userList.toArray(new User[0]);
        System.out.println(" ===> userList.toArray()[] = ");
        Arrays.stream(users).forEach(System.out::println);
        userList.add(new User(19, "Mc"));
        return userList;
    }

    @Override
    public Map<String, User> getMap(Map<String, User> userMap) {
        userMap.values().forEach(x -> System.out.println(x.getClass()));
        User[] users = userMap.values().toArray(new User[userMap.size()]);
        System.out.println(" ==> userMap.values().toArray()[] = ");
        Arrays.stream(users).forEach(System.out::println);
        userMap.put("A2024", new User(2024,"Mc2024"));
        return userMap;
    }

    @Override
    public Boolean getFlag(boolean flag) {
        return !flag;
    }

    @Override
    public User findById(long id) {
        return new User((int) id, "Mc");
    }

    @Override
    public User ex(boolean flag) {
        if (flag) throw new RuntimeException("just throw an exception");
        return new User(19, "Mc");
    }

    @Value("")
    String timeoutPorts = "7001";

    @Override
    public User find(int timeout) {
        String port = environment.getProperty("server.port");
        if (Arrays.asList(timeoutPorts.split(",")).contains(port)) {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException exception) {
                throw new RuntimeException(exception);
            }
        }
        return new User(19, "Mc_" + port);
    }

    @Override
    public void setTimeoutPorts(String timeoutPorts) {
        this.timeoutPorts = timeoutPorts;
    }

    @Override
    public String echoParameter(String key) {
        System.out.println(" ====>> RpcContext.ContextParameters: ");
        RpcContext.getContextParameters().forEach((k,v)-> System.out.println(k+" -> " +v));
        return RpcContext.getContextParameter(key);
    }

}
