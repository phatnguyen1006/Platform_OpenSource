/*
Given two binary strings a and b, return their sum as a binary string.

 

Example 1:

Input: a = "11", b = "1"
Output: "100"
Example 2:

Input: a = "1010", b = "1011"
Output: "10101"
 

Constraints:

1 <= a.length, b.length <= 104
a and b consist only of '0' or '1' characters.
Each string does not contain leading zeros except for the zero itself.
*/

class Solution {
public:
    string addBinary(string a, string b) {
        int aLength = a.length();
        int bLength = b.length();
        int cLength = aLength > bLength ? aLength + 1 : bLength + 1;

        string c (cLength, '0');

        int offset = 0;
        int i = 0;
        while (i < cLength) {

            if (aLength > i && bLength > i) {
                c[cLength - i - 1] = (a[aLength - i - 1] & 1) + (b[bLength - i - 1] & 1) + offset;
            } else {
                if (aLength > i) {
                    // fill in
                    c[cLength - i - 1] = (a[aLength - i - 1] & 1) + offset;
                } else if (bLength > i) {
                    // fill in
                    c[cLength - i - 1] = (b[bLength - i - 1] & 1) + offset;
                } else {
                    c[cLength - i - 1] = offset;
                }
            }

            if (c[cLength - i - 1] >= 2) {
                offset = 1;
            } else {
                offset = 0;
            }
            c[cLength - i - 1] = c[cLength - i - 1] & 1 | 48;
            i++;
        }

        if (cLength >= 2 && c[0] == '0') {
            c.erase(0, 1);
        }

        return c;
    }
};
