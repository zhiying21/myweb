package com.zhiying.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zhiying.entity.DemoEntity;
import com.zhiying.mapper.DemoMapper;
import com.zhiying.service.DemoService;
import org.springframework.stereotype.Service;

/**
 * 示例 Service 实现
 */
@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, DemoEntity> implements DemoService {
}
