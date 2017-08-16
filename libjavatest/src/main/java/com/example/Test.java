package com.example;

import java.awt.Point;

/**
 * Created by liujun on 17/6/5.
 */

public class Test {
    public static class ListNode {
        int val;
        ListNode next;

        ListNode(int x) {
            val = x;
        }

    }
    //LeetCode(2)
    public static ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        ListNode listNode = null;
        ListNode tmp = null;
        boolean first = true;
        boolean addOne = false;
        while (l1 != null || l2 != null || addOne) {
            int temp = 0;
            if (l1 != null && l2 != null) {
                temp = l1.val + l2.val;
            } else if (l1 != null) {
                temp = l1.val;
            } else if (l2 != null) {
                temp = l2.val;
            }

            if (addOne) {
                temp++;
                addOne = false;
            }
            if (temp >= 10) {
                temp = temp - 10;
                addOne = true;
            }
            if (first) {
                tmp = listNode = new ListNode(temp);
                first = false;
            } else {
                listNode.next = new ListNode(temp);
                listNode = listNode.next;
            }
            if (l1 != null) {
                l1 = l1.next;
            }
            if (l2 != null) {
                l2 = l2.next;
            }
        }
        return tmp;
    }
    //整数反转 LeetCode(7)
    public static int reverse(int x) {
        if (x == 0) return 0;
        StringBuilder str = new StringBuilder(x + "");
        if (str.indexOf("-") == 0) str.deleteCharAt(0);
        StringBuilder reverse = str.reverse();
        while (reverse.indexOf("0") == 0){
            reverse.deleteCharAt(0);
        }
        long l = Long.parseLong(reverse.toString());
        if (l > Integer.MAX_VALUE || -l < Integer.MIN_VALUE) {
            return 0;
        } else {
            return x>0? (int) l : (int) -l;
        }
    }
    //LeetCode(3)
    public static int lengthOfLongestSubstring(String s) {
        if(s.length() == 1)return 1;
        char[] chars = s.toCharArray();
        String maxSubStr = "";
        int max = 0;
        for(int j = 0; j < chars.length; j++) {
            for (int i = j; i < chars.length; i++) {
                if (!maxSubStr.contains(chars[i] + "")) {
                    maxSubStr += chars[i];
                } else {
                    if (max < maxSubStr.length()) {
                        max = maxSubStr.length();
                    }
                    maxSubStr = "";
                    break;
                }
            }
        }
        return max;
    }

    public static boolean isPalindrome(int x) {
        if(x<0)
        {
            return false;
        }

        int tmp=x,res=0;
        while(tmp>0)
        {
            res=res*10+tmp%10;
            tmp=tmp/10;
        }
        return x==res;
    }

    private static void reset(Point[] points){
        points[0] = new Point(0,0);
        points[1] = new Point(1,1);
        points[2] = new Point(2,2);
        points[3] = new Point(3,3);

    }
    public static void main(String args[]) {
        Point[] points = new Point[4];

        reset(points);
        System.out.println(points[1]);
    }
}
