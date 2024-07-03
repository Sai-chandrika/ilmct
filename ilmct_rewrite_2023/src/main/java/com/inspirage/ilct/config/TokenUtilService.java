package com.inspirage.ilct.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.inspirage.ilct.documents.User;
import com.inspirage.ilct.dto.ApiResponse;
import com.inspirage.ilct.enums.RoleType;
import com.inspirage.ilct.exceptions.InvalidUserTokenException;
import com.inspirage.ilct.repo.UserRepository;
import com.inspirage.ilct.service.PropertiesService;
import com.inspirage.ilct.util.NumberGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.Serial;
import java.io.Serializable;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
public class TokenUtilService implements Serializable {
    public static final String SECRET = "ilmct_2018";

    public static final long EXPIRATION_TIME = 300000;

    private static final long VALIDITY_TIME_MS = 10 * 24 * 60 * 60 * 1000;// 10 days Validity

    private final String secret = "SsvUBb&\\8K$#B_w;cfz-7hS_)w;4He4L2VkjKUSP\"wB!;?2d#5[%=FQFt6T3ujjvTuqX}AK+,G7s.W)n^7(97CMFy\"+XvvrC+;~Q?gmn2=6E#aCB)A,XnTx.`m+GaX2buuSCxA@3Fp['XveB;nxnDwJau`-~.8w8/Agw7s3GGq?q5s\\>9@#\\FQcbn9>bC9D+w#PcF!tEF@r'NBK3gX~DJ.8dD_T\\-fp7X:7aP]B>2hYN\"(\\w8?h2Ye6zkWbNbwS(s-/:J'%XQ`q~D(kZ>.TQ>qw5n`h^QK~Un:E@RxkuP]?mSMYXuu.^:_24F%/a.B=F)y?VUbcPG8t`\\\"jGh;Eb}mK/6P{&7@m:SC;_8\"$Zbx5\\W'/CMKqJ#F5S(z.C`nHHTEGHV]Ur{su2W#<;\\P7aF8?zF?ms^yEGAL-}{r2EwY,%PCVrC;Q)g!p&d2@ZEY@3<P;W)-X*!c5vx{zP2:ur7:X^(`&@y\"ZL4L6)d~Jr8c[+{*X#t#,q9H+T[d@'e&sJ";
    public static final String AUTH_HEADER_NAME = "Authorization";
    private static final String AUTH_HEADER_TOKEN_PREFIX = "Bearer";
    public static final String X_API_KEY_NAME = "X-API-KEY";
    public static final String USER_NAME_KEY = "UserName";
    public static final String PASSWORD_KEY = "Password";
    @Serial
    private static final long serialVersionUID = -1029281748694725202L;
    @Autowired
    RedisService redisService;
    @Autowired
    UserRepository userRepository;
    private final PropertiesService propertiesService;

    @Autowired
    public TokenUtilService(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }


    private String generateKeyForRedis(String userId, String hash) {
        return String.format("%s:%s::%s", userId, hash, "");
    }

    public static String generateJWTToken(User driver) {
        String token = null;
        try {
            token = Jwts.builder().setSubject(driver.getEmail())
                    .claim("userId", new ObjectMapper().writeValueAsString(driver))
                    .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(SignatureAlgorithm.HS256, SECRET).compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    public  Claims getJWTClaims(String token) {

        Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build().parseClaimsJws(token).getBody();
    }

    public  String getUserId(HttpServletRequest request) {
        try {
            String token = request.getHeader(AUTH_HEADER_NAME);
            String jwtToken;
            if (ObjectUtils.isEmpty(token))
                return null;
            jwtToken = token.replace(AUTH_HEADER_TOKEN_PREFIX, "").trim();
            Claims claims = getJWTClaims(jwtToken);
            return (String) claims.get("userId");
        } catch (Exception e) {
            //e.printStackTrace();
        }
        return null;
    }

    public User getUserFromToken(HttpServletRequest request) throws InvalidUserTokenException {

        User user = parseUserFromToken(request.getHeader(AUTH_HEADER_NAME));
        return userRepository.findOneByUserId(user.getUserId()).orElse(null);

    }

    public ResponseEntity deleteUserTokenFromRedis(HttpServletRequest request, Authentication authentication) {
        String token = request.getHeader(AUTH_HEADER_NAME);
        String jwtToken;
        if (StringUtils.isEmpty(token))
            return new ResponseEntity<>(new ApiResponse(HttpStatus.BAD_REQUEST, "Missing header Authorization Token"), HttpStatus.BAD_REQUEST);
        jwtToken = token.replace(AUTH_HEADER_TOKEN_PREFIX, "").trim();
        Claims claims = getJWTClaims(jwtToken);
        String userId = (String) claims.get("userId");
        String hash = claims.get("userKey").toString();
        redisService.deleteKey(this.generateKeyForRedis(userId, hash));
        return new ResponseEntity<>(new ApiResponse(HttpStatus.OK, "Successfully logout"), HttpStatus.OK);
    }


    public static void main() {

    }

    public Optional<Authentication> verifyToken(HttpServletRequest request) throws InvalidUserTokenException {

        String token = request.getHeader(AUTH_HEADER_NAME);
        if (!StringUtils.isEmpty(token)) {
            try {
                User user = parseUserFromToken(token.replace(AUTH_HEADER_TOKEN_PREFIX, "").trim());
                if (user != null) {
                    return Optional.of(new UserAuthentication(new LoginUser(user)));
                }
            } catch (UsernameNotFoundException | InvalidUserTokenException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        token = request.getHeader(X_API_KEY_NAME);
        if (!StringUtils.isEmpty(token)) {
            try {
                if (!(request.getRequestURI().startsWith("/v1/otm/save") || request.getRequestURI().startsWith("/ilmct/v3/otm-post/saveShipmentXml") || request.getRequestURI().startsWith("/ilmct/v3/otm-post/saveOTMShipmentXml") || request.getRequestURI().startsWith("/v3/otm-post/saveShipmentXml") || request.getRequestURI().startsWith("/v3/otm-post/saveOTMShipmentXml") || request.getRequestURI().startsWith("/v3/otm-post/saveShipmentStatus") || request.getRequestURI().startsWith("/ilmct/v3/otm-post/saveShipmentStatus")))
                    throw new InvalidUserTokenException("Unauthorized Access ! URL not accessible.");

                if (!propertiesService.getX_API_KEY_VALUE().equals(token))
                    throw new InvalidUserTokenException("Invalid " + X_API_KEY_NAME);

                User dummyUserByAuthKey = new User();
                dummyUserByAuthKey.setUserId(propertiesService.getALLOWED_IP());
                dummyUserByAuthKey.setPassword(propertiesService.getX_API_KEY_VALUE());
                dummyUserByAuthKey.setRole(RoleType.OTM_SERVICE_ADMIN);

                return Optional.of(new UserAuthentication(new LoginUser(dummyUserByAuthKey)));
            } catch (UsernameNotFoundException | InvalidUserTokenException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String userName = request.getHeader(USER_NAME_KEY);
        String password = request.getHeader(PASSWORD_KEY);
        if (!StringUtils.isEmpty(userName) && !StringUtils.isEmpty(password)) {
            try {
                if (!propertiesService.getUSER_NAME().equals(userName.trim()) || !propertiesService.getPASSWORD().equals(password.trim()))
                    throw new InvalidUserTokenException("Unauthorized Access ! Incorrect User name or password.");

                User dummyUserByAuthKey = new User();
                dummyUserByAuthKey.setUserId(userName);
                dummyUserByAuthKey.setPassword(password);
                dummyUserByAuthKey.setRole(RoleType.OTM_SERVICE_ADMIN);

                return Optional.of(new UserAuthentication(new LoginUser(dummyUserByAuthKey)));
            } catch (UsernameNotFoundException | InvalidUserTokenException e) {
                throw e;
            } catch (Exception e) {
            }
        }

        return Optional.empty();
    }


    public String createTokenForUser(LoginUser loginUser, String requestedFrom) {
        String hash = NumberGenerator.generateUserSesssionToken();
        String token = createTokenForUser(loginUser.getUser(), hash);
        //TODO:: Commented for security Pass
        redisService.setValue(
                this.generateKeyForRedis(loginUser.getUsername().toLowerCase(), hash),
                loginUser.getUser(),
                100,
                true
        );
        return token;
    }

    public String createTokenForUser(User user, String hash) {
        Key secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + VALIDITY_TIME_MS))
                .setSubject(user.getUserId())
                .claim("userId", user.getUserId())
                .claim("role", user.getDefaultUserRole())
                .claim("userKey", hash)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                //.signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }



    public User parseUserFromToken(String token) throws UsernameNotFoundException, InvalidUserTokenException {
        Claims claims = getJWTClaims(token);
        String userId = (String) claims.get("userId");
        String hash = claims.get("userKey").toString();
        String key = this.generateKeyForRedis(userId, hash);
        //TODO:: Commented for security Pass
        User user = (User) redisService.getValue(key, User.class);
        if (user == null) {
            throw new InvalidUserTokenException("Session Expired");
        }
          redisService.setValue(key, user, propertiesService.getSESSION_TIMEOUT(), true);
        user = userRepository.findOneByUserId(userId).orElse(null);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        user.setUserId(userId);
        user.setPassword(user.getPassword());
        user.setDefaultUserRole(((String) claims.get("role")));
        return (user);
    }

    public static void main(String[] args){
        String redisHost = "localhost";
        int redisPort = 6379;
        String redisUsername = "your_redis_username";
        String redisPassword = "your_redis_password";
        RedisClient redisClient = RedisClient.create("redis://" + redisHost + ":" + redisPort);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        try {
            RedisCommands<String, String> syncCommands = connection.sync();
            String response = syncCommands.ping();

            System.out.println("Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        redisClient.shutdown();
    }

}
