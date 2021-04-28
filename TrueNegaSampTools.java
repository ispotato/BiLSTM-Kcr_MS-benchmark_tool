package evidence_file_tools;

import java.io.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class TrueNegaSampTools {
    public static void main(String args[]){
        String noKcrPepti31ListFile="human_noKcr_evidence_column.txtnoKcr_uniSamp31.xls";
        String crotKPepti31ListFile="human_abundKcrEvidence_column.txt_mulPepti31_cut0.1.xls";
        getTruePepti31List(noKcrPepti31ListFile, crotKPepti31ListFile);
    }

    /**
     * Exclude the Kcr positive datasets from MS-based non-Kcr negative datasets
     *
     * @param noKcrPepti31ListFile MS-based non-Kcr negative datasets
     * @param crotKPepti31ListFile MS-based Kcr positive datasets
     */
    public static void getTruePepti31List(String noKcrPepti31ListFile, String crotKPepti31ListFile)
    {
        Hashtable<String,String> trueNegaSamp2ProtHt=new Hashtable<>();
        Hashtable<String,String> noKcrPepti31Samp2ProtHt=loadPepti2AccessHt(noKcrPepti31ListFile);

        Hashtable<String,String> checkCrotKPeptiHt=loadPepti2AccessHt(crotKPepti31ListFile);
        Enumeration noKcrPepti31SampSets = noKcrPepti31Samp2ProtHt.keys();
        while (noKcrPepti31SampSets.hasMoreElements()) {
            String noKcrPepti31Samp = (String) noKcrPepti31SampSets.nextElement();
            String proteins=noKcrPepti31Samp2ProtHt.get(noKcrPepti31Samp);

            if(!checkCrotKPeptiHt.containsKey(noKcrPepti31Samp))
                trueNegaSamp2ProtHt.put(noKcrPepti31Samp,proteins);
        }

        SeleNoKcrEvidenceTools.writeHash(trueNegaSamp2ProtHt,noKcrPepti31ListFile+"_trueNega.list");
    }

    public static Hashtable<String,String> loadPepti2AccessHt(String xlsFile)
    {
        Hashtable<String,String> pepti2AccessHt=null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            pepti2AccessHt=new Hashtable<>();
            fis = new FileInputStream(xlsFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line="";
            while ((line = br.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;
                String[] tmpArry=line.split("\t");
                String peptide=tmpArry[0].trim();
                String access=tmpArry[tmpArry.length-1].trim();
                pepti2AccessHt.put(peptide,access);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("can't find file:"+xlsFile);
        } catch (IOException e) {
            System.out.println("can't read file:"+xlsFile);
        }
        return pepti2AccessHt;
    }
}
