package fasta_tools;

import java.io.*;
import java.util.*;

public class FastaUtils {

    public static Hashtable<String,String> loadProtClustList2Ht(String txtFile)
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
                if(!line.contains(";"))
                    strHt.put(line.trim(),"");
                else
                {
                    String[] tmpArry=line.split(";");
                    for(int i=0;i<tmpArry.length;i++)
                        strHt.put(tmpArry[i].trim(),"");
                }
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

    public static void prot2SequHt2Fasta(Hashtable<String,String> prot2SequHt, String outFastaFile)
    {
        StringBuilder sb=new StringBuilder();
        Enumeration protAccessSet = prot2SequHt.keys();
        while (protAccessSet.hasMoreElements()) {
            String protAccess = (String) protAccessSet.nextElement();
            String sequ=prot2SequHt.get(protAccess);
            sb.append(">").append(protAccess).append("\n");
            sb.append(sequ).append("\n");
        }
        Common.writeStr2File(sb.toString(),outFastaFile);
    }

    public static Hashtable<String,String> getFastaAccessHt(String dbFasta)
    {
        Hashtable<String,String> accessHt=null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            accessHt=new Hashtable<>();
            fis = new FileInputStream(dbFasta);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line="";
            String protAccess="";
            StringBuilder sequSb=new StringBuilder();
            while ((line = br.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;
                if(line.startsWith(">"))
                    accessHt.put(line.trim().substring(1),"");
            }
            br.close();
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("can't find file:"+dbFasta);
        } catch (IOException e) {
            System.out.println("can't read file:"+dbFasta);
        }
        return accessHt;
    }

    public static Hashtable<String, String> loadProAcc2SequHt(String dbFasta, Hashtable<String,String> seleProtAccHt)
    {
        Hashtable<String,String> protAccess2SequHt=new Hashtable<>();
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            fis = new FileInputStream(dbFasta);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line="";
            String protAccess="";
            StringBuilder sequSb=new StringBuilder();
            while ((line = br.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;
                if(line.startsWith(">"))
                {
                    if(!protAccess.equals("")) {
                        if(seleProtAccHt.containsKey(protAccess)) {
                            protAccess2SequHt.put(protAccess,sequSb.toString());
                        }
                        protAccess="";
                        sequSb.setLength(0);
                    }
                    //>sp|P51410|RL9_MOUSE 60S ribosomal protein L9 OS=Mus musculus OX=10090 GN=Rpl9 PE=2 SV=2
                    String[] tmpArry=line.replace("|","#").split("#");
                    protAccess=tmpArry[1].trim();
                }
                else
                    sequSb.append(line.trim());
            }
            protAccess2SequHt.put(protAccess,sequSb.toString());
            br.close();
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("can't find file:"+dbFasta);
        } catch (IOException e) {
            System.out.println("can't read file:"+dbFasta);
        }
        return protAccess2SequHt;
    }
}
