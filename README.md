# 🎮 CarpetBotManager

> 一个用于 Fabric 服务端的 Carpet 假人管理模组 — 便捷地保存、加载、分组、自动部署你的假人。

[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE.txt)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-blue)](gradle.properties)
[![Fabric](https://img.shields.io/badge/Fabric_API-0.141.4-yellow)](gradle.properties)

---

## 📋 目录

- [功能概览](#-功能概览)
- [前置依赖](#-前置依赖)
- [快速开始](#-快速开始)
- [指令系统](#-指令系统)
- [配置说明](#-配置说明)
- [自动加载](#-自动加载)
- [项目结构](#-项目结构)
- [TODO](#-todo)

---

## ✨ 功能概览

| 功能 | 说明 |
|------|------|
| 💾 **Bot 预设** | 以在线玩家的位置、维度、朝向为模板，保存为 Bot 配置 |
| 🚀 **一键召唤** | 通过 Carpet `/player` 指令加载 Bot 到预设位置 |
| 📦 **分组管理** | 将多个 Bot 组织为 Group，支持批量加载 |
| ⏰ **自动加载** | 服务器启动后自动部署指定的 Bot / Group |
| 🔧 **灵活配置** | 可配置指令权限等级、Bot 名称前缀等 |
| 🌍 **多语言** | 内置英文（en_us）与中文（zh_cn）翻译 |

---

## 📦 前置依赖

| 依赖 | 版本 |
|------|------|
| [Fabric Loader](https://fabricmc.net/) | ≥ 0.18.5 |
| [Fabric API](https://fabricmc.net/use/) | 0.141.4+ |
| [Carpet Mod](https://github.com/gnembon/fabric-carpet) | 任意 1.21 兼容版本 |

> ⚠️ 本模组通过调度 Carpet 的 `/player spawn` 指令来生成假人，必须安装 Carpet。

---

## 🚀 快速开始

```
# 1. 添加 Bot 预设（玩家 bot_miner 必须在线）
/cbot add bot_miner 主世界挖矿机器人

# 2. 查看所有 Bot
/cbot list

# 3. 召唤 Bot
/cbot load bot_miner

# 4. 创建分组
/cbot group add 挖矿组 自动挖矿 bot_miner bot_digger

# 5. 设为开机自启
/cbot autoload add bot_miner
/cbot group autoload add 挖矿组

# 6. 查看自动加载列表
/cbot autoload list
```

---

## 🌲 指令系统

```
/cbot
├── add <player> [description]          # 保存当前玩家为 Bot 预设
├── remove <name>                       # 删除 Bot 预设
├── load <name>                         # 召唤 Bot
├── list                                # 查看所有 Bot 和 Group
├── help                                # 显示全部指令用法
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

> 💡 `auto_load_bots` 和 `auto_load_groups` 通过 `/cbot autoload` 和 `/cbot group autoload` 指令管理，无需手动编辑。

---

## ⏰ 自动加载

服务器启动时，模组会按顺序执行：

```
1. 遍历 auto_load_bots → 逐个执行 /player <name> spawn
2. 遍历 auto_load_groups → 展开组内 Bot → 逐个 spawn
```

- 已从数据中删除的 Bot 会被优雅跳过
- Carpet 返回的错误会正常显示在服务器日志中
- 加载完成后输出汇总信息到日志

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
    │   │   └── CarpetBotCommand.java             # 全部 /cbot 指令定义与处理
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
    │   ├── carpetbotmanager.mixins.json          # Mixin 配置（暂无使用）
    │   └── assets/carpetbotmanager/lang/
    │       ├── en_us.json                        # 英文翻译
    │       └── zh_cn.json                        # 中文翻译
    │
    └── client/                                   # 客户端 source set（占位）
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
- [x] 国际化（中文 / 英文）
- [ ] 加入 GUI 管理界面
- [ ] 支持更多 Carpet 假人参数（gamemode 等）
- [ ] 通过指令修改已有 Bot 描述
- [ ] 热重载配置

---

## 📄 许可

本项目基于 [MIT License](LICENSE.txt) 开源。
