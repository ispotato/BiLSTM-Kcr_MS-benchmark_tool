package evidence_file_tools;

import fasta_tools.Common;
import fasta_tools.FastaUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

public class SeleNoKcrEvidenceTools {
    public static void main(String args[]){

        String evidenSubListFile="human_noKcr_evidence_column.txt";
        String swissProtDBFile="uniprot-human-filtered-reviewed_yes_110.fasta";
        int size=15;

        getNoKcrEvidenPepti31SampList(evidenSubListFile, swissProtDBFile, size);
    }

    /**
     * Generate MS-based non-Kcr negative dataset from the selected evidence.txt file
     *
     * @param evidenSubListFile selected "Sequence,Length,Modifications,Modified sequence,Crot(K) Probabilities,roteins" columns from evidence.txt file
     * @param swissProtDBFile download sequence from UniProt
     * @param size The length of the sequence on both sides of the modified site
     */
    public static void getNoKcrEvidenPepti31SampList(String evidenSubListFile, String swissProtDBFile, int size)
    {
        Hashtable<String,EvidenceSubInfo> noKcrPepti2EvidenceHt=loadNoKcrEvidenSubHt(evidenSubListFile);
        Hashtable<String,String> protAccessHt=evidenceSubHt2ProtHt(noKcrPepti2EvidenceHt);
        Hashtable<String, String> prot2SequHt=FastaUtils.loadProAcc2SequHt(swissProtDBFile, protAccessHt);

        List<Pepti31Info>  noKcrPepti31SampList=new ArrayList<>();
        Enumeration noKcrPeptiSets = noKcrPepti2EvidenceHt.keys();
        while (noKcrPeptiSets.hasMoreElements()) {
            String noKcrPepti = (String) noKcrPeptiSets.nextElement();
            EvidenceSubInfo noKcrEvidenceSubInfo = noKcrPepti2EvidenceHt.get(noKcrPepti);

            String peptide=noKcrEvidenceSubInfo.getSequence();
            String proteins=noKcrEvidenceSubInfo.getProteins();
            List<Pepti31Info>  pepti31List=null;
            if(!proteins.contains(";"))
            {
                if(!prot2SequHt.containsKey(proteins)) {
                    System.out.println(proteins+" not find!");
                    continue;
                }

                String sequence=prot2SequHt.get(proteins);
                pepti31List= getUniProt2Pepti31List(peptide,proteins,sequence,size);
            }
            else
                pepti31List=protClust2UniPepti31List(prot2SequHt,peptide, proteins, size);

            if(pepti31List!=null)
                noKcrPepti31SampList=mergePepti31List(pepti31List, noKcrPepti31SampList);
        }

        Common.writeStr2File(pepti31InfoList2Str(noKcrPepti31SampList),evidenSubListFile+"noKcr_uniSamp31.xls");
    }
    public static String pepti31InfoList2Str(List<Pepti31Info> pepti31List)
    {
        StringBuilder sb=new StringBuilder();
        for(int i=0;i<pepti31List.size();i++)
            sb.append(pepti31List.get(i).getPeptide31Samp()).append("\t")
            .append(pepti31List.get(i).getPeptide()).append("\t")
            .append(pepti31List.get(i).getProteins()).append("\n");

        return sb.toString();
    }

    public static List<Pepti31Info> mergePepti31List(List<Pepti31Info> newPepti31List, List<Pepti31Info> allPeptiList)
    {
        for(int i=0;i<newPepti31List.size();i++)
        {
            allPeptiList.add(newPepti31List.get(i));
        }

        return allPeptiList;
    }

    public static List<Pepti31Info> getUniProt2Pepti31List(String pepti, String protAccess,String sequence, int size)
    {
        List<Pepti31Info> pepti31InfoList=new ArrayList<>();
        if(!sequence.contains(pepti))
        {
            System.out.println(pepti+" "+protAccess);
            return null;
        }

        int startIdx=sequence.indexOf(pepti);
        char[] peptiArry=pepti.toCharArray();

        int currentKIdx=-1;
        for(int i=0;i<peptiArry.length;i++) {
            if(peptiArry[i]=='K') {
                currentKIdx=i;
                String pepti31Samp=getPepti31Str(currentKIdx, sequence, startIdx, size);
                if(pepti31Samp!=null)
                {
                    Pepti31Info peptide31Obj=new Pepti31Info();
                    peptide31Obj.setPeptide(pepti);
                    peptide31Obj.setProteins(protAccess);
                    peptide31Obj.setPeptide31Samp(pepti31Samp);
                    pepti31InfoList.add(peptide31Obj);
                }
            }
        }
        return pepti31InfoList;
    }

    public static List<Pepti31Info> protClust2UniPepti31List(Hashtable<String,String> prot2SequHt,
                                               String pepti, String protClust,int size)
    {
        List<Pepti31Info> allPepti31SampList=new ArrayList<>();
        String[] proteinArry = protClust.split(";");
        char[] peptiArry=pepti.toCharArray();
        int curentKIdx=-1;

        for(int i=0;i<peptiArry.length;i++) {
            if(peptiArry[i]=='K') {
                curentKIdx=i;

                Hashtable<String,String> checkUniPepti31Ht=new Hashtable<>();
                for (int proIdx = 0; proIdx < proteinArry.length; proIdx++) {
                    String protAccess = proteinArry[proIdx];
                    if (protAccess.startsWith("CON_"))
                        continue;
                    if (!prot2SequHt.containsKey(protAccess)) {
                        System.out.println(protAccess + " no sequ");
                        continue;
                    }

                    String sequence = prot2SequHt.get(protAccess);
                    if (!sequence.contains(pepti)) {
                        System.out.println(pepti + " not in " + protAccess);
                        continue;
                    }

                    int startIdx=sequence.indexOf(pepti);
                    String pepti31Samp = getPepti31Str(curentKIdx, sequence, startIdx, size);
                    if (pepti31Samp != null) {
                        checkUniPepti31Ht.put(pepti31Samp,"");
                    }
                }

                if(checkUniPepti31Ht.size()==1)
                {
                    String uniPepti31="";
                    Enumeration uniPepti31Sets = checkUniPepti31Ht.keys();
                    while (uniPepti31Sets.hasMoreElements()) {
                        uniPepti31 = (String) uniPepti31Sets.nextElement();
                    }

                    Pepti31Info pepti31Obj=new Pepti31Info();
                    pepti31Obj.setPeptide(pepti);
                    pepti31Obj.setProteins(protClust);
                    pepti31Obj.setPeptide31Samp(uniPepti31);

                    allPepti31SampList.add(pepti31Obj);
                }
            }
        }
        return allPepti31SampList;
    }

    public static String getPepti31Str(int firstKIdx,String sequence, int startIdx, int size)
    {
        int sequStartIdx=startIdx+firstKIdx-size;
        int endIdx=startIdx+firstKIdx+size;
        if(sequStartIdx>=0 && endIdx<=sequence.length()-1)
        {
            String pepti31=sequence.substring(sequStartIdx,endIdx+1);
            return pepti31;
        }
        else
            return null;
    }

    public static Hashtable<String,String> evidenceSubHt2ProtHt(Hashtable<String,EvidenceSubInfo> pepti2EvidenceHt)
    {
        Hashtable<String,String> protAccessHt=new Hashtable<>();
        Enumeration peptiSets = pepti2EvidenceHt.keys();
        while (peptiSets.hasMoreElements()) {
            String pepti = (String) peptiSets.nextElement();
            EvidenceSubInfo evidenceSubInfo=pepti2EvidenceHt.get(pepti);

            String protAccessClust=evidenceSubInfo.getProteins();
            if(protAccessClust.contains(";"))
            {
                String[] tmpArry=protAccessClust.split(";");
                for(int j=0;j<tmpArry.length;j++)
                    protAccessHt.put(tmpArry[j].trim(),"");
            }
            else
                protAccessHt.put(protAccessClust,"");
        }

        return protAccessHt;
    }

    public static Hashtable<String,EvidenceSubInfo> loadNoKcrEvidenSubHt(String evidenSubListFile)
    {
        Hashtable<String,EvidenceSubInfo> pepti2EvidenSubHt=null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            pepti2EvidenSubHt=new Hashtable<>();
            fis = new FileInputStream(evidenSubListFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line = br.readLine();//Sequence	Length	Modifications	Modified sequence	Proteins
            while ((line = br.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;

                String[] tmpArry=line.split("\t");
                String sequence=tmpArry[0].trim();
                String modify=tmpArry[2].trim();
                String proteins=tmpArry[tmpArry.length-1].trim();
                if(!sequence.contains("K")
                        || modify.contains("Crotony(K)")
                        || modify.contains("Crot(K)"))
                    continue;

                EvidenceSubInfo evidSubInfo=new EvidenceSubInfo();
                evidSubInfo.setSequence(sequence);
                evidSubInfo.setModifications(modify);
                evidSubInfo.setProteins(proteins);

                pepti2EvidenSubHt.put(sequence,evidSubInfo);
            }
            br.close();
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("can't find file:"+evidenSubListFile);
        } catch (IOException e) {
            System.out.println("can't read file:"+evidenSubListFile);
        }
        return pepti2EvidenSubHt;
    }

    public static int checkSequenceK(String sequence)
    {
        String noEndSequ=sequence.substring(0,sequence.length()-1);
        if(!noEndSequ.contains("K"))
            return 0;
        else
            return 1;
    }

    public static void writeHash(Hashtable<String,String> pepti2ProtHt, String outFile)
    {
        StringBuilder sb=new StringBuilder();
        Enumeration peptiSets = pepti2ProtHt.keys();
        while (peptiSets.hasMoreElements()) {
            String pepti = (String) peptiSets.nextElement();
            String protAccess= pepti2ProtHt.get(pepti);
            sb.append(pepti).append("\t").append(protAccess).append("\n");
        }

        Common.writeStr2File(sb.toString(),outFile);
    }

    public static Hashtable<String,String> loadPepti2ProtHt(String pepti2ProtFile)
    {
        Hashtable<String,String> pepti2ProtHt=null;
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        try {
            pepti2ProtHt=new Hashtable<>();
            fis = new FileInputStream(pepti2ProtFile);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line="";
            while ((line = br.readLine()) != null) {
                if(line.trim().equals(""))
                    continue;
                String[] tmpArry=line.split("\t");
                pepti2ProtHt.put(tmpArry[0].trim(),tmpArry[1].trim());
            }
            br.close();
            isr.close();
            fis.close();
        } catch (FileNotFoundException e) {
            System.out.println("can't find file:"+pepti2ProtFile);
        } catch (IOException e) {
            System.out.println("can't read file:"+pepti2ProtFile);
        }
        return pepti2ProtHt;
    }
}
