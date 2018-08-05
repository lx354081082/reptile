package com.lx.reptile.service.impl;

import com.lx.reptile.mapper.DouyuUserMapper;
import com.lx.reptile.service.UserService;
import com.lx.reptile.util.BarrageConstant;
import com.lx.reptile.util.PageBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private DouyuUserMapper douyuUserMapper;

    @Override
    public PageBean<Map> selectUserByWhere(String where, String username, Integer offset, Integer limit) {
        PageBean<Map> pageBean = new PageBean<>();
        if (where.equals(BarrageConstant.DOUYU)) {
            List<Map> objects = douyuUserMapper.selectByName("%"+username+"%", offset, limit);
            pageBean.setRows(objects);
            pageBean.setTotal(douyuUserMapper.selectCountByName("%"+username+"%"));
        }
//        if (where.equals(BarrageConstant.PANDA)) {
//            List<Object[]> objects = pandaUserRepository.selectByName(username, offset, limit);
//            toDTO(objects,userDTOS,"熊猫");
//            pageBean.setRows(userDTOS);
//            pageBean.setTotal(pandaUserRepository.selectCountByName(username));
//        }
        return pageBean;
    }

    @Override
    public Map selectByWhereAndUid(String where, String id) {
        Map map = new HashMap();
        if (where.equals(BarrageConstant.DOUYU)) {
            map = douyuUserMapper.findOne("douyu_user", id);
        }
        return map;
    }
}
