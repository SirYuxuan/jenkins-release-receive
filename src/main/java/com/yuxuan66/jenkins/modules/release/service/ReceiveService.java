/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yuxuan66.jenkins.modules.release.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RuntimeUtil;
import cn.hutool.core.util.URLUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.yuxuan66.jenkins.modules.release.entity.Receive;
import com.yuxuan66.jenkins.modules.utils.ShellUtil;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author Sir丶雨轩
 * @since 2022/10/20
 */
@Slf4j
@Service
public class ReceiveService {

    private final static String DOWNLOAD_URL = "http://139.9.235.36:51002/";

    /**
     * 接收数据在堡垒机内发布项目
     *
     * @param receive 参数
     */
    @SneakyThrows
    public String release(Receive receive) {
        String url = DOWNLOAD_URL + receive.getType() + "/" + receive.getFileName();
        log.info("Start publishing your project: " + receive.getProjectName() + ", Type: " + receive.getType());
        log.info("Project Download Address: " + url);

        String fileName = receive.getSavePath() + receive.getSaveName();

        if("api".equalsIgnoreCase(receive.getType())){
            if (FileUtil.exist(fileName)) FileUtil.del(fileName);
        }else{
            FileUtil.del(receive.getSavePath());
        }
        // 下载文件
        long fileSize = HttpUtil.downloadFile(url, fileName);
        // 执行Shell并根据启动状态发送推送
        log.info("File download completed, file size: " + fileSize / 1024 / 1024 + "MB");
        if ("api".equalsIgnoreCase(receive.getType())) {
            log.info("The project is an API project and starts executing the restart command");
            ShellUtil.exec(" ./shellApi.sh restart");
            boolean success = false;
            log.info("Start monitoring log");
            for (int i = 0; i < 180; i++) {
                // TODO 这里使用tail命令无法退出执行，使用Java的方式读取文件
                String content = FileUtil.readUtf8String(receive.getSavePath() + "/cataline.log");
                log.info("In the monitoring log...");
                if (content.contains("Exception")) {
                    // 发布失败
                    break;
                }
                if (content.contains("JVM running for")) {
                    success = true;
                    break;
                }
                Thread.sleep(1000);
            }
            HttpUtil.get("http://api.hd-eve.com/api/sendMsg/801407271?msg=" + URLUtil.encode(receive.getProjectName() + "API,自动发布" + (success ? "成功" : "失败")));
            log.info("project release ok status: " + success);
        } else if ("web".equalsIgnoreCase(receive.getType())) {
            // 删除目录内数据

            // 执行解压命令
            ShellUtil.exec("unzip " + receive.getSaveName());
            HttpUtil.get("http://api.hd-eve.com/api/sendMsg/801407271?msg=" + URLUtil.encode(receive.getProjectName() + "WEB,自动发布成功"));
            log.info("project release ok status: true" );
        }

        return "Release Success";
    }


}
