package com.maccoy.mcrpc.provider;

import com.maccoy.mcrpc.core.annotation.McProvider;
import com.maccoy.mcrpc.demo.api.IOrderService;
import com.maccoy.mcrpc.demo.api.IUserService;
import com.maccoy.mcrpc.demo.api.Order;
import com.maccoy.mcrpc.demo.api.User;
import org.springframework.stereotype.Service;

@Service
@McProvider
public class OrderServiceImpl implements IOrderService {

    @Override
    public Order selectOrderById(Integer id) {
        return new Order(id.longValue(), 19.5F);
    }
}
