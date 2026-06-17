package org.example.strayanimalrescuebackend.Filter;

import com.alibaba.fastjson2.JSONObject;
import org.example.strayanimalrescuebackend.Util.JwtUtil;
import org.example.strayanimalrescuebackend.model.ResponseResult;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;


@Component
@WebFilter("/*")
public class LoginCheckFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        //解决跨域问题
        String origin = request.getHeader("Origin");
        if (origin != null) {
            // 允许携带凭证时，指定具体的源
            response.setHeader("Access-Control-Allow-Origin", origin);
        }
        // 处理预请求的代码
        if ("OPTIONS".equals(request.getMethod())) {
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, HEAD, OPTIONS");
            // 明确列出允许的请求头
            response.setHeader("Access-Control-Allow-Headers", "token, Content-Type");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        /*1.获取请求路径*/
        String url = request.getRequestURI();
        System.out.println("请求路径为："+url);
        /*2.判断路径中是否包含login和register,包含则放行,不包含则要进行登录校验*/
        if (url.contains("login") || url.contains("register")|| url.contains("swagger-ui")
                || url.contains("swagger-ui.html")
                || url.contains("v3/api-docs")
                || url.contains("swagger-resources")
                || url.equals("/shelters/all")) {
            System.out.println("登录操作或者注册操作，放行");
            filterChain.doFilter(request,response);
            return;
        }

        /*3.获取请求头中的token*/
        String token = request.getHeader("token");
        System.out.println("获取到的token："+token);
        /*4.判断令牌是否存在，不存在则返回错误结果*/
        if (!StringUtils.hasLength(token)) {
            System.out.println("token不存在");
            ResponseResult responseResult = new ResponseResult(HttpStatus.BAD_REQUEST.value(), "token is null,no login",null,0L);
            String result = JSONObject.toJSONString(responseResult);
            response.getWriter().write(result);
            return;
        }

        /*5.解析token，解析失败则返回错误结果*/
        try {

            String username = JwtUtil.extractUsername(token);
            if (username != null && JwtUtil.validateToken(token, username)) {
                // 如果 Token 验证成功，设置认证信息
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, null, new ArrayList<>());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            System.out.println("token解析失败");
            ResponseResult responseResult = new ResponseResult(HttpStatus.BAD_REQUEST.value(), "token parse fail,no login",null,0L);
            String result = JSONObject.toJSONString(responseResult);
            response.getWriter().write(result);
            return;
        }

        /*6.放行*/
        System.out.println("token解析成功,放行");
        filterChain.doFilter(request,response);
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
