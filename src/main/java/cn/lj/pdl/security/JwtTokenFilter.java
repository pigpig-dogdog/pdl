package cn.lj.pdl.security;

import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.dto.Body;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author luojian
 * @date 2019/11/23
 */
public class JwtTokenFilter extends OncePerRequestFilter {

    private JwtTokenProvider jwtTokenProvider;

    JwtTokenFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        try {
            if (!StringUtils.isEmpty(token) && jwtTokenProvider.validateToken(token)) {
                // token验证成功
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (BizException e) {
            // token验证失败，以json格式返回
            SecurityContextHolder.clearContext();

            Body body = Body.buildFail(e);
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(e.getHttpStatus().value());
            response.getWriter().write(new ObjectMapper().writeValueAsString(body));
            response.flushBuffer();
            return;
        }

        // 若token为空，走这个分支
        /* 后续会向前端返回http响应，至于为什么我也不太清楚，应该是security包的内部操作
         * http包格式如下:
         * status: 403 Forbidden
         * body:
         * {
         *    "timestamp": "20xx-xx-xxTxx:xx:xx.xxx+0000",
         *    "status": 403,
         *    "error": "Forbidden",
         *    "message": "Access Denied",
         *    "path": "xxx"
         * }
         */
        filterChain.doFilter(request, response);
    }
}
