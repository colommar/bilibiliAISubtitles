# B站AI字幕获取工具

<img src="https://img.shields.io/badge/Java-8-orange.svg" alt="Java 8"/>
<img src="https://img.shields.io/badge/Maven-3.6+-blue.svg" alt="Maven"/>
<img src="https://img.shields.io/badge/Architecture-DDD-green.svg" alt="DDD Architecture"/>

## 📖 项目简介

这是一个用于获取B站视频AI字幕和摘要的工具，采用DDD（领域驱动设计）架构重构，代码结构清晰，易于维护和扩展。

### ✨ 主要功能

- 🔍 **AI摘要获取**: 获取B站视频的AI生成摘要
- 📝 **视频大纲**: 获取视频的分段提纲和要点
- 🎬 **AI字幕**: 获取视频的AI生成字幕
- 💾 **文件输出**: 支持将结果保存到本地文件
- ⏰ **时间戳控制**: 可选择是否显示时间戳信息
- 🔐 **WBI签名**: 自动处理B站API的WBI签名验证

## 🏗️ 项目架构

### 文件结构

```
bilibiliAISubtitles/
├── src/main/java/com/github/colommar/
│   ├── application/                    # 应用层
│   │   ├── BiliWebApplication.java    # 应用主类
│   │   └── service/                   # 应用服务
│   │       ├── BiliWebService.java    # 服务接口
│   │       └── impl/
│   │           └── BiliWebServiceImpl.java  # 服务实现
│   ├── domain/                        # 领域层
│   │   ├── model/                     # 领域模型
│   │   │   ├── AISummaryResponse.java # AI摘要响应模型
│   │   │   ├── VideoDetails.java      # 视频详情模型
│   │   │   └── WbiKeys.java           # WBI密钥模型
│   │   └── service/                   # 领域服务
│   │       ├── BiliWebDomainService.java     # 领域服务接口
│   │       └── impl/
│   │           └── BiliWebDomainServiceImpl.java  # 领域服务实现
│   └── infrastructure/                # 基础设施层
│       ├── config/                    # 配置管理
│       │   └── ConfigLoader.java      # 配置加载器
│       ├── gateway/                   # 网关层
│       │   ├── BiliWebGateway.java    # 网关接口
│       │   └── impl/
│       │       └── BiliWebGatewayImpl.java   # 网关实现
│       └── util/                      # 工具类
│           ├── HttpUtil.java          # HTTP工具类
│           └── WbiUtil.java           # WBI工具类
├── src/main/resources/
│   ├── config.properties              # 配置文件
│   └── logback.xml                    # 日志配置
├── pom.xml                           # Maven配置
└── README.md                         # 项目说明
```

### 架构说明

#### 🎯 应用层 (Application Layer)
- **职责**: 应用入口、参数校验、业务流程编排、输出控制
- **主要组件**: 
  - `BiliWebApplication`: 程序主入口，负责配置加载和结果输出
  - `BiliWebService`: 应用服务接口
  - `BiliWebServiceImpl`: 应用服务实现，包含参数校验和异常处理

#### 🧠 领域层 (Domain Layer)
- **职责**: 核心业务逻辑、领域模型定义
- **主要组件**:
  - `AISummaryResponse`: AI摘要响应模型，对应B站API返回的数据结构
  - `VideoDetails`: 视频详情模型（cid、upMid）
  - `WbiKeys`: WBI密钥模型（imgKey、subKey）
  - `BiliWebDomainService`: 领域服务接口
  - `BiliWebDomainServiceImpl`: 领域服务实现，编排业务流程

#### 🔧 基础设施层 (Infrastructure Layer)
- **职责**: 外部系统交互、工具类、配置管理
- **主要组件**:
  - `ConfigLoader`: 配置文件加载器
  - `BiliWebGateway`: B站API网关，处理HTTP请求
  - `HttpUtil`: HTTP请求工具类
  - `WbiUtil`: WBI签名工具类，实现B站API签名算法

## 🚀 使用方法

### 1. 环境准备

- Java 8+
- Maven 3.6+

### 2. 配置设置

编辑 `src/main/resources/config.properties`:

```properties
# B站登录凭证（从浏览器Cookie中获取）
sessdata=你的SESSDATA

# 要获取AI摘要的视频BV号
videoId=video号

# 是否显示时间戳信息
isTimeDetailOn=true

# 是否输出到文件
isFileOutput=true
```

### 3. 运行应用

```bash
# 编译项目
mvn compile

# 运行应用
mvn exec:java -Dexec.mainClass="com.github.colommar.application.BiliWebApplication"
```

### 4. 输出示例

#### 控制台输出
```
获取AI摘要成功
摘要: 中文在网络时代的退化现象。作者通过列举一系列错别字和用法错误...

=== 视频大纲 ===
标题: 中文网络用语错别字泛滥，影响规范使用。 (时间戳: 1s)
  - 网友评论错别字多，勉强能看懂，大家包容心强，不较真 (时间戳: 1s)
  - 作者对错别字的忍耐，输入法导致错别字增多 (时间戳: 50s)

=== AI字幕 ===
标题: 无标题 (时间戳: 1s)
  - 有时候上网啊 (0s - 1s)
  - 看网友的评论内容 (1s - 3s)
```

#### 文件输出
- 文件名格式: `{videoId}_{timestamp}.txt`
- 包含完整的摘要、大纲和字幕信息

## 🔧 技术栈

| 技术 | 版本 | 用途 |
|------|------|------|
| Java | 8 | 开发语言 |
| Maven | 3.6+ | 项目管理 |
| OkHttp | 4.12.0 | HTTP客户端 |
| Gson | 2.11.0 | JSON处理 |
| SLF4J + Logback | 1.7.36 | 日志框架 |
| Lombok | 1.18.30 | 代码简化 |
| Apache Commons Lang3 | 3.17.0 | 工具类 |

## ⚠️ 注意事项

- **SESSDATA获取**: 需要有效的B站SESSDATA才能访问API，可从浏览器开发者工具中获取
- **使用条款**: 请遵守B站的使用条款和API调用频率限制
- **请求频率**: 建议合理控制请求频率，避免被限制
- **数据准确性**: AI生成的内容可能存在不准确的情况，仅供参考
- **隐私保护**: 请勿泄露个人SESSDATA等敏感信息

## 未来更新计划

- [ ] 支持多个VideoId的爬取，遵循B站使用条款
- [ ] 支持VideoId的持久化，减少无效API调用
- [ ] 接入客制化AI总结，允许自定义Prompt，AI调用允许API和本地部署两种方式
- [ ] 建设客制化AI的多轮、记忆模块、本地持久化

## 🤝 贡献指南

欢迎提交Issue和Pull Request来改进这个项目！


## 🔗 相关链接

- [B站开放平台](https://open.bilibili.com/)
- [B站API文档](https://github.com/SocialSisterYi/bilibili-API-collect)
- [WBI签名算法](https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/sign/wbi.md)
