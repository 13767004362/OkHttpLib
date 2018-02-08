package com.zongke.okhttplib.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ${xinGen} on 2018/1/19.
 *
 * 网络配置类
 *
 */

public class NetConfig {
    /**
     * 是否开启 log
     */
    private boolean isLog;
    /**
     * 通用表头
     */
    private Map<String,String> commonHeaders;
    private NetConfig(){
        this.commonHeaders=new HashMap<>();
    }
    public boolean isLog() {
        return isLog;
    }
    public void setLog(boolean log) {
        isLog = log;
    }
    public void addCommonHeader(String key,String values){
        this.commonHeaders.put(key,values);
    }
    public Map<String, String> getCommonHeaders() {
        return commonHeaders;
    }
    public void setCommonHeaders(Map<String, String> commonHeaders) {
        this.commonHeaders = commonHeaders;
    }
    public static class  Builder{
        private NetConfig netConfig;
        public Builder(){
            this.netConfig=new NetConfig();
        }
        public Builder  setLog(boolean log) {
           this.netConfig. isLog = log;
           return this;
        }
        public Builder addCommonHeader(String key,String values){
            this.netConfig.commonHeaders.put(key,values);
            return this;
        }
        public NetConfig builder(){
          return   this.netConfig;
        }
    }

}
