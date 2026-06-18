# Logs 目录说明

`docs/logs/` 采用“一个问题一个文件”的记录方式，不再把所有问题写进同一个总文件。

## 规则

- 一个问题对应一个 Markdown 文件
- 文件名尽量同时体现日期和问题主题
- 问题结束后保留原文件，不做覆盖式整理
- 后续相关问题继续新建文件，而不是把多个问题混写

## 推荐命名

- `YYYY-MM-DD-issue-topic.md`

例如：

- `2026-06-18-ai-transformation-kickoff.md`
- `2026-06-19-smartrecyclerview-loadmore-duplication.md`
- `2026-06-20-readme-outdated-analysis.md`

## 推荐结构

```md
# 问题标题

## 现象

## 初步观察

## 问题拆解

## 当前判断

## 决策

## 待确认

## 下一步
```
