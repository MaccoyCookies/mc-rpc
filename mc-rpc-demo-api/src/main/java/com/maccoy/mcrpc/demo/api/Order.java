package com.maccoy.mcrpc.demo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Maccoy
 * @date 2024/3/10 11:11
 * Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    Long id;
    Float amount;

}
