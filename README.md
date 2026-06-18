# 🎮 CarpetBotManager

> 一个用于 Fabric 服务端的 Carpet 假人管理模组 — 保存、召唤、分组、自动部署，全部通过聊天交互菜单完成。

[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE.txt)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11~26.2-blue)](gradle.properties)
[![Fabric](https://img.shields.io/badge/Fabric_API-0.141.4~0.152.1-yellow)](gradle.properties)

---

## 📋 目录

- [聊天交互](#-聊天交互)
- [功能概览](#-功能概览)
- [前置依赖](#-前置依赖)
- [快速开始](#-快速开始)
- [指令系统](#-指令系统)
- [配置说明](#-配置说明)
- [自动加载](#-自动加载)
- [项目结构](#-项目结构)
- [TODO](#-todo)

---

## 💬 聊天交互

输入 `/cbot` 打开交互菜单，所有操作通过点击聊天按钮完成：

```
  ==== CarpetBotManager ====
  [Bot list]  [Group list]  [Autoload]
  [Add bot]  [Help]

  ==== Bot List ====
  bot_miner - 主世界挖矿机器人
      [Spawn] [Delete]
  ◀ [Back]
```

按钮都是可点击的聊天组件，悬停显示提示，点击触发对应操作。

---

## ✨ 功能概览

| 功能 | 说明 |
|------|------|
| 💬 **聊天菜单** | `/cbot` 直接进入交互界面，点击按钮完成所有操作 |
| 💾 **Bot 预设** | 以在线玩家位置、维度、朝向为模板保存，支持描述 |
| 🚀 **一键召唤** | 通过 Carpet `/player spawn` 加载 Bot 到预设位置 |
| 📦 **分组管理** | 多个 Bot 组织为 Group，支持批量加载 |
| ⏰ **自动加载** | 通过指令设置开服自动部署 Bot / Group |
| 🔧 **灵活配置** | 可配置权限等级、Bot 名称前缀 |
| 🌍 **多语言** | 内置英文（en_us）与中文（zh_cn）翻译 |

---

## 📦 前置依赖

| 依赖 | 版本 |
|------|------|
| [Fabric Loader](https://fabricmc.net/) | ≥ 0.19.3 |
| [Fabric API](https://fabricmc.net/use/) | ≥ 0.151.0 |
| [Carpet Mod](https://github.com/gnembon/fabric-carpet) | 任意兼容版本 |
| Minecraft | 1.21.11 ~ 26.2（多分支支持） |

> ⚠️ 本模组通过调度 Carpet 的 `/player spawn` 指令来生成假人，必须安装 Carpet。

---

## 🚀 快速开始

```
# 1. 打开聊天菜单
/cbot

# 2. 点击 [Add bot] 查看帮助 → /cbot add bot_miner 挖矿机器人
# 3. 点击菜单中的 [Bot list] → 看到已添加的 bot
# 4. 点击 bot 旁的 [Spawn] → 假人上线
# 5. 创建分组: /cbot group add 挖矿组 自动挖矿 bot_miner bot_digger
# 6. 设为开机自启: 菜单 → [Autoload] → 点击对应选项
```

---

## 🌲 指令系统

```
/cbot                                   # 直接打开聊天交互菜单
├── add <player> [description]          # 保存当前玩家为 Bot 预设
├── remove <name>                       # 删除 Bot 预设
├── load <name>                         # 召唤 Bot
├── list                                # 查看所有 Bot 和 Group
├── help                                # 显示全部指令用法
├── ui                                  # 聊天交互菜单
│   ├── bots / groups / autoload / add
│   └── autoload add/group add <name>
│
├── autoload
│   ├── add <name>                      # Bot 加入自动加载列表
│   ├── remove <name>                   # Bot 移出自动加载列表
│   └── list                            # 查看自动加载设置
│
└── group
    ├── add <name> <description> <bots> # 创建分组（空格分隔 Bot 名）
    ├── remove <name>                   # 删除分组
    ├── load <name>                     # 加载分组内所有 Bot
    └── autoload
        ├── add <name>                  # 分组加入自动加载
        └── remove <name>               # 分组移出自动加载
```

---

## ⚙️ 配置说明

配置文件位于 `config/carpetbotmanager.json`：

```json
{
  "permission_level": 0,
  "bot_name_prefix": "bot_",
  "auto_load_bots": [],
  "auto_load_groups": []
}
```

| 配置项 | 类型 | 默认值 | 说明 |
|--------|------|--------|------|
| `permission_level` | int (0-4) | `0` | 执行指令所需最低权限等级 |
| `bot_name_prefix` | string | `"bot_"` | Bot 名称必须以此前缀开头 |
| `auto_load_bots` | list | `[]` | 开机自启的 Bot 名称列表 |
| `auto_load_groups` | list | `[]` | 开机自启的 Group 名称列表 |

> 💡 `auto_load_bots` 和 `auto_load_groups` 通过 `/cbot autoload` 和 `/cbot group autoload` 管理，也可在交互菜单的 Autoload 页面操作。

---

## ⏰ 自动加载

服务器启动时，模组会按顺序执行：

```
1. 遍历 auto_load_bots → 逐个执行 /player <name> spawn
2. 遍历 auto_load_groups → 展开组内 Bot → 逐个 spawn
```

- 已删除的 Bot 会被优雅跳过
- Carpet 的错误正常显示在服务器日志

---

## 📁 项目结构

```
CarpetBotManager/
├── build.gradle                                  # 构建配置
├── gradle.properties                             # 版本变量
├── settings.gradle                               # 项目设置
└── src/
    ├── main/java/cn/hycer/carpetbotmanager/
    │   ├── Carpetbotmanager.java                 # 入口 — 注册指令 & 自动加载钩子
    │   ├── command/
    │   │   ├── CarpetBotCommand.java             # 指令树注册（路由层）
    │   │   ├── CommandExceptions.java            # 异常常量
    │   │   ├── CommandSuggestions.java           # 补全建议提供器
    │   │   ├── BotHandlers.java                  # Bot 增删查改 & help
    │   │   ├── GroupHandlers.java                # 分组增删加载
    │   │   ├── AutoLoadHandlers.java             # 自动加载管理
    │   │   ├── BotSpawner.java                   # Carpet 假人召唤 & 启动自动加载
    │   │   └── ChatInterface.java                # 聊天交互菜单
    │   ├── config/
    │   │   └── CarpetBotConfig.java              # 配置文件读写 (JSON)
    │   ├── data/
    │   │   └── BotDataManager.java               # Bot/Group 数据持久化
    │   └── model/
    │       ├── BotPreset.java                    # Bot 预设数据模型
    │       └── BotGroup.java                     # Bot 分组数据模型
    │
    ├── main/resources/
    │   ├── fabric.mod.json                       # Fabric 模组元数据
    │   ├── carpetbotmanager.mixins.json          # Mixin 配置
    │   └── assets/carpetbotmanager/lang/
    │       ├── en_us.json                        # 英文翻译
    │       └── zh_cn.json                        # 中文翻译
    │
    └── client/                                   # 客户端 source set
        └── ...
```

---

## 📝 TODO

- [x] Bot 预设保存（位置、维度、朝向、描述）
- [x] Bot 一键召唤（Carpet `/player spawn`）
- [x] Bot 分组管理（创建 / 删除 / 加载）
- [x] 开机自动加载（Bot + Group）
- [x] 自动加载指令管理（`/cbot autoload`）
- [x] 指令权限控制（可配置等级 0-4）
- [x] Bot 名称前缀校验（默认 `bot_`）
- [x] 聊天交互界面（`/cbot` 可点击菜单）
- [x] 国际化（中文 / 英文）
- [ ] 支持更多 Carpet 假人参数（gamemode 等）
- [ ] 热重载配置

---

## 📄 许可

本项目基于 [MIT License](LICENSE.txt) 开源。
