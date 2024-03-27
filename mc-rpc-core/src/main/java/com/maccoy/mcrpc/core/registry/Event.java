package com.maccoy.mcrpc.core.registry;

import com.maccoy.mcrpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    List<InstanceMeta> data;

}
