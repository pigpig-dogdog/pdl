package cn.lj.pdl.security;

import cn.lj.pdl.exception.BizException;
import cn.lj.pdl.exception.BizExceptionEnum;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

/**
 * @author luojian
 * @date 2019/11/23
 */
@Component
public class JwtTokenProvider {

    private static final String TOKEN_HEADER = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final Long EXPIRATION = 7 * 24 * 3600 * 1000L;
    private static String JWT_SECRET_KEY = "C*F-JaNdRgUkXn2r5u8x/A?D(G+KbPeShVmYq3s6v9y$B&E)H@McQfTjWnZr4u7w";

    private UserDetailsServiceImpl userDetailsServiceImpl;

    @Autowired
    public JwtTokenProvider(UserDetailsServiceImpl userDetailsServiceImpl) {
        this.userDetailsServiceImpl = userDetailsServiceImpl;
    }

    @PostConstruct
    protected void init() {
        JWT_SECRET_KEY = Base64.getEncoder().encodeToString(JWT_SECRET_KEY.getBytes());
    }

    public String getUsername(HttpServletRequest req) {
        String token = resolveToken(req);
        return getUsername(token);
    }

    public String createBearerToken(String username) {
        String token = Jwts.builder()
                .setClaims(Jwts.claims().setSubject(username))
                //设置过期时间
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET_KEY)
                .compact();
        return TOKEN_PREFIX + token;
    }

    String resolveToken(HttpServletRequest req) {
        String bearerToken = req.getHeader(TOKEN_HEADER);
        if (bearerToken != null && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(TOKEN_PREFIX.length());
        }
        return null;
    }

    boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(token);
            return true;

        } catch (ExpiredJwtException e) {
            throw new BizException(BizExceptionEnum.USER_LOGIN_STATUS_EXPIRED);

        } catch (UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            throw new BizException(BizExceptionEnum.USER_TOKEN_INVALID);
        }
    }

    Authentication getAuthentication(String token) {
        String username = getUsername(token);
        UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    private String getUsername(String token) {
        return Jwts.parser().setSigningKey(JWT_SECRET_KEY).parseClaimsJws(token).getBody().getSubject();
    }
}
