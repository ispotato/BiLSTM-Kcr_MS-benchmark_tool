package my_deep_net;

import java.util.ArrayList;
import java.util.List;

public class Pepti2EmbeddedTools {
    public static void main(String args[]) {
        String rootPath="/media/yang/soft/numStr/indep_data";
        String posiListFile=rootPath+"/posi.list";
        String negaListFile=rootPath+"/nega.list";
        String posiNegaNumCsvFile=rootPath+"/train_posiNegaNum.list";

        sampleSet2AcidNumSet(posiListFile, negaListFile, posiNegaNumCsvFile);
    }

    public static void sampleSet2AcidNumSet(String posiListFile, String negaListFile, String posiNegaNumCsvFile)
    {
        List<String> posiPeptiList=Common.loadTxt2List(posiListFile);
        List<String> negaPeptiList=Common.loadTxt2List(negaListFile);

        int titleSize=posiPeptiList.get(0).length();
        String titleStr=getCsvTitle(titleSize);

        String posiNumStr=peptiList2AcidNumCsvStr(1,posiPeptiList);
        String negaNumStr=peptiList2AcidNumCsvStr(0,negaPeptiList);

        Common.writeStr2File(titleStr+posiNumStr+negaNumStr,posiNegaNumCsvFile);
    }

    public static void sampleSet2SecondStructNumSet(String posiListFile, String negaListFile, String posiNegaNumCsvFile)
    {
        List<String> posiPeptiList=Common.loadTxt2List(posiListFile);
        List<String> negaPeptiList=Common.loadTxt2List(negaListFile);

        int titleSize=posiPeptiList.get(0).length();
        String titleStr=getCsvTitle(titleSize);

        String posiNumStr=peptiList2SecondStructNumCsvStr(1,posiPeptiList);
        String negaNumStr=peptiList2SecondStructNumCsvStr(0,negaPeptiList);

        Common.writeStr2File(titleStr+posiNumStr+negaNumStr,posiNegaNumCsvFile);
    }

    private static String peptiList2AcidNumCsvStr(int classType, List<String> peptiList)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<peptiList.size();i++)
        {
            char[] acidArry=peptiList.get(i).toCharArray();
            sb.append(classType);
            for(int j=0;j<acidArry.length;j++)
                sb.append(",").append(acid2Num(acidArry[j]));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String peptiList2HydrophobicityNumCsvStr(int classType, List<String> peptiList)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<peptiList.size();i++)
        {
            char[] acidArry=peptiList.get(i).toCharArray();
            sb.append(classType);
            for(int j=0;j<acidArry.length;j++)
                sb.append(",").append(acid2HydrophobicityNum(acidArry[j]));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String peptiList2VanDerWaalsNumCsvStr(int classType, List<String> peptiList)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<peptiList.size();i++)
        {
            char[] acidArry=peptiList.get(i).toCharArray();
            sb.append(classType);
            for(int j=0;j<acidArry.length;j++)
                sb.append(",").append(acid2VanDerWaalsNum(acidArry[j]));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String peptiList2PolarityNumCsvStr(int classType, List<String> peptiList)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<peptiList.size();i++)
        {
            char[] acidArry=peptiList.get(i).toCharArray();
            sb.append(classType);
            for(int j=0;j<acidArry.length;j++)
                sb.append(",").append(acid2PolarityNum(acidArry[j]));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String peptiList2PolarizabilityNumCsvStr(int classType, List<String> peptiList)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<peptiList.size();i++)
        {
            char[] acidArry=peptiList.get(i).toCharArray();
            sb.append(classType);
            for(int j=0;j<acidArry.length;j++)
                sb.append(",").append(acid2PolarizabilityNum(acidArry[j]));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String peptiList2SecondStructNumCsvStr(int classType, List<String> peptiList)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<peptiList.size();i++)
        {
            char[] acidArry=peptiList.get(i).toCharArray();
            sb.append(classType);
            for(int j=0;j<acidArry.length;j++)
                sb.append(",").append(acid2SecondStructNum(acidArry[j]));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String peptiList2SolventAccessibiNumCsvStr(int classType, List<String> peptiList)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<peptiList.size();i++)
        {
            char[] acidArry=peptiList.get(i).toCharArray();
            sb.append(classType);
            for(int j=0;j<acidArry.length;j++)
                sb.append(",").append(acid2SolventAccessibiNum(acidArry[j]));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String peptiList2EBGWNumCsvStr(int classType, List<String> peptiList)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<peptiList.size();i++)
        {
            char[] acidArry=peptiList.get(i).toCharArray();
            sb.append(classType);
            for(int j=0;j<acidArry.length;j++)
                sb.append(",").append(acid2EBGWNum(acidArry[j]));
            sb.append("\n");
        }
        return sb.toString();
    }

    private static String getCsvTitle(int columnCount)
    {
        StringBuilder sb=new StringBuilder();
        sb.append("class");
        for(int i=0;i<columnCount;i++)
            sb.append(",").append("feat").append(i);
        sb.append("\n");
        return sb.toString();
    }

    private static int acid2Num(char acid)
    {
        switch (acid) {
            case 'A':
                return 0;
            case 'R':
                return 1;
            case 'N':
                return 2;
            case 'D':
                return 3;
            case 'C':
                return 4;
            case 'E':
                return 5;
            case 'Q':
                return 6;
            case 'G':
                return 7;
            case 'H':
                return 8;
            case 'I':
                return 9;
            case 'L':
                return 10;
            case 'K':
                return 11;
            case 'M':
                return 12;
            case 'F':
                return 13;
            case 'P':
                return 14;
            case 'S':
                return 15;
            case 'T':
                return 16;
            case 'W':
                return 17;
            case 'Y':
                return 18;
            case 'V':
                return 19;
            default:
                return 20;
        }
    }

    /***
     * class1 = 'RKEDQN'
     * class2 = 'GASTPHY'
     * class3 = 'CLVIMFW'
     * @param acid
     * @return
     */
    private static int acid2HydrophobicityNum(char acid)
    {
        switch (acid) {
            case 'A':
                return 1;
            case 'R':
                return 0;
            case 'N':
                return 0;
            case 'D':
                return 0;
            case 'C':
                return 2;
            case 'E':
                return 0;
            case 'Q':
                return 0;
            case 'G':
                return 1;
            case 'H':
                return 1;
            case 'I':
                return 2;
            case 'L':
                return 2;
            case 'K':
                return 0;
            case 'M':
                return 2;
            case 'F':
                return 2;
            case 'P':
                return 1;
            case 'S':
                return 1;
            case 'T':
                return 1;
            case 'W':
                return 2;
            case 'Y':
                return 1;
            case 'V':
                return 2;
            default:
                return 3;
        }
    }

    /**
     *         class1 = 'GASTPDC'
     *         class2 = 'NVEQIL'
     *         class3 = 'MHKFRYW'
     * @param acid
     * @return
     */
    private static int acid2VanDerWaalsNum(char acid)
    {
        switch (acid) {
            case 'A':
                return 0;
            case 'R':
                return 2;
            case 'N':
                return 1;
            case 'D':
                return 0;
            case 'C':
                return 0;
            case 'E':
                return 1;
            case 'Q':
                return 1;
            case 'G':
                return 0;
            case 'H':
                return 2;
            case 'I':
                return 2;
            case 'L':
                return 1;
            case 'K':
                return 2;
            case 'M':
                return 2;
            case 'F':
                return 2;
            case 'P':
                return 0;
            case 'S':
                return 0;
            case 'T':
                return 0;
            case 'W':
                return 2;
            case 'Y':
                return 2;
            case 'V':
                return 1;
            default:
                return 3;
        }
    }

    /**
     *         class1 = 'LIFWCMVY'
     *         class2 = 'PATGS'
     *         class3 = 'HQRKNED
     * @param acid
     * @return
     */
    private static int acid2PolarityNum(char acid)
    {
        switch (acid) {
            case 'A':
                return 1;
            case 'R':
                return 2;
            case 'N':
                return 2;
            case 'D':
                return 2;
            case 'C':
                return 0;
            case 'E':
                return 2;
            case 'Q':
                return 2;
            case 'G':
                return 1;
            case 'H':
                return 2;
            case 'I':
                return 2;
            case 'L':
                return 0;
            case 'K':
                return 2;
            case 'M':
                return 0;
            case 'F':
                return 0;
            case 'P':
                return 1;
            case 'S':
                return 1;
            case 'T':
                return 1;
            case 'W':
                return 0;
            case 'Y':
                return 0;
            case 'V':
                return 0;
            default:
                return 3;
        }
    }

    /**
     *         class1 = 'GASDT'
     *         class2 = 'CPNVEQIL'
     *         class3 = 'KMHFRYW'
     * @param acid
     * @return
     */
    private static int acid2PolarizabilityNum(char acid)
    {
        switch (acid) {
            case 'A':
                return 0;
            case 'R':
                return 2;
            case 'N':
                return 2;
            case 'D':
                return 0;
            case 'C':
                return 1;
            case 'E':
                return 1;
            case 'Q':
                return 1;
            case 'G':
                return 0;
            case 'H':
                return 2;
            case 'I':
                return 1;
            case 'L':
                return 1;
            case 'K':
                return 2;
            case 'M':
                return 2;
            case 'F':
                return 2;
            case 'P':
                return 1;
            case 'S':
                return 0;
            case 'T':
                return 0;
            case 'W':
                return 2;
            case 'Y':
                return 2;
            case 'V':
                return 1;
            default:
                return 3;
        }
    }

    /**
     *         class1 = 'EALMQKRH'
     *         class2 = 'VIYCWFT'
     *         class3 = 'GNPSD'
     * @param acid
     * @return
     */
    private static int acid2SecondStructNum(char acid)
    {
        switch (acid) {
            case 'A':
                return 0;
            case 'R':
                return 0;
            case 'N':
                return 2;
            case 'D':
                return 2;
            case 'C':
                return 1;
            case 'E':
                return 0;
            case 'Q':
                return 0;
            case 'G':
                return 2;
            case 'H':
                return 0;
            case 'I':
                return 1;
            case 'L':
                return 0;
            case 'K':
                return 0;
            case 'M':
                return 0;
            case 'F':
                return 1;
            case 'P':
                return 2;
            case 'S':
                return 2;
            case 'T':
                return 1;
            case 'W':
                return 1;
            case 'Y':
                return 1;
            case 'V':
                return 1;
            default:
                return 3;
        }
    }

    /**
     *         class1 = 'ALFCGIVW'
     *         class2 = 'PKQEND'
     *         class3 = 'MRSTHY'
     * @param acid
     * @return
     */
    private static int acid2SolventAccessibiNum(char acid)
    {
        switch (acid) {
            case 'A':
                return 0;
            case 'R':
                return 2;
            case 'N':
                return 1;
            case 'D':
                return 1;
            case 'C':
                return 0;
            case 'E':
                return 1;
            case 'Q':
                return 1;
            case 'G':
                return 0;
            case 'H':
                return 2;
            case 'I':
                return 0;
            case 'L':
                return 0;
            case 'K':
                return 1;
            case 'M':
                return 2;
            case 'F':
                return 0;
            case 'P':
                return 1;
            case 'S':
                return 2;
            case 'T':
                return 2;
            case 'W':
                return 0;
            case 'Y':
                return 2;
            case 'V':
                return 0;
            default:
                return 3;
        }
    }

    /**
     *         c1 = 'AFGILMPVW'
     *         # The polar group
     *         c2 = 'CNQSTY'
     *         # The positively charged group
     *         c3 = 'KHR'
     *         # The negatively charged group
     *         c4 = 'DE'
     * @param acid
     * @return
     */
    private static int acid2EBGWNum(char acid)
    {
        switch (acid) {
            case 'A':
                return 0;
            case 'R':
                return 2;
            case 'N':
                return 1;
            case 'D':
                return 3;
            case 'C':
                return 1;
            case 'E':
                return 3;
            case 'Q':
                return 1;
            case 'G':
                return 0;
            case 'H':
                return 2;
            case 'I':
                return 0;
            case 'L':
                return 0;
            case 'K':
                return 2;
            case 'M':
                return 0;
            case 'F':
                return 0;
            case 'P':
                return 0;
            case 'S':
                return 1;
            case 'T':
                return 1;
            case 'W':
                return 0;
            case 'Y':
                return 1;
            case 'V':
                return 0;
            default:
                return 4;
        }
    }
    
    /***
     * 将str输出到指定文件
     * @param str 待输出的str
     * @param fileName 输出的文件名
     */
    public static void writeStr2File(String str, String fileName)
    {
        try {
            FileWriter output = new FileWriter(fileName);
            output.append(str);
            output.flush();
            output.close();
        } catch (IOException e) {
            System.out.println(fileName+"文件写入失败");
        }
    }
}
