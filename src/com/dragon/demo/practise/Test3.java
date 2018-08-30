package com.dragon.demo.practise;

import java.util.Scanner;

/**
 * 编写“长整数相乘”的程序，实现两个任意长度的长整数（正数）相乘，输出结果
 */
public class Test3 {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入任意两个长整数，注意长度需要一致：");
		String s1 = sc.nextLine();
		String s2 = sc.nextLine();
		while (!s1.matches("\\d+") || !s2.matches("\\d+") || s1.length() != s2.length()){
			System.out.println("输入非法");
			s1 = sc.nextLine();
			s2 = sc.nextLine();
		}
		long start1 = System.currentTimeMillis();
		String res1 = muiltyBigNumByDivide(s1, s2);
		long end1 = System.currentTimeMillis();
		System.out.println("第一种方式计算结果为：");
		System.out.println(res1);
		System.out.println("耗时：" + (end1 - start1) + "ms");
		int len = s1.length();
		int[] a = new int[len];
		int[] b = new int[len];
		for (int i = 0; i < len; i++) {
			a[i] = s1.charAt(i) - '0';
			b[i] = s2.charAt(i) - '0';
		}
		long start2 = System.currentTimeMillis();
		String res2 = bigNumMuiltyByOrgin(a, b);
		long end2 = System.currentTimeMillis();
		System.out.println("第二种方式计算结果为：");
		System.out.println(res2);
		System.out.println("耗时：" + (end2 - start2) + "ms");
	}

	/**
	 * 任意两个长整数相乘：X * Y
	 * 利用分治的思想：这里两个数长度要一样
	 * X = A * 10(n/2次方) + B
	 * Y = C * 10(n/2次方) + D
	 * X * Y = A*C*10(n次方) + (A*D+B*C)*10(n/2次方) + B*D
	 * = A*C*10(n次方) + [(A-B)*(D-C)+BD+AC]*10(n/2次方) + B*D [公式中n/2下取整，n为下取整后乘2]
	 */
	private static String muiltyBigNumByDivide(String X, String Y/*, int n*/) {
		// 两位数时就转成Long来相乘
		if (X.length() == 0 || Y.length() == 0) {
			return 0 + "";
		}
		if (X.length() < 9 && Y.length() < 9) {
			return Long.parseLong(X) * Long.parseLong(Y) + "";
		} else if (X.length() == 1 || Y.length() == 1) {
			return Integer.parseInt(X) * Integer.parseInt(Y) + "";
		} else {
			String A, B, C, D;
			int X_len = X.length();
			if (X.startsWith("-")) {
				A = "-" + X.substring(1, (int) Math.ceil((X.length() - 1) / 2.0) + 1);
				B = "-" + X.substring((int) Math.ceil((X.length() - 1) / 2.0) + 1);
				X_len--;
			} else {
				A = X.substring(0, (int) Math.ceil(X.length() / 2.0));
				B = X.substring((int) Math.ceil(X.length() / 2.0));
			}
			if (Y.startsWith("-")) {
				C = "-" + Y.substring(1, (int) Math.ceil((Y.length() - 1) / 2.0) + 1);
				D = "-" + Y.substring((int) Math.ceil((Y.length() - 1) / 2.0) + 1);
			} else {
				C = Y.substring(0, (int) Math.ceil(Y.length() / 2.0));
				D = Y.substring((int) Math.ceil(Y.length() / 2.0));
			}
			String A_sub_B = sub(A, B);
			String D_sub_C = sub(D, C);
			String A_multi_C = muiltyBigNumByDivide(A, C);
			String B_multi_D = muiltyBigNumByDivide(B, D);
			String A_sub_B_multi_D_sub_C = muiltyBigNumByDivide(A_sub_B, D_sub_C);
//			System.out.println(A + " | " + B + " | " + C + " | " + D);
			return sum(sum(multiTenPower(A_multi_C, (X_len / 2) * 2), multiTenPower(sum(sum(A_sub_B_multi_D_sub_C, B_multi_D), A_multi_C), X_len / 2)), B_multi_D).trim();
		}
	}

	/**
	 * 字符串数字与10的n次方的乘
	 */
	private static String multiTenPower(String str, int n) {
		StringBuilder res = new StringBuilder(str);
		for (int i = 0; i < n; i++) {
			res.append("0");
		}
		return res.toString().trim();
	}

	/**
	 * 计算两个字符串数相减的结果，没有考虑负数str1-str2
	 */
	private static String sub(String str1, String str2) {
		if (str1.startsWith("-") && str2.startsWith("-")) {
			return sub(str2.substring(1), str1.substring(1));
		} else if (str1.startsWith("-")) {
			return "-" + sum(str1.substring(1), str2);
		} else if (str2.startsWith("-")) {
			return sum(str1, str2.substring(1));
		} else {
			// 两个字符串数都为正数
			StringBuilder res = new StringBuilder();
			// 让str1比str2大，如果str2大，就交换到str1
			if (!compare(str1, str2)) {
				String temp = str1;
				str1 = str2;
				str2 = temp;
				res.append("-");
			}
			// 拆成字符数组
			char[] chars = str1.toCharArray();
			int j = str2.length() - 1;
			for (int i = chars.length - 1; i >= 0; i--) {
				if (j != -1) {
					int diff = chars[i] - str2.charAt(j);
					if (diff < 0) {
						chars[i] = (char) (diff + 10 + '0');
						// 一直向前借位，1000 - 1
						int k = i - 1;
						while (--chars[k] < '0') {
							chars[k] += 10;
							k--;
						}
					} else {
						chars[i] = (char) (chars[i] - str2.charAt(j) + '0');
					}
					j--;
				}
			}
			return res.append(doPreZero(chars)).toString().trim();
		}
	}

	/**
	 * 计算两个字符串数字的和
	 */
	private static String sum(String str1, String str2) {
		if (str1.startsWith("-") && str2.startsWith("-")) {
			return "-" + sum(str1.substring(1), str2.substring(1));
		} else if (str1.startsWith("-")) {
			return sub(str2, str1.substring(1));
		} else if (str2.startsWith("-")) {
			return sub(str1, str2.substring(1));
		} else {
			if (!compare(str1, str2)) {
				String temp = str1;
				str1 = str2;
				str2 = temp;
			}
			char[] chs = new char[str1.length() + 1];
			for (int i = chs.length - 1, j = str2.length() - 1; i >= 1; i--, j--) {
				if (j >= 0) {
					int sum = str1.charAt(i - 1) - '0' + str2.charAt(j) - '0';
					if (chs[i] != 0) {
						chs[i] += sum % 10;
						// 进位
						if (chs[i] > '9') {
							chs[i - 1] += ((chs[i] - '0') / 10 + '0');
							chs[i] = (char) ((chs[i] - '0') % 10 + '0');
						}
					} else {
						chs[i] = (char) (sum % 10 + '0');
					}

					if (chs[i - 1] != 0) {
						chs[i - 1] += sum / 10;
					} else {
						chs[i - 1] += sum / 10 + '0';
					}

				} else {
					if (chs[i] != 0) {
						chs[i] += str1.charAt(i - 1) - '0';
						// 进位
						if (chs[i] > '9') {
							chs[i - 1] += ((chs[i] - '0') / 10 + '0');
							chs[i] = (char) ((chs[i] - '0') % 10 + '0');
						}
					} else {
						chs[i] = str1.charAt(i - 1);
					}
				}
			}
			return doPreZero(chs);
		}
	}

	/**
	 * 比较两个字符串数字的大小,true表示第一个参数大，否则第二个参数大
	 */
	private static boolean compare(String str1, String str2) {
		if (str1.length() > str2.length()) {
			return true;
		} else if (str2.length() > str1.length()) {
			return false;
		} else {
			//两个字符串数字长度一样
			for (int i = 0; i < str1.length(); i++) {
				if (str1.charAt(i) > str2.charAt(i)) {
					return true;
				}
				if (str1.charAt(i) < str2.charAt(i)) {
					return false;
				}
			}
			// 相等返回true
			return true;
		}
	}

	// 结果返回，处理前缀0
	private static String doPreZero(char[] chs) {
		StringBuilder res = new StringBuilder();
		boolean isFirstZero = true;
		for (char aChar : chs) {
			if (aChar != '0') {
				isFirstZero = false;
			}
			if (!isFirstZero) {
				res.append(aChar);
			}
		}
		return res.toString().trim();
	}

	/**
	 * 蛮力计算，每个数进行计算一次
	 */
	private static String bigNumMuiltyByOrgin(int[] a, int[] b) {
		int[] res = new int[a.length + b.length];
		for (int i = 0; i < a.length; i++) {
			for (int j = 0; j < b.length; j++) {
				int temp = res[i + j] + a[i] * b[j];
				res[i + j] = temp % 10;
				res[i + j + 1] += temp / 10;
				if (res[i + j + 1] >= 10) {
					res[i + j + 1] %= 10;
					res[i + j + 2]++;
				}
			}
		}

		StringBuilder sb = new StringBuilder();
		for (int i = res.length - 1; i >= 0; i--) {
			sb.append(res[i]);
//			System.out.print(res[i]);
		}
		return sb.toString();
	}
}
