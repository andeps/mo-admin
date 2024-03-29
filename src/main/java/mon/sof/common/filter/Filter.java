package mon.sof.common.filter;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpStatus;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.LogFactory;
import mon.sof.common.exception.BaseException;
import mon.sof.common.tool.token.JWTHelper;
import mon.sof.common.tool.token.LoginRequired;
import mon.sof.common.tool.token.SessionCache;
import mon.sof.common.tool.token.UserTokenTypeEnum;
import mon.sof.project.sys.sysUser.entity.SysUser;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Filter implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws BaseException {
        String url = request.getRequestURI().replace(
            request.getContextPath()+"/",""
        );
        LogFactory.get().debug("**********url**************" + url);
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            LoginRequired methodAnnotation = handlerMethod.getMethodAnnotation(LoginRequired.class);
            if(methodAnnotation != null){
                return true;
            }
        }
        if (url.startsWith("error") || url.startsWith("portal")) {
            if(url.startsWith("error")){
                response.setStatus(HttpStatus.HTTP_NOT_FOUND);
            }
            return true;
        }
        String token = null;
        Cookie[] cookies = request.getCookies();
        if(cookies == null){
            throw new BaseException("token为空，请重新登录！");
        }
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(UserTokenTypeEnum.TOKEN.getName())){
                token = cookie.getValue();
            }
        }
        if(StrUtil.isEmpty(token)){
            throw new BaseException("token为空，请重新登录！");
        }
        String sysUserJson = JWTHelper.verifyToken4Login(token);
        SysUser sysUser = JSONUtil.toBean(sysUserJson, SysUser.class);
        if(ObjectUtil.isNull(sysUser)){
            throw new BaseException("token解析失败！请重新登陆！");
        }
        String newSysUserJson = JSONUtil.toJsonStr(sysUser);
        String newSysUserToken = JWTHelper.createToken4Login(newSysUserJson);
        Cookie cookie = new Cookie(UserTokenTypeEnum.TOKEN.getName(),newSysUserToken);
        cookie.setPath("/");
        response.addCookie(cookie);
        SessionCache.put(UserTokenTypeEnum.TOKEN.getName(),newSysUserJson);
        return true;
    }
    

}
