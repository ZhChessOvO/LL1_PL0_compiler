import lexical.LexicalAnalyzer;
import lexical.Token;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ll1 {

    static String sourcePath = "/src/code/test.txt"; // 测试代码地址
    static String testPath = "/src/code/code.pl0code"; // 测试目标代码地址

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("读取pl0文件中...");
        File directory = new File("");// 参数为空
        String courseFile = directory.getCanonicalPath();
//        String sourceCodePath = scanner.next();
        String sourceCodePath = courseFile + sourcePath;

//        System.out.println("\u4f18\u79c0");
        System.out.println("显示源代码吗 ?(Y/N)");
        String s = scanner.next();
//        String s = "n";
        String sourceCode = SourceCodeFileReader.readFileContent(sourceCodePath, "\n");
        if (s.equals("Y")) {
            System.out.println("\n==========源代码==========");
            System.out.println(sourceCode);
        }

        System.out.println("显示符号表吗? (Y/N)");
        s = scanner.next();
        ArrayList<Token> wordsToken = LexicalAnalyzer.getsym(sourceCode);
        for (int i = 0; i < wordsToken.size(); i++)
            wordsToken.get(i).setPosition(i);
        if (s.equals("Y")) {
            System.out.println("\n==========词法分析后的Token==========");
            for (int a = 0; a < wordsToken.size(); a++) {
                System.out.println(a + " " + wordsToken.get(a).toString());
            }
        }
        Test test = new Test();

        for (Token token : wordsToken) {
            if (token.getSym().equals("NUMBER") || token.getSym().equals("IDENT")) {
                char[] c = token.getContent().toCharArray();
                StringBuilder str = new StringBuilder();
                for (char value : c) {
                    str.append(value).append(" ");
                }
                test.strInput += str;
            } else {
                test.strInput += token.getContent() + " ";
            }
        }

        test.getNvNt();
        test.Init();
        test.createTable();
        test.analyzeLL(wordsToken);
//        test.output();
    }
}

class Test {
    //单个符号first集
    public HashMap<String, HashSet<String>> firstSet = new HashMap<String, HashSet<String>>();
    //符号串first集
    public HashMap<String, HashSet<String>> firstSetX = new HashMap<String, HashSet<String>>();
    //开始符
    public static String S = "程序";
    public HashMap<String, HashSet<String>> followSet = new HashMap<String, HashSet<String>>();
    //非终结符
    public HashSet<String> VnSet = new HashSet<String>();
    //终结符
    public HashSet<String> VtSet = new HashSet<>();
    //非终结符-产生式集合
    public HashMap<String, ArrayList<String>> expressionSet = new HashMap<String, ArrayList<String>>();
    public String[][] table;
    public String[][] tableSLR = {
            {"", "i", "+", "*", "(", ")", "$", "E", "T", "F"},
            {"0", "s5", "", "", "s4", "", "", "1", "2", "3"},
            {"1", "", "s6", "", "", "", "acc", "", "", ""},
            {"2", "", "r2", "s7", "", "r2", "r2", "", "", ""},
            {"3", "", "r4", "r4", "", "r4", "r4", "", "", ""},
            {"4", "s5", "", "", "s4", "", "", "8", "2", "3"},
            {"5", "", "r6", "r6", "", "r6", "r6", "", "", ""},
            {"6", "s5", "", "", "s4", "", "", "", "9", "3"},
            {"7", "s5", "", "", "s4", "", "", "", "", "10"},
            {"8", "", "s6", "", "", "s11", "", "", "", ""},
            {"9", "", "r1", "s7", "", "r1", "r1", "", "", ""},
            {"10", "", "r3", "r3", "", "r3", "r3", "", "", ""},
            {"11", "", "r5", "r5", "", "r5", "r5", "", "", ""}};

    //    public String[] inputExpression = {"S->I", "S->o", "I->i(E)SL", "L->eS", "L->~", "E->a", "E->b"};//输入的语法规则
    public String[] inputExpression = {
            "<程序>-><分程序>.",
            "<分程序>-><常量说明部分><变量说明部分><过程说明部分s><语句>",
            "<常量说明部分>->const<常量定义><常量定义s>;",
            "<常量说明部分>->~",
            "<常量定义s>->,<常量定义><常量定义s>",
            "<常量定义s>->~",
            "<常量定义>-><标识符>:=<无符号整数>",
            "<无符号整数>-><数字><数字s>",
            "<数字s>-><数字><数字s>",
            "<数字s>->~",
            "<变量说明部分>->var<标识符><标识符s>;",
            "<标识符s>->,<标识符><标识符s>",
            "<标识符s>->~",
            "<变量说明部分>->~",
            "<标识符>-><字母><字母或数字s>",
            "<字母或数字s>-><字母><字母或数字s>",
            "<字母或数字s>-><无符号整数><字母或数字s>",
            "<字母或数字s>->~",
            "<过程说明部分>-><过程首部><分程序>;<过程说明部分s>",
            "<过程说明部分s>-><过程说明部分><过程说明部分s>",
            "<过程说明部分>->~",
            "<过程首部>->procedure<标识符>;",
            "<语句>-><赋值语句>",
            "<语句>-><条件语句>",
            "<语句>-><当型循环语句>",
            "<语句>-><过程调用语句>",
            "<语句>-><读语句>",
            "<语句>-><写语句>",
            "<语句>-><复合语句>",
            "<语句>->~",
            "<赋值语句>-><标识符>:=<表达式>",
            "<复合语句>->begin<语句><语句s>end",
            "<语句s>->;<语句><语句s>",
            "<语句s>->~",
            "<条件>-><表达式><关系运算符><表达式>",
            "<条件>->odd<表达式>",
            "<表达式>-><加减运算符><项><加减运算符和项>",
            "<加减运算符和项>-><加减运算符><项><加减运算符和项>",
            "<加减运算符和项>->~",
            "<项>-><因子><乘除运算符和因子>",
            "<乘除运算符和因子>-><乘除运算符><因子><乘除运算符和因子>",
            "<乘除运算符和因子>->~",
            "<因子>-><标识符>",
            "<因子>-><无符号整数>",
            "<因子>->(<表达式>)",
            "<加减运算符>->+",
            "<加减运算符>->-",
            "<加减运算符>->~",
            "<乘除运算符>->*",
            "<乘除运算符>->/",
            "<关系运算符>->=",
            "<关系运算符>->#",
            "<关系运算符>-><",
            "<关系运算符>-><=",
            "<关系运算符>->>",
            "<关系运算符>->>=",
            "<条件语句>->if<条件>then<语句>",
            "<过程调用语句>->call<标识符>",
            "<当型循环语句>->while<条件>do<语句>",
            "<读语句>->read(<标识符><标识符s>)",
            "<写语句>->write(<表达式><表达式s>)",
            "<表达式s>->,<表达式><表达式s>",
            "<表达式s>->~",
            "<字母>->a",
            "<字母>->b",
            "<字母>->c",
            "<字母>->d",
            "<字母>->e",
            "<字母>->f",
            "<字母>->g",
            "<字母>->h",
            "<字母>->i",
            "<字母>->j",
            "<字母>->k",
            "<字母>->l",
            "<字母>->m",
            "<字母>->n",
            "<字母>->o",
            "<字母>->p",
            "<字母>->q",
            "<字母>->r",
            "<字母>->s",
            "<字母>->t",
            "<字母>->u",
            "<字母>->v",
            "<字母>->w",
            "<字母>->x",
            "<字母>->y",
            "<字母>->z",
            "<数字>->0",
            "<数字>->1",
            "<数字>->2",
            "<数字>->3",
            "<数字>->4",
            "<数字>->5",
            "<数字>->6",
            "<数字>->7",
            "<数字>->8",
            "<数字>->9"
    };
    public Stack<String> analyzeStack = new Stack<String>();
    public Stack<String> stackState = new Stack<>();
    public Stack<Character> stackSymbol = new Stack<>();
    public String strInput = "";//输入的程序
    public String action = "";
    //    public String[] LRGS = {"E->E+T", "E->T", "T->T*F", "T->F", "F->(E)", "F->i"};
    int index = 0;

    public void Init() {
        //获取生成式
        for (String e : inputExpression) {
            String[] str = e.split("->");
            String c = str[0];
            c = c.substring(1, c.length() - 1);
            ArrayList<String> list;
            if (expressionSet.containsKey(c))
                list = expressionSet.get(c);
            else {
                list = new ArrayList<>();
            }
            String[] s = str[1].split("<");
            for (String item : s) {
                String[] ss = item.split(">");
                for (String value : ss) {
                    if (value.contains("read")) {
                        list.add("read");
                        list.add("(");
                    } else if (value.contains("write")) {
                        list.add("write");
                        list.add("(");
                    } else if (!value.equals(""))
                        list.add(value);
                }
            }
            if (str[1].equals("<") || str[1].equals("<=") || str[1].equals(">") || str[1].equals(">="))
                list.add(str[1]);
            list.add("@");
            expressionSet.put(c, list);
        }
        //构造非终结符的first集
        for (String c : VnSet) {
            getFirst(c);
        }
        //构造开始符的follow集
        getFollow(S);
        //构造非终结符的follow集
        for (String c : VnSet) {
            getFollow(c);
        }
        for (String c : VnSet) {
            getFollow(c);
        }
        check();
    }

    /**
     * 先求非终结符，再求终结符
     */
    public void getNvNt() {
        for (String e : inputExpression) {
            String c = e.split("->")[0];
            c = c.split("<")[1];
            c = c.split(">")[0];
            VnSet.add(c);
        }
        for (String e : inputExpression) {
            String c = e.split("->")[1];
            String[] vt = c.split("<");
            for (String value : vt) {
                String[] vtt = value.split(">");
                for (String s : vtt) {
                    if (!VnSet.contains(s) && !s.equals("")) {
                        if (s.contains("read"))
                            VtSet.add("read");
                        else if (s.contains("write"))
                            VtSet.add("write");
                        else
                            VtSet.add(s);
                    }
                }
            }
            VtSet.add("<");
            VtSet.add("<=");
            VtSet.add(">");
            VtSet.add(">=");
        }
    }

    public void getFirst(String c) {
        HashSet<String> set = new HashSet<>();
        // c为非终结符 处理其每条产生式
        ArrayList<String> right = expressionSet.get(c);
        for (int i = 0; i < right.size(); i++) {
            if (right.get(i).equals(c) || right.get(i).equals("@")) {
                continue;
            } else {
                if (firstSet.containsKey(right.get(i))) {
                    set.addAll(firstSet.get(right.get(i)));
                    if (!set.contains("~")) {
                        while (!right.get(i).equals("@"))
                            i++;
                    }
                } else if (VtSet.contains(right.get(i))) {
                    set.add(right.get(i));
                    while (!right.get(i).equals("@"))
                        i++;
                } else {
                    getFirst(right.get(i));
                    set.addAll(firstSet.get(right.get(i)));
                    if (!set.contains("~"))
                        while (!right.get(i).equals("@"))
                            i++;
                }
            }
        }
        firstSet.put(c, set);
    }


    public void getFollow(String c) {
        ArrayList<String> list = expressionSet.get(c);//非终结符右边的一切
        HashSet<String> leftFollowSet = followSet.containsKey(c) ? followSet.get(c) : new HashSet<>();
        //如果是开始符 添加 $
        if (c.equals(S))
            leftFollowSet.add("$");
        //查找输入的所有产生式，添加c的后跟 终结符
        for (String ch : VnSet) {
            ArrayList<String> right = expressionSet.get(ch);
            for (int i = 0; i < right.size(); i++) {
                if (right.get(i).equals(c) && (!right.get(i + 1).equals("@")) && VtSet.contains(right.get(i + 1)))
                    leftFollowSet.add(right.get(i + 1));
            }
        }
        followSet.put(c, leftFollowSet);
        ArrayList<String> right = expressionSet.get(c);
        HashSet<String> hashSet = new HashSet<>();
        for (int i = 0; i < right.size(); i++) {
            if (VtSet.contains(right.get(i)) || right.get(i).equals("@")) {
                continue;
            }
            if (right.get(i + 1).equals("@")) {//句子末尾，为Vn则将FOLLOW(A)加入到FOLLOW中
                HashSet<String> hashSet1 = followSet.get(c);
                if (followSet.get(right.get(i)) == null) {
                    followSet.put(right.get(i), hashSet1);
                } else {
                    followSet.get(right.get(i)).addAll(hashSet1);
                }
            } else if (VtSet.contains(right.get(i + 1)) && !right.get(i + 1).equals("~")) {//若后面是终结符直接加上并且下一句
                hashSet.add(right.get(i + 1));
                if (followSet.get(right.get(i)) == null)
                    followSet.put(right.get(i), hashSet);
                else
                    followSet.get(right.get(i)).addAll(hashSet);
            } else if (VnSet.contains(right.get(i + 1))) {//句子中间，为Vn将后面的first加入到follow中一直到没有空为止
                HashSet<String> hashSet1 = new HashSet<>();
                int j = i + 1;
                while (!right.get(j).equals("@")) {
                    if (VtSet.contains(right.get(j))) {
                        if (!right.get(j).equals("~"))
                            hashSet1.add(right.get(j));
                        if (followSet.get(right.get(i)) == null)
                            followSet.put(right.get(j), hashSet1);
                        else
                            followSet.get(right.get(i)).addAll(hashSet1);
                        i = j;
                        break;
                    } else if (firstSet.get(right.get(j)).contains("~")) {
                        for (String s : firstSet.get(right.get(j))) {//去掉 <空>
                            if (!s.equals("~"))
                                hashSet1.add(s);
                        }
                        if (followSet.get(right.get(i)) == null)
                            followSet.put(right.get(j), hashSet1);
                        else
                            followSet.get(right.get(i)).addAll(hashSet1);
                        j++;
                    } else {
                        hashSet1 = firstSet.get(right.get(j));
                        if (followSet.get(right.get(i)) == null)
                            followSet.put(right.get(j), hashSet1);
                        else
                            followSet.get(right.get(i)).addAll(hashSet1);
                        break;
                    }
                }
            }
        }
    }


    public void createTable() {
        Object[] VtArray = VtSet.toArray();
        Object[] VnArray = VnSet.toArray();
        // 预测分析表初始化
        table = new String[VnArray.length + 1][VtArray.length + 1];
        table[0][0] = "Vn/Vt";
        //初始化首行首列
        for (int i = 0; i < VtArray.length; i++)
            table[0][i + 1] = (VtArray[i].toString().charAt(0) == '~') ? "$" : VtArray[i].toString();
        for (int i = 0; i < VnArray.length; i++)
            table[i + 1][0] = VnArray[i] + "";
        //全部置error
        for (int i = 0; i < VnArray.length; i++)
            for (int j = 0; j < VtArray.length; j++)
                table[i + 1][j + 1] = "error";
        //插入生成式
        for (String A : VnSet) {
            int num = 0;
            ArrayList<String> s = expressionSet.get(A);
            for (int i = 0; i < s.size(); i++) {
                if (s.get(i).equals("@"))
                    num++;
            }
            String[] right = new String[num];
            Arrays.fill(right, "");
            String[][] firstRight = new String[num][s.size()];
            for (String[] strings : firstRight) {
                Arrays.fill(strings, "");
            }
            int j = 0;
            int p = 0;
            int q = 0;
            for (int i = 0; i < s.size(); i++) {
                if (!s.get(i).equals("@")) {
                    right[j] += s.get(i);
                    firstRight[p][q] = s.get(i);
                    q++;
                } else {
                    if (i == s.size() - 1)
                        continue;
                    j++;
                    p++;
                    q = 0;
                }
            }
            if (firstSet.get(A).contains("~")) {
                HashSet<String> hashSet = followSet.get(A);
                for (String s1 : hashSet) {
                    insert(A, s1, "~");
                }
            }
            for (int i = 0; i < firstRight.length; i++) {
                for (j = 0; j < firstRight[i].length; j++) {
                    if (firstRight[i][j].equals(""))
                        break;
                    if (VtSet.contains(firstRight[i][j])) {//如果是终结符直接插入并结束这一句
                        insert(A, firstRight[i][j], right[i]);
                        break;
                    } else {
                        HashSet<String> hashSet = firstSet.get(firstRight[i][j]);
                        if (hashSet.contains("~")) {
                            for (String s1 : hashSet) {
                                insert(A, s1, right[i]);
                            }
                        } else {
                            for (String s1 : hashSet) {
                                insert(A, s1, right[i]);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    public void analyzeLL(ArrayList<Token> wordsToken) {
        System.out.println("****************LL分析过程**********");
        System.out.println("               Stack           Input     Action");
        analyzeStack.push("$");
        analyzeStack.push(S);
        String X = analyzeStack.peek();
        while (!X.equals("$")) {
            String[] input = strInput.split(" ");
            if (index >= input.length) {
                break;
            }
            String a = input[index];
            if (X.equals(a)) {
                action = "match " + analyzeStack.peek();
                analyzeStack.pop();
                index++;
            } else if (VtSet.contains(X))
                return;
            else if (find(X, a).equals("error")) {
                System.out.println("error at '" + input[index] + " in " + index);
                analyzeStack.pop();
                action = X + "->~";
            } else if (find(X, a).equals("~")) {
                analyzeStack.pop();
                action = X + "->~";
            } else {
                String str = find(X, a);
                ArrayList<String> s = expressionSet.get(X);
                int num = 0;
                for (int i = 0; i < s.size(); i++) {
                    if (s.get(i).equals("@"))
                        num++;
                }
                String[] rightSpace = new String[num];
                String[] right = new String[num];
                Arrays.fill(rightSpace, "");
                Arrays.fill(right, "");
                int j = 0;
                for (int i = 0; i < s.size(); i++) {
                    if (!s.get(i).equals("@")) {
                        rightSpace[j] += s.get(i) + " ";
                        right[j] += s.get(i);
                    } else {
                        if (i == s.size() - 1)
                            continue;
                        j++;
                    }
                }
                for (int i = 0; i < right.length; i++) {
                    if (right[i].equals(str)) {
                        if (!str.equals("")) {
                            action = X + "->" + str;
                            analyzeStack.pop();
                            String[] rights = rightSpace[i].split(" ");
                            for (int k = rights.length - 1; k >= 0; k--)
                                analyzeStack.push(rights[k]);
                        } else {
                            System.out.println("error at '" + input[index] + " in " + index);
                            return;
                        }
                    }
                }

            }
            X = analyzeStack.peek();
            displayLL();
        }
        System.out.println("analyze LL1 successfully");
        System.out.println("****************LL分析过程**********");
    }

    public String find(String X, String a) {
        for (int i = 0; i < VnSet.size() + 1; i++) {
            if (table[i][0].equals(X))
                for (int j = 0; j < VtSet.size() + 1; j++) {
                    if (table[0][j].equals(a))
                        return table[i][j];
                }
        }
        return "";
    }

    public void check() {
        HashSet<String> hashSet1 = new HashSet<>();
        hashSet1.add("+");
        hashSet1.add("-");
        if (followSet.get("字母或数字s") == null) {
            followSet.put("字母或数字s", hashSet1);
        } else {
            followSet.get("字母或数字s").addAll(hashSet1);
        }
        hashSet1 = new HashSet<>();
        hashSet1.add("do");
        if (followSet.get("数字s") == null) {
            followSet.put("数字s", hashSet1);
        } else {
            followSet.get("数字s").addAll(hashSet1);
        }
        hashSet1.add("#");
        hashSet1.add(")");
        hashSet1.add(";");
        hashSet1.add("<");
        hashSet1.add("end");
        hashSet1.add("then");
        if (followSet.get("乘除运算符和因子") == null) {
            followSet.put("乘除运算符和因子", hashSet1);
        } else {
            followSet.get("乘除运算符和因子").addAll(hashSet1);
        }
    }

    public void insert(String X, String a, String s) {
        if (a.equals("~")) a = "$";
        for (int i = 0; i < VnSet.size() + 1; i++) {
            if (table[i][0].equals(X))
                for (int j = 0; j < VtSet.size() + 1; j++) {
                    if (table[0][j].equals(a)) {
                        table[i][j] = s;
                        return;
                    }
                }
        }
    }

    public void displayLL() {
        // 输出 LL1
        Stack<String> s = analyzeStack;
        System.out.println(s);
//        System.out.printf("%13s", strInput.substring(index));
        System.out.println(action);
    }

    public void output() {
        System.out.println("*********first集********");
        for (String c : VnSet) {
            HashSet<String> set = firstSet.get(c);
            System.out.printf("%10s", c + "  ->   ");
            for (String var : set)
                System.out.print(var);
            System.out.println();
        }
        System.out.println("**********first集**********");
        System.out.println("*********firstX集********");
        Set<String> setStr = firstSetX.keySet();
        for (String s : setStr) {
            HashSet<String> set = firstSetX.get(s);
            System.out.printf("%10s", s + "  ->   ");
            for (String var : set)
                System.out.print(var);
            System.out.println();
        }
        System.out.println("**********firstX集**********");
        System.out.println("**********follow集*********");

        for (String c : VnSet) {
            HashSet<String> set = followSet.get(c);
            System.out.print("Follow " + c + ":");
            for (String var : set)
                System.out.print(var);
            System.out.println();
        }
        System.out.println("**********follow集**********");

        System.out.println("**********LL1预测分析表********");

        for (int i = 0; i < VnSet.size() + 1; i++) {
            for (int j = 0; j < VtSet.size() + 1; j++) {
                System.out.printf("%6s", table[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println("**********LL1预测分析表********");
    }

}

