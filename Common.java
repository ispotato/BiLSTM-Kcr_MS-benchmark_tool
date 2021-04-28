package fasta_tools;

import evidence_file_tools.CrotKSiteInfo;
import evidence_file_tools.PeptiCrotKProbTools;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class Common {

    public static Hashtable<String,String> loadPepti2CrotKPeptiHt(String txtFile)
    {
        Hashtable<String,String> strHt=null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            strHt=new Hashtable<>();
            fis = new FileInputStream(txtFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line="";
            while ((line = br.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;
                String[] tmpArry=line.trim().split("\t");
                String crotK=tmpArry[1].trim();
                if(!crotK.contains("Crotony(K)"))
                    continue;
                strHt.put(tmpArry[0].trim(),tmpArry[2].trim());
            }
            br.close();
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("can't find file:"+txtFile);
        } catch (IOException e) {
            System.out.println("can't read file:"+txtFile);
        }
        return strHt;
    }

    public static void getCortKScoreCount(String fileList)
    {
        Hashtable<String,String> pepti2CrotKpeptiHt=loadPepti2CrotKPeptiHt(fileList);
        List<Double> kScoreList=new ArrayList<>();
        Enumeration peptiSets = pepti2CrotKpeptiHt.keys();
        while (peptiSets.hasMoreElements()) {
            String pepti = (String) peptiSets.nextElement();
            String cortKPepti=pepti2CrotKpeptiHt.get(pepti);

            if(pepti.contains("K"))
            {
                //"LDSEDK(0.194)DK(0.648)EGK(0.158)PLLK";
                List<CrotKSiteInfo> crotSiteList=PeptiCrotKProbTools.crotKProb2SiteInfoList(cortKPepti);
                for(int i=0;i<crotSiteList.size();i++)
                {
                    kScoreList.add(crotSiteList.get(i).getProbScore());
                }
            }
        }

        StringBuilder sb=new StringBuilder();
        for(int i=0;i<kScoreList.size();i++)
            sb.append(kScoreList.get(i)).append("\n");

        writeStr2File(sb.toString(),fileList+"_KcrScoreAll.list");
    }

    public static void writeStr2File(String str, String fileName)
    {
        try {
            FileWriter output = new FileWriter(fileName);
            output.append(str);
            output.flush();
            output.close();
        } catch (IOException e) {
            System.out.println(fileName+"file write fail!");
        }
    }

    public static List<String> getDiffList(List<String> aimList, Hashtable<String,Double> queryHt)
    {
        List<String> noFindList=new ArrayList<>();
        for(int i=0;i<aimList.size();i++)
        {
            if(!queryHt.containsKey(aimList.get(i)) && aimList.get(i).length()==31)
                noFindList.add(aimList.get(i));
        }
        return noFindList;
    }

    public static Hashtable<String,Double> loadHighScorePeptiHt(String lineFile, double cutScore)
    {
        Hashtable<String,Double> pepti2HighScoreHt=null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            pepti2HighScoreHt=new Hashtable<>();
            fis = new FileInputStream(lineFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line="";
            while ((line = br.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;
                String[] tmpArry=line.split("\t");
                double score=Double.parseDouble(tmpArry[0]);
                if(score>=cutScore)
                {
                    String pepti = tmpArry[1];
                    if (!pepti2HighScoreHt.containsKey(pepti))
                        pepti2HighScoreHt.put(pepti.trim(), score);
                    else {
                        double highScore = pepti2HighScoreHt.get(pepti);
                        if (score > highScore)
                            pepti2HighScoreHt.put(pepti.trim(), score);
                    }
                }
            }
            br.close();
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("can't find file:"+lineFile);
        } catch (IOException e) {
            System.out.println("can't read file:"+lineFile);
        }
        return pepti2HighScoreHt;
    }

    public static List<String> loadTxt2List(String lineFile)
    {
        List<String> list=null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            list=new ArrayList<>();
            fis = new FileInputStream(lineFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line="";
            while ((line = br.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;
                list.add(line.trim());
            }
            br.close();
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("can't find file:"+lineFile);
        } catch (IOException e) {
            System.out.println("can't read file:"+lineFile);
        }
        return list;
    }

}
