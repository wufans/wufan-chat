package com.wufan.chat.wfchattestclient.securityconfig;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wufan.chat.wfchatcommon.rest.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.cors.CorsUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author: wufan
 * @Date: 2019/7/5 13:28
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private CustomAuthenticationProvider authProvider;

    protected void configure(HttpSecurity httpSecurity) throws Exception{
        httpSecurity
                    .cors()
                .and()
                    .authorizeRequests()
                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll() // 这句比较重要，放过 option 请求
//                    .antMatchers("/**").permitAll()
                    .antMatchers("/swagger-ui.html","/account/login","/account/register","/login","/account/varifyname").permitAll()
                    .anyRequest().authenticated()
                    // 无权访问是返回 json 格式数据。
//                .and()
//                .httpBasic().authenticationEntryPoint(authenticationEntryPoint)
                // 登录响应
                .and()
                    .formLogin()
                    .loginPage("/account/return/failure").permitAll()//请求时未登录跳转接口
                    .loginProcessingUrl("/account/login")//post登录接口，登录验证由系统实现
                    .failureHandler(authenticationFailureHandler)
                    .successHandler(authenticationSuccessHandler).permitAll()
                // 退出登录
                .and()
                    .logout()
                    .logoutSuccessHandler(logoutSuccessHandler).permitAll()
//                    .deleteCookies("myCookie") //删除自定义的cookie

                // 关闭crsf因为crsf还有token之类的安全防护，所以这种方式并不安全
                .and()
                    .csrf().disable();
    }


//    protected void configure(HttpSecurity httpSecurity) throws Exception{
//
//        httpSecurity
//                .cors()
//                .and()
//                .authorizeRequests()//配置权限
//                    .antMatchers(HttpMethod.OPTIONS, "*").permitAll()//允许跨域请求中的options请求
//                    .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
////                    .antMatchers("/**").permitAll()
//                    .antMatchers("/account/login","/account/register","/login","/account/varifyname","/account/return/**").permitAll()
//                    .anyRequest().authenticated()
//                    .and()
//                .formLogin()//开启formLogin默认配置
//                    .loginPage("/account/return/failure").permitAll()//请求时未登录跳转接口
//                    .failureUrl("/account/return/failure")//用户密码错误跳转接口
//                    .defaultSuccessUrl("/account/return/success",true)//登录成功跳转接口
//                    .loginProcessingUrl("/account/login")//post登录接口，登录验证由系统实现
//                    .usernameParameter("username")	//要认证的用户参数名，默认username
//                    .passwordParameter("password")	//要认证的密码参数名，默认password
//                    .and()
//                .logout()//配置注销
//                    .logoutUrl("/account/logout")//注销接口
//                    .logoutSuccessUrl("/account/return/logout").permitAll()//注销成功跳转接口
//                    .deleteCookies("myCookie") //删除自定义的cookie
//                    .and()
//                .csrf().disable();
////                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());           //禁用csrf,否则非thymeleaf表单提交会被拦截
//
//        CharacterEncodingFilter filter = new CharacterEncodingFilter();
//        filter.setEncoding("UTF-8");
//        filter.setForceEncoding(true);
//        httpSecurity.addFilterBefore(filter, CsrfFilter.class);
//    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception{
        authenticationManagerBuilder
                        .authenticationProvider(authProvider); //方法一

//                      .userDetailsService(systemUserService()); //方法二

//                    .inMemoryAuthentication() //固定密码
    //                .withUser("user").password("password").roles("USER");
    }

    /**
     *从数据库中读取用户信息并封装到 UserDetail 接口
     */
//    @Bean
//    protected UserDetailsService systemUserService(){
//        return new LogInService();
//    }
    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/resources/static/**");
    }

    AuthenticationEntryPoint authenticationEntryPoint = new AuthenticationEntryPoint() {
        @Override
        public void commence(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
                AuthenticationException e) throws IOException {
            RestResponse restResponse = new RestResponse();
            restResponse.setCode(401);
            httpServletResponse.getWriter().write(JSON.toJSONString(restResponse));
        }
    };
    private AuthenticationFailureHandler authenticationFailureHandler = new AuthenticationFailureHandler() {
        @Override
        public void onAuthenticationFailure(HttpServletRequest req, HttpServletResponse resp,
                AuthenticationException e) throws IOException {
            resp.setContentType("application/json;charset=utf-8");
            RestResponse restResponse = new RestResponse();
            if (e instanceof BadCredentialsException || e instanceof UsernameNotFoundException) {
                restResponse.setMsg("账户名或者密码输入错误!").setCode(400);
            } else if (e instanceof LockedException) {
                restResponse.setMsg("账户被锁定，请联系管理员!").setCode(400);
            } else if (e instanceof CredentialsExpiredException) {
                restResponse.setMsg("密码过期，请联系管理员!").setCode(400);
            } else if (e instanceof AccountExpiredException) {
                restResponse.setMsg("账户过期，请联系管理员!").setCode(400);
            } else if (e instanceof DisabledException) {
                restResponse.setMsg("账户被禁用，请联系管理员!").setCode(400);
            } else {
                restResponse.setMsg("登录失败!").setCode(400);
            }
            resp.setStatus(401);
            PrintWriter out = resp.getWriter();
            out.write(JSON.toJSONString(restResponse));
            out.flush();
            out.close();
        }
    };
    private AuthenticationSuccessHandler authenticationSuccessHandler = new AuthenticationSuccessHandler() {
        @Override
        public void onAuthenticationSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication auth)
                    throws IOException {
            resp.setContentType("application/json;charset=utf-8");
            RestResponse restResponse = new RestResponse();
            ObjectMapper om = new ObjectMapper();
            PrintWriter out = resp.getWriter();
            out.write(om.writeValueAsString(restResponse.setMsg("登录成功!")));
            out.flush();
            out.close();
        }
    };
    private LogoutSuccessHandler logoutSuccessHandler = new LogoutSuccessHandler() {
        @Override
        public void onLogoutSuccess(HttpServletRequest req, HttpServletResponse resp, Authentication authentication)
                    throws IOException, ServletException {
            resp.setContentType("application/json;charset=utf-8");
            RestResponse restResponse = new RestResponse();
            PrintWriter out = resp.getWriter();
            restResponse.setMsg("注销成功!");
            if(authentication == null){
                System.out.println("没有用户登录但是请求了注销！");
                restResponse.setMsg("请先登录再注销！").setCode(400);
            }else{
                System.out.println(authentication.getPrincipal()+" 注销成功！");
            }
            out.write(JSON.toJSONString(restResponse));
            out.flush();
            out.close();
        }
    };
}
