/**
 * 
 */
package com.gupao.bd.sample.rcmdsys.controller;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.gupao.bd.sample.rcmdsys.conf.RcmdSettingLoader;

/**
 * @author george
 * 在应用程序启动时执行初始化工作：加载推荐配置
 */
@Component
public class StartupServlet extends HttpServlet {

    private static final long serialVersionUID = -2534165414821741557L;

    @Override
    public void init() throws ServletException {
        super.init();
        String settingConfPath =  getClass().getResource("/").getPath() + "/rcmd_setting.json";
        try {
            ApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(this.getServletContext());
            RcmdSettingLoader rcmdSettingLoader = applicationContext.getBean(RcmdSettingLoader.class);
            rcmdSettingLoader.loadRcmdSetting(settingConfPath);
        } catch (Exception e) {
            throw new RuntimeException("加载推荐配置失败.", e);
        }
    }

}
