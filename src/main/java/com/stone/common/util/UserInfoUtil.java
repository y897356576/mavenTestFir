package com.stone.common.util;

import com.stone.common.redis.RedisShard;
import com.stone.core.factory.UserFactory;
import com.stone.core.model.User;
import org.apache.commons.lang3.StringUtils;
import redis.clients.jedis.Jedis;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by 石头 on 2018/2/26.
 */
public class UserInfoUtil {

    /**
     * 从cookie中获取userId，根据userId从redis中获取用户信息
     * @param request
     * @return
     */
    public static User getUserFromRedis(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies() == null ? new Cookie[0] : request.getCookies();
        String userId = "";
        for (Cookie cookie : cookies){
            if(cookie.getName().equals("userId")){
                userId = cookie.getValue();
            }
        }
        if (StringUtils.isBlank(userId)) {
            return null;
        }

        User user = null;
        Jedis jedis = RedisShard.getRedisNode(userId);
        Map userMap = jedis.hgetAll(userId + "_user");
        if (userMap != null && userMap.size() > 0) {
            user = (User) ObjMapTransUtil.mapToObj(userMap, User.class);
            UserFactory.standardUser(user);
        }
        return user;
    }

}