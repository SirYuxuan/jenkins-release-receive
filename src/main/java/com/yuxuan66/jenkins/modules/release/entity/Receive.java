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
package com.yuxuan66.jenkins.modules.release.entity;

import lombok.Data;

/**
 * @author Sir丶雨轩
 * @since 2022/10/20
 */
@Data
public class Receive {

    /**
     * 类型 api or web
     */
    private String type;
    /**
     * 要去下载的文件名
     */
    private String fileName;
    /**
     * 要保存的文件名
     */
    private String saveName;
    /**
     * 要保存的目录
     */
    private String savePath;

    /**
     * 项目名称，用来判断是否成功
     */
    private String projectName;

    /**
     * 脚本名称
     */
    private String shellName;
}
