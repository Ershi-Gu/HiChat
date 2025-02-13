package com.ershi.hichat.common.sensitive.algorithm.sensitiveWord.acpro;

import java.util.*;


/**
 * AC自动机实现敏感词过滤-支持特殊符号过滤
 * @author Ershi
 * @date 2025/02/13
 */
public class ACProTrie {

    private final static char MASK = '*'; // 替代字符
    private static final String SPECIAL_SYMBOLS = " !*-+_=,，.@;:；：。、？?（）()【】[]《》<>“”\"‘’";

    private Word root;

    static class Word {
        boolean end = false;          // 是否为敏感词结尾
        Word failOver = null;         // 失败指针
        int depth = 0;                // 节点深度（根节点到该节点的长度）
        Map<Character, Word> next = new HashMap<>();

        public boolean hasChild(char c) {
            return next.containsKey(c);
        }
    }

    // 构建AC自动机
    public void createACTrie(List<String> list) {
        root = new Word();
        for (String keyword : list) {
            Word current = root;
            for (int i = 0; i < keyword.length(); i++) {
                char c = keyword.charAt(i);
                if (!current.next.containsKey(c)) {
                    current.next.put(c, new Word());
                }
                current = current.next.get(c);
                current.depth = i + 1;  // 更新节点深度
            }
            current.end = true; // 标记敏感词结尾
        }
        initFailOver(); // 初始化失败指针
    }

    // 初始化失败指针（BFS构建）
    private void initFailOver() {
        Queue<Word> queue = new LinkedList<>();
        root.failOver = null;
        queue.addAll(root.next.values());

        while (!queue.isEmpty()) {
            Word current = queue.poll();

            for (Map.Entry<Character, Word> entry : current.next.entrySet()) {
                char c = entry.getKey();
                Word child = entry.getValue();
                Word failOver = current.failOver;

                // 回溯失败指针直到找到匹配或根节点
                while (failOver != null && !failOver.hasChild(c)) {
                    failOver = failOver.failOver;
                }
                child.failOver = (failOver == null) ? root : failOver.next.get(c);
                queue.add(child);
            }
        }
    }

    // 匹配并替换敏感词（支持特殊符号忽略）
    public String match(String text) {
        // 预处理：过滤特殊符号并记录原始位置
        List<Character> filteredChars = new ArrayList<>();
        List<Integer> originalIndices = new ArrayList<>();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (!isSpecialSymbol(c)) {
                filteredChars.add(c);
                originalIndices.add(i);
            }
        }

        boolean[] mask = new boolean[text.length()]; // 标记需替换的位置
        Word currentState = root;

        for (int i = 0; i < filteredChars.size(); ) {
            char c = filteredChars.get(i);
            // 失败指针回溯
            while (currentState != null && !currentState.hasChild(c)) {
                currentState = currentState.failOver;
            }
            if (currentState == null) {
                currentState = root;
                i++;
                continue;
            }

            currentState = currentState.next.get(c);
            if (currentState.end) {
                // 查找最长匹配
                int maxLength = currentState.depth;
                int endIndex = i;
                Word longestMatch = currentState;

                // 继续向后查找更长的匹配
                for (int j = i + 1; j < filteredChars.size(); j++) {
                    char nextChar = filteredChars.get(j);
                    while (longestMatch != null && !longestMatch.hasChild(nextChar)) {
                        longestMatch = longestMatch.failOver;
                    }
                    if (longestMatch == null) break;

                    longestMatch = longestMatch.next.get(nextChar);
                    if (longestMatch.end) {
                        maxLength = longestMatch.depth;
                        endIndex = j;
                    }
                }

                // 计算原始位置范围
                int startPos = originalIndices.get(endIndex - maxLength + 1);
                int endPos = originalIndices.get(endIndex);

                // 标记替换区域
                Arrays.fill(mask, startPos, endPos + 1, true);

                // 跳过已处理部分
                i = endIndex + 1;
                currentState = longestMatch.failOver != null ? longestMatch.failOver : root;
            } else {
                i++;
            }
        }

        // 执行替换
        char[] chars = text.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (mask[i]) chars[i] = MASK;
        }
        return new String(chars);
    }

    // 判断是否为特殊符号
    private boolean isSpecialSymbol(char c) {
        return SPECIAL_SYMBOLS.indexOf(c) != -1;
    }
}