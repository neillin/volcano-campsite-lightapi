package com.mservicetech.campsite.sample;

public class LongestSubStr {

    static String getLongestSubStr(String input) {
        String longest = "";
        String key = input.substring(0,2);

        StringBuilder builder = new StringBuilder().append(key);
        for (int i=2; i<input.length(); i++) {
            char c = input.charAt(i);
            if (key.contains(String.valueOf(c))) {
                builder.append(c);
            } else {
                    if (builder.length()>longest.length()) {
                        longest = builder.toString();
                    }
                    builder.setLength(0);
                    key=key.substring(1) + c;
                    builder.append(key);
            }
        }

        return longest;
    }

    public static void main(String[] args) {
        String str = "abcbbbbccccbbdddadacb";
      //  System.out.println(str.substring(0,2));

        System.out.println(getLongestSubStr(str));
    }
}
